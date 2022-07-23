package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.checkconcepts.persistence.dao.CategoryRepository;
import com.checkconcepts.persistence.dao.SubCategoryRepository;
import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.persistence.model.SubCategory;
import com.checkconcepts.web.dto.SubCategoryDto;
import com.checkconcepts.web.error.CategoryAlreadyExistException;

@Service
@Transactional
public class SubCategoryService implements ISubCategoryService {

	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	SubCategoryRepository subCategoryRepository;
	
	@Override
	public List<SubCategory> findAll() {
		// TODO Auto-generated method stub
		return subCategoryRepository.findAll();
	}

	@Override
	public SubCategory createNewSubCategory(SubCategoryDto categoryDto) {
        if (categoryExists(categoryDto.getName())) {
            throw new CategoryAlreadyExistException("Category already exist: " + categoryDto.getName());
        }
        Optional<Category> category = categoryRepository.findById(categoryDto.getCategoryId());
        
        final SubCategory subcategory = new SubCategory();
        subcategory.setCategoryType(category.get());
        subcategory.setName(categoryDto.getName());
        subcategory.setDescription(categoryDto.getDescription());
        subcategory.setPremium(categoryDto.isPremium());

        return subCategoryRepository.save(subcategory);
    }
	
	 private boolean categoryExists(final String name) {
	        return subCategoryRepository.findByName(name) != null;
	    }

	@Override
	public Optional<SubCategory> findSubCategoryById(Long id) {
		return subCategoryRepository.findById(id);
	}

	@Override
	public void save(SubCategory category) {
		subCategoryRepository.save(category);
	}

	@Override
	public void delete(SubCategory category) {
		subCategoryRepository.delete(category);
	}
}
