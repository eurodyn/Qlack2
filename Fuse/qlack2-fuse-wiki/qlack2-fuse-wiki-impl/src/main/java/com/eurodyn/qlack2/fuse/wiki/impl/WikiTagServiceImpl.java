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
package com.eurodyn.qlack2.fuse.wiki.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.wiki.api.WikiTagService;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiEntryDTO;
import com.eurodyn.qlack2.fuse.wiki.api.dto.WikiTagDTO;
import com.eurodyn.qlack2.fuse.wiki.api.exception.QInvalidObject;
import com.eurodyn.qlack2.fuse.wiki.api.exception.QMissingFields;
import com.eurodyn.qlack2.fuse.wiki.api.exception.QWikiException;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikEntry;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikEntryHasTag;
import com.eurodyn.qlack2.fuse.wiki.impl.model.WikTag;
import com.eurodyn.qlack2.fuse.wiki.impl.util.ConverterUtil;

/**
 * A Stateless Session EJB providing services to manage a Wiki Tag. For details
 * regarding the functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA
 */
@Transactional
public class WikiTagServiceImpl implements WikiTagService {
	@PersistenceContext(unitName = "fuse-wiki")
	private EntityManager em;
	private static final Logger LOGGER = Logger
			.getLogger(WikiTagServiceImpl.class.getName());
	private static final String wikiTagNode = "search_wiki_tags";

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public WikiTagDTO createTag(WikiTagDTO dto) throws QWikiException {
		if (dto == null) {
			LOGGER.log(Level.SEVERE, "Invalid tag object passed");
			throw new QInvalidObject("Wiki Tag DTO can not be null.");
		}
		if (dto.getName() == null) {
			LOGGER.log(Level.SEVERE, "Wiki Tag DTO missing required fields");
			throw new QMissingFields("Wiki Tag DTO missing required fields");
		}

		if (findTagByName(dto.getName()) == null) {
			WikTag wikTag = ConverterUtil.convertToTagEntity(dto);
			em.persist(wikTag);
			dto.setId(wikTag.getId());
			return dto;
		} else {
			return null;
		}
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void editTag(WikiTagDTO dto) throws QWikiException {
		if (dto == null) {
			LOGGER.log(Level.SEVERE, "Invalid tag object passed");
			throw new QInvalidObject("Wiki Tag DTO can not be null.");
		}

		if (dto.getName() == null) {
			LOGGER.log(Level.SEVERE, "Wiki Tag DTO missing required fields");
			throw new QMissingFields("Wiki Tag DTO missing required fields");
		}
		WikTag tag = em.find(WikTag.class, dto.getId());
		if (tag == null) {
			LOGGER.log(Level.SEVERE, "Tag  object does not exist");
			throw new QInvalidObject("Wiki Tag object does not exist");
		}
		WikTag wikTag = ConverterUtil.convertToTagEntity(dto);
		em.merge(wikTag);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void removeTag(String tagId) throws QWikiException {
		WikTag tag = em.find(WikTag.class, tagId);
		if (tag == null) {
			LOGGER.log(Level.SEVERE, "Tag  object does not exist");
			throw new QInvalidObject("Wiki Tag object does not exist");
		}

		em.remove(em.merge(tag));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public WikiTagDTO findTag(Object id) {
		WikTag tag = em.find(WikTag.class, id);
		return ConverterUtil.convertToWikiTagDTO(tag);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public Set<WikiEntryDTO> getAllAssociatedEntries(String tagName)
			throws QWikiException {
		WikTag tag = findTagByName(tagName);
		Set<WikiEntryDTO> entries = new HashSet<WikiEntryDTO>();
		if (tag != null) {
			Set<WikEntryHasTag> tagHasEntries = tag.getWikEntryHasTags();
			for (WikEntryHasTag tagHasWikiEntry : tagHasEntries) {
				entries.add(ConverterUtil.convertToWikiEntryDTO(tagHasWikiEntry
						.getWikEntryId()));
			}
		} else {
			LOGGER.log(Level.SEVERE, "Wiki tag Does not exist");
			throw new QInvalidObject("Wiki tag Does not exist");
		}

		return entries;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<WikiTagDTO> findTagByEntryId(String entryId)
			throws QWikiException {
		List<WikiTagDTO> list = new ArrayList();
		WikEntry entity = null;

		try {
			entity = em.getReference(WikEntry.class, entryId);
		} catch (EntityNotFoundException enf) {
			LOGGER.log(Level.SEVERE, "Wiki Entry Does not exist");
			throw new QInvalidObject("Wiki Entry Does not exist");
		}

		Query query = em
				.createNativeQuery(
						"SELECT * from wik_tag where id IN"
								+ "(SELECT wik_tag_id from wik_entry_has_tag where wik_entry_id=?1)",
						WikTag.class);
		query.setParameter(1, entryId);
		List listResult = query.getResultList();
		Iterator iter = listResult.iterator();
		while (iter.hasNext()) {
			WikTag tasgEntity = (WikTag) iter.next();
			list.add(ConverterUtil.convertToWikiTagDTO(tasgEntity));
		}
		return list;
	}

	private WikTag findTagByName(String name) {
		WikTag wikTag = null;
		try {
			Query q = em.createQuery("select wt from WikTag wt where wt.name = :name");
			q.setParameter("name", name);
			wikTag = (WikTag) q.getSingleResult();
		} catch (NoResultException nre) {
		}
		return wikTag;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<WikiTagDTO> findAll() {
		List<WikTag> wikiEntityList = null;
		List<WikiTagDTO> dtoList = new ArrayList();
		try {
			Query q = em.createQuery("select wt from WikTag wt");
			wikiEntityList = q.getResultList();
		} catch (NoResultException nre) {
		}
		for (WikTag wikTag : wikiEntityList) {
			dtoList.add(ConverterUtil.convertToWikiTagDTO(wikTag));
		}

		return dtoList;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String getTagName(String tagId) {
		WikTag wikTag = null;
		try {
			Query q = em.createQuery("select wt from WikTag wt where wt.id = :id");
			q.setParameter("id", tagId);
			wikTag =((WikTag) q.getSingleResult());
			//TODO misses check for null WikTag.
		} catch (NoResultException nre) {
			
		}
		return wikTag.getName();
	}

}
