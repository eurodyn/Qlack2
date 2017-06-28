package com.eurodyn.qlack2.fuse.simm.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.GroupUserService;
import com.eurodyn.qlack2.fuse.simm.api.SocialGroupService;
import com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupUserDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import static com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants.GROUP_USER_STATUS_BANNED;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class GroupUserServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    GroupUserService groupUserService;

    @Inject
    @Filter(timeout = 1200000)
    SocialGroupService socialGroupService;

    @Test
    public void requestToJoinGroup(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
    }

    @Test
    public void listGroupsForUser(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO[] GrouplistID = groupUserService.listGroupsForUser(socialGroupDTO.getSrcUserId(),null,TestConst.privacy,paging);
        Assert.assertNotNull(GrouplistID);
    }

    @Test
    public void acceptUserJoin(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupID.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        Assert.assertNotNull(groupUserService.getAllContactsForStatus(socialGroupDTO.getSrcUserId(), SIMMConstants.GROUP_USER_STATUS_ACCEPTED));
    }

    @Test
    public void rejectUserJoin(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        groupUserService.rejectUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        SocialGroupUserDTO socialUserDTO = groupUserService.getGroupUser(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNull(socialUserDTO);
    }

    @Test
    public void leaveGroup(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        groupUserService.leaveGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        SocialGroupUserDTO socialUserDTO = groupUserService.getGroupUser(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNull(socialUserDTO);
    }

    @Test
    public void banUser(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        groupUserService.banUser(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        SocialGroupUserDTO socialUserDTO = groupUserService.getGroupUser(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertEquals(GROUP_USER_STATUS_BANNED,socialUserDTO.getStatus());
    }

    @Test
    public void getGroupUser(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        SocialGroupUserDTO socialUserDTO = groupUserService.getGroupUser(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(socialUserDTO);
    }

    @Test
    public void getMembersForUserGroups(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        Assert.assertNotNull(groupUserService.getMembersForUserGroups(socialGroupDTO.getSrcUserId()));
    }

    @Test
    public void listContactsForUser(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        Assert.assertNotNull(groupUserService.listContactsForUser(socialGroupDTO.getSrcUserId(),TestConst.privacy,paging));
    }

    @Test
    public void listContactsForGroup(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        Assert.assertNotNull(groupUserService.listContactsForGroup(socialGroupDTO.getId(),TestConst.privacy,paging));
    }

    @Test
    public void getAllContactsForStatus(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        Assert.assertNotNull(groupUserService.getAllContactsForStatus(socialGroupDTO.getSrcUserId(), SIMMConstants.GROUP_USER_STATUS_ACCEPTED));
    }

    @Test
    public void isInvited(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);

        Assert.assertTrue(groupUserService.isInvited(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId()));
    }

    @Test
    public void listAvailableGroups(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO GroupID = groupUserService.requestToJoinGroup(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());
        Assert.assertNotNull(GroupID.getId());
        groupUserService.inviteUser(socialGroupDTO.getSrcUserId(),socialGroupDTO);
        groupUserService.acceptUserJoin(socialGroupDTO.getSrcUserId(),socialGroupDTO.getId());

        Assert.assertNotNull(groupUserService.listAvailableGroups(socialGroupDTO.getSrcUserId(),false));
    }

}



