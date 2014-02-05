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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.converters.SocialEngineConverter;
import eu.trentorise.smartcampus.social.model.Concept;
import eu.trentorise.smartcampus.social.model.Constants;
import eu.trentorise.smartcampus.social.model.EntityRequest;
import eu.trentorise.smartcampus.social.model.EntityType;
import eu.trentorise.smartcampus.social.model.ShareVisibility;

/**
 * <i>SharingManager</i> manages all aspects about sharing resources in the
 * social network
 * 
 * @author mirko perillo
 * 
 */
// @Component
public class SharingManager extends SocialEngineConnector {

	private static final Logger logger = Logger.getLogger(SharingManager.class);
	@Autowired
	private SocialEngineConverter socialConverter;
	private static final Integer DEFAULT_SUGGESSTIONS_RESULTS = 20;

	// /*
	// * type in social engine
	// *
	// *
	// * social community event experience computer file journey person location
	// * portfolio narrative
	// */
	//
	// private static final String[] supportedTypes = new String[] {
	// Constants.ENTTIY_TYPE_EVENT, Constants.ENTTIY_TYPE_EXPERIENCE,
	// Constants.ENTTIY_TYPE_FILE, Constants.ENTTIY_TYPE_JOURNEY,
	// Constants.ENTTIY_TYPE_POI, Constants.ENTTIY_TYPE_STORY,
	// Constants.ENTTIY_TYPE_PORTFOLIO };
	// private static Map<String, Long> typeIds = null;
	//
	// private Map<String, Long> getTypesIds() throws WebApiException {
	// if (typeIds == null) {
	// typeIds = new HashMap<String, Long>(supportedTypes.length);
	// for (int i = 0; i < supportedTypes.length; i++) {
	// it.unitn.disi.sweb.webapi.model.entity.EntityType eType =
	// socialEngineClient
	// .readEntityType(supportedTypes[i], SemanticHelper
	// .getSCCommunityEntityBase(socialEngineClient)
	// .getKbLabel());
	// if (eType != null) {
	// typeIds.put(supportedTypes[i], eType.getId());
	// } else {
	// logger.error("Type " + supportedTypes[i]
	// + " is not instantiated");
	// }
	// }
	// }
	// return typeIds;
	// }

	/**
	 * shares an entity with some share options
	 * 
	 * @param entityId
	 *            id of entity to share
	 * @param ownerId
	 *            social id of owner of entity
	 * @param visibility
	 *            sharing options, see ShareVisibility class to details
	 * @return true if operation gone fine
	 * @throws SocialServiceException
	 */
	public boolean share(String entityId, String ownerId,
			ShareVisibility visibility) throws SocialServiceException {
		try {
			SemanticHelper.shareEntity(socialEngineClient,
					Long.parseLong(entityId), Long.parseLong(ownerId),
					visibility);
			return true;
		} catch (WebApiException e) {
			logger.error("Exception sharing entity " + entityId + " of user "
					+ ownerId, e);
			throw new SocialServiceException();

		}
	}

	/**
	 * unshares an entity
	 * 
	 * @param entityId
	 *            id of the entity to unshare
	 * @param socialId
	 *            social id of the owner of the entity
	 * @return true if operation gone fine
	 * @throws SocialServiceException
	 */
	public boolean unshare(String entityId, String socialId)
			throws SocialServiceException {
		try {
			SemanticHelper.unshareEntity(socialEngineClient,
					Long.parseLong(entityId), Long.parseLong(socialId));
			return true;
		} catch (WebApiException e) {
			logger.error("Exception unsharing entity " + entityId);
			throw new SocialServiceException();
		}
	}

	/**
	 * @param entityId
	 * @param socialId
	 * @param shareVisibility
	 * @return
	 * @throws SocialServiceException
	 */
	public eu.trentorise.smartcampus.social.model.Entity getShared(
			String entityId, String socialId, boolean shareVisibility)
			throws SocialServiceException {
		try {
			Entity e = socialEngineClient.readEntity(Long.parseLong(entityId),
					null);
			return socialConverter.toEntity(
					e,
					true,
					shareVisibility ? getAssignments(Long.parseLong(socialId),
							Long.parseLong(entityId)) : null);
		} catch (WebApiException e) {
			logger.error("Failure reading entity: " + e.getMessage());
			return null;
		}
	}

