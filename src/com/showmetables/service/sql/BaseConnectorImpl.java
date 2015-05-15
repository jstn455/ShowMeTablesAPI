package com.showmetables.service.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.cxf.common.util.StringUtils;

import com.showmetables.json.webrequest.BrowseRequest;
import com.showmetables.json.webrequest.QueryRequest;
import com.showmetables.service.helper.SQLHelper;
import com.showmetables.service.json.BaseJSON;
import com.showmetables.service.json.DatabasesJSON;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.json.TablesJSON;
import com.showmetables.service.model.Column;
import com.showmetables.service.model.Condition;
import com.showmetables.service.model.Database;
import com.showmetables.service.model.Schema;
import com.showmetables.service.model.Table;
import com.showmetables.service.model.TableComparator;

public class BaseConnectorImpl implements SQLConnector{
	Database db;
	DataSource ds;
	public BaseJSON connect(Database db) {
		
		BaseJSON json;
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (Exception e) {
			json = new BaseJSON(false, e.getMessage());
			SQLHelper.closeConnections(conn, null, null, null);
			return json;
		} finally {
			SQLHelper.closeConnections(conn, null, null, null);
		}
		json = new BaseJSON(true, "Connection was successful!");
		return json;
	}
	public void init(Database db) {
		DataSource ds = db.createDataSource();
		this.ds = ds;
		this.db = db;
	}
	
	public QueryJSON getBrowseResults(BrowseRequest request){
		return this.getBrowseResults(request.getTableName(), request.getPage(), request.getPageSize(), request.getSortColumn(), request.getSortOrder(), request.getConditions());
	}
		
