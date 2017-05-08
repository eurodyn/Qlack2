package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.aaa.api.AccountingService;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import javax.inject.Inject;
import java.util.UUID;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class AccountingServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    AccountingService accountingService;

    @Inject
    @Filter(timeout = 1200000)
    UserService userService;

    @Test
    public void createSession(){
        //creates new user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates new SessionDTO
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);

        String crtSession = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(crtSession);
    }

    @Test
    public void terminateSession() {
        //creates userDTO to assign in sessionDTO
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(0);

        String crtSession = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(crtSession);

        //terminates the newly created session
        accountingService.terminateSession(crtSession);

        //checks is duration isn't 0, expected >=0 result
        Assert.assertTrue(accountingService.getSessionDuration(crtSession) >=0 );
        Assert.assertTrue(sessionDTO.getTerminatedOn() >= 0);
    }

    @Test
    public void getSession(){
        //creates new user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates new session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        String crtSessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(crtSessionID);

        Assert.assertNotNull(accountingService.getSession(crtSessionID));
    }

    @Test
    public void getSessionDuration(){
        //creates user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        String crtSessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(crtSessionID);

        //terminates service to stop session
        accountingService.terminateSession(crtSessionID);

        //expect not null and non zero result
        Assert.assertNotNull(accountingService.getSessionDuration(crtSessionID));
        Assert.assertTrue(accountingService.getSessionDuration(crtSessionID) >= 0 );
    }

    @Test
    public void getUserLastLogIn(){
        //creates user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        String crtSessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(crtSessionID);

        //expect not null and non zero result
        Assert.assertNotNull(accountingService.getUserLastLogIn(userID));
        Assert.assertTrue(accountingService.getUserLastLogIn(userID) >= 0 );
    }

    @Test
    public void getUserLastLogOut(){
        //creates user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        String sesUserGetLstLog = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(sesUserGetLstLog);

        //expect not null and non zero result
        Assert.assertNotNull(accountingService.getUserLastLogOut(userID));
        Assert.assertTrue(accountingService.getUserLastLogOut(userID) >= 0 );
    }

    @Test
    public void getUserLastLogInDuration(){
        //creates user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        String sesUserGetInDur = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(sesUserGetInDur);

        //terminates the session
        accountingService.terminateSession(sesUserGetInDur);

        Assert.assertNotNull(accountingService.getUserLastLogInDuration(userID));
        Assert.assertTrue(accountingService.getUserLastLogInDuration(userID) >= 0);
    }

    @Test
    public void getNoOfTimesUserLoggedIn(){
        //creates user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        String crtSessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(crtSessionID);

        Assert.assertNotNull(accountingService.getNoOfTimesUserLoggedIn(userID));
        Assert.assertTrue(accountingService.getNoOfTimesUserLoggedIn(userID) >=0 );
    }

    @Test
    public void filterOnlineUsers(){
        //creates user
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        sessionDTO.setUserId(userID);
        sessionDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        String crtSessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(crtSessionID);

        //list to store userIds
        List<String> userIDs = new ArrayList();
        userIDs.add(userID);

        Assert.assertNotNull(accountingService.filterOnlineUsers(userIDs));
    }

    @Test
    public void updateAttribute(){
        //creates User
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setUserId(userID);
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        String sessionID = accountingService.createSession(sessionDTO);

        //creates SessionAttribute
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setSessionId(sessionID);
        sessionAttributeDTO.setName("attr");
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setValue(TestConst.SESSION_ATTRIBUTE_VALUE);

        Set<SessionAttributeDTO> attrsDTO = new HashSet();
        attrsDTO.add(sessionAttributeDTO);

        //finds the newly created Session
        SessionDTO sessionUpdDTO = accountingService.getSession(sessionID);
        sessionUpdDTO.setAttributes(attrsDTO);
        sessionUpdDTO.setUserId(userID);
        sessionUpdDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        Assert.assertNotNull(sessionUpdDTO);

        //check if Attribute exist: expected:Null
        Assert.assertNull(accountingService.getAttribute(sessionID,"attr") );

        //Attribute does not exist, method updateAttribute() creates the attribute
        accountingService.updateAttribute(sessionAttributeDTO,true);

        //check if Attribute exist
        Assert.assertNotNull(accountingService.getAttribute(sessionID,"attr") );
    }

    @Test
    public void updateAttributes(){
        //create User
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates SessionDTO
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setUserId(userID);
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        String crtSessionID = accountingService.createSession(sessionDTO);

        //set SessionAttribute
        SessionAttributeDTO sessionAttrDTO = new SessionAttributeDTO();
        sessionAttrDTO.setId(UUID.randomUUID().toString());
        sessionAttrDTO.setSessionId(crtSessionID);
        sessionAttrDTO.setName("attr");
        sessionAttrDTO.setId(UUID.randomUUID().toString());
        sessionAttrDTO.setValue(TestConst.SESSION_ATTRIBUTE_VALUE);

        Set<SessionAttributeDTO> sessionAttrs = new HashSet();
        sessionAttrs.add(sessionAttrDTO);

        //find sesUpdAttr
        SessionDTO sessionUpdDTO = accountingService.getSession(crtSessionID);
        sessionUpdDTO.setAttributes(sessionAttrs);
        sessionUpdDTO.setUserId(userID);
        sessionUpdDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        Assert.assertNotNull(sessionUpdDTO);

        //check if Attribute exist: expected:Null
        Assert.assertNull(accountingService.getAttribute(crtSessionID,"attr") );

        //list to store SessionAttributes
        Set<SessionAttributeDTO> updAttr = new HashSet();
        updAttr.add(sessionAttrDTO);

        //Attribute does not exist, method updateAttribute() creates the attribute
        accountingService.updateAttributes(updAttr,true);

        //check if Attribute exist
        Assert.assertNotNull(accountingService.getAttribute(crtSessionID,"attr") );
    }

    @Test
    public void deleteAttribute(){
        //creates user
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setUserId(userID);
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        String sessionID = accountingService.createSession(sessionDTO);

        //set SessionAttribute
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setSessionId(sessionID);
        sessionAttributeDTO.setName("attr");
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setValue(TestConst.SESSION_ATTRIBUTE_VALUE);

        Set<SessionAttributeDTO> sessAttrs = new HashSet();
        sessAttrs.add(sessionAttributeDTO);

        //find sesDelAttr
        SessionDTO sessionUpdDTO = accountingService.getSession(sessionID);
        sessionUpdDTO.setAttributes(sessAttrs);
        sessionUpdDTO.setUserId(userID);
        sessionUpdDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        Assert.assertNotNull(sessionUpdDTO);

        //check if Attribute exist: expected:Null
        Assert.assertNull(accountingService.getAttribute(sessionID,"attr") );

        //Attribute does not exist, method updateAttribute() creates the attribute
        accountingService.updateAttribute(sessionAttributeDTO,true);

        //check if Attribute exist
        Assert.assertNotNull(accountingService.getAttribute(sessionID,"attr") );

        accountingService.deleteAttribute(sessionUpdDTO.getId(),sessionAttributeDTO.getName());
        Assert.assertNull(accountingService.getAttribute(sessionID,"attr") );
    }

    @Test
    public void getAttribute() {
        //creates User
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setUserId(userID);
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        String sessionID = accountingService.createSession(sessionDTO);

        //sets SessionAttribute
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setSessionId(sessionID);
        sessionAttributeDTO.setName("attr");
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setValue(TestConst.SESSION_ATTRIBUTE_VALUE);

        Set<SessionAttributeDTO> sessAtts = new HashSet();
        sessAtts.add(sessionAttributeDTO);

        SessionDTO sessionUpdDTO = accountingService.getSession(sessionID);
        sessionUpdDTO.setAttributes(sessAtts);
        sessionUpdDTO.setUserId(userID);
        sessionUpdDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        Assert.assertNotNull(sessionUpdDTO);

        //check if Attribute exist: expected:Null
        Assert.assertNull(accountingService.getAttribute(sessionID, "attr"));

        //Attribute does not exist, method updateAttribute() creates the attribute
        accountingService.updateAttribute(sessionAttributeDTO, true);

        //check if Attribute exist
        Assert.assertNotNull(accountingService.getAttribute(sessionID, "attr"));
    }

    @Test
    public void getSessionIDsForAttribute() {
        //creates User
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //creates Session
        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setId(UUID.randomUUID().toString());
        sessionDTO.setUserId(userID);
        sessionDTO.setApplicationSessionID(UUID.randomUUID().toString());
        sessionDTO.setTerminatedOn(TestConst.DATE_TERMINATED_ON);
        String sessionID = accountingService.createSession(sessionDTO);

        //creates SessionAttribute
        SessionAttributeDTO sessionAttributeDTO = new SessionAttributeDTO();
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setSessionId(sessionID);
        sessionAttributeDTO.setName("attr");
        sessionAttributeDTO.setId(UUID.randomUUID().toString());
        sessionAttributeDTO.setValue(TestConst.SESSION_ATTRIBUTE_VALUE);

        Set<SessionAttributeDTO> list = new HashSet();
        list.add(sessionAttributeDTO);

        SessionDTO sessionUpdDTO = accountingService.getSession(sessionID);
        sessionUpdDTO.setAttributes(list);
        sessionUpdDTO.setUserId(userID);
        sessionUpdDTO.setCreatedOn(TestConst.DATE_CREATED_ON);
        Assert.assertNotNull(sessionUpdDTO);

        //check if SessionAttribute exist: expected:null
        Assert.assertNull(accountingService.getAttribute(sessionID, "attr"));

        //Attribute does not exist, method updateAttribute() creates the attribute
        accountingService.updateAttribute(sessionAttributeDTO, true);

        Set<String> sessAttrs = new HashSet();
        sessAttrs.add(sessionID);

        //check if Attribute exist
        Assert.assertNotNull(accountingService.getSessionIDsForAttribute(sessAttrs, sessionAttributeDTO.getName(),sessionAttributeDTO.getValue()));
    }

}