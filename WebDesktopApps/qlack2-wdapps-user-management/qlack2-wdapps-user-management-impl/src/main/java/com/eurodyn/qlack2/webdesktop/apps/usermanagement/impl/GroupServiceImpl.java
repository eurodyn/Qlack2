package com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl;

import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopGroupService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.ConfigService;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.GroupService;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.*;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.util.Constants;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.dto.MoveUserGroupAuditDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.dto.UserGroupAuditDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.impl.util.AuditConstants.LEVEL;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Transactional
@OsgiServiceProvider(classes = {GroupService.class})
public class GroupServiceImpl implements GroupService {
	@OsgiService
	@Inject
	private IDMService idmService;

	@OsgiService
	@Inject
	private DesktopGroupService desktopGroupService;

	@OsgiService
	@Inject
	private AuditClientService audit;

	@OsgiService
	@Inject
	private EventPublisherService eventPublisher;

	private void publishGroupEvent(SignedTicket signedTicket, String event, String groupId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_GROUP_ID, groupId);

		eventPublisher.publishSync(message,
				"com/eurodyn/qlack2/wd/apps/usermanagement/"
						+ Constants.RESOURCE_TYPE_GROUP + "/" + event);
	}

	private void publishDomainEvent(SignedTicket signedTicket, String event, String domainId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_DOMAIN_ID, domainId);

		eventPublisher.publishSync(message,
				"com/eurodyn/qlack2/wd/apps/usermanagement/"
						+ Constants.RESOURCE_TYPE_DOMAIN + "/" + event);
	}

	@Override
	@ValidateTicket
	public List<UserGroupDTO> getGroups(EmptySignedRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest dreq =
				new com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest();
		dreq.setSignedTicket(sreq.getSignedTicket());

		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.VIEW.toString(), GROUP.ALL_GROUPS.toString(),
				null, sreq.getSignedTicket().getUserID(), null);

		return desktopGroupService.getDomainsAsTree(dreq);
	}

	@Override
	@ValidateTicket
	public UserGroupDTO getGroup(GetGroupRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest dreq =
				new com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest();
		dreq.setSignedTicket(sreq.getSignedTicket());
		dreq.setGroupId(sreq.getGroupId());
		dreq.setIncludeRelatives(sreq.isIncludeRelatives());
		dreq.setIncludeUsers(sreq.isIncludeUsers());
		UserGroupDTO group = desktopGroupService.getGroup(dreq);

		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.VIEW.toString(), GROUP.GROUP.toString(),
				null, sreq.getSignedTicket().getUserID(), group);

		return group;
	}

	@Override
	@ValidateTicket
	public String createGroup(CreateGroupRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.group.CreateGroupRequest dreq =
				new com.eurodyn.qlack2.webdesktop.api.request.group.CreateGroupRequest();
		dreq.setSignedTicket(sreq.getSignedTicket());
		dreq.setName(sreq.getName());
		dreq.setDescription(sreq.getDescription());
		dreq.setParentGroupId(sreq.getParentGroupId());
		String groupId = desktopGroupService.createGroup(dreq);

		UserGroupAuditDTO auditDTO = new UserGroupAuditDTO();
		auditDTO.setId(groupId);
		auditDTO.setName(sreq.getName());
		auditDTO.setDescription(sreq.getDescription());
		auditDTO.setParentGroupId(sreq.getParentGroupId());

		String auditGroup = sreq.getParentGroupId() == null ? GROUP.DOMAIN.toString() : GROUP.GROUP.toString();
		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.CREATE.toString(), auditGroup,
				null, sreq.getSignedTicket().getUserID(), auditDTO);

		if (sreq.getParentGroupId() == null) {
			publishDomainEvent(sreq.getSignedTicket(), Constants.EVENT_CREATE, groupId);
		} else {
			publishGroupEvent(sreq.getSignedTicket(), Constants.EVENT_CREATE, groupId);
		}

		return groupId;
	}

	@Override
	@ValidateTicket
	public void updateGroup(UpdateGroupRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.group.UpdateGroupRequest dreq =
				new com.eurodyn.qlack2.webdesktop.api.request.group.UpdateGroupRequest();
		dreq.setSignedTicket(sreq.getSignedTicket());
		dreq.setId(sreq.getId());
		dreq.setName(sreq.getName());
		dreq.setDescription(sreq.getDescription());
		dreq.setUserIds(sreq.getUserIds());
		desktopGroupService.updateGroup(dreq);

		UserGroupAuditDTO auditDTO = getGroupForAudit(sreq.getSignedTicket(), sreq.getId());
		String auditGroup = auditDTO.getParentGroupId() == null ? GROUP.DOMAIN.toString() : GROUP.GROUP.toString();
		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.UPDATE.toString(), auditGroup,
				null, sreq.getSignedTicket().getUserID(), auditDTO);

		if (auditDTO.getParentGroupId() == null) {
			publishDomainEvent(sreq.getSignedTicket(), Constants.EVENT_UPDATE, sreq.getId());
		} else {
			publishGroupEvent(sreq.getSignedTicket(), Constants.EVENT_UPDATE, sreq.getId());
		}
	}

	@Override
	@ValidateTicket
	public void deleteGroup(DeleteGroupRequest sreq) {
		UserGroupAuditDTO auditDTO = getGroupForAudit(sreq.getSignedTicket(), sreq.getId());

		com.eurodyn.qlack2.webdesktop.api.request.group.DeleteGroupRequest dreq =
				new com.eurodyn.qlack2.webdesktop.api.request.group.DeleteGroupRequest(sreq.getId());
		dreq.setSignedTicket(sreq.getSignedTicket());
		desktopGroupService.deleteGroup(dreq);

		String auditGroup = auditDTO.getParentGroupId() == null ? GROUP.DOMAIN.toString() : GROUP.GROUP.toString();
		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.DELETE.toString(), auditGroup,
				null, sreq.getSignedTicket().getUserID(), auditDTO);

		if (auditDTO.getParentGroupId() == null) {
			publishDomainEvent(sreq.getSignedTicket(), Constants.EVENT_DELETE, sreq.getId());
		} else {
			publishGroupEvent(sreq.getSignedTicket(), Constants.EVENT_DELETE, sreq.getId());
		}
	}

	@Override
	@ValidateTicket
	public void moveGroup(MoveGroupRequest sreq) {
		com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest groupReq =
				new com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest();
		groupReq.setSignedTicket(sreq.getSignedTicket());
		groupReq.setGroupId(sreq.getId());
		groupReq.setIncludeRelatives(true);
		groupReq.setIncludeUsers(false);

		UserGroupDTO groupDTO = desktopGroupService.getGroup(groupReq);
		MoveUserGroupAuditDTO auditDTO = new MoveUserGroupAuditDTO();
		auditDTO.setId(sreq.getId());
		auditDTO.setName(groupDTO.getName());
		auditDTO.setOldParentId(groupDTO.getParentGroup().getId());
		auditDTO.setNewParentId(sreq.getNewParentId());

		com.eurodyn.qlack2.webdesktop.api.request.group.MoveGroupRequest dreq =
				new com.eurodyn.qlack2.webdesktop.api.request.group.MoveGroupRequest(sreq.getId(), sreq.getNewParentId());
		dreq.setSignedTicket(sreq.getSignedTicket());
		desktopGroupService.moveGroup(dreq);

		audit.audit(LEVEL.WD_USERMANAGEMENT.toString(), EVENT.MOVE.toString(), GROUP.GROUP.toString(),
				null, sreq.getSignedTicket().getUserID(), auditDTO);

		publishGroupEvent(sreq.getSignedTicket(), Constants.EVENT_MOVE, sreq.getId());
	}

	private UserGroupAuditDTO getGroupForAudit(SignedTicket ticket, String groupId) {
		com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest dreq =
				new com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest();
		dreq.setSignedTicket(ticket);
		dreq.setGroupId(groupId);
		dreq.setIncludeRelatives(true);
		dreq.setIncludeUsers(true);
		UserGroupDTO groupDTO = desktopGroupService.getGroup(dreq);

		UserGroupAuditDTO auditDTO = new UserGroupAuditDTO();
		auditDTO.setId(groupId);
		auditDTO.setName(groupDTO.getName());
		auditDTO.setDescription(groupDTO.getDescription());
		UserGroupDTO parentGroupDto = groupDTO.getParentGroup();
		if (parentGroupDto != null) {
			auditDTO.setParentGroupId(parentGroupDto.getId());
		}
		auditDTO.setUsers(groupDTO.getUsers());
		return auditDTO;
	}
}
