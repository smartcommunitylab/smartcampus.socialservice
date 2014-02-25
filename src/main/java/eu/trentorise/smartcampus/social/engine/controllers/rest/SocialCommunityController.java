package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.engine.beans.Community;
import eu.trentorise.smartcampus.social.managers.PermissionManager;
import eu.trentorise.smartcampus.social.managers.SocialCommunityManager;

@Controller
public class SocialCommunityController extends RestController {

	@Autowired
	PermissionManager permissionManager;

	@Autowired
	SocialCommunityManager communityManager;

	@RequestMapping(method = RequestMethod.GET, value = "/community")
	public @ResponseBody
	List<Community> readCommunities() {
		return communityManager.readCommunities(null);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/{communityId}")
	public @ResponseBody
	Community readCommunity(@PathVariable String communityId) {
		return communityManager.readCommunity(communityId);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/app/{appId}/community")
	public @ResponseBody
	Community createCommunity(@RequestBody Community community,
			@PathVariable String appId) {
		return communityManager.create(community.getName(), appId);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/app/{appId}/community/{communityId}")
	public @ResponseBody
	boolean deleteCommunity(@PathVariable String appId,
			@PathVariable String communityId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}

		return communityManager.delete(communityId);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/community/{communityId}/members")
	public @ResponseBody
	boolean subscribe(@PathVariable String communityId,
			@RequestParam Set<String> userIds) {
		return communityManager.addMembers(communityId, userIds);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/community/{communityId}/members")
	public @ResponseBody
	boolean unsubscribe(@PathVariable String communityId,
			@RequestParam Set<String> userIds) {
		return communityManager.removeMembers(communityId, userIds);
	}
}
