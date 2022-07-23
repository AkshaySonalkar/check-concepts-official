package com.checkconcepts.web.staff.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.checkconcepts.persistence.dao.CategoryRepository;
import com.checkconcepts.persistence.dao.SubCategoryRepository;
import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.persistence.model.SubCategory;
import com.checkconcepts.service.ICategoryService;
import com.checkconcepts.service.ISubCategoryService;
import com.checkconcepts.web.dto.CategoryDto;
import com.checkconcepts.web.dto.SubCategoryDto;
import com.checkconcepts.web.error.CategoryAlreadyExistException;
import com.checkconcepts.web.util.GenericResponse;

@RestController
public class StaffActionsRestController {

	@Autowired
	ICategoryService categoryService;

	@Autowired
	ISubCategoryService subCategoryService;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	SubCategoryRepository subCategoryRepository;

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@PostMapping("staff/category/save")
	public GenericResponse categorySave(@Valid final CategoryDto categoryDto, final HttpServletRequest request) {
		LOGGER.debug("New Category Save : {}", categoryDto);
		try {
			final Category category = categoryService.createNewCategory(categoryDto);
		} catch (CategoryAlreadyExistException e) {
			return new GenericResponse("CategoryAlreadyExistException");
		}

		return new GenericResponse("success");
	}

	@PostMapping("staff/subcategory/save")
	public GenericResponse subCategorySave(@Valid final SubCategoryDto categoryDto, final HttpServletRequest request) {
		LOGGER.debug("New Sub Category Save : {}", categoryDto);
		try {
			final SubCategory category = subCategoryService.createNewSubCategory(categoryDto);
		} catch (Exception e) {
			return new GenericResponse("CategoryAlreadyExistException");
		}
		return new GenericResponse("success");
	}
}
