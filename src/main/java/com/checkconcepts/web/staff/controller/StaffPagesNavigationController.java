package com.checkconcepts.web.staff.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.checkconcepts.menu.MenuCategory;
import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.PostsAttachments;
import com.checkconcepts.persistence.model.PostsStatus;
import com.checkconcepts.persistence.model.SubCategory;
import com.checkconcepts.persistence.model.Tag;
import com.checkconcepts.persistence.model.User;
import com.checkconcepts.service.AWSStorageService;
import com.checkconcepts.service.ICategoryService;
import com.checkconcepts.service.IPostService;
import com.checkconcepts.service.IPostsAttachmentsService;
import com.checkconcepts.service.ISubCategoryService;
import com.checkconcepts.service.ITagService;
import com.checkconcepts.service.StorageService;

@Controller
public class StaffPagesNavigationController {

	/*
	 * private final StorageService storageService;
	 * 
	 * @Autowired public StaffPagesNavigationController(StorageService
	 * storageService) { this.storageService = storageService; }
	 */

	@Autowired
	private MessageSource messages;

	@Autowired
	ICategoryService categoryService;

	@Autowired
	ISubCategoryService subCategoryService;

	@Autowired
	IPostService postService;

	@Autowired
	StorageService storageService;

	@Autowired
	AWSStorageService awsStorageService;

	@Autowired
	IPostsAttachmentsService postAttachmentService;

