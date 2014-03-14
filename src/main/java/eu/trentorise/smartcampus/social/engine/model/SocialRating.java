package eu.trentorise.smartcampus.social.engine.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import eu.trentorise.smartcampus.social.engine.beans.Rating;

@Entity
public class SocialRating implements Serializable {

	private static final long serialVersionUID = -9219629387109493261L;

	@Id
	@GeneratedValue
	Long id;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private SocialUser user;

	@OneToOne(fetch = FetchType.LAZY)
	private SocialEntity entity;

	private double rating;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SocialUser getUser() {
		return user;
	}

	public void setUser(SocialUser user) {
		this.user = user;
	}

	public SocialEntity getEntity() {
		return entity;
	}

	public void setEntity(SocialEntity entity) {
		this.entity = entity;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public Rating toRating() {
		return new Rating(user.getId(), entity.getUri(), rating);

	}
}
