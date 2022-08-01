package com.checkconcepts.web.staff.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.checkconcepts.persistence.dao.CategoryRepository;
import com.checkconcepts.persistence.dao.SubCategoryRepository;
import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.SubCategory;
import com.checkconcepts.persistence.model.Tag;
import com.checkconcepts.service.ICategoryService;
import com.checkconcepts.service.ISubCategoryService;
import com.checkconcepts.service.ITagService;
import com.checkconcepts.service.PostService;
import com.checkconcepts.web.dto.CategoryDto;
import com.checkconcepts.web.dto.PostDto;
import com.checkconcepts.web.dto.SubCategoryDto;
import com.checkconcepts.web.dto.TagDto;
import com.checkconcepts.web.error.CategoryAlreadyExistException;
import com.checkconcepts.web.error.PostAlreadyExistException;
import com.checkconcepts.web.error.TagAlreadyExistException;
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
	
	@Autowired
	PostService postService;
	
	@Autowired
	ITagService tagService;

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@PostMapping("staff/category/save")
	public GenericResponse categorySave(@Valid final CategoryDto categoryDto, final HttpServletRequest request) {
		LOGGER.debug("New Category Save : {}", categoryDto);
		try {
			final Category category = categoryService.createNewCategory(categoryDto);
		} catch (CategoryAlreadyExistException e) {
			return new GenericResponse("error","New Category failed to save, this category already exists  " + categoryDto.getName() + "!");
		} catch (Exception e) {
			return new GenericResponse("error","New Category failed to save  " + categoryDto.getName() + "!");
		}

		return new GenericResponse("success");
	}

	@PostMapping("staff/subcategory/save")
	public GenericResponse subCategorySave(@Valid final SubCategoryDto categoryDto, final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
		LOGGER.debug("New Sub Category Save : {}", categoryDto);
		try {
			final SubCategory category = subCategoryService.createNewSubCategory(categoryDto);
		} catch (CategoryAlreadyExistException e) {
			return new GenericResponse("error","New sub Category failed to save, this sub category already exists  " + categoryDto.getName() + "!");
		}
		return new GenericResponse("success");
	}
	
	@PostMapping("staff/post/save")
	public GenericResponse postSave(@Valid final PostDto postDto, final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
		LOGGER.debug("New Post Save : {}", postDto);
		try {
			final Post post = postService.createNewPost(postDto);
		} catch (PostAlreadyExistException e) {
			return new GenericResponse("error","New Post failed to save, this post title already exists  " + postDto.getTitle() + "!");
		} catch (Exception e) {
			return new GenericResponse("error","New Post failed to save  " + postDto.getTitle() + "!");
		}

		return new GenericResponse("success");
	}
	
	@PostMapping("staff/tag/save")
	public GenericResponse tagSave(@Valid final TagDto tagDto, final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
		LOGGER.debug("New Tag Save : {}", tagDto);
		try {
			final Tag tag = tagService.createNewTag(tagDto);
		} catch (TagAlreadyExistException e) {
			return new GenericResponse("error","New Tag failed to save, this tag already exists  " + tagDto.getName() + "!");
		}catch (Exception e) {
			return new GenericResponse("error","New Tag failed to save  " +  tagDto.getName() + "!");
		}

		return new GenericResponse("success");
	}
}
