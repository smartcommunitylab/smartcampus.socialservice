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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.engine.beans.Rating;
import eu.trentorise.smartcampus.social.engine.beans.Result;
import eu.trentorise.smartcampus.social.managers.EntityManager;
import eu.trentorise.smartcampus.social.managers.PermissionManager;
import eu.trentorise.smartcampus.social.managers.RatingManager;

@Controller
public class RatingController extends RestController {

	@Autowired
	private RatingManager ratingManager;
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PermissionManager permissionManager;

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/{appId}/rating/{localId}")
	public @ResponseBody
	Result removeRating(@PathVariable String appId, @PathVariable String localId) {
		String userId = getUserId();
		String entityURI = entityManager.defineUri(appId, localId);
		if (!permissionManager.checkRatingPermission(entityURI, userId)) {
			throw new SecurityException();
		}
		return new Result(ratingManager.delete(entityURI, userId));

	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/{appId}/rating/{localId}")
	public @ResponseBody
	Result rate(@RequestBody Rating rating, @PathVariable String appId,
			@PathVariable String localId) {
		String userId = getUserId();
		String entityURI = entityManager.defineUri(appId, localId);
		if (!permissionManager.checkSharingPermission(entityURI, userId)) {
			throw new SecurityException();
		}
		return new Result(ratingManager.rate(entityURI, userId,
				rating.getRating()));
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{appId}/rating/{localId}")
	public @ResponseBody
	Result getRating(@PathVariable String appId, @PathVariable String localId) {
		String userId = getUserId();
		String entityURI = entityManager.defineUri(appId, localId);
		if (!permissionManager.checkRatingPermission(entityURI, userId)) {
			throw new SecurityException();
		}
		return new Result(ratingManager.getRating(userId, entityURI));
	}
}
