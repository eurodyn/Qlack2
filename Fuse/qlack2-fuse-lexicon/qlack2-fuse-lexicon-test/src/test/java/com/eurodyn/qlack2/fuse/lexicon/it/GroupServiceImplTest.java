package com.eurodyn.qlack2.fuse.lexicon.it;

import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class GroupServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    LanguageService languageService;

    @Inject
    @Filter(timeout = 1200000)
    GroupService groupService;

    @Test
    public void createGroup(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);
    }

    @Test
    public void updateGroup(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        groupDTO.setId(groupID);
        groupDTO.setTitle("testDscr01");
        groupService.updateGroup(groupDTO);
        Assert.assertNotNull("testDscr01",groupService.getGroup(groupID).getTitle());
    }

    @Test
    public void deleteGroup(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        groupService.deleteGroup(groupID);
        Assert.assertNull(groupService.getGroup(groupID));
    }

    @Test
    public void getGroup(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(groupService.getGroup(groupID));
    }

    @Test
    public void getGroupByName(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(groupService.getGroupByName(groupDTO.getTitle()));
    }

    @Test
    public void getRemainingGroups(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        List<String> excludedGroupNames = new ArrayList<>();
        excludedGroupNames.add(groupDTO.getTitle());

        Assert.assertNotNull(groupService.getRemainingGroups(excludedGroupNames));
    }

    @Test
    public void getGroups(){
        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(groupService.getGroups());
        Assert.assertTrue(groupService.getGroups().size() != 0);
    }

    @Test
    public void deleteLanguageTranslations(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        groupService.deleteLanguageTranslations(groupID,languageID);
        Assert.assertNotNull(groupService.getGroups());
        Assert.assertTrue(groupService.getGroups().size() != 0);
    }

    @Test
    public void deleteLanguageTranslationsByLocale(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        groupService.deleteLanguageTranslations(groupID,languageID);
        Assert.assertNotNull(groupService.getGroups());
        Assert.assertTrue(groupService.getGroups().size() != 0);
    }

    @Test
    public void getLastUpdateDateForLocale(){
        LanguageDTO languageDTO = TestUtilities.createLanguageDTO();
        String languageID = languageService.createLanguage(languageDTO);
        Assert.assertNotNull(languageID);

        GroupDTO groupDTO = TestUtilities.createGroupDTO();
        String groupID = groupService.createGroup(groupDTO);
        Assert.assertNotNull(groupID);

        Assert.assertNotNull(groupService.getLastUpdateDateForLocale(groupID,languageDTO.getLocale()));
    }

}

