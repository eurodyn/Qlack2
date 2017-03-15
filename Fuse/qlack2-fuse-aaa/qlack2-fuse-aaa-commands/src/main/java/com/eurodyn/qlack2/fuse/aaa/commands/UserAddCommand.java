/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License").
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package com.eurodyn.qlack2.fuse.aaa.commands;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;

@Command(scope = "qlack", name = "aaa-user-add", description = "Creates a new user into the AAA.")
@Service
public final class UserAddCommand implements Action {
	@Argument(index = 0, name = "username", description = "The username of the user to add.", required = true, multiValued = false)
	private String username;

	@Argument(index = 1, name = "password", description = "The password of the user to add.", required = true, multiValued = false)
	private String password;

	@Argument(index = 2, name = "superadmin", description = "Whether this user should be a superadmin", required = false, multiValued = false)
	private Boolean superadmin;

	@Argument(index = 3, name = "status", description = "The status of the user to add", required = false, multiValued = false)
	private Byte status;

	@Reference
	private UserService userService;

	@Override
	public Object execute() {
		UserDTO userDTO = new UserDTO();
		userDTO.setUsername(username);
		userDTO.setPassword(password);
		if (superadmin != null) {
			userDTO.setSuperadmin(superadmin);
		}
		if (status != null) {
			userDTO.setStatus(status.byteValue());
		}

		String userId = userService.createUser(userDTO);
		System.out.println("User created with Id: " + userId);

		return null;
	}

}
