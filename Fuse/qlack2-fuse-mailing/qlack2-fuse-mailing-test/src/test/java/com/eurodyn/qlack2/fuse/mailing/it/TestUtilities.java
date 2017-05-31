package com.eurodyn.qlack2.fuse.mailing.it;

import com.eurodyn.qlack2.fuse.mailing.api.dto.*;

import java.util.*;

public class TestUtilities {

    public static InternalMessagesDTO createInternalMessagesDTO(){
        InternalMessagesDTO internalMessagesDTO = new InternalMessagesDTO();
        internalMessagesDTO.setId(UUID.randomUUID().toString());
        internalMessagesDTO.setDateReceived(new Date().getTime());
        internalMessagesDTO.setDateSent(new Date().getTime());
        internalMessagesDTO.setFrom(TestConst.generateRandomString());
        internalMessagesDTO.setDeleteType("I");
        internalMessagesDTO.setMessage(TestConst.generateRandomString());
        internalMessagesDTO.setStatus(TestConst.generateRandomString());
        internalMessagesDTO.setSubject(TestConst.generateRandomString());
        internalMessagesDTO.setSrcUserId(UUID.randomUUID().toString());
        internalMessagesDTO.setTo(TestConst.generateRandomString());

        InternalAttachmentDTO attachmentDTO = new InternalAttachmentDTO();
        attachmentDTO.setId(UUID.randomUUID().toString());
        attachmentDTO.setContentType(TestConst.generateRandomString());
        attachmentDTO.setFilename(TestConst.generateRandomString());
        attachmentDTO.setData(TestConst.data);

        List<InternalAttachmentDTO> attachmentDTOList = new ArrayList<>();
        attachmentDTOList.add(attachmentDTO);

        internalMessagesDTO.setAttachments(attachmentDTOList);
        return internalMessagesDTO;
    }

    public static EmailDTO createEmailDTO(){
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setId(UUID.randomUUID().toString());
        emailDTO.setSubject(TestConst.generateRandomString());
        emailDTO.setStatus(TestConst.generateRandomString());
        emailDTO.setBody(TestConst.generateRandomString());
        emailDTO.setDateSent(new Date().getTime());
        emailDTO.setFrom(TestConst.generateRandomString());
        emailDTO.setMessageId(UUID.randomUUID().toString());
        emailDTO.setToContact(TestConst.generateRandomString());

        return emailDTO;
    }

    public static DistributionListDTO createDistributionList(){
        DistributionListDTO distributionListDTO = new DistributionListDTO();
        distributionListDTO.setId(UUID.randomUUID().toString());
        distributionListDTO.setName(TestConst.generateRandomString());
        distributionListDTO.setCreatedOn(new Date().getTime());
        distributionListDTO.setCreatedBy(TestConst.generateRandomString());
        distributionListDTO.setSrcUserId(UUID.randomUUID().toString());
        distributionListDTO.setDescription(TestConst.generateRandomString());

        return distributionListDTO;
    }

    public static ContactDTO createContactDTO(){
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setId(UUID.randomUUID().toString());
        contactDTO.setEmail(TestConst.generateRandomString());
        contactDTO.setUserID(UUID.randomUUID().toString());
        contactDTO.setFirstName(TestConst.generateRandomString());
        contactDTO.setLastName(TestConst.generateRandomString());
        contactDTO.setLocale("test");

        return contactDTO;
    }

}
