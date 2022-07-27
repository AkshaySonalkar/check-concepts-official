package com.checkconcepts.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkconcepts.persistence.model.PostsAttachments;

public interface PostsAttachmentsRepository extends JpaRepository<PostsAttachments, Long> {

}
