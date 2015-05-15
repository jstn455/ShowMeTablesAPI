package com.showmetables.test.functional;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.showmetables.json.webrequest.BrowseRequest;
import com.showmetables.json.webrequest.DatabaseRequest;
import com.showmetables.json.webrequest.QueryRequest;
import com.showmetables.service.json.DatabasesJSON;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.json.TablesJSON;
import com.showmetables.service.model.Condition;
import com.showmetables.service.model.Database;
import com.showmetables.service.sql.SQLConnector;
import com.showmetables.service.sql.SQLFactory;

public class MSSQLConnectorImplTest {

	Database dbInfo;
	
	@Before
	public void setup(){
		dbInfo = new Database("smt_user@sqlserver.cjrzsbwpstl2.us-east-1.rds.amazonaws.com:1433", "Demo", "smt_user", "smt_password", "sqlserver.cjrzsbwpstl2.us-east-1.rds.amazonaws.com", "1433");
		dbInfo.setType(Database.MSSQL_TYPE_CONSTANT);
	}
	
	@Test
	public void testGetDatabases(){
		DatabaseRequest request = new DatabaseRequest();
		request.setDbInfo(dbInfo);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		DatabasesJSON result = connector.getDatabases();
		Assert.assertEquals(7, result.getDatabases().size());
		Assert.assertEquals("Demo", result.getDatabases().get(0).getName());
		Assert.assertEquals("rdsadmin", result.getDatabases().get(4).getName());
	}
	
