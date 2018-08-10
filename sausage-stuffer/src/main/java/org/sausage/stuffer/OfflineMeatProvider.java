package org.sausage.stuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.wm.app.b2b.server.FlowSvcImpl;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageFS;
import com.wm.app.b2b.server.PackageManager;
import com.wm.app.b2b.server.PackageStore;
import com.wm.app.b2b.server.Server;
import com.wm.app.b2b.server.ServiceSetupException;
import com.wm.data.IData;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;
import com.wm.lang.ns.NSPackage;
import com.wm.util.Values;

public class OfflineMeatProvider implements MeatProvider {

	private static final Logger LOG = LogManager.getLogger(OfflineMeatProvider.class);

	@Override
	public List<NSName> getNodeNames(String packageName) {
		List<NSName> result = new ArrayList<NSName>();
		Package pkg = new Package(packageName);
		PackageFS pFs = (PackageFS) pkg.getStore();
		// IData ndfContent = pFs.getDescription(name);

		File nsDir = pFs.getNSDir();
		Collection<File> listFiles = FileUtils.listFiles(nsDir, new NameFileFilter(PackageStore.NDF_FILE), TrueFileFilter.INSTANCE);
		for (File file : listFiles) {
			final String assetFullName = getAssetFullName(nsDir, file);
			NSName nsName = NSName.create(assetFullName);
			result.add(nsName);
		}
		return result;
	}

	@Override
	public NSNode getNode(String packageName, NSName name) {

		Package pkg = new Package(packageName);
		PackageFS pFs = (PackageFS) pkg.getStore();
		IData ndfContent = pFs.getDescription(name);

		final NSNode nsNode;
		if (ndfContent != null) {
			nsNode = loadNode(pkg, name, (Values) ndfContent);

		} else {
			LOG.warn("invalid node.ndf file for " + name);
			nsNode = null;
		}
		return nsNode;
	}

	private NSNode loadNode(Package pkg, NSName name, Values ndfContent) {
		final NSNode nsNode;
		try {
			nsNode = PackageManager.createNode(pkg, name, (Values) ndfContent);
		} catch (ServiceSetupException e) {
			throw new IllegalArgumentException("can't load " + name, e);
		}
		
		if (nsNode == null && LOG.isDebugEnabled()) {
			LOG.debug("No corresponding NodeFactory registered in NodeMaster for node " + name);
		}
		
		postProcessNode(nsNode, pkg);

		return nsNode;
	}

	private void postProcessNode(final NSNode nsNode, NSPackage pkg) {
		if (nsNode instanceof FlowSvcImpl) {
			FlowSvcImpl flowSvc = (FlowSvcImpl) nsNode;
			try {
				flowSvc.validate(); // <- load the flow.xml !
			} catch (ServiceSetupException e) {
				LOG.warn("Failed to load flow.xml for " + nsNode.getNSName(), e);
			}
		}
		if (nsNode != null && nsNode.getPackage() == null) {
			// happen for all jms triggers and adapter connections. Dunno why...
			nsNode.setPackage(pkg);
		}
	}

	@Override
	public List<String> getPackageList() {
		String[] packageList = Server.getResources().getPackageList();
		return Arrays.asList(packageList);
	}

	private static String getAssetFullName(File nsDir, File ndfFile) {
		File assetFolder = ndfFile.getParentFile();
		String folderPath = nsDir.toURI().relativize(assetFolder.getParentFile().toURI()).toString();
		final String assetFullName;
		if (folderPath.isEmpty()) {
			assetFullName = assetFolder.getName();
		} else {
			assetFullName = folderPath.substring(0, folderPath.length() - 1).replace("/", ".") + ":" + assetFolder.getName();
		}
		return assetFullName;
	}

}
