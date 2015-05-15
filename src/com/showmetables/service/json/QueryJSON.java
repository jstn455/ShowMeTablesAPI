package com.showmetables.service.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.showmetables.service.model.Column;

public class QueryJSON extends BaseJSON implements Serializable{

	private static final long serialVersionUID = -4268219317812411369L;
	//private ArrayList<Record> records = new ArrayList<Record>();
	private ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
	private int totalCount;
	private String showing;
	private ArrayList<Column> columns = new ArrayList<Column>();
	private String sql;
	
	public QueryJSON(boolean success, String message) {
		super(success, message);
	}
	
	public QueryJSON() {
		super();
	}




	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getShowing() {
		return showing;
	}

	public void setShowing(String showing) {
		this.showing = showing;
	}

	public ArrayList<HashMap<String, String>> getRecords() {
		return records;
	}

	public void setRecords(ArrayList<HashMap<String, String>> records) {
		this.records = records;
	}

	public ArrayList<Column> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<Column> columns) {
		this.columns = columns;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
