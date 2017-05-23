package com.eurodyn.qlack2.fuse.forum.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.forum.api.MessageService;
import com.eurodyn.qlack2.fuse.forum.api.ForumService;
import com.eurodyn.qlack2.fuse.forum.api.TopicService;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.TopicDTO;
import com.eurodyn.qlack2.fuse.forum.api.exception.QMessageNotFound;
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
public class MessageServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    MessageService messageService;

    @Inject
    @Filter(timeout = 1200000)
    ForumService forumService;

    @Inject
    @Filter(timeout = 1200000)
    TopicService topicService;

    @Test
    public void postMessage(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);
    }

    @Test (expected =QMessageNotFound.class)
    public void deleteMessage(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        messageService.deleteMessage(messageID.getId());
        Assert.assertNull(messageService.getMessageById(messageID.getId()));
    }

    @Test
    public void updateMessage(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setText("testText01");
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        messageService.updateMessage(messageDTO);
        Assert.assertEquals("testText01",messageService.getMessageById(messageID.getId()).getText());
    }

    @Test
    public void getMessageById(){
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setText("testText02");
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.getMessageById(messageID.getId()));
        Assert.assertEquals("testText02",messageService.getMessageById(messageID.getId()).getText());
    }

    @Test
    public void listMessages() {
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.listMessages(topicID.getId()));
    }

    @Test
    public void listMessagesArgs() {
        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.listMessages(topicID.getId(),pagingParams));
    }

    @Test
    public void getMessageModerationStatus() {
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.getMessageModerationStatus(messageID.getId()));
    }

    @Test
    public void acceptMessage() {
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.acceptMessage(messageID.getId()));
    }

    @Test
    public void rejectMessage() {
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        messageService.acceptMessage(messageID.getId());
        Assert.assertNotNull(messageService.rejectMessage(messageID.getId()));
    }

    @Test
    public void getLatestMessageId() {
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.getLatestMessageId(messageID.getTopicId()));
    }

    @Test
    public void getTopicRootMessage() {
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.getTopicRootMessage(messageID.getTopicId()));
    }

    @Test
    public void getAllMessages() {
        ForumDTO forumDTO = TestUtilities.createForumDTO();
        ForumDTO forumID = forumService.createForum(forumDTO);
        Assert.assertNotNull(forumID);

        TopicDTO topicDTO = TestUtilities.createTopicDTO(forumID);
        TopicDTO topicID = topicService.createTopic(topicDTO,TestConst.messageText);
        Assert.assertNotNull(topicID);

        MessageDTO messageDTO = TestUtilities.createMessageDTO();
        messageDTO.setTopicId(topicID.getId());
        MessageDTO messageID = messageService.postMessage(messageDTO);
        Assert.assertNotNull(messageID);

        Assert.assertNotNull(messageService.getAllMessages(messageID.getTopicId()));
    }

}

