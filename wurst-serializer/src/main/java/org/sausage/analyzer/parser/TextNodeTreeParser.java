package org.sausage.analyzer.parser;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;

/**
 * Low level parser based on Snakeyaml. Builds a tree of TextNode
 * 
 * @author S620024
 *
 */
public class TextNodeTreeParser {
	
	public TextNode parse(InputStream stream) {
		
		FricYamlStreamEventHandler eventWalker = new FricYamlStreamEventHandler();
		
		Iterable<Event> events = new Yaml().parse(new InputStreamReader(stream));
		
		for (Event event : events) {
			if (event instanceof MappingStartEvent) {
				eventWalker.startElement(event.getStartMark(), ((CollectionStartEvent) event).getTag());
				
			} else if (event instanceof MappingEndEvent) {
				eventWalker.endElement(event.getEndMark());
				
			} else if (event instanceof SequenceStartEvent) {
				eventWalker.startSequence(event.getStartMark(), ((CollectionStartEvent) event).getTag());
				
			} else if (event instanceof SequenceEndEvent) {
				eventWalker.endSequence(event.getEndMark());
				
			} else if (event instanceof ScalarEvent) {
				
			} else {
				// most likely StreamEndEvent StreamStartEvent DocumentStartEvent DocumentEndEvent
			}
		}
		
		return eventWalker.root;
	}
	
	public static class FricYamlStreamEventHandler {
		
		public TextNode root = new TextNode();
		public TextNode current;
		public TextNode currentParent = root;
		
		void startElement(Mark mark, String name) {
			
			current = new TextNode();
			current.name = name;
			current.start = newFilePosition(mark);
			current.parent = currentParent;
			currentParent.children.add(current);
			if ("INVOKE".equals(current.name) || "Transformer".equals(current.name)) {
				// ugly hack for pseudo sequence handling
				startSequence(mark, null);
			}
		}
		
		void endElement(Mark mark) {
			current.end = newFilePosition(mark);
			if ("INVOKE".equals(current.name) || "Transformer".equals(current.name)) {
				// ugly hack !
				endSequence(mark);
			}
		}
		
		void startSequence(Mark mark, String name) {
			currentParent = current == null ? root : current;
		}
		
		void endSequence(Mark mark) {
			current = currentParent == null ? root : currentParent;
			currentParent = current.parent == null ? root : current.parent;
		}
		
		private static FilePosition newFilePosition(Mark mark) {
			FilePosition result = new FilePosition();
			result.column = mark.getColumn();
			result.line = mark.getLine();
			return result;
		}
		
	}
}
