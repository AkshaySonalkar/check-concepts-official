package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.web.dto.PostDto;

public interface IPostService {

	List<Post> findAll();
	Post createNewPost(PostDto categoryDto); 
	Optional<Post> findPostById(Long id);
	void save(Post category);
	void delete(Post category);
}
