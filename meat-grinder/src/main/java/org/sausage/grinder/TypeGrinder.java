package org.sausage.grinder;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sausage.model.document.CompositeType;
import org.sausage.model.document.DocumentReferenceType;
import org.sausage.model.document.Field;
import org.sausage.model.document.JavaType;
import org.sausage.model.document.StringType;
import org.sausage.model.document.Type;

import com.wm.lang.ns.NSField;
import com.wm.lang.ns.NSRecord;
import com.wm.lang.ns.NSRecordRef;
import com.wm.lang.schema.ContentType;
import com.wm.lang.schema.SimpleType;
import com.wm.lang.schema.datatypev2.Constraint;
import com.wm.util.JavaWrapperType;
import com.wm.util.Name;
import com.wm.util.QName;

public class TypeGrinder {

	public static Field convert(NSField input) {
		Field result = new Field();
		result.name = input.getName();
		final Type type;
		switch (input.getType()) {
		case NSField.FIELD_RECORD:
			type = convert((NSRecord) input);
			break;
		case NSField.FIELD_RECORDREF:
			DocumentReferenceType docRef = new DocumentReferenceType();
			NSRecordRef nsRecordRef = (NSRecordRef) input;
			docRef.ref = nsRecordRef.getTargetName().getFullName();
			type = docRef;
			break;
		case NSField.FIELD_STRING:
			type = new StringType();
			break;
		case NSField.FIELD_OBJECT:
			JavaType javaType = new JavaType();
			javaType.wrapperType = input.getJavaWrapperType() == JavaWrapperType.JAVA_TYPE_UNKNOWN //
					? null //
					: input.getJavaWrapperTypeString();
			type = javaType;
			break;
		default:
			throw new IllegalArgumentException("unhandled field type : " + input.getType());
		}

		if (input.getStringOptions() != null && input.getStringOptions().length > 0) {
			type.picklistChoices = Arrays.asList(input.getStringOptions());
		}
		type.optional = input.isOptional();
		populateXmlFields(input, type);

		result.type = type;
		return result;
	}

	public static CompositeType convert(NSRecord input) {
		CompositeType result = new CompositeType();
		NSField[] fields = input.getFields();
		for (NSField nsField : fields) {
			Field field = convert(nsField);
			result.fields.add(field);
		}
		return result;
	}

	private static void populateXmlFields(NSField input, final Type type) {
		QName schemaType = input.getSchemaTypeName();
		if (schemaType != null) {
			type.xmlType = schemaType.getLocalName().toString();
			String xmlns = schemaType.getNamespace();
			if (xmlns != null && !xmlns.contains("w3.org")) {
				type.xmlns = xmlns;
			}
		}
		Name xmlNamespace = input.getXmlNamespace();
		if (xmlNamespace != null //
				&& input.getParentRecord() != null //
				&& !xmlNamespace.equals(input.getParentRecord().getXmlNamespace())) {
			type.xmlNamespace = xmlNamespace.toString();
		}
		ContentType contentType = input.getContentType();
		if (contentType instanceof SimpleType) {
			SimpleType simpleContentType = (SimpleType) contentType;
			Constraint[] constraints = simpleContentType.getConstraints();
			type.schemaContraints = constraints == null ? null : convertConstraints(constraints);
			QName[] primitiveQNames = simpleContentType.getPrimitiveQNames();
			if (primitiveQNames != null && primitiveQNames.length > 0 && primitiveQNames[0] != null) {
				// the interesting part is the primitive type (i.e decimal, string). The specialized name is useless. 
				type.xmlType = primitiveQNames[0].getNCName();
			} else if(type.xmlType == null) {
				type.xmlType = contentType.getName();
			}
		}
	}

	private static Map<String, List<String>> convertConstraints(Constraint[] constraints) {
		Map<String, List<String>> constraintMap = new LinkedHashMap<String, List<String>>();
		for (Constraint constraint : constraints) {
			constraintMap.put(constraint.getName(), Arrays.asList(constraint.getValue()));
		}
		constraintMap.remove("whiteSpace"); // useless default value
		return constraintMap;
	}

}
