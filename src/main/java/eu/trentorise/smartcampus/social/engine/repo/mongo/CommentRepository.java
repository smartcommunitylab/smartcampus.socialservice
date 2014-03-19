package eu.trentorise.smartcampus.social.engine.repo.mongo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialComment;

public interface CommentRepository extends
		MongoRepository<SocialComment, String> {

	public SocialComment findById(String id);

	public SocialComment findByAuthorIgnoreCaseAndEntityURI(String author,
			String entityURI);

	public SocialComment findByAuthorIgnoreCaseAndTextIgnoreCaseAndEntityURI(
			String author, String text, String entityURI);

	public List<SocialComment> findByEntityURI(String entityURI,
			Pageable pageable);

	public List<SocialComment> findByEntityURIAndCreationTimeBetween(
			String entityURI, Long startTime, Long endTime, Pageable pageable);

	public List<SocialComment> findByEntityURIAndCreationTimeGreaterThan(
			String entityURI, Long beginTime, Pageable pageable);

	public List<SocialComment> findByEntityURIAndCreationTimeLessThan(
			String entityURI, Long endTime, Pageable pageable);

}
