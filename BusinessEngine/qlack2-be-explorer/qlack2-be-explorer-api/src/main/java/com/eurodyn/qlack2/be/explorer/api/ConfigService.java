package com.eurodyn.qlack2.be.explorer.api;

import java.util.List;
import java.util.Set;

import com.eurodyn.qlack2.be.explorer.api.request.config.AddManagedSubjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.config.GetManagedGroupsRequest;
import com.eurodyn.qlack2.be.explorer.api.request.config.GetManagedUsersRequest;
import com.eurodyn.qlack2.be.explorer.api.request.config.GetSecureOperationsRequest;
import com.eurodyn.qlack2.be.explorer.api.request.config.RemoveManagedSubjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.config.SaveSecureOperationsRequest;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;
import com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest;

public interface ConfigService {	
	List<SecureOperationAccessDTO> getSecureOperations(GetSecureOperationsRequest sreq);
	
	void saveSecureOperations(SaveSecureOperationsRequest sreq);
	
	void addManagedSubject(AddManagedSubjectRequest sreq);
	
	void removeManagedSubject(RemoveManagedSubjectRequest sreq);
	
	Set<UserDTO> getManagedUsers(GetManagedUsersRequest sreq);
	
	Set<UserGroupDTO> getManagedGroups(GetManagedGroupsRequest sreq);
	
	List<UserDTO> getUsers(EmptySignedRequest sreq);
	
	List<UserGroupDTO> getGroups(EmptySignedRequest sreq);
}