	@Test
	public void testGetQueryResults() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM [HumanResources].Employee");
		request.setHandlePagination(false);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(290, result.getRecords().size());
		Assert.assertEquals("1", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\ken0", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("50", result.getRecords().get(49).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\sidney0", result.getRecords().get(49).get("LoginID"));
		Assert.assertEquals(null, result.getShowing());
		Assert.assertEquals(null, result.getSql());
		Assert.assertEquals(0, result.getTotalCount());
		
	}
	
	@Test
	public void testGetQueryResultsNoResults() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM [HumanResources].Employee WHERE LoginID='Blah'");
		request.setHandlePagination(false);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals(0, result.getRecords().size());
		Assert.assertEquals(0, result.getTotalCount());
	}
	
	@Test
	public void testGetTables() throws SQLException{
		dbInfo.setIncludeSystemTables(true);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		TablesJSON tables = connector.getTables("");
		Assert.assertEquals(91, tables.getTables().size());
		Assert.assertEquals(6, tables.getSchemas().size());
		Assert.assertEquals("AWBuildVersion", tables.getTables().get(0).getName());
		Assert.assertEquals("dbo", tables.getTables().get(0).getSchemaName());
		Assert.assertEquals("EmployeeDepartmentHistory", tables.getTables().get(5).getName());
		Assert.assertEquals("HumanResources", tables.getTables().get(5).getSchemaName());
	}
	
	@Test
	public void testGetTablesWithoutSystemTables() throws SQLException{
		dbInfo.setIncludeSystemTables(false);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		TablesJSON tables = connector.getTables("");
		Assert.assertEquals(91, tables.getTables().size());
		Assert.assertEquals(6, tables.getSchemas().size());
		Assert.assertEquals("AWBuildVersion", tables.getTables().get(0).getName());
		Assert.assertEquals("dbo", tables.getTables().get(0).getSchemaName());
		Assert.assertEquals("EmployeeDepartmentHistory", tables.getTables().get(5).getName());
		Assert.assertEquals("HumanResources", tables.getTables().get(5).getSchemaName());
	}
	
	//CONTINUE
	@Test
	public void testBrowseResultsFront() throws SQLException{
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("1", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\ken0", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("50", result.getRecords().get(49).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\sidney0", result.getRecords().get(49).get("LoginID"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee ", result.getSql());
		Assert.assertEquals(290, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsMiddle() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(4);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("151", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\rostislav0", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("200", result.getRecords().get(49).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\frank0", result.getRecords().get(49).get("LoginID"));
		Assert.assertEquals("151 - 200", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee ", result.getSql());
		Assert.assertEquals(290, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsLast() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(6);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(40, result.getRecords().size());
		Assert.assertEquals("251", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\mikael0", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("290", result.getRecords().get(39).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\ranjit0", result.getRecords().get(39).get("LoginID"));
		Assert.assertEquals("251 - 290", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee ", result.getSql());
		Assert.assertEquals(290, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsSortAscending() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(3);
		request.setSortColumn("LoginID");
		request.setSortOrder("ASC");
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("237", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\hao0", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("105", result.getRecords().get(49).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\kevin2", result.getRecords().get(49).get("LoginID"));
		Assert.assertEquals("101 - 150", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee ", result.getSql());
		Assert.assertEquals(290, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsSortDescending() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(3);
		request.setSortColumn("LoginID");
		request.setSortOrder("DESC");
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("64", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\michael7", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("220", result.getRecords().get(49).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\karen0", result.getRecords().get(49).get("LoginID"));
		Assert.assertEquals("101 - 150", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee ", result.getSql());
		Assert.assertEquals(290, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithOneCondition() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(1);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("OrganizationLevel");
		cond.setOperator(">");
		cond.setValue("3");
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("8", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\diane1", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("77", result.getRecords().get(49).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\merav0", result.getRecords().get(49).get("LoginID"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee WHERE  (OrganizationLevel > ?)", result.getSql());
		Assert.assertEquals(190, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithOneConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(4);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("OrganizationLevel");
		cond.setOperator(">");
		cond.setValue("3");
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(40, result.getRecords().size());
		Assert.assertEquals("193", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\alejandro0", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("261", result.getRecords().get(39).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\reinout0", result.getRecords().get(39).get("LoginID"));
		Assert.assertEquals("151 - 190", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee WHERE  (OrganizationLevel > ?)", result.getSql());
		Assert.assertEquals(190, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithTwoConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(1);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("OrganizationLevel");
		cond.setOperator(">");
		cond.setValue("3");
		cond.setConditionType("AND");
		Condition cond2 = new Condition();
		cond2.setColumn("JobTitle");
		cond2.setOperator("LIKE");
		cond2.setValue("%WC40%");
		ArrayList<Condition> additionalConditions = new ArrayList<Condition>();
		additionalConditions.add(cond2);
		cond.setAdditionalConditions(additionalConditions);
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(26, result.getRecords().size());
		Assert.assertEquals("79", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\eric1", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("204", result.getRecords().get(25).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\gabe0", result.getRecords().get(25).get("LoginID"));
		Assert.assertEquals("1 - 26", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee WHERE  (OrganizationLevel > ? AND JobTitle LIKE ?)", result.getSql());
		Assert.assertEquals(26, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithThreeConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[HumanResources].Employee");
		request.setPage(1);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("OrganizationLevel");
		cond.setOperator(">");
		cond.setValue("3");
		cond.setConditionType("AND");
		Condition cond2 = new Condition();
		cond2.setColumn("JobTitle");
		cond2.setOperator("LIKE");
		cond2.setValue("%WC40%");
		Condition cond3 = new Condition();
		cond3.setColumn("HireDate");
		cond3.setOperator("=");
		cond3.setValue("2004-02-25");
		ArrayList<Condition> additionalConditions = new ArrayList<Condition>();
		additionalConditions.add(cond2);
		additionalConditions.add(cond3);
		cond.setAdditionalConditions(additionalConditions);
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(16,result.getColumns().size());
		Assert.assertEquals("BusinessEntityID",result.getColumns().get(0).getField());
		Assert.assertEquals("LoginID",result.getColumns().get(2).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(1, result.getRecords().size());
		Assert.assertEquals("79", result.getRecords().get(0).get("BusinessEntityID"));
		Assert.assertEquals("adventure-works\\eric1", result.getRecords().get(0).get("LoginID"));
		Assert.assertEquals("1 - 1", result.getShowing());
		Assert.assertEquals("SELECT * FROM [HumanResources].Employee WHERE  (OrganizationLevel > ? AND JobTitle LIKE ? AND HireDate = ?)", result.getSql());
		Assert.assertEquals(1, result.getTotalCount());
	}

	
	
}
