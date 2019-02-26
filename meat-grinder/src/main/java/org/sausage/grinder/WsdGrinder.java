package org.sausage.grinder;

import java.io.StringWriter;
import java.io.Writer;

import javax.wsdl.Definition;

import org.sausage.model.misc.WebServiceDescriptor;
import org.sausage.model.misc.WebServiceDescriptor.Operation;
import org.sausage.model.misc.WsdConsumer;
import org.sausage.model.misc.WsdProvider;

import com.ibm.wsdl.xml.WSDLWriterImpl;
import com.wm.lang.ns.NSWSDescriptor;
import com.wm.lang.websvc.WSOperation;

public class WsdGrinder {

	public static WebServiceDescriptor convert(NSWSDescriptor node) {

		WebServiceDescriptor wsd = node.isInbound() ? new WsdProvider() : new WsdConsumer();
		
		WSOperation[] operations = node.getOperations();
		
		for (WSOperation wsOperation : operations) {
			Operation op = new Operation();
			op.operationName = wsOperation.getOperationName();
			op.serviceName = wsOperation.getServiceName().getFullName();
			wsd.operations.add(op);
		}
		wsd.externalUrl = node.getExternalWSDLURL();
		try {
			Definition wsdl = node.generateWSDLDefinition();
			WSDLWriterImpl writer = new WSDLWriterImpl();
			Writer sink = new StringWriter();
			writer.writeWSDL(wsdl, sink );
			wsd.wsdl = sink.toString();
		} catch (Exception e) {
			// just ignore this part if it fails..
		}
		return wsd;
	}
	
}
