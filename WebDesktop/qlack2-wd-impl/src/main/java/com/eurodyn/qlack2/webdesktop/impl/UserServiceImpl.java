package com.eurodyn.qlack2.webdesktop.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicketHolder;
import com.eurodyn.qlack2.webdesktop.api.UserService;
import com.eurodyn.qlack2.webdesktop.api.constants.LVUser;
import com.eurodyn.qlack2.webdesktop.api.dto.UserProfileDTO;
import com.eurodyn.qlack2.webdesktop.impl.mappers.QFUserDTOMapperImpl;

@Transactional
@OsgiServiceProvider(classes = {UserService.class})
@Singleton
public class UserServiceImpl extends BaseService implements UserService {
	
	// Service refs.
	@OsgiService
	@Inject
	private com.eurodyn.qlack2.fuse.aaa.api.UserService userService;
	
	// Mapstruct refs.
	private static QFUserDTOMapperImpl qfUserDTOMapper = new QFUserDTOMapperImpl();
	
	// Get logged in user's profile info
	@Override
	@ValidateTicketHolder
	@Transactional(Transactional.TxType.SUPPORTS)
	public UserProfileDTO getProfile() {
		UserProfileDTO user = qfUserDTOMapper.toUserDTO(userService.getUserById(getUserID()));
		return user;
	}

	// Edit logged in user's profile info
	@Override
	@ValidateTicketHolder
	@Transactional(Transactional.TxType.REQUIRED)
	public void editProfile(UserProfileDTO userDTO) {
		com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO user = userService.getUserById(getUserID());
		user.setUsername(userDTO.getUsername());
		user.setAttribute(new UserAttributeDTO(LVUser.ATTRIBUTES_LOOKUP.FIRST_NAME.getValue(), userDTO.getFirstName()));
		user.setAttribute(new UserAttributeDTO(LVUser.ATTRIBUTES_LOOKUP.LAST_NAME.getValue(), userDTO.getLastName()));
		user.setAttribute(new UserAttributeDTO(LVUser.ATTRIBUTES_LOOKUP.EMAIL.getValue(), userDTO.getEmail()));
		user.setAttribute(new UserAttributeDTO(LVUser.ATTRIBUTES_LOOKUP.PHONE.getValue(), userDTO.getPhone()));
		user.setAttribute(new UserAttributeDTO(LVUser.ATTRIBUTES_LOOKUP.MOBILE.getValue(), userDTO.getMobile()));
		userService.updateUser(user, false);
	}
	
	@Override
	@ValidateTicketHolder
	@Transactional(Transactional.TxType.SUPPORTS)
	public boolean isUserAttributeUnique(String attributeValue) {
		// check UserAttribute's uniqueness
		return userService.isAttributeValueUnique(attributeValue, LVUser.ATTRIBUTES_LOOKUP.EMAIL.getValue(), getUserID());
	}
}
