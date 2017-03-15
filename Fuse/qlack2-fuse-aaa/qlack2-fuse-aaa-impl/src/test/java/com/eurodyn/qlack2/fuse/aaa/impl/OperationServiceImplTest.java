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

import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OperationDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.*;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author European Dynamics SA
 */
public class OperationServiceImplTest {

    private EntityManager em;
    private EntityTransaction tr;
    private OperationService operationService;

    @BeforeClass
    public static void beforeClass() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }

    @Before
    public void setup() throws Exception {

        em = AllAAATests.getEm();
        operationService = AllAAATests.getOperationService();

        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.clear();
        EntityTransaction sanitizeDB = em.getTransaction();
        sanitizeDB.begin();

        TestDBUtil.cleanTable("Resource");
        TestDBUtil.cleanTable("UserHasOperation");
        TestDBUtil.cleanTable("GroupHasOperation");
        TestDBUtil.cleanTable("User");
        TestDBUtil.cleanTable("Group");
        TestDBUtil.cleanOpTemplate();
        TestDBUtil.cleanOpTemplateHasOperation();
        TestDBUtil.cleanOperation();
        sanitizeDB.commit();

        if (tr == null){
            tr = em.getTransaction();
        }
        tr.begin();
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
    public void createOperation() throws Exception {

        OperationDTO operationDTO = TestsUtil.createOperationDTO();
        operationService.createOperation(operationDTO);

        Operation operation = TestDBUtil.fetchOperationFromDB();

        Assert.assertEquals(operation.getName(), operationDTO.getName());
    }

    @Test
    public void updateOperation() throws Exception {

        Operation operation = TestsUtil.createOperation();

        TestDBUtil.persistToDB(operation);

        OperationDTO operationDTO = TestsUtil.createOperationDTO();
        TestsUtil.modifyOperationDTO(operationDTO, UUID.randomUUID().toString());

        operationDTO.setId(operation.getId());
        operationService.updateOperation(operationDTO);

        Operation operationFromDB = TestDBUtil.fetchOperationFromDB();
        Assert.assertEquals(operationFromDB.getName(), operationDTO.getName());
    }

    @Test(expected = NoResultException.class)
    public void deleteOperation() throws Exception {

        Operation operation = TestsUtil.createOperation();
        TestDBUtil.persistToDB(operation);

        operationService.deleteOperation(operation.getId());
        TestDBUtil.fetchOperationFromDB();

    }

    @Test
    public void getOperationByName() throws Exception {

        Operation operation = TestsUtil.createOperation();
        TestDBUtil.persistToDB(operation);

        OperationDTO operatonFromDB = operationService.getOperationByName(operation.getName());

        Assert.assertEquals(operatonFromDB.getName(), operation.getName());
    }

    @Test
    //Potential bug.. NPE thrown
    public void addOperationToUser() throws Exception {

        User user = TestsUtil.createUser();
        Operation operation = TestsUtil.createOperation();

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);

        operationService.addOperationToUser(user.getId(), operation.getName(), false);

        List<UserHasOperation> userHasOperation = user.getUserHasOperations();

        Assert.assertTrue(userHasOperation.size() > 0);

    }

    @Test
    public void addOperationToUser1() throws Exception {

        User user = TestsUtil.createUser();
        Operation operation = TestsUtil.createOperation();
        Resource resource = TestsUtil.createResource();

        TestDBUtil.persistToDB(operation);

        operationService.addOperationToUser(user.getId(), operation.getName(), false);

        List<UserHasOperation> userHasOperation = user.getUserHasOperations();

        Assert.assertTrue(userHasOperation.size() > 0);
    }

    @Test
    public void addOperationsToUserFromTemplateID() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        Operation operation = TestsUtil.createOperation();
        List<Operation> operationsList = new ArrayList<>();
        operationsList.add(operation);

        UserHasOperation userHasOperation = TestsUtil.createUserHasOperation();
        userHasOperation.setOperation(operation);

        List<UserHasOperation> userHasOperationList = new ArrayList<>();
        userHasOperationList.add(userHasOperation);

        OpTemplateHasOperation opTemplateHasOperation = TestsUtil.createOpTemplateHasOperation();
        opTemplateHasOperation.setTemplate(opTemplate);
        opTemplateHasOperation.setOperation(operation);

        List<OpTemplateHasOperation> templateOperations = new ArrayList<>();
        templateOperations.add(opTemplateHasOperation);
        opTemplate.setOpTemplateHasOperations(templateOperations);

        User user = TestsUtil.createUser();
        userHasOperation.setUser(user);
        user.setUserHasOperations(userHasOperationList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(opTemplate);
        TestDBUtil.persistToDB(opTemplateHasOperation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(userHasOperation);
        operationService.addOperationsToUserFromTemplateID(user.getId(), opTemplate.getId());

        List<UserHasOperation> userHasOperationsList = user.getUserHasOperations();

        Assert.assertTrue(userHasOperationsList.size() > 0);

    }

    @Test
    public void addOperationsToUserFromTemplateName() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();

        User user = TestsUtil.createUser();

        operationService.addOperationsToUserFromTemplateName(user.getId(), opTemplate.getName());

        List<UserHasOperation> userHasOperationsList = user.getUserHasOperations();

        Assert.assertTrue(userHasOperationsList.size() > 0);

    }

    @Test
    public void addOperationToGroup() throws Exception {

        Operation operation = TestsUtil.createOperation();

        Group group = TestsUtil.createGroup();
        GroupHasOperation gho = TestsUtil.createGroupHasOperation();
        gho.setOperation(operation);
        gho.setGroup(group);

        List<GroupHasOperation> operationsList = new ArrayList<>();
        operationsList.add(gho);
        group.setGroupHasOperations(operationsList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(gho);
        operationService.addOperationToGroup(group.getId(), operation.getName(), false);

        List<GroupHasOperation> groupHasOperations = group.getGroupHasOperations();

        Assert.assertTrue(groupHasOperations.size() > 0);

    }

    @Test
    public void addOperationToGroup1() throws Exception {

        Operation operation = TestsUtil.createOperation();

        Group group = TestsUtil.createGroup();

        Resource resource = TestsUtil.createResource();

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(resource);

        operationService.addOperationToGroup(group.getId(), operation.getName(),resource.getId(), false);

        List<GroupHasOperation> groupHasOperations = group.getGroupHasOperations();

        Assert.assertTrue(groupHasOperations.size() > 0);

    }

    @Test
    public void addOperationsToGroupFromTemplateID() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();

        Group group = TestsUtil.createGroup();
        //TODO modify optemplate for operations
        operationService.addOperationsToGroupFromTemplateID(group.getId(), opTemplate.getId());

        List <GroupHasOperation> groupHasOperationList = group.getGroupHasOperations();

        Assert.assertTrue(groupHasOperationList.size()>0);

    }

    @Test
    public void addOperationsToGroupFromTemplateName() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();

        Group group = TestsUtil.createGroup();
        //TODO modify optemplate for operations
        operationService.addOperationsToGroupFromTemplateID(group.getId(), opTemplate.getName());

        List <GroupHasOperation> groupHasOperationList = group.getGroupHasOperations();

        Assert.assertTrue(groupHasOperationList.size()>0);
    }

    @Test
    public void removeOperationFromUser() throws Exception {

        User user = TestsUtil.createUser();

        Operation operation = TestsUtil.createOperation();
        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);

        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(uho);
        operationService.removeOperationFromUser(user.getId(), operation.getId());

        List<UserHasOperation> operationList = user.getUserHasOperations();
        Assert.assertNull(operationList);

    }

    @Test
    public void removeOperationFromUser1() throws Exception {

        User user = TestsUtil.createUser();
        Resource resource = TestsUtil.createResource();

        Operation operation = TestsUtil.createOperation();
        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);

        List<UserHasOperation> operationsListBefore = new ArrayList<>();
        operationsListBefore.add(uho);
        resource.setUserHasOperations(operationsListBefore);

        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(uho);
        operationService.removeOperationFromUser(user.getId(), operation.getName(), resource.getId());

        List<UserHasOperation> operationList = user.getUserHasOperations();
        Assert.assertNull(operationList);
    }

    @Test(expected = NoResultException.class)
    public void removeOperationFromGroup() throws Exception {

        GroupHasOperation gho = TestsUtil.createGroupHasOperation();

        Group group = TestsUtil.createGroup();
        Operation operation = TestsUtil.createOperation();

        gho.setGroup(group);
        gho.setOperation(operation);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(gho);

        operationService.removeOperationFromGroup(group.getId(), gho.getOperation().getName());

        GroupHasOperation ghoFromDB = TestDBUtil.fetchSingleResultFromDB("GroupHasOperation");

        Assert.assertNull(ghoFromDB);

    }


    @Test
    public void isPermitted() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();
        UserHasOperation uho = TestsUtil.createUserHasOperation();

        uho.setOperation(operation);
        uho.setUser(user);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(uho);
        boolean isPermitted = operationService.isPermitted(user.getId(), operation.getName());

        Assert.assertTrue(isPermitted);
    }

    @Test
    public void isPermitted1() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();
        UserHasOperation uho = TestsUtil.createUserHasOperation();
        Resource resource = TestsUtil.createResource();
        Group group = TestsUtil.createGroup();
        List<Group> groupList = new ArrayList<>();
        groupList.add(group);

        user.setGroups(groupList);

        List<UserHasOperation> operationList = new ArrayList<>();

        uho.setOperation(operation);
        uho.setUser(user);
        uho.setResource(resource);
        operationList.add(uho);

        resource.setUserHasOperations(operationList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(uho);
        TestDBUtil.persistToDB(resource);

        boolean isPermitted = operationService.isPermitted(user.getId(), operation.getName(), resource.getObjectId());

        Assert.assertTrue(isPermitted);
    }

    @Test
    public void isPermittedForGroup() throws Exception {

        Operation operation = TestsUtil.createOperation();

        Group group = TestsUtil.createGroup();
        GroupHasOperation gho = TestsUtil.createGroupHasOperation();
        gho.setOperation(operation);
        gho.setGroup(group);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(gho);

        boolean isPermitted = operationService.isPermittedForGroup(group.getId(), operation.getName());

        Assert.assertTrue(isPermitted);
    }

    @Test
    public void isPermittedForGroup1() throws Exception {

        Operation operation = TestsUtil.createOperation();

        Group group = TestsUtil.createGroup();
        Resource resource = TestsUtil.createResource();

        GroupHasOperation gho = TestsUtil.createGroupHasOperation();
        gho.setOperation(operation);
        gho.setGroup(group);
        gho.setResource(resource);

        List<GroupHasOperation> groupOperationsList = new ArrayList<>();
        groupOperationsList.add(gho);

        resource.setGroupHasOperations(groupOperationsList);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(gho);
        TestDBUtil.persistToDB(resource);

        boolean isPermitted = operationService.isPermittedForGroup(group.getId(), operation.getName(), resource.getObjectId());

        Assert.assertTrue(isPermitted);
    }

    @Test
    public void getAllowedUsersForOperation() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();

        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);

        List<UserHasOperation> uhoList = new ArrayList<>();
        uhoList.add(uho);
        operation.setUserHasOperations(uhoList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(uho);

        Set<String> usersList = operationService.getAllowedUsersForOperation(operation.getName(), false);

        Assert.assertNotNull(usersList);
    }

    @Test
    public void getAllowedUsersForOperation1() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();
        Resource resource = TestsUtil.createResource();

        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);
        uho.setResource(resource);


        List<UserHasOperation> uhoList = new ArrayList<>();
        uhoList.add(uho);
        operation.setUserHasOperations(uhoList);
        resource.setUserHasOperations(uhoList);

        TestDBUtil.persistToDB(resource);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(uho);

        Set<String> usersList = operationService.getAllowedUsersForOperation(operation.getName(), resource.getId(), false);

        Assert.assertNotNull(usersList);
    }

    @Test
    public void getBlockedUsersForOperation() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();

        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);
        uho.setDeny(true);

        List<UserHasOperation> uhoList = new ArrayList<>();
        uhoList.add(uho);
        operation.setUserHasOperations(uhoList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(uho);

        Set<String> usersList = operationService.getBlockedUsersForOperation(operation.getName(),false);

        Assert.assertNotNull(usersList);
    }

    @Test
    public void getBlockedUsersForOperation1() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();
        Resource resource = TestsUtil.createResource();


        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);
        uho.setDeny(true);

        List<UserHasOperation> uhoList = new ArrayList<>();
        uhoList.add(uho);
        operation.setUserHasOperations(uhoList);

        resource.setUserHasOperations(uhoList);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(uho);
        TestDBUtil.persistToDB(resource);

        Set<String> usersList = operationService.getBlockedUsersForOperation(operation.getName(),resource.getObjectId(),false);

        Assert.assertNotNull(usersList);
    }

    @Test
    public void getAllowedGroupsForOperation() throws Exception {

        Group group = TestsUtil.createGroup();
        Operation operation = TestsUtil.createOperation();
        GroupHasOperation gho = TestsUtil.createGroupHasOperation();

        gho.setOperation(operation);
        gho.setGroup(group);

        List<GroupHasOperation> groupHasOperationsList = new ArrayList<>();
        groupHasOperationsList.add(gho);
        group.setGroupHasOperations(groupHasOperationsList);

        Set<String> allowedGroups = operationService.getAllowedGroupsForOperation(operation.getName(), false);

        Assert.assertNotNull(allowedGroups);
    }

    @Test
    public void getAllowedGroupsForOperation1() throws Exception {

        Group group = TestsUtil.createGroup();
        Operation operation = TestsUtil.createOperation();
        GroupHasOperation gho = TestsUtil.createGroupHasOperation();
        Resource resource = TestsUtil.createResource();
        gho.setResource(resource);
        gho.setGroup(group);
        gho.setOperation(operation);


        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(gho);
        TestDBUtil.persistToDB(resource);

        gho.setOperation(operation);
        gho.setGroup(group);

        List<GroupHasOperation> groupHasOperationsList = new ArrayList<>();
        groupHasOperationsList.add(gho);
        group.setGroupHasOperations(groupHasOperationsList);
        resource.setGroupHasOperations(groupHasOperationsList);

        Set<String> allowedGroups = operationService.getAllowedGroupsForOperation(operation.getName(),resource.getObjectId(), false);

        Assert.assertNotNull(allowedGroups);

    }

    @Test
    public void getBlockedGroupsForOperation() throws Exception {

        Group group = TestsUtil.createGroup();
        Operation operation = TestsUtil.createOperation();
        GroupHasOperation gho = TestsUtil.createGroupHasOperation();

        gho.setOperation(operation);
        gho.setGroup(group);
        gho.setDeny(true);

        List<GroupHasOperation> groupHasOperationsList = new ArrayList<>();
        groupHasOperationsList.add(gho);
        group.setGroupHasOperations(groupHasOperationsList);

        Set<String> blockedGroups = operationService.getAllowedGroupsForOperation(operation.getName(), false);

        Assert.assertNotNull(blockedGroups);
    }

    @Test
    public void getBlockedGroupsForOperation1() throws Exception {

        Group group = TestsUtil.createGroup();
        Operation operation = TestsUtil.createOperation();

        GroupHasOperation gho = TestsUtil.createGroupHasOperation();
        Resource resource = TestsUtil.createResource();
        gho.setResource(resource);
        gho.setGroup(group);
        gho.setOperation(operation);
        gho.setDeny(true);


        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(gho);
        TestDBUtil.persistToDB(resource);

        gho.setOperation(operation);
        gho.setGroup(group);

        List<GroupHasOperation> groupHasOperationsList = new ArrayList<>();
        groupHasOperationsList.add(gho);
        group.setGroupHasOperations(groupHasOperationsList);
        resource.setGroupHasOperations(groupHasOperationsList);

        Set<String> blockedGroups = operationService.getBlockedGroupsForOperation(operation.getName(),resource.getObjectId(), false);

        Assert.assertNotNull(blockedGroups);

    }

    @Test
    public void getPermittedOperationsForUser() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();

        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);
        uho.setDeny(false);

        List<UserHasOperation> userOperationsList = new ArrayList<>();
        userOperationsList.add(uho);
        user.setUserHasOperations(userOperationsList);

        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(uho);

        Set<String> userIds = operationService.getPermittedOperationsForUser(user.getId(), false);

        Assert.assertNotNull(userIds);

    }

    @Test
    public void getPermittedOperationsForUser1() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();
        Resource resource = TestsUtil.createResource();

        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setUser(user);
        uho.setOperation(operation);
        uho.setDeny(false);

        List<UserHasOperation> userOperationsList = new ArrayList<>();
        userOperationsList.add(uho);
        user.setUserHasOperations(userOperationsList);

        resource.setUserHasOperations(userOperationsList);

        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(uho);
        TestDBUtil.persistToDB(resource);

        Set<String> userIds = operationService.getPermittedOperationsForUser(user.getId(), false);

        Assert.assertNotNull(userIds);
    }

    @Test
    public void getResourceForOperation() throws Exception {

        User user = TestsUtil.createUser();
        Operation operation = TestsUtil.createOperation();
        Resource resource = TestsUtil.createResource();

        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setOperation(operation);
        uho.setResource(resource);
        uho.setUser(user);

        List<UserHasOperation> operationList = new ArrayList<>();
        operationList.add(uho);

        user.setUserHasOperations(operationList);

        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(resource);
        TestDBUtil.persistToDB(uho);

        Set<ResourceDTO> resourceDTOs = operationService.getResourceForOperation(user.getId(), operation.getName(), true);

        Assert.assertNotNull(resourceDTOs);

    }

    @Test
    public void getOperationByID() throws Exception {

        Operation operation = TestsUtil.createOperation();
        TestDBUtil.persistToDB(operation);

        OperationDTO oprationFromApp = operationService.getOperationByID(operation.getId());

        Assert.assertEquals(operation.getName(), oprationFromApp.getName());
    }

    @Test
    //Bug, method to test has not been implemented.   :)
    public void getGroupIDsByOperationAndUser() throws Exception {

        Operation operation = TestsUtil.createOperation();
        User user = TestsUtil.createUser();
        Group group = TestsUtil.createGroup();

        List<Group> userGroups = new ArrayList<>();
        userGroups.add(group);

        user.setGroups(userGroups);

        UserHasOperation uho = TestsUtil.createUserHasOperation();
        uho.setOperation(operation);
        uho.setUser(user);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(user);
        TestDBUtil.persistToDB(group);
        TestDBUtil.persistToDB(uho);

        List <String> groupIds = operationService.getGroupIDsByOperationAndUser(operation.getName(), user.getId());

        Assert.assertEquals(groupIds.get(0), group.getId());


    }

}