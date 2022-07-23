package com.checkconcepts.persistence.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.checkconcepts.persistence.model.PostsMeta;

public interface PostsMetaRepository extends JpaRepository<PostsMeta, Long> {

}
