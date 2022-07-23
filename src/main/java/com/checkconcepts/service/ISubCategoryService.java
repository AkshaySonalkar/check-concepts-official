package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import com.checkconcepts.persistence.model.SubCategory;
import com.checkconcepts.web.dto.SubCategoryDto;

public interface ISubCategoryService {

	List<SubCategory> findAll();
	SubCategory createNewSubCategory(SubCategoryDto categoryDto); 
	Optional<SubCategory> findSubCategoryById(Long id);
	void save(SubCategory category);
	void delete(SubCategory category);
}
