package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.checkconcepts.persistence.dao.TagsRepository;
import com.checkconcepts.persistence.model.Tag;
import com.checkconcepts.web.dto.TagDto;
import com.checkconcepts.web.error.TagAlreadyExistException;

@Service
@Transactional
public class TagService implements ITagService {

	@Autowired
	TagsRepository tagsRepository;

	@Override
	public List<Tag> findAll() {
		return tagsRepository.findAll();
	}

	@Override
	public Tag createNewTag(TagDto tagDto) {
		if (tagExists(tagDto.getName())) {
			throw new TagAlreadyExistException("Tag already exist: " + tagDto.getName());
		}
		final Tag tag = new Tag();
		tag.setName(tagDto.getName());
		tag.setTech(tagDto.isTech());
		return tagsRepository.save(tag);
	}

	private boolean tagExists(final String name) {
		return tagsRepository.findByName(name) != null;
	}

	@Override
	public Optional<Tag> findTagById(Long id) {
		return tagsRepository.findById(id);
	}

	@Override
	public void save(Tag tag) {
		tagsRepository.save(tag);
	}

	@Override
	public void delete(Tag tag) {
		tagsRepository.delete(tag);
	}

}
