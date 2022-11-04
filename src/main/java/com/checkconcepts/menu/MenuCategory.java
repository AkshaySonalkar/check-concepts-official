package com.checkconcepts.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuCategory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private final String name;
	private final String description;
	private boolean tech;
	private boolean premium;
	private String pageUrl;
	private List<MenuSubCategory> subcategoies = new ArrayList<>();

	public MenuCategory(Long id, String name, String description, boolean tech, boolean premium,String pageUrl) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.tech = tech;
		this.premium = premium;
		this.pageUrl = pageUrl;
	}
	
	public MenuCategory(Long id, String name, String description, boolean tech, boolean premium,String pageUrl, List<MenuSubCategory> subcategoies) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.tech = tech;
		this.premium = premium;
		this.pageUrl = pageUrl;
		this.subcategoies = subcategoies;
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

	public boolean isTech() {
		return tech;
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
	
	public List<MenuSubCategory> getSubcategoies() {
		return subcategoies;
	}

	public void setSubcategoies(List<MenuSubCategory> subcategoies) {
		this.subcategoies = subcategoies;
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
		if (!(obj instanceof MenuCategory)) {
			return false;
		}
		MenuCategory other = (MenuCategory) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "MenuCategory [name=" + name + "]";
	}
}
