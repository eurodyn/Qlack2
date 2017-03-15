package com.eurodyn.qlack2.webdesktop.api;

import java.util.List;
import java.util.Set;

import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.AllowSecureOperationForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.CreateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DenySecureOperationForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetAllowedGroupsForOperationRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetAllowedUsersForOperationRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.GetSecureOperationsForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromTemplateRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RemoveSecureOperationFromUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.RequirePermittedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.UpdateSecureResourceRequest;

public interface SecurityService {
	String createSecureResource(CreateSecureResourceRequest sreq);
	void updateSecureResource(UpdateSecureResourceRequest sreq);
	void deleteSecureResource(DeleteSecureResourceRequest sreq);
	Boolean isPermitted(IsPermittedRequest sreq);
	void requirePermitted(RequirePermittedRequest sreq);
	
	void allowSecureOperationForGroup(AllowSecureOperationForGroupRequest sreq);
	void denySecureOperationForGroup(DenySecureOperationForGroupRequest sreq);
	void removeSecureOperationFromGroup(RemoveSecureOperationFromGroupRequest sreq);
	void allowSecureOperationForUser(AllowSecureOperationForUserRequest sreq);
	void denySecureOperationForUser(DenySecureOperationForUserRequest sreq);
	void removeSecureOperationFromUser(RemoveSecureOperationFromUserRequest sreq);
	
	List<SecureOperationAccessDTO> getSecureOperationsForUser(GetSecureOperationsForUserRequest sreq);
	List<SecureOperationAccessDTO> getSecureOperationsForGroup(GetSecureOperationsForGroupRequest sreq);
	Set<UserDTO> getAllowedUsersForOperation(GetAllowedUsersForOperationRequest sreq);
	Set<UserGroupDTO> getAllowedGroupsForOperation(GetAllowedGroupsForOperationRequest sreq);
	
	Set<SecureOperationAccessDTO> getSecureOperationsForTemplate(GetSecureOperationsForTemplateRequest sreq);
	void allowSecureOperationForTemplate(AllowSecureOperationForTemplateRequest sreq);
	void denySecureOperationForTemplate(DenySecureOperationForTemplateRequest sreq);
	void removeSecureOperationFromTemplate(RemoveSecureOperationFromTemplateRequest sreq);
}
