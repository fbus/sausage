package org.sausage.model.step;

public class Exit extends LeafStep {

    public enum FromEnum {
        PARENT,
        FLOW,
        LOOP;
    }
    public String failureMessage;
    public Boolean signalFailure;
    public FromEnum from;

}
