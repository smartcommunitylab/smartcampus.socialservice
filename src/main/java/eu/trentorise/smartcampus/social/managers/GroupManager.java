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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.trentorise.smartcampus.social.converters.SocialEngineConverter;
import eu.trentorise.smartcampus.social.model.Constants;
import eu.trentorise.smartcampus.social.model.Group;

/**
 * <i>GroupManager</i> manages the group functionalities of social network
 * 
 * @author mirko perillo
 * 
 */
// @Component
public class GroupManager extends SocialEngineConnector {

	public GroupManager() throws IOException {
		super();
	}

	private static final Logger logger = Logger.getLogger(GroupManager.class);

	@Autowired
	private SocialEngineConverter socialConverter;

	@Autowired
	private UserManager userManager;

	/**
	 * returns the list of groups relative given list of ids
	 * 
	 * @param groupIds
	 *            list of group ids
	 * @return the list of group entities from given ids
	 * @throws SocialServiceException
	 */
	public List<Group> getGroups(List<Long> groupIds)
			throws SocialServiceException {
		try {
			return socialConverter.toGroup(socialEngineClient
					.readUserGroups(groupIds));
		} catch (Exception e) {
			logger.error("Exception retrieving social engine groups", e);
			throw new SocialServiceException();
		}
	}

	/**
	 * returns the default group for given user
	 * 
	 * @param socialUserId
	 *            social id of the user
	 * @return default group containing all known users by given user
	 * @throws SocialServiceException
	 */
	public Group getDefaultGroup(long socialUserId)
			throws SocialServiceException {
		try {
			it.unitn.disi.sweb.webapi.model.smartcampus.social.User user = socialEngineClient
					.readUser(socialUserId);
			user.getKnownUserIds();
			Group defaultGroup = new Group();
			defaultGroup.setName(Constants.MY_PEOPLE_GROUP_NAME);
			defaultGroup.setSocialId(Constants.MY_PEOPLE_GROUP_ID);
			defaultGroup.setUsers(userManager.getUsers(user.getKnownUserIds()));
			return defaultGroup;
		} catch (Exception e) {
			logger.error("Exception getting default group for user "
					+ socialUserId);
			throw new SocialServiceException();
		}
	}

	/**
	 * returns groups created by the user
	 * 
	 * @param socialUserIdStr
	 *            social id of the user
	 * @return the list of user groups
	 * @throws SocialServiceException
	 */
	public List<Group> getGroups(String socialUserIdStr)
			throws SocialServiceException {
		Long socialUserId = Long.parseLong(socialUserIdStr);
		List<Group> groups = new ArrayList<Group>();
		// add default group
		Group defaultGroup = getDefaultGroup(socialUserId);
		if (defaultGroup != null) {
			groups.add(defaultGroup);
		}
		try {
			it.unitn.disi.sweb.webapi.model.smartcampus.social.User user = socialEngineClient
					.readUser(socialUserId);
			Set<Long> groupIds = user.getUserGroupIds();
			for (Long groupId : groupIds) {
				groups.add(socialConverter.toGroup(socialEngineClient
						.readUserGroup(groupId)));
			}

		} catch (Exception e) {
			logger.error("Exception retrieving social engine groups", e);
			throw new SocialServiceException();
		}
		return groups;
	}

	/**
	 * returns the group by given id
	 * 
	 * @param groupIdStr
	 *            id of the group
	 * @return the group or null if it doesn't exist
	 * @throws SocialServiceException
	 */
	public Group getGroup(String groupIdStr) throws SocialServiceException {
		try {
			Long groupId = Long.parseLong(groupIdStr);
			return socialConverter.toGroup(socialEngineClient
					.readUserGroup(groupId));
		} catch (Exception e) {
			logger.error("Exception during delete sociale engine group "
					+ groupIdStr, e);
			throw new SocialServiceException();
		}
	}

	/**
	 * deletes a group
	 * 
	 * @param groupIdStr
	 *            id of group to delete
	 * @return true if operation gone fine, false otherwise
	 * @throws SocialServiceException
	 */
	public boolean delete(String groupIdStr) throws SocialServiceException {
		boolean removed = false;
		try {
			Long groupId = Long.parseLong(groupIdStr);
			removed = socialEngineClient.deleteUserGroup(groupId);
		} catch (WebApiException e) {
			logger.error(
					"Exception deleting social engine group " + groupIdStr, e);
			throw new SocialServiceException();
		}
		return removed;
	}