	public List<eu.trentorise.smartcampus.social.model.Entity> getCommunityEntities(
			String cid, Integer position, Integer size, String filterType,
			Boolean addVisibility) throws SocialServiceException {
		try {
			Community c = socialEngineClient.readCommunity(cid);
			if (c == null)
				throw new SocialServiceException("Community with id " + cid
						+ "is not found.");

			EntityBase eb = socialEngineClient.readEntityBase(c
					.getEntityBaseId());
			String typeStr = "";
			if (filterType != null) {
				it.unitn.disi.sweb.webapi.model.entity.EntityType et = socialEngineClient
						.readEntityType(Long.parseLong(filterType));
				if (et != null)
					typeStr = et.getName();
			}

			List<Entity> list = socialEngineClient.readEntities(typeStr,
					eb.getLabel(), null);
			if (list != null) {
				List<eu.trentorise.smartcampus.social.model.Entity> res = new ArrayList<eu.trentorise.smartcampus.social.model.Entity>();
				for (Entity e : list) {
					if ("community".equals(e.getEtype().getName()))
						continue;
					ShareVisibility vis = null;
					if (addVisibility)
						vis = getAssignments(c.getId(), e.getId());
					res.add(socialConverter.toEntity(e, false, vis));
				}
				return res;
			}
			return null;
		} catch (WebApiException e) {
			logger.error(
					"Erro reading community " + cid + " entities: "
							+ e.getMessage(), e);
			throw new SocialServiceException(e.getMessage());
		}
	}

