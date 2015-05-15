package com.showmetables.service.model;

import java.util.ArrayList;

public class Schema {

	private String name = "";
	private ArrayList<Table> tables = new ArrayList<Table>();
	
	public Schema() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Table> getTables() {
		return tables;
	}

	public void setTables(ArrayList<Table> tables) {
		this.tables = tables;
	}
}
