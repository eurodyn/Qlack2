package com.eurodyn.qlack2.fuse.blog.it;

import com.eurodyn.qlack2.fuse.blog.api.dto.*;
import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static BlogDTO createBlogDTO(){
        BlogDTO blogDTO = new BlogDTO();
        String blogID = UUID.randomUUID().toString();
        blogDTO.setId(blogID);
        blogDTO.setName(TestConst.generateRandomString());
        blogDTO.setIsRss(false);
        blogDTO.setLanguage(TestConst.generateRandomString());
        blogDTO.setUserId(UUID.randomUUID().toString());
        blogDTO.setPicture(TestConst.pictureBytes);
        LayoutDTO layoutDTO = createLayoutDTO();
        blogDTO.setLayoutId(layoutDTO.getId());

        return blogDTO;
    }

    public static BlogCategoryDTO createBlogCategoryDTO(String blogID){
        BlogCategoryDTO blogCategoryDTO = new BlogCategoryDTO();
        blogCategoryDTO.setName(TestConst.generateRandomString());
        blogCategoryDTO.setId(UUID.randomUUID().toString());
        blogCategoryDTO.setDescription(TestConst.generateRandomString());
        blogCategoryDTO.setBlogId(blogID);

        return blogCategoryDTO;
    }

    public static LayoutDTO createLayoutDTO() {
        LayoutDTO layoutDTO = new LayoutDTO();
        layoutDTO.setHome(TestConst.generateRandomString());
        layoutDTO.setName(TestConst.generateRandomString());
        layoutDTO.setId(UUID.randomUUID().toString());

        return layoutDTO;
    }

    public static BlogPostDTO createBlogPostDTO() {
        BlogPostDTO blogPostDTO = new BlogPostDTO();
        blogPostDTO.setId(UUID.randomUUID().toString());
        blogPostDTO.setName(TestConst.generateRandomString());
        blogPostDTO.setArchived(false);
        blogPostDTO.setBody(TestConst.generateRandomString());

        return blogPostDTO;
    }

    public static BlogCommentDTO createBlogCommentDTO() {
        BlogCommentDTO blogCommentDTO = new BlogCommentDTO();
        blogCommentDTO.setUserId(UUID.randomUUID().toString());
        blogCommentDTO.setBody(TestConst.generateRandomString());
        blogCommentDTO.setDtCommented(new Date());

        return blogCommentDTO;
    }

    public static BlogTagDTO createBlogTagDTO() {
        BlogTagDTO blogTagDTO = new BlogTagDTO();
        blogTagDTO.setName(UUID.randomUUID().toString());
        blogTagDTO.setDescription(TestConst.generateRandomString());
        blogTagDTO.setPosts(1);

        return  blogTagDTO;
    }

}
