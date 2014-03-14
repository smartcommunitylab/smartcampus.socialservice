package eu.trentorise.smartcampus.social.engine.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialRating;

public interface RatingRepository extends CrudRepository<SocialRating, Long> {

	@Query("select r from SocialRating r where user.id=?1 AND entity.id=?2")
	public SocialRating findByUserAndEntity(String userId, String entityURI);

	@Query("select SUM(r.rating) from SocialRating r WHERE r.entity.id=?1 GROUP BY r.entity.id")
	public double findRatingByEntity(String entityURI);
}
