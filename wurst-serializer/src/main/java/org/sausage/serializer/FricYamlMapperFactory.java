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
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class FricYamlMapperFactory {


	public static ObjectMapper getObjectMapper() {
        YAMLFactory factory = new YAMLFactory();
        //factory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        //factory.enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE);
        //factory.disable(YAMLGenerator.Feature.SPLIT_LINES);
        ObjectMapper mapper = new ObjectMapper(factory);
        //mapper.enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE, As.WRAPPER_ARRAY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.addMixIn(Branch.class, JacksonBranchMixin.class);
        mapper.addMixIn(Step.class, JacksonStepMixin.class);
        mapper.addMixIn(Type.class, JacksonTypeMixin.class);
        mapper.addMixIn(Asset.class, JacksonAssetMixin.class);
        return mapper;
	}
}
