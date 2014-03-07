package eu.trentorise.smartcampus.social.engine.controllers.rest;

import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
	private static String mongoQuery = "_find";
	private static int QUERY_TYPE_INTOBJ = 0;
	private static int QUERY_TYPE_STRING = 1;
	private static int QUERY_AND_CONDITION = 0;
	private static int QUERY_OR_CONDITION = 1;
	
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
		if(StringUtils.hasLength(author)){
			query = createSimpleQuery("author", author, QUERY_TYPE_STRING);
		}
		uri = uri.concat(mongoQuery).concat(query);
		
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
		String query = createSimpleQuery("_id", objectId, QUERY_TYPE_INTOBJ);
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
		if(StringUtils.hasLength(author)){
			query = createMultipleQuery("author", "entityId", author, entityId, QUERY_AND_CONDITION);
		} else {
			query = createSimpleQuery("entityId", entityId, QUERY_TYPE_STRING);
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
	
	private String createSimpleQuery(String param, String value, int type){
		// type 0 -> object, type 1-> string
		String query = "";
		switch (type){
			case 0: query = String.format("?criteria={\"%s\":%s}", param, value); break;
			case 1: 
				try {
					value = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				query = String.format("?criteria={\"%s\":\"%s\"}", param, value);  
				break;
			default: break;	
		}
		return query;
	}
	
	private String createMultipleQuery(String param1, String param2, String value1, String value2, int condition){
		// condition 0 -> and, condition 1 -> or
		String query = "";
		switch (condition){
			case 0: query = String.format("?criteria={_and:[{\"%s\":\"%s\"},{\"%s\":\"%s\"}]}", param1, value1, param2, value2);break;
			case 1: query = String.format("?criteria={_or:[{\"%s\":\"%s\"},{\"%s\":\"%s\"}]}", param1, value1, param2, value2);break;
			default: break;
		}
		return query;
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
