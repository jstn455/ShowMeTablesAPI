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

public class PostgreSQLConnectorImplTest {

	Database dbInfo;
	
	@Before
	public void setup(){
		dbInfo = new Database("smt_user@postgresql.cjrzsbwpstl2.us-east-1.rds.amazonaws.com:5432", "Demo", "smt_user", "smt_password", "postgresql.cjrzsbwpstl2.us-east-1.rds.amazonaws.com", "5432");
		dbInfo.setType(Database.POSTGRESQL_TYPE_CONSTANT);
	}
	
	@Test
	public void testGetDatabases(){
		DatabaseRequest request = new DatabaseRequest();
		request.setDbInfo(dbInfo);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		DatabasesJSON result = connector.getDatabases();
		Assert.assertEquals(3, result.getDatabases().size());
		Assert.assertEquals("rdsadmin", result.getDatabases().get(0).getName());
		Assert.assertEquals("Demo", result.getDatabases().get(2).getName());
	}
	
	@Test
	public void testGetQueryResultsFront() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM public.\"Track\"");
		request.setHandlePagination(true);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Composer",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("1", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("343719", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("50", result.getRecords().get(49).get("TrackId"));
		Assert.assertEquals("491885", result.getRecords().get(49).get("Milliseconds"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT COUNT(*) OVER() as smt_reserved_cnt, * FROM public.\"Track\" OFFSET 0 LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
		
	}
	
	@Test
	public void testGetQueryResultsMiddle() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM public.\"Track\"");
		request.setHandlePagination(true);
		request.setPage(8);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Composer",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("351", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("234553", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("400", result.getRecords().get(49).get("TrackId"));
		Assert.assertEquals("165982", result.getRecords().get(49).get("Milliseconds"));
		Assert.assertEquals("351 - 400", result.getShowing());
		Assert.assertEquals("SELECT COUNT(*) OVER() as smt_reserved_cnt, * FROM public.\"Track\" OFFSET 350 LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
	}
	
	@Test
	public void testGetQueryResultsLast() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM public.\"Track\"");
		request.setHandlePagination(true);
		request.setPage(71);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Composer",result.getColumns().get(5).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(3, result.getRecords().size());
		Assert.assertEquals("3501", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("66639", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("3503", result.getRecords().get(2).get("TrackId"));
		Assert.assertEquals("206005", result.getRecords().get(2).get("Milliseconds"));
		Assert.assertEquals("3501 - 3503", result.getShowing());
		Assert.assertEquals("SELECT COUNT(*) OVER() as smt_reserved_cnt, * FROM public.\"Track\" OFFSET 3500 LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
		
	}

	@Test
	public void testGetQueryResultsNoPagination() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT \"TrackId\", \"Milliseconds\" FROM public.\"Track\" WHERE \"GenreId\" = 1 AND \"Milliseconds\" < 150000");
		request.setHandlePagination(false);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(2,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(1).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(67, result.getRecords().size());
		Assert.assertEquals("346", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("127869", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("3101", result.getRecords().get(66).get("TrackId"));
		Assert.assertEquals("86987", result.getRecords().get(66).get("Milliseconds"));
		Assert.assertEquals(null, result.getShowing());
		Assert.assertEquals(null, result.getSql());
		Assert.assertEquals(0, result.getTotalCount());
	}
	@Test
	public void testGetQueryResultsNoResults() {
		QueryRequest request = new QueryRequest();
		request.setDbInfo(dbInfo);
		request.setSql("SELECT * FROM public.\"Track\" WHERE \"Name\"='BLAH'");
		request.setHandlePagination(true);
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(request.getDbInfo());
		QueryJSON result = connector.getQueryResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals(0, result.getRecords().size());
		Assert.assertEquals(0, result.getTotalCount());
	}
	
	
	
	@Test
	public void testGetTables() throws SQLException{
		dbInfo.setIncludeSystemTables(false);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		TablesJSON tables = connector.getTables("");
		Assert.assertEquals(11, tables.getTables().size());
		Assert.assertEquals(1, tables.getSchemas().size());
		Assert.assertEquals("Album", tables.getTables().get(0).getName());
		Assert.assertEquals("Invoice", tables.getTables().get(5).getName());
		Assert.assertEquals("public", tables.getTables().get(5).getSchemaName());
	}
	
	//Continue
	@Test
	public void testBrowseResultsFront() throws SQLException{
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(1);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("1", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("343719", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("50", result.getRecords().get(49).get("TrackId"));
		Assert.assertEquals("491885", result.getRecords().get(49).get("Milliseconds"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\"     LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsMiddle() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(8);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("351", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("234553", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("400", result.getRecords().get(49).get("TrackId"));
		Assert.assertEquals("165982", result.getRecords().get(49).get("Milliseconds"));
		Assert.assertEquals("351 - 400", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\"    OFFSET 350 LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsLast() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(71);
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(3, result.getRecords().size());
		Assert.assertEquals("3501", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("66639", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("3503", result.getRecords().get(2).get("TrackId"));
		Assert.assertEquals("206005", result.getRecords().get(2).get("Milliseconds"));
		Assert.assertEquals("3501 - 3503", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\"    OFFSET 3500 LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsSortAscending() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(71);
		request.setSortColumn("Composer");
		request.setSortOrder("ASC");
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(3, result.getRecords().size());
		Assert.assertEquals("2903", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("2635343", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("2905", result.getRecords().get(2).get("TrackId"));
		Assert.assertEquals("2610125", result.getRecords().get(2).get("Milliseconds"));
		Assert.assertEquals("3501 - 3503", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\"  ORDER BY \"Composer\" ASC  OFFSET 3500 LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsSortDescending() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(63);
		request.setSortColumn("Composer");
		request.setSortOrder("DESC");
		request.setPageSize(50);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("1892", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("527986", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("1490", result.getRecords().get(49).get("TrackId"));
		Assert.assertEquals("210259", result.getRecords().get(49).get("Milliseconds"));
		Assert.assertEquals("3101 - 3150", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\"  ORDER BY \"Composer\" DESC  OFFSET 3100 LIMIT 50", result.getSql());
		Assert.assertEquals(3503, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithOneCondition() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(1);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("MediaTypeId");
		cond.setOperator("=");
		cond.setValue("2");
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(50, result.getRecords().size());
		Assert.assertEquals("3503", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("206005", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("3449", result.getRecords().get(49).get("TrackId"));
		Assert.assertEquals("120000", result.getRecords().get(49).get("Milliseconds"));
		Assert.assertEquals("1 - 50", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\" WHERE  (\"MediaTypeId\" = ?)    LIMIT 50", result.getSql());
		Assert.assertEquals(237, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithOneConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(5);
		request.setPageSize(50);
		Condition cond = new Condition();
		cond.setColumn("MediaTypeId");
		cond.setOperator("=");
		cond.setValue("2");
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(37, result.getRecords().size());
		Assert.assertEquals("1205", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("467810", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("2", result.getRecords().get(36).get("TrackId"));
		Assert.assertEquals("342562", result.getRecords().get(36).get("Milliseconds"));
		Assert.assertEquals("201 - 237", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\" WHERE  (\"MediaTypeId\" = ?)   OFFSET 200 LIMIT 50", result.getSql());
		Assert.assertEquals(237, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithTwoConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(10);
		request.setPageSize(50);
		request.setSortColumn("TrackId");
		request.setSortOrder("DESC");
		Condition cond = new Condition();
		cond.setColumn("MediaTypeId");
		cond.setOperator("=");
		cond.setValue("2");
		cond.setConditionType("OR");
		Condition cond2 = new Condition();
		cond2.setColumn("MediaTypeId");
		cond2.setOperator("=");
		cond2.setValue("3");
		ArrayList<Condition> additionalConditions = new ArrayList<Condition>();
		additionalConditions.add(cond2);
		cond.setAdditionalConditions(additionalConditions);
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(1, result.getRecords().size());
		Assert.assertEquals("2", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("342562", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("451 - 451", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\" WHERE  (\"MediaTypeId\" = ? OR \"MediaTypeId\" = ?) ORDER BY \"TrackId\" DESC  OFFSET 450 LIMIT 50", result.getSql());
		Assert.assertEquals(451, result.getTotalCount());
	}
	
	@Test
	public void testBrowseResultsWithThreeConditionLastPage() {
		BrowseRequest request = new BrowseRequest();
		request.setDbInfo(dbInfo);
		request.setTableName("[public].Track");
		request.setPage(10);
		request.setPageSize(50);
		request.setSortColumn("TrackId");
		request.setSortOrder("DESC");
		Condition cond = new Condition();
		cond.setColumn("MediaTypeId");
		cond.setOperator("=");
		cond.setValue("2");
		cond.setConditionType("OR");
		Condition cond2 = new Condition();
		cond2.setColumn("MediaTypeId");
		cond2.setOperator("=");
		cond2.setValue("3");
		Condition cond3 = new Condition();
		cond3.setColumn("MediaTypeId");
		cond3.setOperator("=");
		cond3.setValue("4");
		ArrayList<Condition> additionalConditions = new ArrayList<Condition>();
		additionalConditions.add(cond2);
		additionalConditions.add(cond3);
		cond.setAdditionalConditions(additionalConditions);
		request.setConditions(cond);
		SQLConnector connector = SQLFactory.getConnector(dbInfo);
		QueryJSON result = connector.getBrowseResults(request);
		Assert.assertEquals(9,result.getColumns().size());
		Assert.assertEquals("TrackId",result.getColumns().get(0).getField());
		Assert.assertEquals("Milliseconds",result.getColumns().get(6).getField());
		Assert.assertEquals("", result.getMessage());
		Assert.assertEquals(8, result.getRecords().size());
		Assert.assertEquals("1149", result.getRecords().get(0).get("TrackId"));
		Assert.assertEquals("263893", result.getRecords().get(0).get("Milliseconds"));
		Assert.assertEquals("2", result.getRecords().get(7).get("TrackId"));
		Assert.assertEquals("342562", result.getRecords().get(7).get("Milliseconds"));
		Assert.assertEquals("451 - 458", result.getShowing());
		Assert.assertEquals("SELECT *, COUNT(*) OVER() as smt_reserved_cnt FROM public.\"Track\" WHERE  (\"MediaTypeId\" = ? OR \"MediaTypeId\" = ? OR \"MediaTypeId\" = ?) ORDER BY \"TrackId\" DESC  OFFSET 450 LIMIT 50", result.getSql());
		Assert.assertEquals(458, result.getTotalCount());
	}

	
	
}
