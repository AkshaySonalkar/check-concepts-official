package com.checkconcepts.web.controller;

import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.checkconcepts.persistence.dao.UserRepository;
import com.checkconcepts.persistence.model.User;
import com.checkconcepts.security.ActiveUserStore;
import com.checkconcepts.service.IUserService;

@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;
    
    @Autowired
    UserRepository userRepository;

    @GetMapping("/loggedUsers")
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }

    @GetMapping("/loggedUsersFromSessionRegistry")
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userService.getUsersFromSessionRegistry());
        return "users";
    }
    
    @GetMapping("/userProfile")
    public String getUserProfile(final Locale locale, final Model model) {
        model.addAttribute("userprof", "User Profile Data");
        return "userProfile";
    }
    
    
}
