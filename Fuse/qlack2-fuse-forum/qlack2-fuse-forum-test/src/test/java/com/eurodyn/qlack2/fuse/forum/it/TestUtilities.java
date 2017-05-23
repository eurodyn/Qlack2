package com.eurodyn.qlack2.fuse.forum.it;

import java.util.UUID;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.TopicDTO;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

public class TestUtilities {

  public static ForumDTO createForumDTO(){
      ForumDTO forumDTO = new ForumDTO();
      forumDTO.setId(UUID.randomUUID().toString());
      forumDTO.setTitle(TestConst.generateRandomString());
      forumDTO.setDescription(TestConst.generateRandomString());
      forumDTO.setArchived(true);
      forumDTO.setCreatedBy(TestConst.generateRandomString());
      forumDTO.setCreatedOn(new Date().getTime());

      TopicDTO topicsDTO = createTopicDTO(forumDTO);
      List<TopicDTO> tpcs = new ArrayList();
      tpcs.add(topicsDTO);

      forumDTO.setTopics(tpcs);
      return forumDTO;
  }

  public static MessageDTO createMessageDTO(){
      MessageDTO messageDTO = new MessageDTO();
      messageDTO.setId(UUID.randomUUID().toString());
      messageDTO.setCreatedOn(new Date().getTime());
      messageDTO.setTopicId(UUID.randomUUID().toString());
      messageDTO.setText(TestConst.generateRandomString());
      messageDTO.setCreatorId(TestConst.generateRandomString());

      return messageDTO;
  }

  public static TopicDTO createTopicDTO(ForumDTO forumDTO){
      TopicDTO topicDTO = new TopicDTO();
      topicDTO.setTitle(TestConst.generateRandomString());
      topicDTO.setDescription(TestConst.generateRandomString());
      topicDTO.setId(UUID.randomUUID().toString());
      topicDTO.setCreatedOn(new Date().getTime());
      topicDTO.setForumId(forumDTO.getId());
      topicDTO.setCreatorId(UUID.randomUUID().toString());

      return topicDTO;
  }

}
