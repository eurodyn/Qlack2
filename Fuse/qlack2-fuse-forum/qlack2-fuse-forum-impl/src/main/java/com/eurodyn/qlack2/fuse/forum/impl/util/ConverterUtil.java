/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.forum.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.eurodyn.qlack2.fuse.forum.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.MessageDTO;
import com.eurodyn.qlack2.fuse.forum.api.dto.TopicDTO;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmAttachment;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmForum;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmMessage;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmTopic;

/**
 *
 * @author European Dynamics SA.
 */
public final class ConverterUtil {

    /**
     * Converts an FrmForum object to a ForumDTO object
     * @param forum The FrmForum object to convert
     * @return A ForumDTO object containing the information passed through the forum parameter
     */
    public static ForumDTO convert2ForumDTO(FrmForum forum) {
        ForumDTO dto = null;
        if (null != forum) {
            dto = new ForumDTO();
            dto.setCreatedBy(forum.getCreatedBy());
            dto.setCreatedOn(forum.getCreatedOn());
            dto.setId(forum.getId());
            dto.setStatus(forum.getStatus());
            dto.setTitle(forum.getTitle());
            dto.setDescription(forum.getDescription());
            dto.setLogo(forum.getLogo());
            dto.setModerated(forum.getModerated());
            dto.setArchived(forum.isArchived());
        }
        return dto;
    }

    /**
     * Converts a ForumDTO object to a FrmForum object. This method does not set the
     * topics included in this forum (frmTopics)
     * @param forum The ForumDTO object to convert
     * @return A FrmForum object containing the information passed through the forum parameter
     */
    public static FrmForum convert2ForumModel(ForumDTO forum) {
        FrmForum entity = null;
        if (null != forum) {
            entity = new FrmForum();
            entity.setCreatedBy(forum.getCreatedBy());
            entity.setCreatedOn(forum.getCreatedOn());
            entity.setId(forum.getId());
            entity.setStatus(forum.getStatus());
            entity.setTitle(forum.getTitle());
            entity.setDescription(forum.getDescription());
            entity.setLogo(forum.getLogo());
            entity.setModerated(forum.getModerated());
            entity.setArchived(forum.isArchived());

        }
        return entity;
    }

    /**
     * Used to convert Forum Model list to Forum DTO list
     * @param forumList
     * @return forum dto.
     */
    public static List<ForumDTO> convert2ForumDTOList(List<FrmForum> forumList) {
        List<ForumDTO> list = null;
        if (null != forumList) {
            list = new ArrayList<ForumDTO>(forumList.size());
            for (FrmForum forum : forumList) {
                list.add(ConverterUtil.convert2ForumDTO(forum));
            }
        }
        return list;
    }

    /**
     * Converts an FrmTopic object to a TopicDTO object
     * @param topic The FrmTopic object to convert
     * @return A TopicDTO object containing the information passed through the topic parameter
     */
    public static TopicDTO convert2TopicDTO(FrmTopic topic) {
        TopicDTO dto = null;
        if (topic != null) {
            dto = new TopicDTO();
            dto.setId(topic.getId());
            dto.setCreatedOn(topic.getCreatedOn());
            dto.setDescription(topic.getDescription());
            dto.setLogo(topic.getLogo());
            dto.setStatus(topic.getStatus());
            dto.setTitle(topic.getTitle());
            dto.setCreatorId(topic.getCreatedBy());
            dto.setModerated(topic.isModerated());
            dto.setForumId(topic.getFrmForumId().getId());
            dto.setArchived(topic.isArchived());
            dto.setModerationStatus(topic.getModerationStatus());
        }
        return dto;
    }

