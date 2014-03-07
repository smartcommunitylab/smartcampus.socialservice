package eu.trentorise.smartcampus.social.engine.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;

public class SocialComparator<T> implements Comparator<T> {

	private List<String> fields;
	private ORDER order;

	public static enum ORDER {
		ASC, DESC
	};

	public SocialComparator(List<String> fields, ORDER order) {
		this.fields = fields;
		this.order = order;
	}

	public int compare(T o1, T o2) {
		CompareToBuilder compBuilder = new CompareToBuilder();
		int compare = 0;
		if (fields == null || fields.isEmpty()) {
			compare = CompareToBuilder.reflectionCompare(o1, o2);
		} else {
			for (String field : fields) {
				try {
					compBuilder.append(extractValue(o1, field),
							extractValue(o2, field));
				} catch (Exception e) {
					throw new IllegalArgumentException(String.format(
							"Sort error %s not exist", field));
				}
			}
			compare = compBuilder.toComparison();
		}
		switch (order) {
		case ASC:
		default:
			return compare;
		case DESC:
			return -compare;
		}
	}

	private <T> Object extractValue(T a, String field)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		StringBuffer u = new StringBuffer();
		u.append(Character.toUpperCase(field.toCharArray()[0]));
		u.append(field.substring(1));

		return a.getClass().getMethod("get" + u.toString()).invoke(a);
	}

}
