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
import com.checkconcepts.menu.MenuSubCategory;
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

		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);

		return "index";
	}

	@GetMapping("/contact")
	public String contact(final HttpServletRequest request, final Model model) {
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		Locale locale = request.getLocale();
		return "contact";
	}

	@GetMapping("/about")
	public String about(final HttpServletRequest request, final Model model) {
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		Locale locale = request.getLocale();
		return "aboutus";
	}

	@GetMapping("/tech/post/{id}/{name}")
	public String techPostView(@PathVariable("id") Long id, @PathVariable("name") String name,
			final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		Post post = postService.findPostById(id).get();
		Set<Post> posts = postService.findPostById(id).get().getSubCategoryType().getPosts();

		model.addAttribute("sidebarHeader", postService.findPostById(id).get().getSubCategoryType().getName());
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(posts, true, false));

		boolean supportingDocPresent = post.getPostsAttachments().stream().anyMatch(att -> att.isSupportingDoc());
		model.addAttribute("supportingDocPresent", supportingDocPresent);

		model.addAttribute("post", post);

		List<Post> relatedPosts = post.getSubCategoryType().getPosts().stream().limit(10).collect(Collectors.toList());
		model.addAttribute("relatedPosts", relatedPosts);

		Category cat = post.getSubCategoryType().getCategoryType();
		model.addAttribute("postcategory", cat);

		return "/techPost";
	}

	@GetMapping("/nonTech/post/{id}/{name}")
	public String nonTechPostView(@PathVariable("id") Long id, @PathVariable("name") String name,
			final HttpServletRequest request, final Model model) {
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		Post post = postService.findPostById(id).get();
		Set<Post> posts = postService.findPostById(id).get().getSubCategoryType().getPosts();

		model.addAttribute("sidebarHeader", postService.findPostById(id).get().getSubCategoryType().getName());
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(posts, false, false));

		boolean supportingDocPresent = post.getPostsAttachments().stream().anyMatch(att -> att.isSupportingDoc());
		model.addAttribute("supportingDocPresent", supportingDocPresent);
		model.addAttribute("post", post);

		List<Post> relatedPosts = post.getSubCategoryType().getPosts().stream().limit(10).collect(Collectors.toList());
		model.addAttribute("relatedPosts", relatedPosts);

		Category cat = post.getSubCategoryType().getCategoryType();
		model.addAttribute("postcategory", cat);
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
		
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);

		List<MenuCategory> categories = getTechCategories();
		Set<MenuCategory> displayCategories = new HashSet<MenuCategory>();

		Set<Post> allPosts = new HashSet<>();
		StringBuilder sb = new StringBuilder();
		for (MenuCategory cat : categories) {
			Set<SubCategory> subCategories = categoryService.findCategoryById(cat.getId()).get().getSubCategories();
			for (SubCategory subcat : subCategories) {
				int subCatPostsCount = subcat.getPosts().stream()
						.filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet()).size();

				if (subCatPostsCount > 0) {
					displayCategories.add(cat);
				}

				allPosts.addAll(subcat.getPosts().stream().filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED))
						.collect(Collectors.toSet()));
				sb.append(subcat.getName() + ", ");
			}
		}

		model.addAttribute("posts", allPosts.stream().sorted((o1, o2) -> o2.getUpdatedAt().compareTo(o1.getUpdatedAt()))
				.collect(Collectors.toList()));
		model.addAttribute("pagetitle", "Explore Technical Contents");
		model.addAttribute("pagedesc", "Explore " + sb + " and many more ...");
		model.addAttribute("categories", displayCategories);

		model.addAttribute("sidebarHeader", "Tech");
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(allPosts, true, false));

		return "allCategoryTechContents";
	}

	@GetMapping("/nonTech")
	public String exploreNonTech(final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);

		List<MenuCategory> categories = getNonTechCategories();
		Set<MenuCategory> displayCategories = new HashSet<MenuCategory>();

		Set<Post> allPosts = new HashSet<>();
		StringBuilder sb = new StringBuilder();
		for (MenuCategory cat : categories) {
			Set<SubCategory> subCategories = categoryService.findCategoryById(cat.getId()).get().getSubCategories();
			for (SubCategory subcat : subCategories) {

				int subCatPostsCount = subcat.getPosts().stream()
						.filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED)).collect(Collectors.toSet()).size();

				if (subCatPostsCount > 0) {
					displayCategories.add(cat);
				}

				allPosts.addAll(subcat.getPosts().stream().filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED))
						.collect(Collectors.toSet()));
				sb.append(subcat.getName() + ", ");
			}
		}

		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle", "Explore Non Technical Contents");
		model.addAttribute("pagedesc", "Explore " + sb + " and many more ...");
		model.addAttribute("categories", displayCategories);

		model.addAttribute("sidebarHeader", "Non Tech");
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(allPosts, false, false));

		return "allCategoryNonTechContents";
	}

	@GetMapping("/tech/category/{id}/{name}")
	public String exploreTechCategory(@PathVariable("id") long id, @PathVariable("name") String name,
			final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		
		Set<SubCategory> subCategories = categoryService.findCategoryById(id).get().getSubCategories();
		List<MenuCategory> techSubCategories = new ArrayList<>();
		Set<Post> allPosts = new HashSet<>();
		for (SubCategory cat : subCategories) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), true,
					cat.isPremium(), "/tech/subcategory/" + cat.getId());
			techSubCategories.add(category);
			allPosts.addAll(cat.getPosts().stream().filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED))
					.collect(Collectors.toSet()));
		}
