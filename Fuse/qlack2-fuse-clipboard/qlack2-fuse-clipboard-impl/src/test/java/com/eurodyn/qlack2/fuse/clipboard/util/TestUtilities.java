package com.eurodyn.qlack2.fuse.clipboard.util;

import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardEntryDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardMetaDTO;
import java.util.UUID;
import java.util.Date;

public class TestUtilities {

    public static ClipboardEntryDTO createClipboardEntryDTO(){
        ClipboardEntryDTO clipboardEntryDTO = new ClipboardEntryDTO();
        clipboardEntryDTO.setId(UUID.randomUUID().toString());
        clipboardEntryDTO.setTitle(TestConst.generateRandomString());
        clipboardEntryDTO.setSrcUserId(UUID.randomUUID().toString());
        clipboardEntryDTO.setCreatedOn(new Date().getTime());
        clipboardEntryDTO.setDescription(TestConst.generateRandomString());
        clipboardEntryDTO.setObjectId(UUID.randomUUID().toString());
        clipboardEntryDTO.setOwnerId(UUID.randomUUID().toString());
        return clipboardEntryDTO;
    }

    public static ClipboardMetaDTO createClipboardMetaDTO(){
        ClipboardMetaDTO clipboardMetaDTO = new ClipboardMetaDTO();
        clipboardMetaDTO.setId(UUID.randomUUID().toString());
        clipboardMetaDTO.setValue(TestConst.generateRandomString());
        clipboardMetaDTO.setSrcUserId(UUID.randomUUID().toString());
        clipboardMetaDTO.setName(UUID.randomUUID().toString());
        return clipboardMetaDTO;
    }

}
