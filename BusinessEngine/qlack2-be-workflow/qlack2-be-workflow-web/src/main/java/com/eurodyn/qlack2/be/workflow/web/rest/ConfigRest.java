package com.eurodyn.qlack2.be.workflow.web.rest;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.eurodyn.qlack2.be.workflow.api.ConfigService;
import com.eurodyn.qlack2.be.workflow.api.request.config.AddManagedSubjectRequest;
import com.eurodyn.qlack2.be.workflow.api.request.config.GetManagedGroupsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.config.GetManagedUsersRequest;
import com.eurodyn.qlack2.be.workflow.api.request.config.GetSecureOperationsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.config.RemoveManagedSubjectRequest;
import com.eurodyn.qlack2.be.workflow.api.request.config.SaveSecureOperationsRequest;
import com.eurodyn.qlack2.be.workflow.web.util.Utils;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest;

@Path("/config")
public class ConfigRest {
	@Context
	private HttpHeaders headers;
	private ConfigService configService;

	public void setConfigService(ConfigService configService) {
		this.configService = configService;
	}

	@GET
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserDTO> getUsers() {
		EmptySignedRequest sreq = new EmptySignedRequest();
		Utils.sign(sreq, headers);
		return configService.getUsers(sreq);
	}

	@GET
	@Path("groups")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroupDTO> getGroups() {
		EmptySignedRequest sreq = new EmptySignedRequest();
		Utils.sign(sreq, headers);
		return configService.getGroups(sreq);
	}
	
	@GET
	@Path("domains")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroupDTO> getDomains() {
		EmptySignedRequest sreq = new EmptySignedRequest();
		Utils.sign(sreq, headers);
		return configService.getDomains(sreq);
	}

	@GET
	@Path("users/managed")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<UserDTO> getManagedUsers(@QueryParam("resourceId") String resourceId) {
		GetManagedUsersRequest userReq = new GetManagedUsersRequest();
		Utils.sign(userReq, headers);
		userReq.setResourceId(resourceId);
		return configService.getManagedUsers(userReq);
	}

	@GET
	@Path("groups/managed")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<UserGroupDTO> getManagedGroups(@QueryParam("resourceId") String resourceId) {
		GetManagedGroupsRequest groupReq = new GetManagedGroupsRequest();
		Utils.sign(groupReq, headers);
		groupReq.setIncludeRelatives(true);
		groupReq.setIncludeUsers(false);
		groupReq.setResourceId(resourceId);
		return configService.getManagedGroups(groupReq);
	}

	@PUT
	@Path("subjects/{subjectId}/manage")
	public void addManagedSubject(@QueryParam("resourceId") String resourceId,
			@PathParam("subjectId") String subjectId) {
		AddManagedSubjectRequest sreq = new AddManagedSubjectRequest();
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		Utils.sign(sreq, headers);
		configService.addManagedSubject(sreq);
	}

	@PUT
	@Path("subjects/{subjectId}/unmanage")
	public void removeManagedSubject(@QueryParam("resourceId") String resourceId,
			@PathParam("subjectId") String subjectId) {
		RemoveManagedSubjectRequest sreq = new RemoveManagedSubjectRequest();
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		Utils.sign(sreq, headers);
		configService.removeManagedSubject(sreq);
	}

	@GET
	@Path("subjects/{subjectId}/operations")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SecureOperationAccessDTO> getSecureOperations(
			@QueryParam("resourceId") String resourceId, @PathParam("subjectId") String subjectId) {
		GetSecureOperationsRequest sreq = new GetSecureOperationsRequest();
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		Utils.sign(sreq, headers);
		return configService.getSecureOperations(sreq);
	}

	@POST
	@Path("subjects/{subjectId}/operations")
	@Produces(MediaType.APPLICATION_JSON)
	public void saveSecureOperations(@QueryParam("resourceId") String resourceId,
			@PathParam("subjectId") String subjectId,
			List<SecureOperationAccessDTO> operations) {
		SaveSecureOperationsRequest sreq = new SaveSecureOperationsRequest();
		Utils.sign(sreq, headers);
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		sreq.setOperations(operations);
		configService.saveSecureOperations(sreq);
	}
}
