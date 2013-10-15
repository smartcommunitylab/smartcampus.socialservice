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
package eu.trentorise.smartcampus.social.controllers.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.managers.GroupManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.model.Constants;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.social.model.Groups;
import eu.trentorise.smartcampus.social.model.User;

@Controller("groupController")
public class GroupController extends RestController {

	@Autowired
	private GroupManager groupManager;

	@RequestMapping(method = RequestMethod.GET, value = "/user/group")
	public @ResponseBody Groups getUserGroups() throws SocialServiceException, IOException {

		String userId = getUserId();
		User user = getUserObject(userId);

		return new Groups(groupManager.getGroups(user.getSocialId()));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/group/{groupId}")
	public @ResponseBody Group getUserGroup(@PathVariable String groupId) throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		if (!groupManager.checkPermission(user.getSocialId(), groupId)) {
			throw new SecurityException();
		}

		return groupManager.getGroup(groupId);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/group")
	public @ResponseBody
	Group createGroup(@RequestBody Group groupInRequest)
			throws SocialServiceException, IOException {

		String userId = getUserId();
		User user = getUserObject(userId);

		String id = groupManager.create(user.getSocialId(), groupInRequest.getName());
		if (id != null) {
			groupInRequest.setSocialId(id);
			return groupInRequest;
		} else {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}")
	public @ResponseBody
	boolean deleteGroup(@PathVariable String groupId)
			throws SocialServiceException, IOException {

		String userId = getUserId();
		User user = getUserObject(userId);

		if (!groupManager.checkPermission(user.getSocialId(), groupId)) {
			throw new SecurityException();
		}

		return groupManager.delete(groupId);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}")
	public @ResponseBody
	boolean updateGroup(@PathVariable("groupId") String groupId, @RequestBody Group groupInRequest)
			throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		if (!groupManager.checkPermission(user.getSocialId(), groupId)) {
			throw new SecurityException();
		}
		// socialId is mandatory
		groupInRequest.setSocialId(groupId);

		return groupManager.update(groupInRequest);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}/members")
	public @ResponseBody
	boolean addUser(@PathVariable String groupId, @RequestParam("userIds") List<String> userIds)
			throws SocialServiceException, IOException {

		String userId = getUserId();
		User user = getUserObject(userId);

		if (!groupManager.checkPermission(user.getSocialId(), groupId)) {
			throw new SecurityException();
		}

		return groupManager.addUser(user.getSocialId(), groupId, userIds);

	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}/members")
	public @ResponseBody
	boolean removeUser(@PathVariable String groupId, @RequestParam("userIds") List<String> userIds) throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		if (!groupId.equals(Constants.MY_PEOPLE_GROUP_ID) && !groupManager.checkPermission(user.getSocialId(), groupId)) {
			throw new SecurityException();
		}

		return groupManager.removeUser(user.getSocialId(), userIds, groupId);
	}

}
