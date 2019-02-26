package org.sausage.grinder.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sausage.model.document.CompositeType;
import org.sausage.model.document.Field;
import org.sausage.model.document.Type;
import org.sausage.model.step.map.PipelineChanges;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

public class RecordDiffer {

	private static final ObjectDiffer DIFFER = ObjectDifferBuilder.buildDefault();
	
	public static PipelineChanges getChanges(CompositeType before, CompositeType after) {
		PipelineChanges result = new PipelineChanges();
		
		Map<String, Type> newFieldMap = after.asMap();
		Map<String, Type> beforeMap = before.asMap();
		newFieldMap.keySet().removeAll(beforeMap.keySet());
		
		if(!newFieldMap.isEmpty()) {
			result.added = asCompositeType(newFieldMap);
		}
		
		
		Map<String, Type> modifiedFieldMap = after.asMap();
		modifiedFieldMap.keySet().removeAll(newFieldMap.keySet());
		
		for (Iterator<Entry<String, Type>> iterator = modifiedFieldMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Type> entry = iterator.next();
			
			Type afterType = entry.getValue();
			Type beforeType = beforeMap.get(entry.getKey());
			DiffNode comparison = DIFFER.compare(afterType, beforeType);
			if(comparison.isUntouched()) {
				iterator.remove();
			}
		}
		
		if(!modifiedFieldMap.isEmpty()) {
			result.structureModified = asCompositeType(modifiedFieldMap);
		}
		
    	return result;
	}

	public static Map<String, Type> asMap(CompositeType c) {
		Map<String, Type> result = new LinkedHashMap<String, Type>();
		for (Field field : c.fields) {
			result.put(field.name, field.type);
		}
		return result;
	}

	public static CompositeType asCompositeType(Map<String, Type> map) {
		CompositeType result = new CompositeType();
		for (Entry<String, Type> entry : map.entrySet()) {
			Field field = new Field();
			field.name = entry.getKey();
			field.type = entry.getValue();
			result.fields.add(field);
		}
		return result;
	}
}
