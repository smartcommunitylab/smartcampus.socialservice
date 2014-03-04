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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	 * Method set Limit: used to initialize the pagination object using the four input parameter's values
	 * @param pageNum: Integer for set the page number;
	 * @param pageSize: Integer for set the page size;
	 * @param fromDate: Long for set the from-date limit
	 * @param toDate: Long for set the to-date limit
	 * @return Limit limit created by the input parameters
	 */
	protected Limit setLimit(Integer pageNum, Integer pageSize, Long fromDate,
			Long toDate, Integer sortDirection, Set<String> sortList) {
		Limit limit = new Limit();
		int page = INIT_PAGE;
		int page_size = INIT_PAGE_SIZE;
		long from_date = 0L;
		long to_date = 0L;
		if ((pageNum == null) && (pageSize == null) && (fromDate == null) && (toDate == null) && (sortDirection == null) && (sortList == null)) {
			limit = null;
		} else {
			// init the limit pageNumber and pageSize value to the default
			limit.setPage(page);
			limit.setPageSize(page_size);
			
			// sorting options
			if(sortDirection != null){
				limit.setDirection(sortDirection.intValue());
			}
			if(sortList != null && !sortList.isEmpty()){
				List<String> sortParams = new ArrayList<String>();
				for(String param : sortList){
					sortParams.add(param);
				}
				limit.setSortList(sortParams);
			}

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
