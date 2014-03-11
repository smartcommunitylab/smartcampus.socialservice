package eu.trentorise.smartcampus.social.engine.controllers.rest;

import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
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
	
	private static String mongoRestDB = "commentDb";
	private static String mongoRestCollection = "comment";
	private static String mongoRestPort = "27080";
	private static String mongoRestHost = "localhost";
	private static final String mongoQuery = "_find";
	private static final String mongoCount = "_count"; 
	private static final int QUERY_TYPE_INTOBJ = 0;
	private static final int QUERY_TYPE_STRING = 1;
	private static final int QUERY_TYPE_GT = 2;
	private static final int QUERY_TYPE_LT = 3;
	private static final int QUERY_NO_CONDITION = -1;
	private static final int QUERY_AND_CONDITION = 0;
	//private static final int QUERY_OR_CONDITION = 1;
	private static final int PAGE_SIZE = 5;
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/comment")
	public @ResponseBody
	Result getUserComments(
			@RequestParam(value = "author", required = false) String author,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList)
			throws SocialServiceException, IOException {
		String fromServer = "";
		Result result = null;
		
		// here I have to create an http request to the mongodb rest interface
		String uri = createUri(mongoRestHost, mongoRestPort, mongoRestDB, mongoRestCollection);
		
		// create query
		String query = "";
		List<String> parameters = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		List<Integer> types = new ArrayList<Integer>();
		if(StringUtils.hasLength(author)){
			parameters.add("author");
			values.add(author);
			types.add(QUERY_TYPE_STRING);
		}
		if(fromDate != null){
			parameters.add("creationTime");
			values.add(fromDate.toString());
			types.add(QUERY_TYPE_GT);
		}
		if(toDate != null){
			parameters.add("creationTime");
			values.add(toDate.toString());
			types.add(QUERY_TYPE_LT);
		}	
		query = composeQuery(parameters, values, types, QUERY_AND_CONDITION);
		
		// sort
		String sort = "";
		if(sortList != null && !sortList.isEmpty()){
			List<String> params = new ArrayList<String>();
			params.addAll(sortList);
			if(sortDirection != null){
				sort = orderResult(params, sortDirection.intValue());
			} else {
				sort = orderResult(params, 0);
			}
		}
		
		// set limit
		String limit = "";
		String queryCount = "";
		int totElements = 0;
		if(pageNum!= null){
			if(pageSize == null){
				pageSize = PAGE_SIZE;
			}
			
			// here I have to use "_count" operation but it seems to be not available - OK added to handlers.py
			queryCount = uri.concat(mongoCount).concat(query);
			
			// create and manage the rest call for count operation
			URL url = new URL(queryCount);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");
			
			if (connection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
			}
	 
			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
	 
			String output;
			fromServer = "";
			while ((output = br.readLine()) != null) {
				fromServer = fromServer.concat(output);
			}
			connection.disconnect();
			
			String tot = fromServer.substring(10, fromServer.indexOf(","));	// 10 is the index of the "count" string + size + 1 in the response
			totElements = Integer.parseInt(tot);
			
			// check the max page I can request
			if(pageNum.intValue()*pageSize.intValue() > totElements){
				// error in log ad return map available page
				pageNum = totElements/pageSize.intValue();
			}
			
			limit = setPagination(pageSize, pageNum);		
		}
		
		uri = uri.concat(mongoQuery).concat(query).concat(sort).concat(limit);
		
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		
		if (connection.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
		}
 
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
 
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
	Result getUserComment(@PathVariable String commentId)
			throws SocialServiceException, IOException {
		String fromServer = "";
		Result result = null;
		
		// here I have to create an http request to the mongodb rest interface
		String uri = createUri(mongoRestHost, mongoRestPort, mongoRestDB, mongoRestCollection);
		
		// create query
		String objectId = String.format("{\"$oid\":\"%s\"}", commentId);	// wrap id in objectId
		List<String> params = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		List<Integer> types = new ArrayList<Integer>();
		params.add("_id");
		values.add(objectId);
		types.add(QUERY_TYPE_INTOBJ);
		//String query = createSimpleQuery("_id", objectId, QUERY_TYPE_INTOBJ);
		String query = composeQuery(params, values, types, QUERY_NO_CONDITION);
		uri = uri.concat(mongoQuery).concat(query);
		
		// create final url and open connection
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
		
		if (connection.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
		}
 
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
 
		String output;
		while ((output = br.readLine()) != null) {
			fromServer = fromServer.concat(output);
		}
		connection.disconnect();
		
		result = new Result(getJSONStringResult(fromServer));
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/entity/{entityId}/comment")
	public @ResponseBody 
	Result getUserEntityComment(@PathVariable String entityId, @RequestParam(value="author", required=false) String author) throws SocialServiceException, IOException{
		String fromServer = "";
		
		Result result = null;
		if(!StringUtils.hasLength(entityId)){
			Exception ex = new IllegalArgumentException(String.format("param 'entityId' should be valid"));
			result = new Result(ex, 400);
			return result;
		}
		
		// here I have to create an http request to the mongodb rest interface
		String uri = createUri(mongoRestHost, mongoRestPort, mongoRestDB, mongoRestCollection);
		
		// create query
		String query = "";
		List<String> params = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		List<Integer> types = new ArrayList<Integer>();
		if(StringUtils.hasLength(author)){
			params.add("author");
			params.add("entityId");
			values.add(author);
			values.add(entityId);
			types.add(QUERY_TYPE_STRING);
			types.add(QUERY_TYPE_STRING);
			//query = createMultipleQuery("author", "entityId", author, entityId, QUERY_TYPE_STRING, QUERY_TYPE_STRING, QUERY_AND_CONDITION);
			query = composeQuery(params, values, types, QUERY_AND_CONDITION);
		} else {
			params.add("entityId");
			values.add(entityId);
			types.add(QUERY_TYPE_STRING);
			//query = createSimpleQuery("entityId", entityId, QUERY_TYPE_STRING);
			query = composeQuery(params, values, types, QUERY_NO_CONDITION);
		}
		uri = uri.concat(mongoQuery).concat(query);
		
		// create final url and open connection
		URL url = new URL(uri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/json");
				
		if (connection.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
		}
		 
		BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
		
		String output;
		while ((output = br.readLine()) != null) {
			fromServer = fromServer.concat(output);
		}
		connection.disconnect();
				
		result = new Result(getJSONStringResult(fromServer));
		return result;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/user/entity/{entityId}/comment")
	public @ResponseBody 
	Result createUserEntityComment(@RequestBody Comment commentInRequest, @PathVariable String entityId) throws SocialServiceException, IOException{
		String userId = getUserId();
		String user = concatNameAndSurname(getUserObject(userId).getName(),getUserObject(userId).getSurname());
		//String user = "mattia bortolamedi";		// for test
		
		Result result = null;
		try{
			result = new Result(commentManager.create(commentInRequest.getText(), user, entityId));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/entity/{entityId}/comment/{commentId}")
	public @ResponseBody 
	Result deleteUserEntityComment(@PathVariable String entityId, @PathVariable String commentId) throws SocialServiceException, IOException{
		String userId = getUserId();
		String user = concatNameAndSurname(getUserObject(userId).getName(),getUserObject(userId).getSurname());
		
		// check permission
		if(!permissionManager.checkCommentPermission(commentId, user)){
			throw new IllegalArgumentException(String.format("User '%s' has no permission to delete comment '%s'.", user, commentId));
		}
		
		Result result = null;
		try{
			result = new Result(commentManager.delete(commentId, user));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	// Used for tests
	@RequestMapping(method = RequestMethod.PUT, value = "/user/entity/{entityId}/comment/{commentId}")
	public @ResponseBody 
	Result removeUserEntityComment(@PathVariable String entityId, @PathVariable String commentId) throws SocialServiceException, IOException{
		String userId = getUserId();
		String user = concatNameAndSurname(getUserObject(userId).getName(),getUserObject(userId).getSurname());
		
		// check permission
		if(!permissionManager.checkCommentPermission(commentId, user)){
			throw new IllegalArgumentException(String.format("User '%s' has no permission to remove comment '%s'.", user, commentId));
		}
		
		Result result = null;
		try{
			result = new Result(commentManager.remove(commentId, user));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/user/entity/{entityId}/comment")
	public @ResponseBody 
	Result removeEntityComments(@PathVariable String entityId) throws SocialServiceException, IOException{
		
		Result result = null;
		try{
			result = new Result(commentManager.removeByEntity(entityId));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String concatNameAndSurname(String name, String surname){
		return name.concat(" "+surname);
	}
	
	private String createUri(String host, String port, String db, String collection){
		return String.format("http://%s:%s/%s/%s/", host, port, db, collection);
	}
	
	private String composeQuery(List<String> params, List<String> values, List<Integer> types, int condition){
		String query = "";
		String queryHead = "?criteria=";
		int numParams = params.size();
		if(numParams == 1 || condition == QUERY_NO_CONDITION){
			query = queryHead.concat(composeSingleParam(params.get(0), values.get(0), types.get(0)));
		} else {
			query = queryHead;
			if(condition == QUERY_AND_CONDITION){	// And case
				query = query.concat("{\"$and\":[");
			} else {			// Or case
				query = query.concat("{\"$or\":[");
			}
			int i = 0;
			for(i = 0; i < params.size() - 1; i++){
				query = query.concat(composeSingleParam(params.get(i), values.get(i), types.get(i)) + ",");
			}
			query = query.concat(composeSingleParam(params.get(i), values.get(i), types.get(i)) + "]}");
		}
		return query;
	}
	
	private String composeSingleParam(String param, String value, int type){
		String query = "";
		switch (type){
			case 0:	//Object - int
				query = String.format("{\"%s\":%s}", param, value);
				break;	
			case 1: //String
				try {
					value = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				query = String.format("{\"%s\":\"%s\"}", param, value); 
				break;	
			case 2: //Grater than
				query = String.format("{\"%s\":{\"$gt\":%s}}", param, value);
				break;	
			case 3:	//Little than 
				query = String.format("{\"%s\":{\"$lt\":%s}}", param, value);
				break;	
			default: break;
		}
		return query;
	}
	
	private String orderResult(List<String> params, int direction){
		String query = "&sort={";
		if(direction == 1){
			direction = -1;	// here, for descending order I have to use -1 value (in the project is 1)
		}
		if(params.size()==1){
			query = query.concat(String.format("\"%s\":%s", params.get(0),direction));
		} else {
			int i;
			for(i = 0; i < params.size()-1; i++){
				query = query.concat(String.format("{\"%s\":%s},", params.get(i),direction));
			}
			query = query.concat(String.format("{\"%s\":%s}", params.get(i),direction));
		}
		query = query.concat("}");
		
		return query;
	}
	
	private String setPagination(Integer pageSize, Integer pageNumber){
		// limit -> pageSize, skip -> pageNumber
		String pagination = "&limit=%s&skip=%s";
		int limit = pageSize.intValue();
		int skip = (pageNumber.intValue() - 1) * limit;
		pagination = String.format(pagination, limit, skip);
		return pagination;
	}
	
	private String getJSONStringResult(String serverResponse){
		String commentList = "";
		try{
			// here I have to read the content of rows in json output
			JSONObject jsonResult = new JSONObject(serverResponse);
			commentList = jsonResult.getString("results");
			commentList = commentList.substring(commentList.indexOf("[")+1, commentList.indexOf("]"));
		} catch (JSONException ex){
			ex.printStackTrace();
		}
		return commentList;
		
	}
	
}
