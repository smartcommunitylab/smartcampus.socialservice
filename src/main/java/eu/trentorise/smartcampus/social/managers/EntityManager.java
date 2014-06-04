/**
 *    Copyright 2012-2013 Trento RISE
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
 */

package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import eu.trentorise.smartcampus.social.engine.EntityOperations;
import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.beans.Visibility;
import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialGroup;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;
import eu.trentorise.smartcampus.social.engine.repo.CommunityRepository;
import eu.trentorise.smartcampus.social.engine.repo.EntityRepository;
import eu.trentorise.smartcampus.social.engine.repo.EntityTypeRepository;
import eu.trentorise.smartcampus.social.engine.repo.GroupRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
@Transactional
public class EntityManager implements EntityOperations {

	private static final Logger logger = Logger.getLogger(EntityManager.class);

	public static final String URI_CONCAT_CHAR = "-";

	@Autowired
	EntityRepository entityRepository;

	@Autowired
	CommunityRepository communityRepository;

	@Autowired
	EntityTypeRepository typeRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	UserManager userManager;

	@Override
	public Entity saveOrUpdate(String namespace, Entity entity) {
		SocialEntity persistedEntity = null;

		if (entity == null) {
			logger.error("Try to create/update a null entity");
			throw new IllegalArgumentException("entity cannot be null");
		}

		String uri = entity.getUri();
		if (StringUtils.hasLength(uri)) {
			persistedEntity = entityRepository.findOne(uri);
		} else {
			uri = defineUri(namespace, entity);
			persistedEntity = entityRepository.findOne(uri);
		}

		// entity doesn't exist -> create it
		if (persistedEntity == null) {
			logger.info(String.format("Created entity with uri %s", uri));
			persistedEntity = new SocialEntity();
			persistedEntity.setNamespace(namespace);
			persistedEntity.setUri(uri);
			persistedEntity.setLocalId(entity.getLocalId());
			long date = System.currentTimeMillis();
			persistedEntity.setCreationTime(date);
			persistedEntity.setLastModifiedTime(date);

			if (StringUtils.hasText(entity.getType())) {
				try {
					persistedEntity.setType(typeRepository.findOne(new Long(
							entity.getType())));
				} catch (NumberFormatException e) {
					logger.error(String.format(
							"Entity with uri %s, type not valid number %s",
							uri, entity.getType()));
					throw new IllegalArgumentException(
							"type should be a number");
				}
			} else {
				logger.error(String.format(
						"Entity with uri %s, type not valid %s", uri,
						entity.getType()));
				throw new IllegalArgumentException("type should be valid");
			}

			boolean ownerSetted = false;
			if (StringUtils.hasLength(entity.getCommunityOwner())) {
				SocialCommunity community = communityRepository
						.findOne(RepositoryUtils.convertId(entity
								.getCommunityOwner()));
				if (ownerSetted = community != null) {
					persistedEntity.setCommunityOwner(community);
				}
			} else if (ownerSetted = StringUtils.hasLength(entity.getOwner())) {
				persistedEntity.setOwner(userManager.defineSocialUser(entity
						.getOwner()));
			}

			if (!ownerSetted) {
				throw new IllegalArgumentException(
						"owner or communityOwner should be valid");
			}
		} else {
			logger.info(String.format("Updated entity with uri %s", uri));
			persistedEntity.setLastModifiedTime(System.currentTimeMillis());
		}

		if (StringUtils.hasText(entity.getName())) {
			persistedEntity.setName(entity.getName());
		}
		if (StringUtils.hasText(entity.getExternalUri())) {
			persistedEntity.setExternalUri(entity.getExternalUri());
		}
		// set visibility
		setVisibility(entity.getOwner(), persistedEntity,
				entity.getVisibility());
		// save
		persistedEntity = entityRepository.save(persistedEntity);
		return persistedEntity.toEntity(true);
	}

