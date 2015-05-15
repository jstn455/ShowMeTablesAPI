package com.showmetables.json.web;

import com.showmetables.json.webrequest.BrowseRequest;
import com.showmetables.json.webrequest.DatabaseRequest;
import com.showmetables.json.webrequest.QueryRequest;
import com.showmetables.service.json.BaseJSON;
import com.showmetables.service.json.DatabasesJSON;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.json.TablesJSON;
import com.showmetables.service.json.VersionJSON;
import com.showmetables.service.sql.SQLConnector;
import com.showmetables.service.sql.SQLFactory;

public class RequestManagerService implements RequestManager{

	public BaseJSON checkConnection(DatabaseRequest request) {
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		BaseJSON baseJSON = connector.connect(request.getDbInfo());
		return baseJSON;
	}

	public TablesJSON getTables(DatabaseRequest request) {
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		TablesJSON json = connector.getTables("");
		return json;
	}

	@Override
	public QueryJSON browse(BrowseRequest request) {
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON json = connector.getBrowseResults(request);
		return json;
	}

	@Override
	public QueryJSON query(QueryRequest request) {
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON json = connector.getQueryResults(request);
		return json;
	}

	@Override
	public DatabasesJSON getDatabases(DatabaseRequest request) {
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		DatabasesJSON json = connector.getDatabases();
		return json;
	}

	@Override
	public VersionJSON getVersion() {
		return new VersionJSON(3);
	}

}
