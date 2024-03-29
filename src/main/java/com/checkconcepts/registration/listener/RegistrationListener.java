package com.checkconcepts.registration.listener;

import java.util.UUID;

import com.checkconcepts.service.IUserService;
import com.checkconcepts.persistence.model.User;
import com.checkconcepts.registration.OnRegistrationCompleteEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
	@Autowired
	private IUserService service;

	@Autowired
	private MessageSource messages;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private Environment env;

	// API

	@Override
	public void onApplicationEvent(final OnRegistrationCompleteEvent event) {
		this.confirmRegistration(event);
	}

	private void confirmRegistration(final OnRegistrationCompleteEvent event) {
		final User user = event.getUser();
		final String token = UUID.randomUUID().toString();
		service.createVerificationTokenForUser(user, token);

		final SimpleMailMessage email = constructEmailMessage(event, user, token);
		mailSender.send(email);
	}

	//

	private SimpleMailMessage constructEmailMessage(final OnRegistrationCompleteEvent event, final User user,
			final String token) {
		final String recipientAddress = user.getEmail();
		final String subject = "CheckConcepts.org | Registration Veification";
		final String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
		// final String message = messages.getMessage("message.regSuccLink", null, "You
		// registered successfully with Check-Concepts. To confirm your registration,
		// please click on the below link.", event.getLocale());

		final String message = "Hello " + user.getFirstName() + " " + user.getLastName() + ", \n\n\n\n"
				+ "Congratualation! Your CheckConcepts.org account created successfully. \n\n"
				+ "As you registered an account on CheckConcepts.org, before being able to use your account you need to verify that this is your email address by clicking below verification link: \n\n"
				+ confirmationUrl + "\n\n\n" + "Kind Regards,\nCheckConcepts.org";

		final SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject(subject);
		email.setText(message);
		email.setFrom(env.getProperty("support.email"));
		return email;
	}

}
