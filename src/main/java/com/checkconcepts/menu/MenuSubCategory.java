package com.checkconcepts.menu;

import java.io.Serializable;
import java.util.Objects;

public class MenuSubCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private final String name;
	private final String description;
	private boolean premium;
	private String pageUrl;

	public MenuSubCategory(Long id, String name, String description, boolean premium, String pageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.premium = premium;
		this.pageUrl = pageUrl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isPremium() {
		return premium;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MenuSubCategory)) {
			return false;
		}
		MenuSubCategory other = (MenuSubCategory) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "MenuSubCategory [name=" + name + "]";
	}
}