	/**
	 * create a group for given user
	 * 
	 * @param socialUserIdStr
	 *            social id of the user owner of the group
	 * @param groupName
	 *            name of the group
	 * @return the id of created group
	 * @throws SocialServiceException
	 */
	public String create(String socialUserIdStr, String groupName)
			throws SocialServiceException {
		UserGroup group = new UserGroup();
		group.setName(groupName);
		Long socialUserId = Long.parseLong(socialUserIdStr);
		group.setOwnerId(socialUserId);
		group.setUserIds(new HashSet<Long>());
		Long groupId = -1l;
		try {
			groupId = socialEngineClient.create(group);
			logger.debug("Group created, id " + groupId);
		} catch (Exception e) {
			logger.error("Exception during creation of social engine group "
					+ groupName, e);
			throw new SocialServiceException();
		}

		return groupId.toString();
	}

	/**
	 * updates of a group
	 * 
	 * @param group
	 *            group of update
	 * @return true if operation gone fine, false otherwise
	 * @throws SocialServiceException
	 */
	public boolean update(Group group) throws SocialServiceException {
		try {
			Long groupId = Long.parseLong(group.getSocialId());
			UserGroup usergroup = socialEngineClient.readUserGroup(groupId);
			usergroup.setName(group.getName());
			socialEngineClient.update(usergroup);
			return true;
		} catch (Exception e) {
			logger.error("exception updating group", e);
			throw new SocialServiceException();
		}

	}

	/**
	 * adds a user to a group
	 * 
	 * @param ownerIdStr
	 *            social id of the owner of group
	 * @param groupIdStr
	 *            id of the group
	 * @param userIds
	 *            list of social user ids to add to group
	 * @return true if operation gone fine, false otherwise
	 * @throws SocialServiceException
	 */
	public boolean addUser(String ownerIdStr, String groupIdStr,
			List<String> userIds) throws SocialServiceException {
		try {
			Long groupId = Long.parseLong(groupIdStr);
			UserGroup group = socialEngineClient.readUserGroup(groupId);
			Long ownerId = Long.parseLong(ownerIdStr);
			it.unitn.disi.sweb.webapi.model.smartcampus.social.User owner = socialEngineClient
					.readUser(ownerId);
			for (String uidStr : userIds) {
				Long uid = Long.parseLong(uidStr);
				group.getUserIds().add(uid);
				if (!owner.getKnownUserIds().contains(uid)) {
					owner.getKnownUserIds().add(uid);
				}
			}

			socialEngineClient.update(group);
			socialEngineClient.update(owner);
			return true;
		} catch (Exception e) {
			logger.error("exception add users to group " + groupIdStr, e);
			throw new SocialServiceException();
		}
	}

	/**
	 * removes a list of users from a group
	 * 
	 * @param ownerIdStr
	 *            social id of the owner of group
	 * @param userIds
	 *            list of social user ids to remove from the group
	 * @param groupIdStr
	 *            id of the group
	 * @return true if operation gone fine, false otherwise
	 * @throws SocialServiceException
	 */
	public boolean removeUser(String ownerIdStr, List<String> userIds,
			String groupIdStr) throws SocialServiceException {
		boolean success = false;
		try {
			Long groupId = Long.parseLong(groupIdStr);
			if (Constants.MY_PEOPLE_GROUP_ID.equals(groupIdStr)) {

				Long ownerId = Long.parseLong(ownerIdStr);
				it.unitn.disi.sweb.webapi.model.smartcampus.social.User owner = socialEngineClient
						.readUser(ownerId);
				if (owner.getKnownUserIds() != null) {
					for (String userId : userIds) {
						owner.getKnownUserIds().remove(Long.parseLong(userId));
					}
					socialEngineClient.update(owner);
				}
				owner = socialEngineClient.readUser(ownerId);
			} else {

				UserGroup group = socialEngineClient.readUserGroup(groupId);
				Set<Long> set = new HashSet<Long>();
				if (userIds != null)
					for (String s : userIds)
						set.add(Long.parseLong(s));
				if (group != null && group.getUserIds() != null
						&& (success = group.getUserIds().removeAll(set))) {
					socialEngineClient.update(group);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception removing users from group " + groupIdStr, e);
			throw new SocialServiceException();
		}
		return success;
	}

	/**
	 * checks permission constraints
	 * 
	 * @param userId
	 *            social id of user to checks
	 * @param groupId
	 *            id of group that user wants to manage
	 * @return true if user can manage the group, false otherwise
	 * @throws SocialServiceException
	 */

	public boolean checkPermission(String userId, String groupId)
			throws SocialServiceException {
		it.unitn.disi.sweb.webapi.model.smartcampus.social.User user;
		try {
			user = socialEngineClient.readUser(Long.parseLong(userId));
			return user.getUserGroupIds().contains(Long.parseLong(groupId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new SocialServiceException();
		}

	}
}
