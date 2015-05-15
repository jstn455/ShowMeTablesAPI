package com.showmetables.service.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.showmetables.json.webrequest.QueryRequest;
import com.showmetables.service.helper.SQLHelper;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.model.Column;
import com.showmetables.service.model.Database;
import com.showmetables.service.model.Table;

public class MySQLConnectorImpl extends BaseConnectorImpl implements SQLConnector{

	public MySQLConnectorImpl() {
		
	}
	public MySQLConnectorImpl(Database db) {
		
	}
	
	
	@Override
	public QueryJSON getQueryResults(QueryRequest request) {
		int page = request.getPage();
		int pageSize = request.getPageSize();
		boolean handlePagination = request.isHandlePagination();
		String sql = request.getSql();
		int pageStart = (page - 1) * pageSize;
		
		if (handlePagination){
			sql = request.getSql() + " LIMIT " + pageStart + ", " + pageSize; 
			sql = sql.replaceFirst(Pattern.quote("SELECT"), "SELECT SQL_CALC_FOUND_ROWS");
		} else {
			sql = request.getSql();
		}
		request.setSql(sql);
		QueryJSON json =  super.getQueryResults(request);
	
		return json;
	}
	
		
	
	public ArrayList<Column> getColumns(String tableName) {
		Connection conn = null;
		PreparedStatement columnStatement = null;
		ResultSet columnRs = null;
		ArrayList<Column> columnList = null;
		try {
			conn = ds.getConnection();
			columnStatement = null;
	    	String columnQuery = "SHOW COLUMNS FROM `" + tableName + "`";
	    	columnStatement = conn.prepareStatement(columnQuery);
	    	//columnStatement.setString(1, tableName);
	    	columnRs = columnStatement.executeQuery();
	    	columnList = new ArrayList<Column>();
	    	while (columnRs.next()){
	    		String field = columnRs.getString(1);
	    		String type = columnRs.getString(2);
	    		String nullALlowed = columnRs.getString(3);
	    		String key = columnRs.getString(4);
	    		String defaultValue = columnRs.getString(5);
	    		String extra = columnRs.getString(6);
	    		columnList.add(new Column(field, type, Boolean.parseBoolean(nullALlowed), key, defaultValue, extra));
	    	}
		} catch (SQLException e){
			
		} finally {
			SQLHelper.closeConnections(conn, null, columnStatement, columnRs);
		}
		
    	
    	return columnList;
	}
	
	@Override
	public String assembleSelect(Table table,String where, int pageStart, int pageSize, String order){
		String limit =" LIMIT " + pageStart + ", " + pageSize;
		String sql = "SELECT SQL_CALC_FOUND_ROWS * FROM `" + table.getName() + "` " + where + " "+ order + "  " + limit;
		return sql;
	}
	
	@Override
	public void handlePages(Connection conn, Table t, String where, WhereCriteria wc, int page, int pageSize,
			QueryJSON json, String sql) throws SQLException {
		PreparedStatement stmt;
		ResultSet rs;
		sql = sql.replace("SQL_CALC_FOUND_ROWS", "");
		while (sql.contains("  ")) {
			sql = (sql.replace("  ", " "));
		}
		json.setSql(sql);
		String foundSQL = "SELECT FOUND_ROWS()";
		stmt = conn.prepareStatement(foundSQL);
		rs = stmt.executeQuery();
		if (rs.next()) 
			json.setTotalCount(rs.getInt(1));
		if (page != 1) {
			page = pageSize * (page - 1) + 1;
		}
		stmt.close();
		rs.close();
		int pageEnd = page + pageSize - 1;
		pageEnd = json.getTotalCount() > pageEnd ? pageEnd : json.getTotalCount();
		json.setShowing(page + " - " + pageEnd);
	}
}