	@Autowired
	ITagService tagService;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	@GetMapping("/staff/staffConsole")
	public ModelAndView staffConsole(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});
		model.addAttribute("sidebarHeader", "Check-Concepts");
		model.addAttribute("sidebarcategories", getStaffCategories());

		List<Post> posts = postService.findAll().stream()
				.filter(p -> p.getAuthor().equals((User) request.getSession().getAttribute("userObj"))
						&& p.getStatus().equals(PostsStatus.IMPROVEMENT_REQUIRED))
				.collect(Collectors.toList());
		model.addAttribute("improvementposts", posts);

		int totalCategoriesCount = categoryService.findAll().size();
		int totalSubCategoriesCount = subCategoryService.findAll().size();
		int totalCreatedPostsCount = postService.findAll().stream()
				.filter(p -> p.getAuthor().equals((User) request.getSession().getAttribute("userObj"))
						&& p.getStatus().equals(PostsStatus.CREATED))
				.collect(Collectors.toList()).size();
		int totalPublishedPostsCount = postService.findAll().stream()
				.filter(p -> p.getAuthor().equals((User) request.getSession().getAttribute("userObj"))
						&& p.getStatus().equals(PostsStatus.PUBLISHED))
				.collect(Collectors.toList()).size();
		int totalPostsForImprovementCount = postService.findAll().stream()
				.filter(p -> p.getAuthor().equals((User) request.getSession().getAttribute("userObj"))
						&& p.getStatus().equals(PostsStatus.IMPROVEMENT_REQUIRED))
				.collect(Collectors.toList()).size();

		model.addAttribute("totalCategoriesCount", totalCategoriesCount);
		model.addAttribute("totalSubCategoriesCount", totalSubCategoriesCount);
		model.addAttribute("totalCreatedPostsCount", totalCreatedPostsCount);
		model.addAttribute("totalPublishedPostsCount", totalPublishedPostsCount);
		model.addAttribute("totalPostsForImprovementCount", totalPostsForImprovementCount);

		return new ModelAndView("staffConsole", model);
	}

	@GetMapping("/staff/categoryCrud")
	public ModelAndView categoryCrud(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});
		model.addAttribute("data", "Staff Console Data");

		model.addAttribute("sidebarHeader", "Check-Concepts");
		model.addAttribute("sidebarcategories", getStaffCategories());

		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);

		model.addAttribute("sidebarHeader", "Check-Concepts");

		return new ModelAndView("categoryCrud", model);
	}

	@GetMapping("/staff/category/edit/{id}")
	public String showCategoryUpdateForm(@PathVariable("id") long id, Model model) {
		Category category = categoryService.findCategoryById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
		model.addAttribute("category", category);
		return "update-category";
	}

	@GetMapping("/staff/subCategoryCrud")
	public ModelAndView subCategoryCrud(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});
		model.addAttribute("data", "Staff Console Data");

		model.addAttribute("sidebarHeader", "Check-Concepts");
		model.addAttribute("sidebarcategories", getStaffCategories());

		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);

		List<SubCategory> subcategories = subCategoryService.findAll();
		model.addAttribute("subcategories", subcategories);

		model.addAttribute("sidebarHeader", "Check-Concepts");

		return new ModelAndView("subCategoryCrud", model);
	}

	@GetMapping("/staff/subcategory/edit/{id}")
	public String showSubCategoryUpdateForm(@PathVariable("id") long id, Model model) {
		SubCategory category = subCategoryService.findSubCategoryById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
		model.addAttribute("subcategory", category);
		return "update-subcategory";
	}

	@PostMapping("/staff/update/category/{id}")
	public String updateCategory(@PathVariable("id") long id, @Valid Category category, BindingResult result,
			Model model, final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			category.setId(id);
			return "update-category";
		}
		Category categoryObj = categoryService.findCategoryById(id).get();
		categoryObj.setName(category.getName());
		categoryObj.setDescription(category.getDescription());
		categoryObj.setPremium(category.isPremium());
		categoryObj.setTech(category.isTech());
		categoryService.save(categoryObj);

		redirectAttributes.addFlashAttribute("message", "Category updated successfully  " + category.getName() + "!");

		return "redirect:/staff/categoryCrud";
	}

	@GetMapping("/staff/category/delete/{id}")
	public String deleteCategory(@PathVariable("id") long id, @Valid Category category, BindingResult result,
			Model model, final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			category.setId(id);
			return "redirect:/staff/categoryCrud";
		}
		Category categoryObj = categoryService.findCategoryById(id).get();
		try {
			categoryService.delete(categoryObj);
			redirectAttributes.addFlashAttribute("message", "Category deleted successfully  " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Category failed to delete  " + "!");
			return "redirect:/staff/categoryCrud";
		}
		return "redirect:/staff/categoryCrud";
	}

	@PostMapping("/staff/update/subcategory/{id}")
	public String updateSubCategory(@PathVariable("id") long id, @Valid SubCategory category, BindingResult result,
			Model model, final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			category.setId(id);
			return "update-subcategory";
		}
		SubCategory categoryObj = subCategoryService.findSubCategoryById(id).get();
		categoryObj.setName(category.getName());
		categoryObj.setDescription(category.getDescription());
		categoryObj.setPremium(category.isPremium());
		subCategoryService.save(categoryObj);
		redirectAttributes.addFlashAttribute("message",
				"Sub Category updated successfully  " + category.getName() + "!");

		return "redirect:/staff/subCategoryCrud";
	}

	@GetMapping("/staff/subcategory/delete/{id}")
	public String deleteSubCategory(@PathVariable("id") long id, @Valid SubCategory category, BindingResult result,
			Model model, final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			category.setId(id);
			return "redirect:/staff/subCategoryCrud";
		}
		SubCategory categoryObj = subCategoryService.findSubCategoryById(id).get();
		try {
			subCategoryService.delete(categoryObj);
			redirectAttributes.addFlashAttribute("message", "Sub Category deleted successfully  " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Sub Category failed to delete  " + "!");
			return "redirect:/staff/subCategoryCrud";
		}
		return "redirect:/staff/subCategoryCrud";
	}

	@GetMapping("/staff/postsCrud")
	public ModelAndView postsCrud(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});
		model.addAttribute("data", "Staff Console Data");

		model.addAttribute("sidebarHeader", "Check-Concepts");
		model.addAttribute("sidebarcategories", getStaffCategories());

		List<SubCategory> subcategories = subCategoryService.findAll();
		model.addAttribute("subcategories", subcategories);

		List<Post> posts = postService.findAll().stream()
				.filter(p -> p.getAuthor().equals((User) request.getSession().getAttribute("userObj"))
						&& p.getStatus().equals(PostsStatus.CREATED))
				.collect(Collectors.toList());

		model.addAttribute("posts", posts);

		/*
		 * model.addAttribute("files", storageService.loadAll() .map(path ->
		 * MvcUriComponentsBuilder .fromMethodName(FileUploadController.class,
		 * "serveFile", path.getFileName().toString()) .build().toUri().toString())
		 * .collect(Collectors.toList()));
		 */

		return new ModelAndView("postsCrud", model);
	}

	@GetMapping("/staff/post/edit/{id}")
	public String showPostUpdateForm(@PathVariable("id") long id, Model model) {
		Post post = postService.findPostById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
		model.addAttribute("post", post);
		List<Tag> tags = new ArrayList<>();
		if (post.getSubCategoryType().getCategoryType().isTech())
			tags = tagService.findAll().stream().filter(t -> t.isTech()).collect(Collectors.toList());
		else
			tags = tagService.findAll().stream().filter(t -> !t.isTech()).collect(Collectors.toList());
		model.addAttribute("alltags", tags);
		return "update-post";
	}

	@PostMapping("/staff/update/post/{id}")
	public String updatePost(@PathVariable("id") long id, @Valid Post post, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			post.setId(id);
			return "update-post";
		}
		Post postObj = postService.findPostById(id).get();
		postObj.setTitle(post.getTitle());
		postObj.setDescription(post.getDescription());
		postObj.setUpdatedAt(new Date());
		postObj.setContent(post.getContent());
		postObj.setTags(post.getTags());
		postService.save(postObj);

		redirectAttributes.addFlashAttribute("message", "Post updated successfully  " + post.getTitle() + "!");

		return "redirect:/staff/postsCrud";
	}

	@GetMapping("/staff/attachment/delete/{postid}/{attid}/{name}")
	public String deleteSubCategory(@PathVariable("postid") long postid, @PathVariable("attid") long attid,
			@PathVariable("name") String name, PostsAttachments att, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			att.setName(name);
			return "redirect:/staff/post/edit/" + postid;
		}

		PostsAttachments postAttachmentObj = postAttachmentService.findPostAttachmentById(attid).get();
		try {
			if (activeProfile.equalsIgnoreCase("dev"))
				storageService.deleteFile(name);
			if (activeProfile.equalsIgnoreCase("prod"))
				awsStorageService.deleteFile(name);
			postAttachmentService.delete(postAttachmentObj);
			redirectAttributes.addFlashAttribute("message", "Post attachment deleted successfully  " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Post attachment failed to delete  " + "!");
			return "redirect:/staff/post/edit/" + postid;
		}
		return "redirect:/staff/post/edit/" + postid;
	}

	@GetMapping("/staff/attachment/make/profilepic/{postid}/{attid}/{name}")
	public String makePostProfilePic(@PathVariable("postid") long postid, @PathVariable("attid") long attid,
			@PathVariable("name") String name, PostsAttachments att, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			att.setName(name);
			return "redirect:/staff/post/edit/" + postid;
		}

		postService.findPostById(postid).get().getPostsAttachments().forEach(attObj -> {
			attObj.setProfilePic(false);
			postAttachmentService.save(attObj);
		});
		PostsAttachments postAttachmentObj = postAttachmentService.findPostAttachmentById(attid).get();
		try {
			postAttachmentObj.setProfilePic(true);
			postAttachmentService.save(postAttachmentObj);
			redirectAttributes.addFlashAttribute("message", "Post attachment make profile pic successfully  " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Post attachment failed to make profile pic  " + "!");
			return "redirect:/staff/post/edit/" + postid;
		}
		return "redirect:/staff/post/edit/" + postid;
	}

	@GetMapping("/staff/attachment/remove/profilepic/{postid}/{attid}/{name}")
	public String removePostProfilePic(@PathVariable("postid") long postid, @PathVariable("attid") long attid,
			@PathVariable("name") String name, PostsAttachments att, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			att.setName(name);
			return "redirect:/staff/post/edit/" + postid;
		}

		PostsAttachments postAttachmentObj = postAttachmentService.findPostAttachmentById(attid).get();
		try {
			postAttachmentObj.setProfilePic(false);
			postAttachmentService.save(postAttachmentObj);
			redirectAttributes.addFlashAttribute("message", "Post attachment removed profile pic successfully  " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Post attachment failed to remove profile pic  " + "!");
			return "redirect:/staff/post/edit/" + postid;
		}
		return "redirect:/staff/post/edit/" + postid;
	}

	@GetMapping("/staff/attachment/support/uncheck/{postid}/{attid}/{name}")
	public String uncheckSupportDoc(@PathVariable("postid") long postid, @PathVariable("attid") long attid,
			@PathVariable("name") String name, PostsAttachments att, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			att.setName(name);
			return "redirect:/staff/post/edit/" + postid;
		}

		PostsAttachments postAttachmentObj = postAttachmentService.findPostAttachmentById(attid).get();
		try {
			postAttachmentObj.setSupportingDoc(false);
			postAttachmentService.save(postAttachmentObj);
			redirectAttributes.addFlashAttribute("message", "Post attachment support unchecked successfully  " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Post attachment failed to uncheck supporting doc  " + "!");
			return "redirect:/staff/post/edit/" + postid;
		}
		return "redirect:/staff/post/edit/" + postid;
	}

	@GetMapping("/staff/attachment/support/check/{postid}/{attid}/{name}")
	public String checkSupportDoc(@PathVariable("postid") long postid, @PathVariable("attid") long attid,
			@PathVariable("name") String name, PostsAttachments att, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			att.setName(name);
			return "redirect:/staff/post/edit/" + postid;
		}

		PostsAttachments postAttachmentObj = postAttachmentService.findPostAttachmentById(attid).get();
		try {
			postAttachmentObj.setSupportingDoc(true);
			postAttachmentService.save(postAttachmentObj);
			redirectAttributes.addFlashAttribute("message", "Post attachment checked successfully  " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("error", "Post attachment failed to check as supporting doc  " + "!");
			return "redirect:/staff/post/edit/" + postid;
		}
		return "redirect:/staff/post/edit/" + postid;
	}

	@GetMapping("/staff/post/request/publish/{id}")
	public String sendPostForPublish(@PathVariable("id") long id, @Valid Post post,
			RedirectAttributes redirectAttributes, Model model) {
		try {
			Post postObj = postService.findPostById(id).get();
			postObj.setStatus(PostsStatus.READY_FOR_PUBLISH);
			postService.save(postObj);
			redirectAttributes.addFlashAttribute("message", "Post sent for publish successfully " + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Post failed to sent for publish " + "!");
			return "redirect:/staff/postsCrud";
		}
		return "redirect:/staff/postsCrud";
	}

	@GetMapping("/staff/post/delete/{id}")
	public String deletePost(@PathVariable("id") long id, @Valid Post post, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			post.setId(id);
			return "redirect:/staff/postsCrud";
		}
		Post postObj = postService.findPostById(id).get();
		try {
			postService.delete(postObj);
			redirectAttributes.addFlashAttribute("message", "Post deleted successfully " + "!");
		} catch (Exception e) {
			// TODO: handle exception
			redirectAttributes.addFlashAttribute("message", "Post failed to delete " + "!");
			return "redirect:/staff/postsCrud";
		}
		return "redirect:/staff/postsCrud";
	}

	@GetMapping("/staff/staffUsers")
	public ModelAndView staffUsers(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});
		model.addAttribute("data", "Staff Console Data");

		model.addAttribute("categories", getStaffCategories());

		model.addAttribute("sidebarHeader", "Check-Concepts");

		return new ModelAndView("staffUsers", model);
	}

	@GetMapping("/staff/previewPost/{postid}/{postname}")
	public ModelAndView staffPreviewPost(@PathVariable("postid") long postid, @PathVariable("postname") String postname,
			final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});

		model.addAttribute("sidebarHeader", "Check-Concepts");
		model.addAttribute("sidebarcategories", getStaffCategories());

		Post post = postService.findPostById(postid).get();
		model.addAttribute("post", post);

		boolean supportingDocPresent = post.getPostsAttachments().stream().anyMatch(att->att.isSupportingDoc());
		model.addAttribute("supportingDocPresent", supportingDocPresent);
		/*
		 * model.addAttribute("files", storageService.loadAll() .map(path ->
		 * MvcUriComponentsBuilder .fromMethodName(FileUploadController.class,
		 * "serveFile", path.getFileName().toString()) .build().toUri().toString())
		 * .collect(Collectors.toList()));
		 */

		return new ModelAndView("previewPost", model);
	}

	@GetMapping("/staff/tagsCrud")
	public ModelAndView tagsCrud(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});

		model.addAttribute("sidebarHeader", "Check-Concepts");
		model.addAttribute("sidebarcategories", getStaffCategories());

		List<Post> posts = postService.findAll().stream()
				.filter(p -> p.getAuthor().equals((User) request.getSession().getAttribute("userObj"))
						&& p.getStatus().equals(PostsStatus.CREATED))
				.collect(Collectors.toList());

		List<Tag> tags = tagService.findAll();

		model.addAttribute("tags", tags);

		/*
		 * model.addAttribute("files", storageService.loadAll() .map(path ->
		 * MvcUriComponentsBuilder .fromMethodName(FileUploadController.class,
		 * "serveFile", path.getFileName().toString()) .build().toUri().toString())
		 * .collect(Collectors.toList()));
		 */

		return new ModelAndView("tagsCrud", model);
	}

	@GetMapping("/staff/tag/edit/{id}")
	public String showTagUpdateForm(@PathVariable("id") long id, Model model) {
		Tag tag = tagService.findTagById(id).orElseThrow(() -> new IllegalArgumentException("Invalid tag Id:" + id));
		model.addAttribute("tag", tag);
		return "update-tag";
	}

	@PostMapping("/staff/update/tag/{id}")
	public String updateTag(@PathVariable("id") long id, @Valid Tag tag, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			tag.setId(id);
			return "update-tag";
		}
		try {
			Tag tagObj = tagService.findTagById(id).get();
			tagObj.setName(tag.getName());
			tagObj.setTech(tag.isTech());
			tagService.save(tagObj);
			redirectAttributes.addFlashAttribute("message", "Tag saved successfully  " + tag.getName() + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Tag failed to save  " + tag.getName() + "!");
		}
		return "redirect:/staff/tagsCrud";
	}

	@GetMapping("/staff/tag/delete/{id}")
	public String deleteTag(@PathVariable("id") long id, @Valid Tag tag, BindingResult result, Model model,
			final RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			tag.setId(id);
			return "redirect:/staff/tagsCrud";
		}
		Tag tagObj = tagService.findTagById(id).get();
		try {
			tagService.delete(tagObj);
			redirectAttributes.addFlashAttribute("message", "Tag deleted successfully  " + "!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Tag failed to delete  " + "!");
			return "redirect:/staff/tagsCrud";
		}
		return "redirect:/staff/tagsCrud";
	}

	public List<MenuCategory> getStaffCategories() {

		List<MenuCategory> staffCategories = new ArrayList<>();

		MenuCategory category1 = new MenuCategory(1L, "Categories Master Data", null, false, false,
				"/staff/categoryCrud");
		MenuCategory category2 = new MenuCategory(2L, "Sub Categories Master Data", null, false, false,
				"/staff/subCategoryCrud");
		MenuCategory category3 = new MenuCategory(3L, "Tags Master Data", null, false, false, "/staff/tagsCrud");
		MenuCategory category4 = new MenuCategory(3L, "Posts Data", null, false, false, "/staff/postsCrud");

		staffCategories.add(category1);
		staffCategories.add(category2);
		staffCategories.add(category3);
		staffCategories.add(category4);

		return staffCategories;

	}
}
