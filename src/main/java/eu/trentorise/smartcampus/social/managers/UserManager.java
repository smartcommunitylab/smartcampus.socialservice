/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.social.managers;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.model.entity.EntityBase;
import it.unitn.disi.sweb.webapi.model.smartcampus.social.SocialActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.exceptions.SmartCampusException;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;
import eu.trentorise.smartcampus.social.SocialEngineConnector;
import eu.trentorise.smartcampus.social.model.User;

/**
 * <i>UserManager</i> manages the profile informations of all the user in the
 * system
 * 
 * @author mirko perillo
 * 
 */
@Component
public class UserManager extends SocialEngineConnector {

	private static final Logger logger = Logger.getLogger(UserManager.class);

	@Autowired AuthServices authServices;

	@Autowired
	private CommunityManager communityManager;

	private Map<Long, Long> entityBaseUserMap = new HashMap<Long, Long>();

	@PostConstruct
	protected void init() throws SmartCampusException {
		super.init();
		try {
			List<it.unitn.disi.sweb.webapi.model.smartcampus.social.User> users = socialEngineClient
					.readUsers();
			if (users != null) {
				for (it.unitn.disi.sweb.webapi.model.smartcampus.social.User user : users) {
					entityBaseUserMap.put(user.getEntityBaseId(), user.getId());
				}
			}
		} catch (WebApiException e) {
			logger.error("Failed to initialize user entityBase cache:"
					+ e.getMessage());
			throw new SmartCampusException(e);
		}
	}


	/**
	 * returns the {@link eu.trentorise.smartcampus.social.model.User} of the users specified with social IDs
	 * 
	 * @param socialUserIds
	 *            list of social user ids
	 * @return the list of minimal profiles
	 * @throws SocialServiceException
	 */
	public List<eu.trentorise.smartcampus.social.model.User> getUsers(Set<Long> socialUserIds)
			throws SocialServiceException {
		List<eu.trentorise.smartcampus.social.model.User> profiles = new ArrayList<eu.trentorise.smartcampus.social.model.User>();
		try {
			for (Long temp : socialUserIds) {
				eu.trentorise.smartcampus.social.model.User profile = getUserBySocialId(temp);
				if (profile != null) {
					profiles.add(profile);
				}
			}
		} catch (Exception e) {
			logger.error("Exception getting user profiles " + socialUserIds);
			throw new SocialServiceException();
		}
		return profiles;
	}

	/**
	 * returns the {@link User} instance of a user given its social id
	 * 
	 * @param socialId
	 *            social id of the user
	 * @return MinimalProfile of user or null if it doesn't exist
	 * @throws SocialServiceException
	 */
	public eu.trentorise.smartcampus.social.model.User getUserBySocialId(Long socialId)
			throws SocialServiceException {
		try {
			return authServices.loadUserBySocialId(socialId.toString());
		} catch (Exception e) {
			throw new SocialServiceException(e);
		}
	}

	/**
	 * returns the MinimalProfile of a user given its social id
	 * 
	 * @param eb
	 *            entity base of user
	 * @return MinimalProfile of user or null if it doesn't exist
	 * @throws SocialServiceException
	 */
	public eu.trentorise.smartcampus.social.model.User getUserByEntityBase(EntityBase eb)
			throws SocialServiceException {
		Long socialId = entityBaseUserMap.get(eb.getId());
		if (socialId == null) {
			try {
				SocialActor actor = socialEngineClient.readActorByEntityBase(eb
						.getId());
				if (actor != null) {
					socialId = actor.getId();
					entityBaseUserMap.put(eb.getId(), socialId);
				}
			} catch (Exception e) {
				logger.error("Failed to read user for entity base "
						+ eb.getLabel() + ":" + e.getMessage());
			}
		}
		if (socialId == null) {
			return null;
		}
		return getUserBySocialId(socialId);
	}

}
