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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.blog.api.BlogService;
import com.eurodyn.qlack2.fuse.blog.api.PostService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCategoryDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCommentDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogDashboardDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogPostDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogTagDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.FlagBlogDTO;
import com.eurodyn.qlack2.fuse.blog.api.dto.LayoutDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogExists;
import com.eurodyn.qlack2.fuse.blog.api.exception.QJAXBError;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgCategory;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgComment;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgFlag;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgLayout;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgPost;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgTag;
import com.eurodyn.qlack2.fuse.blog.impl.util.ConverterUtil;

/**
 * @author European Dynamics SA
 */
@Transactional
public class BlogServiceImpl implements BlogService {
	private static final Logger LOGGER = Logger.getLogger(BlogServiceImpl.class.getName());
	private PostService postService;
	@PersistenceContext(unitName = "fuse-blog")
	private EntityManager em;
	
	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createBlog(BlogDTO dto) throws QBlogException {
		LOGGER.log(Level.FINE, "Creating blog with name {0}", dto.getName());
		
		if (isBlogNameAlreadyPresent(dto.getName())) 
			throw new QBlogExists("Blog with title" + dto.getName() + " already present.");
		
		BlgLayout blgLayout = BlgLayout.find(em, dto.getLayoutId());	
		BlgBlog blog = new BlgBlog();
		blog.setName(dto.getName());
		blog.setLanguage(dto.getLanguage());
		blog.setRssFeedEnabled(dto.getIsRss());
		blog.setUserId(dto.getUserId());
		blog.setBlgLayoutId(blgLayout);
		em.persist(blog);

		return blog.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void editBlog(BlogDTO dto) throws QBlogException {
		LOGGER.log(Level.FINE, "Updating blog with name {0}", dto.getName());
		
		BlgBlog blog = BlgBlog.find(em, dto.getId());
		
		if (blog == null) {
			return;
		}

		if (isBlogNameAlreadyPresent(dto.getName())) 
			throw new QBlogExists("Blog with title" + dto.getName() + " already present.");

		blog.setName(dto.getName());
		blog.setLanguage(dto.getLanguage());
		blog.setRssFeedEnabled(dto.getIsRss());
		blog.setUserId(dto.getUserId());
		blog.setPicture(dto.getPicture());
		BlgLayout blgLayout = BlgLayout.find(em, dto.getLayoutId());	
		blog.setBlgLayoutId(blgLayout);

		em.merge(blog);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteBlog(String blogId) throws QBlogException {
		LOGGER.log(Level.FINE, "Deleting blog with id {0}", blogId);
		BlgBlog blog = BlgBlog.find(em, blogId);
		
		if (blog == null) {
			return;
		}

		em.remove(blog);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public BlogDTO viewBlog(String id, boolean includeNotPublished) throws QBlogException {
		LOGGER.log(Level.FINE, "Viewing blog with id {0}", id);
		BlgBlog blog = BlgBlog.find(em, id);
		
		if (blog == null) {
			return null;
		}
		
		BlogDTO dto = ConverterUtil.blogToBlogDTO(blog);
//		dto.setTags(getTagsForBlog(id, includeNotPublished));
		return dto;
	}

	@Override
	public List<BlogDTO> findBlogsForUser(String userId) throws QBlogException {
		LOGGER.log(Level.FINEST, "Finding blogs of user with id {0}", userId);
		List<BlogDTO> result = null;
		List<BlgBlog> queryResult = BlgBlog.findByUser(em, userId);
		if ((queryResult != null) && (queryResult.size() > 0)) {
			result = new ArrayList<BlogDTO>();
			for (BlgBlog blog : queryResult) {
				BlogDTO dto = ConverterUtil.blogToBlogDTO(blog);
//				// Tags are not directly part of blog entity, so we need to set them externally.
//				dto.setTags(getTagsForBlog(blog.getId(), true));
				result.add(dto);
			}
		}

		return result;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void flagBlog(FlagBlogDTO dto) throws QBlogException {
		LOGGER.log(Level.FINEST, "Flagging blog with id {0}", dto.getBlogId());
		BlgBlog blog = BlgBlog.find(em, dto.getBlogId());
		
		if (blog == null) {
			return;
		}
		
		BlgFlag flag = new BlgFlag();
		flag.setBlogId(blog);
		flag.setUserId(dto.getUserId());
		flag.setFlagDescription(dto.getFlagDescription());
		flag.setFlagName(dto.getFlagName());
		if (dto.getDateFlagged() != null) 
			flag.setDateFlagged(dto.getDateFlagged().getTime());
		else 
			flag.setDateFlagged((new Date()).getTime());
		blog.getBlgFlags().add(flag);
		em.persist(flag);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void layoutBlog(LayoutDTO dto, String blogId) throws QBlogException {
		LOGGER.log(Level.FINEST, "layoutBlog with id {0}", blogId);
		BlgBlog blog = BlgBlog.find(em, blogId);
		
		if (blog == null) {
			return;
		}
		
		BlgLayout blgLayout = BlgLayout.find(em, dto.getId());
		
		if (blgLayout == null) {
			blgLayout = new BlgLayout();
			blgLayout.setName(dto.getName());
			blgLayout.setHome(dto.getHome());
			blgLayout.getBlgBlogs().add(blog);

			em.persist(blgLayout);
		}
		
		blog.setBlgLayoutId(blgLayout);
		em.merge(blog);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void manageRssFeed(String id, boolean isRss) throws QBlogException {
		LOGGER.log(Level.FINEST, "manageRssFeed with id {0}", id);
		BlgBlog blog = BlgBlog.find(em, id);
		
		if (blog == null) {
			return;
		}

		blog.setRssFeedEnabled(isRss);
		em.merge(blog);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<BlogTagDTO> getTagsForBlog(String blogId, boolean includeNotPublished) throws QBlogException {
		LOGGER.log(Level.FINEST, "getTagsForBlog with id {0}", blogId);
		List<BlogTagDTO> tags = new ArrayList<>();
		List<BlgTag> blgTags = BlgTag.findByBlog(em, blogId);
		for (BlgTag blgTag : blgTags) {
			// get the number of posts for the tag.
			List<BlogPostDTO> posts = postService.getBlogPostsByTag(blogId, blgTag.getId(), includeNotPublished);
			tags.add(ConverterUtil.tagToTagDTOWithPosts(blgTag, posts.size()));
		}
		return tags;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<BlogCategoryDTO> getCategoriesForBlog(String blogId) {
		LOGGER.log(Level.FINEST, "getCategoriesForBlog with id {0}", blogId);
		List<BlogCategoryDTO> categories = new ArrayList<>();
		List<BlgCategory> blgCategories = BlgCategory.findByBlog(em, blogId);
		for (BlgCategory blgCategory : blgCategories) {
			categories.add(ConverterUtil.categoryToCategoryDTO(blgCategory));
		}

		return categories;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String getDashBoardXml(String blogId, PagingParams pagingParams) throws QBlogException {
		LOGGER.log(Level.FINEST, "getDashBoardXml with id {0}", blogId);
		StringWriter xml = new StringWriter();

		try {
			BlogDashboardDTO dto = getDashBoard(blogId, pagingParams);
			JAXBContext context;
			context = JAXBContext.newInstance(BlogDashboardDTO.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(dto, xml);
			xml.flush();
		} catch (JAXBException ex) {
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QJAXBError(ex.getLocalizedMessage());
		}

		return xml.toString();
	}
	
	@Override
	@Transactional(TxType.REQUIRED)
	public BlogDTO getBlogByName(String name) {
		LOGGER.log(Level.FINEST, "Retrieving blog with name {0}", name);

		return ConverterUtil.blogToBlogDTO(BlgBlog.findByName(em, name));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public BlogDashboardDTO getDashBoard(String blogId, PagingParams pagingParams) throws QBlogException {
		LOGGER.log(Level.FINEST, "getDashBoard with id {0}", blogId);
		BlgBlog blgBlog = BlgBlog.find(em, blogId);
		
		if (blgBlog == null) {
			return null;
		}
		BlogDashboardDTO dashBoardDTO = new BlogDashboardDTO();

		List<BlogPostDTO> allPosts = new ArrayList<>();
		List<BlogPostDTO> publishedPosts = new ArrayList<>();
		List<BlogPostDTO> unPublishedPosts = new ArrayList<>();
		List<BlogPostDTO> archivedPosts = new ArrayList<>();

		allPosts = postService.getPostsForBlog(blgBlog.getId(), true);
		// get comments for the recent posts

		dashBoardDTO.setAllPosts(allPosts.size());

		for (BlogPostDTO post : allPosts) {
			if (post.isArchived()) {
				archivedPosts.add(post);
			}
			if (post.isPublished()) {
				publishedPosts.add(post);
			} else {
				unPublishedPosts.add(post);
			}
		}
		dashBoardDTO.setArchivedPosts(archivedPosts.size());
		dashBoardDTO.setPublishedPosts(publishedPosts.size());
		dashBoardDTO.setUnpublishedPosts(unPublishedPosts.size());
		dashBoardDTO.setMostCommentedPosts(getUsersMostCommentedPosts(blogId, pagingParams));
		dashBoardDTO.setRecentComments(getRecentComments(blogId, pagingParams));

		return dashBoardDTO;
	}

	private List<BlogPostDTO> getUsersMostCommentedPosts(String blogId, PagingParams pagingParams) {
		List<BlogPostDTO> mostCommentedPosts = new ArrayList<>();
		List<BlgPost> posts = BlgPost.findByBlog(em, blogId, pagingParams);
		for (BlgPost post : posts)
			mostCommentedPosts.add(ConverterUtil.postToPostDTO(post));
		return mostCommentedPosts;
	}

	private List<BlogCommentDTO> getRecentComments(String blogId, PagingParams pagingParams) {
		List<BlgComment> comments = BlgComment.findByBlog(em, blogId, pagingParams);

		List<BlogCommentDTO> mostCommentedPosts = new ArrayList<>();
		for (BlgComment comment : comments)
			mostCommentedPosts.add(ConverterUtil.commentToCommentDTO(comment));
		return mostCommentedPosts;
	}

	private boolean isBlogNameAlreadyPresent(String name) {
		return BlgBlog.findByName(em, name)!=null ? true : false;
	}
}