    /**
     * Converts an TopicDTO object to a FrmTopic object. This method does not set
     * the foreign-key-related attributes of the FrmTopic object (frmForumId).
     * @param topic The TopicDTO object to convert
     * @return A FrmTopic object containing the information passed through the topic parameter
     */
    public static FrmTopic convert2TopicModel(TopicDTO topic) {
        FrmTopic entity = null;
        if (topic != null) {
            entity = new FrmTopic();
            entity.setId(topic.getId());
            entity.setTitle(topic.getTitle());
            entity.setDescription(topic.getDescription());
            entity.setLogo(topic.getLogo());
            entity.setCreatedOn(topic.getCreatedOn());
            entity.setStatus(topic.getStatus());
            entity.setCreatedBy(topic.getCreatorId());
            entity.setModerated(topic.getModerated());
            entity.setArchived(topic.isArchived());
            entity.setModerationStatus(topic.getModerationStatus());
        }
        return entity;
    }

    /**
     * The method used to convert Message dto to message entity.
     *
     * @param messageDTO The MessageDTO object to convert
     * @return A FrmMessage object containing message information
     */
    public static FrmMessage convert2MessageModel(MessageDTO messageDTO) {
        FrmMessage entity = null;
        if (messageDTO != null) {
            entity = new FrmMessage();
            entity.setId(messageDTO.getId());
            entity.setText(messageDTO.getText());
            entity.setCreatedOn(messageDTO.getCreatedOn());
            entity.setCreatedBy(messageDTO.getCreatorId());
            entity.setModerationStatus(messageDTO.getModerationStatus());
            if (messageDTO.getParentId() == null) {
                entity.setParentId(null);
            } else {
                FrmMessage messageParent = new FrmMessage();
                messageParent.setId(messageDTO.getParentId());
                entity.setParentId(messageParent);
            }
        }
        return entity;
    }

    /**
     * Method converts an FrmMessage object to a MessageDTO Object
     *
     * @param message FrmMessage Entity Object.
     * @return MessageDTO Transfer Object.
     */
    public static MessageDTO convert2MessageDTO(FrmMessage message) {
        MessageDTO dto = null;
        if (message != null) {
            dto = new MessageDTO();
            dto.setId(message.getId());
            dto.setCreatedOn(message.getCreatedOn());
            dto.setCreatorId(message.getCreatedBy());
            dto.setTopicId(message.getFrmTopicId().getId());
            if (message.getParentId() != null) {
                dto.setParentId(message.getParentId().getId());
            } else {
                dto.setParentId(null);
            }
            dto.setModerationStatus(message.getModerationStatus());
            dto.setText(message.getText());

            Set<FrmAttachment> collectionAttachments = message.getFrmAttachments();
            if (collectionAttachments != null) {
                for (FrmAttachment attachment : collectionAttachments) {
                    dto.setAttachment(convert2AttachmentDTO(attachment));
                }
            }
        }
        return dto;
    }

    /**
     * Method converts an AttachmentDTO Transfer object to FrmAttachment Entity object.
     * @param attachmentDTO Transfer object.
     * @return FrmAttachment Entity Object.
     */
    public static FrmAttachment convert2AttachmentModel(AttachmentDTO attachmentDTO) {
        FrmAttachment attachment = null;
        if (attachmentDTO != null) {
            attachment = new FrmAttachment();
            attachment.setContent(attachmentDTO.getContent());
            attachment.setFilename(attachmentDTO.getFilename());
            attachment.setMimetype(attachmentDTO.getMimetype());          
            attachment.setId(attachmentDTO.getId());
 
        }
        return attachment;
    }

    /**
     * Method converts an FrmAttachment Entity object to AttachmentDTO Transfer object.
     *
     * @param attachment FrmAttachment Entity Object.
     * @return AttachmentDTO Transfer Object.
     */
    public static AttachmentDTO convert2AttachmentDTO(FrmAttachment attachment) {
        AttachmentDTO dto = null;

        if (attachment != null) {
            dto = new AttachmentDTO();
            dto.setId(attachment.getId());
            dto.setContent(attachment.getContent());
            dto.setFilename(attachment.getFilename());
            dto.setMimetype(attachment.getMimetype());
            dto.setMessageId(attachment.getFrmMessageId().getId());
        }
        return dto;
    }
}
