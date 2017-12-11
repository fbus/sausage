package org.sausage.serializer;

import org.sausage.model.document.Type;
import org.sausage.model.service.ISService;
import org.sausage.model.step.Step;
import org.sausage.serializer.mixin.JacksonServiceMixin;
import org.sausage.serializer.mixin.JacksonStepMixin;
import org.sausage.serializer.mixin.JacksonTypeMixin;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SaucisJsonMapperFactory {

	public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.addMixIn(Step.class, JacksonStepMixin.class);
        mapper.addMixIn(Type.class, JacksonTypeMixin.class);
        mapper.addMixIn(ISService.class, JacksonServiceMixin.class);
        return mapper;
	}
}
