package com.eurodyn.qlack2.be.rules.web.rest;

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

import com.eurodyn.qlack2.be.rules.api.ConfigService;
import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.AddManagedSubjectRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetManagedGroupsRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetManagedUsersRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetSecureOperationsRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.RemoveManagedSubjectRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.SaveSecureOperationsRequest;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;

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
		EmptyRequest sreq = new EmptyRequest();
		Utils.sign(sreq, headers);
		return configService.getUsers(sreq);
	}

	@GET
	@Path("groups")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroupDTO> getGroups() {
		EmptyRequest sreq = new EmptyRequest();
		Utils.sign(sreq, headers);
		return configService.getGroups(sreq);
	}

	@GET
	@Path("domains")
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroupDTO> getDomains() {
		EmptyRequest sreq = new EmptyRequest();
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
	public void addManagedSubject(@PathParam("subjectId") String subjectId,
			@QueryParam("resourceId") String resourceId) {
		AddManagedSubjectRequest sreq = new AddManagedSubjectRequest();
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		Utils.sign(sreq, headers);
		configService.addManagedSubject(sreq);
	}

	@PUT
	@Path("subjects/{subjectId}/unmanage")
	public void removeManagedSubject(@PathParam("subjectId") String subjectId,
			@QueryParam("resourceId") String resourceId) {
		RemoveManagedSubjectRequest sreq = new RemoveManagedSubjectRequest();
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		Utils.sign(sreq, headers);
		configService.removeManagedSubject(sreq);
	}

	@GET
	@Path("subjects/{subjectId}/operations")
	@Produces(MediaType.APPLICATION_JSON)
	public List<SecureOperationAccessDTO> getSecureOperations(@PathParam("subjectId") String subjectId,
			@QueryParam("resourceId") String resourceId) {
		GetSecureOperationsRequest sreq = new GetSecureOperationsRequest();
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		Utils.sign(sreq, headers);
		return configService.getSecureOperations(sreq);
	}

	@POST
	@Path("subjects/{subjectId}/operations")
	@Produces(MediaType.APPLICATION_JSON)
	public void saveSecureOperations(@PathParam("subjectId") String subjectId,
			@QueryParam("resourceId") String resourceId, List<SecureOperationAccessDTO> operations) {
		SaveSecureOperationsRequest sreq = new SaveSecureOperationsRequest();
		Utils.sign(sreq, headers);
		sreq.setSubjectId(subjectId);
		sreq.setResourceId(resourceId);
		sreq.setOperations(operations);
		configService.saveSecureOperations(sreq);
	}
}
