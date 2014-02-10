package eu.trentorise.smartcampus.social.engine.utils;

import eu.trentorise.smartcampus.social.engine.model.SocialUser;

public class RepositoryUtils {

	public static String convertId(Long id) {
		return id == null ? null : id.toString();
	}

	public static Long convertId(String id) throws IllegalArgumentException {
		try {
			return Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(id + " not valid");
		}
	}

	public static String convertUser(SocialUser user) {
		return user != null ? user.getId() : null;
	}

}
