package eu.trentorise.smartcampus.social.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

@Component
public class PermissionManager {

	@Autowired
	SocialGroupManager groupManager;
	
	Limit limit = null;
	
	/**
	 * Method checkGroupPermission: used to check if a user is the creator and the owner of a group
	 * @param creatorId: id of the user to check
	 * @param groupId: id of the group
	 * @return true if the user is the group creator of this group.
	 */
	public boolean checkGroupPermission(String creatorId, String groupId){
		boolean checked = false;
		Group group = null;
			group = groupManager.readGroup(groupId);
			if(group == null){
				return true;
			}
			if(group.getCreatorId().compareTo(creatorId) == 0){
				checked = true;
			}
		return checked;
	}
}
