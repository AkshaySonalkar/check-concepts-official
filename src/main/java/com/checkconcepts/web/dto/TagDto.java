package com.checkconcepts.web.dto;

import javax.validation.constraints.NotNull;

public class TagDto {

	@NotNull
	private String name;
	
	private boolean tech;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTech() {
		return tech;
	}

	public void setTech(boolean tech) {
		this.tech = tech;
	}
}
