package eu.trentorise.smartcampus.social.engine.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.engine.beans.Entity;
import eu.trentorise.smartcampus.social.engine.beans.Result;
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
	Result createOrUpdate(@PathVariable String appId, @RequestBody Entity entity) {

		if (!permissionManager.checkEntityPermission(getUserId(),
				StringUtils.hasText(entity.getUri()) ? entity.getUri()
						: entityManager.defineUri(appId, entity), false)) {
			throw new SecurityException();
		}
		// set owner
		entity.setOwner(getUserId());
		return new Result(entityManager.saveOrUpdate(appId, entity));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/app/{appId}/community/{communityId}/entity")
	public @ResponseBody
	Result createOrUpdateByCommunity(@PathVariable String appId,
			@PathVariable String communityId, @RequestBody Entity entity) {

		if (!permissionManager.checkEntityPermission(communityId,
				StringUtils.hasText(entity.getUri()) ? entity.getUri()
						: entityManager.defineUri(appId, entity), true)) {
			throw new SecurityException();
		}

		// set owner
		entity.setCommunityOwner(communityId);
		return new Result(entityManager.saveOrUpdate(appId, entity));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/entity")
	public @ResponseBody
	Result readMyEntities(
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate) {
		return new Result(entityManager.readEntities(getUserId(), null,
				setLimit(pageNum, pageSize, fromDate, toDate, null, null)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{appId}/entity/{localId}")
	public @ResponseBody
	Result readMyEntity(@PathVariable String appId, @PathVariable String localId) {

		if (!permissionManager.checkEntityPermission(getUserId(),
				entityManager.defineUri(appId, localId), false)) {
			throw new SecurityException();
		}

		return new Result(entityManager.readEntity(entityManager.defineUri(
				appId, localId)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/entity")
	public @ResponseBody
	Result readCommunityEntities(
			@PathVariable String appId,
			@PathVariable String communityId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return new Result(entityManager.readEntities(null, communityId,
				setLimit(pageNum, pageSize, fromDate, toDate, null, null)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/entity/{localId}")
	public @ResponseBody
	Result readCommunityEntity(@PathVariable String appId,
			@PathVariable String communityId, @PathVariable String localId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)
				|| !permissionManager.checkEntityPermission(communityId,
						entityManager.defineUri(appId, localId), true)) {
			throw new SecurityException();
		}

		return new Result(entityManager.readEntity(entityManager.defineUri(
				appId, localId)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/shared")
	public @ResponseBody
	Result readSharedEntities(
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate) {
		return new Result(entityManager.readShared(getUserId(), false,
				setLimit(pageNum, pageSize, fromDate, toDate, null, null)));

	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{appId}/shared/{localId}")
	public @ResponseBody
	Result readSharedEntity(@PathVariable String appId,
			@PathVariable String localId) {
		return new Result(entityManager.readShared(getUserId(), false,
				entityManager.defineUri(appId, localId)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/shared")
	public @ResponseBody
	Result readCommunitySharedEntity(
			@PathVariable String appId,
			@PathVariable String communityId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return new Result(entityManager.readShared(communityId, true,
				setLimit(pageNum, pageSize, fromDate, toDate, null, null)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/{appId}/community/{communityId}/shared/{localId}")
	public @ResponseBody
	Result readCommunitySharedEntity(@PathVariable String appId,
			@PathVariable String communityId, @PathVariable String localId) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return new Result(entityManager.readShared(communityId, true,
				entityManager.defineUri(appId, localId)));
	}

}
