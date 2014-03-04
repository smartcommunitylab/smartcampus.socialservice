package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.managers.EntityManager;
import eu.trentorise.smartcampus.social.managers.PermissionManager;

@Controller
public class EntityController extends RestController {

	@Autowired
	PermissionManager permissionManager;

	@Autowired
	EntityManager entityManager;

	@RequestMapping(method = RequestMethod.POST, value = "/user/{appId}/entity")
	public @ResponseBody
	Entity createOrUpdate(@PathVariable String appId, @RequestBody Entity entity) {

		if (!permissionManager.checkEntityPermission(getUserId(),
				StringUtils.hasText(entity.getUri()) ? entity.getUri()
						: entityManager.defineUri(appId, entity), false)) {
			throw new SecurityException();
		}
		// set owner
		entity.setOwner(getUserId());
		return entityManager.saveOrUpdate(appId, entity);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/app/{appId}/community/{communityId}/entity")
	public @ResponseBody
	Entity createOrUpdateByCommunity(@PathVariable String appId,
			@PathVariable String communityId, @RequestBody Entity entity) {

		if (!permissionManager.checkEntityPermission(communityId,
				StringUtils.hasText(entity.getUri()) ? entity.getUri()
						: entityManager.defineUri(appId, entity), true)) {
			throw new SecurityException();
		}

		// set owner
		entity.setCommunityOwner(communityId);
		return entityManager.saveOrUpdate(appId, entity);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/entity")
	public @ResponseBody
	List<Entity> readMyEntities() {
		return entityManager.readEntities(getUserId(), null, null);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{appId}/entity/{localId}")
	public @ResponseBody
	Entity readMyEntity(@PathVariable String appId, @PathVariable String localId) {

		if (!permissionManager.checkEntityPermission(getUserId(),
				entityManager.defineUri(appId, localId), false)) {
			throw new SecurityException();
		}

		return entityManager
				.readEntity(entityManager.defineUri(appId, localId));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/entity")
	public @ResponseBody
	List<Entity> readCommunityEntities(@PathVariable String appId,
			@PathVariable String communityId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return entityManager.readEntities(null, communityId, null);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/entity/{localId}")
	public @ResponseBody
	Entity readCommunityEntity(@PathVariable String appId,
			@PathVariable String communityId, @PathVariable String localId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)
				|| !permissionManager.checkEntityPermission(communityId,
						entityManager.defineUri(appId, localId), true)) {
			throw new SecurityException();
		}

		return entityManager
				.readEntity(entityManager.defineUri(appId, localId));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/shared")
	public @ResponseBody
	List<Entity> readSharedEntities() {
		return entityManager.readShared(getUserId(), false,
				setLimit(null, null, null, null));

	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{appId}/shared/{localId}")
	public @ResponseBody
	Entity readSharedEntity(@PathVariable String appId,
			@PathVariable String localId) {
		return entityManager.readShared(getUserId(), false,
				entityManager.defineUri(appId, localId));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/shared")
	public @ResponseBody
	List<Entity> readCommunitySharedEntity(@PathVariable String appId,
			@PathVariable String communityId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return entityManager.readShared(communityId, true,
				setLimit(null, null, null, null));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/shared/{localId}")
	public @ResponseBody
	Entity readCommunitySharedEntity(@PathVariable String appId,
			@PathVariable String communityId, @PathVariable String localId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return entityManager.readShared(communityId, true,
				entityManager.defineUri(appId, localId));
	}

}
