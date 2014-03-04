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
import java.util.HashSet;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
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
import eu.trentorise.smartcampus.social.engine.beans.Result;

@Controller("groupController")
public class SocialGroupController extends RestController {
	
	@Autowired
	private SocialGroupManager groupManager;
	
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
			@RequestParam(value = "sortList", required = false) Set<String> sortList)
			throws SocialServiceException {
		String userId = getUserId();
		
		Result result = null;
		try{
			result = new Result(groupManager.readGroups(userId, setLimit(pageNum, pageSize, fromDate, toDate, sortDirection, sortList)));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/group/{groupId}")
	public @ResponseBody
	Result getUserGroup(@PathVariable String groupId) throws SocialServiceException{
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException(String.format("User '%s' has not permission to read group '%s'", userId, groupId));
		}
		
		Result result = null;
		try{
			result = new Result(groupManager.readGroup(groupId));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/user/group")
	public @ResponseBody 
	Result createUserGroup(@RequestBody Group groupInRequest) throws SocialServiceException, IOException{
		String userId = getUserId();
		
		Result result = null;
		try{
			result = new Result(groupManager.create(userId, groupInRequest.getName()));
		}catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}")
	public @ResponseBody
	Result deleteGroup(@PathVariable String groupId) throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException(String.format("User '%s' has not permission to delete group '%s'", userId, groupId));
		}
		
		Result result = null;
		try{
			result = new Result(groupManager.delete(groupId));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}")
	public @ResponseBody
	Result updateGroup(@PathVariable("groupId") String groupId, @RequestBody Group groupInRequest) throws SocialServiceException {
		String userId = getUserId();
		
		if (!StringUtils.hasLength(groupId)) {
			throw new IllegalArgumentException("param 'groupId' should be valid");
		}
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException(String.format("User '%s' has not permission to update group '%s'", userId, groupId));
		}
		
		Result result = null;
		try{
			result = new Result(groupManager.update(groupId, groupInRequest.getName()));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	//------------------------------------------------------------------------------------------------------------------
	//| Used Only in tests. Added a specific line in resourceList.xml (line 26-27) REMOVE IT before final distribution |
	//------------------------------------------------------------------------------------------------------------------
	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}/test")
	public @ResponseBody
	Result updateGroupTest(@PathVariable("groupId") String groupId, @RequestParam Long updateTime) throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException(String.format("User '%s' has not permission to update group '%s'", userId, groupId));
		}
		
		Result result = null;
		try{
			result = new Result(groupManager.update(groupId, updateTime));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}	

	@SuppressWarnings("static-access")
	@RequestMapping(method = RequestMethod.GET, value = "/user/group/{groupId}/members")
	public @ResponseBody
	Result getMembers(@PathVariable("groupId") String groupId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList)
			throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException(String.format("User '%s' has not permission to getMenbers from group '%s'", userId, groupId));
		}
		
		Result result = null;
		Set<String> mySortList = new HashSet<String>();
		if(sortList == null || sortList.isEmpty()){
			mySortList.add(groupManager.getMembersort());
		} else {
			mySortList.addAll(sortList);
		}
		try{
			result = new Result(groupManager.readMembersAsString(groupId, setLimit(pageNum, pageSize, null, null, sortDirection, mySortList)));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/user/group/{groupId}/members")
	public @ResponseBody
	Result addMembers(@PathVariable("groupId") String groupId, @RequestParam("userIds") Set<String> userIds) throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException(String.format("User '%s' has not permission to addMenbers to group '%s'", userId, groupId));
		}
		
		Result result = null;
		try{
			result = new Result(groupManager.addMembers(groupId, userIds));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/group/{groupId}/members")
	public @ResponseBody
	Result removeMembers(@PathVariable("groupId") String groupId, @RequestParam("userIds") Set<String> userIds) throws SocialServiceException {
		String userId = getUserId();
		
		if(!permissionManager.checkGroupPermission(userId, groupId)){
			throw new SecurityException(String.format("User '%s' has not permission to removeMenbers from group '%s'", userId, groupId));
		}
		
		Result result = null;
		try{
			result = new Result(groupManager.removeMembers(groupId, userIds));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	

}