	@Override
	public List<Entity> readShared(String actorId, boolean isCommunity,
			String appId, Limit limit) {
		Set<SocialEntity> result = new HashSet<SocialEntity>();
		Sort sort = null;
		if (limit != null && limit.getSortList() != null
				&& !limit.getSortList().isEmpty()) {
			sort = new Sort(limit.getDirection() == 0 ? Direction.ASC
					: Direction.DESC, limit.getSortList());
		}
		/**
		 * having 4 queries to collect all entities shared with actor is a
		 * problem to paginate results.
		 * 
		 * To simplify pagination is done functionally, probably there will be
		 * some PERFORMANCE problems when many entities [more than 10000] will
		 * be shared public or with the actor
		 */
		long fromDate = RepositoryUtils.DEFAULT_FROM_DATE;
		long toDate = RepositoryUtils.DEFAULT_TO_DATE;
		if (limit != null) {
			if (limit.getFromDate() > 0) {
				fromDate = limit.getFromDate();
			}

			if (limit.getToDate() > 0) {
				toDate = limit.getToDate();
			}
		}

		if (!isCommunity) {
			if (appId == null) {
				result.addAll(entityRepository.findByUserSharedWith(actorId,
						fromDate, toDate, sort));
				result.addAll(entityRepository.findByGroupSharedWith(actorId,
						fromDate, toDate, sort));
				result.addAll(entityRepository.findByCommunitySharedWith(
						actorId, fromDate, toDate, sort));
				result.addAll(entityRepository.findPublicEntities(actorId,
						fromDate, toDate, sort));
			} else {
				result.addAll(entityRepository.findByUserSharedWithByAppId(
						actorId, fromDate, toDate, appId, sort));
				result.addAll(entityRepository.findByGroupSharedWithByAppId(
						actorId, fromDate, toDate, appId, sort));
				result.addAll(entityRepository
						.findByCommunitySharedWithByAppId(actorId, fromDate,
								toDate, appId, sort));
				result.addAll(entityRepository.findPublicEntitiesByAppId(
						actorId, fromDate, toDate, appId, sort));
			}
		} else {
			try {
				if (appId == null) {
					result.addAll(entityRepository.findBySharedWithCommunity(
							new Long(actorId), fromDate, toDate, sort));
					result.addAll(entityRepository.findPublicEntities(new Long(
							actorId), fromDate, toDate, sort));
				} else {
					result.addAll(entityRepository
							.findBySharedWithCommunityByAppId(
									new Long(actorId), fromDate, toDate, appId,
									sort));
					result.addAll(entityRepository.findPublicEntitiesByAppId(
							new Long(actorId), fromDate, toDate, appId, sort));
				}
			} catch (NumberFormatException e) {
				logger.warn(String.format("%s is not valid community id",
						actorId));
			}
		}
		return SocialEntity.toEntity(RepositoryUtils.getSublistPagination(
				new ArrayList<SocialEntity>(result), limit));
	}

	@Override
	public List<Entity> readEntities(String ownerId, String communityId,
			String appId, Limit limit) {
		SocialUser owner = null;
		SocialCommunity communityOwner = null;
		PageRequest pager = null;
		Sort sort = null;
		long fromDate = RepositoryUtils.DEFAULT_FROM_DATE;
		long toDate = RepositoryUtils.DEFAULT_TO_DATE;
		if (limit != null) {
			if (limit.getSortList() != null && !limit.getSortList().isEmpty()) {
				sort = new Sort(limit.getDirection() == 0 ? Direction.ASC
						: Direction.DESC, limit.getSortList());
			}
			if (limit.getPage() >= 0 && limit.getPageSize() > 0) {
				pager = new PageRequest(limit.getPage(), limit.getPageSize(),
						sort);
			}
			if (limit.getFromDate() > 0) {
				fromDate = limit.getFromDate();
			}

			if (limit.getToDate() > 0) {
				toDate = limit.getToDate();
			}
		}
		if (ownerId != null) {
			owner = userManager.defineSocialUser(ownerId);
		}
		if (communityId != null) {
			communityOwner = communityRepository.findOne(RepositoryUtils
					.convertId(communityId));
		}
		if (communityOwner == null && owner == null) {
			return Collections.<Entity> emptyList();
		} else {
			if (pager != null) {
				if (appId == null) {
					return SocialEntity.toEntity(entityRepository
							.findMyEntities(owner, communityOwner, fromDate,
									toDate, pager));
				} else {
					return SocialEntity.toEntity(entityRepository
							.findMyEntitiesByAppId(owner, communityOwner,
									fromDate, toDate, appId, pager));
				}
			} else {
				if (appId == null) {
					return SocialEntity.toEntity(entityRepository
							.findMyEntities(owner, communityOwner, fromDate,
									toDate, sort));
				} else {
					return SocialEntity.toEntity(entityRepository
							.findMyEntitiesByAppId(owner, communityOwner,
									fromDate, toDate, appId, sort));
				}
			}
		}
	}

