package org.sausage.grinder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sausage.model.adapter.AdapterConnection;
import org.sausage.model.adapter.AdapterService;
import org.sausage.model.document.Field;
import org.sausage.model.service.FlowService;
import org.sausage.model.service.ISService;
import org.sausage.model.service.JavaService;
import org.sausage.serializer.FricYamlMapperFactory;

import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.ACLManager;
import com.wm.app.b2b.server.BaseService;
import com.wm.app.b2b.server.FlowServiceFactory;
import com.wm.app.b2b.server.FlowSvcImpl;
import com.wm.app.b2b.server.JavaServiceFactory;
import com.wm.app.b2b.server.NSRecordFactory;
import com.wm.app.b2b.server.NodeFactory;
import com.wm.app.b2b.server.Package;
import com.wm.app.b2b.server.PackageFS;
import com.wm.app.b2b.server.Resources;
import com.wm.app.b2b.server.Server;
import com.wm.app.b2b.server.UserManager;
import com.wm.data.IData;
import com.wm.lang.ns.NSField;
import com.wm.lang.ns.NSName;
import com.wm.lang.ns.NSNode;
import com.wm.pkg.art.ns.AdapterServiceFactory;
import com.wm.pkg.art.ns.ConnectionDataNode;
import com.wm.pkg.art.ns.ConnectionDataNodeFactory;
import com.wm.util.Values;

public class GrindingTest {

	@BeforeClass
	public static void init() throws Exception {
		String root = "C:\\rappatriement\\temp\\testIS";
		boolean create = false;
		Resources gResources = new Resources(root, create);
		Server.setResources(gResources);
		UserManager.init();
		ACLManager.init();
	}

	@Test
	public void testFlow() throws Exception {

		NodeFactory serviceFactory = new FlowServiceFactory();
		NSNode nsNode = loadNsNode("AFS_PEGASE_to_Collective_v1.pub:callSMQS14LWithPivot", serviceFactory);
		FlowSvcImpl flowSvcImpl = (FlowSvcImpl) nsNode;
		flowSvcImpl.validate(); // <- will load the flow.xml !!!

		ISService service = ServiceGrinder.convert((BaseService) nsNode);
		Assert.assertTrue(service instanceof FlowService);

		// FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, service);
	}

	@Test
	public void testFlow97() throws Exception {

		NodeFactory serviceFactory = new FlowServiceFactory();
		NSNode nsNode = loadNsNode("AFS_SFO_TaskManager_v1.pub.cases:createCase", serviceFactory);
		FlowSvcImpl flowSvcImpl = (FlowSvcImpl) nsNode;
		flowSvcImpl.validate(); // <- will load the flow.xml !!!

		ISService service = ServiceGrinder.convert((BaseService) nsNode);
		Assert.assertTrue(service instanceof FlowService);

		FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, service);
	}

	@Test
	public void testJava() throws Exception {

		NodeFactory serviceFactory = new JavaServiceFactory();
		NSNode nsNode = loadNsNode("AFS_SFO_TaskManager_v1.priv.utils:substringAfterLast", serviceFactory);

		ISService service = ServiceGrinder.convert((BaseService) nsNode);
		Assert.assertTrue(service instanceof JavaService);

		// FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, service);
	}

	@Test
	public void testSalesforceAdp() throws Exception {

		NodeFactory serviceFactory = new AdapterServiceFactory();
		NSNode nsNode = loadNsNode("AFS_SFO_TaskManager_v1.adp.cases:upsertCase", serviceFactory);

		ISService service = ServiceGrinder.convert((BaseService) nsNode);
		Assert.assertTrue(service instanceof AdapterService);

		// FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, service);
	}

	@Test
	public void testSalesforceConnection() throws Exception {

		NodeFactory serviceFactory = new ConnectionDataNodeFactory();
		NSNode nsNode = loadNsNode("AFS_SFO_TaskManager_v1.adp:InterfaceSalesforce", serviceFactory);

		ConnectionDataNode conn = (ConnectionDataNode) nsNode;
		AdapterConnection connection = AdapterGrinder.convert(conn);

		FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, connection);
	}
	
	@Test
	public void testEntireX() throws Exception {

		NodeFactory serviceFactory = new AdapterServiceFactory();
		NSNode nsNode = loadNsNode("AFS_PEGASE_to_Collective_v1.adp:SMQS20L", serviceFactory);

		ISService service = ServiceGrinder.convert((BaseService) nsNode);
		Assert.assertTrue(service instanceof AdapterService);

		// FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, service);
	}

	@Test
	public void testDocType() throws Exception {

		NodeFactory serviceFactory = new NSRecordFactory();
		NSNode nsNode = loadNsNode("AFS_PEGASE_to_Collective_v1.doc.SMQS12L:PivotPerson", serviceFactory);

		Field field = TypeGrinder.convert((NSField) nsNode);
		Assert.assertNotNull(field);

		// FricYamlMapperFactory.getObjectMapper().writerWithDefaultPrettyPrinter().writeValue(System.out, field);
	}

	private NSNode loadNsNode(String nodeName, NodeFactory serviceFactory) {

		NSName name = NSName.create(nodeName);

		String packageName = name.getInterfacePath()[0].toString();

		Package pkg = new Package(packageName);
		PackageFS pFs = (PackageFS) pkg.getStore();
		IData ndfContent = pFs.getDescription(name);

		NSNode nsNode = serviceFactory.createFromNodeDef(pkg, name, (Values) ndfContent);
		return nsNode;
	}
}
