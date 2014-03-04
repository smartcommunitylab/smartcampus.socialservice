package eu.trentorise.smartcampus.social.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
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

	public static <T> List<T> getSublistPagination(List<T> list, Limit limit) {
		if (limit == null) {
			return list;
		}
		if (list == null) {
			return null;
		}
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

	public static <T extends Comparable<? super T>> List<T> asSortedList(
			Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	public static String normalizeString(String toNormalize) {
		String normalized = null;
		if (toNormalize != null) {
			normalized = toNormalize.trim();
		}
		return normalized;
	}

	public static boolean normalizeCompare(String string1, String string2) {
		string1 = string1.toLowerCase();
		string2 = string2.toLowerCase();
		return string1.compareTo(string2) == 0;
	}
}
