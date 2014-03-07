package eu.trentorise.smartcampus.social.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.beans.Comment;
import eu.trentorise.smartcampus.social.engine.model.SocialComment;
import eu.trentorise.smartcampus.social.engine.repo.mongo.CommentRepository;

@Component
@Transactional
public class SocialCommentManager {
	
	private static final Logger logger = Logger.getLogger(SocialCommentManager.class);
	private static final String deleteComment = "User '%s' has delete the comment.";
	
	@Autowired
	CommentRepository commentRepository;
	
	public Comment create(String text, String user, String entityId){
		if(!StringUtils.hasLength(text)){
			throw new IllegalArgumentException("param 'text' should be valid");
		}
		if(!StringUtils.hasLength(user)){
			throw new IllegalArgumentException("param 'user' should be valid");
		}
		if(!StringUtils.hasLength(entityId)){
			throw new IllegalArgumentException("param 'entityId' should be valid");
		}
		
		//if(commentRepository.findByAuthorIgnoreCaseAndEntityId(user, entityId) != null){
		//	throw new IllegalArgumentException(String.format("comment for entity '%s' already inserted by user '%s'", entityId, user));
		//}
		
		SocialComment comment = new SocialComment(text, user, entityId);
		return commentRepository.save(comment).toComment();
	}
	
	public Comment readComment(String commentId){
		if(!StringUtils.hasLength(commentId)){
			throw new IllegalArgumentException("param 'commentId' should be valid");
		}
		SocialComment result = commentRepository.findById(commentId);
		return result != null ? result.toComment() : null;
	}
	
	public Comment delete(String commentId, String user){
		if(!StringUtils.hasLength(commentId)){
			throw new IllegalArgumentException("param 'commentId' should be valid");
		}
		if(!StringUtils.hasLength(user)){
			throw new IllegalArgumentException("param 'user' should be valid");
		}
		
		SocialComment comment = commentRepository.findOne(commentId);
		comment.setDeleted(true);
		comment.setText(String.format(deleteComment, user));
		return commentRepository.save(comment).toComment();
	}
	
	public boolean remove(String commentId, String user){
		if(!StringUtils.hasLength(commentId)){
			logger.error("param 'commentId' should be valid");
			return true;
		}
		if(!StringUtils.hasLength(user)){
			logger.error("param 'author' should be valid");
			return true;
		}
		try{
			commentRepository.delete(commentId);
		} catch(Exception ex){
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean removeByEntity(String entityId){
		if(!StringUtils.hasLength(entityId)){
			logger.error("null 'entityId' passed to the manager.");
			return true;
		}
		
		try{
			List<SocialComment> entityComments = commentRepository.findByEntityId(entityId);
			if(entityComments!= null && !entityComments.isEmpty()){
				for(SocialComment comment:entityComments){
					commentRepository.delete(comment.getId());
				}
			} else {
				logger.warn(String.format("no comment found for entity '%s'.", entityId));
			}
		} catch(Exception ex){
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}
	
	

}
