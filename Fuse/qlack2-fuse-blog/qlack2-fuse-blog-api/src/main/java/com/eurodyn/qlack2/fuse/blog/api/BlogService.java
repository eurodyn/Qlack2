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
package com.eurodyn.qlack2.fuse.blog.api;

import java.util.List;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCategoryDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDashboardDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogTagDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.FlagBlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.LayoutDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;

/**
 * @author European Dynamics SA.
 */
public interface BlogService {

    /**
     * Creates a new Blog. While creating a Blog title is required field.
     * @param dto BlogDTO contains the information for BLOG creation.
     * @throws QBlogException
     */
    String createBlog(BlogDTO dto) throws QBlogException;

    /**
     * Edits the BLOG.While editing the BLOG this method makes sure that
     * title,user id values are set in DTO.
     * If required values are missing then exception will be thrown.
     * @param dto BlogDTO contains the information to edit the BLOG.
     * @throws QBlogException
     */
    void editBlog(BlogDTO dto) throws QBlogException;

    /**
     * Deletes a blog
     * @param blogId the id of the blog to remove
     * @throws QBlogException
     */
    void deleteBlog(String blogId) throws QBlogException;

    /**
     * Views the BLOG.Expects valid BLOG id, otherwise throws exception
     * @param id Primary key(PK) of the BLOG.
     * @return BlogDTO the BLOG
     * @throws QBlogException if id passed is invalid
     */
    BlogDTO viewBlog(String id, boolean includeNotPublished) throws QBlogException;

    /**
     * Finds the blogs of a specific user
     * @param userId The id of the user whose blogs will be retrieved
     * @return A list of the user's blogs or null if no blogs exist for the specified user.
     * @throws QBlogException if user id  passed is invalid
     */
    List<BlogDTO> findBlogsForUser(String userId) throws QBlogException;

    /**
     * Flags a BLOG
     * @param dto FlagBlogDTO contains the information for flagging a BLOG.
     * @throws QBlogException
     */
    void flagBlog(FlagBlogDTO dto) throws QBlogException;

    /**
     * Layouts BLOG.
     * @param dto LayoutDTO contains the information for  BLOG layout.
     * @param blogId Id of the blog to which this layout is assigned
     * @throws QBlogException
     */
    void layoutBlog(LayoutDTO dto, String blogId) throws QBlogException;

    /**
     * Manages RSS feed.
     * @param id Primary key(PK) of the BLOG.
     * @param isRss can be true or false to enable or disable RSS.
     * @throws QBlogException
     */
    void manageRssFeed(String id, boolean isRss) throws QBlogException;

    /**
     * Get all tags used in the BLOG.
     * @param blogId Primary key of the BLOG.
     * @return List of all tags used in that BLOG.
     * @throws QBlogException
     */
    List<BlogTagDTO> getTagsForBlog(String blogId, boolean includeNotPublished)
            throws QBlogException;

    /**
     * Get all categories used in the BLOG.
     * @param blogId Primary key of the BLOG.
     * @return List of all categories used in that BLOG.
     */
    List<BlogCategoryDTO> getCategoriesForBlog(String blogId);

    /**
     * Gets the dashboard data transfer object xml for the user blog.
     * @param userId user id of user
     * @param pagingParams The paging parameters to use
     * @return Dashboard data transfer object xml.
     * @throws QBlogException
     */
    String getDashBoardXml(String userId, PagingParams pagingParams) throws QBlogException;

    /**
     * Gets the dashboard data transfer object for the user blog.
     * @param blogId blog id of user
     * @param pagingParams The paging parameters to use
     * @return Dashboard data transfer object.
     * @throws QBlogException
     */
    BlogDashboardDTO getDashBoard(String blogId, PagingParams pagingParams) throws QBlogException;
    
    /**
	 * Retrieves a blog with a specific title
	 *
	 * @param name
	 *            The name of the blog to retrieve
	 * @return The blog with the specified title or null if a blog with this
	 *         title does not exist.
	 */
    BlogDTO getBlogByName(String name);
}