	@Override
	public Entity readEntity(String uri) {
		SocialEntity entity = entityRepository.findOne(uri);
		return entity != null ? entity.toEntity(true) : null;
	}

	public String defineUri(String namespace, Entity entity)
			throws IllegalArgumentException {
		return defineUri(namespace, entity.getLocalId());
	}

	public String defineUri(String namespace, String localId)
			throws IllegalArgumentException {
		if (!StringUtils.hasText(namespace)) {
			logger.error("namespace should be valid in uri definition");
			throw new IllegalArgumentException("namespace should be valid");
		}
		if (!StringUtils.hasText(localId)) {
			logger.error("localId should be valid in uri definition");
			throw new IllegalArgumentException("localId should be valid");
		}

		return namespace + URI_CONCAT_CHAR + localId;
	}

	public static Visibility getVisibility(SocialEntity entity) {
		Visibility visibility = new Visibility();
		visibility.setPublicShared(entity.isPublicShared());

		if (entity.getGroupsSharedWith() != null) {
			List<String> groups = new ArrayList<String>();
			for (SocialGroup group : entity.getGroupsSharedWith()) {
				groups.add(group.getId().toString());
			}
			visibility.setGroups(groups);
		}

		if (entity.getCommunitiesSharedWith() != null) {
			List<String> communities = new ArrayList<String>();
			for (SocialCommunity community : entity.getCommunitiesSharedWith()) {
				communities.add(community.getId().toString());
			}
			visibility.setCommunities(communities);
		}

		if (entity.getUsersSharedWith() != null) {
			List<String> users = new ArrayList<String>();
			for (SocialUser user : entity.getUsersSharedWith()) {
				users.add(user.getId());
			}
			visibility.setUsers(users);
		}
		return visibility;
	}

	private SocialEntity setVisibility(String ownerId, SocialEntity entity,
			Visibility visibility) {
		if (entity != null && visibility != null) {
			entity.setPublicShared(visibility.isPublicShared());

			if (visibility.getUsers() == null) {
				visibility.setUsers(Collections.<String> emptyList());
			}

			if (visibility.getCommunities() == null) {
				visibility.setCommunities(Collections.<String> emptyList());
			}

			if (visibility.getGroups() == null) {
				visibility.setGroups(Collections.<String> emptyList());
			}

			Set<SocialUser> users = new HashSet<SocialUser>();
			for (String user : visibility.getUsers()) {
				if (!user.equals(ownerId)) {
					users.add(userManager.defineSocialUser(user));
				}
			}
			entity.setUsersSharedWith(users);

			Set<SocialCommunity> communities = new HashSet<SocialCommunity>();
			for (String community : visibility.getCommunities()) {
				SocialCommunity c = communityRepository.findOne(RepositoryUtils
						.convertId(community));
				if (c != null) {
					communities.add(c);
				}
				entity.setCommunitiesSharedWith(communities);
			}
			Set<SocialGroup> groups = new HashSet<SocialGroup>();
			for (String group : visibility.getGroups()) {
				SocialGroup g = groupRepository.findOne(RepositoryUtils
						.convertId(group));
				if (g != null && ownerId != null
						&& g.getCreator().getId().equals(ownerId)) {
					groups.add(g);
				}
			}
			entity.setGroupsSharedWith(groups);
		}
		return entity;
	}

	@Override
	public Entity readShared(String actorId, boolean isCommunity, String uri) {
		SocialEntity entity = null;
		if (!isCommunity) {
			entity = entityRepository.findByUserSharedWith(actorId, uri);
			if (entity == null) {
				entity = entityRepository.findByGroupSharedWith(actorId, uri);
			}
			if (entity == null) {
				entity = entityRepository.findByCommunitySharedWith(actorId,
						uri);
			}
			if (entity == null) {
				entity = entityRepository.findPublicEntities(actorId, uri);
			}
		} else {
			try {
				entity = entityRepository.findBySharedWithCommunity(new Long(
						actorId), uri);
				if (entity == null) {
					entity = entityRepository.findPublicEntities(new Long(
							actorId), uri);
				}
			} catch (NumberFormatException e) {
				logger.warn(String.format("%s is not valid community id",
						actorId));
			}
		}

		return entity != null ? entity.toEntity(true) : null;
	}

	@Override
	public boolean deleteEntity(String uri) {
		entityRepository.delete(uri);
		return true;
	}
}
