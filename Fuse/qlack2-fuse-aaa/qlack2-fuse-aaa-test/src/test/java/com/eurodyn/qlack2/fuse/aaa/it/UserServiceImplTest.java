package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.aaa.api.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.AccountingService;
import com.eurodyn.qlack2.fuse.aaa.api.UserGroupService;
import com.eurodyn.qlack2.fuse.aaa.api.criteria.UserSearchCriteria;
import javax.inject.Inject;
import org.junit.Assert;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class UserServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    UserService userService;

    @Inject
    @Filter(timeout = 1200000)
    AccountingService accountingService;

    @Inject
    @Filter(timeout = 1200000)
    UserGroupService userGroupService;

    @Test
    public void createUser(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);
    }

    @Test
    public void updateUser(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        UserDTO userUpdDTO = userService.getUserById(userID);
        Assert.assertNotNull(userUpdDTO);
        userUpdDTO.setUsername("upd");

        userService.updateUser(userUpdDTO,false);
        UserDTO checkUserID = userService.getUserById(userUpdDTO.getId());

        Assert.assertEquals("upd",checkUserID.getUsername());
    }

    @Test
    public void deleteUser(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        userService.deleteUser(userID);
        Assert.assertNull(userService.getUserById(userDTO.getId()));
    }

    @Test
    public void getUserById(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Assert.assertNotNull(userService.getUserById(userID));
    }

    @Test
    public void getUsersById(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Set<String> userIDs = new HashSet();
        userIDs.add(userID);

        Assert.assertNotNull(userService.getUsersById(userIDs));
    }

    @Test
    public void getUsersByIdAsHash(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Set<String> userIDs = new HashSet();
        userIDs.add(userID);

        Assert.assertNotNull(userService.getUsersByIdAsHash(userIDs));
    }

    @Test
    public void getUserByName(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Assert.assertNotNull(userService.getUserByName(userDTO.getUsername()));
    }

    @Test
    public void updateUserStatus(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        userService.updateUserStatus(userID,TestConst.statusUpd);
        UserDTO userUpdID = userService.getUserById(userID);
        Assert.assertTrue(userUpdID.getStatus() == 101 );
    }

    @Test
    public void getUserStatus(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Assert.assertNotNull(userService.getUserStatus(userID));
        Assert.assertTrue(userService.getUserStatus(userID) == 100 );
    }

    @Test
    public void isSuperadmin(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //default value setSuperadmin=false
        Assert.assertFalse(userService.isSuperadmin(userID));
    }

    @Test
    public void isExternal(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //default value setExternal=false
        Assert.assertFalse(userService.isSuperadmin(userID));
    }

    @Test
    public void canAuthenticate(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Assert.assertNotNull(userService.canAuthenticate(userDTO.getUsername(),userDTO.getPassword()));
    }

    @Test
    public void login(){
        //creates User
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        SessionDTO sessionDTO = TestUtilities.createSessionDTO();
        sessionDTO.setUserId(userID);
        String sessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(sessionID);

        Assert.assertNotNull(userService.login(userID,null,true));
    }

    @Test
    public void logout(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //create new session using accountingService
        SessionDTO sessionDTO = TestUtilities.createSessionDTO();
        sessionDTO.setUserId(userID);
        String sessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(sessionID);

        userService.logout(userID,sessionID);

        Assert.assertNull(userService.isUserAlreadyLoggedIn(userID));
    }

    @Test
    public void logoutAll(){
        //user login
        UserDTO userOneDTO = TestUtilities.createUserDTO();
        String userOneID = userService.createUser(userOneDTO);
        Assert.assertNotNull(userOneID);

        //create new session using accountingService
        SessionDTO sessionOneDTO = TestUtilities.createSessionDTO();
        sessionOneDTO.setUserId(userOneID);
        String sessionOneID = accountingService.createSession(sessionOneDTO);
        Assert.assertNotNull(sessionOneID);

        //new user login
        UserDTO userTwoDTO = TestUtilities.createUserDTO();
        String userTwoID = userService.createUser(userTwoDTO);
        Assert.assertNotNull(userTwoID);

        //create new session using accountingService
        SessionDTO sessionTwoDTO = TestUtilities.createSessionDTO();
        sessionTwoDTO.setUserId(userTwoID);
        String sessionTwoID = accountingService.createSession(sessionTwoDTO);
        Assert.assertNotNull(sessionTwoID);

        //logout all users
        userService.logoutAll();

       //check if each use is logout
        Assert.assertNull(userService.isUserAlreadyLoggedIn(userOneID));
        Assert.assertNull(userService.isUserAlreadyLoggedIn(userTwoID));
    }

    @Test
    public void isUserAlreadyLoggedIn(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        //create new session using accountingService
        SessionDTO sessionDTO = TestUtilities.createSessionDTO();
        sessionDTO.setUserId(userID);
        String sessionID = accountingService.createSession(sessionDTO);
        Assert.assertNotNull(sessionID);

        userService.logout(userID,sessionID);

        Assert.assertNull(userService.isUserAlreadyLoggedIn(userID));
    }

    @Test
    public void updatePassword(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        Assert.assertEquals("newpass",userService.updatePassword(userDTO.getUsername(),"newpass"));
    }

    @Test
    public void belongsToGroupByName(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = userGroupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(userService.belongsToGroupByName(userID,groupDTO.getName(),true));
    }

    @Test
    public void updateAttribute(){
        UserDTO userDTO = new UserDTO();
        String userID = UUID.randomUUID().toString();
        userDTO.setId(userID);

        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userUpdAttrID = userService.createUser(userDTO);
        Assert.assertNotNull(userService.getUserById(userUpdAttrID));

        UserDTO userDTOtoUpd = userService.getUserById(userUpdAttrID);

        //set AttributeDTO
        UserAttributeDTO userAttrDTO = new UserAttributeDTO();
        userAttrDTO.setId(UUID.randomUUID().toString());
        userAttrDTO.setName(TestConst.generateRandomString());
        userAttrDTO.setUserId(userID);

        userDTOtoUpd.setAttribute(userAttrDTO);

        userService.updateAttribute(userAttrDTO,true);
        Assert.assertNotNull(userService.getAttribute(userUpdAttrID,userAttrDTO.getName()));
    }

    @Test
    public void updateAttributes(){
        UserDTO userDTO = new UserDTO();
        String userId = UUID.randomUUID().toString();
        userDTO.setId(userId);

        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userUpdAttrsID = userService.createUser(userDTO);
        Assert.assertNotNull(userService.getUserById(userUpdAttrsID));

        UserDTO userGetDTO = userService.getUserById(userUpdAttrsID);

        //set AttributeDTO
        UserAttributeDTO userAttrDTO = new UserAttributeDTO();
        userAttrDTO.setId(UUID.randomUUID().toString());
        userAttrDTO.setName(TestConst.generateRandomString());
        userAttrDTO.setUserId(userId);

        //list of attributes
        Set<UserAttributeDTO> attrList = new HashSet();
        attrList.add(userAttrDTO);

        userGetDTO.setAttribute(userAttrDTO);

        userService.updateAttributes(attrList,true);

        Assert.assertNotNull(userService.getAttribute(userUpdAttrsID,userAttrDTO.getName()));
    }

    @Test
    public void findUsers(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);

        UserSearchCriteria userSearchCriteria = UserSearchCriteria.UserSearchCriteriaBuilder
                .createCriteria()
                .withUsernameLike(userID)
                .build();

        Assert.assertNotNull( userService.findUsers(userSearchCriteria) );
    }

    @Test
    public void findUserCount(){
        UserDTO userDTO = TestUtilities.createUserDTO();
        String userID = userService.createUser(userDTO);
        Assert.assertNotNull(userID);


        UserSearchCriteria userSearchCriteria = UserSearchCriteria.UserSearchCriteriaBuilder
                .createCriteria()
                .withUsernameLike(userID)
                .build();

        Assert.assertNotNull( userService.findUserCount(userSearchCriteria) );
        Assert.assertTrue( userService.findUserCount(userSearchCriteria) >= 0 );
    }

    @Test
    public void isAttributeValueUnique(){
        UserDTO userDTO = new UserDTO();
        String userId = UUID.randomUUID().toString();
        userDTO.setId(userId);

        //userUpdAttrDTO.setAttribute(userAttrUpdAttrDTO);
        userDTO.setExternal(TestConst.USER_EXTERNAL);
        userDTO.setPassword(TestConst.USER_PASSWORD);
        userDTO.setSuperadmin(TestConst.USER_SUPERADMIN);
        userDTO.setUsername(TestConst.generateRandomString());
        String userUpdAttrID = userService.createUser(userDTO);
        Assert.assertNotNull(userService.getUserById(userUpdAttrID));

        UserDTO userUpdDTO = userService.getUserById(userUpdAttrID);

        //set AttributeDTO
        UserAttributeDTO userAttrDTO = new UserAttributeDTO();
        userAttrDTO.setId(UUID.randomUUID().toString());
        userAttrDTO.setName(TestConst.generateRandomString());
        userAttrDTO.setUserId(userId);
        userAttrDTO.setData("{data}");

        userUpdDTO.setAttribute(userAttrDTO);

        userService.updateAttribute(userAttrDTO,true);
        Assert.assertNotNull(userService.isAttributeValueUnique(userAttrDTO.getData(),userAttrDTO.getName(),userId));
    }

}
