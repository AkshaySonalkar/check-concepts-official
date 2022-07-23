package com.checkconcepts.persistence.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
@Table(name = "posts")
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 300)
	private String title;

	@Lob
	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private Date createdAt = new Date();

	@Column
	private Date updatedAt = new Date();

	@Column
	private boolean published;

	@Column
	private Date publishedAt = new Date();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private User author;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private SubCategory subCategoryType;

	@OneToMany(mappedBy = "parentPost")
	private Set<PostsMeta> postsMeta = new HashSet<PostsMeta>();

	@ElementCollection
	private List<Tag> tags = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public SubCategory getSubCategoryType() {
		return subCategoryType;
	}

	public void setSubCategoryType(SubCategory subCategoryType) {
		this.subCategoryType = subCategoryType;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public Date getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}

	public Set<PostsMeta> getPostsMeta() {
		return postsMeta;
	}

	public void setPostsMeta(Set<PostsMeta> postsMeta) {
		this.postsMeta = postsMeta;
	}

	public Post() {
	}

	public Post(Long id, String title, String description, Date createdAt, Date updatedAt, boolean published,
			Date publishedAt, User author, SubCategory subCategoryType, Set<PostsMeta> postsMeta, List<Tag> tags) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.published = published;
		this.publishedAt = publishedAt;
		this.author = author;
		this.subCategoryType = subCategoryType;
		this.postsMeta = postsMeta;
		this.tags = tags;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		return Objects.equals(id, other.id) && Objects.equals(title, other.title);
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", title=" + title + ", description=" + description + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", published=" + published + ", publishedAt=" + publishedAt + ", author="
				+ author + ", subCategoryType=" + subCategoryType + ", postsMeta=" + postsMeta + ", tags=" + tags + "]";
	}

}
