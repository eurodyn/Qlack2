package com.eurodyn.qlack2.fuse.simm.impl.mappers;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupAttributeDTO;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroupAttribute;

@Mapper
public interface SimGroupAttributeMapper {
	@Mapping(source = "src.group.id", target = "groupId")
	SocialGroupAttributeDTO toSocialGroupAttributeDTO(SimGroupAttribute src);

	Set<SocialGroupAttributeDTO> toSocialGroupAttributeDTOList(List<SimGroupAttribute> src);
}
