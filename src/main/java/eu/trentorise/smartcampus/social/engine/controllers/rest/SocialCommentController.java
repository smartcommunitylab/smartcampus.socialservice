package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.engine.beans.Comment;
import eu.trentorise.smartcampus.social.engine.beans.Result;
import eu.trentorise.smartcampus.social.managers.PermissionManager;
import eu.trentorise.smartcampus.social.managers.SocialCommentManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;

@Controller("commentController")
public class SocialCommentController extends RestController {

	@Autowired
	SocialCommentManager commentManager;

	@Autowired
	private PermissionManager permissionManager;

	private static final Logger logger = Logger
			.getLogger(SocialCommentController.class);
	// mongodb connection
	private static String mongoRestDB = "commentDb";
	private static String mongoRestCollection = "comment";
	private static String mongoRestPort = "27080";
	private static String mongoRestHost = "localhost";
	private static final String mongoQuery = "_find";
	private static final String mongoCount = "_count";
	// mongodb query configuration
	private static final int QUERY_TYPE_INTOBJ = 0;
	private static final int QUERY_TYPE_STRING = 1;
	private static final int QUERY_TYPE_GT = 2;
	private static final int QUERY_TYPE_LT = 3;
	private static final int QUERY_NO_CONDITION = -1;
	private static final int QUERY_AND_CONDITION = 0;
	// private static final int QUERY_OR_CONDITION = 1;
	private static final String BATCH_SIZE = "&batch_size=10000";
	// comment fields
	private static final String AUTHOR = "author";
	private static final String CREATION_TIME = "creationTime";
	private static final String ENTITY_ID = "entityId";
	private static final String COMMENT_TEXT = "commentText";

	@RequestMapping(method = RequestMethod.GET, value = "/user/comment")
	public @ResponseBody
	Result getUserComments(
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList,
			@RequestParam(value = "findType", required = false) Integer findType)
			throws SocialServiceException, IOException {
		String fromServer = "";
		Result result = null;

		String uri = createUri(mongoRestHost, mongoRestPort, mongoRestDB,
				mongoRestCollection);

		// create query
		String query = "";
		List<String> parameters = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		List<Integer> types = new ArrayList<Integer>();
		if (StringUtils.hasLength(author)) {
			parameters.add(AUTHOR);
			values.add(author);
			types.add(QUERY_TYPE_STRING);
		}
		if (fromDate != null) {
			parameters.add(CREATION_TIME);
			values.add(fromDate.toString());
			types.add(QUERY_TYPE_GT);
		}
		if (toDate != null) {
			parameters.add(CREATION_TIME);
			values.add(toDate.toString());
			types.add(QUERY_TYPE_LT);
		}
		if (!parameters.isEmpty()) {
			query = composeQuery(parameters, values, types, QUERY_AND_CONDITION);
		}
		// sort
		String sort = "";
		if (sortList != null && !sortList.isEmpty()) {
			List<String> params = new ArrayList<String>();
			params.addAll(sortList);
			if (sortDirection != null) {
				sort = orderResult(params, sortDirection.intValue());
			} else {
				sort = orderResult(params, 0);
			}
		}
		// set limit
		String limit = "";
		String queryCount = "";
		int totElements = 0;
		if (pageNum != null) {
			if (pageSize == null) {
				pageSize = INIT_PAGE_SIZE;
			}

			queryCount = uri.concat(mongoCount).concat(query);
			// create and manage the rest call for count operation
			URL url = new URL(queryCount);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");

			if (connection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ connection.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(connection.getInputStream())));

			String output;
			fromServer = "";
			while ((output = br.readLine()) != null) {
				fromServer = fromServer.concat(output);
			}
			connection.disconnect();

			String tot = fromServer.substring(10, fromServer.indexOf(",")); // 10
																			// is
																			// the
																			// index
																			// of
																			// the
																			// "count"
																			// string
																			// +
																			// size
																			// +
																			// 1
																			// in
																			// the
																			// response
			totElements = Integer.parseInt(tot);

			// check the max page I can request
			if (pageNum.intValue() * pageSize.intValue() > totElements) {
				logger.error(String
						.format("The requested page %s exceded the max available page %s",
								pageNum.intValue(),
								totElements / pageSize.intValue()));
				pageNum = totElements / pageSize.intValue();
			}
			limit = setPagination(pageSize, pageNum);
		}
		uri = uri.concat(mongoQuery).concat(query).concat(sort).concat(limit);

		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");

