package com.eurodyn.qlack2.fuse.lexicon.tests;

import com.eurodyn.qlack2.fuse.lexicon.api.TemplateService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.TemplateDTO;
import com.eurodyn.qlack2.fuse.lexicon.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.lexicon.util.TestUtilities;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class TemplateServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    TemplateService templateService;

    @Inject
    @Filter(timeout = 1200000)
    LanguageService languageService;

    @Test
    public void createTemplate(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);
    }

    @Test
    public void updateTemplate(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);

        //update DTO
        templateDTO.setName("testName01");
        templateDTO.setId(templateID);

        templateService.updateTemplate(templateDTO);
        Assert.assertEquals("testName01",templateService.getTemplate(templateID).getName());
    }

    @Test
    public void deleteTemplate(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);

        Assert.assertNotNull(templateService.getTemplate(templateID).getName());
        templateService.deleteTemplate(templateID);
        Assert.assertNull(templateService.getTemplate(templateID));
    }

    @Test
    public void getTemplate(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);

        Assert.assertNotNull(templateService.getTemplate(templateID).getName());
        Assert.assertNotNull(templateService.getTemplate(templateID).getId());
    }

    @Test
    public void getTemplateContentByName(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);

        Assert.assertNotNull(templateService.getTemplateContentByName(templateDTO.getName()));
    }

    @Test
    public void getTemplateContentByNameArgs(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);

        Assert.assertNotNull(templateService.getTemplateContentByName(templateDTO.getName(),languageID));
    }

    @Test
    public void processTemplateByName(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);

        Map<String, Object> templateData = new HashMap();
        templateData.put("testKey01","testData01");

        Assert.assertNotNull(templateService.processTemplateByName(templateDTO.getName(),languageID,templateData));
    }

    @Test
    public void processTemplate(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        TemplateDTO templateDTO = TestUtilities.createTemplateDTO();
        templateDTO.setLanguageId(languageID);
        String templateID = templateService.createTemplate(templateDTO);
        Assert.assertNotNull(templateID);

        Map<String, Object> templateData = new HashMap();
        templateData.put("testKey02","testData02");

        Assert.assertNotNull(templateService.processTemplate(templateDTO.getContent(),templateData));
    }

}

