package com.checkconcepts.web.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.checkconcepts.menu.MenuCategory;
import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.PostsStatus;
import com.checkconcepts.persistence.model.SubCategory;
import com.checkconcepts.service.ICategoryService;
import com.checkconcepts.service.IPostService;
import com.checkconcepts.service.ISubCategoryService;

@Controller
public class PublicDashboardController {

	@Autowired
	private IPostService postService;
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	private ISubCategoryService subCategoryService;

	@Value("${spring.profiles.active}")
	private String activeProfile;

	private String getAppUrl(HttpServletRequest request) {
		if (activeProfile.equalsIgnoreCase("dev"))
			return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		if (activeProfile.equalsIgnoreCase("prod"))
			return "https://www.checkconcepts.org" + request.getContextPath();
		return null;
	}

	@GetMapping("/index")
	public String login(final HttpServletRequest request, final Model model) {
		return "index";
	}

	@GetMapping("/contact")
	public String contact(final HttpServletRequest request, final Model model) {
		Locale locale = request.getLocale();
		return "contact";
	}

	@GetMapping("/about")
	public String about(final HttpServletRequest request, final Model model) {
		Locale locale = request.getLocale();
		return "aboutus";
	}


	@GetMapping("/tech/post/{id}/{name}")
	public String techPostView(@PathVariable("id") Long id, @PathVariable("name") String name,
			final HttpServletRequest request, final Model model) {
		Post post = postService.findPostById(id).get();
		Set<Post> posts = postService.findPostById(id).get().getSubCategoryType().getPosts();

		List<MenuCategory> techCategories = new ArrayList<>();
		for (Post postObj : posts) {
			MenuCategory category = new MenuCategory(postObj.getId(), postObj.getTitle(), postObj.getDescription(),
					true, false, "/tech/post/" + postObj.getId() + "/" + postObj.getTitle());
			techCategories.add(category);
		}

		/*
		 * model.addAttribute("sidebarHeader",
		 * postService.findPostById(id).get().getSubCategoryType().getName() +
		 * " Posts"); model.addAttribute("sidebarcategories", techCategories);
		 */
		boolean supportingDocPresent = post.getPostsAttachments().stream().anyMatch(att->att.isSupportingDoc());
		model.addAttribute("supportingDocPresent", supportingDocPresent);
		model.addAttribute("post", post);
		return "/techPost";
	}

	@GetMapping("/nonTech/post/{id}/{name}")
	public String nonTechPostView(@PathVariable("id") Long id, @PathVariable("name") String name, final HttpServletRequest request, final Model model) {
		Post post = postService.findPostById(id).get();
		Set<Post> posts = postService.findPostById(id).get().getSubCategoryType().getPosts();

		List<MenuCategory> nonTtechCategories = new ArrayList<>();
		for (Post postObj : posts) {
			MenuCategory category = new MenuCategory(postObj.getId(), postObj.getTitle(), postObj.getDescription(),
					false, false, "/nonTech/post/" + postObj.getId() + "/" + postObj.getTitle());
			nonTtechCategories.add(category);
		}

		/*
		 * model.addAttribute("sidebarHeader",
		 * postService.findPostById(id).get().getSubCategoryType().getName() +
		 * " Posts"); model.addAttribute("sidebarcategories", nonTtechCategories);
		 */
		boolean supportingDocPresent = post.getPostsAttachments().stream().anyMatch(att->att.isSupportingDoc());
		model.addAttribute("supportingDocPresent", supportingDocPresent);
		model.addAttribute("post", post);
		return "/nonTechPost";
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

	@GetMapping("/tech")
	public String exploreTech(final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> categories = getTechCategories();
//		model.addAttribute("sidebarHeader", "Tech Categories");
//		model.addAttribute("sidebarcategories", categories);
		Set<Post> allPosts = new HashSet<>();
		StringBuilder sb = new StringBuilder();
		for(MenuCategory cat : categories) {
			Set<SubCategory> subCategories = categoryService.findCategoryById(cat.getId()).get().getSubCategories();
			for (SubCategory subcat : subCategories) {
				allPosts.addAll(subcat.getPosts().stream().filter(p->p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet()));
				sb.append(subcat.getName()+", ");
			}
		}
		
		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle", "Explore Technical Contents");
		model.addAttribute("pagedesc", "Explore "+sb+" and many more ...");
		
		return "allCategoryTechContents";
	}

	@GetMapping("/nonTech")
	public String exploreNonTech(final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> categories = getNonTechCategories();
//		model.addAttribute("sidebarHeader", "Non Tech Categories");
//		model.addAttribute("sidebarcategories", categories);
		Set<Post> allPosts = new HashSet<>();
		StringBuilder sb = new StringBuilder();
		for(MenuCategory cat : categories) {
			Set<SubCategory> subCategories = categoryService.findCategoryById(cat.getId()).get().getSubCategories();
			for (SubCategory subcat : subCategories) {
				allPosts.addAll(subcat.getPosts().stream().filter(p->p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet()));
				sb.append(subcat.getName()+", ");
			}
		}
		
		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle", "Explore Non Technical Contents");
		model.addAttribute("pagedesc", "Explore "+sb+" and many more ...");
		return "allCategoryNonTechContents";
	}

	@GetMapping("/tech/category/{id}")
	public String exploreTechCategory(@PathVariable("id") long id, final HttpServletRequest request,
			final Model model) {
		Set<SubCategory> subCategories = categoryService.findCategoryById(id).get().getSubCategories();
		List<MenuCategory> techCategories = new ArrayList<>();
		Set<Post> allPosts = new HashSet<>();
		for (SubCategory cat : subCategories) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), true,
					cat.isPremium(), "/tech/subcategory/" + cat.getId());
			techCategories.add(category);
			allPosts.addAll(cat.getPosts().stream().filter(p->p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet()));
		}
