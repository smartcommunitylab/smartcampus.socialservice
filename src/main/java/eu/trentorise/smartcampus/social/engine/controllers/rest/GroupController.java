/*******************************************************************************
 * Copyright 2012-2014 Trento RISE
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
package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.engine.beans.Group;
import eu.trentorise.smartcampus.social.engine.beans.Result;
import eu.trentorise.smartcampus.social.managers.GroupManager;
import eu.trentorise.smartcampus.social.managers.PermissionManager;

@Controller("groupController")
public class GroupController extends RestController {

	@Autowired
	private GroupManager groupManager;

	@Autowired
	private PermissionManager permissionManager;

	@RequestMapping(method = RequestMethod.GET, value = "/user/group")
	public @ResponseBody
	Result getUserGroups(
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {
		String userId = getUserId();

		Result result = new Result(groupManager.readGroups(
				userId,
				setLimit(pageNum, pageSize, fromDate, toDate, sortDirection,
						sortList)));

		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/group/{groupId}")
	public @ResponseBody
	Result getUserGroup(@PathVariable String groupId) {
		String userId = getUserId();

		if (!permissionManager.checkGroupPermission(userId, groupId)) {
			throw new SecurityException(String.format(
					"User '%s' has not permission to read group '%s'", userId,
					groupId));
		}

		Result result = new Result(groupManager.readGroup(groupId));
		return result;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/group")
	public @ResponseBody
	Result createUserGroup(@RequestBody Group groupInRequest) {
		String userId = getUserId();

		Result result = null;
		result = new Result(groupManager.create(userId,
				groupInRequest.getName()));

		return result;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}")
	public @ResponseBody
	Result deleteGroup(@PathVariable String groupId) {
		String userId = getUserId();

		if (!permissionManager.checkGroupPermission(userId, groupId)) {
			throw new SecurityException(String.format(
					"User '%s' has not permission to delete group '%s'",
					userId, groupId));
		}

		Result result = new Result(groupManager.delete(groupId));

		return result;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}")
	public @ResponseBody
	Result updateGroup(@PathVariable("groupId") String groupId,
			@RequestBody Group groupInRequest) {
		String userId = getUserId();

		if (!StringUtils.hasLength(groupId)) {
			throw new IllegalArgumentException(
					"param 'groupId' should be valid");
		}

		if (!permissionManager.checkGroupPermission(userId, groupId)) {
			throw new SecurityException(String.format(
					"User '%s' has not permission to update group '%s'",
					userId, groupId));
		}

		Result result = new Result(groupManager.update(groupId,
				groupInRequest.getName()));

		return result;
	}

	@SuppressWarnings("static-access")
	@RequestMapping(method = RequestMethod.GET, value = "/user/group/{groupId}/members")
	public @ResponseBody
	Result getMembers(
			@PathVariable("groupId") String groupId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {
		String userId = getUserId();

		if (!permissionManager.checkGroupPermission(userId, groupId)) {
			throw new SecurityException(
					String.format(
							"User '%s' has not permission to getMenbers from group '%s'",
							userId, groupId));
		}

		// sort the list
		Set<String> mySortList = null;
		if (sortList != null) {
			if (sortList.size() != 1) {
				throw new IllegalArgumentException(
						String.format(
								"To many arguments passed in group member sorting. Use '%s' instead",
								groupManager.getMembersort()));
			}
			for (String param : sortList) {
				if (param.compareTo(groupManager.getMembersort()) != 0) {
					throw new IllegalArgumentException(
							String.format(
									"Parameter '%s' not exist for group member. Use '%s' instead",
									param, groupManager.getMembersort()));
				}
			}
			mySortList = new HashSet<String>();
		} else {
			mySortList = new HashSet<String>();
			sortDirection = new Integer(0);
		}
		Result result = new Result(groupManager.readMembersAsString(
				groupId,
				setLimit(pageNum, pageSize, null, null, sortDirection,
						mySortList)));

		return result;
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}/members")
	public @ResponseBody
	Result addMembers(@PathVariable("groupId") String groupId,
			@RequestParam("userIds") Set<String> userIds) {
		String userId = getUserId();

		if (!permissionManager.checkGroupPermission(userId, groupId)) {
			throw new SecurityException(String.format(
					"User '%s' has not permission to addMenbers to group '%s'",
					userId, groupId));
		}

		Result result = new Result(groupManager.addMembers(groupId, userIds));

		return result;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}/members")
	public @ResponseBody
	Result removeMembers(@PathVariable("groupId") String groupId,
			@RequestParam("userIds") Set<String> userIds) {
		String userId = getUserId();

		if (!permissionManager.checkGroupPermission(userId, groupId)) {
			throw new SecurityException(
					String.format(
							"User '%s' has not permission to removeMenbers from group '%s'",
							userId, groupId));
		}

		Result result = new Result(groupManager.removeMembers(groupId, userIds));

		return result;
	}

}
