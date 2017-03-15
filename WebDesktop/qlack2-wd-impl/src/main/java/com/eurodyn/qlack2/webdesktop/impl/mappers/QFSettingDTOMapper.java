package com.eurodyn.qlack2.webdesktop.impl.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.eurodyn.qlack2.webdesktop.api.dto.SettingDTO;

/**
 * Mapper for com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO. 
 */
@Mapper
public interface QFSettingDTOMapper {
	SettingDTO toSettingDTO(com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO dto);
	List<SettingDTO> toSettingDTO(List<com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO> dtoList);
}
