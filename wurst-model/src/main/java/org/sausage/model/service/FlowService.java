package org.sausage.model.service;

import org.sausage.model.document.CompositeType;
import org.sausage.model.step.CompositeStep;

public class FlowService extends ISService {

    public CompositeStep rootStep = new CompositeStep();
    public CompositeType inputSignature;

}
