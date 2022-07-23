package com.checkconcepts.web.staff.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.checkconcepts.menu.MenuCategory;
import com.checkconcepts.persistence.dao.CategoryRepository;
import com.checkconcepts.persistence.dao.SubCategoryRepository;
import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.persistence.model.SubCategory;
import com.checkconcepts.service.ICategoryService;
import com.checkconcepts.service.ISubCategoryService;
import com.checkconcepts.service.StorageService;
import com.checkconcepts.web.controller.FileUploadController;

@Controller
public class StaffPagesNavigationController {
	
	private final StorageService storageService;

	@Autowired
	public StaffPagesNavigationController(StorageService storageService) {
		this.storageService = storageService;
	}
	
	@Autowired
	private MessageSource messages;

	@Autowired
	ICategoryService categoryService;

	@Autowired
	ISubCategoryService subCategoryService;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@GetMapping("/staff/staffConsole")
	public ModelAndView staffConsole(final HttpServletRequest request, final ModelMap model,
			@RequestParam("messageKey") final Optional<String> messageKey) {

		Locale locale = request.getLocale();
		messageKey.ifPresent(key -> {
			String message = messages.getMessage(key, null, locale);
			model.addAttribute("message", message);
		});
		model.addAttribute("data", "Staff Console Data");
		model.addAttribute("sidebarHeader", "Check-Concepts");
		model.addAttribute("sidebarcategories", getStaffCategories());

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
		Category category = categoryRepository.findById(id)
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
		SubCategory category = subCategoryRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
		model.addAttribute("subcategory", category);
		return "update-subcategory";
	}

	@PostMapping("/staff/update/category/{id}")
	public String updateCategory(@PathVariable("id") long id, @Valid Category category, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			category.setId(id);
			return "update-category";
		}
		Category categoryObj = categoryRepository.findById(id).get();
		categoryObj.setName(category.getName());
		categoryObj.setDescription(category.getDescription());
		categoryObj.setPremium(category.isPremium());
		categoryObj.setTech(category.isTech());
		categoryService.save(categoryObj);

		return "redirect:/staff/categoryCrud";
	}

	@GetMapping("/staff/category/delete/{id}")
	public String deleteCategory(@PathVariable("id") long id, @Valid Category category, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			category.setId(id);
			return "redirect:/staff/categoryCrud";
		}
		Category categoryObj = categoryRepository.findById(id).get();
		try {
			categoryService.delete(categoryObj);
		} catch (Exception e) {
			// TODO: handle exception
			return "redirect:/staff/categoryCrud";
		}
		return "redirect:/staff/categoryCrud";
	}

	@PostMapping("/staff/update/subcategory/{id}")
	public String updateSubCategory(@PathVariable("id") long id, @Valid SubCategory category, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			category.setId(id);
			return "update-subcategory";
		}
		SubCategory categoryObj = subCategoryRepository.findById(id).get();
		categoryObj.setName(category.getName());
		categoryObj.setDescription(category.getDescription());
		categoryObj.setPremium(category.isPremium());
		subCategoryService.save(categoryObj);

		return "redirect:/staff/subCategoryCrud";
	}

	@GetMapping("/staff/subcategory/delete/{id}")
	public String deleteSubCategory(@PathVariable("id") long id, @Valid Category category, BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			category.setId(id);
			return "redirect:/staff/subCategoryCrud";
		}
		SubCategory categoryObj = subCategoryRepository.findById(id).get();
		try {
			subCategoryService.delete(categoryObj);
		} catch (Exception e) {
			// TODO: handle exception
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

		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);

		List<SubCategory> subcategories = subCategoryService.findAll();
		model.addAttribute("subcategories", subcategories);

		model.addAttribute("sidebarHeader", "Check-Concepts");
		
		
		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
						"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));

		return new ModelAndView("postsCrud", model);
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

	public List<MenuCategory> getStaffCategories() {

		List<MenuCategory> staffCategories = new ArrayList<>();

		MenuCategory category1 = new MenuCategory(1L, "Create/Update Categories", null, false, false,
				"/staff/categoryCrud");
		MenuCategory category4 = new MenuCategory(4L, "Create/Update Sub Categories", null, false, false,
				"/staff/subCategoryCrud");
		MenuCategory category2 = new MenuCategory(2L, "Create/Update Posts", null, false, false, "/staff/postsCrud");
		MenuCategory category3 = new MenuCategory(3L, "All Staff Details", null, false, false, "/staff/staffUsers");

		staffCategories.add(category1);
		staffCategories.add(category4);
		staffCategories.add(category2);
		staffCategories.add(category3);

		return staffCategories;

	}
}
