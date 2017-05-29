package com.eurodyn.qlack2.fuse.lexicon.it;

import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.TemplateDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestUtilities {

    public static TemplateDTO createTemplateDTO(){
        TemplateDTO templateDTO = new TemplateDTO();
        templateDTO.setId(UUID.randomUUID().toString());
        templateDTO.setName(TestConst.generateRandomString());
        templateDTO.setContent(TestConst.generateRandomString());
        templateDTO.setLanguageId(createLanguageDTO().getId());

        return templateDTO;
    }

    public static LanguageDTO createLanguageDTO(){
        LanguageDTO languageDTO = new LanguageDTO();
        languageDTO.setId(UUID.randomUUID().toString());
        languageDTO.setName(TestConst.generateRandomString());
        languageDTO.setActive(true);

        String strToCut = TestConst.generateRandomString();
        String locale = strToCut.substring(0,5);

        languageDTO.setLocale(locale);

        return languageDTO;
    }

    public static KeyDTO createKeyDTO(){
        KeyDTO keyDTO = new KeyDTO();
        keyDTO.setId(UUID.randomUUID().toString());
        keyDTO.setName(TestConst.generateRandomString());
        keyDTO.setGroupId(UUID.randomUUID().toString());

        Map<String, String> translations = new HashMap();
        translations.put("testTrs01","testTrsData01");

        keyDTO.setTranslations(translations);

        return keyDTO;
    }

    public static GroupDTO createGroupDTO(){
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setId(UUID.randomUUID().toString());
        groupDTO.setTitle(TestConst.generateRandomString());
        groupDTO.setDescription(TestConst.generateRandomString());

        return groupDTO;
    }

}
