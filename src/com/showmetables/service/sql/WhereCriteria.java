package com.showmetables.service.sql;

import java.util.ArrayList;

public class WhereCriteria {
	
	public String query = "";
	public ArrayList<ColumnValuePair> params = new ArrayList<ColumnValuePair>();
	
	public WhereCriteria(){
		
	}
	public WhereCriteria(String query, ArrayList<ColumnValuePair> params){
		this.query = query;
		this.params = params;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public ArrayList<ColumnValuePair> getParams() {
		return params;
	}
	public void setParams(ArrayList<ColumnValuePair> params) {
		this.params = params;
	}

}
