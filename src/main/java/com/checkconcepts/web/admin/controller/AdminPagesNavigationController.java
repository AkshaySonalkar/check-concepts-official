package com.checkconcepts.web.admin.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.checkconcepts.persistence.dao.RoleRepository;
import com.checkconcepts.persistence.dao.UserRepository;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.PostsStatus;
import com.checkconcepts.persistence.model.User;
import com.checkconcepts.persistence.model.UserGender;
import com.checkconcepts.service.ICategoryService;
import com.checkconcepts.service.IPostService;
import com.checkconcepts.service.ISubCategoryService;

@Controller
public class AdminPagesNavigationController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSource messages;

	@Autowired
	ICategoryService categoryService;

	@Autowired
	ISubCategoryService subCategoryService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	IPostService postService;

	@GetMapping("/admin/adminConsole")
	public ModelAndView console(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});

		List<Post> posts = postService.findAll().stream()
				.filter(p -> p.getStatus().equals(PostsStatus.READY_FOR_PUBLISH)).collect(Collectors.toList());
		model.addAttribute("publishposts", posts);

		int totalCategoriesCount = categoryService.findAll().size();
		int totalSubCategoriesCount = subCategoryService.findAll().size();
		int totalCreatedPostsCount = postService.findAll().stream()
				.filter(p -> p.getStatus().equals(PostsStatus.CREATED)).collect(Collectors.toList()).size();
		int totalPublishedPostsCount = postService.findAll().stream()
				.filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toList()).size();
		int totalPostsForPublishCount = postService.findAll().stream()
				.filter(p -> p.getStatus().equals(PostsStatus.READY_FOR_PUBLISH)).collect(Collectors.toList()).size();

		model.addAttribute("totalCategoriesCount", totalCategoriesCount);
		model.addAttribute("totalSubCategoriesCount", totalSubCategoriesCount);
		model.addAttribute("totalCreatedPostsCount", totalCreatedPostsCount);
		model.addAttribute("totalPublishedPostsCount", totalPublishedPostsCount);
		model.addAttribute("totalPostsForPublishCount", totalPostsForPublishCount);
		return new ModelAndView("adminConsole", model);
	}

	@GetMapping("/admin/usersinfo")
	public String showUserList(Model model) {
		model.addAttribute("endusers", userRepository.findAll().stream().filter(
				u -> u.isEnabled() && u.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ROLE_USER")))
				.collect(Collectors.toList()));
		model.addAttribute("adminusers", userRepository.findAll().stream().filter(
				u -> u.isEnabled() && u.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ROLE_ADMIN")))
				.collect(Collectors.toList()));
		model.addAttribute("staffusers", userRepository.findAll().stream().filter(
				u -> u.isEnabled() && u.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("ROLE_STAFF")))
				.collect(Collectors.toList()));
		
		model.addAttribute("genders", UserGender.getValuesAsString());
		return "usersinfo";
	}

	@GetMapping("/admin/edit/user/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		model.addAttribute("user", user);
		model.addAttribute("userrole", user.getRoles().iterator().next().getName());
		return "update-user";
	}

	@PostMapping("/admin/update/{id}")
	public String updateUser(@PathVariable("id") long id, @Valid User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			user.setId(id);
			return "update-user";
		}
		try {
			
			User userObj = userRepository.findById(id).get();
			userObj.setLastName(user.getLastName());
			userObj.setFirstName(user.getFirstName());
			userRepository.save(userObj);
			redirectAttributes.addFlashAttribute("message", "User updated successfully " + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "User failed to update " + "!");
		}
		

		return "redirect:/admin/usersinfo";
	}

	@GetMapping("/admin/deactivate/{id}")
	public String deactivateUser(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttributes) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		user.setAccountActive(false);
		userRepository.save(user);
		redirectAttributes.addFlashAttribute("message", "User deactivated successfully " + "!");

		return "redirect:/admin/usersinfo";
	}

	@GetMapping("/admin/activate/{id}")
	public String activateUser(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttributes) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
		user.setAccountActive(true);
		userRepository.save(user);
		redirectAttributes.addFlashAttribute("message", "User activated successfully " + "!");
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

	@GetMapping("/admin/previewPost/{postid}/{postname}")
	public ModelAndView staffPreviewPost(@PathVariable("postid") long postid, @PathVariable("postname") String postname,
			final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});

		/*
		 * List<Post> posts = postService.findAll().stream() .filter(p ->
		 * p.getStatus().equals(PostsStatus.READY_FOR_PUBLISH)).collect(Collectors.
		 * toList()); model.addAttribute("publishposts", posts);
		 */

		Post post = postService.findPostById(postid).get();
		model.addAttribute("post", post);

		/*
		 * Post post = postService.findPostById(postid).get();
		 * model.addAttribute("post", post);
		 */

		/*
		 * model.addAttribute("files", storageService.loadAll() .map(path ->
		 * MvcUriComponentsBuilder .fromMethodName(FileUploadController.class,
		 * "serveFile", path.getFileName().toString()) .build().toUri().toString())
		 * .collect(Collectors.toList()));
		 */

		return new ModelAndView("previewPost", model);
	}

	@GetMapping("/admin/post/approve/publish/{id}")
	public String publishPost(@PathVariable("id") long id, @Valid Post post, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			Post postObj = postService.findPostById(id).get();
			postObj.setStatus(PostsStatus.PUBLISHED);
			postService.save(postObj);
			redirectAttributes.addFlashAttribute("message", "Post published successfully " + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error occured " + "!");
			return "redirect:/admin/adminConsole";
		}
		return "redirect:/admin/adminConsole";
	}

	@GetMapping("/admin/post/reject/publish/{id}")
	public String sendPostForImprovement(@PathVariable("id") long id, @Valid Post post,
			RedirectAttributes redirectAttributes, Model model) {
		try {
			Post postObj = postService.findPostById(id).get();
			postObj.setStatus(PostsStatus.IMPROVEMENT_REQUIRED);
			postService.save(postObj);
			redirectAttributes.addFlashAttribute("message", "Post sent for improvement successfully " + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error occured " + "!");
			return "redirect:/admin/adminConsole";
		}
		return "redirect:/admin/adminConsole";
	}
}
