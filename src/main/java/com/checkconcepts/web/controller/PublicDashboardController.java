package com.checkconcepts.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicDashboardController {
	
	@GetMapping("/index")
    public String login(final HttpServletRequest request, final Model model) {
        Locale locale = request.getLocale();
        model.addAttribute("data", "DASHBOARD DATA");
        return "index";
    }
	
	@GetMapping("/contact")
    public String contact(final HttpServletRequest request, final Model model) {
        Locale locale = request.getLocale();
        model.addAttribute("data", "CONTACT DATA");
        return "contact";
    }
	
	@GetMapping("/about")
    public String about(final HttpServletRequest request, final Model model) {
        Locale locale = request.getLocale();
        model.addAttribute("data", "ABOUT DATA");
        return "about";
    }

}
