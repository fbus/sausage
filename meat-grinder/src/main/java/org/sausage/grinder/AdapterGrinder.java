package org.sausage.grinder;

import org.sausage.grinder.util.SimpleIDataMap;
import org.sausage.model.adapter.AdapterConnection;
import org.sausage.model.adapter.AdapterListener;
import org.sausage.model.adapter.AdapterNotification;

import com.wm.data.IData;
import com.wm.pkg.art.ns.ConnectionDataNode;
import com.wm.pkg.art.ns.ListenerNode;
import com.wm.pkg.art.ns.NotificationNode;

public class AdapterGrinder {

    public static AdapterConnection convert(ConnectionDataNode node) {
		AdapterConnection result = new AdapterConnection();
		IData metadata = node.getConnectionProperties();
		result.metadata = new SimpleIDataMap(metadata);
		result.adapterTypeName = node.getAdapterTypeName();
		result.disabled = !node.isEnabled();
		return result;
    }

	public static AdapterListener convert(ListenerNode node) {
		AdapterListener result = new AdapterListener();
		IData metadata = node.getListenerProperties();
		result.metadata = new SimpleIDataMap(metadata);
		result.adapterTypeName = node.getAdapterTypeName();
		result.disabled = !node.getEnabledStatus();
		
		return result;
	}
	
	public static AdapterNotification convert(NotificationNode node) {
		AdapterNotification result = new AdapterNotification();
		IData metadata = node.getNotificationProperties();
		result.metadata = new SimpleIDataMap(metadata);
		result.adapterTypeName = node.getAdapterTypeName();
		result.disabled = !node.getEnabledStatus();
		return result;
	}
}
