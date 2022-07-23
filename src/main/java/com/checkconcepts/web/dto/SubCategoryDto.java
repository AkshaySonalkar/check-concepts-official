package com.checkconcepts.web.dto;

import javax.validation.constraints.NotNull;

public class SubCategoryDto {

	@NotNull
	private Long categoryId;

	@NotNull
	private String name;

	@NotNull
	private String description;

	private boolean premium;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}
}