	/**
	 * returns the shared contents for a user from particular sources
	 * 
	 * @param ownerStr
	 *            id of the user destination of the shared contents
	 * @param userIds
	 *            list of social ids of user owners that shares the contents
	 * @param groupStr
	 *            list of ids of groups that shares the contents
	 * @param communityIds
	 *            list of ids of communities that shares the contents
	 * @param position
	 *            counter for buffering the results
	 * @param size
	 *            number of results to retrieve
	 * @param filterType
	 *            type ID of entities to retrieve
	 * @param addUser
	 *            if true add user details about user who own the entities, none
	 *            details otherwise
	 * @param addVisibility
	 *            if true add the info about visibility to the objects
	 * @return the list of contents shared with ownerId from the list of
	 *         userIds, groupId and communityIds, empty list if no shared
	 *         contents are present for the user
	 * @throws SocialServiceException
	 */
	public List<eu.trentorise.smartcampus.social.model.Entity> getShared(
			String ownerStr, List<String> userIds, String groupStr,
			List<String> communityIds, Integer position, Integer size,
			String filterType, boolean addUser, boolean addVisibility)
			throws SocialServiceException {
		try {
			if (position == null || position < 0)
				position = 0;

			LiveTopic filter = new LiveTopic();
			LiveTopicSource filterSource = new LiveTopicSource();
			long ownerId = Long.parseLong(ownerStr);
			if (ownerId > 0) {
				filter.setActorId(ownerId); // <-- mandatory
			}
			if (userIds != null && !userIds.isEmpty()) {
				filterSource.setUserIds(new HashSet<Long>());
				for (String s : userIds) {
					filterSource.getUserIds().add(Long.parseLong(s));
				}
			}
			if (groupStr != null) {
				Long groupId = Long.parseLong(groupStr);

				// check if default group
				if (Constants.MY_PEOPLE_GROUP_ID.equals(groupStr)) {
					if (ownerId != -1) {
						filterSource.setAllKnownUsers(true);
					} else {
						throw new SocialServiceException(
								"Cannot user both null ownerId and default group");
					}
				} else if (groupId > 0) {
					filterSource.setGroupIds(Collections.singleton(groupId));
				}
			}

			if (communityIds != null && !communityIds.isEmpty()) {
				filterSource.setCommunityIds(new HashSet<Long>());
				for (String s : communityIds) {
					filterSource.getCommunityIds().add(Long.parseLong(s));
				}
			}
			filter.setSource(filterSource);

			LiveTopicSubject subject = new LiveTopicSubject();
			subject.setAllSubjects(true); // <-- important
			filter.setSubjects(Collections.singleton(subject));

			LiveTopicContentType type = new LiveTopicContentType();
			if (StringUtils.hasLength(filterType)) {
				type.setEntityTypeIds(Collections.singleton(Long
						.parseLong(filterType)));
			} else {
				type.setAllTypes(true);
				type.setEntityTypeIds(new HashSet<Long>());
			}
			filter.setType(type); // <-- mandatory
			filter.setStatus(LiveTopicStatus.ACTIVE); // <-- mandatory

			// long start = System.currentTimeMillis();
			// logger.info("START compute");
			List<Long> sharedIds = socialEngineClient
					.computeEntitiesForLiveTopic(filter, null, null);
			// logger.info("STOP compute " + (System.currentTimeMillis() -
			// start));
			Collections.sort(sharedIds, new Comparator<Long>() {
				public int compare(Long o1, Long o2) {
					return o2 > o1 ? 1 : o2 == o1 ? 0 : -1;
				}
			});
			if (position == null)
				position = 0;
			if (size == null || size < 0)
				size = sharedIds.size();
			if (position < sharedIds.size()) {
				sharedIds = sharedIds.subList(position,
						Math.min(position + size, sharedIds.size()));
			} else {
				return Collections.emptyList();
			}
			if (!sharedIds.isEmpty()) {
				// filter to retrieve only name attribute of entity
				// Filter f = new Filter(null, new HashSet<String>(
				// Arrays.asList("name")), false, false, 0, null, null,
				// null, null);
				List<eu.trentorise.smartcampus.social.model.Entity> shared = new ArrayList<eu.trentorise.smartcampus.social.model.Entity>(
						sharedIds.size());
				// logger.info(String.format("START read items %s",
				// sharedIds.size()));
				List<Entity> results = socialEngineClient.readEntities(
						sharedIds, null);
				// logger.info("STOP read " + (System.currentTimeMillis() -
				// start));
				ShareVisibility vis = null;
				for (Entity e : results) {
					if ("community".equals(e.getEtype().getName()))
						continue;
					if ("person".equals(e.getEtype().getName()))
						continue;
					if (addVisibility)
						vis = getAssignments(ownerId, e.getId());
					shared.add(socialConverter.toEntity(e, addUser, vis));
				}
				return shared;
			} else {
				return Collections.emptyList();
			}
		} catch (WebApiException e) {
			logger.error("Exception getting user shared content", e);
			return Collections.emptyList();
		}
	}

	// /**
	// * returns sharing options of a list of entities
	// *
	// * @param ownerId
	// * the social id of the owner of entities
	// * @param entityIds
	// * list of id of entities
	// * @return the list of sharing options relative to the list of entities
	// * @throws SocialServiceException
	// */
	// private List<ShareVisibility> getAssignments(long ownerId,
	// List<Long> entityIds) throws SocialServiceException {
	// List<ShareVisibility> visibilities = new ArrayList<ShareVisibility>();
	// for (Long eid : entityIds) {
	// visibilities.add(getAssignments(ownerId, eid));
	// }
	// return visibilities;
	// }

	/**
	 * returns the sharing options of an entity
	 * 
	 * @param ownerId
	 *            the social id of the owner of the entity
	 * @param entityId
	 *            the id of the entity
	 * @return sharing options for given entity
	 * @throws SocialServiceException
	 */
	private ShareVisibility getAssignments(long ownerId, long entityId)
			throws SocialServiceException {
		try {
			return socialConverter.toShareVisibility(socialEngineClient
					.readAssignments(entityId, Operation.READ, ownerId));
		} catch (WebApiException e) {
			logger.error("Exception getting assignment of entity", e);
			throw new SocialServiceException();
		}
	}

