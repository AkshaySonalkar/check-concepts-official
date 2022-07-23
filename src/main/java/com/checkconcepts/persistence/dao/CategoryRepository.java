package com.checkconcepts.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkconcepts.persistence.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findByName(String categoryName);
}
