package com.eurodyn.qlack2.fuse.settings.it;

import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;

import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static  SettingDTO createSettingDTO(){
        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setId(UUID.randomUUID().toString());
        settingDTO.setCreatedOn(new Date().getTime());
        settingDTO.setGroup(TestConst.generateRandomString());
        settingDTO.setKey(TestConst.generateRandomString());
        settingDTO.setOwner(TestConst.generateRandomString());
        settingDTO.setPassword(true);
        settingDTO.setSensitive(false);
        settingDTO.setVal(TestConst.generateRandomString());

        return settingDTO;
    }

}
