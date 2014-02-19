package eu.trentorise.smartcampus.social.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import eu.trentorise.smartcampus.social.engine.UserOperations;
import eu.trentorise.smartcampus.social.engine.beans.User;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;
import eu.trentorise.smartcampus.social.engine.repo.UserRepository;

@Component
@Transactional
public class SocialUserManager implements UserOperations {

	@Autowired
	UserRepository userRepository;

	@Override
	public User create(String userId) {
		// load user
		return createSocial(userId).toUser();
	}

	@Override
	public SocialUser createSocial(String userId) {
		// load user
		SocialUser user = userRepository.findOne(userId);
		if (user == null) {
			// add user in local db
			user = new SocialUser(userId);
			userRepository.save(user);
		}
		return user;
	}

	@Override
	public User readUser(String userId) {
		return readSocialUser(userId).toUser();
	}

	@Override
	public SocialUser readSocialUser(String userId) {
		return userRepository.findOne(userId);
	}

	public SocialUser defineSocialUser(String userId) {
		SocialUser user = readSocialUser(userId);
		return user != null ? user : new SocialUser(userId);
	}

}
