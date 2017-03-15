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

import com.eurodyn.qlack2.fuse.blog.api.dto.*;
import com.eurodyn.qlack2.fuse.blog.impl.model.*;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.*;

/**
 * @author European Dynamics SA
 */
public class BlogTestUtil {

    private static EntityManager em = BlogTests.getEm();

    public static BlgBlog createAndPersistBlog(String name) {

        BlgBlog blog = createBlog(name);
        em.persist(blog);

        return blog;
    }

    public static BlgBlog createBlog(String name){

        BlgLayout layout = createAndPersistLayout(name+"layout");
        BlgBlog blog = new BlgBlog();

        blog.setName(name);
        blog.setLanguage("TestBlgLanguage");
        blog.setUserId("TestBlogUserId");
        blog.setBlgLayoutId(layout);

        return blog;
    }

    public static BlogDTO createBlogDTO(String testData){

        BlogDTO blogDTO = new BlogDTO();
        BlgLayout layout = createAndPersistLayout(testData);
        blogDTO.setLayoutId(layout.getId());
        blogDTO.setName(testData);
        blogDTO.setIsRss(false);
        blogDTO.setLanguage(testData);
        blogDTO.setUserId(testData);

        return blogDTO;

    }

    private static LayoutDTO createLayoutDTO(String testData) {

        LayoutDTO layoutDTO = new LayoutDTO();
        layoutDTO.setHome(testData);
        layoutDTO.setName(testData);
        return layoutDTO;
    }

    public static BlgLayout createAndPersistLayout(String testData) {

        BlgLayout layout = new BlgLayout();
        layout.setHome("TestHome");
        layout.setName(testData+UUID.randomUUID().toString());
//        layout.setId(id);

        em.persist(layout);
        return layout;
    }

    public static BlgLayout createLayout(String testData) {

        BlgLayout layout = new BlgLayout();
        layout.setHome("TestHome");
        layout.setName(testData);

        return layout;
    }


    public static BlogPostDTO createBlogPostDTO(String testData) {

        BlogPostDTO blogPostDTO = new BlogPostDTO();
        blogPostDTO.setName(testData);
        blogPostDTO.setArchived(false);
        blogPostDTO.setBlgCategories(null);
        blogPostDTO.setBlgTags(null);
        BlgBlog blog = createAndPersistBlog(testData+UUID.randomUUID().toString());
        blogPostDTO.setBlogId(blog.getId());
        blogPostDTO.setBody(testData);

        return blogPostDTO;
    }

    public static BlgPost persistPost(BlogPostDTO blogPostDTO) {

        BlgPost blgPost = new BlgPost();
        blgPost.setId(blogPostDTO.getId());
        blgPost.setBody(blogPostDTO.getName());
        blgPost.setName(blogPostDTO.getName());
        blgPost.setArchived(blogPostDTO.isArchived());


        long datePosted = blogPostDTO.getDtposted() == null ? new Date().getTime() : blogPostDTO.getDtposted().getTime();
        blgPost.setDatePosted(datePosted);
        blgPost.setId(UUID.randomUUID().toString());
        String blogId = blogPostDTO.getBlogId();
        BlgBlog blog = fetchBlogById(blogId);
        blgPost.setBlogId(blog);

        Set<BlgPost> blgPostSet = new HashSet<>();
        blgPostSet.add(blgPost);
        blog.setBlgPosts(blgPostSet);

        List<BlgCategory> blogCategories = new ArrayList<>();

        if(blogPostDTO.getBlgCategories() != null && blogPostDTO.getBlgCategories().size() >0){



            for ( String category : blogPostDTO.getBlgCategories()){

                BlgCategory blgCategory = createAndPersistBlogCategory(category, blog);
                blgCategory.setBlogId(blog);
                List<BlgPost> blogPostList = new ArrayList<>();
                blogPostList.add(blgPost);
                blgCategory.setBlgPostHasCategories(blogPostList);
                blogCategories.add(blgCategory);
            }
        }

        blog.setBlgCategories(new HashSet<>(blogCategories));
        blgPost.setBlgPostHasCategories(blogCategories);
        em.persist(blgPost);
        return blgPost;
    }

    private static BlgBlog fetchBlogById(String blogId) {

        return em.createQuery("select blg from BlgBlog blg where blg.id=:blogId", BlgBlog.class)
                .setParameter("blogId", blogId)
                .getSingleResult();
    }

    private static BlgCategory createAndPersistBlogCategory(String category, BlgBlog blog) {

        BlgCategory blgCategory= new BlgCategory();
        blgCategory.setBlogId(blog);
        blgCategory.setDescription(category);
        blgCategory.setName(category);
        em.persist(blgCategory);
        return blgCategory;
    }

    public static BlogCommentDTO createBlogComment(String testData) {

        BlogCommentDTO blogCommentDTO = new BlogCommentDTO();
        blogCommentDTO.setUserId(testData);
        blogCommentDTO.setBody(testData);
        blogCommentDTO.setDtCommented(new Date());
        BlogPostDTO blgPostDTO = createBlogPostDTO(testData);
        BlgPost blgPost = persistPost(blgPostDTO);
        em.persist(blgPost);
        blogCommentDTO.setPostName(blgPostDTO.getName());
        blogCommentDTO.setPostId(blgPost.getId());

        return blogCommentDTO;
    }

