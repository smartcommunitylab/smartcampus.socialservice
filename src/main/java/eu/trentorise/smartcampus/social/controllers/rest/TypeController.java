/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
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
 ******************************************************************************/
package eu.trentorise.smartcampus.social.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.managers.AlreadyExistException;
import eu.trentorise.smartcampus.social.managers.SharingManager;
import eu.trentorise.smartcampus.social.managers.SocialServiceException;
import eu.trentorise.smartcampus.social.model.Concepts;
import eu.trentorise.smartcampus.social.model.EntityType;
import eu.trentorise.smartcampus.social.model.EntityTypes;

@Controller
public class TypeController extends RestController {

	@Autowired
	private SharingManager sharingManager;
	
	@RequestMapping(method = RequestMethod.POST, value = "/type")
	public @ResponseBody
	EntityType createEntityType(@RequestParam String conceptId) throws  SocialServiceException {

		try {
			return sharingManager.createEntityType(conceptId);
		} catch (AlreadyExistException e) {
			return getEntityTypeByConcept(conceptId);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/type/{typeId}")
	public @ResponseBody
	EntityType getEntityTypeById(@PathVariable String typeId) throws  SocialServiceException {

		return sharingManager.getEntityType(typeId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/type/concept/{cId}")
	public @ResponseBody
	EntityType getEntityTypeByConcept(@PathVariable String cId) throws  SocialServiceException {

		return sharingManager.getEntityTypeByConceptId(cId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/type")
	public @ResponseBody
	EntityTypes getEntityTypeBySuggestions(@RequestParam String prefix,
			@RequestParam(required = false) Integer maxResults)
			throws SocialServiceException {

		return new EntityTypes(sharingManager.getEntityTypeByName(prefix, maxResults));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/concept")
	public @ResponseBody
	Concepts getConceptsBySuggestions(@RequestParam String prefix,
			@RequestParam(required = false) Integer maxResults)
			throws SocialServiceException {

		return new Concepts(sharingManager.getConceptsByName(prefix, maxResults));
	}
	

}