//		model.addAttribute("sidebarHeader", categoryService.findCategoryById(id).get().getName() + " SubCategories");
//		model.addAttribute("sidebarcategories", techCategories);
		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle",
				"Explore " + categoryService.findCategoryById(id).get().getName() + " Technical Contents");
		model.addAttribute("pagedesc", "Explore " + categoryService.findCategoryById(id).get().getDescription());

		model.addAttribute("sidebarHeader", name);
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(allPosts, true, false));

		return "categorySpecificTechContents";
	}

	@GetMapping("/tech/subcategory/{id}/{name}")
	public String exploreTechSubCategory(@PathVariable("id") long id, @PathVariable("name") String name,
			final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		
		SubCategory subCategories = subCategoryService.findSubCategoryById(id).get();

		Set<Post> allPosts = new HashSet<>();

		allPosts.addAll(subCategories.getPosts().stream().filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED))
				.collect(Collectors.toSet()));

		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle",
				"Explore " + categoryService.findCategoryById(id).get().getName() + " Technical Contents");
		model.addAttribute("pagedesc", "Explore " + categoryService.findCategoryById(id).get().getDescription());

		model.addAttribute("sidebarHeader", name);
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(allPosts, true, false));

		return "categorySpecificTechContents";
	}

	@GetMapping("/nonTech/category/{id}/{name}")
	public String exploreNonTechCategory(@PathVariable("id") long id, @PathVariable("name") String name,
			final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		
		Set<SubCategory> subCategories = categoryService.findCategoryById(id).get().getSubCategories();
		List<MenuCategory> techSubCategories = new ArrayList<>();
		Set<Post> allPosts = new HashSet<>();
		for (SubCategory cat : subCategories) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), false,
					cat.isPremium(), "/nonTech/subcategory/" + cat.getId());
			techSubCategories.add(category);
			allPosts.addAll(cat.getPosts().stream().filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED))
					.collect(Collectors.toSet()));
		}
