package com.eurodyn.qlack2.fuse.lexicon.it;

import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.KeyService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.io.*;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class LanguageServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    LanguageService languageService;

    @Inject
    @Filter(timeout = 1200000)
    GroupService groupService;

    @Inject
    @Filter(timeout = 1200000)
    KeyService keyService;

    @Test
    public void createLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);
    }

    @Test
    public void createLanguageUsingKeyName(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO,"testPrefix");
        Assert.assertNotNull(languageID);
    }

    @Test
    public void createLanguageByExistingLanguage(){
        LanguageDTO languageScrDTO = TestUtilities.createLanguageDTO();
        String languageScrID = languageService.createLanguage(languageScrDTO);
        Assert.assertNotNull(languageScrID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        KeyDTO keyDTO = TestUtilities.createKeyDTO();
        keyDTO.setGroupId(groupID);
        String keyID = keyService.createKey(keyDTO,true);
        Assert.assertNotNull(keyID);

        keyService.updateTranslationByKeyName(keyDTO.getName(),keyID,languageScrID,"value02");
        Assert.assertNotNull(keyService.getTranslationsForKeyName(keyDTO.getName(),groupID));

        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO,languageScrID,"prefix");
        Assert.assertNotNull(languageID);
    }

    @Test
    public void updateLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        languageDTO.setName("testUpdate01");
        String languageID = languageService.createLanguage(languageDTO);
        languageDTO.setId(languageID);
        Assert.assertNotNull(languageID);

        languageService.updateLanguage(languageDTO);
        Assert.assertNotNull(languageService.getLanguage(languageID));
        Assert.assertEquals("testUpdate01",languageService.getLanguage(languageID).getName());
    }

    @Test
    public void deleteLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        Assert.assertNotNull(languageService.getLanguage(languageID));
        languageService.deleteLanguage(languageID);
        Assert.assertNull(languageService.getLanguage(languageID));
    }

    @Test
    public void activateLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        languageService.activateLanguage(languageID);
        Assert.assertTrue(languageService.getLanguage(languageID).isActive());
    }

    @Test
    public void deactivateLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        languageService.deactivateLanguage(languageID);
        Assert.assertFalse(languageService.getLanguage(languageID).isActive());
    }

    @Test
    public void getLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        Assert.assertNotNull(languageService.getLanguage(languageID));
        Assert.assertNotNull(languageService.getLanguage(languageID).getName());
    }

    @Test
    public void getLanguageByLocale(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        Assert.assertNotNull(languageService.getLanguageByLocale(languageDTO.getLocale()));
    }

    @Test
    public void getLanguages(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        Assert.assertNotNull(languageService.getLanguages(true));
    }

    @Test
    public void getEffectiveLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        Assert.assertNotNull(languageService.getEffectiveLanguage(languageDTO.getLocale(),languageDTO.getLocale()));
    }

    @Test
    public void downloadLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        Assert.assertNotNull(languageService.downloadLanguage(languageID));
    }

    @Test
    public void uploadLanguage(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        try {
           FileInputStream excelFile = new FileInputStream(new File(TestConst.createPath()));
           Workbook workbook = new XSSFWorkbook(excelFile);
           ByteArrayOutputStream bos = new ByteArrayOutputStream();
           workbook.write(bos);
           byte[] bytes = bos.toByteArray();
           languageService.uploadLanguage(languageID,bytes);
           Assert.assertNotNull(languageService.downloadLanguage(languageID));
        }catch(IOException e){ }
    }

    @Test
    public void isLocaleRTL(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        //expect that local isnt for RTL
        Assert.assertFalse(languageService.isLocaleRTL(languageDTO.getLocale()));
    }

}

