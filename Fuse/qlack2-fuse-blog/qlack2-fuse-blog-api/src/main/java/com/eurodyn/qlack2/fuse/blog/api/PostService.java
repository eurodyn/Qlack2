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
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogPostDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;

/**
 *
 * @author European Dynamics SA.
 */
public interface PostService {

	/**
	 * Creates a post.While creating the new post this method makes sure that
	 * summary,title,categories,post body and BLOG id values are set in DTO. If
	 * required values are missing then exception will be thrown.
	 *
	 * @param dto
	 *            DTO BlogPost DTO contains information for the post to
	 *            persist..
	 * @return
	 * @throws QBlogException
	 */
	String createPost(BlogPostDTO dto) throws QBlogException;

	/**
	 * Edits the post.While editing the new post this method makes sure that
	 * summary,title,categories,post body and BLOG id values are set in DTO. If
	 * required values are missing then exception will be thrown.
	 *
	 * @param dto
	 *            DTO BlogPost DTO contains information for the post to edit.
	 * @throws QBlogException
	 */
	void editPost(BlogPostDTO dto) throws QBlogException;

	/**
	 * Deletes a post
	 *
	 * @param postId
	 *            the id of the post to remove
	 * @throws QBlogException
	 */
	void deletePost(String postId) throws QBlogException;

	/**
	 * Archives the post.
	 *
	 * @param id
	 *            PK of the BLOG post.
	 * @throws QBlogException
	 */
	void archivePost(String id) throws QBlogException;

	/**
	 * Finds the post.
	 *
	 * @param id
	 *            PK of the BLOG post.
	 * @return
	 * @throws QBlogException
	 */
	BlogPostDTO findPost(String id) throws QBlogException;

	/**
	 * Returns the posts of a specific blog
	 *
	 * @param blogId
	 *            The id of the blog whose posts to retrieve
	 * @param includeNotPublished
	 *            A flag determining whether not published posts will also be
	 *            returned
	 * @return A list of blog posts sorted by date descending
	 * @throws QBlogException
	 */
	List<BlogPostDTO> getPostsForBlog(String blogId, boolean includeNotPublished)
			throws QBlogException;

	/**
	 * Returns the posts of a specific blog, using a set of paging parameters
	 *
	 * @param blogId
	 *            The id of the blog whose posts to retrieve
	 * @param pagingParams
	 *            The paging parameters to use
	 * @param includeNotPublished
	 *            A flag determining whether not published posts will also be
	 *            returned
	 * @return A list of blog posts satisfying the specified paging parameters
	 *         sorted by date descending
	 * @throws QBlogException
	 */
	List<BlogPostDTO> getPostsForBlog(String blogId, PagingParams pagingParams,
			boolean includeNotPublished) throws QBlogException;

	/**
	 * Finds the posts of a blog which have been assigned a specific category
	 *
	 * @param blogId
	 *            The id of the blog whose posts to retrieve
	 * @param categoryId
	 *            The id of the category for which to search
	 * @param includeNotPublished
	 *            A flag determining whether not published posts will also be
	 *            returned
	 * @return A list containing the retrieved posts
	 * @throws QBlogException
	 */
	List<BlogPostDTO> getBlogPostsByCategory(String blogId, String categoryId,
			boolean includeNotPublished) throws QBlogException;

	/**
	 * Finds the posts of a blog which have been assigned a specific tag
	 *
	 * @param blogId
	 *            The id of the blog whose tags to retrieve
	 * @param tagId
	 *            The id of the category for which to search
	 * @param includeNotPublished
	 *            A flag determining whether not published posts will also be
	 *            returned
	 * @return A list containing the retrieved posts
	 * @throws QBlogException
	 */
	List<BlogPostDTO> getBlogPostsByTag(String blogId, String tagId,
			boolean includeNotPublished) throws QBlogException;

	/**
	 * Finds the posts of a blog for specified month and year.
	 *
	 * @param blogId
	 *            PK of the blog.
	 * @param startDate
	 *            start date for which posts created.
	 * @param includeNotPublished
	 *            A flag determining whether not published posts will also be
	 *            returned
	 * @return List of posts.
	 */
	List<BlogPostDTO> getBlogPostsByDate(String blogId, long startDate,
			boolean includeNotPublished);
	
	 /**
		 * Retrieves a post with a specific title
		 *
		 * @param name
		 *            The name of the post to retrieve
		 * @return The post with the specified title or null if a post with this
		 *         title does not exist.
		 */
	BlogPostDTO getPostByName(String name);
	
	 /**
     * Finds the amount of blog posts of a specific user
     * @param userId The id of the user whose numbrt of blog posts will be retrieved
     * @return A list of the user's blogs or null if no blogs exist for the specified user.
     * @throws QBlogException if user id  passed is invalid
     */
    long findBlogPostsForUser(String userId) throws QBlogException;
}
