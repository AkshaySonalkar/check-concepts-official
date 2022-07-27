package com.checkconcepts.persistence.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "posts")
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 300, unique = true)
	private String title;

	@Lob
	@Column(nullable = false)
	private String description;
	
	@Lob
	@Column
	private String content;

	@Column(nullable = false)
	private Date createdAt = new Date();

	@Column
	private Date updatedAt = new Date();
	
	@Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
	private PostsStatus status;

	@Column
	private Date publishedAt = new Date();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private User author;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private SubCategory subCategoryType;
	
	@OneToMany(mappedBy = "parentPostAttachment")
	private Set<PostsAttachments> postsAttachments = new HashSet<PostsAttachments>();
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "posts_tags",
      joinColumns = @JoinColumn(name = "post_id", 
      referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id", 
      referencedColumnName = "id"))
    private Set<Tag> tags = new HashSet<>();
	

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

	public PostsStatus getStatus() {
		return status;
	}

	public void setStatus(PostsStatus status) {
		this.status = status;
	}

	public Date getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}
	
	public Set<PostsAttachments> getPostsAttachments() {
		return postsAttachments;
	}

	public void setPostsAttachments(Set<PostsAttachments> postsAttachments) {
		this.postsAttachments = postsAttachments;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Post() {
	}

	public Post(Long id, String title, String description, String content, Date createdAt, Date updatedAt, PostsStatus status,
			Date publishedAt, User author, SubCategory subCategoryType) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.content = content;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.status = status;
		this.publishedAt = publishedAt;
		this.author = author;
		this.subCategoryType = subCategoryType;
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
				+ ", updatedAt=" + updatedAt + ", status=" + status + ", publishedAt=" + publishedAt + ", author="
				+ author + ", subCategoryType=" + subCategoryType +  "]";
	}

}
