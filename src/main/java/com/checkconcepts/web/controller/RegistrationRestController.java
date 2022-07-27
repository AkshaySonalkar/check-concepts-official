package com.checkconcepts.web.controller;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.checkconcepts.persistence.model.User;
import com.checkconcepts.persistence.model.VerificationToken;
import com.checkconcepts.registration.OnRegistrationCompleteEvent;
import com.checkconcepts.security.ISecurityUserService;
import com.checkconcepts.service.IUserService;
import com.checkconcepts.web.dto.ContactDto;
import com.checkconcepts.web.dto.PasswordDto;
import com.checkconcepts.web.dto.UserDto;
import com.checkconcepts.web.error.InvalidOldPasswordException;
import com.checkconcepts.web.util.GenericResponse;

@RestController
public class RegistrationRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;
    
    @Value("${spring.profiles.active}")
	private String activeProfile;

    public RegistrationRestController() {
        super();
    }

    // Registration
    @PostMapping("/user/registration")
    public GenericResponse registerUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
//        userService.addUserLocation(registered, getClientIP(request));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }
    
    @PostMapping("/user/admin/registration")
    public GenericResponse registerUserStaffAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);
        if(accountDto.getRoleName().equalsIgnoreCase("ROLE_STAFF")) {
        	final User registered = userService.registerNewStaffUserAccount(accountDto);
//        	userService.addUserLocation(registered, getClientIP(request));
        }
        if(accountDto.getRoleName().equalsIgnoreCase("ROLE_ADMIN")) {
        	final User registered = userService.registerNewAdminUserAccount(accountDto);
//        	userService.addUserLocation(registered, getClientIP(request));
        }
        
//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    // User activation - verification
    @GetMapping("/user/resendRegistrationToken")
    public GenericResponse resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, user));
        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    // Reset password
    @PostMapping("/user/resetPassword")
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail, final Model model) {
        final User user = userService.findUserByEmail(userEmail);
        if(user!=null && !user.isAccountActive()) {
        	return new GenericResponse(messages.getMessage("auth.message.deactivated", null, request.getLocale()));
        }
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
        }else {
        	return new GenericResponse(messages.getMessage("message.userNotFound", null, request.getLocale()));
        }
        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    // Save password
    @PostMapping("/user/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

        final String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if(result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user.isPresent()) {
            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
    }
    
    //Contact Us
    @PostMapping("/contact/mail")
    public GenericResponse contactUs(@Valid final ContactDto contactDto, final HttpServletRequest request) {
        LOGGER.debug("Contact Us: {}", contactDto);
        mailSender.send(constructContactEmail(contactDto.getSubject(), contactDto.getMessage(), contactDto.getEmail(), contactDto.getName()));
        mailSender.send(constructContactReplyEmail(contactDto.getSubject(), contactDto.getMessage(), contactDto.getEmail(), contactDto.getName()));
//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    // Change user password
    @PostMapping("/user/updatePassword")
    public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = currentUser();/*userService.findUserByEmail(((org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());*/
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }
    
     public User currentUser() {
    	  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	  if (auth == null || auth instanceof AnonymousAuthenticationToken) {
    	    return null;
    	  }
    	  String email = ((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername();
    	  return userService.findUserByEmail(email);
    	}

    // ============== NON-API ============

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
    
    private SimpleMailMessage constructContactEmail(String subject, String message, String emailFrom, String name) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(message + "\r\n" + "Mail from user name: "+ name+ " email: "+emailFrom);
        email.setTo(env.getProperty("support.email"));
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
    
    private SimpleMailMessage constructContactReplyEmail(String subject, String message, String emailFrom, String name) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText("Hello "+name+"\r\n"+" we have received your request. We are going through your query. We will get back to you within 24 hrs. "+"\r\n"+"Best Regards,"+"\r\n"+"Check-Concepts Team");
        email.setTo(emailFrom);
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
		if(activeProfile.equalsIgnoreCase("dev"))
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		if(activeProfile.equalsIgnoreCase("prod"))
		return "http://www.check-concepts.com" + request.getContextPath();
		return null;
	}

    private String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
