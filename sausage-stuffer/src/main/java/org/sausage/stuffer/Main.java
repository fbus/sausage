package org.sausage.stuffer;

import java.io.File;
import java.util.List;

import org.sausage.model.Asset;
import org.sausage.offline.util.OfflineInitializer;
import org.sausage.serializer.FricYamlMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

	public static void main(String[] args) throws Exception {
		String serverRootDir = "C:\\rappatriement\\temp\\testIS";
		OfflineInitializer.setupWmConstants(serverRootDir);
		
		ObjectMapper objectMapper = FricYamlMapperFactory.getObjectMapper();
		
		MeatProvider meatProvider = new OfflineMeatProvider();
		SausageStuffer sausageStuffer = new SausageStuffer(meatProvider);
		
		List<String> packageList = sausageStuffer.getPackageList();
		for (String packageName : packageList) {
			System.out.println(packageName);
			List<String> nodeNames = sausageStuffer.getAllAssetNamesFor(packageName);
			for (String nsName : nodeNames) {
				Asset asset = sausageStuffer.get(packageName, nsName);
				if(asset == null) {
					System.out.println(nsName + " not loaded ?");
					continue;
				}
				File resultFile = new File("c:/temp/fricyaml/" + asset.getName().replace(':', '.') + ".yml");
				try {
					objectMapper.writeValue(resultFile, asset);
				} catch (Throwable e) {
					System.out.println(resultFile + " " + e.getMessage());
				}
					
			}
		}
	}

}
