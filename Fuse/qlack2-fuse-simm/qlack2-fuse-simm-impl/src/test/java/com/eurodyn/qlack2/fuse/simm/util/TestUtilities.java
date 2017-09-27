package com.eurodyn.qlack2.fuse.simm.util;

import com.eurodyn.qlack2.fuse.simm.api.dto.NotificationDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.PostItemDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupAttributeDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static PostItemDTO createPostItemDTO(){
        PostItemDTO postItemDTO = new PostItemDTO();
        postItemDTO.setId(UUID.randomUUID().toString());
        postItemDTO.setCreatedOn(new Date().getTime());
        postItemDTO.setTitle(TestConst.generateRandomString());
        postItemDTO.setDescription(TestConst.generateRandomString());
        postItemDTO.setCategoryIcon(TestConst.generateRandomString());
        postItemDTO.setCategoryID(UUID.randomUUID().toString());
        postItemDTO.setStatus(TestConst.byte_status);
        postItemDTO.setHomepageID(UUID.randomUUID().toString());
        postItemDTO.setSrcUserId(UUID.randomUUID().toString());
        postItemDTO.setCreatedByUserID(UUID.randomUUID().toString());
        postItemDTO.setCreatedByUserFullname(TestConst.generateRandomString());

        return postItemDTO;
    }

    public static NotificationDTO createNotificationDTO(){
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(UUID.randomUUID().toString());
        notificationDTO.setCreatedOn(new Date().getTime());
        notificationDTO.setTitle(TestConst.generateRandomString());
        notificationDTO.setDescription(TestConst.generateRandomString());
        notificationDTO.setStatus(TestConst.byte_status);
        notificationDTO.setSrcUserId(UUID.randomUUID().toString());
        notificationDTO.setFromUserID(UUID.randomUUID().toString());
        notificationDTO.setToUserID(UUID.randomUUID().toString());

        return notificationDTO;
    }

    public static SocialGroupDTO createSocialGroupDTO(){
        SocialGroupDTO socialGroupDTO = new SocialGroupDTO();
        socialGroupDTO.setId(UUID.randomUUID().toString());
        socialGroupDTO.setCreatedOn(new Date().getTime());
        socialGroupDTO.setName(TestConst.generateRandomString());
        socialGroupDTO.setDescription(TestConst.generateRandomString());
        socialGroupDTO.setStatus(TestConst.byte_status);
        socialGroupDTO.setSrcUserId(UUID.randomUUID().toString());
        socialGroupDTO.setParentGroupId(UUID.randomUUID().toString());
        socialGroupDTO.setCreatedOn(new Date().getTime());
        socialGroupDTO.setSlugify(TestConst.generateRandomString());
        socialGroupDTO.setSocialGroupAttribute(createSocialGroupAttributeDTO());

        return socialGroupDTO;
    }

    public static SocialGroupAttributeDTO createSocialGroupAttributeDTO(){
        SocialGroupAttributeDTO socialGroupAttributeDTO = new SocialGroupAttributeDTO();
        socialGroupAttributeDTO.setId(UUID.randomUUID().toString());
        socialGroupAttributeDTO.setName(TestConst.generateRandomString());
        socialGroupAttributeDTO.setContentType(TestConst.generateRandomString());
        socialGroupAttributeDTO.setGroupId(UUID.randomUUID().toString());

        return socialGroupAttributeDTO;
    }
}
