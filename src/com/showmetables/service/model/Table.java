package com.showmetables.service.model;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Table {

	private String name = "";
	private String schemaName = "";
	private String catalogName = "";
	private ArrayList<Column> columns = new ArrayList<Column>();
	
	public Table(String name, ArrayList<Column> columns){
		this.setName(name);
		this.setColumns(columns);
	}
	public Table(String name,String schemaName, String catalogName, ArrayList<Column> columns){
		this.setName(name);
		this.schemaName = schemaName;
		this.catalogName = catalogName;
		this.setColumns(columns);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Column> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public String getFullName(){
		if (StringUtils.isEmpty(schemaName)) {
			return name;
		} else {
			return "[" + schemaName + "]." + name;
		}
		
	}
}
