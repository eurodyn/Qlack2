package com.eurodyn.qlack2.fuse.aaa.impl;

import com.eurodyn.qlack2.fuse.aaa.api.dto.*;
import com.eurodyn.qlack2.fuse.aaa.impl.model.*;
import com.eurodyn.qlack2.fuse.aaa.impl.util.ConverterUtil;

import java.util.UUID;

/**
 *
 * @author European Dynamics SA
 */
public class TestsUtil {

    public static OpTemplateDTO createOpTemplateDTO(){

        OpTemplateDTO opTemplate = new OpTemplateDTO();
        opTemplate.setId(TestConstants.OPTEMPLATE_ID);
        opTemplate.setDescription(TestConstants.OPTEMPLATE_DESCRIPTION);
        opTemplate.setName(TestConstants.OPTEMPLATE_NAME);

        return opTemplate;

    }


    public static Operation createOperation(){

        Operation operation = new Operation();
        operation.setId(TestConstants.OPERATION_ID);
        operation.setName(TestConstants.OPERATION_NAME);
        operation.setDescription(TestConstants.OPERATION_DESCRIPTION);
        operation.setDynamic(TestConstants.OPERATION_DYNAMIC);
        operation.setDynamicCode(TestConstants.OPERATION_DYNAMIC_CODE);

        return operation;
    }

    public static OpTemplate createOpTemplate(){

        OpTemplate opTemplate = new OpTemplate();
        opTemplate.setId(TestConstants.OPTEMPLATE_ID);
        opTemplate.setDescription(TestConstants.OPTEMPLATE_DESCRIPTION);
        opTemplate.setName(TestConstants.OPTEMPLATE_NAME);

        return opTemplate;

    }

    public static GroupHasOperation createGroupHasOperation(){

        GroupHasOperation groupHasOperation = new GroupHasOperation();
        groupHasOperation.setId(TestConstants.GROUPHASOPERATION_ID);
        groupHasOperation.setDeny(TestConstants.GROUPHASOPERATION_DENY);

        return groupHasOperation;
    }

    public static OpTemplateHasOperation createOpTemplateHasOperation(){

        OpTemplateHasOperation opTemplateHasOperation = new OpTemplateHasOperation();

        opTemplateHasOperation.setDeny(TestConstants.OPTEMPLATEHASOPERATION_DENY);
        opTemplateHasOperation.setId(TestConstants.OPTEMPLATEHASOPERATION_ID);

        return opTemplateHasOperation;
    }




    public static SessionDTO createSessionDTO(){

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(TestConstants.SESSIONDTOID);
        sessionDTO.setApplicationSessionID(TestConstants.APPLICATION_SESSION_ID);
        sessionDTO.setTerminatedOn(TestConstants.DATE_TERMINATED_ON);
        sessionDTO.setUserId(TestConstants.SESSION_USER_ID);
        sessionDTO.setCreatedOn(TestConstants.DATE_CREATED_ON);

        return sessionDTO;

    }

    public static UserDTO createUserDTO(){

        UserDTO userDTO = new UserDTO();
        userDTO.setId(TestConstants.USER_ID);

        UserAttributeDTO attributeDTO = ConverterUtil.userAttributeToUserAttributeDTO(TestsUtil.createuserAttribute());
        userDTO.setAttribute(attributeDTO);

        userDTO.setExternal(TestConstants.USER_EXTERNAL);
        userDTO.setPassword(TestConstants.USER_PASSWORD);
        userDTO.setSuperadmin(TestConstants.USER_SUPERADMIN);

        userDTO.setUsername(TestConstants.USER_NAME);

        return  userDTO;
    }


    public static SessionAttributeDTO createSessionAttributeDTO(){

        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setId(TestConstants.SESSION_ATTRIBUTE_ID);
        sessionAttributeDTO.setName(TestConstants.SESSION_ATTRIBUTE_NAME);
        sessionAttributeDTO.setSessionId(TestConstants.SESSION_ID);
        sessionAttributeDTO.setValue(TestConstants.SESSION_ATTRIBUTE_VALUE);

        return sessionAttributeDTO;
    }

