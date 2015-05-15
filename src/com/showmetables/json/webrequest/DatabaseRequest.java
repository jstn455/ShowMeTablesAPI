package com.showmetables.json.webrequest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.showmetables.service.model.Database;
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatabaseRequest {

	Database dbInfo;

	public DatabaseRequest() {
		
	}

	public Database getDbInfo() {
		return dbInfo;
	}

	public void setDbInfo(Database dbInfo) {
		this.dbInfo = dbInfo;
	}
}
