package eu.trentorise.smartcampus.social.engine.repo.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialComment;

public interface CommentRepository extends MongoRepository<SocialComment, String>{
	
	public SocialComment findById(String id);
	
	public SocialComment findByAuthorIgnoreCaseAndEntityId(String author, String entityId);
	
	public List<SocialComment> findByEntityId(String entityId);
	
}