    public static BlgComment persistBlogComment(BlogCommentDTO dto){

        BlgComment blgComment = new BlgComment();

        blgComment.setBody(dto.getBody());
        blgComment.setDateCommented(dto.getDtCommented().getTime());
        blgComment.setPostId(fetchPost(dto.getPostId()));

        blgComment.setUserId(dto.getUserId());
        em.persist(blgComment);
        return blgComment;

    }

    public static BlgPost fetchPost(String postId) {

       return em.createQuery("select blogpost from BlgPost blogpost where blogpost.id=:postId", BlgPost.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    public static BlogCategoryDTO createBlogCategoryDTO(String testData) {

        BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();
        blogCategoryDTO.setDescription(testData);
        blogCategoryDTO.setName(testData);
        blogCategoryDTO.setPosts(5);
        return  blogCategoryDTO;
    }

    public static BlogCategoryDTO modifyBlogCategoryDTO(BlogCategoryDTO blogCategoryDTO) {

        String testData = UUID.randomUUID().toString();
        blogCategoryDTO.setDescription(testData);
        blogCategoryDTO.setName(testData);
        blogCategoryDTO.setPosts(5);
        return  blogCategoryDTO;
    }

    public static BlgCategory persistBlogCategory(BlogCategoryDTO dto){

        BlgCategory blgCategory = new BlgCategory();
        blgCategory.setName(dto.getName());
        blgCategory.setDescription(dto.getDescription());
        BlgBlog blog = createAndPersistBlog(UUID.randomUUID().toString());
        blgCategory.setBlogId(blog);

        em.persist(blgCategory);
        return blgCategory;

    }

    public static List<BlogCategoryDTO> createBlogCategoriesDTOs() {

        List<BlogCategoryDTO> blogCategoryDTOList = new ArrayList<>();
        BlogCategoryDTO first = createBlogCategoryDTO("TestData");
        BlogCategoryDTO second = createBlogCategoryDTO("TestData1");

        BlgBlog firstBlog = BlogTestUtil.createAndPersistBlog("firstBlog");
        BlgBlog secondBlog = BlogTestUtil.createAndPersistBlog("secondBlog");

        first.setBlogId(firstBlog.getId());
        second.setBlogId(secondBlog.getId());

        blogCategoryDTOList.add(first);
        blogCategoryDTOList.add(second);

        return blogCategoryDTOList;
    }


    public static void addTagsToBlog(BlgBlog blogWithTags) {

        BlgPost post = createBlogPost("TestBlogPost");
        Set<BlgPost> blgPosts = new HashSet<>();
        blgPosts.add(post);
        BlgTag blgTag = createBlogTag("TestBlgTag");
        persistToDB(blgTag);
        BlgTag blgTag1 = createBlogTag("TestBlgTag1");
        persistToDB(blgTag1);
        List<BlgTag> blogTags = new ArrayList<>();
        blogTags.add(blgTag);
        blogTags.add(blgTag1);

        post.setBlgPostHasTags(blogTags);

        blogWithTags.setBlgPosts(blgPosts);
    }

    public static<T> void persistToDB(T record) {

        em.persist(record);
    }

    public static BlgPost createBlogPost(String testBlogPost) {

        BlgPost blgPost = new BlgPost();
        blgPost.setName(testBlogPost);

        return blgPost;

    }

    public static void persistBlogPost(BlgPost blgPost){

        em.persist(blgPost);
    }

    public static BlgTag  createBlogTag(String testBlogTag) {

        BlgTag blgTag = new BlgTag();
        blgTag.setName(testBlogTag);
        blgTag.setDescription(testBlogTag);

        return  blgTag;
    }

    public static void persistBlogTag(BlgTag tag){

        em.persist(tag);
    }

    public static void persistBlog(BlgBlog blog) {

        em.persist(blog);
    }

    public static BlgPost convertBlogPostDTOToblgPost(BlogPostDTO blogPostDTO) {

        BlgPost blgPost = new BlgPost();
        blgPost.setBlogId(fetchBlogById(blogPostDTO.getBlogId()));
        blgPost.setDatePosted(Instant.now().getEpochSecond());
        blgPost.setName(blogPostDTO.getName());
        blgPost.setArchived(blogPostDTO.isArchived());
        blgPost.setBody(blogPostDTO.getBody());

        return  blgPost;
    }

    public static void addTagsToPost(BlgPost blgPost, List<String> tagList) {

        List<BlgPost> blgPostList = new ArrayList<>();
        List<BlgTag> blgTagList = new ArrayList<>();
        blgPostList.add(blgPost);

        for (String tag : tagList){

            BlgTag blgTag = createBlogTag(tag);
            blgTag.setBlgPostHasTags(blgPostList);
            blgTagList.add(blgTag);
            em.persist(blgTag);
        }

        blgPost.setBlgPostHasTags(blgTagList);
    }
}
