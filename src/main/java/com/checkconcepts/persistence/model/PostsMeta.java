package com.checkconcepts.persistence.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "posts_meta")
public class PostsMeta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String keyType;

	@Lob
	@Column
	private String content;

	@Column
	private String imgSrc;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Post parentPost;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKeyType() {
		return keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Post getParentPost() {
		return parentPost;
	}

	public void setParentPost(Post parentPost) {
		this.parentPost = parentPost;
	}

	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, keyType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostsMeta other = (PostsMeta) obj;
		return Objects.equals(id, other.id) && Objects.equals(keyType, other.keyType);
	}

	@Override
	public String toString() {
		return "PostsMeta [id=" + id + ", keyType=" + keyType + ", content=" + content + ", imgSrc=" + imgSrc
				+ ", parentPost=" + parentPost + "]";
	}

}
