package com.eurodyn.qlack2.fuse.simm.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.FriendsService;
import com.eurodyn.qlack2.fuse.simm.api.GroupUserService;
import com.eurodyn.qlack2.fuse.simm.api.SocialGroupService;
import com.eurodyn.qlack2.fuse.simm.api.dto.FriendDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.UUID;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class FriendsServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    FriendsService friendsService;

    @Inject
    @Filter(timeout = 1200000)
    GroupUserService groupUserService;

    @Inject
    @Filter(timeout = 1200000)
    SocialGroupService socialGroupService;

    @Test
    public void requestFriendship(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(),"test01");
        String[] friendIds = friendsService.getFriendsIDs(socialGroupDTO.getSrcUserId());
        Assert.assertNotNull(friendIds);
    }

    @Test
    public void rejectFriendship(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(), UUID.randomUUID().toString());
        String[] friendBefIds = friendsService.getFriendsIDs(socialGroupDTO.getSrcUserId());
        Assert.assertNotNull(friendBefIds[0]);
        friendsService.rejectFriendship(socialGroupDTO.getSrcUserId(),friendBefIds[0]);
        Assert.assertNotNull(friendBefIds);
    }

    @Test
    public void getFriendsIDs() {
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        //creates the friendId
        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(), "test02");
        String[] friendIds = friendsService.getFriendsIDs(socialGroupDTO.getSrcUserId());
        Assert.assertNotNull(friendIds);
    }

    @Test
    public void getFriends() {
        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        //creates the friendId
        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(), "test03");
        FriendDTO[] friendIds = friendsService.getFriends(socialGroupDTO.getSrcUserId(),pagingParams);
        Assert.assertNotNull(friendIds);
    }

    @Test
    public void getEstablishedFriends() {
        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        //creates the friendId
        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(), "test04");
        FriendDTO[] friendIds = friendsService.getEstablishedFriends(socialGroupDTO.getSrcUserId(),pagingParams);
        Assert.assertNotNull(friendIds);
    }

    @Test
    public void getAllFriends() {
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        //creates the friendId
        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(), "test05");
        long friendIds = friendsService.getAllFriends(socialGroupDTO.getSrcUserId());
        Assert.assertNotNull(friendIds);
    }

    @Test
    public void getFriendsOwnRequest() {
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        //creates the friendId
        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(), "test06");
        FriendDTO[] friendIds = friendsService.getFriendsOwnRequest(socialGroupDTO.getSrcUserId());
        Assert.assertNotNull(friendIds);
    }

    @Test
    public void getFriendsRemoteRequest() {
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        //creates the friendId
        friendsService.requestFriendship(socialGroupDTO.getSrcUserId(), "test07");
        FriendDTO[] friendIds = friendsService.getFriendsRemoteRequest(socialGroupDTO.getSrcUserId());
        Assert.assertNotNull(friendIds);
    }

}



