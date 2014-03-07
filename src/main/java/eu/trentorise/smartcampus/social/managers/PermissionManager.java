package eu.trentorise.smartcampus.social.managers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.model.SocialComment;
import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;
import eu.trentorise.smartcampus.social.engine.repo.mongo.CommentRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
public class PermissionManager {

	@Autowired
	SocialGroupManager groupManager;

	@Autowired
	CommunityRepository communityRepo;

	@Autowired
	EntityRepository entityRepo;
	
	@Autowired
	CommentRepository commentRepo;

	Limit limit = null;

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
					|| entity.getCommunityOwner().getId().toString()
							.equals(ownerId);
		} else {
			return entity == null || entity.getOwner().getId().equals(ownerId);
		}
	}
	
	public boolean checkCommentPermission(String commentId, String author){
		try{
			SocialComment comment = commentRepo.findById(commentId);
			return (comment != null) && (comment.getAuthor().compareToIgnoreCase(author) == 0);
		} catch (Exception ex){
			logger.error(String.format("Error in checking permission for comment '%s'", commentId));
			return false;
		}
	}
}
