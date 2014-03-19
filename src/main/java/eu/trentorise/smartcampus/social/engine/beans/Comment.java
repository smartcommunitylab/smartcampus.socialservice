package eu.trentorise.smartcampus.social.engine.beans;

import java.io.Serializable;

public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String text;
	private String entityURI;
	private String author;

	private Long creationTime;

	private boolean deleted;

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getEntityURI() {
		return entityURI;
	}

	public String getAuthor() {
		return author;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setEntityURI(String entityURI) {
		this.entityURI = entityURI;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

}
