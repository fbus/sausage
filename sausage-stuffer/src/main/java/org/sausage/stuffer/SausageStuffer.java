package org.sausage.stuffer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.sausage.grinder.NsNodeGrinder;
import org.sausage.model.Asset;
import org.sausage.repository.SausageLoader;

import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;

/**
 * Stuff sausage on demand !
 * @author S620024
 *
 */
public class SausageStuffer implements SausageLoader {
	
	private final MeatProvider meatProvider;
	
	public SausageStuffer(MeatProvider meatProvider) {
		super();
		this.meatProvider = meatProvider;
	}

	@Override
	public Asset get(String name) {
		Validate.notNull(name, "name is required");
		String packageName = getPackageFromFullName(name);
		return get(packageName, name);
	}
	
	@Override
	public Asset get(String packageName, String name) {
		NSNode node = meatProvider.getNode(packageName , NSName.create(name));
		
		Asset asset = node == null ? null : NsNodeGrinder.convert(node);
		
		return asset;
	}
	
	@Override
	public <T extends Asset> T get(String name, Class<T> requiredType) {
		String packageName = getPackageFromFullName(name);
		return get(packageName, name, requiredType);
	}
	
	@Override
	public <T extends Asset> T get(String packageName, String name, Class<T> requiredType) {
		Asset asset = get(name);
		if(requiredType.isAssignableFrom(asset.getClass())) {
			@SuppressWarnings("unchecked")
			T restul = (T) asset;
			return (T) restul;
		} else {
			throw new IllegalArgumentException(name +" is not of the required type " + requiredType) ;
		}
	}
	
	@Override
	public List<String> getPackageList() {
		return meatProvider.getPackageList();
	}
	
	@Override
	public List<String> getAllAssetNamesFor(String packageName) {
		List<NSName> nsNames = meatProvider.getNodeNames(packageName);
		List<String> assetNames = new ArrayList<String>();
		for (NSName nsName : nsNames) {
			assetNames.add(nsName.getFullName());
		}
		
		return assetNames;
	}
	
	private String getPackageFromFullName(String assetName) {
		return StringUtils.substringBefore(assetName, ".");
	}

	@Override
	public Iterator<String> iterateOnAllAssets() {
		throw new NotImplementedException(); // TODO composite iterator.
	}
}
