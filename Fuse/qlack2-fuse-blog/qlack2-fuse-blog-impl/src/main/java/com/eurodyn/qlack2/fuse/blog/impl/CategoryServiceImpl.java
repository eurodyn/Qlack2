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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.blog.api.CategoryService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCategoryDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogException;
import com.eurodyn.qlack2.fuse.blog.api.exception.QCategoryExists;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgCategory;
import com.eurodyn.qlack2.fuse.blog.impl.util.ConverterUtil;

/**
 * @author European Dynamics SA.
 */
@Transactional
public class CategoryServiceImpl implements CategoryService {
	private static final Logger LOGGER = Logger.getLogger(CategoryServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-blog")
	private EntityManager em;
	
	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createCategory(BlogCategoryDTO dto) throws QBlogException {
		LOGGER.log(Level.FINE, "Creating category with name {0}", dto.getName());

		if (categoryExists(dto.getName(), dto.getBlogId())) {
			throw new QCategoryExists(
					"A category with the specified name already exists for this blog");
		}

		BlgCategory blgCategory = new BlgCategory();

		blgCategory.setDescription(dto.getDescription());
		blgCategory.setName(dto.getName());
		blgCategory.setBlogId(BlgBlog.find(em, dto.getBlogId()));
		em.persist(blgCategory);
		return blgCategory.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void createCategories(List<BlogCategoryDTO> categories) throws QBlogException {
		LOGGER.log(Level.FINE, "Creating categories with size {0}", categories.size());
		if (!categories.isEmpty()) {
			for (BlogCategoryDTO dto : categories) {
				createCategory(dto);
			}
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void editCategory(BlogCategoryDTO dto) throws QBlogException {
		LOGGER.log(Level.FINE, "Editing category with name {0}", dto.getName());
		
		BlgCategory blgCategory = BlgCategory.find(em, dto.getId());
		
		if (blgCategory == null) {
			return;
		}

		if (categoryExists(dto.getName(), dto.getBlogId())) {
			throw new QCategoryExists(
					"A category with the specified name already exists for this blog");
		}

		BlgBlog blog = BlgBlog.find(em, dto.getBlogId());
		blgCategory.setBlogId(blog);
		blgCategory.setDescription(dto.getDescription());
		blgCategory.setName(dto.getName());
		em.merge(blgCategory);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void editCategories(List<BlogCategoryDTO> categories) throws QBlogException {
		LOGGER.log(Level.FINE, "Editing categories with size {0}", categories.size());
		if (!categories.isEmpty()) {
			for (BlogCategoryDTO dto : categories) {
				editCategory(dto);
			}
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteCategory(String categoryId) throws QBlogException {
		LOGGER.log(Level.FINE, "Deleting category with id {0}", categoryId);
		BlgCategory blgCategory = BlgCategory.find(em, categoryId);
		
		if (blgCategory == null) {
			return;
		}

		em.remove(blgCategory);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public BlogCategoryDTO findCategory(String id) {
		LOGGER.log(Level.FINE, "Finding category with id {0}", id);
		BlgCategory blgCategory = BlgCategory.find(em, id);
		return ConverterUtil.categoryToCategoryDTO(blgCategory);
	}

	private boolean categoryExists(String name, String blogId) {
		return BlgCategory.findByNameAndBlog(em, name, blogId)!=null ? true : false;
	}

}
