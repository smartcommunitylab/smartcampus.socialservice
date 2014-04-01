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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.social.engine.beans.EntityType;
import eu.trentorise.smartcampus.social.engine.beans.Result;
import eu.trentorise.smartcampus.social.managers.EntityTypeManager;

@Controller("typeController")
public class EntityTypeController extends RestController {

	@Autowired
	private EntityTypeManager typeManager;

	@RequestMapping(method = RequestMethod.POST, value = "/type")
	public @ResponseBody
	Result createEntityType(@RequestBody EntityType entityType) {

		Result result = new Result(typeManager.create(entityType.getName(),
				entityType.getMimeType()));

		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/type/{typeId}")
	public @ResponseBody
	Result getEntityTypeById(@PathVariable String typeId) {

		Result result = new Result(typeManager.readType(typeId));
		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/type")
	public @ResponseBody
	Result getEntityTypes(
			@RequestParam(value = "mimeType", required = false) String mimeType,
			@RequestParam(value = "pageNum", required = false) Integer pageNum,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "fromDate", required = false) Long fromDate,
			@RequestParam(value = "toDate", required = false) Long toDate,
			@RequestParam(value = "sortDirection", required = false) Integer sortDirection,
			@RequestParam(value = "sortList", required = false) Set<String> sortList) {

		Result result = null;
		if (mimeType != null) {
			result = new Result(typeManager.readTypesByMimeType(
					mimeType,
					setLimit(pageNum, pageSize, fromDate, toDate,
							sortDirection, sortList)));
		} else {
			result = new Result(typeManager.readTypes(setLimit(pageNum,
					pageSize, fromDate, toDate, sortDirection, sortList)));
		}

		return result;
	}

}
