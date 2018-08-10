package org.sausage.model.adapter;

import java.util.Map;

import org.sausage.model.AbstractAsset;

public class AdapterListener extends AbstractAsset {

	public Map<String, Object> metadata;
	public String adapterTypeName;
	public boolean disabled;
}
