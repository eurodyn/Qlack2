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
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Session;
import com.eurodyn.qlack2.fuse.aaa.impl.model.SessionAttribute;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.util.ConverterUtil;
import org.junit.*;

import javax.persistence.Convert;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Provides accounting information for the user.
 * For details regarding the functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA
 */

public class AccountingServiceImplTest{

    private  EntityManager em;
    private EntityTransaction tr;
    private AccountingService accountingService;

    @BeforeClass
    public static void beforeClass() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }

    @Before
    public void init() throws Exception {

        em = AllAAATests.getEm();
        accountingService = AllAAATests.getAccountingService();

        org.hibernate.Session hibernateSession = em.unwrap(org.hibernate.Session.class);
        hibernateSession.clear();

        EntityTransaction cleanTables = em.getTransaction();
        cleanTables.begin();

        cleanUsers();
        clearSessionTable();

        cleanTables.commit();

        if (tr == null){
            tr = em.getTransaction();
        }

        tr.begin();
    }

    private void clearSessionTable() {

        em.createQuery("delete from Session")
                .executeUpdate();
    }

    private void cleanUsers(){

        em.createQuery("delete from User")
                .executeUpdate();
    }

    private void cleanSessionAttribute(){

        em.createQuery("delete from SessionAttribute")
                .executeUpdate();
    }

    @After
    public void tearDown() throws Exception {

        tr.commit();
    }

    @Test
    public void createSession() throws Exception {

        User user = TestsUtil.createUser();
        TestDBUtil.persistToUser(user);

        SessionDTO sessionDTO = TestsUtil.createSessionDTO();
        sessionDTO.setUserId(user.getId());

        String sessionId = accountingService.createSession(sessionDTO);

        Session session = TestDBUtil.fetchSessionById(sessionId);
        Assert.assertNotNull(session.getCreatedOn());
    }

    @Test
    public void terminateSession() throws Exception {

        Session session = TestsUtil.createUserSession();
        session.setTerminatedOn(null);
        List<Session> allSessions = TestDBUtil.fetchSessions();
        Assert.assertTrue(allSessions.size() > 0);

        accountingService.terminateSession(session.getId());

        List<Session> allSessionsPostTermination = TestDBUtil.fetchSessions();

        System.out.print(allSessionsPostTermination);

        Assert.assertNotNull(session.getTerminatedOn());
    }

    @Test
    public void getSession(){

        Session session = TestsUtil.createUserSession();

        SessionDTO sessionFromApp = accountingService.getSession(session.getId());

        Assert.assertEquals(session.getCreatedOn(), sessionFromApp.getCreatedOn());

    }

    @Test
    public void getSessionDuration(){

        Session session = TestsUtil.createUserSession();
        session.setTerminatedOn(new Date().getTime());

        long loginInDuration = accountingService.getSessionDuration(session.getId());

        long expectedLoginDuration = session.getTerminatedOn() - session.getCreatedOn();
        Assert.assertEquals(expectedLoginDuration, loginInDuration);

    }


    @Test
    public void getUserLastLogin(){

        Session session = TestsUtil.createUserSession();
        User user = session.getUser();

        long userLastLogIN = accountingService.getUserLastLogIn(user.getId());

        Assert.assertEquals(userLastLogIN, session.getCreatedOn());

    }

    @Test
    public void getUserLastLogOut(){

        Session session = TestsUtil.createUserSession();

        long terminatedTime = new Date().getTime();
        session.setTerminatedOn(terminatedTime);

        long actualLogOutTime = accountingService.getUserLastLogOut(session.getUser().getId());

        Assert.assertEquals(terminatedTime, actualLogOutTime);
    }


    @Test
    public void getUserLastLoginDuration(){

        Session session = TestsUtil.createUserSession();
        session.setTerminatedOn(new Date().getTime());

        long loginInDuration = accountingService.getUserLastLogInDuration(session.getUser().getId());

        long expectedLoginDuration = session.getTerminatedOn() - session.getCreatedOn();
        Assert.assertEquals(expectedLoginDuration, loginInDuration);

    }


    @Test
    public void filterOnlineUsers() throws Exception {

        Session session = TestsUtil.createUserSession();

        User secondUser = TestsUtil.createUser();
        TestsUtil.modifyUser(secondUser, "secondUser");

        TestDBUtil.persistToUser(secondUser);

        List<String> usersList = new ArrayList<>();
        usersList.add(session.getUser().getId());
        usersList.add(secondUser.getId());

        Set<String> filteredUsers = accountingService.filterOnlineUsers(usersList);

        Assert.assertEquals(1, filteredUsers.size());

    }

    @Test
    //Potential bug, throws NPE, does not associate attribute with the session
    public void updateAttribute() throws Exception {

        Session session = TestsUtil.createUserSession();

        SessionAttributeDTO sessionAttributeDTO = TestsUtil.createSessionAttributeDTO();
        sessionAttributeDTO.setSessionId(session.getId());


        accountingService.updateAttribute(sessionAttributeDTO, true);

        Assert.assertEquals(1, session.getSessionAttributes().size());
    }

    @Test
    //Potential bug, throws NPE
    public void updateAttributes() throws Exception {

        Session session = TestsUtil.createUserSession();

        SessionAttributeDTO sessionAttributeDTO = TestsUtil.createSessionAttributeDTO();
        sessionAttributeDTO.setSessionId(session.getId());

        SessionAttributeDTO secondSessionAttributeDTO = TestsUtil.createSessionAttributeDTO();
        TestsUtil.modifySessionAttributeDTO(secondSessionAttributeDTO , "secondSessionAttributeDTO");

        List<SessionAttributeDTO> sessionAttributeDTOList = new ArrayList<>();
        sessionAttributeDTOList.add(sessionAttributeDTO);
        sessionAttributeDTOList.add(secondSessionAttributeDTO);

        accountingService.updateAttributes(sessionAttributeDTOList, true);


        Assert.assertEquals(2, session.getSessionAttributes().size());
    }

    @Test
    public void deleteAttribute() throws Exception {

        Session session = TestsUtil.createUserSession();

        SessionAttribute sessionAttribute = TestsUtil.createSessionAttribute();
        sessionAttribute.setSession(session);
        TestDBUtil.persistToSessionAttribute(sessionAttribute);

        accountingService.deleteAttribute(session.getId(), sessionAttribute.getName() );

        Assert.assertNull(session.getSessionAttributes());
    }

}