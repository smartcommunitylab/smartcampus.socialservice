package eu.trentorise.smartcampus.social.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import eu.trentorise.smartcampus.social.engine.repo.GroupRepository;
import eu.trentorise.smartcampus.social.engine.repo.SocialTypeRepository;
import eu.trentorise.smartcampus.social.engine.utils.RepositoryUtils;

@Component
@Transactional
public class EntityManager implements EntityOperations {

	@Autowired
	EntityRepository entityRepository;

	@Autowired
	CommunityRepository communityRepository;

	@Autowired
	SocialTypeRepository typeRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	SocialUserManager userManager;

	@Override
	public Entity saveOrUpdate(String namespace, Entity entity) {
		SocialEntity persistedEntity = null;

		if (StringUtils.hasLength(entity.getUri())) {
			persistedEntity = entityRepository.findOne(entity.getUri());
		}

		String uri = defineUri(namespace, entity);
		persistedEntity = entityRepository.findOne(uri);

		// entity doesn't exist -> create it
		if (persistedEntity == null) {
			persistedEntity = new SocialEntity();
			persistedEntity.setNamespace(namespace);
			persistedEntity.setUri(uri);
			persistedEntity.setLocalId(entity.getLocalId());

			if (StringUtils.hasText(entity.getType())) {
				try {
					persistedEntity.setType(typeRepository.findOne(new Long(
							entity.getType())));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(
							"type should be a number");
				}
			} else {
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
		return persistedEntity.toEntity();
	}

	@Override
	public List<Entity> readShared(String actorId, Limit limit) {
		Set<SocialEntity> result = new HashSet<SocialEntity>();
		result.addAll(entityRepository.findByUserSharedWith(actorId));
		result.addAll(entityRepository.findByGroupSharedWith(actorId));
		result.addAll(entityRepository.findByCommunitySharedWith(actorId));
		result.addAll(entityRepository.findPublicEntities(actorId));
		return SocialEntity.toEntity(result);
	}

	@Override
	public List<Entity> readEntities(String ownerId, String communityId,
			Limit limit) {
		SocialUser owner = null;
		SocialCommunity communityOwner = null;
		if (ownerId != null) {
			owner = userManager.defineSocialUser(ownerId);
		}

		if (communityId != null) {
			communityOwner = communityRepository.findOne(RepositoryUtils
					.convertId(communityId));
		}

		return SocialEntity.toEntity(entityRepository
				.findByOwnerOrCommunityOwner(owner, communityOwner));

	}

	@Override
	public Entity readEntity(String uri) {
		SocialEntity entity = entityRepository.findOne(uri);
		return entity != null ? entity.toEntity() : null;
	}

	private String defineUri(String namespace, Entity entity)
			throws IllegalArgumentException {
		if (!StringUtils.hasLength(namespace)) {
			throw new IllegalArgumentException("namespace should be valid");
		}
		if (!StringUtils.hasLength(entity.getLocalId())) {
			throw new IllegalArgumentException("localId should be valid");
		}

		return namespace + "." + entity.getLocalId();
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
}