//		model.addAttribute("sidebarHeader", categoryService.findCategoryById(id).get().getName() + " SubCategories");
//		model.addAttribute("sidebarcategories", techCategories);
		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle",
				"Explore " + categoryService.findCategoryById(id).get().getName() + " Non Technical Contents");
		model.addAttribute("pagedesc", "Explore " + categoryService.findCategoryById(id).get().getDescription());

		model.addAttribute("sidebarHeader", name);
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(allPosts, false, false));

		return "categorySpecificNonTechContents";
	}

	@GetMapping("/nonTech/subcategory/{id}/{name}")
	public String exploreNonTechSubCategory(@PathVariable("id") long id, @PathVariable("name") String name,
			final HttpServletRequest request, final Model model) {
		
		List<MenuCategory> techCategories = getTechCategoriesWithSubCategories();
		model.addAttribute("techCategories", techCategories);

		List<MenuCategory> nonTechCategories = getNonTechCategoriesWithSubCategories();
		model.addAttribute("nonTechCategories", nonTechCategories);
		
		SubCategory subCategories = subCategoryService.findSubCategoryById(id).get();

		Set<Post> allPosts = new HashSet<>();

		allPosts.addAll(subCategories.getPosts().stream().filter(p -> p.getStatus().equals(PostsStatus.PUBLISHED))
				.collect(Collectors.toSet()));

		model.addAttribute("posts", allPosts);
		model.addAttribute("pagetitle",
				"Explore " + categoryService.findCategoryById(id).get().getName() + " Non Technical Contents");
		model.addAttribute("pagedesc", "Explore " + categoryService.findCategoryById(id).get().getDescription());

		model.addAttribute("sidebarHeader", name);
		model.addAttribute("sidebarcategories", getPostsDataMenuCategory(allPosts, false, false));

		return "categorySpecificNonTechContents";
	}

	public List<MenuCategory> getTechCategories() {

		List<MenuCategory> techCategories = new ArrayList<>();

		List<Category> categoryList = categoryService.findAll().stream().filter(cat -> cat.isTech())
				.collect(Collectors.toList());
		for (Category cat : categoryList) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), true,
					cat.isPremium(), "/tech/category/" + cat.getId() + "/" + cat.getName());
			techCategories.add(category);
		}
		return techCategories;

	}

	public List<MenuCategory> getNonTechCategories() {

		List<MenuCategory> nonTechCategories = new ArrayList<>();

		List<Category> categoryList = categoryService.findAll().stream().filter(cat -> !cat.isTech())
				.collect(Collectors.toList());
		for (Category cat : categoryList) {
			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), false,
					cat.isPremium(), "/nonTech/category/" + cat.getId() + "/" + cat.getName());
			nonTechCategories.add(category);
		}
		return nonTechCategories;

	}

	public List<MenuCategory> getTechCategoriesWithSubCategories() {

		List<MenuCategory> techCategories = new ArrayList<>();

		List<Category> categoryList = categoryService.findAll().stream().filter(cat -> cat.isTech())
				.collect(Collectors.toList());

		for (Category cat : categoryList) {

			List<MenuSubCategory> subcategories = new ArrayList<>();

			if (!cat.getSubCategories().isEmpty()) {

				for (SubCategory subcat : cat.getSubCategories()) {

					MenuSubCategory subcategory = new MenuSubCategory(subcat.getId(), subcat.getName(),
							subcat.getDescription(), subcat.isPremium(),
							"/tech/subcategory/" + subcat.getId() + "/" + subcat.getName());

					subcategories.add(subcategory);
				}

			}

			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), true,
					cat.isPremium(), "/tech/category/" + cat.getId() + "/" + cat.getName(), subcategories);
			techCategories.add(category);
		}
		return techCategories;

	}

	public List<MenuCategory> getNonTechCategoriesWithSubCategories() {

		List<MenuCategory> nonTechCategories = new ArrayList<>();

		List<Category> categoryList = categoryService.findAll().stream().filter(cat -> !cat.isTech())
				.collect(Collectors.toList());
		for (Category cat : categoryList) {

			List<MenuSubCategory> subcategories = new ArrayList<>();

			if (!cat.getSubCategories().isEmpty()) {

				for (SubCategory subcat : cat.getSubCategories()) {

					MenuSubCategory subcategory = new MenuSubCategory(subcat.getId(), subcat.getName(),
							subcat.getDescription(), subcat.isPremium(),
							"/nonTech/subcategory/" + subcat.getId() + "/" + subcat.getName());

					subcategories.add(subcategory);
				}

			}

			MenuCategory category = new MenuCategory(cat.getId(), cat.getName(), cat.getDescription(), true,
					cat.isPremium(), "/nonTech/category/" + cat.getId() + "/" + cat.getName(), subcategories);
			nonTechCategories.add(category);
		}
		return nonTechCategories;

	}

	public List<MenuCategory> getPostsDataMenuCategory(Set<Post> allPosts, boolean tech, boolean premium) {

		List<MenuCategory> postsDataCategories = new ArrayList<>();

		for (Post post : allPosts) {
			MenuCategory postCat = new MenuCategory(post.getId(), post.getTitle(), post.getDescription(), tech, premium,
					tech ? "/tech/post/" + post.getId() + "/" + post.getTitle().replace(" ", "-")
							: "/nonTech/post/" + post.getId() + "/" + post.getTitle().replace(" ", "-"));
			postsDataCategories.add(postCat);
		}
		return postsDataCategories;

	}

}
