package com.eurodyn.qlack2.be.rules.api;

import java.util.List;
import java.util.Set;

import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.AddManagedSubjectRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetManagedGroupsRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetManagedUsersRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.GetSecureOperationsRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.RemoveManagedSubjectRequest;
import com.eurodyn.qlack2.be.rules.api.request.config.SaveSecureOperationsRequest;
import com.eurodyn.qlack2.webdesktop.api.dto.SecureOperationAccessDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.dto.UserGroupDTO;

public interface ConfigService {	
	List<SecureOperationAccessDTO> getSecureOperations(GetSecureOperationsRequest sreq);
	
	void saveSecureOperations(SaveSecureOperationsRequest sreq);
	
	void addManagedSubject(AddManagedSubjectRequest sreq);
	
	void removeManagedSubject(RemoveManagedSubjectRequest sreq);
	
	Set<UserDTO> getManagedUsers(GetManagedUsersRequest sreq);
	
	Set<UserGroupDTO> getManagedGroups(GetManagedGroupsRequest sreq);
	
	List<UserDTO> getUsers(EmptyRequest sreq);
	
	List<UserGroupDTO> getGroups(EmptyRequest sreq);
	
	List<UserGroupDTO> getDomains(EmptyRequest sreq);
}
