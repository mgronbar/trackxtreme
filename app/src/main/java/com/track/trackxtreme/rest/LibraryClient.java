package com.track.trackxtreme.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.track.trackxtreme.data.track.Track;

@Consumes({ "application/json" })
@Produces({ "application/json" })
public interface LibraryClient {

	// testing with http://www.mocky.io/v2/57b4b92f0f00007f190c40fb
	@PUT
	@Path("/57b4b92f0f00007f190c40fb")
	public List<Track> getTracktest();

	@GET
	@Path("/track")
	public List<Track> getTracks();

	@GET
	@Path("/track/{isbn}")
	public Track getTrack(@PathParam("isbn") String id);

	@PUT
	@Path("/track/{isbn}")
	public Track addTrack(@PathParam("isbn") String id, @QueryParam("title") String title);

	@POST
	@Path("/track/{isbn}")
	public Track updateTrack(@PathParam("isbn") String id, String title);

	@DELETE
	@Path("/track/{isbn}")
	public Track removeTrack(@PathParam("isbn") String id);
}