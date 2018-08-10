package org.sausage.model.document;

import java.util.List;
import java.util.Map;

public class Type {

	public boolean optional;
	public String xmlNamespace;
	public String xmlType;
	public String xmlns;
	public Map<String, List<String>> schemaContraints;
	public List<String> picklistChoices;

}
