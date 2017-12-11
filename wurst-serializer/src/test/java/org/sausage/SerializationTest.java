package org.sausage;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.sausage.model.service.FlowService;
import org.sausage.model.service.ISService;
import org.sausage.serializer.FricYamlMapperFactory;
import org.sausage.serializer.SaucisJsonMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationTest {

	@Test
	public void testMe() throws Exception {
		InputStream resourceAsStream = getClass().getResourceAsStream("/sample.yml");
		
		ISService result = FricYamlMapperFactory.getObjectMapper().readValue(resourceAsStream, ISService.class);
		
		Assert.assertTrue(result instanceof FlowService);
		Assert.assertEquals("AFS_PEGASE_to_Collective_v1", result.packageName);
		
		
		ObjectMapper saucisjsonMapper = SaucisJsonMapperFactory.getObjectMapper();
		String json = saucisjsonMapper.writeValueAsString(result);
		
		Assert.assertTrue(json.contains("functionCode = '01' || functionCode = '03'"));
		
		saucisjsonMapper.readValue(json, ISService.class);
		Assert.assertTrue(result instanceof FlowService);
	}
}
