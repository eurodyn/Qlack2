package com.eurodyn.qlack2.fuse.lexicon.tests;

import com.eurodyn.qlack2.fuse.lexicon.api.KeyService;
import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.criteria.KeySearchCriteria;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.lexicon.util.TestConst;
import com.eurodyn.qlack2.fuse.lexicon.util.TestUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.*;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class KeyServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    LanguageService languageService;

    @Inject
    @Filter(timeout = 1200000)
    KeyService keyService;

    @Inject
    @Filter(timeout = 1200000)
    GroupService groupService;

    @Test
    public void createKey(){
        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);
    }

    @Test
    public void createKeys(){
        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        List<KeyDTO> keys = new ArrayList<>();
        keys.add(keyDTO);
        Assert.assertNotNull(keyService.createKeys(keys,true));
    }

    @Test
    public void deleteKey(){
        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        keyService.deleteKey(keyID);
        Assert.assertNull(keyService.getKeyByID(keyID,false));
    }

    @Test
    public void deleteKeys(){
        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        List<KeyDTO> keys = new ArrayList<>();
        keys.add(keyDTO);
        List<String> keyIDs = keyService.createKeys(keys,true);
        Assert.assertNotNull(keyIDs);

        keyService.deleteKeys(keyIDs);
        Assert.assertNull(keyService.getKeyByID(keyDTO.getId(),false));
    }

    @Test
    public void deleteKeysByGroupId(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        keyService.deleteKeysByGroupId(keyDTO.getGroupId());
        Assert.assertNull(keyService.getKeyByID(keyDTO.getGroupId(),false));
    }

    @Test
    public void renameKey(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        String newKeyName = TestConst.generateRandomString();
        keyDTO.setName(newKeyName);

        keyService.renameKey(keyID,keyDTO.getName());
        Assert.assertEquals(newKeyName,keyService.getKeyByID(keyID,false).getName());
    }

    @Test
    public void moveKeys(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        List<KeyDTO> keys = new ArrayList<>();
        keys.add(keyDTO);
        List<String> keyIDs = keyService.createKeys(keys,true);
        Assert.assertNotNull(keyIDs);

        String newGroupID = UUID.randomUUID().toString();
        keyDTO.setGroupId(newGroupID);

        keyService.moveKeys(keyIDs,keyDTO.getGroupId());
        Assert.assertNull(keyService.getKeyByName(keyDTO.getId(),keyDTO.getGroupId(),false));
    }

    @Test
    public void getKeyByID(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Assert.assertNull(keyService.getKeyByID(keyDTO.getId(),false));
    }

    @Test
    public void getKeyByName(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Assert.assertNotNull(keyService.getKeyByName(keyDTO.getName(),keyDTO.getGroupId(),false));
    }

    @Test
    public void findKeys(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        KeySearchCriteria keySearchCriteria = new KeySearchCriteria();

        Assert.assertNotNull(keyService.findKeys(keySearchCriteria,false));
    }

    @Test
    public void updateTranslation(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        keyService.updateTranslation(keyID,languageID,"value01");
        Assert.assertNotNull(keyService.getTranslation(keyDTO.getName(),languageDTO.getLocale()));
    }

    @Test
    public void updateTranslationByKeyName(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        keyService.updateTranslationByKeyName(keyDTO.getName(),keyID,languageID,"value02");
        Assert.assertNotNull(keyService.getTranslationsForKeyName(keyDTO.getName(),groupID));
    }

    @Test
    public void updateTranslationByGroupId(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        keyService.updateTranslationByGroupId(keyDTO.getName(),"value03",groupID,languageID);
        Assert.assertNotNull(keyService.getTranslationsForGroupAndLocale(groupID,languageDTO.getLocale()));
    }

    @Test
    public void updateTranslationsByGroupId(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Map<String, String> keys = new HashMap<>();
        keys.put("testKey04","testVal04");

        keyService.updateTranslationsByGroupId(keys,groupID,languageID);
        Assert.assertNotNull(keyService.getTranslationsForGroupAndLocale(groupID,languageDTO.getLocale()));
    }

    @Test
    public void updateTranslationByLocale(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        keyService.updateTranslationByLocale(keyID,languageDTO.getLocale(),"value05");
        Assert.assertNotNull(keyService.getTranslationsForLocale(languageDTO.getLocale()));
    }

    @Test
    public void updateTranslationsForKey(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Map<String, String> translations = new HashMap<>();
        translations.put("testKey06","testVal06");

        keyService.updateTranslationsForKey(keyID,translations);
        Assert.assertNotNull(keyService.getTranslation(keyDTO.getName(),languageDTO.getLocale()));
    }

    @Test
    public void updateTranslationsForKeyByLocale(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Map<String, String> translations = new HashMap<>();
        translations.put("testKey07","testVal07");

        keyService.updateTranslationsForKeyByLocale(keyID,translations);
        Assert.assertNotNull(keyService.getTranslation(keyDTO.getName(),languageDTO.getLocale()));
    }

    @Test
    public void updateTranslationsForLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Map<String, String> translations = new HashMap<>();
        translations.put("testKey08","testVal08");

        keyService.updateTranslationsForLanguage(keyID,translations);
        Assert.assertNotNull(keyService.getTranslation(keyDTO.getName(),languageDTO.getLocale()));
    }

    @Test
    public void updateTranslationsForLanguageByKeyName(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Map<String, String> translations = new HashMap<>();
        translations.put("testKey09","testVal09");

        keyService.updateTranslationsForLanguageByKeyName(languageID,keyID,translations);
        Assert.assertNotNull(keyService.getTranslation(keyDTO.getName(),languageDTO.getLocale()));
    }

    @Test
    public void getTranslation(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Assert.assertNotNull(keyService.getTranslation(keyDTO.getName(),languageDTO.getLocale()));
    }

    @Test
    public void getTranslationsForLocale(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Assert.assertNotNull(keyService.getTranslationsForLocale(languageDTO.getLocale()));
    }

    @Test
    public void getTranslationsForGroupAndLocale(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        Assert.assertNotNull(keyService.getTranslationsForGroupAndLocale(groupID,languageDTO.getLocale()));
    }

}

