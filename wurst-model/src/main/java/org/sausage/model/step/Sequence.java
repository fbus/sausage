package org.sausage.model.step;

public class Sequence extends CompositeStep {

    public enum ExitOn {
        FAILURE, SUCCESS, DONE
    }
    
    public ExitOn exitOn;
}
