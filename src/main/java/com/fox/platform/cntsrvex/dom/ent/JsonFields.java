package com.fox.platform.cntsrvex.dom.ent;

public enum JsonFields {
	HITS_OBJECT("hits"),
	HITS_ARRAY("hits"),
	INNER_HITS("inner_hits"),
	SOURCE("_source"),
	CODE("code"),
	GROUPS("groups"),
	FIELDS("fields"),
	NAME("name"),
    ID("id");
	private String fieldName;
	private JsonFields(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName(){
		return fieldName;
	}
}
