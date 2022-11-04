package com.checkconcepts.web.user.controller;

import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserPagesNavigationController {
	
	@Autowired
    private MessageSource messages;
	
	@GetMapping("/user/userDashboard")
    public ModelAndView userDashboard(final HttpServletRequest request, final ModelMap model, @RequestParam("messageKey") final Optional<String> messageKey) {

        Locale locale = request.getLocale();
        messageKey.ifPresent( key -> {
                    String message = messages.getMessage(key, null, locale);
                    model.addAttribute("message", message);
                }
        );
        System.out.println("Hey User !!!");
        model.addAttribute("data", "User Console Data");
        return new ModelAndView("userDashboard", model);
    }

}
