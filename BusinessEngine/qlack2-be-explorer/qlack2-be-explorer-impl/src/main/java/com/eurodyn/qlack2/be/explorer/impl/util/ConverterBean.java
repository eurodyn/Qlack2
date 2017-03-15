package com.eurodyn.qlack2.be.explorer.impl.util;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.impl.model.Project;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopUserService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserUncheckedRequest;

public class ConverterBean {
	private DesktopUserService userService;

	public void setUserService(DesktopUserService userService) {
		this.userService = userService;
	}

	private UserDTO user(String userId) {
		if (userId != null) {
			GetUserUncheckedRequest request = new GetUserUncheckedRequest(userId, false);
			return userService.getUserUnchecked(request);
		}
		else {
			return null;
		}
	}

	public ProjectDTO convert(Project entity, SignedTicket signedTicket) {
		if (entity == null) {
			return null;
		}

		ProjectDTO dto = new ProjectDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setDescription(entity.getDescription());
		dto.setActive(entity.isActive());
		dto.setRules(entity.isRules());
		dto.setWorkflows(entity.isWorkflows());
		dto.setForms(entity.isForms());

		dto.setCreatedOn(entity.getCreatedOn());
		dto.setCreatedBy(user(entity.getCreatedBy()));
		dto.setLastModifiedOn(entity.getLastModifiedOn());
		dto.setLastModifiedBy(user(entity.getLastModifiedBy()));

		return dto;
	}
}
