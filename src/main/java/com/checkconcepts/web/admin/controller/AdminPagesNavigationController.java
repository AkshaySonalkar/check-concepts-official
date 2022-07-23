package com.checkconcepts.web.admin.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.checkconcepts.persistence.dao.RoleRepository;
import com.checkconcepts.persistence.dao.UserRepository;
import com.checkconcepts.persistence.model.User;
import com.checkconcepts.registration.OnRegistrationCompleteEvent;
import com.checkconcepts.security.ISecurityUserService;
import com.checkconcepts.service.IUserService;
import com.checkconcepts.web.dto.UserDto;
import com.checkconcepts.web.util.GenericResponse;

@Controller
public class AdminPagesNavigationController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
    private MessageSource messages;
	
	@Autowired
    UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;

	@GetMapping("/admin/adminConsole")
	public ModelAndView console(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});
		model.addAttribute("data", "Admin Console Data");
		System.out.println("Hey Admin !!!");
		return new ModelAndView("adminConsole", model);
	}
	
	@GetMapping("/admin/usersinfo")
    public String showUserList(Model model) {
        model.addAttribute("endusers", userRepository.findAll().stream().filter(u-> u.isEnabled() && u.getRoles().stream().anyMatch(r->r.getName().equalsIgnoreCase("ROLE_USER"))).collect(Collectors.toList()));
        model.addAttribute("adminusers", userRepository.findAll().stream().filter(u-> u.isEnabled() && u.getRoles().stream().anyMatch(r->r.getName().equalsIgnoreCase("ROLE_ADMIN"))).collect(Collectors.toList()));
        model.addAttribute("staffusers", userRepository.findAll().stream().filter(u-> u.isEnabled() && u.getRoles().stream().anyMatch(r->r.getName().equalsIgnoreCase("ROLE_STAFF"))).collect(Collectors.toList()));
        return "usersinfo";
    }
    
    @GetMapping("/admin/edit/user/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("userrole", user.getRoles().iterator().next().getName());
        return "update-user";
    }
    
    @PostMapping("/admin/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            user.setId(id);
            return "update-user";
        }
        System.out.println(model.getAttribute("userrole"));
        User userObj = userRepository.findById(id).get();
        userObj.setLastName(user.getLastName());
        userObj.setFirstName(user.getFirstName());
        userRepository.save(userObj);

        return "redirect:/admin/usersinfo";
    }
    
    @GetMapping("/admin/deactivate/{id}")
    public String deactivateUser(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setAccountActive(false);
        userRepository.save(user);
        
        return "redirect:/admin/usersinfo";
    }
    
    @GetMapping("/admin/activate/{id}")
    public String activateUser(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.setAccountActive(true);
        userRepository.save(user);
        
        return "redirect:/admin/usersinfo";
    }
    
    @GetMapping("/admin/make/staff/{id}")
    public String makeStaffUser(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.getRoles().remove(roleRepository.findByName("ROLE_USER"));
        User userObj = userRepository.save(user);
        userObj.setRoles(Arrays.asList(roleRepository.findByName("ROLE_STAFF")));
        userRepository.save(userObj);
        return "redirect:/admin/usersinfo";
    }
    
    @GetMapping("/admin/make/admin/{id}")
    public String makeAdminUser(@PathVariable("id") long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        user.getRoles().remove(roleRepository.findByName("ROLE_USER"));
        User userObj = userRepository.save(user);
        userObj.setRoles(Arrays.asList(roleRepository.findByName("ROLE_ADMIN")));
        userRepository.save(userObj);
        return "redirect:/admin/usersinfo";
    }
    
    
    
    @GetMapping("/admin/signup")
    public String showSignUpForm(User user) {
        return "admin-signup";
    }
    
    @PostMapping("/adduser")
    public String addUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "admin-signup";
        }
        
        userRepository.save(user);
        return "redirect:/admin/usersinfo";
    }
}
