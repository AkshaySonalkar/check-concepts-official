package com.checkconcepts.web.dto;

import javax.validation.constraints.NotNull;

public class PostDto {
	
	@NotNull
	private Long categoryId;

	@NotNull
	private String title;

	@NotNull
	private String description;
	

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
