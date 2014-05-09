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

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.managers.SharingManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.model.EntityRequest;
import eu.trentorise.smartcampus.social.model.ShareVisibility;
import eu.trentorise.smartcampus.social.model.Entity;
import eu.trentorise.smartcampus.social.model.Entities;
import eu.trentorise.smartcampus.social.model.User;

@Controller
public class UserDataController extends RestController {

	@Autowired
	private SharingManager sharingManager;

	@RequestMapping(method = RequestMethod.PUT, value = "/user/shared/{entityId}")
	public @ResponseBody
	boolean share(@PathVariable String entityId, @RequestBody ShareVisibility shareVisibility)
			throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		return sharingManager.share(entityId, user.getSocialId(), shareVisibility);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/shared/{entityId}")
	public @ResponseBody
	boolean unshare(@PathVariable String entityId)
			throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		return sharingManager.unshare(entityId, user.getSocialId());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/shared/{entityId}")
	public @ResponseBody
	Entity getSharedEntity(@PathVariable String entityId)
			throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		if (!sharingManager.checkPermission(user.getSocialId(), entityId)) {
			throw new SecurityException();
		}

		return sharingManager.getShared(entityId, user.getSocialId(), false);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/shared")
	public @ResponseBody
	Entities getSharedEntities(
			@RequestBody ShareVisibility visibility,
			@RequestParam(required=false) int position, 
			@RequestParam(required=false) int size,
			@RequestParam(required=false) String type) throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		String groupId = (visibility.getGroupIds() != null && !visibility
				.getGroupIds().isEmpty()) ? visibility.getGroupIds().get(0)
				: "-1";
		return new Entities(sharingManager.getShared(user.getSocialId(),
				visibility.getUserIds(), groupId, visibility.getCommunityIds(),
				position, size, type, visibility.getCommunityIds() == null
						|| visibility.getCommunityIds().isEmpty(), false));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/entities")
	public @ResponseBody
	Entities getMyContents(
			@RequestParam(required=false) int position, 
			@RequestParam(required=false) int size,
			@RequestParam(required=false) String type) throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		return new Entities(sharingManager.getShared(user.getSocialId(),
				Collections.singletonList(user.getSocialId()), null, null,
				position, size, type, false, false));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/entities/{eid}")
	public @ResponseBody
	Entity getMyContent(@PathVariable String eid) throws SocialServiceException {

		String userId = getUserId();
		User user = getUserObject(userId);

		if (!sharingManager.checkPermission(user.getSocialId(), eid)) {
			throw new SecurityException();
		}

		return sharingManager.getShared(eid, user.getSocialId(), true);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/entities")
	public @ResponseBody
	Entity createEntity(@RequestBody EntityRequest entity)
			throws SocialServiceException {
		String userId = getUserId();
		User user = getUserObject(userId);

		return sharingManager.createEntity(entity, user.getSocialId());
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/entities/{eid}")
	public @ResponseBody
	boolean updateEntity(@PathVariable String eid, @RequestBody EntityRequest entity)
			throws SocialServiceException {
		
		String userId = getUserId();
		User user = getUserObject(userId);

		if (!sharingManager.checkPermission(user.getSocialId(), eid)) {
			throw new SecurityException();
		}
		entity.setId(eid);
		sharingManager.updateEntity(entity);
		return true;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/entities/{eid}")
	public @ResponseBody
	boolean deleteEntity(@PathVariable String eid)
			throws SocialServiceException {
		String userId = getUserId();
		User user = getUserObject(userId);

		if (!sharingManager.checkPermission(user.getSocialId(), eid)) {
			throw new SecurityException();
		}
		return sharingManager.deleteEntity(eid);
	}
}
