package com.eurodyn.qlack2.fuse.settings.impl.mappers;

import com.eurodyn.qlack2.fuse.settings.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;
import com.eurodyn.qlack2.fuse.settings.impl.model.Setting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface SettingMapper {

  List<SettingDTO> map(List<Setting> o);

  SettingDTO map(Setting o);

  @Mappings({
    @Mapping(source = "group", target = "name")
  })
  GroupDTO mapToGroupDTO(Setting o);

  List<GroupDTO> mapToGroupDTO(List<Setting> o);

}