	public QueryJSON getBrowseResults(String tableName, int page, int pageSize, String sortColumn, String sortOrder, Condition conditions){
		QueryJSON json = new QueryJSON();
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ds.getConnection();
			int pageStart = (page - 1) * pageSize;
			StringBuffer order = new StringBuffer("");
			StringBuffer where = new StringBuffer("");
			HashMap<String,Table> tables = getTables(true, tableName, db.isIncludeSystemTables());
//			if (!SQLHelper.tableAndColumnNameExists(tables, tableName, sortColumn)) {
//				//input was wrong, return false
//				SQLHelper.closeConnections(conn, null, stmt, null);
//				return null;
//			}
			
			if (!StringUtils.isEmpty(sortColumn)){
				order.append("ORDER BY ");
				order.append(sortColumn);
				if (!StringUtils.isEmpty(sortOrder) && "DESC".equals(sortOrder)) {
					order.append(" DESC");
				} else {
					order.append(" ASC");
				}
			}
			WhereCriteria wc = new WhereCriteria();
			if (conditions != null ) {
				wc = assembleWhere(conditions, null);
				where.append("WHERE " + wc.getQuery());
			}
			Table table = tables.get(tableName);
			String sql =  this.assembleSelect(table, where.toString(), pageStart, pageSize, order.toString());
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < wc.getParams().size(); i ++){
				setParameter(stmt, wc, i, table.getColumns());
			}
			handleResults(conn, page, pageSize, json, sql, stmt, true, true);
			handlePages(conn, table, where.toString(), wc, page, pageSize, json, sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLHelper.closeConnections(conn, null, stmt, null);
		}
		return json;
	}
	
	public void setParameter(PreparedStatement stmt, WhereCriteria wc, int i, ArrayList<Column> columns) throws SQLException {
		stmt.setString(i + 1, wc.getParams().get(i).getValue());
	}

	public TablesJSON getTables(String tableNamePattern) {
		ArrayList<Table> tableList = new ArrayList<Table>();
		tableList.addAll(getTables(true, tableNamePattern, db.isIncludeSystemTables()).values());
		Collections.sort(tableList, new TableComparator());
		TablesJSON json = new TablesJSON(tableList, true, "Tables loaded!");
		json.setSchemas(getSchemaList(tableList));
		return json;

	}
	private ArrayList<Schema> getSchemaList(ArrayList<Table> tables){
		ArrayList<Schema> list = new ArrayList<Schema>();
		for (int i = 0;i < tables.size(); i++){
			Table t = tables.get(i);
			if (!StringUtils.isEmpty(t.getSchemaName())) {
				Schema schema = new Schema();
				String schemaName = t.getSchemaName();
				schema.setName(schemaName);
				while (t.getSchemaName().equals(schemaName) && i < tables.size()) {
					schema.getTables().add(t);
					i++;
					if (i == tables.size()){
						break;
					}
					t = tables.get(i);
				}
				list.add(schema);
			}
		}
		return list;
	}

	public ArrayList<Column> getColumns(String tableName)
			throws SQLException {
		return null;
	}

	public HashMap<String,Table> getTables(boolean getColumns,String tableNamePattern, boolean getSystemTables) {
		HashMap<String,Table> tableMap = null;
		Connection conn = null;
		ResultSet rs = null;
		try {
			tableMap = new HashMap<String,Table>();
			conn = ds.getConnection();
			DatabaseMetaData md = conn.getMetaData();
			if (StringUtils.isEmpty(tableNamePattern)) {
				tableNamePattern = "%";
			} else {
				int cutOff = tableNamePattern.indexOf(".");
				if (cutOff >= 0) {
					tableNamePattern = tableNamePattern.substring(cutOff  + 1);
				}
				tableNamePattern = "%" + tableNamePattern + "%";
			}
			String[] types = new String[1];
			if (!getSystemTables) {
				types[0] = "TABLE";
			} else {
				types = null;
			}
			rs = md.getTables(this.db.getName(), null,tableNamePattern,types);
			while (rs.next()) {
				String catName = rs.getString(1);
				String schemaName = rs.getString(2);
				String tableName = rs.getString(3);
				if (!StringUtils.isEmpty(schemaName) && this.db.getSchemaIgnoreList().contains(schemaName.toLowerCase())) {
					continue;
				}
				Table table;
				if (getColumns) {
					table = new Table(tableName, schemaName, catName, getColumns(md,tableName, schemaName, catName));
				} else {
					table = new Table(tableName, schemaName, catName, null);
				}
				if (StringUtils.isEmpty(table.getSchemaName())) {
					tableMap.put(table.getName(), table);
				} else {
					tableMap.put("[" + table.getSchemaName() + "]." + table.getName(), table);
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLHelper.closeConnections(conn, null, null, rs);

		}
		return tableMap;
	}
		
	public ArrayList<Column> getColumns(DatabaseMetaData md, String tableName,String schemaName, String catName) {
		
		Connection conn = null;
		PreparedStatement columnStatement = null;
		ResultSet columnRs = null;
		ArrayList<Column> columnList = null;
		try {
			ResultSet rs = md.getColumns(catName, schemaName, tableName, null);
			columnList = new ArrayList<Column>();
			while (rs.next()) {
				String field = rs.getString(4);
				String type = getSqlTypeName(Integer.parseInt(rs.getString(5)));
				String nullAllowed = rs.getString(11);
				//String key = rs.getString(4);
				String defaultValue = rs.getString(13);
				String extra = rs.getString(23);
				columnList.add(new Column(field, type, Boolean
						.parseBoolean(nullAllowed), "", defaultValue, extra));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SQLHelper.closeConnections(conn, null, columnStatement, columnRs);
		}

		return columnList;
	}

	@Override
	public WhereCriteria assembleWhere(Condition conditions,WhereCriteria wc) {
		if (wc == null){
			wc = new WhereCriteria();
		}
		StringBuffer where = new StringBuffer("(");
		
		if (!conditions.hasMoreConditions()){
			ArrayList<Condition> totalConditions = conditions.getAdditionalConditions();
			totalConditions.add(0,conditions);
			for (int i = 0; i < totalConditions.size(); i++){
				Condition c = totalConditions.get(i);
				wc.getParams().add(new ColumnValuePair(c.getColumn(),c.getValue()));
				
				where.append(getColumnName(c) + " " + c.getOperator() + " ?");
				if ((i +1) != totalConditions.size()) {
					where.append(" " + conditions.getConditionType() + " ");
				}
			}
			where.append(")");
			wc.setQuery(wc.getQuery() + " " + where);
		} else {
			ArrayList<Condition> totalConditions = conditions.getAdditionalConditions();
			totalConditions.add(0,conditions);
			for (int i = 0; i < totalConditions.size(); i++){
				Condition c = totalConditions.get(i);
				if ((i +1) != totalConditions.size()) {
					wc = assembleWhere(c, wc);
				} else {
					wc.getParams().add(new ColumnValuePair(c.getColumn(),c.getValue()));
					where.append(c.getColumn() + " " + c.getOperator() + " ?");
				}
				if ((i +1) != totalConditions.size()) {
					where.append(" " + conditions.getConditionType() + " ");
				}
			}
			where.append(")");
			wc.setQuery(wc.getQuery() + " " + where.toString());
		}
		return wc;
	}

	public String getColumnName(Condition c) {
		return c.getColumn();
	}
	@Override
	public QueryJSON getQueryResults(QueryRequest request) {
		String sql = request.getSql();
		sql = sql.replaceAll(Pattern.quote(";"), "");
		QueryJSON json = new QueryJSON();
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement(sql);
			int page = request.getPage();
			int pageSize = request.getPageSize();
			boolean handlePaging = request.isHandlePagination();
			handleResults(conn, page, pageSize, json, sql, stmt, true, handlePaging);
			if (handlePaging && !db.getType().equals(Database.POSTGRESQL_TYPE_CONSTANT)) {
				handlePages(conn, null, null,null, page, pageSize, json, sql);
			}
		} catch (SQLException e) {
			json.setSuccess(false);
			json.setMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			SQLHelper.closeConnections(conn, null, stmt, null);
		}
		
		return json;
	}
	
	private void handleResults(Connection conn, int page, int pageSize, QueryJSON json,
			String sql, PreparedStatement stmt,boolean setColumns, boolean handlePagination) throws SQLException {
		ResultSet rs = stmt.executeQuery();
		boolean didPagination = false; //only for postgresql
		while (rs.next()){
			HashMap<String,String> map = new HashMap<String, String>();
			if (setColumns){
				setColumns = fillColumns(json, rs);
			}
			for (Column c : json.getColumns()) {
				map.put(c.getField(), rs.getString(c.getField()));
			}
			json.getRecords().add(map);
			if (handlePagination && !didPagination && db.getType().equals(Database.POSTGRESQL_TYPE_CONSTANT)) {
				json.setTotalCount(rs.getInt("smt_reserved_cnt"));
				int pageStart = (page - 1);
				if (pageStart >= 1 ) {
					pageStart = pageStart * pageSize + 1;
				} else {
					pageStart = 1;
				}
				int pageEnd = pageStart + pageSize - 1;
				pageEnd = json.getTotalCount() > pageEnd ? pageEnd : json.getTotalCount();
				json.setShowing(pageStart + " - " + pageEnd);
				didPagination = true;
				json.setSql(sql);
			}
		}
		if (setColumns){
			setColumns = fillColumns(json, rs);
		}
		json.setSuccess(true);
	}
	private boolean fillColumns(QueryJSON json, ResultSet rs)
			throws SQLException {
		boolean setColumns;
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i ++){
			if (!rsmd.getColumnName(i).equals("smt_reserved_cnt") && !rsmd.getColumnName(i).equals("Smt_RowNum")) {
				json.getColumns().add(new Column(rsmd.getColumnName(i)));
			}
		}
		setColumns = false;
		return setColumns;
	}
	public void handlePages(Connection conn,Table t, String where, int page, int pageSize,
			QueryJSON json, String sql) throws SQLException {
		return;
	}


	public DatabasesJSON getDatabases() {
		ArrayList<Database> databases = new ArrayList<Database>();
		ResultSet rs = null;
		DatabasesJSON json = new DatabasesJSON();
		Connection conn = null;
		try {
			conn = ds.getConnection();
			rs = conn.getMetaData().getCatalogs();
			while (rs.next()) {
			    Database db = new Database();
			    db.setName(rs.getString("TABLE_CAT"));
			    databases.add(db);
			}
			
			json.setSuccess(true);
			json.setDatabases(databases);
		} catch (SQLException e) {
			json.setSuccess(false);
			json.setMessage(e.getMessage());
			e.printStackTrace();
		} finally {
			SQLHelper.closeConnections(conn, null, null, rs);
		}
		
		
		
		return json;

	}
	@Override
	public String assembleSelect(Table table, String where,
			int pageStart, int pageSize, String order) {
		return null;
	}
	@Override
	public void handlePages(Connection conn, Table t, String where,
			WhereCriteria wc, int page, int pageSize, QueryJSON json, String sql)
			throws SQLException {
		// TODO Auto-generated method stub
	}
	
	private  String getSqlTypeName(int type) {
	    switch (type) {
	    case Types.BIT:
	        return "BIT";
	    case Types.TINYINT:
	        return "TINYINT";
	    case Types.SMALLINT:
	        return "SMALLINT";
	    case Types.INTEGER:
	        return "INTEGER";
	    case Types.BIGINT:
	        return "BIGINT";
	    case Types.FLOAT:
	        return "FLOAT";
	    case Types.REAL:
	        return "REAL";
	    case Types.DOUBLE:
	        return "DOUBLE";
	    case Types.NUMERIC:
	        return "NUMERIC";
	    case Types.DECIMAL:
	        return "DECIMAL";
	    case Types.CHAR:
	        return "CHAR";
	    case Types.VARCHAR:
	        return "VARCHAR";
	    case Types.LONGVARCHAR:
	        return "LONGVARCHAR";
	    case Types.DATE:
	        return "DATE";
	    case Types.TIME:
	        return "TIME";
	    case Types.TIMESTAMP:
	        return "TIMESTAMP";
	    case Types.BINARY:
	        return "BINARY";
	    case Types.VARBINARY:
	        return "VARBINARY";
	    case Types.LONGVARBINARY:
	        return "LONGVARBINARY";
	    case Types.NULL:
	        return "NULL";
	    case Types.OTHER:
	        return "OTHER";
	    case Types.JAVA_OBJECT:
	        return "JAVA_OBJECT";
	    case Types.DISTINCT:
	        return "DISTINCT";
	    case Types.STRUCT:
	        return "STRUCT";
	    case Types.ARRAY:
	        return "ARRAY";
	    case Types.BLOB:
	        return "BLOB";
	    case Types.CLOB:
	        return "CLOB";
	    case Types.REF:
	        return "REF";
	    case Types.DATALINK:
	        return "DATALINK";
	    case Types.BOOLEAN:
	        return "BOOLEAN";
	    case Types.ROWID:
	        return "ROWID";
	    case Types.NCHAR:
	        return "NCHAR";
	    case Types.NVARCHAR:
	        return "NVARCHAR";
	    case Types.LONGNVARCHAR:
	        return "LONGNVARCHAR";
	    case Types.NCLOB:
	        return "NCLOB";
	    case Types.SQLXML:
	        return "SQLXML";
	    }

	    return "?";
	}
}
