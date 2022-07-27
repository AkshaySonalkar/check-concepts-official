package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import com.checkconcepts.persistence.model.PostsAttachments;

public interface IPostsAttachmentsService {

	List<PostsAttachments> findAll();
//	PostsAttachments createNewPostAttachment(PostDto categoryDto); 
	Optional<PostsAttachments> findPostAttachmentById(Long id);
	void save(PostsAttachments attachment);
	void delete(PostsAttachments attachment);
}
