package com.showmetables.service.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.showmetables.json.webrequest.QueryRequest;
import com.showmetables.service.helper.SQLHelper;
import com.showmetables.service.json.DatabasesJSON;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.model.Column;
import com.showmetables.service.model.Condition;
import com.showmetables.service.model.Database;
import com.showmetables.service.model.Table;

public class PostgreSQLConnectorImpl extends BaseConnectorImpl implements SQLConnector {


	
	
	@Override
	public QueryJSON getBrowseResults(String tableName, int page, int pageSize, String sortColumn, String sortOrder, Condition conditions){
		if (StringUtils.isNotBlank(sortColumn)) {
			sortColumn = "\"" + sortColumn + "\"";
		}
		if (conditions !=null && StringUtils.isNotEmpty(conditions.getColumn())) {
			conditions.setColumn("\"" + conditions.getColumn() + "\"");
			for (Condition c : conditions.getAdditionalConditions()){
				c.setColumn("\"" + c.getColumn() + "\"");
			}
		}
		
		return super.getBrowseResults(tableName, page, pageSize, sortColumn, sortOrder, conditions);
	}

	@Override
	public DatabasesJSON getDatabases() {
		ArrayList<Database> databases = new ArrayList<Database>();
		ResultSet rs = null;
		DatabasesJSON json = new DatabasesJSON();
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement("SELECT datname FROM pg_database WHERE datistemplate = false");
			rs = stmt.executeQuery();
			while (rs.next()) {
			    Database db = new Database();
			    db.setName(rs.getString("datname"));
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
	public QueryJSON getQueryResults(QueryRequest request) {
		int page = request.getPage();
		String sql;
		int pageSize = request.getPageSize();
		int pageStart = (page - 1) * pageSize ;
		if (request.isHandlePagination()) {
			sql = request.getSql() + " OFFSET " + pageStart + " LIMIT " + pageSize; 
			sql = sql.replaceFirst(Pattern.quote("SELECT"), "SELECT COUNT(*) OVER() as smt_reserved_cnt,");
		} else {
			sql = request.getSql();
		}
		request.setSql(sql);
		return super.getQueryResults(request);
	}

	@Override
	public String assembleSelect(Table table,String where, int pageStart, int pageSize, String order){
		StringBuffer limit = new StringBuffer("");
		if (pageStart > 0){
			limit.append("OFFSET " +pageStart);
		}
		limit.append(" LIMIT " + pageSize);
		String sql = "SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM " + table.getSchemaName() + ".\"" + table.getName() + "\" " + where + " "+ order + "  " + limit.toString();
		return sql;
	}
	
	@Override
	public void setParameter(PreparedStatement stmt, WhereCriteria wc, int i, ArrayList<Column> columns) throws SQLException {
		ColumnValuePair pair = wc.getParams().get(i);
		for (Column c : columns){
			if (c.getField().equalsIgnoreCase(pair.getColumn().replaceAll("\"", ""))){
				if ("INTEGER".equalsIgnoreCase(c.getType())) {
					stmt.setInt(i + 1, Integer.parseInt(pair.getValue()));
				} else if ("NUMERIC".equalsIgnoreCase(c.getType())) {
					stmt.setBigDecimal(i + 1,new BigDecimal(pair.getValue()));
				} else {
					stmt.setString(i + 1, pair.getValue());
				}
				return;
			}
		}
		stmt.setString(i + 1, pair.getValue());
	}

}
