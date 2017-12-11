package org.sausage.model.step;



public class Step {

    public String comment;
    public String label;
    public boolean disable;
    public CompositeStep parent; // <- might not really be needed
}
