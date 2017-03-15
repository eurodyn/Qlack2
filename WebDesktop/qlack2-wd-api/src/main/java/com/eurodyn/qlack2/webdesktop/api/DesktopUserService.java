package com.eurodyn.qlack2.webdesktop.api;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.request.user.CreateUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.DeleteUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserUncheckedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUsersRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.IsUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.user.UpdateUserRequest;

public interface DesktopUserService {
	List<UserDTO> getUsers(GetUsersRequest sreq);

	UserDTO getUser(GetUserRequest sreq);

	UserDTO getUserUnchecked(GetUserUncheckedRequest sreq);

	boolean isUser(IsUserRequest sreq);

	// --

	String createUser(CreateUserRequest sreq);

	void updateUser(UpdateUserRequest sreq);

	void deleteUser(DeleteUserRequest sreq);
}
