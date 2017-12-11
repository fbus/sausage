package org.sausage.serializer.mixin;

import org.sausage.model.document.CompositeType;
import org.sausage.model.document.DocumentReferenceType;
import org.sausage.model.document.JavaType;
import org.sausage.model.document.StringType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type")
      @JsonSubTypes({ 
        @Type(value = DocumentReferenceType.class, name = "docref"), 
        @Type(value = StringType.class, name = "string"), 
        @Type(value = JavaType.class, name = "javatype"),
        @Type(value = CompositeType.class, name = "document"),
      })
//@JsonIgnoreProperties({"parent"}) // to avoid recursion
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class JacksonTypeMixin {
}
