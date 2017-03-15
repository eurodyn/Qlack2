package com.eurodyn.qlack2.wd.web.resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.ops4j.pax.cdi.api.OsgiService;

import com.eurodyn.qlack2.webdesktop.api.DesktopService;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.AtmosphereSubscriptionsManagementRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.AtmosphereSubscriptionsManagementRequest.TYPE;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.GetAppDetailsRequest;

@Singleton
@Path("desktop")
public class DesktopResource {
	@OsgiService @Inject
	private DesktopService desktopService;

	@GET
	@Path("applications/active")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ApplicationInfo> getAllActiveApps() {
		return desktopService.getAllApps(true);
	}
	
	@GET
	@Path("application/{appUUID}")
	@Produces(MediaType.APPLICATION_JSON)
	public ApplicationInfo getAppDetails(@PathParam("appUUID") String appUUID) {
		GetAppDetailsRequest sreq = new GetAppDetailsRequest(appUUID);
		return desktopService.getAppDetails(sreq);
	}

	@GET
	@Path("desktop-app-icons")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ApplicationInfo> getOwnDesktopIcons() {
//		utils.validateTicket(headers);
//		SignedTicket ticket = utils.getSignedTicket(headers);
//		GetDesktopIconsForUserRequest sreq = new GetDesktopIconsForUserRequest(
//				ticket.getUserID());
//		utils.sign(sreq, headers);
//		return desktopService.getDesktopIconsForUser(sreq);
		return new ArrayList<ApplicationInfo>();
	}

	@PUT
	@Path("desktop-app-icon/{appUUID}")
	public void addAppToOwnDesktop(@PathParam("appUUID") String appUUID) {
//		utils.validateTicket(headers);
//		SignedTicket ticket = utils.getSignedTicket(headers);
//		AddAppToUserDesktopRequest sreq = new AddAppToUserDesktopRequest(
//				ticket.getUserID(), appUUID);
//		utils.sign(sreq, headers);
//		desktopService.addAppToUserDesktop(sreq);
	}

	@DELETE
	@Path("desktop-app-icon/{appUUID}")
	public void removeAppFromOwnDesktop(@PathParam("appUUID") String appUUID) {
//		utils.validateTicket(headers);
//		SignedTicket ticket = utils.getSignedTicket(headers);
//		RemoveAppFromUserDesktopRequest sreq = new RemoveAppFromUserDesktopRequest(
//				ticket.getUserID(), appUUID);
//		utils.sign(sreq, headers);
//		desktopService.removeAppFromUserDesktop(sreq);
	}

	@GET
	@Path("atmosphere/{type}")
	public void atmosphereSubscriptionsManagement(
			@PathParam("type") String type, @QueryParam("topic") String topic) {
		AtmosphereSubscriptionsManagementRequest sreq = new AtmosphereSubscriptionsManagementRequest();
		sreq.setTopic(topic);
		sreq.setRequestType(TYPE.valueOf(type));
		desktopService.manageAtmosphereSubscriptions(sreq);
	}

}
