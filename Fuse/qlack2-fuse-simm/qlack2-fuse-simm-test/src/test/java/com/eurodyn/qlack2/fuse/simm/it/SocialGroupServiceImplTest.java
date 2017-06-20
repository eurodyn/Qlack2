package com.eurodyn.qlack2.fuse.simm.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.SocialGroupService;
import com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SocialGroupServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    SocialGroupService socialGroupService;

    @Test
    public void createGroup(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());
    }

    @Test
    public void viewGroup(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        SocialGroupDTO socialGroupGetDTO = socialGroupService.viewGroup(socialGroupID.getId());
        Assert.assertNotNull(socialGroupGetDTO);
    }

    @Test
    public void updateGroup(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        socialGroupID.setName("testName01");
        SocialGroupDTO socialGroupGetDTO = socialGroupService.updateGroup(socialGroupID);
        Assert.assertNotNull(socialGroupGetDTO);
    }

    @Test
    public void deleteGroup(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        socialGroupService.deleteGroup(socialGroupID.getId());
        Assert.assertNull(socialGroupService.viewGroup(socialGroupID.getId()));
    }

    @Test
    public void searchGroups(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        Assert.assertNotNull(socialGroupService.searchGroups("test",paging,TestConst.privacy));
    }

    @Test
    public void listGroups(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        Assert.assertNotNull(socialGroupService.listGroups(paging));
    }

    @Test
    public void suspendGroup(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        socialGroupService.suspendGroup(socialGroupID.getId());
        Assert.assertEquals(SIMMConstants.GROUP_STATUS_SUSPENDED,socialGroupService.viewGroup(socialGroupID.getId()).getStatus());
    }

    @Test
    public void resumeGroup(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        socialGroupService.resumeGroup(socialGroupID.getId());
        Assert.assertEquals(SIMMConstants.GROUP_STATUS_APPROVED,socialGroupService.viewGroup(socialGroupID.getId()).getStatus());
    }

    @Test
    public void getGroupUsers(){
        PagingParams paging = new PagingParams();
        paging.setCurrentPage(0);
        paging.setPageSize(0);

        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        Assert.assertNotNull(socialGroupService.getGroupUsers(socialGroupID.getId(),TestConst.privacy,paging));
    }

    @Test
    public void groupNameAlreadyExists(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        Assert.assertNotNull(socialGroupService.groupNameAlreadyExists(socialGroupID.getName(),socialGroupID.getId()));
    }

    @Test
    public void findGroupByName(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        Assert.assertNotNull(socialGroupService.findGroupByName(socialGroupID.getName()));
    }

    @Test
    public void groupURLAlreadyExists(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        Assert.assertTrue(socialGroupService.groupURLAlreadyExists(socialGroupID.getSlugify(),socialGroupID.getSrcUserId()));
    }

    @Test
    public void findGroupByURL(){
        SocialGroupDTO socialGroupDTO = TestUtilities.createSocialGroupDTO();
        SocialGroupDTO socialGroupID = socialGroupService.createGroup(socialGroupDTO);
        Assert.assertNotNull(socialGroupID.getId());

        Assert.assertNotNull(socialGroupService.findGroupByURL(socialGroupID.getSlugify()));
    }

}



