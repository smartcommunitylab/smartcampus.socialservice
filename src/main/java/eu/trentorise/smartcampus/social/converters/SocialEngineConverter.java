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
package eu.trentorise.smartcampus.social.converters;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.trentorise.smartcampus.social.managers.CommunityManager;
import eu.trentorise.smartcampus.social.managers.GroupManager;
import eu.trentorise.smartcampus.social.managers.SharingManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.managers.UserManager;
import eu.trentorise.smartcampus.social.model.Community;
import eu.trentorise.smartcampus.social.model.Concept;
import eu.trentorise.smartcampus.social.model.EntityType;
import eu.trentorise.smartcampus.social.model.Group;
import eu.trentorise.smartcampus.social.model.ShareVisibility;

/**
 * <i>SocialEngineConverter</i> converts all model classes from and to social
 * engine model to smartcampus model
 * 
 * @author mirko perillo
 * 
 */
// @Component
public class SocialEngineConverter {

	@Autowired
	private UserManager userManager;

	@Autowired
	private CommunityManager communityManager;

	@Autowired
	private GroupManager groupManager;

	@Autowired
	private SharingManager sharingManager;

	public List<Group> toGroup(List<UserGroup> userGroups)
			throws SocialServiceException {
		List<Group> groups = new ArrayList<Group>();
		try {
			for (UserGroup temp : userGroups) {
				groups.add(toGroup(temp));
			}
		} catch (NullPointerException e) {
			groups = null;
		}
		return groups;
	}

	public Group toGroup(UserGroup userGroup) throws SocialServiceException {
		Group group = new Group();
		try {
			group.setSocialId(userGroup.getId().toString());
			group.setName(userGroup.getName());
			if (userGroup.getUserIds() != null) {
				group.setUsers(userManager.getUsers(userGroup.getUserIds()));
			}
		} catch (NullPointerException e) {
			group = null;
		}

		return group;
	}

	public Community toCommunity(
			it.unitn.disi.sweb.webapi.model.smartcampus.social.Community community,
			Entity entity) {
		Community com = new Community();
		try {
			com.setSocialId(community.getId().toString());
			Attribute a = findAttribute(entity, "name");
			if (a != null && a.getFirstValue() != null)
				com.setName(a.getFirstValue().getStringValue());
			com.setId(community.getName());
		} catch (NullPointerException e) {
			com = null;
		}
		return com;
	}

	public ShareVisibility toShareVisibility(LiveTopicSource src) {
		ShareVisibility sv = new ShareVisibility();
		sv.setUserIds(new ArrayList<String>());
		if (src.getUserIds() != null)
			for (Long l : src.getUserIds())
				sv.getUserIds().add(l.toString());
		sv.setGroupIds(new ArrayList<String>());
		if (src.getGroupIds() != null)
			for (Long l : src.getGroupIds())
				sv.getGroupIds().add(l.toString());
		sv.setCommunityIds(new ArrayList<String>());
		if (src.getCommunityIds() != null)
			for (Long l : src.getCommunityIds())
				sv.getCommunityIds().add(l.toString());
		sv.setAllCommunities(src.isAllCommunities());
		sv.setAllKnownCommunities(src.isAllKnownCommunities());
		sv.setAllUsers(src.isAllUsers());
		sv.setAllKnownUsers(src.isAllKnownUsers());
		return sv;

	}

	public List<eu.trentorise.smartcampus.social.model.Entity> toEntity(
			List<Entity> entities, boolean addActor) throws WebApiException {
		List<eu.trentorise.smartcampus.social.model.Entity> scList = new ArrayList<eu.trentorise.smartcampus.social.model.Entity>();
		try {
			for (Entity e : entities) {
				eu.trentorise.smartcampus.social.model.Entity sc = toEntity(e,
						addActor, null);
				if (sc != null) {
					scList.add(sc);
				}
			}
		} catch (NullPointerException e) {
			return null;
		}
		return scList;
	}

