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
	private static final long TEST_TIME_GAP = 0L;//86400000;//0L;//-86400000;
	
	@Id
	private String id;
	
	@Field("commentText")
	private String text;
	
	@Field("entityId")
	private String entityId;

	@Field("author")
	private String author;
	
	@Field("creationTime")
	private Long creationTime;
	
	@Field("deleted")
	private boolean deleted;
	
	public SocialComment(){
		this.deleted = false;
		this.creationTime = System.currentTimeMillis() + TEST_TIME_GAP;
	}
	
	public SocialComment(String text){
		this.text = text;
		this.deleted = false;
		this.creationTime = System.currentTimeMillis()  + TEST_TIME_GAP;
	}
	
	public SocialComment(String text, String author, String entityId){
		this.text = text;
		this.author = author;
		this.entityId = entityId;
		this.deleted = false;
		this.creationTime = System.currentTimeMillis() + TEST_TIME_GAP;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getEntityId() {
		return entityId;
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

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Comment toComment(){
		Comment comment = new Comment();
		comment.setId(id);
		comment.setText(text);
		comment.setEntityId(entityId);
		comment.setAuthor(author);
		comment.setCreationTime(creationTime);
		comment.setDeleted(deleted);
		return comment;
	}
	
	public static List<Comment> toComment(Iterable<SocialComment> comments) {	
		List<Comment> outputComments = new ArrayList<Comment>();
		if(comments != null){
			for (SocialComment comment : comments) {
				outputComments.add(comment.toComment());
			}
		}
		return outputComments;
	}
	
	public static Long convertId(String id){
		try {
			return Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(id + " not valid");
		}
	}
	
}

