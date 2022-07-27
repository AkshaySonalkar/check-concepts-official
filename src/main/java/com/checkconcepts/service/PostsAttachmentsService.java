package com.checkconcepts.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.checkconcepts.persistence.dao.PostsAttachmentsRepository;
import com.checkconcepts.persistence.model.PostsAttachments;
import com.checkconcepts.web.error.StorageException;

@Service
@Transactional
public class PostsAttachmentsService implements IPostsAttachmentsService {
	
	@Autowired
	PostsAttachmentsRepository postsAttachmentsRepository;

	@Override
	public List<PostsAttachments> findAll() {
		return postsAttachmentsRepository.findAll();
	}

	@Override
	public Optional<PostsAttachments> findPostAttachmentById(Long id) {
		return postsAttachmentsRepository.findById(id);
	}

	@Override
	public void save(PostsAttachments attachment){
		try {
			postsAttachmentsRepository.save(attachment);
		} catch (Exception e) {
			throw new StorageException("File save failed");
		}
	}

	@Override
	public void delete(PostsAttachments attachment) {
		postsAttachmentsRepository.delete(attachment);
	}

}
