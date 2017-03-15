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
package com.eurodyn.qlack2.fuse.blog.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCategoryDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCommentDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogPostDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogTagDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogTrackbackDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.FlagBlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.LayoutDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;
import com.eurodyn.qlack2.fuse.blog.api.exception.QInvalidBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgCategory;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgComment;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgFlag;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgLayout;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgPost;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgTag;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgTrackbacks;

/**
 * Utility class to convert 1.transfer object to entity 2.entity to transfer
 * object.
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {

	private static final Logger LOGGER = Logger.getLogger(ConverterUtil.class
			.getName());

	private ConverterUtil() {
	}

	/**
	 * Convert from entity to layout DTO.
	 *
	 * @param blgLayout
	 * @return LayoutDTO data transfer object
	 */
	public static LayoutDTO layoutToLayoutDTO(BlgLayout blgLayout) {
		LayoutDTO dto = null;
		if (blgLayout != null) {
			dto = new LayoutDTO();
			dto.setHome(blgLayout.getHome());
			dto.setId(blgLayout.getId());
			dto.setName(blgLayout.getName());
		}
		return dto;
	}

	/**
	 * Convert from entity to Blog Flag DTO.
	 *
	 * @param entity
	 * @return FlagBlogDTO data transfer object
	 */
	public static FlagBlogDTO flagToFlagDTO(BlgFlag entity) {
		if (entity == null) {
			return null;
		}
		FlagBlogDTO dto = new FlagBlogDTO();
		dto.setId(entity.getId());
		dto.setBlogId(entity.getBlogId().getId());
		dto.setUserId(entity.getUserId());
		dto.setFlagDescription(entity.getFlagDescription());
		dto.setFlagName(entity.getFlagName());
		dto.setDateFlagged(entity.getDateFlagged());
		return dto;
	}

	/**
	 * Converts the entity BlgBlog to data transfer object BlogDTO Tags are not
	 * directly part of BLOG entity , so the caller to set them externally.
	 *
	 * @param entity
	 *            BlgBlog entity.
	 * @return BlogDTO Data transfer object, null if entity is null.
	 */
	public static BlogDTO blogToBlogDTO(BlgBlog entity) {

		if (entity == null) {
			return null;
		}
		BlogDTO dto = new BlogDTO();
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setUserId(entity.getUserId());
		dto.setPicture(entity.getPicture());
		dto.setIsRss(entity.getRssFeedEnabled());
		dto.setLanguage(entity.getLanguage());
		dto.setLayoutId(entity.getBlgLayoutId() != null ? entity
				.getBlgLayoutId().getId() : null);

		Set<BlgFlag> blgFlags = entity.getBlgFlags();
		List<FlagBlogDTO> flags = new ArrayList<FlagBlogDTO>();
		for (BlgFlag blgFlag : blgFlags) {
			FlagBlogDTO flagDto = flagToFlagDTO(blgFlag);
			flags.add(flagDto);
		}
		if (!flags.isEmpty()) {
			dto.setFlags(flags);
		}

		Set<BlgCategory> blgCategories = entity.getBlgCategories();
		List<BlogCategoryDTO> categories = new ArrayList<>();

		if (blgCategories != null && !blgCategories.isEmpty()) {
			for (BlgCategory blgCategory : blgCategories) {
				categories.add(categoryToCategoryDTO(blgCategory));
			}
		}
		if (!categories.isEmpty()) {
			dto.setCategories(categories);
		}

		return dto;
	}

	/**
	 * Convert from entity to BLOG Post DTO.
	 *
	 * @param entity
	 * @return BlogPostDTO data transfer object
	 */
	public static BlogPostDTO postToPostDTO(BlgPost entity) {

		if (entity == null) {
			return null;
		}

		BlogPostDTO dto = new BlogPostDTO();
		dto.setArchived(entity.isArchived());

		List<BlgComment> blgComments = entity.getBlgComments();
		List<BlogCommentDTO> comments = new ArrayList();
		if (blgComments != null && !blgComments.isEmpty()) {
			for (BlgComment blgComment : blgComments) {
				comments.add(commentToCommentDTO(blgComment));
			}
		}
		dto.setBlgComments(comments);
		dto.setBlogId(entity.getBlogId().getId());
		dto.setBody(entity.getBody());
		List<BlgCategory> blgCategories = entity.getBlgPostHasCategories();
		List<String> categories = new ArrayList();

		if (blgCategories != null && !blgCategories.isEmpty()) {
			for (BlgCategory blgPostHasCategory : blgCategories) {
				categories.add(blgPostHasCategory.getId());
			}
		}

		dto.setBlgCategories(categories);

		dto.setCommentsEnabled(entity.isCommentsEnabled());
		dto.setDtposted(new java.util.Date(entity.getDatePosted()));
		dto.setId(entity.getId());

		List<BlgTag> blgTags = entity.getBlgPostHasTags();
		List<String> tags = new ArrayList();
		if (blgTags != null && !blgTags.isEmpty()) {
			for (BlgTag blgPostHasTag : blgTags) {
				tags.add(blgPostHasTag.getId());
			}
		}

		dto.setBlgTags(tags);
		dto.setName(entity.getName());
		dto.setPublished(entity.isPublished());
		dto.setTrackbackPingUrl(entity.getTrackbackPingUrl());

		return dto;
	}

	/**
	 * Convert from entity to BLOG Post DTO.
	 *
	 * @param entity
	 * @return BlogPostDTO data transfer object
	 */
	public static BlogPostDTO postToPublishPostDTO(BlgPost entity) {

		if (entity == null) {
			return null;
		}

		BlogPostDTO dto = new BlogPostDTO();
		dto.setArchived(entity.isArchived());

		List<BlgComment> blgComments = entity.getBlgComments();
		List<BlogCommentDTO> comments = new ArrayList();
		if (blgComments != null && !blgComments.isEmpty()) {
			for (BlgComment blgComment : blgComments) {
				comments.add(commentToCommentDTO(blgComment));
			}
		}
		dto.setBlgComments(comments);
		dto.setBlogId(entity.getBlogId().getId());
		dto.setBody(entity.getBody());
		List<BlgCategory> blgCategories = entity
				.getBlgPostHasCategories();
		List<String> categories = new ArrayList();

		if (blgCategories != null && !blgCategories.isEmpty()) {
			for (BlgCategory blgPostHasCategory : blgCategories) {
				categories.add(blgPostHasCategory.getId());
			}
		}

		dto.setBlgCategories(categories);

		dto.setCommentsEnabled(entity.isCommentsEnabled());
		dto.setDtposted(new java.util.Date(entity.getDatePosted()));
		dto.setId(entity.getId());

		List<BlgTag> blgTags = entity.getBlgPostHasTags();
		List<String> tags = new ArrayList();
		if (blgTags != null && !blgTags.isEmpty()) {
			for (BlgTag blgPostHasTag : blgTags) {
				tags.add(blgPostHasTag.getId());
			}
		}

		dto.setBlgTags(tags);
		dto.setName(entity.getName());
		dto.setPublished(entity.isPublished());
		dto.setTrackbackPingUrl(entity.getTrackbackPingUrl());

		return dto;
	}

	/**
	 * Convert from BlgComment entity to BLOG comment DTO.
	 *
	 * @param entity
	 * @return BlogCommentDTO Comment data transfer object
	 */
	public static BlogCommentDTO commentToCommentDTO(BlgComment entity) {

		if (entity == null) {
			return null;
		}

		BlogCommentDTO dto = new BlogCommentDTO();
		dto.setBody(entity.getBody());
		dto.setDtCommented(new java.util.Date(entity.getDateCommented()));
		dto.setId(entity.getId());
		dto.setPostId(entity.getPostId().getId());
		dto.setPostName(entity.getPostId().getName());
		dto.setUserId(entity.getUserId());

		if (!entity.getBlgTrackbackses().isEmpty()) {
			LOGGER.log(Level.FINE, "entity.getBlgTrackbackses() :::{0}"
					+ entity.getBlgTrackbackses());
			Set<BlgTrackbacks> BlgTrackbacksSet = entity.getBlgTrackbackses();
			List<BlogTrackbackDTO> trackbacks = new ArrayList(
					BlgTrackbacksSet.size());

			for (BlgTrackbacks blgTrackbacks : BlgTrackbacksSet) {
				trackbacks.add(trackbackToTrackbackDTO(blgTrackbacks));
				LOGGER.log(Level.FINE, "entity.getBlgTrackbackses() {0}",
						blgTrackbacks.getId());
			}
			dto.setTrackbackUrls(trackbacks);
		}

		return dto;
	}

	/**
	 * Converts to track-back data transfer object.
	 *
	 * @param entity
	 *            BlgTrackbacks entity
	 * @return track-back data transfer object.
	 */
	public static BlogTrackbackDTO trackbackToTrackbackDTO(BlgTrackbacks entity) {
		BlogTrackbackDTO dto = new BlogTrackbackDTO();

		dto.setBlgCommentId(entity.getBlgCommentId().getId());
		dto.setId(entity.getId());
		dto.setPostId(entity.getPostId().getId());
		dto.setTrackbackPostId(entity.getTrackbackPostId().getId());
		dto.setTrackbackPingUrl(entity.getPostId().getTrackbackPingUrl());
		dto.setPostName(entity.getPostId().getName());
		LOGGER.log(Level.FINE, "entity.getPostId().getName() {0}", entity
				.getPostId().getName());

		return dto;
	}

	/**
	 * Convert from BlgCategory entity to BLOG category DTO.
	 *
	 * @param entity
	 * @return BlogCategoryDTO Category data transfer object
	 */
	public static BlogCategoryDTO categoryToCategoryDTO(BlgCategory entity) {

		if (entity == null) {
			return null;
		}

		BlogCategoryDTO dto = new BlogCategoryDTO();
		dto.setDescription(entity.getDescription());
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setBlogId(entity.getBlogId().getId());
		dto.setPosts(entity.getBlgPostHasCategories().size());

		return dto;
	}

	/**
	 * Convert from BlgTag entity to BLOG tag DTO.
	 *
	 * @param entity
	 * @return BlogTagDTO Tag data transfer object
	 */
	public static BlogTagDTO tagToTagDTO(BlgTag entity) {

		if (entity == null) {
			return null;
		}

		BlogTagDTO dto = new BlogTagDTO();
		dto.setDescription(entity.getDescription());
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setPosts(entity.getBlgPostHasTags().size());
		return dto;
	}

	/**
	 * Convert from BlgTag entity to BLOG tag DTO.
	 *
	 * @param entity
	 * @param posts
	 *            number of posts for a tag.
	 * @return BlogTagDTO Tag data transfer object
	 */
	public static BlogTagDTO tagToTagDTOWithPosts(BlgTag entity, long posts) {

		if (entity == null) {
			return null;
		}

		BlogTagDTO dto = new BlogTagDTO();
		dto.setDescription(entity.getDescription());
		dto.setId(entity.getId());
		dto.setName(entity.getName());
		dto.setPosts(posts);
		return dto;
	}
}
