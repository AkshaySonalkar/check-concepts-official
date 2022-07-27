package com.checkconcepts.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.checkconcepts.persistence.dao.PostRepository;
import com.checkconcepts.persistence.dao.SubCategoryRepository;
import com.checkconcepts.persistence.model.Post;
import com.checkconcepts.persistence.model.PostsStatus;
import com.checkconcepts.persistence.model.User;
import com.checkconcepts.web.dto.PostDto;
import com.checkconcepts.web.error.PostAlreadyExistException;


@Service
@Transactional
public class PostService implements IPostService {

	@Autowired
	PostRepository postRepository;

	@Autowired
	SubCategoryRepository subCategoryRepository;

	@Autowired
	private UserService userService;
	
	@Autowired(required=true)
	private HttpServletRequest request;

	@Override
	public List<Post> findAll() {
		return postRepository.findAll();
	}

	@Override
	public Post createNewPost(PostDto postDto) {
		if (postExists(postDto.getTitle())) {
			throw new PostAlreadyExistException("Post already exist: " + postDto.getTitle());
		}
		final Post post = new Post();
		post.setTitle(postDto.getTitle());
		post.setDescription(postDto.getDescription());
		post.setStatus(PostsStatus.CREATED);
		post.setSubCategoryType(subCategoryRepository.getById(postDto.getCategoryId()));
		post.setAuthor((User)request.getSession().getAttribute("userObj"));
		post.setCreatedAt(new Date());
		post.setUpdatedAt(new Date());
		return postRepository.save(post);
	}

	private boolean postExists(final String title) {
		return postRepository.findByTitle(title) != null;
	}

	@Override
	public Optional<Post> findPostById(Long id) {
		return postRepository.findById(id);
	}

	@Override
	public void save(Post category) {
		postRepository.save(category);
	}

	@Override
	public void delete(Post category) {
		postRepository.delete(category);
	}

}
