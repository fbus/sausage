package org.sausage.model.step;

import java.util.ArrayList;
import java.util.List;

public class CompositeStep extends Step {

    public final List<Step> nodes = new ArrayList<Step>();
}
