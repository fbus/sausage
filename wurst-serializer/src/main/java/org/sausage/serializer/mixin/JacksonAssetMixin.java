package org.sausage.serializer.mixin;

import org.sausage.model.UnhandledAsset;
import org.sausage.model.adapter.AdapterConnection;
import org.sausage.model.adapter.AdapterListener;
import org.sausage.model.adapter.AdapterNotification;
import org.sausage.model.adapter.AdapterService;
import org.sausage.model.document.DocumentType;
import org.sausage.model.misc.Trigger;
import org.sausage.model.misc.WsdConsumer;
import org.sausage.model.misc.WsdProvider;
import org.sausage.model.service.FlowService;
import org.sausage.model.service.JavaService;
import org.sausage.model.service.UnhandledService;
import org.sausage.model.service.WsdConnectorService;

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
        @Type(value = WsdConnectorService.class, name = "wsdConnector"), 
        @Type(value = AdapterConnection.class, name = "adapterConnection"),
        @Type(value = AdapterListener.class, name = "adapterListener"),
        @Type(value = AdapterNotification.class, name = "adapterNotification"),
        @Type(value = DocumentType.class, name = "documentType"),
        @Type(value = WsdConsumer.class, name = "wsdConsumer"),
        @Type(value = WsdProvider.class, name = "wsdProvider"),
        @Type(value = Trigger.class, name = "trigger"),
        @Type(value = UnhandledService.class, name = "unhandledService"),
        @Type(value = UnhandledAsset.class, name = "unhandledAsset"),
      })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class JacksonAssetMixin {
}
