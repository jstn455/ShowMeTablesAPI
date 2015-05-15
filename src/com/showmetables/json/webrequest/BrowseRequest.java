package com.showmetables.json.webrequest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.showmetables.service.model.Condition;
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrowseRequest extends DatabaseRequest{
//	var tableName = autoSQL.tableName;
//    var page = autoSQL.page;
//    var pageSize = autoSQL.pageSize;
//    var sortOrder = autoSQL.sortOrder;
//    var sortColumn = autoSQL.sortColumn;
//    var condition = autoSQL.setCondition;
	
	String tableName;
	int page;
	int pageSize;
	String sortOrder;
	String sortColumn;
	Condition conditions;
	
	public BrowseRequest(){
		
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public Condition getConditions() {
		return conditions;
	}

	public void setConditions(Condition conditions) {
		this.conditions = conditions;
	}



}
