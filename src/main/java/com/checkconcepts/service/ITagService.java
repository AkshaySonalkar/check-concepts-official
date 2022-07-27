package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import com.checkconcepts.persistence.model.Tag;
import com.checkconcepts.web.dto.TagDto;

public interface ITagService {

	List<Tag> findAll();
	Tag createNewTag(TagDto categoryDto); 
	Optional<Tag> findTagById(Long id);
	void save(Tag tag);
	void delete(Tag tag);
}
