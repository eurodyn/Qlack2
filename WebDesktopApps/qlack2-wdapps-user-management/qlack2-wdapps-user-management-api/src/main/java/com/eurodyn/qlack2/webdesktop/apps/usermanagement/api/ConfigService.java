package com.eurodyn.qlack2.webdesktop.apps.usermanagement.api;

import java.util.List;
import java.util.Set;

import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.AddManagedSubjectRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.GetManagedGroupsRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.GetSecureOperationsRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.RemoveManagedSubjectRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.config.SaveSecureOperationsRequest;

public interface ConfigService {	
	List<SecureOperationAccessDTO> getSecureOperations(GetSecureOperationsRequest sreq);
	
	void saveSecureOperations(SaveSecureOperationsRequest sreq);
	
	void addManagedSubject(AddManagedSubjectRequest sreq);
	
	void removeManagedSubject(RemoveManagedSubjectRequest sreq);
	
	Set<UserDTO> getManagedUsers(EmptySignedRequest sreq);
	
	Set<UserGroupDTO> getManagedGroups(GetManagedGroupsRequest sreq);
}
