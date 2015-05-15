package com.showmetables.service.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.showmetables.json.webrequest.BrowseRequest;
import com.showmetables.json.webrequest.QueryRequest;
import com.showmetables.service.json.BaseJSON;
import com.showmetables.service.json.DatabasesJSON;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.json.TablesJSON;
import com.showmetables.service.model.Column;
import com.showmetables.service.model.Condition;
import com.showmetables.service.model.Database;
import com.showmetables.service.model.Table;

public interface SQLConnector {
	
	public BaseJSON connect(Database db);
	public void init(Database db);
	public TablesJSON getTables(String tableNamePattern);
	public ArrayList<Column> getColumns(String tableName) throws SQLException;
	public HashMap<String,Table> getTables(boolean getColumns, String tableNamePattern, boolean getSystemTables) throws SQLException;
	public WhereCriteria assembleWhere(Condition conditions,WhereCriteria wc);
	public QueryJSON getQueryResults( QueryRequest request);
	public QueryJSON getBrowseResults(BrowseRequest request);
	public QueryJSON getBrowseResults(String tableName, int page, int pageSize, String sortColumn, String sortOrder, Condition conditions);
	public DatabasesJSON getDatabases();
	public String assembleSelect(Table table,String where, int pageStart, int pageSize, String order);
	public void handlePages(Connection conn,Table t, String where,WhereCriteria wc, int page, int pageSize,QueryJSON json, String sql) throws SQLException;
	
}
