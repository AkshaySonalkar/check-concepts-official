package com.checkconcepts.persistence.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "posts_attachments")
public class PostsAttachments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column
	private String attachmentType;

	@Column
	private String attachmentSrc;
	
	@Column(columnDefinition = "boolean default false")
	private boolean supportingDoc;
	
	@Column(columnDefinition = "boolean default false")
	private boolean profilePic;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Post parentPostAttachment;

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

	public String getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(String attachmentType) {
		this.attachmentType = attachmentType;
	}

	public String getAttachmentSrc() {
		return attachmentSrc;
	}

	public void setAttachmentSrc(String attachmentSrc) {
		this.attachmentSrc = attachmentSrc;
	}

	public boolean isSupportingDoc() {
		return supportingDoc;
	}

	public void setSupportingDoc(boolean supportingDoc) {
		this.supportingDoc = supportingDoc;
	}

	public Post getParentPostAttachment() {
		return parentPostAttachment;
	}

	public void setParentPostAttachment(Post parentPostAttachment) {
		this.parentPostAttachment = parentPostAttachment;
	}

	public boolean isProfilePic() {
		return profilePic;
	}

	public void setProfilePic(boolean profilePic) {
		this.profilePic = profilePic;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostsAttachments other = (PostsAttachments) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "PostsAttachments [id=" + id + ", name=" + name + ", attachmentType=" + attachmentType
				+ ", attachmentSrc=" + attachmentSrc + ", parentPostAttachment=" + parentPostAttachment + "]";
	}
}
