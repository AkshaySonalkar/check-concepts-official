package com.checkconcepts.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkconcepts.persistence.model.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

	SubCategory findByName(String categoryName);
}