    public static UserDTO modifyUserDTO(UserDTO user , String modifiedValue) {

        UserDTO userDTO = new UserDTO();
        userDTO.setUserAttributes(user.getUserAttributes());
        userDTO.setExternal(true);
        userDTO.setPassword(modifiedValue);
        userDTO.setSuperadmin(true);
        userDTO.setUserAttributes(null);
        userDTO.setUsername(modifiedValue);

        return userDTO;
    }

    public static User createUser() {

        User user = new User();
        user.setUsername(TestConstants.USER_NAME);
        user.setPassword(TestConstants.USER_PASSWORD);
        user.setId(TestConstants.USER_ID);
        user.setSuperadmin(TestConstants.USER_SUPERADMIN);
        user.setExternal(TestConstants.USER_EXTERNAL);
        user.setGroups(null);
        user.setSalt(TestConstants.USER_SALT);

        return user;
    }

    public static Group createGroup(){

        Group group = new Group();

        group.setId(TestConstants.GROUP_ID);
        group.setUsers(null);
        group.setChildren(null);
        group.setParent(null);
        group.setDescription(TestConstants.GROUP_DESCRIPTION);
        group.setName(TestConstants.GROUP_NAME);
        group.setObjectId(TestConstants.GROUP_OBJECT_ID);

        return  group;
    }

    public static GroupDTO modifyGroupDTO(GroupDTO groupDTO, String modifiedValue) {

        groupDTO.setName(modifiedValue);
        groupDTO.setDescription(modifiedValue);

        return groupDTO;
    }

    public static User modifyUser(User user, String modifiedUser) {

        user.setId(UUID.randomUUID().toString());
        user.setSalt(modifiedUser);
        user.setExternal(false);
        user.setSuperadmin(false);
        user.setPassword(modifiedUser);
        user.setUsername(modifiedUser);

        return user;
    }

    public static Group modifyGroup(Group mGroup, String modifiedGroup) {

        mGroup.setId(UUID.randomUUID().toString());
        mGroup.setDescription(modifiedGroup);
        mGroup.setName(modifiedGroup);
        mGroup.setObjectId(modifiedGroup);

        return mGroup;


    }

    public static UserAttribute createuserAttribute() {

        UserAttribute userAttribute = new UserAttribute();
        userAttribute.setId(TestConstants.USERATTRIBUTE_ID);
        userAttribute.setName(TestConstants.USERATTRIBUTE_NAME);
        userAttribute.setBindata(TestConstants.USERATTRIBUTE_BIND_DATA);
        userAttribute.setData(TestConstants.USERATTRIBUTE_DATA);

        return userAttribute;
    }

    public static UserAttribute newUserAtrribute(String modifiedUserAttribute) {

        UserAttribute newUserAttribute = new UserAttribute();
        newUserAttribute.setData(modifiedUserAttribute);
        newUserAttribute.setName(modifiedUserAttribute);
        newUserAttribute.setBindata(new byte[]{});
        newUserAttribute.setId(modifiedUserAttribute);
        return newUserAttribute;

    }

    public static Session createUserSession() {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        Session session = new Session();
        session.setId(TestConstants.SESSION_ID);;
        session.setApplicationSessionId(TestConstants.SESSION_APPLICATION_ID);
        session.setCreatedOn(TestConstants.SESSION_CREATED_ON);
        session.setTerminatedOn(null);
        session.setUser(user);

        TestDBUtil.persistToSession(session);

        return session;
    }

    public static SessionAttribute createSessionAttribute(){

        SessionAttribute sessionAttribute = new SessionAttribute();
        sessionAttribute.setId(TestConstants.SESSIONATT_ID);
        sessionAttribute.setName(TestConstants.SESSIONATT_NAME);
        sessionAttribute.setValue(TestConstants.SESSIONATT_VALUE);

        return sessionAttribute;
    }

