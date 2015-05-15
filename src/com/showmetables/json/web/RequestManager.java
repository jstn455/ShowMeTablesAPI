package com.showmetables.json.web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.showmetables.json.webrequest.BrowseRequest;
import com.showmetables.json.webrequest.DatabaseRequest;
import com.showmetables.json.webrequest.QueryRequest;
import com.showmetables.service.json.BaseJSON;
import com.showmetables.service.json.DatabasesJSON;
import com.showmetables.service.json.QueryJSON;
import com.showmetables.service.json.TablesJSON;
import com.showmetables.service.json.VersionJSON;

@Consumes("application/json")
@Produces("application/json")
public interface RequestManager {

	@POST
	@Path("/checkConnection/")
	public BaseJSON checkConnection(DatabaseRequest request);
	
	@POST
	@Path("/getTables/")
	public TablesJSON getTables(DatabaseRequest request);
	
	@POST
	@Path("/browse/")
	public QueryJSON browse(BrowseRequest request);
	
	@POST
	@Path("/query/")
	public QueryJSON query(QueryRequest request);
	
	@POST
	@Path("/getDatabases")
	public  DatabasesJSON getDatabases(DatabaseRequest request);
	
	@GET
	@Path("/getVersion")
	public  VersionJSON getVersion();
}
