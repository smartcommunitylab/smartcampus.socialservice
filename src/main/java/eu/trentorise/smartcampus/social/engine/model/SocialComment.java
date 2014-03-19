package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import eu.trentorise.smartcampus.social.engine.beans.Comment;

@Document(collection = "comment")
public class SocialComment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	@Field("commentText")
	private String text;

	@Field("entityURI")
	private String entityURI;

	@Field("author")
	private String author;

	@Field("creationTime")
	private Long creationTime;

	@Field("deleted")
	private boolean deleted;

	public SocialComment() {
		this.deleted = false;
		this.creationTime = System.currentTimeMillis();
	}

	public SocialComment(String text) {
		this.text = text;
		this.deleted = false;
		this.creationTime = System.currentTimeMillis();
	}

	public SocialComment(String text, String author, String entityId) {
		this.text = text;
		this.author = author;
		this.entityURI = entityId;
		this.deleted = false;
		this.creationTime = System.currentTimeMillis();
	}

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

	public Comment toComment() {
		Comment comment = new Comment();
		comment.setId(id);
		comment.setText(text);
		comment.setEntityURI(entityURI);
		comment.setAuthor(author);
		comment.setCreationTime(creationTime);
		comment.setDeleted(deleted);
		return comment;
	}

	public static List<Comment> toComment(Iterable<SocialComment> comments) {
		List<Comment> outputComments = new ArrayList<Comment>();
		if (comments != null) {
			for (SocialComment comment : comments) {
				outputComments.add(comment.toComment());
			}
		}
		return outputComments;
	}

	public static Long convertId(String id) {
		try {
			return Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(id + " not valid");
		}
	}

}
