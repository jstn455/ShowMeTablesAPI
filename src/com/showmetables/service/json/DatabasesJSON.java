package com.showmetables.service.json;

import java.util.ArrayList;

import com.showmetables.service.model.Database;

public class DatabasesJSON extends BaseJSON{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8638195849766946665L;
	private ArrayList<Database> databases = new ArrayList<Database>();
	
	public DatabasesJSON(){
		
	}

	public ArrayList<Database> getDatabases() {
		return databases;
	}

	public void setDatabases(ArrayList<Database> databases) {
		this.databases = databases;
	}
}
