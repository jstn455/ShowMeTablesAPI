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
import com.showmetables.service.model.Column;
import com.showmetables.service.model.Condition;
import com.showmetables.service.model.Database;
import com.showmetables.service.sql.SQLConnector;
import com.showmetables.service.sql.SQLFactory;

public class MySQLConnectorImplTest {

	Database dbInfo;
	
	@Before
	public void setup(){
		dbInfo = new Database("smt_user@mysql.cjrzsbwpstl2.us-east-1.rds.amazonaws.com:3306", "Demo", "smt_user", "smt_password", "mysql.cjrzsbwpstl2.us-east-1.rds.amazonaws.com", "3306");
		dbInfo.setType(Database.MYSQL_TYPE_CONSTANT);
	}
	
	@Test
	public void testGetDatabases(){
		DatabaseRequest request = new DatabaseRequest();
		request.setDbInfo(dbInfo);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		DatabasesJSON result = connector.getDatabases();
		Assert.assertEquals(5, result.getDatabases().size());
		Assert.assertEquals("information_schema", result.getDatabases().get(0).getName());
		Assert.assertEquals("performance_schema", result.getDatabases().get(4).getName());
	}
	
	@Test
	public void testGetQueryResultsFront() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM employees");
		request.setHandlePagination(true);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("10001", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1986-06-26", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("10050", result.getRecords().get(49).get("emp_no"));
		Assert.assertEquals("1990-12-25", result.getRecords().get(49).get("hire_date"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT * FROM employees LIMIT 0, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
		
	}
	
	@Test
	public void testGetQueryResultsMiddle() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM employees");
		request.setHandlePagination(true);
		request.setPage(8);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("10351", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1985-03-07", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("10400", result.getRecords().get(49).get("emp_no"));
		Assert.assertEquals("1993-10-17", result.getRecords().get(49).get("hire_date"));
		Assert.assertEquals("351 - 400", result.getShowing());
		Assert.assertEquals("SELECT * FROM employees LIMIT 350, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
	}
	
	@Test
	public void testGetQueryResultsLast() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM employees");
		request.setHandlePagination(true);
		request.setPage(6001);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(24, result.getRecords().size());
		Assert.assertEquals("499976", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1988-12-26", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("499999", result.getRecords().get(23).get("emp_no"));
		Assert.assertEquals("1997-11-30", result.getRecords().get(23).get("hire_date"));
		Assert.assertEquals("300001 - 300024", result.getShowing());
		Assert.assertEquals("SELECT * FROM employees LIMIT 300000, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
		
	}

	@Test
	public void testGetQueryResultsNoPagination() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT emp_no, hire_date FROM employees WHERE first_name = 'Martial'");
		request.setHandlePagination(false);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(2,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(1).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(223, result.getRecords().size());
		Assert.assertEquals("11409", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1996-11-23", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("53653", result.getRecords().get(23).get("emp_no"));
		Assert.assertEquals("1986-10-22", result.getRecords().get(23).get("hire_date"));
		Assert.assertEquals(null, result.getShowing());
		Assert.assertEquals(null, result.getSql());
		Assert.assertEquals(0, result.getTotalCount());
	}
	@Test
	public void testGetQueryResultsNoResults() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM employees WHERE first_name = 'Martial' AND last_name='BLAH'");
		request.setHandlePagination(true);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals(0, result.getRecords().size());
		Assert.assertEquals(0, result.getTotalCount());
	}
	
	@Test
	public void testGetColumns() throws SQLException{
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		ArrayList<Column> columns = connector.getColumns("employees");
		Assert.assertEquals(6, columns.size());
		Assert.assertEquals("emp_no", columns.get(0).getField());
		Assert.assertEquals("hire_date", columns.get(5).getField());
	}
	
	@Test
	public void testGetTables() throws SQLException{
		dbInfo.setIncludeSystemTables(true);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		TablesJSON tables = connector.getTables("");
		Assert.assertEquals(6, tables.getTables().size());
		Assert.assertEquals(0, tables.getSchemas().size());
		Assert.assertEquals("departments", tables.getTables().get(0).getName());
		Assert.assertEquals("titles", tables.getTables().get(5).getName());
	}
	
	@Test
	public void testBrowseResultsFront() throws SQLException{
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("10001", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1986-06-26", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("10050", result.getRecords().get(49).get("emp_no"));
		Assert.assertEquals("1990-12-25", result.getRecords().get(49).get("hire_date"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` LIMIT 0, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsMiddle() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(8);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("10351", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1985-03-07", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("10400", result.getRecords().get(49).get("emp_no"));
		Assert.assertEquals("1993-10-17", result.getRecords().get(49).get("hire_date"));
		Assert.assertEquals("351 - 400", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` LIMIT 350, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsLast() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(6001);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(24, result.getRecords().size());
		Assert.assertEquals("499976", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1988-12-26", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("499999", result.getRecords().get(23).get("emp_no"));
		Assert.assertEquals("1997-11-30", result.getRecords().get(23).get("hire_date"));
		Assert.assertEquals("300001 - 300024", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` LIMIT 300000, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsSortAscending() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(6001);
		request.setSortColumn("first_name");
		request.setSortOrder("ASC");
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(24, result.getRecords().size());
		Assert.assertEquals("457742", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1986-10-20", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("71875", result.getRecords().get(23).get("emp_no"));
		Assert.assertEquals("1996-06-12", result.getRecords().get(23).get("hire_date"));
		Assert.assertEquals("300001 - 300024", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` ORDER BY first_name ASC LIMIT 300000, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsSortDescending() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(6001);
		request.setSortColumn("first_name");
		request.setSortOrder("DESC");
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(24, result.getRecords().size());
		Assert.assertEquals("447674", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1990-02-18", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("82975", result.getRecords().get(23).get("emp_no"));
		Assert.assertEquals("1989-08-23", result.getRecords().get(23).get("hire_date"));
		Assert.assertEquals("300001 - 300024", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` ORDER BY first_name DESC LIMIT 300000, 50", result.getSql());
		Assert.assertEquals(300024, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithOneCondition() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(1);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("last_name");
		cond.setOperator("=");
		cond.setValue("Facello");
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("10001", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1986-06-26", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("89589", result.getRecords().get(49).get("emp_no"));
		Assert.assertEquals("1987-08-28", result.getRecords().get(49).get("hire_date"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` WHERE (last_name = ?) LIMIT 0, 50", result.getSql());
		Assert.assertEquals(186, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithOneConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(4);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("last_name");
		cond.setOperator("=");
		cond.setValue("Facello");
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(36, result.getRecords().size());
		Assert.assertEquals("449566", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1989-04-25", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("493549", result.getRecords().get(35).get("emp_no"));
		Assert.assertEquals("1991-11-27", result.getRecords().get(35).get("hire_date"));
		Assert.assertEquals("151 - 186", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` WHERE (last_name = ?) LIMIT 150, 50", result.getSql());
		Assert.assertEquals(186, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithTwoConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(2);
		request.setPageSize(50);
		request.setSortColumn("emp_no");
		request.setSortOrder("DESC");
		Condition cond = new Condition();
		cond.setColumn("last_name");
		cond.setOperator("=");
		cond.setValue("Facello");
		cond.setConditionType("AND");
		Condition cond2 = new Condition();
		cond2.setColumn("birth_date");
		cond2.setOperator(">");
		cond2.setValue("1960-06-06");
		ArrayList<Condition> additionalConditions = new ArrayList<Condition>();
		additionalConditions.add(cond2);
		cond.setAdditionalConditions(additionalConditions);
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(16, result.getRecords().size());
		Assert.assertEquals("89589", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1987-08-28", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("12751", result.getRecords().get(15).get("emp_no"));
		Assert.assertEquals("1995-01-09", result.getRecords().get(15).get("hire_date"));
		Assert.assertEquals("51 - 66", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` WHERE (last_name = ? AND birth_date > ?) ORDER BY emp_no DESC LIMIT 50, 50", result.getSql());
		Assert.assertEquals(66, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithThreeConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("employees");
		request.setPage(1);
		request.setPageSize(50);
		request.setSortColumn("emp_no");
		request.setSortOrder("DESC");
		Condition cond = new Condition();
		cond.setColumn("last_name");
		cond.setOperator("=");
		cond.setValue("Facello");
		cond.setConditionType("AND");
		Condition cond2 = new Condition();
		cond2.setColumn("birth_date");
		cond2.setOperator(">");
		cond2.setValue("1960-06-06");
		Condition cond3 = new Condition();
		cond3.setColumn("gender");
		cond3.setOperator("=");
		cond3.setValue("M");
		ArrayList<Condition> additionalConditions = new ArrayList<Condition>();
		additionalConditions.add(cond2);
		additionalConditions.add(cond3);
		cond.setAdditionalConditions(additionalConditions);
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(6,result.getColumns().size());
		Assert.assertEquals("emp_no",result.getColumns().get(0).getField());
		Assert.assertEquals("hire_date",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(44, result.getRecords().size());
		Assert.assertEquals("485964", result.getRecords().get(0).get("emp_no"));
		Assert.assertEquals("1994-03-11", result.getRecords().get(0).get("hire_date"));
		Assert.assertEquals("12751", result.getRecords().get(43).get("emp_no"));
		Assert.assertEquals("1995-01-09", result.getRecords().get(43).get("hire_date"));
		Assert.assertEquals("1 - 44", result.getShowing());
		Assert.assertEquals("SELECT * FROM `employees` WHERE (last_name = ? AND birth_date > ? AND gender = ?) ORDER BY emp_no DESC LIMIT 0, 50", result.getSql());
		Assert.assertEquals(44, result.getTotalCount());
	}

	
	
}
