package org.sausage.grinder;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sausage.model.service.FlowService;
import org.sausage.model.service.ISService;
import org.sausage.offline.util.OfflineInitializer;
import org.sausage.serializer.FricYamlMapperFactory;
import org.sausage.stuffer.MeatProvider;
import org.sausage.stuffer.OfflineMeatProvider;

import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.FlowSvcImpl;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;

public class OfflineTest {


	@BeforeClass
	public static void init() throws Exception {
		String serverRootDir = "C:\\rappatriement\\temp\\testIS";
		OfflineInitializer.setupWmConstants(serverRootDir);
	}
	
	@Test
	public void testFlow() throws Exception {

		MeatProvider meatProvider = new OfflineMeatProvider();
		String packageName = "AFS_PEGASE_to_Collective_v1";
		NSName name = NSName.create("AFS_PEGASE_to_Collective_v1.priv.exception:checkStatus");
//		NSName name = NSName.create("AFS_PEGASE_to_Collective_v1.pub:callSMQS14LWithPivot");
		
		NSNode nsNode = meatProvider.getNode(packageName, name);
		
		FlowSvcImpl flowSvcImpl = (FlowSvcImpl) nsNode;
		flowSvcImpl.validate(); // <- will load the flow.xml !!!

		ISService service = ServiceGrinder.convert((BaseService) nsNode);
		Assert.assertTrue(service instanceof FlowService);

		FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, service);
	}

//	@Test
	public void testLoadAll() {
		MeatProvider meatProvider = new OfflineMeatProvider();
		List<String> packageList = meatProvider.getPackageList();
		for (String packageName : packageList) {
			List<NSName> nodeNames = meatProvider.getNodeNames(packageName);
			for (NSName nsName : nodeNames) {
				NSNode node = meatProvider.getNode(packageName, nsName);
				if(node == null) {
					System.out.println(nsName);
				}
			}
		}
	}

}
