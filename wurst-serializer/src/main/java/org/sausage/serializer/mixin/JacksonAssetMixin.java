package org.sausage.serializer.mixin;

import org.sausage.model.adapter.AdapterConnection;
import org.sausage.model.adapter.AdapterListener;
import org.sausage.model.adapter.AdapterNotification;
import org.sausage.model.adapter.AdapterService;
import org.sausage.model.document.DocumentType;
import org.sausage.model.service.FlowService;
import org.sausage.model.service.JavaService;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type")
      @JsonSubTypes({ 
        @Type(value = AdapterService.class, name = "adapter"), 
        @Type(value = FlowService.class, name = "flow"), 
        @Type(value = JavaService.class, name = "java"),
        @Type(value = AdapterConnection.class, name = "adapterConnection"),
        @Type(value = AdapterListener.class, name = "adapterListener"),
        @Type(value = AdapterNotification.class, name = "adapterNotification"),
        @Type(value = DocumentType.class, name = "documentType"),
      })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class JacksonAssetMixin {
}
