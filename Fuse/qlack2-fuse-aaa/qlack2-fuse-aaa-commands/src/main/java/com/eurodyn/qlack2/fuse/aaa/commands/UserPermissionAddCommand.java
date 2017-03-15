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

import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;

@Command(scope = "qlack", name = "aaa-user-permission-add", description = "Assigns an operation to a user in the AAA.")
@Service
public final class UserPermissionAddCommand implements Action {
	@Argument(index = 0, name = "username",
			description = "The username of the user to assign the permission to.",
			required = true, multiValued = false)
	private String username;
	@Argument(index = 1, name = "operation name",
			description = "The name of the operation to assign to the user. Be cautious, if the specified operation does not exist will be created.",
			required = true, multiValued = false)
	private String operationName;
	@Argument(index = 2, name = "permit access",
			description = "Whether the user should be permitted or denied access to the operation (true/false)",
			required = true, multiValued = false)
	private boolean permitAccess;
	@Argument(index = 3, name = "resource object ID",
			description = "The resource object ID for which to add the permission",
			required = false, multiValued = false)
	private String resourceObjectId;

	@Reference
	private OperationService operationService;
	@Reference
	private UserService userService;
	@Reference
	private ResourceService resourceService;

	@Override
	public Object execute() throws Exception {
		String userID = userService.getUserByName(username).getId();
		if (operationService.getOperationByName(operationName) == null) {
			operationService.createOperation(new OperationDTO(operationName));
		}
		if (resourceObjectId == null) {
			operationService.addOperationToUser(userID, operationName,
					!permitAccess);
			System.out.println("User '" + username
					+ "' was sucessfully assigned operation '" + operationName + "'.");
		} else {
			String resourceID = resourceService.getResourceByObjectId(
					resourceObjectId).getId();
			operationService.addOperationToUser(userID, operationName,
					resourceID, !permitAccess);
			System.out.println("User '" + username
					+ "' was sucessfully assigned operation '" + operationName
					+ "' on resource with object ID '" + resourceObjectId + "'.");
		}
		return null;
	}

}