		if (connection.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ connection.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(connection.getInputStream())));

		String output;
		fromServer = "";
		while ((output = br.readLine()) != null) {
			fromServer = fromServer.concat(output);
		}
		connection.disconnect();

		result = new Result(getJSONStringResult(fromServer));
		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/comment/{commentId}")
	public @ResponseBody
	Result getUserComment(@PathVariable String commentId,
			@RequestParam(value = "findType", required = false) Integer findType)
			throws SocialServiceException, IOException {
		Result result = null;
		if (findType == null) { // force the usage of api rest mongodb server
			findType = 1;
		}
		if (findType == 0) {
			// query with mongo driver in manager
			result = new Result(commentManager.readComment(commentId));

		} else if (findType == 1) {
			// query with mongo rest interface "sleepy mongoose"
			String fromServer = "";
			// here I have to create an http request to the mongodb rest
			// interface
			String uri = createUri(mongoRestHost, mongoRestPort, mongoRestDB,
					mongoRestCollection);

			// create query
			String objectId = String.format("{\"$oid\":\"%s\"}", commentId); // wrap
																				// id
																				// in
																				// objectId
			List<String> params = new ArrayList<String>();
			List<String> values = new ArrayList<String>();
			List<Integer> types = new ArrayList<Integer>();
			params.add("_id");
			values.add(objectId);
			types.add(QUERY_TYPE_INTOBJ);
			String query = composeQuery(params, values, types,
					QUERY_NO_CONDITION);
			uri = uri.concat(mongoQuery).concat(query);

			// create final url and open connection
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");

			if (connection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ connection.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(connection.getInputStream())));

			String output;
			while ((output = br.readLine()) != null) {
				fromServer = fromServer.concat(output);
			}
			connection.disconnect();

			result = new Result(getJSONStringResult(fromServer));
		} else if (findType == 2) {
			// query with mongo driver using raw json data
			result = new Result(commentManager.readComment_JSON(commentId));
		}

		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/entity/{entityId}/comment")
	public @ResponseBody
	Result getUserEntityComment(
			@PathVariable String entityId,
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "commentText", required = false) String commentText,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList,
			@RequestParam(value = "findType", required = false) Integer findType)
			throws SocialServiceException, IOException {
		String fromServer = "";

		Result result = null;
		if (!StringUtils.hasLength(entityId)) {
			Exception ex = new IllegalArgumentException(
					String.format("param 'entityId' should be valid"));
			result = new Result(ex, 400);
			return result;
		}
		if (findType == null) { // force the usage of api rest mongodb server
			findType = 1;
		}
		if (findType == 0) {
			// query with mongo driver in manager
			if (StringUtils.hasLength(commentText)
					&& StringUtils.hasLength(author)) {
				result = new Result(
						commentManager.readCommentsByAutorAndTextAndEntity(
								author, commentText, entityId));
			} else {
				result = new Result(commentManager.readCommentsByEntity(
						entityId,
						setLimit(pageNum, pageSize, fromDate, toDate,
								sortDirection, sortList)));
			}
		} else if (findType == 1) {
			// query with mongo rest interface "sleepy mongoose"
			String uri = createUri(mongoRestHost, mongoRestPort, mongoRestDB,
					mongoRestCollection);

			// create query
			String query = "";
			List<String> params = new ArrayList<String>();
			List<String> values = new ArrayList<String>();
			List<Integer> types = new ArrayList<Integer>();
			if (StringUtils.hasLength(commentText)) {
				params.add(COMMENT_TEXT);
				values.add(commentText);
				types.add(QUERY_TYPE_STRING);
			}
			if (StringUtils.hasLength(author)) {
				params.add(AUTHOR);
				values.add(author);
				types.add(QUERY_TYPE_STRING);
			}
			params.add(ENTITY_ID);
			values.add(entityId);
			types.add(QUERY_TYPE_STRING);

			if (fromDate != null) {
				params.add(CREATION_TIME);
				values.add(fromDate.toString());
				types.add(QUERY_TYPE_GT);
			}
			if (toDate != null) {
				params.add(CREATION_TIME);
				values.add(toDate.toString());
				types.add(QUERY_TYPE_LT);
			}
			if (params.size() == 1) {
				query = composeQuery(params, values, types, QUERY_NO_CONDITION);
			} else {
				query = composeQuery(params, values, types, QUERY_AND_CONDITION);
			}
			// sort
			String sort = "";
			if (sortList != null && !sortList.isEmpty()) {
				List<String> parameters = new ArrayList<String>();
				parameters.addAll(sortList);
				if (sortDirection != null) {
					sort = orderResult(parameters, sortDirection.intValue());
				} else {
					sort = orderResult(parameters, 0);
				}
			}
			// set limit
			String limit = "";
			String queryCount = "";
			int totElements = 0;
			if (pageNum != null) {
				if (pageSize == null) {
					pageSize = INIT_PAGE_SIZE;
				}
				queryCount = uri.concat(mongoCount).concat(query);

				// create and manage the rest call for count operation
				URL url = new URL(queryCount);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");

				if (connection.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ connection.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
						(connection.getInputStream())));

				String output;
				fromServer = "";
				while ((output = br.readLine()) != null) {
					fromServer = fromServer.concat(output);
				}
				connection.disconnect();

				String tot = fromServer.substring(10, fromServer.indexOf(",")); // 10
																				// is
																				// the
																				// index
																				// of
																				// the
																				// "count"
																				// string
																				// +
																				// size
																				// +
																				// 1
																				// in
																				// the
																				// response
				totElements = Integer.parseInt(tot);

				// check the max page I can request
				if (pageNum.intValue() * pageSize.intValue() > totElements) {
					logger.error(String
							.format("The requested page %s exceded the available pages %s",
									pageNum.intValue(),
									totElements / pageSize.intValue()));
					pageNum = totElements / pageSize.intValue();
				}

				limit = setPagination(pageSize, pageNum);
			}
			uri = uri.concat(mongoQuery).concat(query).concat(sort)
					.concat(limit).concat(BATCH_SIZE);

			// create final url and open connection
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");

			if (connection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ connection.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(connection.getInputStream())));

			String output;
			fromServer = "";
			while ((output = br.readLine()) != null) {
				fromServer = fromServer.concat(output);
			}
			connection.disconnect();
			result = new Result(getJSONStringResult(fromServer));

		} else if (findType == 2) {
			// query with mongo driver using raw json data
			if (StringUtils.hasLength(commentText)
					&& StringUtils.hasLength(author)) {
				result = new Result(
						commentManager
								.readCommentsByAutorAndTextAndEntity_JSON(
										author, commentText, entityId));
			} else {
				result = new Result(commentManager.readCommentsByEntity_JSON(
						entityId,
						setLimit(pageNum, pageSize, fromDate, toDate,
								sortDirection, sortList)));
			}
		}
		return result;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user/entity/{entityId}/comment")
	public @ResponseBody
	Result createUserEntityComment(@RequestBody Comment commentInRequest,
			@PathVariable String entityId) throws SocialServiceException,
			IOException {
		String userId = getUserId();
		String user = concatNameAndSurname(getUserObject(userId).getName(),
				getUserObject(userId).getSurname());

		return new Result(commentManager.create(commentInRequest.getText(),
				user, entityId));

	}

	// @RequestMapping(method = RequestMethod.DELETE, value =
	// "/user/entity/{entityId}/comment/{commentId}")
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/comment/{commentId}")
	public @ResponseBody
	Result deleteUserEntityComment(
	/* @PathVariable String entityId, */@PathVariable String commentId)
			throws SocialServiceException, IOException {
		String userId = getUserId();
		String user = concatNameAndSurname(getUserObject(userId).getName(),
				getUserObject(userId).getSurname());

		if (!StringUtils.hasLength(commentId)) {
			throw new IllegalArgumentException(
					"param 'commentId' should be valid");
		}

		// check permission
		if (!permissionManager.checkCommentPermission(commentId, user)) {
			throw new IllegalArgumentException(String.format(
					"User '%s' has no permission to delete comment '%s'.",
					user, commentId));
		}

		return new Result(commentManager.delete(commentId, user));

	}

	// Used for tests
	@RequestMapping(method = RequestMethod.PUT, value = "/user/entity/{entityId}/comment/{commentId}")
	public @ResponseBody
	Result removeUserEntityComment(@PathVariable String entityId,
			@PathVariable String commentId) throws SocialServiceException,
			IOException {
		String userId = getUserId();
		String user = concatNameAndSurname(getUserObject(userId).getName(),
				getUserObject(userId).getSurname());

		// check permission
		if (!permissionManager.checkCommentPermission(commentId, user)) {
			throw new IllegalArgumentException(String.format(
					"User '%s' has no permission to remove comment '%s'.",
					user, commentId));
		}
		return new Result(commentManager.remove(commentId));
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/user/entity/{entityId}/comment")
	public @ResponseBody
	Result removeEntityComments(@PathVariable String entityId)
			throws SocialServiceException, IOException {

		return new Result(commentManager.removeByEntity(entityId));
	}

	private String concatNameAndSurname(String name, String surname) {
		return name.concat(" " + surname);
	}

}