    public static GroupDTO createGroupDTO() {

        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(TestConstants.GROUP_ID);
        groupDTO.setDescription(TestConstants.GROUP_DESCRIPTION);
        groupDTO.setName(TestConstants.GROUP_NAME);
        groupDTO.setObjectID(TestConstants.GROUP_OBJECT_ID);

        return groupDTO;
    }

    public static void modifySessionAttributeDTO(SessionAttributeDTO secondSessionAttributeDTO, String secondSessionAttributeDTO1) {

        String randomString = UUID.randomUUID().toString();
        secondSessionAttributeDTO.setId(randomString);
        secondSessionAttributeDTO.setName(randomString);
        secondSessionAttributeDTO.setValue(randomString);

    }

    public static OperationDTO createOperationDTO() {

        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setId(TestConstants.OPERATION_ID);
        operationDTO.setName(TestConstants.OPERATION_NAME);
        operationDTO.setDescription(TestConstants.OPERATION_DESCRIPTION);
        operationDTO.setDynamic(TestConstants.OPERATION_DYNAMIC);
        operationDTO.setDynamicCode(TestConstants.OPERATION_DYNAMIC_CODE);

        return operationDTO;
    }

    public static void modifyOperationDTO(OperationDTO operationDTO, String modifiedValue) {

        operationDTO.setDynamicCode(modifiedValue);
        operationDTO.setDynamic(false);
        operationDTO.setName(modifiedValue);
        operationDTO.setDescription(modifiedValue);
    }

    public static Resource createResource() {

        Resource resource = new Resource();

        resource.setDescription(TestConstants.RESOURCE_DESCRIPTION);
        resource.setId(TestConstants.RESOURCE_ID);
        resource.setName(TestConstants.RESOURCE_NAME);
        resource.setObjectId(TestConstants.RESOURCE_OBJECT_ID);

        return resource;
    }

    public static UserHasOperation createUserHasOperation() {

        UserHasOperation uho = new UserHasOperation();
        uho.setId(TestConstants.USERHASOPERATION_ID);
        uho.setDeny(TestConstants.USERHASOPERATION_DENY);

        return uho;
    }

    public static VerificationToken createVerificationToken() {

        VerificationToken verificationToken = new VerificationToken();

        verificationToken.setId(TestConstants.VERIFICATION_ID);
        verificationToken.setCreatedOn(TestConstants.VERIFICATION_CREATED_ON);
        verificationToken.setExpiresOn(TestConstants.VERIFICATION_EXPIRES_ON);
        verificationToken.setData(TestConstants.VERIFICATION_DATA);

        return verificationToken;
    }

    public static ResourceDTO createResourceDTO() {

        ResourceDTO resourceDTO = new ResourceDTO();
        resourceDTO.setId(TestConstants.RESOURCE_ID);
        resourceDTO.setDescription(TestConstants.RESOURCE_DESCRIPTION);
        resourceDTO.setName(TestConstants.RESOURCE_NAME);
        resourceDTO.setObjectID(TestConstants.RESOURCE_OBJECT_ID);

        return resourceDTO;
    }

    public static void modifyResourceDTO(ResourceDTO modifiedResource) {

        String randomString = UUID.randomUUID().toString();
        modifiedResource.setName(randomString);
        modifiedResource.setDescription(randomString);
        modifiedResource.setObjectID(randomString);
    }

    public static void modifyResource(Resource secondResource) {

        String randomString = UUID.randomUUID().toString();
        secondResource.setName(randomString);
        secondResource.setDescription(randomString);
        secondResource.setObjectId(randomString);
        secondResource.setId(randomString);
    }

    public static void modifyOpTemplateDTO(OpTemplateDTO modifiedDTO) {

        String randomString = UUID.randomUUID().toString();
        modifiedDTO.setName(randomString);
        modifiedDTO.setDescription(randomString);
    }
}
