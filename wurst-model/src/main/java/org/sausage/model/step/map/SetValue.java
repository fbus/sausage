package org.sausage.model.step.map;

import org.sausage.model.step.LeafStep;

public class SetValue extends LeafStep {

    public String to;
    public Object value; // TODO ? complex document...
	public boolean doNotOverwritePipelineValue;
	public boolean performVariableSubstitution;

}
