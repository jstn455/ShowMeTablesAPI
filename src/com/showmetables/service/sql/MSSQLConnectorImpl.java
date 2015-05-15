package com.showmetables.service.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.showmetables.service.helper.SQLHelper;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.model.Database;
import com.showmetables.service.model.Table;

public class MSSQLConnectorImpl extends BaseConnectorImpl implements
		SQLConnector {

	public MSSQLConnectorImpl() {

	}

	public MSSQLConnectorImpl(Database db) {

	}

	@Override
	public String assembleSelect(Table table,String where, int pageStart, int pageSize, String order){
		
		if (StringUtils.isEmpty(order)){
			Connection conn = null;
			ResultSet rs = null;
			try {
				conn = ds.getConnection();
				DatabaseMetaData md = conn.getMetaData();
				rs = md.getPrimaryKeys(table.getCatalogName(), table.getSchemaName(), table.getName());
				if (rs.next()) {
					order = "ORDER BY " + rs.getString(4);
				} else {
					order = "ORDER BY " + table.getColumns().get(0).getField();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SQLHelper.closeConnections(conn, null, null, rs);
			}
		}
		StringBuffer sql = new StringBuffer("SELECT * FROM (SELECT ROW_NUMBER() OVER ( " + order + " ) AS Smt_RowNum, *");
		sql.append(" FROM " + table.getFullName() + " " + where + " ) AS xtrainedResult ");
		int pageEnd = pageStart + pageSize;
		if (StringUtils.isEmpty(where)){
			sql.append(" WHERE Smt_RowNum > " + pageStart + " AND Smt_RowNum <= " + pageEnd + " ORDER BY Smt_RowNum");
		} else {
			sql.append(" WHERE (" + "Smt_RowNum > " + pageStart + " AND Smt_RowNum <= " + pageEnd + ") ORDER BY Smt_RowNum");
		}
		return sql.toString();
	}
	
	@Override
	public void handlePages(Connection conn,Table t, String where, WhereCriteria wc, int page, int pageSize,
			QueryJSON json, String sql) throws SQLException {
		PreparedStatement stmt;
		ResultSet rs;
		sql = "SELECT COUNT(*) FROM " + t.getFullName() + " " +  where;
		
		stmt = conn.prepareStatement(sql);
		for (int i = 0; i < wc.getParams().size(); i ++){
			stmt.setString(i + 1, wc.getParams().get(i).getValue());
		}
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
		json.setSql(StringUtils.replaceOnce(sql, "COUNT(*)", "*"));
	}
	
	

}
