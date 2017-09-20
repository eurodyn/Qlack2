package com.eurodyn.qlack2.fuse.aaa;

import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.aaa.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.aaa.util.TestConst;
import com.eurodyn.qlack2.fuse.aaa.util.TestUtilities;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import java.util.*;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class UserGroupServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    UserGroupService userGroupService;

    @Inject
    @Filter(timeout = 1200000)
    UserService userService;

    @Test
    public void createGroup(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);
    }

    @Test
    public void updateGroup(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        groupDTO.setName("upd");
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        //find and change description and name
        GroupDTO groupUpdDTO = userGroupService.getGroupByName("upd",true);
        Assert.assertNotNull(groupUpdDTO);

        groupUpdDTO.setName("updated");
        groupUpdDTO.setDescription("updated");

        //update
        userGroupService.updateGroup(groupUpdDTO);
        GroupDTO groupToUpdGroupName = userGroupService.getGroupByName("updated",true);
        Assert.assertNotEquals(groupDTO.getName(),groupToUpdGroupName.getName());
    }

    @Test
    public void deleteGroup(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        userGroupService.deleteGroup(groupID);
        Assert.assertNull(userGroupService.getGroupByID(groupID,true));
    }

    @Test
    public void moveGroup(){
        //creates Group
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(UUID.randomUUID().toString());
        groupDTO.setDescription(com.eurodyn.qlack2.fuse.aaa.util.TestConst.GROUP_DESCRIPTION);
        groupDTO.setName(com.eurodyn.qlack2.fuse.aaa.util.TestConst.generateRandomString());
        groupDTO.setObjectID(UUID.randomUUID().toString());
        Set<GroupDTO> listPar = new HashSet();
        listPar.add(groupDTO);
        groupDTO.setChildren(listPar);
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        GroupDTO groupMvGroupDTO = new GroupDTO();
        groupMvGroupDTO.setId(UUID.randomUUID().toString());
        groupMvGroupDTO.setDescription(com.eurodyn.qlack2.fuse.aaa.util.TestConst.GROUP_DESCRIPTION);
        groupMvGroupDTO.setName(TestConst.generateRandomString());
        groupMvGroupDTO.setObjectID(UUID.randomUUID().toString());
        Set<GroupDTO> list = new HashSet();
        list.add(groupMvGroupDTO);
        groupMvGroupDTO.setChildren(list);
        String groupMvGroupID = userGroupService.createGroup(groupMvGroupDTO);
        Assert.assertNotNull(groupID);

        //check if has parent
        Assert.assertNull(groupMvGroupDTO.getParent());

        //assign new parent
        userGroupService.moveGroup(groupMvGroupID,groupID);

        GroupDTO checkParentID = userGroupService.getGroupByID(groupMvGroupID,true);
        Assert.assertNotNull(checkParentID);
    }

    @Test
    public void getGroupByID(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(userGroupService.getGroupByID(groupID,true));
    }

    @Test
    public void getGroupsByID(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        List<String> groups = new ArrayList<>();
        groups.add(groupID);

        Assert.assertNotNull(userGroupService.getGroupsByID(groups,true));
    }

    @Test
    public void getGroupByName(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(userGroupService.getGroupByName(groupDTO.getName(),true));
        Assert.assertNotNull(userGroupService.getGroupByName(groupDTO.getName(),false));
    }

    @Test
    public void getGroupByObjectId(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(userGroupService.getGroupByObjectId(groupDTO.getObjectID(),true));
        Assert.assertNotNull(userGroupService.getGroupByObjectId(groupDTO.getObjectID(),false));
    }

    @Test
    public void listGroups(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(userGroupService.listGroups());
    }

    @Test
    public void listGroupsAsTree(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(userGroupService.listGroupsAsTree());
    }

    @Test
    public void getGroupParent(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        //check if groupParentID has parent, expected=null
        GroupDTO groupGetDTO = userGroupService.getGroupByID(groupID,true);
        Assert.assertNull(userGroupService.getGroupParent(groupGetDTO.getId()));

        GroupDTO groupGetParentDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        //assign parent to this groupDTO
        groupGetParentDTO.setParent(groupDTO);
        String groupGetParentID = userGroupService.createGroup(groupGetParentDTO);
        Assert.assertNotNull(groupGetParentID);

        //checks if groupGetParentID has parent, expected=null
        GroupDTO findGroupDTO = userGroupService.getGroupByID(groupGetParentID,true);
        Assert.assertNotNull(userGroupService.getGroupParent(findGroupDTO.getId()));
    }

    @Test
    public void getGroupChildren(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(userGroupService.getGroupChildren(groupID));
    }

    @Test
    public void addUser(){
        //creates User
        UserDTO userDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        userGroupService.addUser(userID,groupID);

        Assert.assertNotNull(userGroupService.getGroupUsersIds(groupID,true));
    }

    @Test
    public void addUsers(){
        //creates User
        UserDTO userDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        List<String> users = new ArrayList();
        users.add(userID);

        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        userGroupService.addUsers(users,groupID);
        Assert.assertNotNull(userGroupService.getGroupUsersIds(groupID,true));
    }

    @Test
    public void addUserByGroupName(){
        //creates User
        UserDTO userDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        userGroupService.addUserByGroupName(userID,groupDTO.getName());

        Assert.assertNotNull(userGroupService.getUserGroupsIds(userID));
    }

    @Test
    public void addUsersByGroupName(){
        //creates User
        UserDTO usersDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createUserDTO();
        String usersID = userService.createUser(usersDTO);
        Assert.assertNotNull(usersID);

        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        List<String> users = new ArrayList();
        users.add(usersID);

        userGroupService.addUsersByGroupName(users,groupDTO.getName());

        Assert.assertNotNull(userGroupService.getUserGroupsIds(usersID));
    }

    @Test
    public void removeUser(){
        //creates User
        UserDTO userDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        userGroupService.addUserByGroupName(userID,groupDTO.getName());
        userGroupService.removeUser(userID,groupID);
        Assert.assertTrue(userGroupService.getUserGroupsIds(userID).isEmpty());
    }

    @Test
    public void removeUsers(){
        //creates User
        UserDTO userDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupRemoveUsersId = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupRemoveUsersId);

        List<String> usersList = new ArrayList();
        usersList.add(userID);

        userGroupService.addUsersByGroupName(usersList,groupDTO.getName());
        userGroupService.removeUsers(usersList,groupRemoveUsersId);
        Assert.assertTrue(userGroupService.getUserGroupsIds(userID).isEmpty());
    }

    @Test
    public void getGroupUsersIds(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        //creates User
        UserDTO userDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userDTO);

        userGroupService.addUserByGroupName(userID,groupDTO.getName());
        Assert.assertNotNull(userGroupService.getGroupUsersIds(groupID,true));
    }

    @Test
    public void getUserGroupsIds(){
        //creates Group
        GroupDTO groupDTO = com.eurodyn.qlack2.fuse.aaa.util.TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        userGroupService.addUserByGroupName(userID,groupDTO.getName());
        Assert.assertNotNull( userGroupService.getUserGroupsIds(userID) );
    }

}
