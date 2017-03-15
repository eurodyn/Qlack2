package com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.rest;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.ConfigService;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.AddManagedSubjectRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.GetManagedGroupsRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.GetSecureOperationsRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.RemoveManagedSubjectRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.SaveSecureOperationsRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.util.Utils;

@Path("/config")
public class ConfigRest {
	@Context
	private HttpHeaders headers;
	private ConfigService configService;

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<UserDTO> getManagedUsers() {
		EmptySignedRequest userReq = new EmptySignedRequest();
		Utils.sign(userReq, headers);
		return configService.getManagedUsers(userReq);
	}

	@GET
	@Path("/groups")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<UserGroupDTO> getManagedGroups() {
		GetManagedGroupsRequest groupReq = new GetManagedGroupsRequest();
		Utils.sign(groupReq, headers);
		groupReq.setIncludeRelatives(true);
		groupReq.setIncludeUsers(false);
		return configService.getManagedGroups(groupReq);
	}

	@PUT
	@Path("subjects/{subjectId}/manage")
	public void addManagedSubject(@PathParam("subjectId") String subjectId) {
		AddManagedSubjectRequest sreq = new AddManagedSubjectRequest(subjectId);
		Utils.sign(sreq, headers);
		configService.addManagedSubject(sreq);
	}

	@PUT
	@Path("subjects/{subjectId}/unmanage")
	public void removeManagedSubject(@PathParam("subjectId") String subjectId) {
		RemoveManagedSubjectRequest sreq = new RemoveManagedSubjectRequest(subjectId);
		Utils.sign(sreq, headers);
		configService.removeManagedSubject(sreq);
	}

	@GET
	@Path("subjects/{subjectId}/operations")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SecureOperationAccessDTO> getSecureOperations(@PathParam("subjectId") String subjectId) {
		GetSecureOperationsRequest sreq = new GetSecureOperationsRequest(subjectId);
		Utils.sign(sreq, headers);
		return configService.getSecureOperations(sreq);
	}

	@POST
	@Path("subjects/{subjectId}/operations")
	@Produces(MediaType.APPLICATION_JSON)
	public void saveSecureOperations(@PathParam("subjectId") String subjectId, List<SecureOperationAccessDTO> operations) {
		SaveSecureOperationsRequest sreq = new SaveSecureOperationsRequest();
		Utils.sign(sreq, headers);
		sreq.setSubjectId(subjectId);
		sreq.setOperations(operations);
		configService.saveSecureOperations(sreq);
	}
}
