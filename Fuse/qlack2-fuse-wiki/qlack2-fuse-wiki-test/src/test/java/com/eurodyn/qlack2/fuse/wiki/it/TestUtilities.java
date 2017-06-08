package com.eurodyn.qlack2.fuse.wiki.it;

import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryVersionDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiTagDTO;

import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static WikiEntryDTO createWikiEntryDTO(){
        WikiEntryDTO wikiEntryDTO = new WikiEntryDTO();
        wikiEntryDTO.setId(UUID.randomUUID().toString());
        wikiEntryDTO.setSrcUserId(UUID.randomUUID().toString());
        wikiEntryDTO.setTitle(TestConst.generateRandomString());
        wikiEntryDTO.setDtCreated(new Date());
        wikiEntryDTO.setCreatedBy(TestConst.generateRandomString());
        wikiEntryDTO.setDtLastModified(new Date());
        wikiEntryDTO.setDtLastModified(new Date());
        wikiEntryDTO.setLock(false);
        wikiEntryDTO.setLockedBy(TestConst.generateRandomString());
        wikiEntryDTO.setWikiId(UUID.randomUUID().toString());
        wikiEntryDTO.setHomepage(true);
        wikiEntryDTO.setNamespace(TestConst.generateRandomString());

        return wikiEntryDTO;
    }

    public static WikiDTO createWikiDTO(){
        WikiDTO wikiDTO = new WikiDTO();
        wikiDTO.setId(UUID.randomUUID().toString());
        wikiDTO.setSrcUserId(UUID.randomUUID().toString());
        wikiDTO.setDescription(TestConst.generateRandomString());
        wikiDTO.setName(TestConst.generateRandomString());
        wikiDTO.setSrcUserId(UUID.randomUUID().toString());

        return wikiDTO;
    }

    public static WikiTagDTO createWikiTagDTO(){
        WikiTagDTO wikiTagDTO = new WikiTagDTO();
        wikiTagDTO.setId(UUID.randomUUID().toString());
        wikiTagDTO.setSrcUserId(UUID.randomUUID().toString());
        wikiTagDTO.setDescription(TestConst.generateRandomString());
        wikiTagDTO.setName(TestConst.generateRandomString());
        wikiTagDTO.setSrcUserId(UUID.randomUUID().toString());

        return wikiTagDTO;
    }

    public static WikiEntryVersionDTO createWikiEntryVersionDTO(){
        WikiEntryVersionDTO wikiEntryVersionDTO = new WikiEntryVersionDTO();
        wikiEntryVersionDTO.setId(UUID.randomUUID().toString());
        wikiEntryVersionDTO.setDtCreated(new Date());
        wikiEntryVersionDTO.setCreatedBy(TestConst.generateRandomString());
        wikiEntryVersionDTO.setSrcUserId(UUID.randomUUID().toString());

        return wikiEntryVersionDTO;
    }

}
