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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
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
	private AuthServices authServices;

	@Override
	protected AuthServices getAuthServices() {
		return authServices;
	}

	private static final int INIT_PAGE = 0;
	protected static final int INIT_PAGE_SIZE = 10;

	private static final int QUERY_NO_CONDITION = -1;
	private static final int QUERY_AND_CONDITION = 0;
	// private static final int QUERY_OR_CONDITION = 1;

	private static final Logger logger = Logger.getLogger(RestController.class);

	/**
	 * Method set Limit: used to initialize the pagination object using the four
	 * input parameter's values
	 * 
	 * @param pageNum
	 *            : Integer for set the page number;
	 * @param pageSize
	 *            : Integer for set the page size;
	 * @param fromDate
	 *            : Long for set the from-date limit
	 * @param toDate
	 *            : Long for set the to-date limit
	 * @return Limit limit created by the input parameters
	 */
	protected Limit setLimit(Integer pageNum, Integer pageSize, Long fromDate,
			Long toDate, Integer sortDirection, Set<String> sortList) {
		Limit limit = new Limit();
		int page = INIT_PAGE;
		int page_size = INIT_PAGE_SIZE;
		long from_date = 0L;
		long to_date = 0L;
		if ((pageNum == null) && (pageSize == null) && (fromDate == null)
				&& (toDate == null) && (sortDirection == null)
				&& (sortList == null)) {
			limit = null;
		} else {
			// init the limit pageNumber and pageSize value to the default
			limit.setPage(page);
			limit.setPageSize(page_size);

			// sorting options
			if (sortDirection != null) {
				limit.setDirection(sortDirection.intValue());
			}
			if (sortList != null) { // && !sortList.isEmpty()){
				List<String> sortParams = new ArrayList<String>();
				for (String param : sortList) {
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

	// ---- Mongo operations ----
	/**
	 * Method createUri: used to create the uri of the query operation to
	 * 'sleepy mongoose' rest server.
	 * 
	 * @param host
	 * @param port
	 * @param db
	 * @param collection
	 * @return
	 */
	protected String createUri(String host, String port, String db,
			String collection) {
		return String.format("http://%s:%s/%s/%s/", host, port, db, collection);
	}

	/**
	 * Method composeQuery: used to compose a 'sleepy mongoose' query with the
	 * parameters passed in input.
	 * 
	 * @param params
	 *            : list of string with the parameters to consider in the query;
	 * @param values
	 *            : list of string wiht parameter's values to consider in the
	 *            query;
	 * @param types
	 *            : list of types of the values passed in input
	 * @param condition
	 *            : condition to be used when the parameters are aggregated
	 *            ('and' or 'or')
	 * @return
	 */
	protected String composeQuery(List<String> params, List<String> values,
			List<Integer> types, int condition) {
		String query = "";
		String queryHead = "?criteria=";
		int numParams = params.size();
		if (numParams == 1 || condition == QUERY_NO_CONDITION) {
			query = queryHead.concat(composeSingleParam(params.get(0),
					values.get(0), types.get(0)));
		} else {
			query = queryHead;
			if (condition == QUERY_AND_CONDITION) { // And case
				query = query.concat("{\"$and\":[");
			} else { // Or case
				query = query.concat("{\"$or\":[");
			}
			int i = 0;
			for (i = 0; i < params.size() - 1; i++) {
				query = query.concat(composeSingleParam(params.get(i),
						values.get(i), types.get(i))
						+ ",");
			}
			query = query.concat(composeSingleParam(params.get(i),
					values.get(i), types.get(i))
					+ "]}");
		}
		return query;
	}

	/**
	 * Method composeSingleParam: used to create the query string when using
	 * 'sleepy mongoose' server.
	 * 
	 * @param param
	 *            String with the param to add in the query;
	 * @param value
	 *            String with the param's value to consider in the query
	 * @param type
	 *            int: type of the value: if 0, object or int; if 1 string; if 2
	 *            condition grater than; if 3 condition little than.
	 * @return String query with the composition of the input parameters
	 */
	protected String composeSingleParam(String param, String value, int type) {
		String query = "";
		switch (type) {
		case 0: // Object - int
			query = String.format("{\"%s\":%s}", param, value);
			break;
		case 1: // String
			try {
				value = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			query = String.format("{\"%s\":\"%s\"}", param, value);
			break;
		case 2: // Grater than
			query = String.format("{\"%s\":{\"$gt\":%s}}", param, value);
			break;
		case 3: // Little than
			query = String.format("{\"%s\":{\"$lt\":%s}}", param, value);
			break;
		default:
			break;
		}
		return query;
	}

	/**
	 * Method orderResult: used to order the query response in a specific order
	 * by a specific parameter/parameters
	 * 
	 * @param params
	 *            : list of parameters to be consider in the sorting operation
	 * @param direction
	 *            : direction of the sorting operation: 0 in asc, 1 in desc
	 * @return string query: part of query string to be used in the sorting
	 *         query
	 */
	protected String orderResult(List<String> params, int direction) {
		String query = "&sort={";
		if (direction == 1) {
			direction = -1; // here, for descending order I have to use -1 value
							// (in the project is 1)
		}
		if (params.size() == 1) {
			query = query.concat(String.format("\"%s\":%s", params.get(0),
					direction));
		} else {
			int i;
			for (i = 0; i < params.size() - 1; i++) {
				query = query.concat(String.format("{\"%s\":%s},",
						params.get(i), direction));
			}
			query = query.concat(String.format("{\"%s\":%s}", params.get(i),
					direction));
		}
		query = query.concat("}");

		return query;
	}

	/**
	 * Method setPagination: used to add pagination preferences in the actual
	 * query.
	 * 
	 * @param pageSize
	 *            : dimension of the page to return
	 * @param pageNumber
	 *            : number of the page to return
	 * @return string query: part of query string to be used in pagination
	 *         operation
	 */
	protected String setPagination(Integer pageSize, Integer pageNumber) {
		// limit -> pageSize, skip -> pageNumber
		String pagination = "&limit=%s&skip=%s";
		int limit = pageSize.intValue();
		int skip = pageNumber.intValue() * limit;
		pagination = String.format(pagination, limit, skip);
		return pagination;
	}

	/**
	 * Method getJSONStringResult:used to extract the result JSON from the
	 * mongodb rest server and to format correctly (without the [])
	 * 
	 * @param serverResponse
	 *            : JSON String to be formatted
	 * @return string commentList that contains the JSON representation of the
	 *         comment object returned from the query
	 */
	protected String getJSONStringResult(String serverResponse) {
		String commentList = "";
		try {
			// here I have to read the content of rows in json output
			JSONObject jsonResult = new JSONObject(serverResponse);
			commentList = jsonResult.getString("results");
			// commentList = commentList.substring(commentList.indexOf("[")+1,
			// commentList.indexOf("]"));
			// if(commentList.compareTo("") == 0){
			// commentList = "null";
			// }
		} catch (JSONException ex) {
			ex.printStackTrace();
		}
		return commentList;
	}

}
