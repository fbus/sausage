package org.sausage.repository;

import java.util.Iterator;
import java.util.List;

import org.sausage.model.Asset;

/*
 * Not the perfect place to define this interface. But it's handy.<p>
 * Don't add dependencies for this.
 */
public interface SausageLoader {

	Asset get(String name);
	
	Asset get(String packageName, String name);
	
	<T extends Asset> T get(String name, Class<T> requiredType);
	
	<T extends Asset> T get(String packageName, String name, Class<T> requiredType);
	
	List<String> getPackageList();
	
	List<String> getAllAssetNamesFor(String packageName);
	
	Iterator<String> iterateOnAllAssets();
	
}
