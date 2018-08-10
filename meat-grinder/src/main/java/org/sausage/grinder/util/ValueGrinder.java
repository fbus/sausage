package org.sausage.grinder.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.wm.data.IData;
import com.wm.util.coder.ValuesCodable;

public class ValueGrinder {

	public static Object convert(Object input) {
		return convert(input, 0);
	}

	private static Object convert(Object input, int depth) {
		Object result = null;
		if (depth > 69) {
			// arbitrary safeguard to avoid stack overflow
			System.out.println("depth limit reached!");
			result = "truncated ! ValueGrinder depth limit reached.";
		}

		if (input == null) {
			result = null;

		} else if (input instanceof Number || input instanceof Boolean || input instanceof CharSequence) {
			result = input;

		} else if (input instanceof IData) {
			result = new SimpleIDataMap((IData) input);

		} else if (input instanceof ValuesCodable) {
			ValuesCodable valuesCodable = (ValuesCodable) input;
			result = new SimpleIDataMap(valuesCodable.getValues());

		} else if (input instanceof Iterable) {
			@SuppressWarnings("rawtypes")
			Iterable it = (Iterable) input;

			List<Object> list = new ArrayList<Object>();
			for (Object element : it) {
				list.add(convert(element, depth + 1));
			}
			result = list;

		} else if (input.getClass().isArray()) {

			List<Object> list = new ArrayList<Object>();
			int length = Array.getLength(input);
			for (int i = 0; i < length; i++) {
				Object element = Array.get(input, i);
				list.add(convert(element, depth + 1));
			}
			result = list;
		} else {
			// result = "complex type " + input.getClass().getName() + " not handled yet !! TODO"; // TODO*
			result = String.valueOf(input);
		}
		return result;
	}
}
