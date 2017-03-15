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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.blog.api.CommentService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCommentDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogExists;
import com.eurodyn.qlack2.fuse.blog.api.exception.QInvalidComment;
import com.eurodyn.qlack2.fuse.blog.api.exception.QInvalidPost;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgComment;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgLayout;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgPost;
import com.eurodyn.qlack2.fuse.blog.impl.util.ConverterUtil;

/**
 * @author European Dynamics SA.
 */
@Transactional
public class CommentServiceImpl implements CommentService {
	private static final Logger LOGGER = Logger.getLogger(CommentServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-blog")
	private EntityManager em;
	
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createComment(BlogCommentDTO dto) throws QBlogException {
		LOGGER.log(Level.FINE, "Creating comment with name {0}", dto.getPostName());

		BlgComment blgComment = new BlgComment();
		blgComment.setBody(dto.getBody());
		blgComment.setDateCommented(dto.getDtCommented().getTime());
		blgComment.setUserId(dto.getUserId());
		blgComment.setPostId(BlgPost.find(em, dto.getPostId()));
		em.persist(blgComment);

		//TODO Implement notification
		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.Blog.realtime.JMS.notifications").equals("true"))
		// {
		// BlogMessage message = new BlogMessage();
		// message.setType(BlogMessage.MSGTYPE_COMMENT_POSTED);
		// message.setSrcUserID(dto.getUserId());
		// message.setStringProperty(BlogMessage.PRIVATE_USERID,
		// blgComment.getPostId().getBlogId().getUserId());
		// message.setStringProperty(BlogMessage.PROPERTY__POST_ID,
		// blgComment.getPostId().getId());
		// message.setStringProperty(BlogMessage.PROPERTY__POST_TITLE,
		// blgComment.getPostId().getTitle());
		// message.setStringProperty(BlogMessage.PROPERTY__COMMENT_ID,
		// blgComment.getId());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QBlogException(CODES.ERR_BLOG_0013,
		// ex.getLocalizedMessage());
		// }
		// }

		return blgComment.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void editComment(BlogCommentDTO dto) throws QBlogException {
		LOGGER.log(Level.FINE, "Updating comment with id {0}", dto.getId());
		
		BlgComment comment = BlgComment.find(em, dto.getId());
		
		if (comment == null) {
			return;
		}

		comment.setBody(dto.getBody());
		comment.setDateCommented(dto.getDtCommented().getTime());
		comment.setPostId(BlgPost.find(em, dto.getPostId()));
		comment.setUserId(dto.getUserId());

		em.merge(comment);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteComment(String commentId) throws QBlogException {
		LOGGER.log(Level.FINE, "Deleting comment with id {0}", commentId);
		BlgComment comment = BlgComment.find(em, commentId);
		
		if (comment == null) {
			return;
		}

		em.remove(comment);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public BlogCommentDTO viewComment(String id) {
		LOGGER.log(Level.FINE, "Getting comment with id {0}", id);
		return ConverterUtil.commentToCommentDTO(BlgComment.find(em, id));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<BlogCommentDTO> getCommentsForPost(String postId) {
		LOGGER.log(Level.FINE, "Getting comments with postId {0}", postId);
		List<BlgComment> comments = BlgComment.findByPost(em, postId);
		List<BlogCommentDTO> dtoList = new ArrayList<>();
		
		for (BlgComment blgComment : comments) {
			dtoList.add(ConverterUtil.commentToCommentDTO(blgComment));
		}

		return dtoList;
	}

}