	public List<EntityType> getEntityTypeByName(String prefix,
			Integer maxResults) throws SocialServiceException {
		try {
			List<EntityType> result = new ArrayList<EntityType>();
			for (it.unitn.disi.sweb.webapi.model.entity.EntityType t : socialEngineClient
					.readEntityTypes(SemanticHelper.getSCCommunityEntityBase(
							socialEngineClient).getKbLabel())) {
				if (t.getName().startsWith(prefix)) {
					result.add(socialConverter.toEntityType(t));
				}
			}

			maxResults = (maxResults != null) ? maxResults
					: DEFAULT_SUGGESSTIONS_RESULTS;
			if (maxResults <= result.size()) {
				return result.subList(0, maxResults);
			} else {
				return result;
			}
		} catch (WebApiException e) {
			throw new SocialServiceException(e);
		}
	}

	public EntityType getEntityTypeByConceptId(String conceptId)
			throws SocialServiceException {
		long conceptGlobalId = getConceptGlobalId(conceptId);
		try {
			return socialConverter.toEntityType(socialEngineClient
					.readEntityTypeByConceptGlobalId(
							conceptGlobalId,
							SemanticHelper.getSCCommunityEntityBase(
									socialEngineClient).getKbLabel()));
		} catch (WebApiException e) {
			logger.error("Exception getting entitytype by concept global id "
					+ conceptGlobalId, e);
			throw new SocialServiceException();
		}
	}

	public EntityType getEntityType(String typeId)
			throws SocialServiceException {
		try {
			return socialConverter.toEntityType(socialEngineClient
					.readEntityType(Long.parseLong(typeId)));
		} catch (Exception e) {
			logger.error("Exception getting entity type", e);
			throw new SocialServiceException();
		}
	}

	public EntityType createEntityType(String conceptId)
			throws SocialServiceException, AlreadyExistException {
		try {
			// search if entitytype already exists
			long conceptGlobalId = getConceptGlobalId(conceptId);
			if (socialEngineClient.readEntityTypeByConceptGlobalId(
					conceptGlobalId, "uk") != null) {
				throw new AlreadyExistException("entityType binded to concept "
						+ conceptId + " already exists");
			}
			it.unitn.disi.sweb.webapi.model.entity.EntityType et = new it.unitn.disi.sweb.webapi.model.entity.EntityType(
					null, null, null, conceptGlobalId);
			AttributeDef textTag = new AttributeDef(null, null, null, null,
					34287L, DataType.STRING, true, null, null);
			AttributeDef semTag = new AttributeDef(null, null, null, null,
					98309L, DataType.SEMANTIC_STRING, true, null, null);
			AttributeDef name = new AttributeDef(null, null, null, null, 2L,
					DataType.STRING, true, null, null);
			AttributeDef entityTag = new AttributeDef(null, null, null, null,
					1L, DataType.RELATION, true, null, null);
			AttributeDef description = new AttributeDef(null, null, null, null,
					3L, DataType.STRING, true, null, null);

			et.getAttrDefs().add(name);
			et.getAttrDefs().add(semTag);
			et.getAttrDefs().add(textTag);
			et.getAttrDefs().add(entityTag);
			et.getAttrDefs().add(description);

			long id = socialEngineClient.create(et);
			return socialConverter.toEntityType(socialEngineClient
					.readEntityType(id));
		} catch (WebApiException e) {
			logger.error("Exception creating entity type", e);
			throw new SocialServiceException();
		}

	}

	public boolean deleteEntityType(long id) throws SocialServiceException {
		try {
			return socialEngineClient.deleteEntityType(id);
		} catch (WebApiException e) {
			logger.error("Exception deleting entity type " + id, e);
			throw new SocialServiceException();
		}
	}

	public Concept getConceptByGlobalId(long id) throws SocialServiceException {
		try {
			return socialConverter.toConcept(socialEngineClient
					.readConceptByGlobalId(id, SemanticHelper
							.getSCCommunityEntityBase(socialEngineClient)
							.getKbLabel()));
		} catch (WebApiException e) {
			logger.error("Exception getting concept", e);
			throw new SocialServiceException();
		}
	}

