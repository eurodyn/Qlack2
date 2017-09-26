package com.eurodyn.qlack2.fuse.blog.tests;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.blog.api.BlogService;
import com.eurodyn.qlack2.fuse.blog.api.CategoryService;
import com.eurodyn.qlack2.fuse.blog.api.PostService;
import com.eurodyn.qlack2.fuse.blog.api.TagService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCategoryDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogPostDTO;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class PostServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    PostService postService;

    @Inject
    @Filter(timeout = 1200000)
    BlogService blogService;


    @Inject
    @Filter(timeout = 1200000)
    CategoryService categoryService;

    @Inject
    @Filter(timeout = 1200000)
    TagService tagService;

    @Test
    public void createPost(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);
    }

    @Test
    public void editPost(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBody("testBody16");
        blogPostDTO.setBlogId(blogID);
        blogPostDTO.setName("testName16");
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        //edit post
        BlogPostDTO blogPostGetDTO = postService.getPostByName("testName16");
        blogPostGetDTO.setBody("testBody17");

        postService.editPost(blogPostGetDTO);

        Assert.assertEquals("testBody17",postService.getPostByName("testName16").getBody());
    }

    @Test
    public void deletePost(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBody("testBody18");
        blogPostDTO.setBlogId(blogID);
        blogPostDTO.setName("testName18");
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        //expected not null
        Assert.assertNotNull(postService.getPostByName("testName18"));

        postService.deletePost(blogPostID);

        //expected null
        Assert.assertNull(postService.getPostByName("testName18"));
    }

    @Test
    public void archivePost(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBody("testBody19");
        blogPostDTO.setBlogId(blogID);
        blogPostDTO.setName("testName19");
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        postService.archivePost(blogPostID);

        BlogPostDTO blogPostGetDTO = postService.getPostByName("testName19");
        Assert.assertNotNull(blogPostGetDTO);

        Assert.assertTrue(blogPostGetDTO.isArchived());
    }

    @Test
    public void findPost(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBody("testBody20");
        blogPostDTO.setBlogId(blogID);
        blogPostDTO.setName("testName20");
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        Assert.assertNotNull(postService.findPost(blogPostID));
    }

    @Test
    public void getPostsForBlog(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBody("testBody21");
        blogPostDTO.setBlogId(blogID);
        blogPostDTO.setName("testName21");
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        Assert.assertNotNull(postService.getPostsForBlog(blogPostID,false));
    }

    @Test
    public void getPostsForBlogArgs(){
        PagingParams pagingParams = new PagingParams();
        pagingParams.setCurrentPage(0);
        pagingParams.setPageSize(0);

        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBody("testBody22");
        blogPostDTO.setBlogId(blogID);
        blogPostDTO.setName("testName22");
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        Assert.assertNotNull(postService.getPostsForBlog(blogPostID,pagingParams,false));
    }

    @Test
    public void getBlogPostsByCategory(){
        //creates the first blog
        BlogDTO blogOneDTO = TestUtilities.createBlogDTO();
        String blogOneID = blogService.createBlog(blogOneDTO);
        Assert.assertNotNull(blogOneID);

        //creates the first category
        BlogCategoryDTO categoryOneDTO = TestUtilities.createBlogCategoryDTO(blogOneID);
        categoryOneDTO.setBlogId(blogOneID);
        String categoryOneID = categoryService.createCategory(categoryOneDTO);
        Assert.assertNotNull(categoryOneID);

        //creates the first blogPost
        BlogPostDTO blogPostOneDTO = TestUtilities.createBlogPostDTO();
        List<String> blogOneCat = new ArrayList();
        blogOneCat.add(categoryOneID);
        blogPostOneDTO.setBlgCategories(blogOneCat);
        blogPostOneDTO.setBlogId(blogOneID);
        blogPostOneDTO.setDtposted(new Date());
        String blogPostOneID = postService.createPost(blogPostOneDTO);
        Assert.assertNotNull(blogPostOneID);

        //creates the second blog
        BlogDTO blogTwoDTO = TestUtilities.createBlogDTO();
        String blogTwoID = blogService.createBlog(blogTwoDTO);
        Assert.assertNotNull(blogTwoID);

        //creates the second category
        BlogCategoryDTO categoryTwoDTO = TestUtilities.createBlogCategoryDTO(blogTwoID);
        categoryTwoDTO.setBlogId(blogTwoID);
        String categoryTwoID = categoryService.createCategory(categoryTwoDTO);
        Assert.assertNotNull(categoryTwoID);

        //creates the second blogPost
        BlogPostDTO blogPostTwoDTO = TestUtilities.createBlogPostDTO();
        List<String> blogCat = new ArrayList();
        blogCat.add(categoryTwoID);
        blogPostTwoDTO.setBlgCategories(blogCat);
        blogPostTwoDTO.setBlogId(blogTwoID);
        blogPostTwoDTO.setDtposted(new Date());
        String blogPostTwoID = postService.createPost(blogPostTwoDTO);
        Assert.assertNotNull(blogPostTwoID);

        //expect not null
        Assert.assertNotNull(postService.getBlogPostsByCategory(blogOneID,categoryOneID,true));
        Assert.assertNotNull(postService.getBlogPostsByCategory(blogTwoID,categoryTwoID,true));
    }

    @Test
    public void getBlogPostsByTag(){
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

        postService.getBlogPostsByTag(blogID,tagID,true);

        //expect post
        Assert.assertNotNull(postService.getBlogPostsByTag(blogID,tagID,true));
        Assert.assertTrue(postService.getBlogPostsByTag(blogID,tagID,true).size() != 0 );
    }

    @Test
    public void getBlogPostsByDate(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        Date date = new Date();
        blogPostDTO.setDtposted(new Date());
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        long startDate = date.getTime();

        Assert.assertNotNull(postService.getBlogPostsByDate(blogID,startDate,false));
    }

    @Test
    public void getPostByName(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setName("testName24");
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        Assert.assertNotNull(postService.getPostByName("testName24"));
        Assert.assertEquals("testName24",postService.getPostByName("testName24").getName());
    }

    @Test
    public void findBlogPostsForUser(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        blogDTO.setUserId("testUser25");
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        blogPostDTO.setUserId("testUser25");
        blogPostDTO.setBody("testBody25");
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        Assert.assertNotNull(postService.findBlogPostsForUser("testUser25"));
        //expected 1 post
        Assert.assertTrue(postService.findBlogPostsForUser("testUser25") == 1);
    }

}
