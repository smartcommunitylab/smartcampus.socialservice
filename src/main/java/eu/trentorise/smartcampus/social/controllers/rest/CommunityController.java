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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.managers.CommunityManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.model.Communities;
import eu.trentorise.smartcampus.social.model.Community;
import eu.trentorise.smartcampus.social.model.User;

//@Controller("communityController")
public class CommunityController extends RestController {

	@Autowired
	private CommunityManager communityManager;

	@RequestMapping(method = RequestMethod.PUT, value = "/community/{cid}")
	public @ResponseBody
	Community create(@RequestBody Community communityInRequest,
			@PathVariable String cid) throws SocialServiceException {
		return communityManager.create(communityInRequest, cid);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/community/{cid}")
	public @ResponseBody
	boolean delete(@PathVariable String cid) throws SocialServiceException {
		return communityManager.delete(cid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community")
	public @ResponseBody
	Communities getCommunities() throws SocialServiceException {
		return new Communities(communityManager.getCommunities());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/{cid}")
	public @ResponseBody
	Community getCommunity(@PathVariable String cid)
			throws SocialServiceException {
		return communityManager.getCommunity(cid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/social/{socialid}")
	public @ResponseBody
	Community getCommunityBySocialId(@PathVariable String socialid)
			throws SocialServiceException {
		return communityManager.getCommunityBySocialId(socialid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/community")
	public @ResponseBody
	Communities getUserCommunities() throws SocialServiceException {
		String userId = getUserId();
		User user = getUserObject(userId);
		return new Communities(communityManager.getCommunities(user
				.getSocialId()));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/community/{cid}")
	public @ResponseBody
	boolean addUser(@PathVariable("cid") String cid)
			throws SocialServiceException {
		String userId = getUserId();
		User user = getUserObject(userId);
		return communityManager.addUser(user.getSocialId(), cid);

	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/community/{cid}")
	public @ResponseBody
	boolean removeUser(@PathVariable("cid") String cid)
			throws SocialServiceException {
		String userId = getUserId();
		User user = getUserObject(userId);

		return communityManager.removeUser(user.getSocialId(), cid);

	}

}
