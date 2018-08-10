package org.sausage.analyzer.parser;

import java.util.ArrayList;
import java.util.List;

public class TextNode {
	
	public String name;
	public FilePosition start;
	public FilePosition end;
	public TextNode parent;
	public List<TextNode> children = new ArrayList<TextNode>();
	
	@Override
	public String toString() {
		return name //
				+ " [" + (start != null ? start.line : "") //
				+ ", " + (end != null ? end.line : "") + "]";
	}
}
