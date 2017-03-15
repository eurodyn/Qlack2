/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.aaa.impl;

import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Group;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author European Dynamics SA
 */
public class UserGroupServiceImplTest {

    private EntityManager em;
    private EntityTransaction tr;
    private UserGroupService groupService;

    @BeforeClass
    public static void beforeClass() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }


    @Before
    public void setUp() throws Exception {

        em = AllAAATests.getEm();
        groupService = AllAAATests.getUserGroupService();

        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.clear();
        EntityTransaction cleanTables = em.getTransaction();
        cleanTables.begin();

        cleanUsers();
        clearGroupsTable();



        cleanTables.commit();

        if (tr == null){
            tr = em.getTransaction();
        }
        tr.begin();
    }

    private void cleanUsers() {

        em.createQuery("delete from User")
                .executeUpdate();
    }

    private void clearGroupsTable() {

        em.createQuery("delete from Group")
                .executeUpdate();
    }

    @After
    public void tearDown() throws Exception {

        if (tr.getRollbackOnly()){
            tr.rollback();
        }
        else
            tr.commit();
    }


    @Test
    public void createGroup() throws Exception {

        GroupDTO groupDTO = TestsUtil.createGroupDTO();

        String groupId = groupService.createGroup(groupDTO);

        Group groupFromDB = TestDBUtil.getGroupById(groupId);

        Assert.assertEquals(groupFromDB.getName(), groupDTO.getName());
    }

    @Test
    public void updateGroup() throws Exception {

        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);

        GroupDTO modifiedDTO = TestsUtil.createGroupDTO();

        groupService.updateGroup(modifiedDTO);

        Group groupFromDB = TestDBUtil.getGroupById(modifiedDTO.getId());

        Assert.assertEquals(modifiedDTO.getDescription(), groupFromDB.getDescription());

    }

    @Test(expected = NoResultException.class)
    public void deleteGroup() throws Exception {


        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);

        groupService.deleteGroup(group.getId());

        Group groupFromDB = TestDBUtil.getGroupById(group.getId());

    }

    @Test
    public void moveGroup() throws Exception {

        Group group = TestsUtil.createGroup();
        Group parentGroup = TestsUtil.createGroup();
        TestsUtil.modifyGroup(parentGroup, "parentGroup");

        Group changedParentGroup = TestsUtil.createGroup();
        TestsUtil.modifyGroup(changedParentGroup, "changedParentGroup");

        group.setParent(parentGroup);
        TestDBUtil.persistToGroup(parentGroup);
        TestDBUtil.persistToGroup(group);
        TestDBUtil.persistToGroup(changedParentGroup);

        groupService.moveGroup(group.getId(), changedParentGroup.getId());

        Assert.assertEquals(group.getParent().getId(), changedParentGroup.getId());
    }

    @Test
    public void getGroupByID() throws Exception {

        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);

        GroupDTO groupFromDB = groupService.getGroupByID(group.getId(), true);

        Assert.assertEquals(groupFromDB.getName(), group.getName());

    }

    @Test
    //Potential bug, fails to fetch group hierarchy
    public void getGroupHierarchyByID() throws Exception {

        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);

        GroupDTO groupFromDB = groupService.getGroupByID(group.getId(), false);

        Assert.assertEquals(groupFromDB.getName(), group.getName());

    }



    @Test
    public void getGroupByName() throws Exception {

        Group group = TestsUtil.createGroup();

        TestDBUtil.persistToGroup(group);

        Group groupFromDB = TestDBUtil.getGroupById(group.getId());

        GroupDTO groupFromApp = groupService.getGroupByName(group.getName(), true);

        Assert.assertEquals(groupFromApp.getName(), groupFromDB.getName());
    }

    @Test
    //Potential bug, throws NPE
    public void getGroupByObjectId() throws Exception {

        Group group = TestsUtil.createGroup();

        TestDBUtil.persistToGroup(group);

        Group groupFromDB = TestDBUtil.getGroupById(group.getId());

        GroupDTO groupFromApp = groupService.getGroupByObjectId(group.getObjectId(), true);

        Assert.assertEquals(groupFromApp.getName(), groupFromDB.getName());
    }

    @Test
    public void listGroups() throws Exception {

        Group group = TestsUtil.createGroup();
        Group parentGroup = TestsUtil.createGroup();
        TestsUtil.modifyGroup(group, "modifiedGroup");

        group.setParent(parentGroup);
        TestDBUtil.persistToGroup(parentGroup);
        TestDBUtil.persistToGroup(group);

        List<GroupDTO> allGroups = groupService.listGroups();

        Assert.assertEquals(2, allGroups.size());
    }

    @Test
    public void listGroupsAsTree() throws Exception {

        Group group = TestsUtil.createGroup();
        Group parentGroup = TestsUtil.modifyGroup(group, "parentGroup");

        group.setParent(parentGroup);
        TestDBUtil.persistToGroup(parentGroup);
        TestDBUtil.persistToGroup(group);

        List<GroupDTO> allGroups = groupService.listGroups();

        Assert.assertEquals(1, allGroups.size());
    }

    @Test
    public void getGroupParent() throws Exception {

        Group group = TestsUtil.createGroup();

        Group parentGroup = TestsUtil.createGroup();
        TestsUtil.modifyGroup(parentGroup, "parentGroup");

        group.setParent(parentGroup);
        TestDBUtil.persistToGroup(parentGroup);
        TestDBUtil.persistToGroup(group);


        GroupDTO parent = groupService.getGroupParent(group.getId());

        Assert.assertEquals(parent.getId(), parentGroup.getId());

    }

    @Test
    //Potential bug, does not work as exptected
    public void getGroupChildren() throws Exception {

        Group group = TestsUtil.createGroup();

        Group childGroup = TestsUtil.createGroup();
        TestsUtil.modifyGroup(group, "childGroup");

        List<Group> children = new ArrayList<>();
        children.add(childGroup);
        group.setChildren( children);

        TestDBUtil.persistToGroup(childGroup);
        TestDBUtil.persistToGroup(group);

        List<GroupDTO> childrenDTOs = groupService.getGroupChildren(group.getId());

        Assert.assertEquals(1, childrenDTOs.size());
    }

    @Test
    public void addUser() throws Exception {

        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);

        User user = TestsUtil.createUser();
        TestsUtil.modifyUser(user, "modifiedUser");
        TestDBUtil.persistToUser(user);

        groupService.addUser(user.getId(), group.getId());

        List<User> userList = group.getUsers();

        Assert.assertEquals(userList.get(0).getId(), user.getId());
    }

    @Test
    public void addUsers() throws Exception {

        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        User secondUser = TestsUtil.createUser();
        TestsUtil.modifyUser(secondUser, "modifiedUser");
        TestDBUtil.persistToUser(secondUser);

        List<String> userList = new ArrayList();
        userList.add(user.getId());
        userList.add(secondUser.getId());

        groupService.addUsers(userList, group.getId());

        Group groupFromDB = TestDBUtil.getGroupById(group.getId());

        List<User> groupUsers = groupFromDB.getUsers();

        Assert.assertEquals(2, groupUsers.size());

    }

    @Test
    public void addUserByGroupName() throws Exception {

        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);
        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        groupService.addUserByGroupName(user.getId(), group.getName());

        List<User> userList = group.getUsers();

        Assert.assertEquals(userList.size(), 1);
    }

    @Test
    public void addUsersByGroupName() throws Exception {

        Group group = TestsUtil.createGroup();
        TestDBUtil.persistToGroup(group);

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        User secondUser = TestsUtil.createUser();
        TestsUtil.modifyUser(secondUser, "secondUser");
        TestDBUtil.persistToUser(secondUser);

        List<String> userList = new ArrayList();
        userList.add(user.getId());
        userList.add(secondUser.getId());

        groupService.addUsersByGroupName(userList, group.getName());

        Group groupFromDB = TestDBUtil.getGroupById(group.getId());

        List<User> groupUsers = groupFromDB.getUsers();

        Assert.assertEquals(2, groupUsers.size());
    }

    @Test
    public void removeUser() throws Exception {

        Group group = TestsUtil.createGroup();
        User user = TestsUtil.createUser();

        List<User> userList = new ArrayList();
        userList.add(user);
        group.setUsers(userList);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToGroup(group);

        groupService.removeUser(user.getId(), group.getId());

        Group groupFromDB = TestDBUtil.getGroupById(group.getId());

        Assert.assertEquals(0,groupFromDB.getUsers().size());
    }

    @Test
    public void removeUsers() throws Exception {

        Group group = TestsUtil.createGroup();
        User user = TestsUtil.createUser();

        User secondUser = TestsUtil.createUser();
        TestsUtil.modifyUser(user, "modifiedUser");

        List<User> userList = new ArrayList();
        List<String> userIds = new ArrayList<>();

        userList.add(user);
        userList.add(secondUser);

        userIds.add(user.getId());
        userIds.add(secondUser.getId());
        group.setUsers(userList);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToUser(secondUser);
        TestDBUtil.persistToGroup(group);

        groupService.removeUsers(userIds, group.getId());

        Group groupFromDB = TestDBUtil.getGroupById(group.getId());

        Assert.assertEquals(groupFromDB.getUsers().size(), 0);
    }

    @Test
    public void getGroupUsersIds() throws Exception {

        Group group = TestsUtil.createGroup();
        User user = TestsUtil.createUser();

        List<User> userList = new ArrayList();
        userList.add(user);
        group.setUsers(userList);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToGroup(group);

        Set<String> userIds = groupService.getGroupUsersIds(group.getId(),false);

        Assert.assertEquals(userIds.size(),1);

    }

    @Test
    public void getUserGroupsIds() throws Exception {

        Group group = TestsUtil.createGroup();
        User user = TestsUtil.createUser();

        List<User> userList = new ArrayList();
        userList.add(user);
        group.setUsers(userList);

        List<Group> groupList = new ArrayList<>();
        groupList.add(group);
        user.setGroups(groupList);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToGroup(group);

        Set<String> groupIds = groupService.getUserGroupsIds(user.getId());

        Assert.assertEquals(groupIds.size(), 1);
    }

}