package eu.trentorise.smartcampus.social.engine.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.managers.PermissionManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.managers.SocialTypeManager;
import eu.trentorise.smartcampus.social.engine.beans.EntityType;

@Controller("typeController")
public class SocialTypeController extends RestController {

	@Autowired
	private SocialTypeManager typeManager;
	
	@Autowired
	private PermissionManager permissionManager;
	
	
	@RequestMapping(method = RequestMethod.POST, value = "/app/type")
	public @ResponseBody
	EntityType createEntityType(
			@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "mimeType", required = true) String mimeType)
			throws SocialServiceException {

		return typeManager.create(name, mimeType);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/type/{typeId}")
	public @ResponseBody
	EntityType getEntityTypeById(@PathVariable String typeId)
			throws SocialServiceException {

		return typeManager.readType(typeId);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/user/type")
	public @ResponseBody
	List<EntityType> getEntityTypeBySuggestions(
			@RequestParam(value = "mimeType", required = false) String mimeType,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate)
			throws SocialServiceException {

		if(mimeType != null){
			return typeManager.readTypesByMimeType(mimeType, setLimit(pageNum, pageSize, fromDate, toDate));
		} else {
			return typeManager.readTypes(setLimit(pageNum, pageSize, fromDate, toDate));
		}
	}
	
	
}
