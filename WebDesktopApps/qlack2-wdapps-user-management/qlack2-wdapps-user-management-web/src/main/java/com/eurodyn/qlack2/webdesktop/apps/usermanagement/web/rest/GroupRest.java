package com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.rest;

import java.util.List;
import java.util.concurrent.Callable;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.GroupService;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.CreateGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.DeleteGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.GetGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.MoveGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.UpdateGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.dto.GroupRDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.util.Utils;

@Path("/groups")
public class GroupRest {

	@Context private HttpHeaders headers;
	private GroupService groupService;

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserGroupDTO> getGroups() {
		EmptySignedRequest sreq = new EmptySignedRequest();
		Utils.sign(sreq, headers);
		return groupService.getGroups(sreq);
	}

	@GET
	@Path("{groupId}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserGroupDTO getGroup(@PathParam("groupId") String groupId) {
		GetGroupRequest sreq = new GetGroupRequest();
		sreq.setGroupId(groupId);
		sreq.setIncludeRelatives(false);
		sreq.setIncludeUsers(true);
		Utils.sign(sreq, headers);
		return groupService.getGroup(sreq);
	}

	@POST
	@Path("{parentGroupId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public String createGroup(@PathParam("parentGroupId") String parentGroupId, GroupRDTO group) {
		final CreateGroupRequest sreq = new CreateGroupRequest();
		Utils.sign(sreq, headers);
		sreq.setName(group.getName());
		sreq.setDescription(group.getDescription());

		// doh, we seem to use "0" as a special meaning of no parent
		// in order to set the path param for domains
		if (parentGroupId.equals("0")) {
			parentGroupId = null;
		}
		sreq.setParentGroupId(parentGroupId);

		return Utils.validateGroup(new Callable<String>() {
			@Override public String call() throws Exception {
				return groupService.createGroup(sreq);
			}
		});
	}

	@PUT
	@Path("{groupId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateGroup(@PathParam("groupId") String groupId, GroupRDTO group) {
		final UpdateGroupRequest sreq = new UpdateGroupRequest();
		sreq.setId(groupId);
		sreq.setName(group.getName());
		sreq.setDescription(group.getDescription());
		sreq.setUserIds(group.getUsers());
		Utils.sign(sreq, headers);

		Utils.validateGroup(new Callable<Void>() {
			@Override public Void call() throws Exception {
				groupService.updateGroup(sreq);
				return null;
			}
		});
	}

	@DELETE
	@Path("{groupId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteGroup(@PathParam("groupId") String groupId) {
		final DeleteGroupRequest sreq = new DeleteGroupRequest(groupId);
		Utils.sign(sreq, headers);

		Utils.validateGroup(new Callable<Void>() {
			@Override public Void call() throws Exception {
				groupService.deleteGroup(sreq);
				return null;
			}
		});
	}

	@PUT
	@Path("{groupId}/move")
	@Produces(MediaType.APPLICATION_JSON)
	public void moveGroup(@PathParam("groupId") String groupId, @QueryParam("newParentId") String newParentId) {
		final MoveGroupRequest sreq = new MoveGroupRequest(groupId, newParentId);
		Utils.sign(sreq, headers);

		Utils.validateGroup(new Callable<Void>() {
			@Override public Void call() throws Exception {
				groupService.moveGroup(sreq);
				return null;
			}
		});
	}

}
