package com.eurodyn.qlack2.fuse.blog.it;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.blog.api.BlogService;
import com.eurodyn.qlack2.fuse.blog.api.TagService;
import com.eurodyn.qlack2.fuse.blog.api.CategoryService;
import com.eurodyn.qlack2.fuse.blog.api.PostService;
import com.eurodyn.qlack2.fuse.blog.api.dto.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class BlogServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    BlogService blogService;

    @Inject
    @Filter(timeout = 1200000)
    TagService tagService;

    @Inject
    @Filter(timeout = 1200000)
    CategoryService categoryService;

    @Inject
    @Filter(timeout = 1200000)
    PostService postService;

    @Test
    public void createBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);
    }

    @Test
    public void editBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        //DTO with new values
        BlogDTO blogUpdDTO = TestUtilities.createBlogDTO();
        blogUpdDTO.setName("testName01");
        blogUpdDTO.setLanguage("testLang01");

        blogService.editBlog(blogUpdDTO);
    }

    @Test
    public void deleteBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        blogDTO.setUserId("testUser01");
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        //check if user:test exist, expected: not null
        Assert.assertNotNull(blogService.findBlogsForUser("testUser01"));

        //delete blog for user:test
        blogService.deleteBlog(blogID);

        //expected null
        Assert.assertNull(blogService.findBlogsForUser("testUser01"));
    }

    @Test
    public void viewBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        Assert.assertNotNull(blogService.viewBlog(blogID,false));
    }

    @Test
    public void findBlogsForUser(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        blogDTO.setUserId("testUser02");
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        //check if user:test exist, expected: not null
        Assert.assertNotNull(blogService.findBlogsForUser("testUser02"));
    }

    @Test
    public void flagBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        FlagBlogDTO flagBlogDTO = new FlagBlogDTO();
        flagBlogDTO.setId(UUID.randomUUID().toString());
        flagBlogDTO.setUserId(UUID.randomUUID().toString());
        flagBlogDTO.setBlogId(blogID);
        flagBlogDTO.setFlagName(TestConst.generateRandomString());

        blogService.flagBlog(flagBlogDTO);

        BlogDTO blogGetDTO = blogService.getBlogByName(blogDTO.getName());
        Assert.assertNotNull(blogGetDTO.getFlags());
    }

    @Test
    public void layoutBlog(){
        LayoutDTO layoutDTO = TestUtilities.createLayoutDTO();

        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        blogDTO.setName("testName02");
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        blogService.layoutBlog(layoutDTO,blogID);

        BlogDTO blogGetDTO = blogService.getBlogByName("testName02");
        Assert.assertNotNull(blogGetDTO.getLayoutId());
    }

    @Test
    public void manageRssFeed(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        blogDTO.setName("testName03");
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        blogService.manageRssFeed(blogID,true);

        BlogDTO blogGetDTO = blogService.getBlogByName("testName03");
        Assert.assertTrue(blogGetDTO.getIsRss());
    }

    @Test
    public void getTagsForBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogTagDTO tagDTO = TestUtilities.createBlogTagDTO();
        tagDTO.setPosts(1);
        String tagID = tagService.createTag(tagDTO);
        Assert.assertNotNull(tagID);

        List<String> blogTagID = new ArrayList();
        blogTagID.add(tagID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlgTags(blogTagID);
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        Assert.assertNotNull(blogService.getTagsForBlog(blogID,false));
        Assert.assertFalse(blogService.getTagsForBlog(blogID,false).isEmpty());
    }

    @Test
    public void getCategoriesForBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogCategoryDTO blogCategoryDTO = TestUtilities.createBlogCategoryDTO(blogID);
        String blogCategoryID = categoryService.createCategory(blogCategoryDTO);
        Assert.assertNotNull(blogCategoryID);

        Assert.assertNotNull(blogService.getCategoriesForBlog(blogID));
        //expect that is not empty
        Assert.assertFalse(blogService.getCategoriesForBlog(blogID).isEmpty());
    }

    @Test
    public void getDashBoardXml(){
        PagingParams pagingParams = new PagingParams();
        pagingParams.setPageSize(0);
        pagingParams.setCurrentPage(0);

        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogDashboardDTO blogDashboardDTO = new BlogDashboardDTO();
        blogDashboardDTO.setId(UUID.randomUUID().toString());
        blogDashboardDTO.setPublishedPosts(1);
        blogDashboardDTO.setAllPosts(2);

        //expected data
        Assert.assertNotNull(blogService.getDashBoardXml(blogID,pagingParams));
    }

    @Test
    public void getDashBoard() {
        PagingParams pagingParams = new PagingParams();
        pagingParams.setPageSize(100);
        pagingParams.setCurrentPage(200);

        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        blogDTO.setName("testName04");
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        //expected data
        Assert.assertNotNull(blogService.getDashBoard(blogID,pagingParams));
    }

    @Test
    public void getBlogByName(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        blogDTO.setName("testName05");
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        Assert.assertNotNull(blogService.getBlogByName("testName05"));
    }

}
