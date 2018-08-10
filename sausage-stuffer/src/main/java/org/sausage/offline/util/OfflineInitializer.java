package org.sausage.offline.util;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wm.app.b2b.server.ACLManager;
import com.wm.app.b2b.server.NodeFactory;
import com.wm.app.b2b.server.NodeMaster;
import com.wm.app.b2b.server.OutboundPasswordManager;
import com.wm.app.b2b.server.PackageManagerHack;
import com.wm.app.b2b.server.Resources;
import com.wm.app.b2b.server.Server;
import com.wm.app.b2b.server.UserManager;
import com.wm.app.b2b.server.ns.NSLockManager;
import com.wm.lang.ns.NSWSDescriptor;
import com.wm.passman.PasswordManager;
import com.wm.passman.PasswordManagerException;
import com.wm.pkg.art.isproxy.Config;

public class OfflineInitializer {

	private static final Logger LOG = LogManager.getLogger(OfflineInitializer.class);

	private static final String NODE_FACTORY_CONFIG = "/node-factories.yml";

	public static void setupWmConstants(String serverRootDir) throws Exception {
		if (Server.isRunning()) {
			throw new IllegalStateException("can't use this class in a running IS instance !");
		} else if (Server.getResources() != null) {
			if(!Server.getResources().getRootDir().getAbsolutePath().equals(new File(serverRootDir).getAbsolutePath())) {
				throw new IllegalStateException("Can't init with a different server root dir");
			}
			return;
		}

		boolean create = false;
		Resources gResources = new Resources(serverRootDir, create);
		Server.setResources(gResources);
		UserManager.init();
		ACLManager.init();

		// avoid some init NPE logged when a service is loaded.
		Config.setProperty(NSLockManager.KEY_NS_LOCK_PROPERTY, NSLockManager.NO_LOCKING);

		mockPasswordManager();
		
		
		registerFactories();

		// needed to avoid useless calls to JMSSubsystem that would try to init some derby DB
		PackageManagerHack.setOffline();

		allowNewerWsdVersions();

	}

	/**
	 * when loading a WSD descriptor, NSWSDescriptor.Version.valueOf is called. You need to have an up to date lib in order
	 * to parse it. Let's lift this restriction (:
	 */
	private static void allowNewerWsdVersions() {
		try {
			// https://en.wikipedia.org/wiki/WebMethods_Integration_Server#Release_history
			EnumHack.unsafelyAddEnumValues(NSWSDescriptor.Version.class, //
					"_9_0", "_9_5", "_9_6", "_9_7", "_9_8", "_9_9", "_9_10", "_9_12", "_10_0", "_10_1");
		} catch (Throwable e) {
			LOG.warn("Failed to hack NSWSDescriptor.Version values :'(", e);
		}
	}

	/**
	 * Not really necessary, but avoid some _caught_ NPE when loading an adapter connection.
	 * 
	 * @throws PasswordManagerException
	 */
	private static void mockPasswordManager() throws PasswordManagerException {
		@SuppressWarnings("rawtypes")
		Class[] interfaces = new Class[] { PasswordManager.class };
		PasswordManager proxy = (PasswordManager) Proxy.newProxyInstance(OfflineInitializer.class.getClassLoader(), interfaces,
				new InvocationHandler() {

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return method.getReturnType().isPrimitive() ? true : null;
					}
				});
		OutboundPasswordManager.init(proxy);
	}

	/**
	 * Load default list of {@link NodeFactory} in {@link NodeMaster}
	 * <p>
	 * The list is available in the {@link #NODE_FACTORY_CONFIG} file.
	 */
	public static void registerFactories() {

		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		InputStream src = OfflineInitializer.class.getResourceAsStream(NODE_FACTORY_CONFIG);
		if (src == null) {
			throw new IllegalArgumentException(NODE_FACTORY_CONFIG + " not found.");
		}
		final Map<String, String> factories;
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> value = objectMapper.readValue(src, Map.class);
			factories = value;
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed to read " + NODE_FACTORY_CONFIG, e);
		}

		registerFactories(factories);
	}

	/**
	 * Load specified list of {@link NodeFactory} in {@link NodeMaster}
	 */
	public static void registerFactories(Map<String, String> factories) {
		Set<Entry<String, String>> entrySet = factories.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String name = entry.getKey();
			String className = entry.getValue();
			NodeFactory factory;
			if (LOG.isInfoEnabled()) {
				LOG.info("registering NodeFactory: " + entry);
			}
			try {
				@SuppressWarnings("unchecked")
				Class<? extends NodeFactory> factoryClass = (Class<? extends NodeFactory>) Class.forName(className);
				factory = factoryClass.newInstance();
			} catch (Exception e) {
				throw new IllegalStateException("Failed to instantiate " + className + " ?", e);
			}
			NodeMaster.registerFactory(name, factory);
		}

	}
}
