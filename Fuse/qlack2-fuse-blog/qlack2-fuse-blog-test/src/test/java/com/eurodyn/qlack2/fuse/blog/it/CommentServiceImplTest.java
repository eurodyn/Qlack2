package com.eurodyn.qlack2.fuse.blog.it;

import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCommentDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogPostDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDTO;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import com.eurodyn.qlack2.fuse.blog.api.CommentService;
import com.eurodyn.qlack2.fuse.blog.api.BlogService;
import com.eurodyn.qlack2.fuse.blog.api.PostService;
import javax.inject.Inject;
import org.junit.Test;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class CommentServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    CommentService commentService;

    @Inject
    @Filter(timeout = 1200000)
    PostService postService;

    @Inject
    @Filter(timeout = 1200000)
    BlogService blogService;

    @Test
    public void createComment(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        BlogCommentDTO blogCommentDTO = TestUtilities.createBlogCommentDTO();
        blogCommentDTO.setPostId(blogPostID);
        String blogCommentID = commentService.createComment(blogCommentDTO);
        Assert.assertNotNull(blogCommentID);
    }

    @Test
    public void editComment(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        BlogCommentDTO blogCommentDTO = TestUtilities.createBlogCommentDTO();
        blogCommentDTO.setPostId(blogPostID);
        blogCommentDTO.setPostName("testPost09");
        blogCommentDTO.setBody("testBody09");
        String blogCommentID = commentService.createComment(blogCommentDTO);
        Assert.assertNotNull(blogCommentID);

        //new DTO to update Comment
        BlogCommentDTO blogCommentUpdDTO = blogCommentDTO;
        blogCommentUpdDTO.setBody("testBody10");
        blogCommentUpdDTO.setId(blogCommentID);

        commentService.editComment(blogCommentUpdDTO);

        Assert.assertEquals("testBody10",commentService.viewComment(blogCommentID).getBody());
    }

    @Test
    public void deleteComment(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        BlogCommentDTO blogCommentDTO = TestUtilities.createBlogCommentDTO();
        blogCommentDTO.setPostId(blogPostID);
        blogCommentDTO.setPostName("testPost11");
        blogCommentDTO.setBody("testBody11");
        String blogCommentID = commentService.createComment(blogCommentDTO);
        Assert.assertNotNull(blogCommentID);

        commentService.deleteComment(blogCommentID);

        Assert.assertNull(commentService.viewComment(blogCommentID));
    }

    @Test
    public void viewComment(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        BlogCommentDTO blogCommentDTO = TestUtilities.createBlogCommentDTO();
        blogCommentDTO.setPostId(blogPostID);
        blogCommentDTO.setPostName("testPost12");
        blogCommentDTO.setBody("testBody12");
        String blogCommentID = commentService.createComment(blogCommentDTO);
        Assert.assertNotNull(blogCommentID);

        Assert.assertNotNull(commentService.viewComment(blogCommentID));
    }

    @Test
    public void getCommentsForPost(){
        BlogDTO blogDTO = TestUtilities.createBlogDTO();
        String blogID = blogService.createBlog(blogDTO);
        Assert.assertNotNull(blogID);

        BlogPostDTO blogPostDTO = TestUtilities.createBlogPostDTO();
        blogPostDTO.setBlogId(blogID);
        String blogPostID = postService.createPost(blogPostDTO);
        Assert.assertNotNull(blogPostID);

        BlogCommentDTO blogCommentDTO = TestUtilities.createBlogCommentDTO();
        blogCommentDTO.setPostId(blogPostID);
        blogCommentDTO.setPostName("testPost13");
        blogCommentDTO.setBody("testBody13");
        String blogCommentID = commentService.createComment(blogCommentDTO);
        Assert.assertNotNull(blogCommentID);

        Assert.assertNotNull(commentService.getCommentsForPost(blogPostID));
    }

}