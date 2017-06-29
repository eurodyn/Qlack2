package com.eurodyn.qlack2.fuse.blog.it;

import com.eurodyn.qlack2.fuse.blog.api.LayoutService;
import com.eurodyn.qlack2.fuse.blog.api.dto.LayoutDTO;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.junit.Assert;
import org.junit.Test;
import javax.inject.Inject;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class LayoutServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    LayoutService layoutService;

    @Test
    public void createLayout(){
        LayoutDTO layoutDTO = TestUtilities.createLayoutDTO();
        String layoutID = layoutService.createLayout(layoutDTO);
        Assert.assertNotNull(layoutID);
    }

    @Test
    public void editLayout(){
        LayoutDTO layoutDTO = TestUtilities.createLayoutDTO();
        String layoutID = layoutService.createLayout(layoutDTO);
        Assert.assertNotNull(layoutID);

        LayoutDTO layoutUpdDTO = new LayoutDTO();
        layoutUpdDTO.setName("testName14");
        layoutUpdDTO.setId(layoutID);
        layoutUpdDTO.setHome("testHome14");

        layoutService.editLayout(layoutUpdDTO);

        Assert.assertNotNull(layoutService.getLayout(layoutID));
    }

    @Test
    public void deleteLayout(){
        LayoutDTO layoutDTO = TestUtilities.createLayoutDTO();
        String layoutID = layoutService.createLayout(layoutDTO);
        Assert.assertNotNull(layoutID);

        //expected not null
        Assert.assertNotNull(layoutService.getLayout(layoutID));

        layoutService.deleteLayout(layoutID);

        //expected null
        Assert.assertNull(layoutService.getLayout(layoutID));
    }

    @Test
    public void getLayouts(){
        LayoutDTO layoutOneDTO = TestUtilities.createLayoutDTO();
        String layoutOneID = layoutService.createLayout(layoutOneDTO);
        Assert.assertNotNull(layoutOneID);

        LayoutDTO layoutTwoDTO = TestUtilities.createLayoutDTO();
        String layoutTwoID = layoutService.createLayout(layoutTwoDTO);
        Assert.assertNotNull(layoutTwoID);

        //expected 2 arguments
        Assert.assertTrue(layoutService.getLayouts().size() != 0);
    }

    @Test
    public void getLayout(){
        LayoutDTO layoutDTO = TestUtilities.createLayoutDTO();
        String layoutID = layoutService.createLayout(layoutDTO);
        Assert.assertNotNull(layoutID);

        Assert.assertTrue(layoutService.getLayouts().size() != 0);
        Assert.assertNotNull(layoutService.getLayout(layoutID));
    }

    @Test
    public void getLayoutByName(){
        LayoutDTO layoutDTO = TestUtilities.createLayoutDTO();
        layoutDTO.setName("testName15");
        String layoutName = layoutDTO.getName();
        String layoutID = layoutService.createLayout(layoutDTO);
        Assert.assertNotNull(layoutID);

        Assert.assertTrue(layoutService.getLayouts().size() != 0);
        Assert.assertNotNull(layoutService.getLayoutByName("testName15"));
    }

}
