package com.eurodyn.qlack2.fuse.blog.tests;

import com.eurodyn.qlack2.fuse.blog.api.TagService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogTagDTO;
import com.eurodyn.qlack2.fuse.blog.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.blog.util.TestUtilities;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class TagServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    TagService tagService;

    @Test
    public void createTag(){
        BlogTagDTO blogTagDTO = TestUtilities.createBlogTagDTO();
        String blogTagID = tagService.createTag(blogTagDTO);
        Assert.assertNotNull(blogTagID);
    }

    @Test
    public void deleteTag(){
        BlogTagDTO blogTagDTO = TestUtilities.createBlogTagDTO();
        String blogTagID = tagService.createTag(blogTagDTO);
        Assert.assertNotNull(blogTagID);

        //delete tag
        tagService.deleteTag(blogTagID);

        //check if deleted, expected null
        Assert.assertNull(tagService.findTag(blogTagID));
    }

    @Test
    public void findTag(){
        BlogTagDTO blogTagDTO = TestUtilities.createBlogTagDTO();
        blogTagDTO.setName("testName27");
        String blogTagID = tagService.createTag(blogTagDTO);
        Assert.assertNotNull(blogTagID);

        Assert.assertNotNull(tagService.findTag(blogTagID));
        Assert.assertEquals("testName27",tagService.findTag(blogTagID).getName());
    }

    @Test
    public void findTagByName(){
        BlogTagDTO blogTagDTO = TestUtilities.createBlogTagDTO();
        blogTagDTO.setName("testName28");
        String blogTagID = tagService.createTag(blogTagDTO);
        Assert.assertNotNull(blogTagID);

        Assert.assertEquals("testName28",tagService.findTagByName("testName28").getName());
    }

    @Test
    public void findAllTags(){
        BlogTagDTO blogTagDTO = TestUtilities.createBlogTagDTO();
        String blogTagID = tagService.createTag(blogTagDTO);
        Assert.assertNotNull(blogTagID);

        Assert.assertTrue(tagService.findAllTags().size() != 0);
    }

}
