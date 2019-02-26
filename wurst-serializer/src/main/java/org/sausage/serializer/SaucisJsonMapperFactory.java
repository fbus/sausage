package org.sausage.serializer;

import org.sausage.model.Asset;
import org.sausage.model.document.Type;
import org.sausage.model.step.Branch;
import org.sausage.model.step.Step;
import org.sausage.serializer.mixin.JacksonAssetMixin;
import org.sausage.serializer.mixin.JacksonBranchMixin;
import org.sausage.serializer.mixin.JacksonStepMixin;
import org.sausage.serializer.mixin.JacksonTypeMixin;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SaucisJsonMapperFactory {

	public static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.addMixIn(Branch.class, JacksonBranchMixin.class);
        mapper.addMixIn(Step.class, JacksonStepMixin.class);
        mapper.addMixIn(Type.class, JacksonTypeMixin.class);
        mapper.addMixIn(Asset.class, JacksonAssetMixin.class);
        return mapper;
	}
}
