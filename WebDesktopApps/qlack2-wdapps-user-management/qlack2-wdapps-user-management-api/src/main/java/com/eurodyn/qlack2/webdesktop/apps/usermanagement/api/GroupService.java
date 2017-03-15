package com.eurodyn.qlack2.webdesktop.apps.usermanagement.api;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.CreateGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.DeleteGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.GetGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.MoveGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.group.UpdateGroupRequest;

public interface GroupService {
	List<UserGroupDTO> getGroups(EmptySignedRequest sreq);

	UserGroupDTO getGroup(GetGroupRequest sreq);

	String createGroup(CreateGroupRequest sreq);

	void updateGroup(UpdateGroupRequest sreq);

	void deleteGroup(DeleteGroupRequest sreq);

	void moveGroup(MoveGroupRequest sreq);
}
