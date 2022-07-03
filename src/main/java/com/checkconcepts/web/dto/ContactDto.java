package com.checkconcepts.web.dto;

import javax.validation.constraints.NotNull;

import com.checkconcepts.validation.ValidEmail;

public class ContactDto {
	
	@NotNull
	private String name;
	
	@ValidEmail
	@NotNull
	private String email;
	
	@NotNull
	private String subject;
	
	@NotNull
	private String message;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
