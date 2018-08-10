package org.sausage.stuffer;

import java.util.List;

import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;

public interface MeatProvider {

	List<NSName> getNodeNames(String packageName);

	NSNode getNode(String packageName, NSName name);

	List<String> getPackageList();

}