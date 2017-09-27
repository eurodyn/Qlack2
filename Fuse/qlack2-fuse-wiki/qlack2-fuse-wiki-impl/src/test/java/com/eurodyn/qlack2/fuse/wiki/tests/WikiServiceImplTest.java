package com.eurodyn.qlack2.fuse.wiki.tests;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.wiki.api.WikiService;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiDTO;
import com.eurodyn.qlack2.fuse.wiki.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.wiki.util.TestUtilities;
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
public class WikiServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    WikiService wikiService;

    @Test
    public void createWiki(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWiki(wikiDTO);
        Assert.assertNotNull(wikiID);
    }

    @Test
    public void createWikiWithId(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWikiWithId(wikiDTO);
        Assert.assertNotNull(wikiID);
    }

    @Test
    public void editWiki(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWikiWithId(wikiDTO);
        Assert.assertNotNull(wikiID);

        wikiDTO.setName("testName02");
        wikiService.editWiki(wikiDTO);
        Assert.assertNotNull(wikiService.getWikiName(wikiDTO.getId()));
        Assert.assertEquals(wikiDTO.getName(),wikiService.getWikiName(wikiDTO.getId()));
    }

    @Test
    public void deleteWiki(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWikiWithId(wikiDTO);
        Assert.assertNotNull(wikiID);

        wikiService.deleteWiki(wikiID);
        Assert.assertNull(wikiService.findWikiByName(wikiDTO.getName()));
    }

    @Test
    public void getAllWikis(){
        PagingParams pagingParams = new PagingParams();
        pagingParams.setPageSize(0);
        pagingParams.setCurrentPage(0);

        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWikiWithId(wikiDTO);
        Assert.assertNotNull(wikiID);

        wikiService.deleteWiki(wikiID);
        Assert.assertNotNull(wikiService.getAllWikis(pagingParams));
    }

    @Test
    public void getWikiName(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWikiWithId(wikiDTO);
        Assert.assertNotNull(wikiID);

        Assert.assertNotNull(wikiService.getWikiName(wikiDTO.getId()));
    }

    @Test
    public void findWikiByName(){
        WikiDTO wikiDTO = TestUtilities.createWikiDTO();
        String wikiID = wikiService.createWikiWithId(wikiDTO);
        Assert.assertNotNull(wikiID);

        Assert.assertNotNull(wikiService.findWikiByName(wikiDTO.getName()));
    }

}

