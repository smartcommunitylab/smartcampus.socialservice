package eu.trentorise.smartcampus.social.engine.repo.mongo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialComment;

public interface CommentRepository extends MongoRepository<SocialComment, String>{
	
	public SocialComment findById(String id);
	
	public SocialComment findByAuthorIgnoreCaseAndEntityId(String author, String entityId);
	
	public SocialComment findByAuthorIgnoreCaseAndTextIgnoreCaseAndEntityId(String author, String text, String entityId);
	
	public List<SocialComment> findByEntityId(String entityId, Pageable pageable);
	
	public List<SocialComment> findByEntityIdAndCreationTimeBetween(String entityId, Long startTime, Long endTime, Pageable pageable);
	
	public List<SocialComment> findByEntityIdAndCreationTimeGreaterThan(String entityId, Long beginTime, Pageable pageable);
	
	public List<SocialComment> findByEntityIdAndCreationTimeLessThan(String entityId, Long endTime, Pageable pageable);
	
}
