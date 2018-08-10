package org.sausage.grinder.util;

import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Set;

import com.wm.data.IData;
import com.wm.data.IDataCursor;

/**
 * Just an extremely basic IData -> Map wrapper. <p>
 * Completely slow and unoptimized. Different to {@link com.softwareag.util.IDataMap} because :
 * <ul> readonly </ul>
 * <ul> recursively transform all IData child structure to an IDataMap </ul>
 * 
 * Created in order to easily serialize an IData value with jackson.
 * 
 * @author S620024
 *
 */
public class SimpleIDataMap extends AbstractMap<String, Object> {

	private IData idata;

	public SimpleIDataMap(IData idata) {
		super();
		this.idata = idata;
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {

		// LinkedHashSet to preserve insertion order !
		Set<Entry<String, Object>> result = new LinkedHashSet<Entry<String, Object>>();

		IDataCursor inCursor = idata.getCursor();

		while (inCursor.next()) {
			final Object value = ValueGrinder.convert(inCursor.getValue());

			SimpleEntry<String, Object> entry = new SimpleEntry<String, Object>(inCursor.getKey(), value);
			result.add(entry);
		}

		return result;
	}

}
