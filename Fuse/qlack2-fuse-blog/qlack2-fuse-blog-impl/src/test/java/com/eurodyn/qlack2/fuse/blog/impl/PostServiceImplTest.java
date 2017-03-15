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

import com.eurodyn.qlack2.fuse.blog.api.PostService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogPostDTO;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgPost;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgTrackbacks;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author European Dynamics SA
 */
public class PostServiceImplTest {

    private EntityManager em;
    private EntityTransaction tx;
    private PostService postService;

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

        postService = BlogTests.getPostService();
        em = BlogTests.getEm();

        EntityTransaction trns = em.getTransaction();
        trns.begin();
        cleanPostTable();
        cleanBlgTable();
        cleanLayoutTable();
        trns.commit();
        if (tx == null){
            tx = em.getTransaction();
        }
        tx.begin();
    }

    private void cleanLayoutTable() {

        em.createQuery("delete  from BlgLayout").executeUpdate();
    }

    private void cleanBlgTable() {

        em.createQuery("delete  from BlgBlog").executeUpdate();
    }

    private void cleanPostTable() {

        em.createQuery("delete  from BlgPost").executeUpdate();
    }

    @After
    public void tearDown() throws Exception {

        if (!tx.getRollbackOnly()){

        tx.commit();
        }
        else
            tx.rollback();
    }

    @Test
    public void createPost() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        String postId = postService.createPost(blogPostDTO);
        BlgPost blgPost = getPostFromDBById(postId).get(0);
        assertEquals(postId, blgPost.getId());
    }

    private List<BlgPost> getPostFromDBByName(String name) {
        return em.createQuery("select post from BlgPost post where post.name=:name")
                .setParameter("name",name)
                .getResultList();
    }

    @Test
    public void editPost() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        BlgPost blgPost = BlogTestUtil.persistPost(blogPostDTO);
        BlogPostDTO postToBeModified = BlogTestUtil.createBlogPostDTO("TestData1");
        postToBeModified.setId(blgPost.getId());
        postService.editPost(postToBeModified);
        BlgPost modifiedPost = getPostFromDBById(blgPost.getId()).get(0);

        assertEquals(postToBeModified.getName(), modifiedPost.getName());
    }

    @Test
    public void deletePost() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        BlgPost post = BlogTestUtil.persistPost(blogPostDTO);
        postService.deletePost(post.getId());
        assertEquals(getPostFromDBByName(blogPostDTO.getName()).size(),0);
    }

    private List<BlgPost> getPostFromDB(String id) {

        return em.createQuery("select post from BlgPost post where post.id=:id")
                .setParameter("id",id)
                .getResultList();
    }

    @Test
    public void archivePost() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        BlgPost post = BlogTestUtil.persistPost(blogPostDTO);
        postService.archivePost(post.getId());
        BlgPost blogPost = getPostFromDB(post.getId()).get(0);
        assertTrue(blogPost.isArchived());
    }

    @Test
    public void findPost() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        BlgPost blgPost = BlogTestUtil.persistPost(blogPostDTO);
        BlogPostDTO postFromDB = postService.findPost(blgPost.getId());
        BlgPost post = getPostFromDBByName(blogPostDTO.getName()).get(0);
        assertEquals(post.getId(), postFromDB.getId());
    }

    @Test
    //Potential bug, exception on application method call
    public void getPostsForBlog() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        BlgPost blgPost = BlogTestUtil.persistPost(blogPostDTO);
        BlgPost post = getPostFromDBById(blgPost.getId()).get(0);
        BlogPostDTO postFromDB = postService.getPostsForBlog(blgPost.getBlogId().getId(),true).get(0);
        assertEquals(post.getId(), postFromDB.getId());
    }

    private List<BlgPost> getPostFromDBById(String id) {

        return em.createQuery("SELECT testpost from BlgPost testpost WHERE testpost.id=:id")
                .setParameter("id",id)
                .getResultList();
    }


    @Test
    public void getBlogPostsByCategory() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        List<String> categoriesList = new ArrayList();
        categoriesList.add("TestCategory1");
        blogPostDTO.setBlgCategories(categoriesList);
        BlgPost createdPost = BlogTestUtil.persistPost(blogPostDTO);
        BlgPost post = getPostFromDBByCategory().get(0);
        assertEquals(post.getName(), blogPostDTO.getName());
        assertEquals(blogPostDTO.getName(), createdPost.getName());
    }

    private List<BlgPost> getPostFromDBByCategory() {

        return em.createQuery("select post from BlgPost post where post.blgPostHasCategories.size=:size")
                .setParameter("size",1)
                .getResultList();
    }

    @Test
    public void getBlogPostsByTag() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        List<String> tagList = new ArrayList();
        tagList.add("TestTag");
        BlgPost blgPost = BlogTestUtil.convertBlogPostDTOToblgPost(blogPostDTO);
        BlogTestUtil.addTagsToPost(blgPost, tagList);
        em.persist(blgPost);
        BlgPost post = getPostFromDBByTag().get(0);
        assertEquals(post.getName(), blogPostDTO.getName());
        assertEquals(post.getName(), blgPost.getName());
    }

    private List<BlgPost> getPostFromDBByTag() {

        return em.createQuery("select post from BlgPost post where post.blgPostHasTags.size=:size")
                .setParameter("size",1)
                .getResultList();
    }

    @Test
    //Potential bug, does not get blogposts by date correctly
    public void getBlogPostsByDate() throws Exception {

        BlogPostDTO dto = BlogTestUtil.createBlogPostDTO("TestData");
        Date currentDate = new Date();
        long currentTimeInMilis = currentDate.getTime();
        dto.setDtposted(currentDate);
        BlgPost blogCreated = BlogTestUtil.persistPost(dto);
        List<BlgPost> postList = getPostsFromDB();
        BlgPost post = postList.get(0);
        List<BlogPostDTO> postDTO = postService.getBlogPostsByDate(blogCreated.getId(), currentTimeInMilis, true);
        assertEquals(1,postDTO.size());
        assertEquals(post.getId(), postDTO.get(0).getId());
    }

    private List<BlgPost> getPostsFromDB() {

        return em.createQuery("select post from BlgPost post")
                .getResultList();
    }

    @Test
    public void getPostByName() throws Exception {

        BlogPostDTO dto = BlogTestUtil.createBlogPostDTO("TestData");
        BlogTestUtil.persistPost(dto);
        BlogPostDTO postDTO = postService.getPostByName("TestData");
        BlgPost postFromDB = getPostFromDBByName("TestData").get(0);
        assertEquals(postDTO.getId(), postFromDB.getId());
    }

    @Test
    public void sendTrackback() throws Exception {

        BlogPostDTO blogPostDTO = BlogTestUtil.createBlogPostDTO("TestData");
        BlogPostDTO blogPostDTOTkbk = BlogTestUtil.createBlogPostDTO("Trackback");
        BlgPost post = BlogTestUtil.convertBlogPostDTOToblgPost(blogPostDTO);
        BlgPost tkbkPost = BlogTestUtil.convertBlogPostDTOToblgPost(blogPostDTOTkbk);
        tkbkPost.setCommentsEnabled(true);
        em.persist(post);
        em.persist(tkbkPost);
        List<String> tkbkIs = new ArrayList<>();
        tkbkIs.add(tkbkPost.getId());
        String postId = post.getId();
        PostServiceImpl impl = (PostServiceImpl) postService;
        List<BlgTrackbacks> tkbks = impl.sendTrackback(tkbkIs, postId, "random", post.getBlogId().getUserId());

        assertNotNull(tkbks);
        assertEquals(1, tkbks.size());
    }

    @Test
    public void findBlogPostsForUser() throws Exception {

        BlogPostDTO dto = BlogTestUtil.createBlogPostDTO("TestData");
        BlogTestUtil.persistPost(dto);
        List<BlgPost> blgPostByUser = getPostFromDBByUser("TestData");
        long numberOfBlogs = postService.findBlogPostsForUser("TestData");
        assertEquals(blgPostByUser.size(), numberOfBlogs);
    }

    private List<BlgPost> getPostFromDBByUser(String userId) {

        return em.createQuery("select post from BlgPost post where post.blogId.userId=:userId")
                .setParameter("userId",userId)
                .getResultList();
    }


}