package org.sausage.analyzer.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.sausage.model.service.FlowService;
import org.sausage.model.step.CompositeStep;
import org.sausage.model.step.Invoke;
import org.sausage.model.step.Step;

public class TextNodeToStepMatcher {
	
	public Map<Step, TextNode> match(FlowService flowService, TextNode rootTextNode) {
		
		List<TextNode> nodes = TextNodeLister.flatten(rootTextNode);
		List<Step> steps = StepLister.flatten(flowService.rootStep);
		
		// Create a copy containing only steps definition (remove flow metadata at start).
		LinkedList<TextNode> flowNodes = new LinkedList<TextNode>(nodes);
		Iterator<TextNode> it = flowNodes.iterator();
		while (it.hasNext() && !"CompositeStep".equals(it.next().name)) {
			it.remove();
		}
		
		Map<Step, TextNode> result = new HashMap<Step, TextNode>();
		
		if (flowNodes.size() != steps.size()) {
			throw new IllegalArgumentException("length does not match! " //
					+ "textNodes: " + flowNodes.size() //
					+ ", steps: " + steps.size() //
			);
		}
		
		for (int i = 0 ; i < steps.size() ; i++) {
			Step step = steps.get(i);
			TextNode textNode = flowNodes.get(i);
			
			result.put(step, textNode);
		}
		
		return result;
	}
	
	/**
	 * Takes a root {@link TextNode} and builds a flat list of his child tree.
	 */
	private static class TextNodeLister {
		
		static List<TextNode> flatten(TextNode root) {
			List<TextNode> flatList = new ArrayList<TextNode>();
			flatten(root, flatList);
			return flatList;
		}
		
		static void flatten(TextNode root, List<TextNode> flatList) {
			
			flatList.add(root);
			
			for (TextNode child : root.children) {
				if (child.name == null || "document".equals(child.name)) {
					// happens on the pipelineChanges structures. Just ignore the whole subnodes
					continue;
				}
				flatten(child, flatList);
			}
			
		}
	}
	
	/**
	 * Takes a root {@link Step} and builds a flat list of his child tree.
	 */
	private static class StepLister {
		
		static List<Step> flatten(CompositeStep root) {
			List<Step> flatList = new ArrayList<Step>();
			flatten(root, root.nodes, flatList);
			return flatList;
		}
		
		static void flatten(Step root, List<Step> TextNodes, List<Step> flatList) {
			
			flatList.add(root);
			int i = 1;
			for (Step child : TextNodes) {
				child.id = root.id + "." + i++;
				if (child.parent == null && root instanceof CompositeStep) {
					child.parent = (CompositeStep) root;
				}
				
				if (child instanceof CompositeStep) {
					CompositeStep compositeChild = (CompositeStep) child;
					flatten(compositeChild, compositeChild.nodes, flatList);
				} else if (child instanceof Invoke) {
					// handle the pseudo composite case of Invoke. (A terminal leaf... with children)
					Invoke invokeChild = (Invoke) child;
					List<Step> pseudoChildren = new ArrayList<Step>();
					if (invokeChild.input != null) {
						pseudoChildren.add(invokeChild.input);
					}
					if (invokeChild.output != null) {
						pseudoChildren.add(invokeChild.output);
					}
					flatten(invokeChild, pseudoChildren, flatList);
				} else {
					flatList.add(child);
				}
			}
		}
	}
}
