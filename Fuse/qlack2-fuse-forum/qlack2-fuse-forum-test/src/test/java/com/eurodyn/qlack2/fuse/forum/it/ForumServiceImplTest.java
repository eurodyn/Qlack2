package com.eurodyn.qlack2.fuse.forum.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.forum.api.ForumService;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumDTO;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class ForumServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    ForumService forumService;

    @Test
    public void createForum(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);
    }

    @Test
    public void updateForum(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        forumDTO.setTitle("testTitle01");
        forumService.updateForum(forumDTO);
        Assert.assertNotNull(forumService.getForumById(forumID.getId(),true));
    }

    @Test
    public void deleteForum(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        forumService.deleteForum(forumDTO.getId());
        Assert.assertNull(forumService.getForumById(forumID.getId(),true));
    }

    @Test
    public void getForumById(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        Assert.assertNotNull(forumService.getForumById(forumID.getId(),true));
    }

    @Test
    public void listForums(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        Assert.assertNotNull(forumService.listForums(false,true));
    }

    @Test
    public void listForumsArgs(){
        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        Assert.assertNotNull(forumService.listForums(false,true,pagingParams));
    }

    @Test
    public void lockForum(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        Assert.assertTrue(forumService.lockForum(forumID.getId()));
    }

    @Test
    public void unlockForum(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        forumService.lockForum(forumID.getId());
        Assert.assertTrue(forumService.unlockForum(forumID.getId()));
    }

    @Test
    public void archiveForum(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        Assert.assertNotNull(forumService.archiveForum(forumID.getId()));
    }

    @Test
    public void unarchiveForum(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        Assert.assertNotNull(forumService.unarchiveForum(forumID.getId()));
    }

}

