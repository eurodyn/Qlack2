package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import com.eurodyn.qlack2.fuse.aaa.api.OpTemplateService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.junit.Assert;
import javax.inject.Inject;
import org.ops4j.pax.exam.util.Filter;
import java.util.UUID;


/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class OperationServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    OperationService operationService;

    @Inject
    @Filter(timeout = 1200000)
    UserService userService;

    @Inject
    @Filter(timeout = 1200000)
    ResourceService resourceService;

    @Inject
    @Filter(timeout = 1200000)
    OpTemplateService opTemplateService;

    @Inject
    @Filter(timeout = 1200000)
    UserGroupService userGroupService;



    @Test
    public void createOperation(){
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);
        Assert.assertNotNull(operationDTO.getName());
    }

    /*
    @Test
    public void getOperationByName() throws Exception {
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        OperationDTO getOperationDTO = operationService.getOperationByName(operationDTO.getName());
        Assert.assertEquals(operationDTO.getName(),getOperationDTO.getName());
    }


    @Test
    public void deleteOperation() throws Exception {
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        operationService.deleteOperation(operationID);
        Assert.assertNull(operationService.getOperationByID(operationID));
    }


    @Test
    public void updateOperation() throws Exception {
        //creates Operation with specific values
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setId(UUID.randomUUID().toString());
        operationDTO.setName("update");
        operationDTO.setDescription("update");
        operationDTO.setDynamic(TestConst.OPERATION_DYNAMIC);
        operationDTO.setDynamicCode(TestConst.OPERATION_DYNAMIC_CODE);
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        //find and change description and name
        OperationDTO operationUpdDTO = operationService.getOperationByName("update");
        Assert.assertNotNull(operationUpdDTO );

        operationUpdDTO.setName("test-updated");
        operationUpdDTO.setDescription("test-updated");

        //updates Operation with new values
        operationService.updateOperation(operationUpdDTO);
        OperationDTO operationUpdName = operationService.getOperationByName("test-updated");
        Assert.assertNotEquals(operationUpdName.getName(),operationDTO.getName());
    }


    @Test
    public void getOperationByID() throws Exception {
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        OperationDTO operationGetDTO = operationService.getOperationByID(operationID);
        Assert.assertNotNull(operationGetDTO.getId());

        Assert.assertEquals(operationID,operationGetDTO.getId());
    }


    @Test
    public void addOperationToUser() throws Exception {
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        operationService.addOperationToUser(userID, operationDTO.getName(), false);
        Assert.assertNotNull(operationService.getPermittedOperationsForUser(userID,false));
    }


    @Test
    public void addOperationToUserArgs() throws Exception {
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        Assert.assertNotNull(operationID);

        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        operationService.addOperationToUser(userID, operationDTO.getName(),resourceID, false);
        Assert.assertNotNull(operationService.getPermittedOperationsForUser(userID,resourceID,false));
    }


    @Test
    public void addOperationsToUserFromTemplateID() throws Exception {
        //creates OpTemplate using opTemplateService
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        Assert.assertNotNull(opTemplateID );

        //creates User using userService
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        operationService.addOperationsToUserFromTemplateID(userID, opTemplateID);
        Assert.assertNotNull(operationService.getPermittedOperationsForUser(userID,false));
    }


    @Test
    public void addOperationsToUserFromTemplateName() throws Exception {
        //creates OpTemplate using opTemplateService
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        String opTemplateName = opTemplateDTO.getName();
        Assert.assertNotNull(opTemplateID);

        //creates User using userService
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        operationService.addOperationsToUserFromTemplateID(userID, opTemplateID);
        Assert.assertNotNull(operationService.getAllowedGroupsForOperation(opTemplateName,true));
    }


    @Test
    public void addOperationToGroup() throws Exception {
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        operationService.addOperationToGroup(groupID,operationName,true);
        Assert.assertNotNull(operationService.getAllowedGroupsForOperation(operationName,true));
    }


    @Test
    public void addOperationsToGroupFromTemplateID(){
        //creates OpTemplate
        OpTemplateDTO opTemplateDTO = TestUtilities.createOpTemplateDTO();
        String opTemplateID = opTemplateService.createTemplate(opTemplateDTO);
        String opTemplateName = opTemplateDTO.getName();
        Assert.assertNotNull(opTemplateID);

        //creates Group
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        operationService.addOperationsToGroupFromTemplateID(groupID,opTemplateID);
        Assert.assertNotNull(operationService.getAllowedGroupsForOperation(opTemplateName,true));
    }


    @Test
    public void removeOperationFromUser(){
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        operationService.removeOperationFromUser(userID,operationName);

        Assert.assertNotNull(operationService.getPermittedOperationsForUser(userID,false));
    }


    @Test
    public void removeOperationFromUserArgs(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationRemUser = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationRemUser);

        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        operationService.removeOperationFromUser(userID,operationName,resourceID);

        //Assert
        Assert.assertNotNull(operationService.getPermittedOperationsForUser(userID,resourceID,false));
    }


    @Test
    public void removeOperationFromGroup(){
        //creates Group using userGroupService
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        String groupRemGroupName = groupDTO.getName();
        Assert.assertNotNull(groupID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        operationService.removeOperationFromGroup(groupID,operationName);
        Assert.assertNotNull(operationService.getAllowedGroupsForOperation(operationName,false));
    }


    @Test
    public void removeOperationFromGroupArgs(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        String groupName = groupDTO.getName();
        Assert.assertNotNull(groupID);

        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        operationService.addOperationToGroup(groupID,operationName,true);

        operationService.removeOperationFromGroup(groupID,operationName,resourceID);

        //assert empty
        Assert.assertTrue(operationService.getAllowedGroupsForOperation(operationName,resourceDTO.getObjectID(),false).isEmpty() );
    }


    @Test
    public void isPermitted(){
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID= operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNull(operationService.isPermitted(userID,operationName,null));
    }


    @Test
    public void isPermittedForGroup() {
        //creates Group
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        String groupName = groupDTO.getName();
        Assert.assertNotNull(groupID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNull(operationService.isPermittedForGroup(groupID,operationName,null));
    }


    @Test
    public void getAllowedUsersForOperation(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNotNull(operationService.getAllowedUsersForOperation(operationName,true));
    }


    @Test
    public void getAllowedUsersForOperationArgs(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationPermGroupName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNotNull(operationService.getAllowedUsersForOperation(operationPermGroupName,resourceDTO.getObjectID(),true));
    }


    @Test
    public void getBlockedUsersForOperation(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNotNull(operationService.getAllowedUsersForOperation(operationName,false));
    }


    @Test
    public void getBlockedUsersForOperationArgs(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationPermGroupName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNotNull(operationService.getAllowedUsersForOperation(operationPermGroupName,resourceDTO.getObjectID(),true));
    }


    @Test
    public void getAllowedGroupsForOperation(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNotNull(operationService.getAllowedGroupsForOperation(operationName,true) );
    }


    @Test
    public void getAllowedGroupsForOperationArgs(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNotNull(operationService.getAllowedGroupsForOperation(operationName,resourceDTO.getObjectID(),true) );
    }


    @Test
    public void getBlockedGroupsForOperation(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNotNull(operationService.getBlockedGroupsForOperation(operationName,true) );
    }


    @Test
    public void getBlockedGroupsForOperationArgs(){
        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNotNull(operationService.getBlockedGroupsForOperation(operationName,resourceDTO.getObjectID(),true) );
    }


    @Test
    public void getPermittedOperationsForUser(){
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Assert.assertNotNull(operationService.getPermittedOperationsForUser(userID,true));
    }


    @Test
    public void getPermittedOperationsForUserArgs(){
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Resource
        ResourceDTO resourceDTO = TestUtilities.createResourceDTO();
        String resourceID = resourceService.createResource(resourceDTO);
        Assert.assertNotNull(resourceID);

        Assert.assertNotNull(operationService.getPermittedOperationsForUser(userID,resourceID,true));
    }


    @Test
    public void getResourceForOperation(){
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNotNull(operationService.getResourceForOperation(userID,operationName,true));
    }


    @Test
    public void getGroupIDsByOperationAndUser(){
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Operation
        OperationDTO operationDTO = TestUtilities.createOperationDTO();
        String operationID = operationService.createOperation(operationDTO);
        String operationName = operationDTO.getName();
        Assert.assertNotNull(operationID);

        Assert.assertNull(operationService.getGroupIDsByOperationAndUser(operationName,userID));
    }
    */

}


