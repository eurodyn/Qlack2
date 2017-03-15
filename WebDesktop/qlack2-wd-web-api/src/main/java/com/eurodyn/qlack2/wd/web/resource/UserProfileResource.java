package com.eurodyn.qlack2.wd.web.resource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.ops4j.pax.cdi.api.OsgiService;

import com.eurodyn.qlack2.webdesktop.api.UserService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserProfileDTO;

@Path("/user/profile")
@Singleton
public class UserProfileResource {
	@OsgiService @Inject
	private UserService userService;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProfile()  {
		UserProfileDTO user = userService.getProfile();
		if (user == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(user).build();
	}
	
	@PUT
	@Path("edit-profile")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editProfile() {
		// TODO
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
}
