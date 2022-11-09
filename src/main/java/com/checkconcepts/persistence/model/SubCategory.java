package com.checkconcepts.persistence.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "sub_category")
public class SubCategory implements Comparable<SubCategory> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100, unique = true)
	private String name;

	@Lob
	@Column(nullable = false)
	private String description;

	@Column
	private boolean premium;

	@OneToMany(mappedBy = "subCategoryType")
	private Set<Post> posts = new HashSet<Post>();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Category categoryType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Set<Post> getPosts() {
		return posts;
	}

	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}

	public Category getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(Category categoryType) {
		this.categoryType = categoryType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubCategory other = (SubCategory) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "SubCategory [id=" + id + ", subCategoryName=" + name + ", subCategoryDescription="
				+ description + ", premium=" + premium + ", posts=" + posts + ", categoryType="
				+ categoryType + "]";
	}

	@Override
	public int compareTo(SubCategory o) {
		if (id == o.getId())
			return 0;
		else if (id > o.getId())
			return 1;
		else
			return -1;
	}

}
