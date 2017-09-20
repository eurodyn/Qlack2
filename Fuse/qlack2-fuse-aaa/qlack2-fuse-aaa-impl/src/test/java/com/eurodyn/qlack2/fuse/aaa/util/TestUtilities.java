package com.eurodyn.qlack2.fuse.aaa.util;

import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TestUtilities {

    public static OperationDTO createOperationDTO(){
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setId(UUID.randomUUID().toString());
        operationDTO.setName(TestConst.generateRandomString());
        operationDTO.setDescription(TestConst.OPERATION_DESCRIPTION);
        operationDTO.setDynamic(TestConst.OPERATION_DYNAMIC);
        operationDTO.setDynamicCode(TestConst.OPERATION_DYNAMIC_CODE);

        return operationDTO;
    }

    public static ResourceDTO createResourceDTO() {
        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(UUID.randomUUID().toString());
        resourceDTO.setDescription(TestConst.RESOURCE_DESCRIPTION);
        resourceDTO.setName(TestConst.generateRandomString());
        resourceDTO.setObjectID(UUID.randomUUID().toString());

        return resourceDTO;
    }

    public static void modifyOperationDTO(OperationDTO operationDTO, String modifiedValue) {
        operationDTO.setDynamicCode(modifiedValue);
        operationDTO.setDynamic(false);
        operationDTO.setName(modifiedValue);
        operationDTO.setDescription(modifiedValue);
    }

    public static UserDTO createUserDTO(){
        Byte status = 100;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setStatus(status);

        //sets AttributeDTO
        UserAttributeDTO attributeDTO = new UserAttributeDTO();
        attributeDTO.setId(UUID.randomUUID().toString());
        attributeDTO.setName(TestConst.generateRandomString());
        attributeDTO.setUserId(UUID.randomUUID().toString());

        userDTO.setAttribute(attributeDTO);
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());

        return  userDTO;
    }

    public static UserAttributeDTO createuserAttributeDTO() {
        UserAttributeDTO userAttributeDTO = new UserAttributeDTO();
        userAttributeDTO.setId(UUID.randomUUID().toString());
        userAttributeDTO.setName(TestConst.generateRandomString());
        userAttributeDTO.setData(TestConst.USERATTRIBUTE_DATA);

        return userAttributeDTO;
    }

    public static OpTemplateDTO createOpTemplateDTO(){
        OpTemplateDTO opTemplate = new OpTemplateDTO();
        opTemplate.setId(UUID.randomUUID().toString());
        opTemplate.setDescription(TestConst.OPTEMPLATE_DESCRIPTION);
        opTemplate.setName(TestConst.generateRandomString());

        return opTemplate;
    }

    public static GroupDTO createGroupDTO() {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(UUID.randomUUID().toString());
        groupDTO.setDescription(TestConst.GROUP_DESCRIPTION);
        groupDTO.setName(TestConst.generateRandomString());
        groupDTO.setObjectID(UUID.randomUUID().toString());

        Set<GroupDTO> list = new HashSet();
        list.add(groupDTO);

        groupDTO.setChildren(list);

        return groupDTO;
    }

    public static SessionAttributeDTO createSessionAttributeDTO(){
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setName(TestConst.generateRandomString());
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setValue(TestConst.SESSION_ATTRIBUTE_VALUE);

        return sessionAttributeDTO;
    }

    public static SessionDTO createSessionDTO(){
        //creates user
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());

        //creates SessionDTO
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setUserId(userDTO.getId());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);

        return sessionDTO;
    }

}
