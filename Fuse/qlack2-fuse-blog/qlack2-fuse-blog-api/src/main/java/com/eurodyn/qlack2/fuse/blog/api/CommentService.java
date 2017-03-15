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

import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCommentDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;

/**
 *
 * An interface for EJBs providing services to manage the comment(s).
 *
 * @author European Dynamics SA.
 */
public interface CommentService {

	/**
	 * Create a comment.While creating comment the comment body,post id and user
	 * id are required fields. This method also posts a JMS message (subject to
	 * the value of the realtime.JMS.notifications application property) of type
	 * MSGTYPE_COMMENT_POSTED. The message has the POST_ID, POST_NAME and
	 * COMMENT_ID properties set to the respective values of the new comment and
	 * has its SRC_USERID set to the id of the user posting the comment and its
	 * PRIVATE_USERID set to the id of the user who created the post for which
	 * this comment is posted.
	 *
	 * @param dto
	 * @return DTO BlogCommentDTO DTO with PK.
	 * @throws QBlogException
	 */
	String createComment(BlogCommentDTO dto) throws QBlogException;

	/**
	 * Edits a comment.
	 *
	 * @param dto
	 * @throws QBlogException
	 */
	void editComment(BlogCommentDTO dto) throws QBlogException;

	/**
	 * Deletes a comment.
	 *
	 * @param commentId
	 *            The id of the comment to delete
	 * @throws QBlogException
	 */
	void deleteComment(String commentId) throws QBlogException;

	/**
	 * Views a comment.
	 *
	 * @param id
	 *            the PK of the comment.
	 * @return DTO BlogCommentDTO DTO contains information for the comment.
	 */
	BlogCommentDTO viewComment(String id);

	/**
	 * Get all the comments for post.
	 *
	 * @param postId
	 *            the BLOG post id.
	 * @return list of BlogCommentDTOs which contains information for the
	 *         comments.
	 */
	List<BlogCommentDTO> getCommentsForPost(String postId);

}
