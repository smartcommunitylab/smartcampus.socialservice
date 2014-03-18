package eu.trentorise.smartcampus.social.engine;

import eu.trentorise.smartcampus.social.engine.beans.Rating;

public interface RatingOperations {
	public boolean delete(String entityURI, String userId);

	public boolean rate(String entityURI, String userId, double rate)
			throws IllegalArgumentException;

	public Rating getRating(String userId, String entityURI);
}
