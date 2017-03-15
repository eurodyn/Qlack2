package com.eurodyn.qlack2.fuse.cm.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.eurodyn.qlack2.fuse.cm.api.dto.BinChunkDTO;
import com.eurodyn.qlack2.fuse.cm.impl.model.VersionBin;

@Mapper
public interface BinContentDTOMapper {
	
	@Mapping(source = "version.id", target = "versionID")
	BinChunkDTO map(VersionBin o);
}
