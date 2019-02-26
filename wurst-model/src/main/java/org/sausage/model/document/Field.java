package org.sausage.model.document;

public class Field {
	
	public String name;
	public Type type;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + "]";
	}
}