	public eu.trentorise.smartcampus.social.model.Entity toEntity(Entity e,
			boolean addActor, ShareVisibility vis) throws WebApiException {
		eu.trentorise.smartcampus.social.model.Entity sc = null;
		try {
			sc = new eu.trentorise.smartcampus.social.model.Entity();
			sc.setEntityId(e.getId().toString());
			sc.setOwnerId(e.getOwnerId().toString());
			if (e.getCreationTime() != null) {
				sc.setCreationDate(System.currentTimeMillis());
			}
			sc.setEntityType(e.getEtype().getId().toString());
			Attribute a = findAttribute(e, "name");
			if (a != null && a.getFirstValue() != null) {
				sc.setTitle(a.getFirstValue().getString());
			}
			a = findAttribute(e, "description");
			if (a != null && a.getFirstValue() != null) {
				sc.setDescription(a.getFirstValue().getString());
			}
			if (addActor) {
				try {
					sc.setUser(userManager.getUserByEntityBase(e
							.getEntityBase()));
				} catch (SocialServiceException e1) {
				}
			}
			if (vis != null) {
				sc.setVisibility(vis);
			}

			sc.setRelations(new ArrayList<String>());
			a = findAttribute(e, "entity");
			if (a != null && a.getValues() != null) {
				for (Value v : a.getValues()) {
					sc.getRelations().add(
							v.getRelationEntity().getId().toString());
				}
			}

			sc.setTags(new ArrayList<Concept>());
			a = findAttribute(e, "text");
			if (a != null && a.getValues() != null) {
				for (Value v : a.getValues()) {
					Concept c = new Concept(null, v.getStringValue());
					sc.getTags().add(c);
				}
			}
			a = findAttribute(e, "semantic");
			if (a != null && a.getValues() != null) {
				for (Value v : a.getValues()) {
					SemanticString ss = v.getSemanticStringValue();
					it.unitn.disi.sweb.webapi.model.ss.Concept cc = ss
							.getTokens().get(0).getConcepts().get(0);
					Concept c = new Concept(cc.getId().toString(),
							cc.getLabel());
					c.setDescription(cc.getDescription());
					c.setSummary(cc.getSummary());
					sc.getTags().add(c);
				}
			}

		} catch (NullPointerException npe) {
			return null;
		}
		return sc;
	}

	/**
	 * @param e
	 * @param name
	 * @return
	 */
	private Attribute findAttribute(Entity e, String name) {
		if (e != null && e.getAttributes() != null && name != null) {
			for (Attribute a : e.getAttributes()) {
				if (name.equals(a.getAttributeDefinition().getName())
						&& a.getFirstValue() != null) {
					return a;
				}
			}
		}
		return null;
	}

	public Concept toConcept(it.unitn.disi.sweb.webapi.model.ss.Concept c) {
		try {
			Concept concept = new Concept();
			concept.setId(c.getId().toString());
			concept.setName(c.getLabel());
			concept.setSummary(c.getSummary());
			concept.setDescription(c.getDescription());
			return concept;
		} catch (NullPointerException e) {
			return null;
		}
	}

	public it.unitn.disi.sweb.webapi.model.entity.EntityType fromEntityType(
			EntityType type) {
		try {
			it.unitn.disi.sweb.webapi.model.entity.EntityType entityType = new it.unitn.disi.sweb.webapi.model.entity.EntityType();
			entityType.setId(Long.parseLong(type.getId()));
			entityType.setName(type.getName());
			entityType.setConceptGlobalId(sharingManager
					.getConceptGlobalId(type.getConcept().getId()));
			return entityType;
		} catch (Exception e) {
			return null;
		}
	}

	public EntityType toEntityType(
			it.unitn.disi.sweb.webapi.model.entity.EntityType type)
			throws SocialServiceException {
		try {
			EntityType entityType = new EntityType();
			entityType.setId(type.getId().toString());
			entityType.setName(type.getName());
			entityType.setConcept(sharingManager.getConceptByGlobalId(type
					.getConceptGlobalId()));

			return entityType;
		} catch (Exception e) {
			return null;
		}
	}
}
