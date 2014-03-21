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

package eu.trentorise.smartcampus.social.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.trentorise.smartcampus.social.engine.beans.Limit;
import eu.trentorise.smartcampus.social.engine.utils.SocialComparator.ORDER;

public class RepositoryUtils {

	public static final long DEFAULT_FROM_DATE = -3600000l; // 01-01-1970
	public static final long DEFAULT_TO_DATE = 4102354800000l; // 31-12-2099

	public static String convertId(Long id) {
		return id == null ? null : id.toString();
	}

	public static Long convertId(String id) throws IllegalArgumentException {
		try {
			return Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(id + " not valid");
		}
	}

	public static <T> List<T> getSublistPagination(List<T> list, Limit limit) {
		if (list == null) {
			return null;
		}
		List<T> result = list;

		if (limit != null) {
			Collections.sort(
					result,
					new SocialComparator<T>(limit.getSortList(), limit
							.getDirection() == 0 ? ORDER.ASC : ORDER.DESC));
			if (limit.getPageSize() > 0) {
				int from = limit.getPage() * limit.getPageSize();
				int to = from + (limit.getPageSize());
				if (from >= result.size()) {
					return Collections.<T> emptyList();
				}
				if (to > result.size()) {
					to = result.size();
				}
				result = result.subList(from, to);
			}

		}
		return result;// NB: from index is included, to index
						// is excluded!
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(
			Collection<T> c, int direction) {
		List<T> list = new ArrayList<T>(c);
		if (direction == 0) {
			java.util.Collections.sort(list);
		} else {
			java.util.Collections.sort(list, Collections.reverseOrder());
		}
		return list;
	}

	public static String normalizeString(String toNormalize) {
		String normalized = null;
		if (toNormalize != null) {
			normalized = toNormalize.trim();
		}
		return normalized;
	}

	public static String normalizeStringLowerCase(String toNormalize) {
		return normalizeString(toNormalize).toLowerCase();
	}

	public static boolean normalizeCompare(String string1, String string2) {
		string1 = string1.toLowerCase();
		string2 = string2.toLowerCase();
		return string1.compareTo(string2) == 0;
	}

	public static String concatStringParams(String[] params) {
		String parameters = "";
		int i = 0;
		for (i = 0; i < params.length - 1; i++) {
			parameters = parameters.concat("'" + params[i] + "', ");
		}
		parameters = parameters.concat("'" + params[i] + "'");
		return parameters;
	}

	public static String getParamFromException(String exMessage) {
		String message = exMessage;
		String property = "property";
		String found = "found";
		message = message.substring(
				message.lastIndexOf(property) + property.length() + 1,
				message.lastIndexOf(found) - 1);
		return message;
	}
}
