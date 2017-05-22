package com.eurodyn.qlack2.fuse.componentlibrary.it;

import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentPermissionDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;
import java.util.UUID;
import java.util.Date;


public class TestUtilities {

    public static ComponentPermissionDTO createComponentPermissionDTO(){
        ComponentPermissionDTO componentPermissionDTO = new ComponentPermissionDTO();
        componentPermissionDTO.setId(UUID.randomUUID().toString());
        componentPermissionDTO.setEnabled(true);
        componentPermissionDTO.setGadgetID(UUID.randomUUID().toString());
        componentPermissionDTO.setGadgetTitle(TestConst.generateRandomString());
        componentPermissionDTO.setUserID(UUID.randomUUID().toString());
        return componentPermissionDTO;
    }

    public static ComponentDTO createComponentDTO() {
        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setId(UUID.randomUUID().toString());
        componentDTO.setDescription(TestConst.generateRandomString());
        componentDTO.setAuthor(TestConst.generateRandomString());
        componentDTO.setBoxLink(TestConst.generateRandomString());
        componentDTO.setOwnerUserID(UUID.randomUUID().toString());
        componentDTO.setUserKey(UUID.randomUUID().toString());
        componentDTO.setTitle(TestConst.generateRandomString());
        componentDTO.setSrcUserId(UUID.randomUUID().toString());
        return componentDTO;
    }

    public static ComponentPermissionDTO createComponentPermissionDTO(String gadgetId, String UserId){
        ComponentPermissionDTO componentPermissionDTO = new ComponentPermissionDTO();
        componentPermissionDTO.setId(UUID.randomUUID().toString());
        componentPermissionDTO.setUserID(UserId);
        componentPermissionDTO.setGadgetTitle(TestConst.generateRandomString());
        componentPermissionDTO.setGadgetID(gadgetId);
        componentPermissionDTO.setEnabled(true);
        componentPermissionDTO.setPermission("test");

        return componentPermissionDTO;
    }

}