	public long getConceptGlobalId(String conceptId)
			throws SocialServiceException {
		try {
			return socialEngineClient.readConcept(Long.parseLong(conceptId))
					.getGlobalId();
		} catch (WebApiException e) {
			logger.error("Exception getting concept gloabal id", e);
			throw new SocialServiceException();
		}
	}

	public eu.trentorise.smartcampus.social.model.Entity createEntity(
			EntityRequest entityRequest, String actorId)
			throws SocialServiceException {
		try {
			String entityType = null;
			if (entityRequest.getType() == null
					|| entityRequest.getType().length() == 0) {
				if (entityRequest.getTypeId() != null) {
					EntityType t = getEntityType(entityRequest.getTypeId());
					if (t != null) {
						entityType = t.getName();
						entityRequest.setType(entityType);
					}
				}
			} else {
				entityType = entityRequest.getType();
				List<EntityType> types = getEntityTypeByName(entityType, 1);
				if (types != null && types.size() > 0) {
					entityRequest.setTypeId(types.get(0).getId());
				}
			}
			if (entityType == null) {
				throw new SocialServiceException(String.format(
						"entityType not exists name:%s id:%s",
						entityRequest.getType(), entityRequest.getTypeId()));
			}
			List<Long> rels = new ArrayList<Long>();
			if (entityRequest.getRelations() != null)
				for (String s : entityRequest.getRelations())
					rels.add(Long.parseLong(s));
			Long id = SemanticHelper.createEntity(socialEngineClient,
					Long.parseLong(actorId), entityType,
					entityRequest.getName(), entityRequest.getDescription(),
					entityRequest.getTags(), rels).getId();
			Entity e = socialEngineClient.readEntity(id, null);
			return socialConverter.toEntity(e, false, null);
		} catch (WebApiException e) {
			logger.error("Exception creating entity", e);
			throw new SocialServiceException();
		}
	}

	public void updateEntity(EntityRequest entity)
			throws SocialServiceException {
		try {
			List<Long> rels = new ArrayList<Long>();
			if (entity.getRelations() != null)
				for (String s : entity.getRelations())
					rels.add(Long.parseLong(s));
			SemanticHelper.updateEntity(socialEngineClient,
					Long.parseLong(entity.getId()), entity.getName(),
					entity.getDescription(), entity.getTags(), rels).getId();
		} catch (WebApiException e) {
			logger.error("Exception creating entity", e);
			throw new SocialServiceException();
		}
	}

	public boolean deleteEntity(String entityId) throws SocialServiceException {
		try {
			return SemanticHelper.deleteEntity(socialEngineClient,
					Long.parseLong(entityId));
		} catch (WebApiException e) {
			logger.error("Exception deleting entity " + entityId, e);
			throw new SocialServiceException();
		}
	}

	public boolean checkPermission(String socialId, String entityId)
			throws SocialServiceException {
		Filter f = new Filter(null, new HashSet<String>(Arrays.asList("name")),
				false, false, 0, null, null, null, null);
		try {
			Entity e = socialEngineClient.readEntity(Long.parseLong(entityId),
					f);
			long ownerId = socialEngineClient.readActorByEntityBase(
					e.getEntityBase().getId()).getId();
			return socialId.equals(ownerId + "");
		} catch (WebApiException e1) {
			logger.error("Exception reading entity " + entityId, e1);
			throw new SocialServiceException();
		}
	}

	/**
	 * @param prefix
	 * @param maxResults
	 * @return
	 * @throws SocialServiceException
	 */
	public List<Concept> getConceptsByName(String prefix, Integer maxResults)
			throws SocialServiceException {
		try {
			return SemanticHelper.getSuggestions(socialEngineClient, prefix,
					maxResults);
		} catch (Exception e) {
			throw new SocialServiceException("Failed to read suggestions: "
					+ e.getMessage());
		}
	}

}
