package org.sausage.grinder.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wm.data.IData;
import com.wm.data.IDataCursor;

/**
 * Just an extremely basic IData -> Map wrapper. Completely slow and
 * unoptimized.
 * <p>
 * Created in order to easily serialize an IData value with jackson.
 * 
 * @author S620024
 *
 */
public class IDataMap extends AbstractMap<String, Object> {

	private IData idata;

	public IDataMap(IData idata) {
		super();
		this.idata = idata;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {

		Set<Entry<String, Object>> result = new HashSet<Entry<String, Object>>();

		IDataCursor inCursor = idata.getCursor();

		while (inCursor.next()) {
			Object obj = inCursor.getValue();
			final Object value;

			if (obj == null) {
				value = null;
			} else if (obj instanceof IData) {
				IData child = (IData) obj;
				value = new IDataMap(child);
			} else if (obj instanceof IData[]) {
				IData[] children = (IData[]) obj;
				List<IDataMap> nodeList = new ArrayList<IDataMap>();
				for (int i = 0; i < children.length; i++) {
					IData elt = children[i];
					IDataMap eltAsMap = elt == null ? null : new IDataMap(elt);
					nodeList.add(eltAsMap);
				}
				value = nodeList;
			} else {
				value = obj;
			}
			SimpleEntry<String, Object> entry = new SimpleEntry<String, Object>(inCursor.getKey(), value);
			result.add(entry);
		}

		return result;
	}

}
