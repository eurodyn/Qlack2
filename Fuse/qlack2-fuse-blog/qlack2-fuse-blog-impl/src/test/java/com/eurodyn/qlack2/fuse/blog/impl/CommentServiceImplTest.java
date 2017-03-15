/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.blog.impl;

import com.eurodyn.qlack2.fuse.blog.api.CommentService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCommentDTO;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgComment;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgPost;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author European Dynamics SA
 */
public class CommentServiceImplTest {


    private EntityManager em;
    private EntityTransaction tx;
    private CommentService commentService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (!BlogTests.suiteRunning) {
            BlogTests.init();
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (!BlogTests.suiteRunning) {
            BlogTests.tearDownAfterClass();
        }
    }
    @Before
    public void setUp() throws Exception {

        em = BlogTests.getEm();
        commentService = BlogTests.getCommentService();

        EntityTransaction trns = em.getTransaction();
        trns.begin();
        cleanBlogs();
        cleanPosts();
        cleanComments();
        trns.commit();

        if (tx == null) {
            tx = BlogTests.getEm().getTransaction();
        }
        tx.begin();
    }

    private void cleanBlogs() {

        em.createQuery("delete from BlgBlog").executeUpdate();
    }

    private void cleanPosts(){

        em.createQuery("delete from BlgPost").executeUpdate();
    }

    private void cleanComments(){

        em.createQuery("delete from BlgComment").executeUpdate();
    }

    @After
    public void tearDown() throws Exception {

        if (! tx.getRollbackOnly()) {
            tx.commit();
        } else
            tx.rollback();
    }

    @Test
    public void createComment() throws Exception {

        BlogCommentDTO blogCommentDTO = BlogTestUtil.createBlogComment("TestData");

        String commentId = commentService.createComment(blogCommentDTO);

        BlgComment comment = getBlgCommentFromDBById(commentId).get(0);

        assertEquals(comment.getBody(), blogCommentDTO.getBody());
    }



    @Test
    public void editComment() throws Exception {

        BlogCommentDTO blogCommentDTO = BlogTestUtil.createBlogComment("TestData");
        BlgComment blgComment = BlogTestUtil.persistBlogComment(blogCommentDTO);


        BlogCommentDTO modifiedBlogComment = BlogTestUtil.createBlogComment("TestData2");
        modifiedBlogComment.setId(blgComment.getId());

        commentService.editComment(modifiedBlogComment);

        BlgComment modifiedCommentInDB = getBlgCommentFromDBById(blgComment.getId()).get(0);

        assertEquals(modifiedBlogComment.getBody(), modifiedCommentInDB.getBody());


    }

    private List<BlgComment> getBlgCommentFromDBById(String blgCommentId) {

        return em.createQuery("select comment from BlgComment comment where comment.id=:blgCommentId")
                .setParameter("blgCommentId", blgCommentId)
                .getResultList();
    }


    @Test
    public void deleteComment() throws Exception {

        BlogCommentDTO blogCommentDTO = BlogTestUtil.createBlogComment("TestData");
        BlgComment blgComment = BlogTestUtil.persistBlogComment(blogCommentDTO);

        commentService.deleteComment(blgComment.getId());

       List <BlgComment> blgComments = getBlgCommentFromDBByUser(blogCommentDTO.getUserId());

        assertEquals(blgComments.size(),0);

    }

    @Test
    //Potential bug does not get the view comment as expected
    public void viewComment() throws Exception {

        BlogCommentDTO blogCommentDTO = BlogTestUtil.createBlogComment("TestData");
        BlgComment blgComment = BlogTestUtil.persistBlogComment(blogCommentDTO);

        BlogCommentDTO viewedComment = commentService.viewComment(blgComment.getId());

        assertEquals(viewedComment.getBody(), blgComment.getBody());

    }

    @Test
    public void getCommentsForPost() throws Exception {

        BlogCommentDTO blgCommentDTO = BlogTestUtil.createBlogComment("TestData");
        BlgComment blgComment = BlogTestUtil.persistBlogComment(blgCommentDTO);

        BlgPost post = blgComment.getPostId();

        List<BlogCommentDTO> blgComments = commentService.getCommentsForPost(post.getId());

        assertTrue(blgComments.size() > 0);



    }

    private List<BlgComment> getBlgCommentFromDBByUser(String userId){

        return em.createQuery("select comment from BlgComment comment where comment.userId=:userId")
                .setParameter("userId", userId)
                .getResultList();
    }

}