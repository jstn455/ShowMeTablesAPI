package com.showmetables.service.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.cxf.common.util.StringUtils;

import com.showmetables.service.model.Column;
import com.showmetables.service.model.Table;

public class SQLHelper {

	public static boolean tableAndColumnNameExists(HashMap<String,Table> tables,
			String tableName, String columnName) {
		
		if (tables.containsKey(tableName)) {
			Table t = tables.get(tableName);
			if (!StringUtils.isEmpty(columnName)) {
				return columnNameExists(t.getColumns(), columnName);
			} else {
				return true;
			}
		}
		
		return false;
	}

	public static boolean columnNameExists(ArrayList<Column> columns,
			String columnName) {
		for (Column c : columns) {
			if (c.getField().equals(columnName)) {
				return true;
			}
		}
		return false;
	}

	public static void closeConnections(Connection conn, Statement stmt,
			PreparedStatement ps, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) { /* ignored */
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) { /* ignored */
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) { /* ignored */
				
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) { /* ignored */
			}
		}
	}
}
