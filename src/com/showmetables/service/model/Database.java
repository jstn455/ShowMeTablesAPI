package com.showmetables.service.model;

import java.io.Serializable;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.showmetables.service.sql.SQLFactory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Database implements Serializable{
	
	/**
	 * 
	 */
	public static final String MYSQL_TYPE_CONSTANT = "1";
	public static final String MSSQL_TYPE_CONSTANT = "2";
	public static final String POSTGRESQL_TYPE_CONSTANT = "3";
	private static final long serialVersionUID = -3733485755838608690L;
	private String nickname;
	private String name = "";
	private String username = "";
	private String password = "";
	private String host = "";
	private String port = "";
	private String type = "";
	private boolean includeSystemTables = false;
	private ArrayList<String> schemaIgnoreList = new ArrayList<String>();
	private boolean ignoreSysAndInformationSchema = true;
	
	public Database (String nickname, String name, String username, String password, String host, String port) {
		this.nickname = nickname;
		this.name = name;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}
	public Database() {
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public DataSource createDataSource() {
		BasicDataSource dataSource =SQLFactory.getDataSource(this);
		if (this.ignoreSysAndInformationSchema && MSSQL_TYPE_CONSTANT.equals(getType())) {
			this.schemaIgnoreList.add("sys");
			this.schemaIgnoreList.add("information_schema");
		}
		if (dataSource == null) {
			dataSource = new BasicDataSource();
		} else {
			return dataSource;
		}
		String dbName = "";
		switch (getType()) {
			case MYSQL_TYPE_CONSTANT :
				dataSource.setDriverClassName("com.mysql.jdbc.Driver");
				if (!StringUtils.isBlank(name)) {
					dbName = "/" + name;
				}
				dataSource.setUrl("jdbc:mysql://" + host + ":" + port + dbName);
				break;
			case MSSQL_TYPE_CONSTANT :
				dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				if (!StringUtils.isBlank(name)) {
					dbName = ";DatabaseName=" + name;
				}
				dataSource.setUrl("jdbc:sqlserver://" + host + ":" + port + dbName);
				break;
			case POSTGRESQL_TYPE_CONSTANT :
				dataSource.setDriverClassName("org.postgresql.Driver");
				if (!StringUtils.isBlank(name)) {
					dbName = "/" + name;
				} else {
					dbName = "/" + "postgres";
				}
				dataSource.setUrl("jdbc:postgresql://" + host + ":" + port +  dbName );
			default:
				break;
		}


		dataSource.setUsername(username);
		dataSource.setPassword(password);
		SQLFactory.putDataSource(this, dataSource);
		return dataSource;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(55, 33).
	            append(host).
	            append(password).
	            append(port).
	            append(type).
	            append(username).
	            append(name).
	            toHashCode();
	}
	@Override
	public boolean equals(Object obj) {
		Database d2 = (Database) obj;
		try {
			return (d2.getHost().equals(host)
					&& d2.getPassword().equals(password)
					&& d2.getPort().equals(port)
					&& d2.getType().equals(type)
					&& d2.getUsername().equals(username)
					&& d2.getName().equals(name)
					);
		} catch (NullPointerException e ) {
			return false; 
		}
		
				
				
	}
	public boolean isMySQL(){
		return MYSQL_TYPE_CONSTANT.equals(getType());
	}
	public boolean isMSSQL() {
		return MSSQL_TYPE_CONSTANT.equals(getType());
	}
	public ArrayList<String> getSchemaIgnoreList() {
		return schemaIgnoreList;
	}
	public void setSchemaIgnoreList(ArrayList<String> schemaIgnoreList) {
		this.schemaIgnoreList = schemaIgnoreList;
	}
	public boolean isIgnoreSysAndInformationSchema() {
		return ignoreSysAndInformationSchema;
	}
	public void setIgnoreSysAndInformationSchema(
			boolean ignoreSysAndInformationSchema) {
		this.ignoreSysAndInformationSchema = ignoreSysAndInformationSchema;
	}
	public boolean isIncludeSystemTables() {
		return includeSystemTables;
	}
	public void setIncludeSystemTables(boolean includeSystemTables) {
		this.includeSystemTables = includeSystemTables;
	}

}
