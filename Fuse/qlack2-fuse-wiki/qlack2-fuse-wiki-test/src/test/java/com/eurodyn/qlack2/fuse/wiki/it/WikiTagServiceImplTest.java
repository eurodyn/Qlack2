package com.eurodyn.qlack2.fuse.wiki.it;

import com.eurodyn.qlack2.fuse.wiki.api.WikiTagService;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiTagDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class WikiTagServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    WikiTagService wikiTagService;

    @Test
    public void createTag(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());
    }

    @Test
    public void editTag(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        wikiTagDTO.setName("testName01");
        wikiTagService.editTag(wikiTagID);
        Assert.assertNotNull(wikiTagService.getTagName(wikiTagDTO.getId()));
        Assert.assertEquals(wikiTagDTO.getName(),wikiTagService.getTagName(wikiTagDTO.getId()));
    }

    @Test
    public void removeTag(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        wikiTagService.removeTag(wikiTagID.getId());
        //expect that object has been removed
        Assert.assertNull(wikiTagService.findTag(wikiTagID.getId()));
    }

    @Test
    public void findTag(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        Assert.assertNotNull(wikiTagService.findTag(wikiTagID.getId()));
        Assert.assertEquals(wikiTagDTO,wikiTagService.findTag(wikiTagID.getId()));
    }

    @Test
    public void getAllAssociatedEntries(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        Assert.assertNotNull(wikiTagService.getAllAssociatedEntries(wikiTagID.getName()));
    }

    @Test
    public void findAll(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        Assert.assertNotNull(wikiTagService.findAll());
    }

    @Test
    public void getTagName(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        Assert.assertNotNull(wikiTagService.getTagName(wikiTagDTO.getId()));
        Assert.assertEquals(wikiTagDTO.getName(),wikiTagService.getTagName(wikiTagDTO.getId()));
    }

    @Test
    public void findTagByEntryId(){
        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        Assert.assertNotNull(wikiTagService.findTagByEntryId(wikiTagDTO.getId()));
    }

}

