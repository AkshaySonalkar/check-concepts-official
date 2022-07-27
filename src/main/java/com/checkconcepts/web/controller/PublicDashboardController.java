package com.checkconcepts.web.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.checkconcepts.blog.services.NotificationService;
import com.checkconcepts.menu.AppSidebarMenu;
import com.checkconcepts.menu.MenuCategory;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.service.PostService;

@Controller
public class PublicDashboardController {
	
	@Autowired
    private PostService postService;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private AppSidebarMenu appMenu;
    
    @Value("${spring.profiles.active}")
	private String activeProfile;
    
    private String getAppUrl(HttpServletRequest request) {
		if(activeProfile.equalsIgnoreCase("dev"))
		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		if(activeProfile.equalsIgnoreCase("prod"))
		return "http://www.check-concepts.com" + request.getContextPath();
		return null;
	}
	
	@GetMapping("/index")
    public String login(final HttpServletRequest request, final Model model) {
		

		/*
		 * List<Post> latest5Posts = postService.findLatest5();
		 * model.addAttribute("latest5posts", latest5Posts);
		 * 
		 * List<Post> latest3Posts = latest5Posts.stream() .limit(3).collect(toList());
		 * model.addAttribute("latest3posts", latest3Posts);
		 */
        
        List<MenuCategory> categories= appMenu.getAllSidebarCategories().stream().filter(a -> a.isTech()).collect(Collectors.toList());
        model.addAttribute("sidebarcategories", categories);
        
        model.addAttribute("sidebarHeader", "Check-C");
        
        
        return "index";
    }
	
	@GetMapping("/contact")
    public String contact(final HttpServletRequest request, final Model model) {
        Locale locale = request.getLocale();
        model.addAttribute("data", "CONTACT DATA");
        model.addAttribute("CONTENT_TITLE", "CONTACT DATA");
        return "contact";
    }
	
	@GetMapping("/about")
    public String about(final HttpServletRequest request, final Model model) {
        Locale locale = request.getLocale();
        model.addAttribute("data", "ABOUT DATA");
        return "aboutme";
    }
	
	@RequestMapping("/posts/view/{id}")
    public String view(@PathVariable("id") Long id,
                       Model model) {
        Optional<Post> post = postService.findPostById(id);

        if (post.isEmpty()) {
            notificationService.addErrorMessage(
                    "Cannot find post: " + id);
            return "redirect:/";
        }

        model.addAttribute("post", post);
        return "/post";
    }
	
	@GetMapping("/invalidSession")
    public String invalidSession(final HttpServletRequest request, final Model model) {
        Locale locale = request.getLocale();
        model.addAttribute("url", getAppUrl(request));
        return "invalidSession";
    }
	
	@GetMapping("/accessDenied")
    public String accessDenied(final HttpServletRequest request, final Model model) {
        Locale locale = request.getLocale();
        model.addAttribute("url", getAppUrl(request));
        return "accessDenied";
    }

}
