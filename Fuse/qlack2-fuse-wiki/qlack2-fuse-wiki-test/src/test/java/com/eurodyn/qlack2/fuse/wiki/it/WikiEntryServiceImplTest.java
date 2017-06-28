package com.eurodyn.qlack2.fuse.wiki.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.wiki.api.WikiEntryService;
import com.eurodyn.qlack2.fuse.wiki.api.WikiService;
import com.eurodyn.qlack2.fuse.wiki.api.WikiTagService;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryVersionDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiTagDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class WikiEntryServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    WikiEntryService wikiEntryService;

    @Inject
    @Filter(timeout = 1200000)
    WikiService wikiService;

    @Inject
    @Filter(timeout = 1200000)
    WikiTagService wikiTagService;

    @Test
    public void createEntry(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);
    }

    @Test
    public void createEntryWithPageContent(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntryWithPageContent(wikiEntryDTO,"testComment01");
        Assert.assertNotNull(wikiEntryID);
    }

    @Test
    public void wikiHasHomepage(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertTrue(wikiEntryService.wikiHasHomepage(wikiEntryDTO.getWikiId()));
    }

    @Test
    public void removeHomepageFromWiki(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertTrue(wikiEntryService.wikiHasHomepage(wikiEntryDTO.getWikiId()));
        wikiEntryService.removeHomepageFromWiki(wikiEntryDTO.getWikiId());
        Assert.assertFalse(wikiEntryService.wikiHasHomepage(wikiEntryDTO.getWikiId()));
    }

    @Test
    public void editEntry(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        wikiEntryDTO.setTitle("testTitle01");
        wikiEntryService.editEntry(wikiEntryDTO);

        Assert.assertEquals("testTitle01",wikiEntryService.getEntryById(wikiEntryID).getTitle());
    }

    @Test
    public void deleteEntry(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        wikiEntryService.deleteEntry(wikiEntryID);

        Assert.assertNull(wikiEntryService.getEntryById(wikiEntryID));
    }

    @Test
    public void getEntryById(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getEntryById(wikiEntryID));
    }

    @Test
    public void getHomepageForWiki(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getHomepageForWiki(wikiEntryDTO.getWikiId()));
    }

    @Test
    public void getEntryByNamespace(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getEntryByNamespace(wikiEntryDTO.getNamespace()));
    }

    @Test
    public void getRelatedEntriesForTag(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        Assert.assertNotNull(wikiEntryService.getRelatedEntriesForTag(wikiEntryID,wikiTagID.getName()));
    }

    @Test
    public void getRelatedEntries(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getRelatedEntries(wikiEntryID));
    }

    @Test
    public void getLatestEntries(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getLatestEntries(1));
    }

    @Test
    public void getAllEntriesForWiki(){
        PagingParams pagingParams = new PagingParams();
        pagingParams.setPageSize(0);
        pagingParams.setCurrentPage(0);

        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getAllEntriesForWiki(wikiEntryID,pagingParams));
    }

    @Test
    public void getDefaultEntryForWIki(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getDefaultEntryForWIki(wikiEntryDTO.getWikiId()));
    }

    @Test
    public void lockEntry(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        wikiEntryService.lockEntry(wikiEntryID,wikiEntryDTO.getSrcUserId());
        Assert.assertTrue(wikiEntryService.isEntryLocked(wikiEntryID));
    }

    @Test
    public void unlockEntry(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        wikiEntryService.lockEntry(wikiEntryID,wikiEntryDTO.getSrcUserId());
        wikiEntryService.unlockEntry(wikiEntryID);
        Assert.assertFalse(wikiEntryService.isEntryLocked(wikiEntryID));
    }

    @Test
    public void getAllEntriesForTag(){
        PagingParams pagingParams = new PagingParams();
        pagingParams.setPageSize(0);
        pagingParams.setCurrentPage(0);

        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        WikiTagDTO wikiTagDTO = TestUtilities.createWikiTagDTO();
        WikiTagDTO wikiTagID = wikiTagService.createTag(wikiTagDTO);
        Assert.assertNotNull(wikiTagID.getId());

        Assert.assertNotNull(wikiEntryService.getAllEntriesForTag(wikiEntryID,wikiTagID.getId(),pagingParams));
    }

    @Test
    public void getAllVersionsForWikiEntry(){
        PagingParams pagingParams = new PagingParams();
        pagingParams.setPageSize(0);
        pagingParams.setCurrentPage(0);

        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.getAllVersionsForWikiEntry(wikiEntryID,pagingParams));
    }

    @Test
    public void getEntryVersionById(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        wikiEntryService.setCurrentVersionEntry(wikiEntryID,2);
        WikiEntryVersionDTO wikiVersionDTO = wikiEntryService.updateEntryVersion(wikiEntryID,"test",wikiEntryDTO.getPageContent(),wikiEntryDTO.getSrcUserId());

        Assert.assertNotNull(wikiEntryService.getEntryVersionById(wikiVersionDTO.getId()));
    }

    @Test
    public void listAllWikiPages(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        Assert.assertNotNull(wikiEntryService.listAllWikiPages(wikiEntryID));
    }

    @Test
    public void setCurrentVersionEntry(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);

        WikiEntryDTO wikiEntryDTO = TestUtilities.createWikiEntryDTO();
        wikiEntryDTO.setWikiId(wikiID);
        String wikiEntryID = wikiEntryService.createEntry(wikiEntryDTO);
        Assert.assertNotNull(wikiEntryID);

        wikiEntryService.setCurrentVersionEntry(wikiEntryID,2);
        WikiEntryVersionDTO wikiVersionDTO = wikiEntryService.updateEntryVersion(wikiEntryID,"test",wikiEntryDTO.getPageContent(),wikiEntryDTO.getSrcUserId());

        Assert.assertNotNull(wikiEntryService.getEntryVersionById(wikiVersionDTO.getId()));
    }

}

