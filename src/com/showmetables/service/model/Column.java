package com.showmetables.service.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Column {

	private String field;
	private String type;
	private boolean nullAllowed;
	private String key;
	private String defaultValue;
	private String extra;
	
	public Column(String field, String type, boolean nullAllowed, String key, String defaultValue, String extra) {
		this.field = field;
		this.type = type;
		this.nullAllowed = nullAllowed;
		this.key = key;
		this.defaultValue = defaultValue;
		this.extra = extra;
	}
	public Column() {
		
	}
	public Column(String field){
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNullAllowed() {
		return nullAllowed;
	}

	public void setNullAllowed(boolean nullAllowed) {
		this.nullAllowed = nullAllowed;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return "Column [field=" + field + ", type=" + type + ", nullAllowed="
				+ nullAllowed + ", key=" + key + ", defaultValue="
				+ defaultValue + ", extra=" + extra + "]";
	}
	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
	
}
