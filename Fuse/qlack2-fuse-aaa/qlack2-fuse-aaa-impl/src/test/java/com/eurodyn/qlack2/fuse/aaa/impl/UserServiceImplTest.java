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

import com.eurodyn.qlack2.fuse.aaa.api.AccountingService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Group;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.model.UserAttribute;
import com.eurodyn.qlack2.fuse.aaa.impl.util.ConverterUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Session;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.*;

/**
 *
 * @author European Dynamics SA
 */
public class UserServiceImplTest {

    private EntityManager em;
    private EntityTransaction tr;
    private UserService userService;
    private AccountingService accountingService;

    @BeforeClass
    public static void beforeClass() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }


    @Before
    public void setUp() throws Exception {

        em = AllAAATests.getEm();
        userService = AllAAATests.getUserService();

        EntityTransaction cleanTables = em.getTransaction();
        cleanTables.begin();

        cleanGroupTable();
        cleanUserAttributes();
        cleanSession();
        clearUserTable();

        Session session = em.unwrap(Session.class);
        session.clear();
        cleanTables.commit();

        if (tr == null){
            tr = em.getTransaction();
        }
        tr.begin();
    }

    private void cleanSession() {

        em.createQuery("delete from Session")
                .executeUpdate();
    }

    private void cleanUserAttributes() {

        em.createQuery("delete from UserAttribute ")
                .executeUpdate();
    }

    private void cleanGroupTable() {

        em.createQuery("delete from Group")
                .executeUpdate();
    }

    private void clearUserTable() {

        em.createQuery("delete from User")
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
    //Potential bug ..throws NPE
    public void createUser() throws Exception {

        UserDTO userDTO = TestsUtil.createUserDTO();
        UserAttributeDTO userAttributeDTO = ConverterUtil.userAttributeToUserAttributeDTO(TestsUtil.createuserAttribute());
        userAttributeDTO.setUserId(userDTO.getUsername());

        Set<UserAttributeDTO> userAttributeDTOs = new HashSet<>();
        userAttributeDTOs.add(userAttributeDTO);
        userDTO.setUserAttributes(userAttributeDTOs);
        String userId = userService.createUser(userDTO);

        User userFromDB = TestDBUtil.getUserById(userId);
        Assert.assertEquals(userFromDB.getUsername(), userDTO.getUsername());

    }

    @Test
    public void updateUser() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);
        UserDTO userDTO = ConverterUtil.userToUserDTO(user);

        UserAttribute userAttribute = TestsUtil.createuserAttribute();
        userAttribute.setUser(user);
        List<UserAttribute> userAttributeList = new ArrayList<>();
        userAttributeList.add(userAttribute);
        user.setUserAttributes(userAttributeList);

        TestDBUtil.persistToUserAttribute(userAttribute);
        TestDBUtil.persistToUser(user);
        UserDTO modifiedDTO = TestsUtil.modifyUserDTO(userDTO, "modifiedValue");
        modifiedDTO.setId(userDTO.getId());
        userService.updateUser(modifiedDTO, true);

        User userFromDB = TestDBUtil.getUserById(modifiedDTO.getId());
        Assert.assertEquals(userFromDB.getUsername(), modifiedDTO.getUsername());

    }

    @Test(expected = NoResultException.class)
    public void deleteUser() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        userService.deleteUser(user.getId());

        User userFromDB = TestDBUtil.getUserById(user.getId());

    }

    @Test
    public void getUserById() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        UserDTO userFromDB = userService.getUserById(user.getId());

        Assert.assertEquals(user.getId(), userFromDB.getId());

    }


    @Test
    public void getUsersByIdAsHash() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        Collection<String> userIds = new ArrayList<>();
        userIds.add(user.getId());

        Map<String, UserDTO> hash = userService.getUsersByIdAsHash(userIds);

        Assert.assertNotNull(hash);
        Assert.assertTrue(hash.size() > 0);
    }

    @Test
    public void getUserByName() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        UserDTO userFromDB = userService.getUserByName(user.getUsername());

        Assert.assertEquals(user.getId(), userFromDB.getId());
    }

    @Test
    public void updateUserStatus() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        byte bt = 100;
        userService.updateUserStatus(user.getId(),bt);

        Assert.assertEquals(100 ,user.getStatus());
    }

    @Test
    public void getUserStatus() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);
        byte userStatus = 100;

        user.setStatus(userStatus);

        Assert.assertEquals(100, user.getStatus());


    }

    @Test
    public void isSuperadmin() throws Exception {

        User user = TestsUtil.createUser();

        TestDBUtil.persistToUser(user);

        User userFromDB = TestDBUtil.getUserById(user.getId());

        Assert.assertEquals(userFromDB.isSuperadmin(), user.isSuperadmin());
    }

    @Test
    public void isExternal() throws Exception {

        User user = TestsUtil.createUser();

        TestDBUtil.persistToUser(user);

        User userFromDB = TestDBUtil.getUserById(user.getId());

        Assert.assertEquals(userFromDB.isExternal(), user.isExternal());

    }

    @Test
    public void canAuthenticate() throws Exception {

        User user = TestsUtil.createUser();

        TestDBUtil.persistToUser(user);

        User userFromDB = TestDBUtil.getUserById(user.getId());

        Assert.assertNull(userService.canAuthenticate(userFromDB.getUsername(), "arbitraryPassword"));

    }

    @Test
    public void login() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        userService.login(user.getId(), null, true);

       List<com.eurodyn.qlack2.fuse.aaa.impl.model.Session>  allSessions = fetchAllSessions();

        Assert.assertNotNull(allSessions);

    }

    @Test
    //potential bug, does not work as expected, throws NPE
    public void logout() throws Exception {

        com.eurodyn.qlack2.fuse.aaa.impl.model.Session session = TestsUtil.createUserSession();
        userService.logout(session.getUser().getId(), session.getApplicationSessionId());

        com.eurodyn.qlack2.fuse.aaa.impl.model.Session sessionForUser = getSessionForUser(session.getUser().getId());

        Assert.assertTrue(sessionForUser.getTerminatedOn() >0);
    }

    private com.eurodyn.qlack2.fuse.aaa.impl.model.Session getSessionForUser(String id) {

        return em.createQuery("select sess from Session sess where sess.user.id=:id", com.eurodyn.qlack2.fuse.aaa.impl.model.Session.class)
                .setParameter("id",id)
                .getSingleResult();
    }

    @Test
    //potential bug, does not work as expected
    public void logoutAll() throws Exception {

        com.eurodyn.qlack2.fuse.aaa.impl.model.Session session = TestsUtil.createUserSession();

        userService.logoutAll();

        //assert that the session is null
        List<com.eurodyn.qlack2.fuse.aaa.impl.model.Session> allSessions = fetchAllSessions();

        Assert.assertEquals(0,allSessions.size());

    }

    private List<com.eurodyn.qlack2.fuse.aaa.impl.model.Session> fetchAllSessions() {

        return em.createQuery("select sess from Session  sess", com.eurodyn.qlack2.fuse.aaa.impl.model.Session.class)
                .getResultList();
    }

    @Test
    public void isUserAlreadyLoggedIn() throws Exception {

        User user = TestsUtil.createUser();

        com.eurodyn.qlack2.fuse.aaa.impl.model.Session userSession = TestsUtil.createUserSession();
        List<com.eurodyn.qlack2.fuse.aaa.impl.model.Session> sessions = new ArrayList<>();
        sessions.add(userSession);

        user.setSessions(sessions);

        Assert.assertTrue(userService.isUserAlreadyLoggedIn(user.getId()).size() >0);

    }

    @Test
    //potential bug, throws NPE
    public void updatePassword() throws Exception {

        User user = TestsUtil.createUser();
        userService.updatePassword(user.getUsername(), "modifiedPassword");

        User userFromDB = TestDBUtil.getUserById(user.getId());

        String newPassword = DigestUtils.md5Hex(user.getSalt() + "modifiedPassword");

        Assert.assertEquals(newPassword, userFromDB.getPassword());
    }

    @Test
    public void belongsToGroupByName() throws Exception {

        User user = TestsUtil.createUser();
        Group group = TestsUtil.createGroup();

        List<User> users = new ArrayList<>();
        users.add(user);

        group.setUsers(users);

        List<Group> groups = new ArrayList<>();
        groups.add(group);
        user.setGroups(groups);

        TestDBUtil.persistToGroup(group);
        TestDBUtil.persistToUser(user);

        Assert.assertTrue(userService.belongsToGroupByName(user.getId(), group.getName(),false));

    }

    @Test
    public void updateAttributes() throws Exception {

        User user = TestsUtil.createUser();
        UserAttribute userAttribute = TestsUtil.createuserAttribute();
        UserAttribute secondUserAttribute = TestsUtil.newUserAtrribute("modifiedUserAttribute");
        userAttribute.setUser(user);
        secondUserAttribute.setUser(user);

        List<UserAttribute> userAttributesList = new ArrayList<>();
        userAttributesList.add(userAttribute);
        userAttributesList.add(secondUserAttribute);

        UserAttributeDTO userAttributeDTO = ConverterUtil.userAttributeToUserAttributeDTO(userAttribute);
        UserAttributeDTO secondUserAttributeDTO = ConverterUtil.userAttributeToUserAttributeDTO(secondUserAttribute);

        Collection<UserAttributeDTO> userAttributes = new ArrayList<>();
        userAttributes.add(userAttributeDTO);
        userAttributes.add(secondUserAttributeDTO);

        user.setUserAttributes(userAttributesList);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToUserAttribute(userAttribute);
        TestDBUtil.persistToUserAttribute(secondUserAttribute);

        userService.updateAttributes(userAttributes, true);

        Assert.assertTrue(user.getUserAttributes().size()>0);
    }

    @Test
    //potential bug, does not create missing attribute
    public void updateAttribute() throws Exception {

        User user = TestsUtil.createUser();
        UserAttribute userAttribute = TestsUtil.createuserAttribute();
        userAttribute.setUser(user);

        UserAttributeDTO userAttributeDTO = ConverterUtil.userAttributeToUserAttributeDTO(userAttribute);


        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToUserAttribute(userAttribute);

        userService.updateAttribute(userAttributeDTO, true);
        Assert.assertTrue(user.getUserAttributes().size() ==1);
    }

    @Test
    //Potential bug, does not delete the attribute
    public void deleteAttribute() throws Exception {

        UserAttribute userAttribute = TestsUtil.createuserAttribute();

        User user = TestsUtil.createUser();
        userAttribute.setUser(user);
//        UserAttributeDTO userAttributeDTO = ConverterUtil.userAttributeToUserAttributeDTO(userAttribute);

        List<UserAttribute> userAttributes = new ArrayList<>();
        userAttributes.add(userAttribute);
        user.setUserAttributes(userAttributes);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToUserAttribute(userAttribute);

        userService.deleteAttribute(user.getId(), userAttribute.getName());
        Assert.assertTrue(user.getUserAttributes().size() ==0);
    }

    @Test
    public void getAttribute() throws Exception {

        UserAttribute userAttribute = TestsUtil.createuserAttribute();

        User user = TestsUtil.createUser();
        List<UserAttribute> userAttributes = new ArrayList<>();
        userAttributes.add(userAttribute);
        user.setUserAttributes(userAttributes);

        userAttribute.setUser(user);
        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToUserAttribute(userAttribute);

        UserAttributeDTO attributeDTO = userService.getAttribute(user.getId(), userAttribute.getName());

        Assert.assertNotNull(attributeDTO);

    }

    @Test
    public void getUserIDsForAttribute() throws Exception {

        UserAttribute userAttribute = TestsUtil.createuserAttribute();

        User user = TestsUtil.createUser();
        List<UserAttribute> userAttributes = new ArrayList<>();
        userAttribute.setUser(user);
        userAttributes.add(userAttribute);
        user.setUserAttributes(userAttributes);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToUserAttribute(userAttribute);

        Collection<String> userIds = userService.getUserIDsForAttribute(null,userAttribute.getName(), userAttribute.getData());
        Assert.assertEquals(userIds.size(), 1);
        Assert.assertEquals(userIds.toArray()[0].toString(), user.getId());
    }

    @Test
    //potential bug, does not work as expected
    public void findUsers() throws Exception {

        User user = TestsUtil.createUser();

        UserSearchCriteria userSearchCriteria = UserSearchCriteria.UserSearchCriteriaBuilder
                .createCriteria()
                .withUsernameLike(user.getUsername())
                .build();

        List<UserDTO> userDTOs =userService.findUsers(userSearchCriteria);

        Assert.assertEquals(1, userDTOs.size());
        Assert.assertEquals(userDTOs.get(0).getPassword(), user.getPassword());

    }

    @Test
    //potential bug, does not work as expected
    public void findUserCount() throws Exception {

        User user = TestsUtil.createUser();

        UserSearchCriteria userSearchCriteria = UserSearchCriteria.UserSearchCriteriaBuilder
                .createCriteria()
                .withUsernameLike(user.getUsername())
                .build();

        long count = userService.findUserCount(userSearchCriteria);

        Assert.assertEquals(1, count);
    }

    @Test
    public void isAttributeValueUnique() throws Exception {

        User user = TestsUtil.createUser();

        UserAttribute userAttribute = TestsUtil.createuserAttribute();
        userAttribute.setUser(user);

        List<UserAttribute> userAttributeList = new ArrayList<>();
        userAttributeList.add(userAttribute);
//        user.addUserAttribute(userAttribute);
        user.setUserAttributes(userAttributeList);

        TestDBUtil.persistToUser(user);
        TestDBUtil.persistToUserAttribute(userAttribute);

        Assert.assertTrue(userService.isAttributeValueUnique(userAttribute.getData(), userAttribute.getName(), user.getId()));

    }

}