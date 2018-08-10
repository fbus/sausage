package org.sausage.grinder;

import org.sausage.model.Asset;
import org.sausage.model.document.DocumentType;

import com.wm.app.b2b.server.BaseService;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSRecord;
import com.wm.lang.ns.NSService;
import com.wm.pkg.art.ns.ConnectionDataNode;
import com.wm.pkg.art.ns.ListenerNode;
import com.wm.pkg.art.ns.NotificationNode;

public class NsNodeGrinder {

	public static Asset convert(NSNode node) {
		final Asset result;
		
		String type = node.getNodeTypeObj().getType();
		if(NSService.TYPE.equals(type)) {
			result = ServiceGrinder.convert((BaseService) node);
			
		} else if(NSRecord.TYPE.equals(type)) {
			DocumentType documentType = new DocumentType();
			documentType.definition = TypeGrinder.convert((NSRecord) node);
			result = documentType;
			
		// TODO avoid hard runtime dependency on wm-adapter-runtime
		} else if(ConnectionDataNode.NSTYPENAME.equals(type)) {
			result = AdapterGrinder.convert((ConnectionDataNode) node);
			
		} else if(ListenerNode.LISTENER_NODE_TYPE_NAME.equals(type)) {
			result = AdapterGrinder.convert((ListenerNode) node);
			
		} else if(NotificationNode.NOTIFICATION_NODE_TYPE_NAME.equals(type)) {
			result = AdapterGrinder.convert((ListenerNode) node);
			
		} else {
			// TODO log. unhandled yet...
			result = null;
			return result;
		}
		
		
		result.setName(node.getNSName().getFullName());
		result.setPackageName(node.getPackage().getName());
		
		return result ;
		
	}
}
