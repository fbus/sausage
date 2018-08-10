package org.sausage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;
import org.sausage.analyzer.parser.TextNode;
import org.sausage.analyzer.parser.TextNodeToStepMatcher;
import org.sausage.analyzer.parser.TextNodeTreeParser;
import org.sausage.model.service.FlowService;
import org.sausage.model.step.Step;
import org.sausage.serializer.FricYamlMapperFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TextNodeToStepMatcherTest {
	
	@Test
	public void testMe() throws JsonParseException, JsonMappingException, IOException {
		InputStream stream = getClass().getResourceAsStream("/sample.yml");
		
		FlowService flowService = FricYamlMapperFactory.getObjectMapper().readValue(stream, FlowService.class);
		
		stream = getClass().getResourceAsStream("/sample.yml");

		TextNodeTreeParser parser = new TextNodeTreeParser();
		TextNode rootTextNode = parser.parse(stream);
		
		TextNodeToStepMatcher matcher = new TextNodeToStepMatcher();
		
		Map<Step, TextNode> map = matcher.match(flowService, rootTextNode);
		
		for (Entry<Step, TextNode> entry : map.entrySet()) {
			String actual = entry.getKey().getClass().getSimpleName().toLowerCase().replace("step", "");
			String expected = entry.getValue().name.toLowerCase().replace("step", "");
			Assert.assertEquals(expected, actual);
		}
	}
}
