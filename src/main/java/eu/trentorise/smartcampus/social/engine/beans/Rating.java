package eu.trentorise.smartcampus.social.engine.beans;

public class Rating {
	private String userId;
	private String entityURI;
	private double rating;

	public Rating() {

	}

	public Rating(String userId, String entityURI, double rating) {
		this.entityURI = entityURI;
		this.rating = rating;
		this.userId = userId;
	}

	public Rating(String entityURI, double rating) {
		this.entityURI = entityURI;
		this.rating = rating;
	}

	public String getEntityURI() {
		return entityURI;
	}

	public void setEntityURI(String entityURI) {
		this.entityURI = entityURI;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
