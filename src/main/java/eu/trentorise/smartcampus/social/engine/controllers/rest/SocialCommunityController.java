package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.util.HashSet;
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
	List<Community> readCommunities(
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate) {
		return communityManager.readCommunities(setLimit(pageNum, pageSize,
				fromDate, toDate));
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

	@RequestMapping(method = RequestMethod.PUT, value = "/user/community/{communityId}/member")
	public @ResponseBody
	boolean subscribeUser(@PathVariable String communityId) {

		Set<String> members = new HashSet<String>();
		members.add(getUserId());
		return communityManager.addMembers(communityId, members);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/community/{communityId}/member")
	public @ResponseBody
	boolean unsubscribeUser(@PathVariable String communityId) {

		Set<String> members = new HashSet<String>();
		members.add(getUserId());
		return communityManager.removeMembers(communityId, members);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/app/{appId}/community/{communityId}/members")
	public @ResponseBody
	boolean subscribe(@PathVariable String appId,
			@PathVariable String communityId, @RequestParam Set<String> userIds) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}

		return communityManager.addMembers(communityId, userIds);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/app/{appId}/community/{communityId}/members")
	public @ResponseBody
	boolean unsubscribe(@PathVariable String appId,
			@PathVariable String communityId, @RequestParam Set<String> userIds) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}

		return communityManager.removeMembers(communityId, userIds);
	}
}
