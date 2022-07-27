package com.checkconcepts.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkconcepts.persistence.model.Tag;

public interface TagsRepository extends JpaRepository<Tag, Long> {

	Tag findByName(String name);
}
