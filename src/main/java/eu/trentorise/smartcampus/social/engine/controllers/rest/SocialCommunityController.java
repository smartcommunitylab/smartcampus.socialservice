package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.util.HashSet;
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
import eu.trentorise.smartcampus.social.engine.beans.Result;
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
	Result readCommunities(
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {
		return new Result(communityManager.readCommunities(setLimit(pageNum,
				pageSize, fromDate, toDate, sortDirection, sortList)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/{communityId}")
	public @ResponseBody
	Result readCommunity(@PathVariable String communityId) {
		return new Result(communityManager.readCommunity(communityId));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/app/{appId}/community")
	public @ResponseBody
	Result createCommunity(@RequestBody Community community,
			@PathVariable String appId) {
		return new Result(communityManager.create(community.getName(), appId));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/app/{appId}/community/{communityId}")
	public @ResponseBody
	Result deleteCommunity(@PathVariable String appId,
			@PathVariable String communityId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}

		return new Result(communityManager.delete(communityId));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/community/{communityId}/member")
	public @ResponseBody
	Result subscribeUser(@PathVariable String communityId) {

		Set<String> members = new HashSet<String>();
		members.add(getUserId());
		return new Result(communityManager.addMembers(communityId, members));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/community/{communityId}/member")
	public @ResponseBody
	Result unsubscribeUser(@PathVariable String communityId) {

		Set<String> members = new HashSet<String>();
		members.add(getUserId());
		return new Result(communityManager.removeMembers(communityId, members));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/app/{appId}/community/{communityId}/members")
	public @ResponseBody
	Result subscribe(@PathVariable String appId,
			@PathVariable String communityId, @RequestParam Set<String> userIds) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}

		return new Result(communityManager.addMembers(communityId, userIds));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/app/{appId}/community/{communityId}/members")
	public @ResponseBody
	Result unsubscribe(@PathVariable String appId,
			@PathVariable String communityId, @RequestParam Set<String> userIds) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}

		return new Result(communityManager.removeMembers(communityId, userIds));
	}
}
