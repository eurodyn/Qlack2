package com.eurodyn.qlack2.webdesktop.apps.usermanagement.api;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.CreateUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.DeleteUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.GetUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.GetUsersRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.UpdateUserRequest;

public interface UserService {
	List<UserDTO> getUsers(GetUsersRequest sreq);

	UserDTO getUser(GetUserRequest sreq);

	String createUser(CreateUserRequest sreq);

	void updateUser(UpdateUserRequest sreq);

	void deleteUser(DeleteUserRequest sreq);
}
