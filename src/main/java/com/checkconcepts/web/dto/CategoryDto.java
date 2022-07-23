package com.checkconcepts.web.dto;

import javax.validation.constraints.NotNull;

public class CategoryDto {

	@NotNull
	private String name;

	@NotNull
	private String description;

	private boolean tech;

	private boolean premium;

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

	public boolean isTech() {
		return tech;
	}

	public void setTech(boolean tech) {
		this.tech = tech;
	}

	public boolean isPremium() {
		return premium;
	}

	public void setPremium(boolean premium) {
		this.premium = premium;
	}

}
