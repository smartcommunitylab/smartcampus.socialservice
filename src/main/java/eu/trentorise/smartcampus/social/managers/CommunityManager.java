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
package eu.trentorise.smartcampus.social.managers;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.model.entity.Attribute;
import it.unitn.disi.sweb.webapi.model.entity.DataType;
import it.unitn.disi.sweb.webapi.model.entity.EntityBase;
import it.unitn.disi.sweb.webapi.model.entity.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.exceptions.SmartCampusException;
import eu.trentorise.smartcampus.social.SocialEngineConnector;
import eu.trentorise.smartcampus.social.converters.SocialEngineConverter;
import eu.trentorise.smartcampus.social.model.Community;
import eu.trentorise.smartcampus.social.model.Constants;

/**
 * <i>CommunityManager</i> manages all aspects about communities
 * 
 * @author mirko
 * 
 */
@Component
public class CommunityManager extends SocialEngineConnector {

	public CommunityManager() throws IOException {
		super();
	}

	private static final Logger logger = Logger
			.getLogger(CommunityManager.class);

	private static Community defaultCommunity;

	@Autowired
	private SocialEngineConverter socialConverter;

	/**
	 * methods checks at initialization class if default community exists. If it
	 * doesn't exist then it's created
	 */
	@PostConstruct
	protected void init() throws SmartCampusException {
		super.init();
		try {
			if ((defaultCommunity = getCommunity(Constants.SMARTCAMPUS_COMMUNITY)) == null) {
				logger.info("No default community is present in the system");
				defaultCommunity = new Community();
				defaultCommunity.setName(Constants.SMARTCAMPUS_COMMUNITY);
				if (create(defaultCommunity,Constants.SMARTCAMPUS_COMMUNITY) != null) {
					logger.info("Created default community: "
							+ Constants.SMARTCAMPUS_COMMUNITY);
					defaultCommunity = getCommunity(Constants.SMARTCAMPUS_COMMUNITY);
				} else {
					logger.warn("Exception creating default community");
				}
			} else {

				logger.info("Default community found in system, "
						+ Constants.SMARTCAMPUS_COMMUNITY);
			}
		} catch (Exception e) {
			throw new SmartCampusException(e);
		}
	}

	/**
	 * returns all communities present in the system
	 * 
	 * @return list of communities
	 * @throws SocialServiceException
	 */
	public List<Community> getCommunities() throws SocialServiceException {
		try {
			List<Community> res = new ArrayList<Community>();
			for (it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c : socialEngineClient.readCommunities()) {
				res.add(socialConverter.toCommunity(c, socialEngineClient.readEntity(c.getEntityId(), null)));
			}
			return res;
		} catch (WebApiException e) {
			throw new SocialServiceException();
		}
	}

	/**
	 * returns communities of a given user. If belongsTo is true returns
	 * communities which user belongsTo, other communities otherwise
	 * 
	 * @param socialUserIdStr
	 *            social id of user
	 * @param belongsTo
	 *            flag : if true returns communities which user belongs to,
	 *            other otherwise
	 * 
	 * @return the list of communities
	 * @throws SocialServiceException
	 */
	public List<Community> getCommunities(String socialUserIdStr)
			throws SocialServiceException {
		try {
			Long socialUserId = Long.parseLong(socialUserIdStr);
			it.unitn.disi.sweb.webapi.model.smartcampus.social.User user = socialEngineClient.readUser(socialUserId);
			if (user.getKnownCommunityIds() == null || user.getKnownCommunityIds().isEmpty()) {
				return Collections.emptyList();
			} else {
				List<Community> res = new ArrayList<Community>();
				for (it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c : socialEngineClient
						.readCommunities(new ArrayList<Long>(user.getKnownCommunityIds()))) {
					res.add(socialConverter.toCommunity(c, socialEngineClient.readEntity(c.getEntityId(), null)));
				}
				return res;
			}
		} catch (Exception e) {
			logger.error("Exception getting communities of user "
					+ socialUserIdStr, e);
			throw new SocialServiceException();
		}
	}

	/**
	 * returns community for given alias
	 * 
	 * @param cid
	 *            id of community
	 * @return return the community or null if it doesn't exist
	 * @throws SocialServiceException
	 */
	public Community getCommunity(String cid) throws SocialServiceException {
		try {
			it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c = socialEngineClient.readCommunity(cid);
			if (c == null) return null;
			it.unitn.disi.sweb.webapi.model.entity.Entity e = socialEngineClient.readEntity(c.getEntityId(), null);
			return socialConverter.toCommunity(c,e);

		} catch (Exception e) {
			logger.error("Exception getting community " + cid);
			throw new SocialServiceException();
		}
	}

	/**
	 * returns community for given id
	 * 
	 * @param communityId
	 *            id of community
	 * @return the community or null if it doesn't exist
	 * @throws SocialServiceException
	 */
	private Community getCommunity(long communityId, String cid)
			throws SocialServiceException {
		try {
			it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c = socialEngineClient.readCommunity(cid);
			if (c == null) return null;
			it.unitn.disi.sweb.webapi.model.entity.Entity e = socialEngineClient.readEntity(c.getEntityId(), null);
			return socialConverter.toCommunity(c,e);
		} catch (WebApiException e) {
			logger.error("Exception getting community " + communityId);
			throw new SocialServiceException();
		}
	}

