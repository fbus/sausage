package org.sausage.model.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompositeType extends Type {

    public final List<Field> fields = new ArrayList<Field>();

	public List<Field> getFields() {
		return fields;
	}
	
	public Map<String, Type> asMap() {
		Map<String, Type> result = new LinkedHashMap<String, Type>();
		for (Field field : fields) {
			result.put(field.name, field.type);
		}
		return result;
	}

}
