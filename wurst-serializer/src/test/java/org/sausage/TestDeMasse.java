package org.sausage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.sausage.analyzer.parser.TextNode;
import org.sausage.analyzer.parser.TextNodeToStepMatcher;
import org.sausage.analyzer.parser.TextNodeTreeParser;
import org.sausage.model.Asset;
import org.sausage.model.service.FlowService;
import org.sausage.model.step.Step;
import org.sausage.serializer.FricYamlMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDeMasse {

	public static void main(String[] args) throws Exception {
		
		
		File folder = new File("c:/temp/fricyaml/");
		File[] listFiles = folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".yml");
			}
		});
		for (File file : listFiles) {
			ObjectMapper objectMapper = FricYamlMapperFactory.getObjectMapper();
			Asset asset;
			try {
				asset = objectMapper.readValue(file, Asset.class);
				if (asset instanceof FlowService) {
					FlowService svc = (FlowService) asset;
					test(svc, file);
				}
				System.out.println(asset.getName());
			} catch (Exception e) {
				System.err.println(file + " << " + e.getMessage());
				System.exit(1);
			}
		}
		
	}

	private static void test(FlowService flowService, File file) {
		TextNodeTreeParser parser = new TextNodeTreeParser();
		FileInputStream stream;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e);
		}
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
