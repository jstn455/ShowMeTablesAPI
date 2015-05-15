package com.showmetables.json.webrequest;

public class QueryRequest extends DatabaseRequest{
	
	String sql;
	int page;
	int pageSize;
	boolean handlePagination;

	public QueryRequest() {
		
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
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

	public boolean isHandlePagination() {
		return handlePagination;
	}

	public void setHandlePagination(boolean handlePagination) {
		this.handlePagination = handlePagination;
	}
}
