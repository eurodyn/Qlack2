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

import com.eurodyn.qlack2.common.util.exception.QException;
import com.eurodyn.qlack2.fuse.blog.api.TagService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogTagDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogExists;
import com.eurodyn.qlack2.fuse.blog.api.exception.QInvalidTag;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgLayout;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgTag;
import com.eurodyn.qlack2.fuse.blog.impl.util.ConverterUtil;

/**
 * @author European Dynamics SA.
 */
@Transactional
public class TagServiceImpl implements TagService {
	private static final Logger LOGGER = Logger.getLogger(TagServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-blog")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	@Override
	@Transactional(TxType.REQUIRED)
	public String createTag(BlogTagDTO dto) throws QException {
		
		LOGGER.log(Level.FINE, "Creating blog with name {0}", dto.getName());
		
		if (isTagNameAlreadyPresent(dto.getName())) 
			throw new QInvalidTag("Tag with name" + dto.getName() + " already present.");
		
		BlgTag blgTag = new BlgTag();
		blgTag.setName(dto.getName());
		blgTag.setDescription(dto.getDescription());

		em.persist(blgTag);
		return blgTag.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteTag(String tagId) throws QException {
		LOGGER.log(Level.FINE, "Deleting tag with id {0}", tagId);
		BlgTag blgTag = BlgTag.find(em, tagId);
		
		if (blgTag == null) {
			return;
		}
		em.remove(blgTag);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public BlogTagDTO findTag(String tagId) {
		LOGGER.log(Level.FINE, "Finding tag with id {0}", tagId);
		return ConverterUtil.tagToTagDTO(BlgTag.find(em, tagId));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public BlogTagDTO findTagByName(String name) {
		LOGGER.log(Level.FINE, "Finding tag with name {0}", name);
		return ConverterUtil.tagToTagDTO(BlgTag.findByName(em, name));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<BlogTagDTO> findAllTags() {
		LOGGER.log(Level.FINE, "Finding all tags");
		List<BlogTagDTO> dtoList = new ArrayList <BlogTagDTO>();
		for (BlgTag blgTag : (BlgTag.findAll(em))) {
			dtoList.add(ConverterUtil.tagToTagDTO(blgTag));
		}
		return dtoList;
	}
	
	private boolean isTagNameAlreadyPresent(String name) {
		return BlgTag.findByName(em, name)!=null ? true : false;
	}

}
