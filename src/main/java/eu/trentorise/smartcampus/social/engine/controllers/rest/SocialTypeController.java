package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.managers.PermissionManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.managers.SocialTypeManager;
import eu.trentorise.smartcampus.social.engine.beans.Result;

@Controller("typeController")
public class SocialTypeController extends RestController {

	@Autowired
	private SocialTypeManager typeManager;
	
	@Autowired
	private PermissionManager permissionManager;
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/app/type")
	public @ResponseBody
	Result createEntityType(
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "mimeType", required = true) String mimeType)
			throws SocialServiceException {

		Result result = null;
		try {
			 result = new Result(typeManager.create(name, mimeType));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			result = new Result("");
			result.setErrorCode(HttpStatus.BAD_REQUEST.toString());
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/type/{typeId}")
	public @ResponseBody
	Result getEntityTypeById(@PathVariable String typeId)
			throws SocialServiceException {

		Result result = null;
		try {
			result = new Result(typeManager.readType(typeId));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			result = new Result("");
			result.setErrorCode(HttpStatus.BAD_REQUEST.toString());
			result.setErrorMessage(e.getMessage());
		}
		
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/type")
	public @ResponseBody
	Result getEntityTypeBySuggestions(
			@RequestParam(value = "mimeType", required = false) String mimeType,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate)
			throws SocialServiceException {

		Result result = null;
		try {
			if(mimeType != null){
				result = new Result(typeManager.readTypesByMimeType(mimeType, setLimit(pageNum, pageSize, fromDate, toDate)));
			} else {
				result = new Result(typeManager.readTypes(setLimit(pageNum, pageSize, fromDate, toDate)));
			}
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			result = new Result("");
			result.setErrorCode(HttpStatus.BAD_REQUEST.toString());
			result.setErrorMessage(e.getMessage());
		}
		
		return result;
	}
	
	
}
