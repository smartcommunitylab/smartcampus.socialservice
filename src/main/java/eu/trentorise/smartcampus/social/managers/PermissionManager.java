package eu.trentorise.smartcampus.social.managers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

@Component
public class PermissionManager {

	@Autowired
	SocialGroupManager groupManager;
	
	Limit limit = null;
	
	private static final Logger logger = Logger.getLogger(PermissionManager.class);
	
	/**
	 * Method checkGroupPermission: used to check if a user is the creator and the owner of a group
	 * @param creatorId: id of the user to check
	 * @param groupId: id of the group
	 * @return true if the user is the group creator of this group.
	 */
	public boolean checkGroupPermission(String creatorId, String groupId){
		boolean checked = false;
		Group group = null;
		if (!StringUtils.hasLength(creatorId)
				|| !StringUtils.hasLength(groupId)) {
			logger.error(String.format("Error in checking permission on creator 'null' or groupId 'null'."));
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
}
