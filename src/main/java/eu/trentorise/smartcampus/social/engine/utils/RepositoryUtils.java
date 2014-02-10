package eu.trentorise.smartcampus.social.engine.utils;

import java.util.List;

import eu.trentorise.smartcampus.social.engine.beans.Limit;

public class RepositoryUtils {

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

	public static List<?> getSublistPagination(List<?> list, Limit limit) {
		int from = limit.getPage() * limit.getPageSize();
		int to = from + (limit.getPageSize());
		if (from >= list.size()) {
			return null;
		}
		if (to > list.size()) {
			to = list.size();
		}
		return list.subList(from, to); // NB: from index is included, to index
										// is excluded!
	}
}
