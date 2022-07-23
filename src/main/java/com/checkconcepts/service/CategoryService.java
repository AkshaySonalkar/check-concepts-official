package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.checkconcepts.persistence.dao.CategoryRepository;
import com.checkconcepts.persistence.model.Category;
import com.checkconcepts.web.dto.CategoryDto;
import com.checkconcepts.web.error.CategoryAlreadyExistException;

@Service
@Transactional
public class CategoryService implements ICategoryService{
	
	@Autowired
	CategoryRepository categoryRepository;

	@Override
	public Category createNewCategory(CategoryDto categoryDto) {
        if (categoryExists(categoryDto.getName())) {
            throw new CategoryAlreadyExistException("Category already exist: " + categoryDto.getName());
        }
        final Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setPremium(categoryDto.isPremium());
        category.setTech(categoryDto.isTech());

        return categoryRepository.save(category);
    }
	
	 private boolean categoryExists(final String name) {
	        return categoryRepository.findByName(name) != null;
	    }

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Optional<Category> findCategoryById(Long id) {
		return categoryRepository.findById(id);
	}

	@Override
	public void save(Category category) {
		categoryRepository.save(category);
	}

	@Override
	public void delete(Category category) {
		categoryRepository.delete(category);		
	}

}
