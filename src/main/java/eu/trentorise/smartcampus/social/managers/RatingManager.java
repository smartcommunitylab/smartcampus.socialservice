package eu.trentorise.smartcampus.social.managers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.social.engine.RatingOperations;
import eu.trentorise.smartcampus.social.engine.beans.Rating;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialRating;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;
import eu.trentorise.smartcampus.social.engine.repo.RatingRepository;

@Component
@Transactional
public class RatingManager implements RatingOperations {
	private static final Logger logger = Logger.getLogger(RatingManager.class);

	@Autowired
	EntityRepository entityRepo;

	@Autowired
	RatingRepository ratingRepo;

	@Autowired
	UserManager userManager;

	@Override
	public boolean delete(String entityURI, String userId) {
		SocialRating rating = ratingRepo.findByUserAndEntity(userId, entityURI);
		if (rating != null) {
			ratingRepo.delete(rating);
			SocialEntity entity = entityRepo.findOne(entityURI);
			if (entity != null) {
				entity.setTotalVoters(entity.getTotalVoters() - 1);
				try {
					entity.setRating(ratingRepo.findRatingByEntity(entityURI)
							/ entity.getTotalVoters());
				} catch (NullPointerException e) { // exception thrown when no
													// rating exist for entity
					entity.setRating(0);
				}
				entityRepo.save(entity);
			} else {
				logger.warn(String.format("Entity %s not exist", entityURI));
			}
		} else {
			logger.warn(String.format(
					"Rating for entity %s of user %s not exist", entityURI,
					userId));
		}
		return true;
	}

	@Override
	public boolean rate(String entityURI, String userId, double rate)
			throws IllegalArgumentException {
		// check if rate between 0 and 5
		if (rate < 0 && rate > 5) {
			throw new IllegalArgumentException(
					"Rate value MUST be between 0 and 5");
		}
		SocialEntity entity = entityRepo.findOne(entityURI);
		if (entity == null) {
			logger.warn(String.format("Entity %s not exist", entityURI));
			return true;
		}
		SocialRating rating = ratingRepo.findByUserAndEntity(userId, entityURI);
		boolean newRate = false;
		if (rating == null) {
			newRate = true;
			rating = new SocialRating();
			rating.setUser(userManager.defineSocialUser(userId));
			rating.setEntity(entity);
		}
		rating.setRating(rate);
		ratingRepo.save(rating);

		if (newRate) {
			entity.setTotalVoters(entity.getTotalVoters() + 1);
		}
		double mediumRating = ratingRepo.findRatingByEntity(entityURI)
				/ entity.getTotalVoters();
		entity.setRating(mediumRating);
		entityRepo.save(entity);
		return true;
	}

	@Override
	public Rating getRating(String userId, String entityURI) {
		SocialRating rating = ratingRepo.findByUserAndEntity(userId, entityURI);
		return rating == null ? null : rating.toRating();
	}
}
