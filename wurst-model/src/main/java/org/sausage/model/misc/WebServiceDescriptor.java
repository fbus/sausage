package org.sausage.model.misc;

import java.util.ArrayList;
import java.util.List;

import org.sausage.model.AbstractAsset;
import org.sausage.model.service.AccessControlList;

public class WebServiceDescriptor extends AbstractAsset {
    
    public AccessControlList acl;

    public List<Operation> operations = new ArrayList<Operation>();

	public String wsdl;

	public String externalUrl;
    
    public static class Operation {
    	public String operationName;
    	public String serviceName;
    }
}
