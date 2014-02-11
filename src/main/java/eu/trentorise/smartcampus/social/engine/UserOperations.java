package eu.trentorise.smartcampus.social.engine;

import java.util.List;

import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.User;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;

public interface UserOperations {
	
	// creation
	public User create(String userId);
	
	public SocialUser createSocial(String userId);
	
	// read
	public User readUser(String userId);
	
	public SocialUser readSocialUser(String userId);

}
