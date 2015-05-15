package com.showmetables.service.json;

import java.io.Serializable;
import java.util.ArrayList;

import com.showmetables.service.model.Schema;
import com.showmetables.service.model.Table;

public class TablesJSON extends BaseJSON implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1350505662256648189L;
	private ArrayList<Table> tables = new ArrayList<Table>();
	private ArrayList<Schema> schemas = new ArrayList<Schema>();
	public TablesJSON(boolean success, String message) {
		super(success, message);
	}
	public TablesJSON(ArrayList<Table> tables, boolean success, String message) {
		super(success, message);
		this.setTables(tables);
		this.setSuccess(success);
		this.setMessage(message);
	}
	public ArrayList<Table> getTables() {
		return tables;
	}
	public void setTables(ArrayList<Table> tables) {
		this.tables = tables;
	}
	public ArrayList<Schema> getSchemas() {
		return schemas;
	}
	public void setSchemas(ArrayList<Schema> schemas) {
		this.schemas = schemas;
	}
	
	

}
