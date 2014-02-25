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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;
import eu.trentorise.smartcampus.social.engine.beans.Limit;

/**
 * @author raman
 *
 */
public class RestController extends SCController {

	@Autowired
	private AuthServices services;
	@Override
	protected AuthServices getAuthServices() {
		return services;
	}
	
	private static final int INIT_PAGE = 0;
	private static final int INIT_PAGE_SIZE = 10;
	
	private static final Logger logger = Logger.getLogger(RestController.class);
	
	/**
	 * Issues to manage:
	 * 1- check the input parameters and use only the not-null/not-void values
	 * 2- cast the values from Object to the real value (int or long)
	 * 3- if the pageNum is set and the paseSize not, I use default value
	 * 4- if the paseSize is set and the pageNum not, I use default value
	 */
	protected Limit setLimit(Integer pageNum, Integer pageSize, Long fromDate,
			Long toDate) {
		Limit limit = new Limit();
		int page = INIT_PAGE;
		int page_size = INIT_PAGE_SIZE;
		long from_date = 0L;
		long to_date = 0L;
		if ((pageNum == null) && (pageSize == null) && (fromDate == null) && (toDate == null)) {
			limit = null;
		} else {
			// init the limit pageNumber and pageSize value to the default
			limit.setPage(page);
			limit.setPageSize(page_size);

			if (pageNum != null) {
				page = pageNum.intValue();
				limit.setPage(page);
			}
			if (pageSize != null) {
				page_size = pageSize.intValue();
				if (page_size <= INIT_PAGE_SIZE) {
					limit.setPageSize(page_size);
				} else {
					logger.warn(String.format(
							"Page size %s exceeds the max value %s.",
							page_size, INIT_PAGE_SIZE));
					limit.setPageSize(INIT_PAGE_SIZE);
				}
			}
			if (fromDate != null) {
				from_date = fromDate.longValue();
				limit.setFromDate(from_date);
			}
			if (toDate != null) {
				to_date = toDate.longValue();
				limit.setToDate(to_date);
			}
		}

		return limit;
	}


}
