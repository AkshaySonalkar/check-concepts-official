package com.checkconcepts.persistence.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.checkconcepts.persistence.model.Post;

@Repository
public interface PostRepository extends
        JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.author ORDER BY p.updatedAt DESC")
    List<Post> findLatest5Posts();
    
    Post findByTitle(String postTitle);
    
    List<Post> findByTags_Name(String name);
}

