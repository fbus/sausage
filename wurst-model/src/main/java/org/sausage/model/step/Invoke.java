package org.sausage.model.step;

import org.sausage.model.step.invoke.InputMap;
import org.sausage.model.step.invoke.OutputMap;
import org.sausage.model.step.map.PipelineChanges;

public class Invoke extends LeafStep {

    public String serviceName;
    public InputMap input;
    public OutputMap output;

    public PipelineChanges pipelineChanges;
}
