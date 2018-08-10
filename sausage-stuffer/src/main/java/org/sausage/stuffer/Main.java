package org.sausage.stuffer;

import java.io.File;
import java.util.List;

import org.sausage.grinder.NsNodeGrinder;
import org.sausage.model.Asset;
import org.sausage.offline.util.OfflineInitializer;
import org.sausage.serializer.FricYamlMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;

public class Main {

	public static void main(String[] args) throws Exception {
		String serverRootDir = "C:\\rappatriement\\temp\\testIS";
		OfflineInitializer.setupWmConstants(serverRootDir);
		
		ObjectMapper objectMapper = FricYamlMapperFactory.getObjectMapper();
		
		MeatProvider meatProvider = new OfflineMeatProvider();
		List<String> packageList = meatProvider.getPackageList();
		for (String packageName : packageList) {
			System.out.println(packageName);
			List<NSName> nodeNames = meatProvider.getNodeNames(packageName);
			for (NSName nsName : nodeNames) {
				NSNode node = meatProvider.getNode(packageName, nsName);
				if(node == null) {
					System.out.println(nsName);
					continue;
				}
				Asset asset;
				try {
					asset = NsNodeGrinder.convert(node);
				} catch (Exception e) {
					System.err.println("failed to convert " + node);
					e.printStackTrace();
					continue;
				}
				if(asset == null) {
					// TODO..
					continue;
				}
				
				File resultFile = new File("c:/temp/fricyaml/" + asset.getName().replace(':', '.') + ".yml");
				try {
					objectMapper.writeValue(resultFile, asset);
				} catch (Throwable e) {
					System.out.println(resultFile);
				}
					
			}
		}
	}

}
