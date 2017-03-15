package com.eurodyn.qlack2.webdesktop.api;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.CreateGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.DeleteGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.GetGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.MoveGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.group.UpdateGroupRequest;

public interface DesktopGroupService {
	List<UserGroupDTO> getDomainsAsTree(EmptySignedRequest sreq);
	
	List<UserGroupDTO> getDomains(EmptySignedRequest sreq);

	UserGroupDTO getGroup(GetGroupRequest sreq);

	String createGroup(CreateGroupRequest sreq);

	void updateGroup(UpdateGroupRequest sreq);

	void deleteGroup(DeleteGroupRequest sreq);

	void moveGroup(MoveGroupRequest sreq);
}
