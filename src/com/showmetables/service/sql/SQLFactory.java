package com.showmetables.service.sql;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import com.showmetables.service.model.Database;

public class SQLFactory {

	private static Map<Database, BasicDataSource> map = new HashMap<Database, BasicDataSource>();
	public static SQLConnector getConnector(Database db) {
		//check cache
//		if (map.containsKey(db)) {
//			return map.get(db);
//		}
		switch (db.getType()){
			
			case Database.MYSQL_TYPE_CONSTANT :
				SQLConnector connector = new MySQLConnectorImpl();
				connector.init(db);
				return connector;
			case Database.MSSQL_TYPE_CONSTANT :
				SQLConnector msConnector = new MSSQLConnectorImpl();
				msConnector.init(db);
				return msConnector;
			case Database.POSTGRESQL_TYPE_CONSTANT :
				SQLConnector postgreConnector = new PostgreSQLConnectorImpl();
				postgreConnector.init(db);
				return postgreConnector;
			default:
				return null;
		}
	}
	
	public static BasicDataSource getDataSource(Database db){
		return map.get(db);
	}
	
	public static void putDataSource(Database db, BasicDataSource ds){
		map.put(db, ds);
	}
}
