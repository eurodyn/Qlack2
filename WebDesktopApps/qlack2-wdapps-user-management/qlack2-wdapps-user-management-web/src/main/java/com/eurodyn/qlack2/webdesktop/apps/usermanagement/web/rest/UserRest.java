package com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.util.validator.util.ValidationHelper;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.UserService;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.CreateUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.DeleteUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.GetUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.GetUsersRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.api.request.user.UpdateUserRequest;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.dto.PasswordRDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.dto.UserRDTO;
import com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.util.Utils;

@Path("/users")
public class UserRest {
	private static final Logger LOGGER = Logger.getLogger(UserRest.class.getName());

	@Context private HttpHeaders headers;
	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserDTO> getUsers() {
		GetUsersRequest sreq = new GetUsersRequest();
		Utils.sign(sreq, headers);
		
		return userService.getUsers(sreq);
	}

	@GET
	@Path("{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public UserDTO getUser(@PathParam("userId") String userId) {
		GetUserRequest sreq = new GetUserRequest(userId, true);
		Utils.sign(sreq, headers);
		return userService.getUser(sreq);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument
	public String createUser(UserRDTO user) {
		// Check that password and verifyPassword match before proceeding
		// We also check that the password and verifyPassword are not blank here
		// instead of the RDTO since the DTO is also used from the updateUser
		// method in which case we don't submit a password.
		List<String> validationFields = new ArrayList<>();
		List<String> validationMessages = new ArrayList<>();
		List<String> validationValues = new ArrayList<>();
		if (StringUtils.isBlank(user.getPassword())) {
			validationFields.add("password");
			validationMessages.add("org.hibernate.validator.constraints.NotEmpty");
			validationValues.add(null);
		}
		if (StringUtils.isBlank(user.getVerifyPassword())) {
			validationFields.add("verifyPassword");
			validationMessages.add("org.hibernate.validator.constraints.NotEmpty");
			validationValues.add(null);
		}
		if (StringUtils.isNotBlank(user.getPassword()) && (!user.getPassword().equals(user.getVerifyPassword()))) {
			validationFields.add("verifyPassword");
			validationMessages.add("validation.error.passwordsShouldMatch");
			validationValues.add(null);
		}
		if (validationFields.size() > 0) {
			ValidationHelper.throwValidationError(validationFields.toArray(new String[validationFields.size()]),
					validationMessages.toArray(new String[validationMessages.size()]),
					validationValues.toArray(new String[validationValues.size()]));
		}

		final CreateUserRequest sreq = new CreateUserRequest();
		Utils.sign(sreq, headers);
		sreq.setUsername(user.getUsername());
		sreq.setFirstName(user.getFirstName());
		sreq.setLastName(user.getLastName());
		sreq.setEmail(user.getEmail());
		sreq.setPassword(user.getPassword());
		sreq.setActive(user.isActive());
		sreq.setSuperadmin(user.isSuperadmin());
		sreq.setGroupIds(user.getGroups());

		return Utils.validateUser(new Callable<String>() {
			@Override public String call() throws Exception {
				return userService.createUser(sreq);
			}
		});
	}

	@PUT
	@Path("{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateUser(@PathParam("userId") String userId, UserRDTO user) {
		final UpdateUserRequest sreq = new UpdateUserRequest();
		sreq.setUserId(userId);
		Utils.sign(sreq, headers);
		sreq.setUsername(user.getUsername());
		sreq.setFirstName(user.getFirstName());
		sreq.setLastName(user.getLastName());
		sreq.setEmail(user.getEmail());
		sreq.setActive(user.isActive());
		sreq.setSuperadmin(user.isSuperadmin());
		sreq.setGroupIds(user.getGroups());

		Utils.validateUser(new Callable<Void>() {
			@Override public Void call() throws Exception {
				userService.updateUser(sreq);
				return null;
			}
		});
	}

	@PUT
	@Path("{userId}/reset-password")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void resetUserPassword(@PathParam("userId") String userId, PasswordRDTO password) {
		if (!password.getPassword().equals(password.getVerifyPassword())) {
			LOGGER.log(Level.SEVERE, "Error resetting user password - password and validate password do not match");
			ValidationHelper.throwValidationError(new String[]{"verifyPassword"},
					new String[]{"validation.error.passwordsShouldMatch"},
					new String[]{null});
		}

		GetUserRequest req = new GetUserRequest(userId, true);
		Utils.sign(req, headers);
		UserDTO user = userService.getUser(req);

		final UpdateUserRequest ureq = new UpdateUserRequest();
		ureq.setUserId(user.getId());
		ureq.setUsername(user.getUsername());
		ureq.setFirstName(user.getFirstName());
		ureq.setLastName(user.getLastName());
		ureq.setActive(user.isActive());
		ureq.setSuperadmin(user.isSuperadmin());
		ureq.setEmail(user.getEmail());
		ureq.setGroupIds(user.getGroups());
		ureq.setPassword(password.getPassword());
		Utils.sign(ureq, headers);

		Utils.validateUser(new Callable<Void>() {
			@Override public Void call() throws Exception {
				userService.updateUser(ureq);
				return null;
			}
		});
	}

	@DELETE
	@Path("{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteUser(@PathParam("userId") String userId) {
		final DeleteUserRequest req = new DeleteUserRequest(userId);
		Utils.sign(req, headers);

		Utils.validateUser(new Callable<Void>() {
			@Override public Void call() throws Exception {
				userService.deleteUser(req);
				return null;
			}
		});
	}

}
