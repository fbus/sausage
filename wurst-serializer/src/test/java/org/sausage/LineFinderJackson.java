package org.sausage;

import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class LineFinderJackson {

	public static void main(String[] args) throws Exception{
		InputStream stream = LineFinderJackson.class.getResourceAsStream("/sample.yml");
		
		
		//JsonFactory jfactory = new JsonFactory();
		YAMLFactory factory = new YAMLFactory();
		JsonParser jParser = factory.createParser(stream);
		
		while (jParser.nextToken() != null) {
			if(jParser.getCurrentToken() == JsonToken.START_OBJECT) {
				System.out.println("youpi");
			}
		    String fieldname = jParser.getCurrentName();
		    System.out.println(fieldname  + " - " + jParser.getCurrentToken());
		}
		
		 
	}
}
