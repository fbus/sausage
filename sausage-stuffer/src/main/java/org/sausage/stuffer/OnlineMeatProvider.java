package org.sausage.stuffer;

import java.util.ArrayList;
import java.util.List;

import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;

public class OnlineMeatProvider implements MeatProvider {

	@Override
	public List<NSName> getNodeNames(String packageName) {
		Package pkg = PackageManager.getPackage(packageName);
		@SuppressWarnings("unchecked")
		List<NSNode> nodeList = Namespace.current().getNodes(pkg);

		List<NSName> result = new ArrayList<NSName>();
		for (NSNode nsNode : nodeList) {
			result.add(nsNode.getNSName());
		}

		return result;
	}

	@Override
	public NSNode getNode(String packageName, NSName name) {
		return Namespace.current().getNode(name);
	}
	
	@Override
	public List<String> getPackageList() {
		List<String> result = new ArrayList<String>();
		Package[] allPackages = PackageManager.getAllPackages();
		for (Package pkg : allPackages) {
			result.add(pkg.getName());
		}
		return result;
	}
}
