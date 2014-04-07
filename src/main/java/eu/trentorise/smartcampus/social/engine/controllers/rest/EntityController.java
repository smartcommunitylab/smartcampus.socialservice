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

package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.util.Set;

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

	@RequestMapping(method = RequestMethod.POST, value = "/app/{appId}/{userId}/entity/create")
	public @ResponseBody
	Result createOrUpdate(@PathVariable String appId,
			@PathVariable String userId, @RequestBody Entity entity) {

		if (!permissionManager.checkEntityPermission(userId,
				StringUtils.hasText(entity.getUri()) ? entity.getUri()
						: entityManager.defineUri(appId, entity), false)) {
			throw new SecurityException();
		}
		// set owner
		entity.setOwner(userId);
		return new Result(entityManager.saveOrUpdate(appId, entity));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/app/{appId}/{userId}/entity/update")
	public @ResponseBody
	Result updateByApp(@PathVariable String appId, @PathVariable String userId,
			@RequestBody Entity entity) {

		String uri = StringUtils.hasText(entity.getUri()) ? entity.getUri()
				: entityManager.defineUri(appId, entity);

		if (!permissionManager.checkEntityPermission(userId, uri, false)) {
			throw new SecurityException("Invalid access to entity");
		}

		if (entityManager.readEntity(uri) == null) {
			throw new IllegalArgumentException("Entity not exist");
		}

		return new Result(entityManager.saveOrUpdate(appId, entity));
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/{appId}/entity/update")
	public @ResponseBody
	Result updateByUser(@PathVariable String appId, @RequestBody Entity entity) {

		String uri = StringUtils.hasText(entity.getUri()) ? entity.getUri()
				: entityManager.defineUri(appId, entity);

		if (!permissionManager.checkEntityPermission(getUserId(), uri, false)) {
			throw new SecurityException("Invalid access to entity");
		}

		if (entityManager.readEntity(uri) == null) {
			throw new IllegalArgumentException("Entity not exist");
		}

		return new Result(entityManager.saveOrUpdate(appId, entity));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/app/{appId}/community/{communityId}/entity")
	public @ResponseBody
	Result createOrUpdateByCommunity(@PathVariable String appId,
			@PathVariable String communityId, @RequestBody Entity entity) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)
				|| !permissionManager.checkEntityPermission(communityId,
						StringUtils.hasText(entity.getUri()) ? entity.getUri()
								: entityManager.defineUri(appId, entity), true)) {
			throw new SecurityException();
		}

		// set owner
		entity.setCommunityOwner(communityId);
		return new Result(entityManager.saveOrUpdate(appId, entity));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/app/entity/{uri}/info")
	public @ResponseBody
	Result gigaMethod(@PathVariable String uri) {
		Entity entity = entityManager.readEntity(uri);
		return new Result(entity == null ? null : entity.toEntityInfo());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/entity")
	public @ResponseBody
	Result readMyEntities(
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {
		return new Result(entityManager.readEntities(
				getUserId(),
				null,
				null,
				setLimit(pageNum, pageSize, fromDate, toDate, sortDirection,
						sortList)));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{appId}/entity")
	public @ResponseBody
	Result readMyEntitiesByApp(
			@PathVariable String appId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {
		return new Result(entityManager.readEntities(
				getUserId(),
				null,
				appId,
				setLimit(pageNum, pageSize, fromDate, toDate, sortDirection,
						sortList)));
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
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return new Result(entityManager.readEntities(
				null,
				communityId,
				null,
				setLimit(pageNum, pageSize, fromDate, toDate, sortDirection,
						sortList)));
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
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {
		return new Result(entityManager.readShared(
				getUserId(),
				false,
				null,
				setLimit(pageNum, pageSize, fromDate, toDate, sortDirection,
						sortList)));

	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{appId}/shared")
	public @ResponseBody
	Result readSharedEntitiesByAppId(
			@PathVariable String appId,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {
		return new Result(entityManager.readShared(
				getUserId(),
				false,
				appId,
				setLimit(pageNum, pageSize, fromDate, toDate, sortDirection,
						sortList)));

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
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {

		if (!permissionManager.checkCommunityPermission(appId, communityId)) {
			throw new SecurityException();
		}
		return new Result(entityManager.readShared(
				communityId,
				true,
				null,
				setLimit(pageNum, pageSize, fromDate, toDate, sortDirection,
						sortList)));
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
