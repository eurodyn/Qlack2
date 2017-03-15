package com.eurodyn.qlack2.webdesktop.api;

import com.eurodyn.qlack2.webdesktop.api.dto.UserProfileDTO;

public interface UserService {
	UserProfileDTO getProfile();
	void editProfile(UserProfileDTO userDTO);
	boolean isUserAttributeUnique(String attributeValue);
}
