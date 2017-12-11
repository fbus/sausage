package org.sausage.grinder;

import org.sausage.model.document.CompositeType;
import org.sausage.model.document.DocumentReferenceType;
import org.sausage.model.document.Field;
import org.sausage.model.document.JavaType;
import org.sausage.model.document.StringType;
import org.sausage.model.document.Type;

import com.wm.lang.ns.NSField;
import com.wm.lang.ns.NSRecord;
import com.wm.lang.ns.NSRecordRef;
import com.wm.util.JavaWrapperType;

public class TypeGrinder {

    public static Field convert(NSField input) {
        Field result = new Field();
        result.name = input.getName();
        final Type type;
        switch (input.getType()) {
        case NSField.FIELD_RECORD:
            type = convert((NSRecord)input);
            break;
        case NSField.FIELD_RECORDREF:
            DocumentReferenceType docRef = new DocumentReferenceType();
            NSRecordRef nsRecordRef = (NSRecordRef)input;
            docRef.ref = nsRecordRef.getTargetName().getFullName();
            type = docRef;
            break;
        case NSField.FIELD_STRING:
            type = new StringType();
            break;
        case NSField.FIELD_OBJECT:
            JavaType javaType = new JavaType();
            javaType.wrapperType = input.getJavaWrapperType() == JavaWrapperType.JAVA_TYPE_UNKNOWN ? null : input.getJavaWrapperTypeString();
            type = javaType;
            break;
        default:
            throw new IllegalArgumentException("unhandled field type : " + input.getType());

        }
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

}
