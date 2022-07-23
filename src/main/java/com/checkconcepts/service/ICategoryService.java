package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.web.dto.CategoryDto;

public interface ICategoryService {

	List<Category> findAll();
	Category createNewCategory(CategoryDto categoryDto); 
	Optional<Category> findCategoryById(Long id);
	void save(Category category);
	void delete(Category category);
}