//		model.addAttribute("sidebarHeader", categoryService.findCategoryById(id).get().getName() + " SubCategories");
//		model.addAttribute("sidebarcategories", techCategories);
		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle", "Explore "+categoryService.findCategoryById(id).get().getName()+" Technical Contents");
		model.addAttribute("pagedesc", "Explore "+categoryService.findCategoryById(id).get().getDescription());
		return "categorySpecificTechContents";
	}

	@GetMapping("/nonTech/category/{id}")
	public String exploreNonTechCategory(@PathVariable("id") long id, final HttpServletRequest request,
			final Model model) {
		Set<SubCategory> subCategories = categoryService.findCategoryById(id).get().getSubCategories();
		List<MenuCategory> techCategories = new ArrayList<>();
		Set<Post> allPosts = new HashSet<>();
		for (SubCategory cat : subCategories) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), false,
					cat.isPremium(), "/nonTech/subcategory/" + cat.getId());
			techCategories.add(category);
			allPosts.addAll(cat.getPosts().stream().filter(p->p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet()));
		}
//		model.addAttribute("sidebarHeader", categoryService.findCategoryById(id).get().getName() + " SubCategories");
//		model.addAttribute("sidebarcategories", techCategories);
		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle", "Explore "+categoryService.findCategoryById(id).get().getName()+" Non Technical Contents");
		model.addAttribute("pagedesc", "Explore "+categoryService.findCategoryById(id).get().getDescription());
		return "categorySpecificNonTechContents";
	}

	@GetMapping("/tech/subcategory/{id}")
	public String exploreTechSubCategory(@PathVariable("id") long id, final HttpServletRequest request,
			final Model model) {
		Set<Post> posts = subCategoryService.findSubCategoryById(id).get().getPosts().stream().filter(p->p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet());
		List<MenuCategory> techCategories = new ArrayList<>();
		for (Post post : posts) {
			MenuCategory category = new MenuCategory(post.getId(), post.getTitle(), post.getDescription(), true, false,
					"/tech/post/" + post.getId() + "/" + post.getTitle());
			techCategories.add(category);
		}
//		model.addAttribute("sidebarHeader", subCategoryService.findSubCategoryById(id).get().getName() + " Posts");
//		model.addAttribute("sidebarcategories", techCategories);
		model.addAttribute("posts", posts);
		model.addAttribute("pagetitle", "Explore "+subCategoryService.findSubCategoryById(id).get().getName()+" Technical Contents");
		model.addAttribute("pagedesc", "Explore "+subCategoryService.findSubCategoryById(id).get().getDescription());
		return "subCategorySpecificTechContents";
	}

	@GetMapping("/nonTech/subcategory/{id}")
	public String exploreNonTechSubCategory(@PathVariable("id") long id, final HttpServletRequest request, final Model model) {
		Set<Post> posts = subCategoryService.findSubCategoryById(id).get().getPosts().stream().filter(p->p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet());
		List<MenuCategory> techCategories = new ArrayList<>();
		for (Post post : posts) {
			MenuCategory category = new MenuCategory(post.getId(), post.getTitle(), post.getDescription(), true, false,
					"/nonTech/post/" + post.getId() + "/" + post.getTitle());
			techCategories.add(category);
		}
//		model.addAttribute("sidebarHeader", subCategoryService.findSubCategoryById(id).get().getName() + " Posts");
//		model.addAttribute("sidebarcategories", techCategories);
		model.addAttribute("posts", posts);
		model.addAttribute("pagetitle", "Explore "+subCategoryService.findSubCategoryById(id).get().getName()+" Non Technical Contents");
		model.addAttribute("pagedesc", "Explore "+subCategoryService.findSubCategoryById(id).get().getDescription());
		return "subCategorySpecificNonTechContents";
	}

	public List<MenuCategory> getTechCategories() {

		List<MenuCategory> techCategories = new ArrayList<>();

		List<Category> categoryList = categoryService.findAll().stream().filter(cat -> cat.isTech())
				.collect(Collectors.toList());
		for (Category cat : categoryList) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), true,
					cat.isPremium(), "/tech/category/" + cat.getId());
			techCategories.add(category);
		}
		return techCategories;

	}
	
	public List<MenuCategory> getNonTechCategories() {

		List<MenuCategory> techCategories = new ArrayList<>();

		List<Category> categoryList = categoryService.findAll().stream().filter(cat -> !cat.isTech())
				.collect(Collectors.toList());
		for (Category cat : categoryList) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), false,
					cat.isPremium(), "/nonTech/category/" + cat.getId());
			techCategories.add(category);
		}
		return techCategories;

	}

}
