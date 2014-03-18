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