	/**
	 * creates a new community
	 * 
	 * @param community
	 *            data to persist
	 * @param cid 
	 * @return the id of new community
	 * @throws SocialServiceException
	 */

	public Community create(Community community, String cid) throws SocialServiceException {
		it.unitn.disi.sweb.webapi.model.smartcampus.social.Community toSave = new it.unitn.disi.sweb.webapi.model.smartcampus.social.Community();
		toSave.setName(cid);
		try {
			EntityBase eb = new EntityBase();
			eb.setLabel(cid+"_COMMUNITY_" + System.currentTimeMillis());
			// Re-read to get the ID of the default KB
			eb = socialEngineClient.readEntityBase(socialEngineClient
					.create(eb));

			it.unitn.disi.sweb.webapi.model.entity.EntityType communityType = socialEngineClient.readEntityType("community", eb.getKbLabel());
			it.unitn.disi.sweb.webapi.model.entity.Entity entity = new it.unitn.disi.sweb.webapi.model.entity.Entity();
			entity.setEntityBase(eb);
			entity.setEtype(communityType);
			
			List<Attribute> attrs = new ArrayList<Attribute>();
			// name attribute
			if (community.getName() != null) {
				attrs.add(createTextAttribute("name", new String[] { community.getName() }, entity.getEtype()));
			}
			entity.setAttributes(attrs);
			
			Long entityId = socialEngineClient.create(entity);
			toSave.setEntityId(entityId);
			toSave.setEntityBaseId(eb.getId());
			long id = socialEngineClient.create(toSave);
			return getCommunity(id, cid);
		} catch (WebApiException e) {
			logger.error("Exception creating community", e);
			throw new SocialServiceException();
		}
	}

	/**
	 * deletes a community
	 * 
	 * @param cid
	 *            id of community to delete
	 * @return true if delete gone fine, false otherwise
	 * @throws SocialServiceException
	 */
	public boolean delete(String cid) throws SocialServiceException {
		try {
			it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c = socialEngineClient.readCommunity(cid);
			if (c == null) return true;
			
			return socialEngineClient.deleteCommunity(c.getId());
		} catch (WebApiException e) {
			logger.error("Exception deleting community " + cid);
			throw new SocialServiceException();
		}
	}

	/**
	 * adds a user to a community
	 * 
	 * @param socialIdStr
	 *            social id of the user to add
	 * @param cid
	 *            id of community
	 * @return true if operation gone fine, false otherwise
	 * @throws SocialServiceException
	 */
	public boolean addUser(String socialIdStr, String cid)
			throws SocialServiceException {
		try {
			Long socialId = Long.parseLong(socialIdStr);
			it.unitn.disi.sweb.webapi.model.smartcampus.social.User user = socialEngineClient.readUser(socialId);
			if (user.getKnownCommunityIds() == null) {
				user.setKnownCommunityIds(new HashSet<Long>(1));
			}
			
			it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c = socialEngineClient.readCommunity(cid);
			if (c == null) return false;
			
			user.getKnownCommunityIds().add(c.getId());
			socialEngineClient.update(user);
			return true;
		} catch (Exception e) {
			logger.error("Exception adding user " + socialIdStr + " to community "
					+ cid);
			throw new SocialServiceException();
		}
	}

	/**
	 * removes a user from a community
	 * 
	 * @param socialIdStr
	 *            social id of the user to remove
	 * @param cid
	 *            id of community
	 * @return true if operation gone fine, false otherwise
	 */
	public boolean removeUser(String socialIdStr, String cid) {
		boolean success = false;
		try {
			Long socialId = Long.parseLong(socialIdStr);
			it.unitn.disi.sweb.webapi.model.smartcampus.social.User user = socialEngineClient.readUser(socialId);

			it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c = socialEngineClient.readCommunity(cid);
			if (c == null) return false;
			if (user.getKnownCommunityIds() != null &&  user.getKnownCommunityIds().contains(c.getId())) {
				if (user.getKnownCommunityIds().remove(c.getId())) {
					socialEngineClient.update(user);
					success = true;
				}
			}
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

	/**
	 * @param socialid
	 * @return
	 * @throws SocialServiceException 
	 */
	public Community getCommunityBySocialId(String socialid) throws SocialServiceException {
		try {
			it.unitn.disi.sweb.webapi.model.smartcampus.social.Community c = socialEngineClient.readCommunity(Long.parseLong(socialid));
			if (c == null) return null;
			it.unitn.disi.sweb.webapi.model.entity.Entity e = socialEngineClient.readEntity(c.getEntityId(), null);
			return socialConverter.toCommunity(c,e);
		} catch (WebApiException e) {
			logger.error("Exception getting community with social Id " + socialid);
			throw new SocialServiceException();
		}
	}

	private Attribute createTextAttribute(String aName, String[] values,
			it.unitn.disi.sweb.webapi.model.entity.EntityType et) {
		List<Value> valueList = new ArrayList<Value>();
		Attribute a = new Attribute();
		a.setAttributeDefinition(et.getAttributeDefByName(aName));
		for (String s : values) {
			Value v = new Value();
			v.setType(DataType.STRING);
			v.setStringValue(s);
			valueList.add(v);
		}
		a.setValues(valueList);
		return a;
	}
}
