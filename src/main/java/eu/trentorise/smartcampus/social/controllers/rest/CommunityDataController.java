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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.managers.CommunityManager;
import eu.trentorise.smartcampus.social.managers.SharingManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.model.Community;
import eu.trentorise.smartcampus.social.model.Entities;
import eu.trentorise.smartcampus.social.model.Entity;
import eu.trentorise.smartcampus.social.model.EntityRequest;
import eu.trentorise.smartcampus.social.model.ShareVisibility;

@Controller
public class CommunityDataController extends RestController {

	@Autowired
	private SharingManager sharingManager;
	@Autowired
	private CommunityManager communityManager;
	
	@RequestMapping(method = RequestMethod.PUT, value = "/community/{cid}/shared/{entityId}")
	public @ResponseBody
	boolean share(@PathVariable String entityId, @PathVariable String cid, @RequestBody ShareVisibility shareVisibility)
			throws SocialServiceException {

		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");
		return sharingManager.share(entityId, c.getSocialId(), shareVisibility);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/community/{cid}/shared/{entityId}")
	public @ResponseBody
	boolean unshare(@PathVariable String entityId, @PathVariable String cid)
			throws SocialServiceException {

		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");
		return sharingManager.unshare(entityId, c.getSocialId());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/{cid}/shared/{entityId}")
	public @ResponseBody
	Entity getSharedEntity(@PathVariable String entityId, @PathVariable String cid)
			throws SocialServiceException {

		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");

		if (!sharingManager.checkPermission(c.getSocialId(), entityId)) {
			throw new SecurityException();
		}

		return sharingManager.getShared(entityId, c.getSocialId(), false);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/{cid}/shared")
	public @ResponseBody
	Entities getSharedEntities(
			@PathVariable String cid,
			@RequestBody ShareVisibility visibility,
			@RequestParam(required=false) int position, 
			@RequestParam(required=false) int size,
			@RequestParam(required=false) String type) throws IOException,
			SocialServiceException {
		
		throw new UnsupportedOperationException();
/*
		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");

		String groupId = (visibility.getGroupIds() != null && !visibility
				.getGroupIds().isEmpty()) ? visibility.getGroupIds().get(0)
				: "-1";
		return new Entities(sharingManager.getShared(c.getSocialId(),
				visibility.getUserIds(), groupId, visibility.getCommunityIds(),
				position, size, type, visibility.getCommunityIds() == null
						|| visibility.getCommunityIds().isEmpty(), false));
*/						
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/{cid}/entities")
	public @ResponseBody
	Entities getMyContents(
			@PathVariable String cid,
			@RequestParam(required=false) Integer position, 
			@RequestParam(required=false) Integer size,
			@RequestParam(required=false) String type) throws SocialServiceException {

		throw new UnsupportedOperationException();
/*
  		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");

		return new Entities(sharingManager.getShared(c.getSocialId(),
				null, null, Collections.singletonList(c.getSocialId()), 
				position, size, type, false, shareVisibility));
//		return new Entities(sharingManager.getCommunityEntities(cid, 
//				position, size, type, shareVisibility));
*/		
	}

	@RequestMapping(method = RequestMethod.GET, value = "/community/{cid}/entities/{eid}")
	public @ResponseBody
	Entity getMyContent(@PathVariable String cid, @PathVariable String eid) throws IOException,
			SocialServiceException {

		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");

		if (!sharingManager.checkPermission(c.getSocialId(), eid)) {
			throw new SecurityException();
		}

		return sharingManager.getShared(eid, c.getSocialId(), true);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/community/{cid}/entities")
	public @ResponseBody
	Entity createEntity(@PathVariable String cid, @RequestBody EntityRequest entity)
			throws SocialServiceException {

		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +" is not found.");

		return sharingManager.createEntity(entity, c.getSocialId());
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/community/{cid}/entities/{eid}")
	public @ResponseBody
	boolean updateEntity(@PathVariable String cid, @PathVariable String eid, @RequestBody EntityRequest entity)
			throws SocialServiceException {
		
		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");

		if (!sharingManager.checkPermission(c.getSocialId(), eid)) {
			throw new SecurityException();
		}
		entity.setId(eid);
		sharingManager.updateEntity(entity);
		return true;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/community/{cid}/entities/{eid}")
	public @ResponseBody
	boolean deleteEntity(@PathVariable String cid, @PathVariable String eid)
			throws SocialServiceException {

		Community c = communityManager.getCommunity(cid);
		if (c == null) throw new SocialServiceException("Community with id "+cid +"is not found.");

		if (!sharingManager.checkPermission(c.getSocialId(), eid)) {
			throw new SecurityException();
		}
		return sharingManager.deleteEntity(eid);
	}
}
