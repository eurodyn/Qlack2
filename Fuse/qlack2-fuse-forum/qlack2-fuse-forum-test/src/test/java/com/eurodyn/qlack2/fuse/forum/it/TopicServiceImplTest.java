package com.eurodyn.qlack2.fuse.forum.it;

import com.eurodyn.qlack2.fuse.forum.api.TopicService;
import com.eurodyn.qlack2.fuse.forum.api.ForumService;
import com.eurodyn.qlack2.fuse.forum.api.MessageService;
import com.eurodyn.qlack2.fuse.forum.api.dto.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class TopicServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    TopicService topicService;

    @Inject
    @Filter(timeout = 1200000)
    ForumService forumService;

    @Test
    public void createTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);
    }

    @Test
    public void createTopicArgs(){
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setId(UUID.randomUUID().toString());
        Set<AttachmentDTO> messageAttachments = new HashSet<>();

        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText,messageAttachments);
        Assert.assertNotNull(topicID);
    }

    @Test
    public void updateTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        topicDTO.setDescription("testDesc01");
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);

        Assert.assertNotNull(topicID);
        Assert.assertNotNull(forumID);

        topicID.setDescription("testDesc02");
        topicService.updateTopic(topicID);
        Assert.assertNotNull(topicService.getTopicById(topicID.getId()));
        Assert.assertEquals("testDesc02",topicService.getTopicById(topicID.getId()).getDescription());
    }

    @Test
    public void deleteTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        topicService.deleteTopic(topicID.getId());
        Assert.assertNull(topicService.getTopicById(topicID.getId()));
    }

    @Test
    public void getTopicById(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        Assert.assertNotNull(topicService.getTopicById(topicID.getId()));
    }

    @Test
    public void lockTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        topicService.lockTopic(topicID.getId());
        Assert.assertNotNull(topicService.getTopicStatus(topicID.getId()));
    }

    @Test
    public void unlockTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        topicService.lockTopic(topicID.getId());
        topicService.unlockTopic(topicID.getId());
        Assert.assertNotNull(topicService.getTopicStatus(topicID.getId()));
    }

    @Test
    public void getTopicStatus(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        Assert.assertNotNull(topicService.getTopicStatus(topicID.getId()));
    }

    @Test
    public void getTopicModerationStatus(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        Assert.assertNotNull(topicService.getTopicModerationStatus(topicID.getId()));
    }

    @Test
    public void acceptTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        forumDTO.setModerated(ForumConstants.FORUM_MODERATED);
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        topicDTO.setModerationStatus(ForumConstants.MODERATION_STATUS_PENDING);
        topicDTO.setForumId(forumID.getId());
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        topicService.acceptTopic(topicID.getId());
        Assert.assertEquals(ForumConstants.MODERATION_STATUS_ACCEPTED,topicService.getTopicModerationStatus(topicID.getId()));
    }

    @Test
    public void rejectTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        forumDTO.setModerated(ForumConstants.FORUM_MODERATED);
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        topicDTO.setModerationStatus(ForumConstants.MODERATION_STATUS_PENDING);
        topicDTO.setForumId(forumID.getId());
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        topicService.rejectTopic(topicID.getId());
        Assert.assertEquals(ForumConstants.MODERATION_STATUS_REJECTED,topicService.getTopicModerationStatus(topicID.getId()));
    }

    @Test
    public void archiveTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        Assert.assertTrue(topicService.archiveTopic(topicID.getId()));
    }

    @Test
    public void unarchiveTopic(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        Assert.assertTrue(topicService.archiveTopic(topicID.getId()));
        Assert.assertFalse(topicService.archiveTopic(topicID.getId()));
    }

}

