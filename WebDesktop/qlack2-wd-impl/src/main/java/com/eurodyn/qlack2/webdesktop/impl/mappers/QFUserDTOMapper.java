package com.eurodyn.qlack2.webdesktop.impl.mappers;

import org.mapstruct.Mapper;

import com.eurodyn.qlack2.webdesktop.api.constants.LVUser;
import com.eurodyn.qlack2.webdesktop.api.dto.UserProfileDTO;

@Mapper
public abstract class QFUserDTOMapper {
	public UserProfileDTO toUserDTO(com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO dto) {
		UserProfileDTO retVal = new UserProfileDTO();
		retVal.setFirstName(dto.getAttributeData(LVUser.ATTRIBUTES_LOOKUP.FIRST_NAME.getValue()));
		retVal.setLastName(dto.getAttributeData(LVUser.ATTRIBUTES_LOOKUP.LAST_NAME.getValue()));
		retVal.setEmail(dto.getAttributeData(LVUser.ATTRIBUTES_LOOKUP.EMAIL.getValue()));
		retVal.setPhone(dto.getAttributeData(LVUser.ATTRIBUTES_LOOKUP.PHONE.getValue()));
		retVal.setMobile(dto.getAttributeData(LVUser.ATTRIBUTES_LOOKUP.MOBILE.getValue()));
		retVal.setUsername(dto.getUsername());
		
		return retVal;
	}
}
