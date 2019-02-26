package org.sausage.serializer;

import org.sausage.model.service.FlowService;
import org.sausage.model.step.CompositeStep;
import org.sausage.model.step.Invoke;
import org.sausage.model.step.Step;

public class RelinkFlowReferences {
	
	public static void visit(FlowService service) {
		visit(service.rootStep);
	}
	
	public static void visit(CompositeStep parent) {
		for (Step step : parent.nodes) {

			step.parent = parent;
			
			if (step instanceof CompositeStep) {
				CompositeStep compositeStep = (CompositeStep) step;
				visit(compositeStep);
				
			} else if (step instanceof Invoke) {
				// special case for invoke (pseudo composite step)
				Invoke invoke = (Invoke) step;
				if (invoke.input != null) {
					invoke.input.parentInvoke = invoke;
					visit(invoke.input);
				}
				if (invoke.output != null) {
					invoke.output.parentInvoke = invoke;
					visit(invoke.output);
				}
			}
			
		}
	}

}
