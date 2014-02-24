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

import java.io.IOException;
import java.util.List;
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

import eu.trentorise.smartcampus.social.managers.PermissionManager;
import eu.trentorise.smartcampus.social.managers.SocialGroupManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.engine.beans.Group;

@Controller("groupController")
public class SocialGroupController extends RestController {
	
	@Autowired
	private SocialGroupManager groupManager;
	
	@Autowired
	private PermissionManager permissionManager;
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/group")
	public @ResponseBody List<Group> getUserGroups() throws SocialServiceException{
		String userId = getUserId();
		
		return groupManager.readGroups(userId, null);
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/group/{groupId}")
	public @ResponseBody
	Group getUserGroup(@PathVariable String groupId) throws SocialServiceException{
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException();
		}
		
		return groupManager.readGroup(groupId);
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/user/group")
	public @ResponseBody 
	Group createUserGroup(@RequestBody Group groupInRequest) throws SocialServiceException, IOException{
		String userId = getUserId();
		
		return groupManager.create(userId, groupInRequest.getName());
	}
	
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}")
	public @ResponseBody
	boolean deleteGroup(@PathVariable String groupId) throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException();
		}
		
		return groupManager.delete(groupId);
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}")
	public @ResponseBody
	Group updateGroup(@PathVariable("groupId") String groupId, @RequestBody Group groupInRequest) throws SocialServiceException {
		String userId = getUserId();
		
		if (!StringUtils.hasLength(groupId)) {
			throw new IllegalArgumentException("param 'groupId' should be valid");
		}
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException();
		}
		
		return groupManager.update(groupId, groupInRequest.getName());
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}/members")
	public @ResponseBody
	boolean addMembers(@PathVariable("groupId") String groupId, @RequestParam("userIds") Set<String> userIds) throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException();
		}
		
		return groupManager.addMembers(groupId, userIds);
	}
	
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}/members")
	public @ResponseBody
	boolean removeMembers(@PathVariable("groupId") String groupId, @RequestParam("userIds") Set<String> userIds) throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException();
		}
		
		return groupManager.removeMembers(groupId, userIds);
	}
	

}
