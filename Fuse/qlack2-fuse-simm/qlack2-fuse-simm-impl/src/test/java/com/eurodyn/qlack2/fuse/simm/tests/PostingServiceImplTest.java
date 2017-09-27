package com.eurodyn.qlack2.fuse.simm.tests;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.PostingService;
import com.eurodyn.qlack2.fuse.simm.api.dto.PostItemDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants;
import com.eurodyn.qlack2.fuse.simm.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.simm.util.TestUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class PostingServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    PostingService postingService;

    @Test
    public void createActivity(){
        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO postItemID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(postItemID.getId());
    }

    @Test
    public void getHomePageActivities(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO postItemID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(postItemID.getId());
        byte[] status = {postItemID.getStatus()};

        Assert.assertNotNull(postingService.getHomePageActivities(postItemID.getHomepageID(),paging,status,false,false));

    }

    @Test
    public void getActivityChildren(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        PostItemDTO postParentItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO postParentItemID = postingService.createActivity(postParentItemDTO);
        Assert.assertNotNull(postParentItemID.getId());

        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        postItemDTO.setParentHomepageID(postParentItemID.getId());
        PostItemDTO postItemID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(postItemID.getId());
        byte[] status = {postItemID.getStatus()};

        Assert.assertNotNull(postingService.getActivityChildren(postItemID.getParentHomepageID(),paging,status,false));
    }

    @Test
    public void getActivityChildrenCatId(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        PostItemDTO postParentItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO postParentItemID = postingService.createActivity(postParentItemDTO);
        Assert.assertNotNull(postParentItemID.getId());

        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        postItemDTO.setParentHomepageID(postParentItemID.getId());
        PostItemDTO postItemID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(postItemID.getId());
        byte[] status = {postItemID.getStatus()};

        Assert.assertNotNull(postingService.getActivityChildren(postItemDTO.getParentHomepageID(),paging,status,false,postItemDTO.getCategoryID()));
    }

    @Test
    public void getChildrenNumber(){
        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO postItemID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(postItemID.getId());
        byte[] status = {postItemID.getStatus()};

        Assert.assertNotNull(postingService.getChildrenNumber(postItemID.getParentHomepageID(),status));
    }

    @Test
    public void getChildrenNumberActCategId(){
        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO postItemID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(postItemID.getId());
        byte[] status = {postItemID.getStatus()};

        Assert.assertNotNull(postingService.getChildrenNumber(postItemID.getParentHomepageID(),status,postItemDTO.getCategoryID()));
    }

    @Test
    public void getLastActivityOfType(){
        PostItemDTO postParentItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO postParentItemID = postingService.createActivity(postParentItemDTO);
        Assert.assertNotNull(postParentItemID.getId());

        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        postItemDTO.setParentHomepageID(postParentItemID.getId());
        PostItemDTO postItemID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(postItemID.getId());
        byte[] status = {postItemID.getStatus()};

        Assert.assertNotNull(postingService.getLastActivityOfType(postItemID.getHomepageID(),postItemDTO.getCategoryID(),status));
    }

    @Test
    public void approveActivity(){
        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO activityID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(activityID.getId());
        byte[] status = {activityID.getStatus()};

        postingService.approveActivity(activityID.getId());
        Assert.assertNotNull(postingService.getActivity(activityID.getId(),false,false).getStatus());
        Assert.assertEquals(SIMMConstants.HOME_PAGE_ACTIVITY_STATUS_APPROVED,postingService.getActivity(activityID.getId(),false,false).getStatus());
    }

    @Test
    public void updateActivity(){
        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO activityID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(activityID.getId());
        byte[] status = {activityID.getStatus()};

        postItemDTO.setTitle("testTitle01");
        postingService.updateActivity(postItemDTO);
        Assert.assertNotNull(postingService.getActivity(activityID.getId(),false,false));
    }

    @Test
    public void deleteActivity(){
        PostItemDTO postItemDTO = TestUtilities.createPostItemDTO();
        PostItemDTO activityID = postingService.createActivity(postItemDTO);
        Assert.assertNotNull(activityID.getId());
        byte[] status = {activityID.getStatus()};

        postingService.deleteActivity(postItemDTO);
        Assert.assertNull(postingService.getActivity(activityID.getId(),false,false));
    }

}


