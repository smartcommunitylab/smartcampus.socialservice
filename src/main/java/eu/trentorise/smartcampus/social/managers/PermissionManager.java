/**
 *    Copyright 2012-2013 Trento RISE
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
 */

package eu.trentorise.smartcampus.social.managers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.model.SocialComment;
import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialRating;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;
import eu.trentorise.smartcampus.social.engine.repo.RatingRepository;
import eu.trentorise.smartcampus.social.engine.repo.mongo.CommentRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
public class PermissionManager {

	@Autowired
	GroupManager groupManager;

	@Autowired
	EntityManager entityManager;

	@Autowired
	CommunityRepository communityRepo;

	@Autowired
	EntityRepository entityRepo;

	@Autowired
	CommentRepository commentRepo;

	@Autowired
	RatingRepository ratingRepo;

	private static final Logger logger = Logger
			.getLogger(PermissionManager.class);

	/**
	 * Method checkGroupPermission: used to check if a user is the creator and
	 * the owner of a group
	 * 
	 * @param creatorId
	 *            : id of the user to check
	 * @param groupId
	 *            : id of the group
	 * @return true if the user is the group creator of this group.
	 */
	public boolean checkGroupPermission(String creatorId, String groupId) {
		boolean checked = false;
		Group group = null;
		if (!StringUtils.hasLength(creatorId)
				|| !StringUtils.hasLength(groupId)) {
			logger.error(String
					.format("Error in checking permission on creator 'null' or groupId 'null'."));
			return checked;
		}
		group = groupManager.readGroup(groupId);
		if (group == null) {
			return true;
		}
		if (group.getCreatorId().compareTo(creatorId) == 0) {
			checked = true;
		}
		return checked;
	}

	public boolean checkCommunityPermission(String appId, String communityId) {
		try {
			SocialCommunity community = communityRepo.findOne(RepositoryUtils
					.convertId(communityId));
			return community != null && community.getAppId().equals(appId);
		} catch (Exception e) {
			return false;
		}
	}

	public boolean checkEntityPermission(String ownerId, String uri,
			boolean isCommunity) {
		if (ownerId == null || uri == null) {
			return false;
		}
		SocialEntity entity = entityRepo.findOne(uri);
		if (isCommunity) {
			return entity == null
					|| (entity.getCommunitiesSharedWith() != null
							&& entity.getCommunityOwner() != null && entity
							.getCommunityOwner().getId().toString()
							.equals(ownerId));
		} else {
			return entity == null
					|| (entity.getOwner() != null && entity.getOwner().getId()
							.equals(ownerId));
		}
	}

	public boolean checkCommentPermission(String commentId, String author) {
		try {
			SocialComment comment = commentRepo.findById(commentId);
			if (comment == null) {
				return true; // if the comment not exist the permission check
								// return true to manage correctly the effective
								// result
			}
			return (comment != null)
					&& (comment.getAuthor().compareToIgnoreCase(author) == 0);
		} catch (Exception ex) {
			logger.error(String.format(
					"Error in checking permission for comment '%s'", commentId));
			return false;
		}
	}

	public boolean checkSharingPermission(String uri, String userId) {
		if (uri == null || userId == null) {
			return false;
		}
		return entityManager.readShared(userId, false, uri) != null;
	}

	public boolean checkRatingPermission(String uri, String userId) {
		if (uri == null || userId == null) {
			return false;
		}
		SocialRating rating = ratingRepo.findByUserAndEntity(userId, uri);
		return rating != null;
	}
}
