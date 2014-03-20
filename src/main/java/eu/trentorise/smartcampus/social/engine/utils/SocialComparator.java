package eu.trentorise.smartcampus.social.engine.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * Uses Apache CompareToBuilder to compare class instances on multiple fields.
 * If Class to compare is a wrapper of primitive types or a String, pass an
 * empty array of fields to sort.
 * 
 * If you pass a null field array comparator will not sort class istances.
 * 
 * @author mirko perillo
 * 
 */
public class SocialComparator<T> implements Comparator<T> {

	private List<String> fields;
	private ORDER order;

	private static final List BASE = Arrays.asList(Long.class, String.class,
			Boolean.class);

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

		if (fields != null) {
			if (fields.isEmpty() && isBaseType(o1.getClass())) {
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
		}
		switch (order) {
		case ASC:
		default:
			return compare;
		case DESC:
			return -compare;
		}
	}

	private boolean isBaseType(Class<? extends Object> class1) {
		Class wrapped = ClassUtils.primitiveToWrapper(class1);
		return BASE.contains(wrapped);
	}

	private Object extractValue(T a, String field)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		StringBuffer u = new StringBuffer();
		u.append(Character.toUpperCase(field.toCharArray()[0]));
		u.append(field.substring(1));

		return a.getClass().getMethod("get" + u.toString()).invoke(a);
	}

}
