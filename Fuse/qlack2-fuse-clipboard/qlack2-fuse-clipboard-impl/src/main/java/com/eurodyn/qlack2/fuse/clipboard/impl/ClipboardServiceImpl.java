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
package com.eurodyn.qlack2.fuse.clipboard.impl;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.clipboard.api.ClipboardService;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardEntryDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.dto.ClipboardMetaDTO;
import com.eurodyn.qlack2.fuse.clipboard.api.exception.QClipboardException;
import com.eurodyn.qlack2.fuse.clipboard.api.exception.QMetaDatumNotFound;
import com.eurodyn.qlack2.fuse.clipboard.impl.model.ClbEntry;
import com.eurodyn.qlack2.fuse.clipboard.impl.model.ClbMetadata;
import com.eurodyn.qlack2.fuse.clipboard.impl.util.ConverterUtil;

/**
 * A Stateless Session EJB providing management of a Clipboard. For details
 * regarding the functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA.
 */
@Transactional
public class ClipboardServiceImpl implements ClipboardService {
	private static final Logger LOGGER = Logger
			.getLogger(ClipboardServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-clipboard")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	private ClbEntry findEntry(String entryId) throws QClipboardException {
		ClbEntry entry = em.find(ClbEntry.class, entryId);
		if (entry == null) {
			throw new QClipboardException("A clipboard entry with id "
					+ entryId + " does not exist.");
		}
		return entry;
	}

	private ClbMetadata findMeta(String metaId) throws QClipboardException {
		ClbMetadata meta = em.find(ClbMetadata.class, metaId);
		if (meta == null) {
			throw new QMetaDatumNotFound("A clipboard entry metadatum with id "
					+ metaId + " does not exist.");
		}
		return meta;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public ClipboardEntryDTO createEntry(ClipboardEntryDTO entry) {
		LOGGER.log(Level.FINEST, "Creating new clipboard entry with title {0}",
				entry.getTitle());

		entry.setCreatedOn(System.currentTimeMillis());
		ClbEntry entity = ConverterUtil.convertToEntryEntity(entry);
		em.persist(entity);
		if (entry.getMetadata() != null) {
			for (ClipboardMetaDTO metadatum : entry.getMetadata()) {
				ClbMetadata metaEntity = ConverterUtil
						.convertToMetaEntity(metadatum);
				metaEntity.setClbContentId(entity);
				em.persist(metaEntity);
			}
		}
		entry.setId(entity.getId());

		return entry;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateEntry(ClipboardEntryDTO entry) throws QClipboardException {
		LOGGER.log(Level.FINEST, "Updating clipboard entry with id {0}",
				entry.getId());

		ClbEntry entity = findEntry(entry.getId());
		entity.setDescription(entry.getDescription());
		entity.setTitle(entry.getTitle());
		entity.setTypeId(entry.getTypeId());
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteEntry(ClipboardEntryDTO entry) throws QClipboardException {
		LOGGER.log(Level.FINEST, "Deleting clipboard entry with id {0}",
				entry.getId());

		ClbEntry entity = findEntry(entry.getId());
		em.remove(entity);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteEntries(String[] entryIds) {
		LOGGER.log(Level.FINEST, "Deleting multiple clipboard entries");

		Query query = em
				.createQuery("DELETE FROM ClbEntry e WHERE e.id in (:ids)");
		query.setParameter("ids", Arrays.asList(entryIds));
		query.executeUpdate();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteEntries(String ownerId) {
		LOGGER.log(Level.FINEST, "Deleting entries of owner with id {0}.",
				ownerId);

		Query query = em
				.createQuery("DELETE FROM ClbEntry e WHERE e.ownerId = :ownerId");
		query.setParameter("ownerId", ownerId);
		query.executeUpdate();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public ClipboardEntryDTO getEntry(String id) throws QClipboardException {
		LOGGER.log(Level.FINEST, "Retrieving entry with id {0}", id);

		return ConverterUtil.convertToEntryDTO(findEntry(id));
	}

	private List<ClipboardEntryDTO> retrieveEntries(String ownerId,
			String typeId, PagingParams paging) {
		String queryString = "SELECT e FROM ClbEntry e WHERE e.ownerId = :ownerId";
		if (typeId != null) {
			queryString = queryString.concat(" AND e.typeId = :type");
		}
		Query query = em.createQuery(queryString);
		query.setParameter("ownerId", ownerId);
		if (typeId != null) {
			query.setParameter("type", typeId);
		}
		if (paging != null) {
			query.setFirstResult((paging.getCurrentPage() - 1)
					* paging.getPageSize());
			query.setMaxResults(paging.getPageSize());
		}
		List<ClipboardEntryDTO> cedtos = ConverterUtil
				.convertToEntryDTOList(query.getResultList());

		return cedtos;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<ClipboardEntryDTO> getEntries(String ownerId,
			PagingParams paging) {
		return retrieveEntries(ownerId, null, paging);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<ClipboardEntryDTO> getEntries(String ownerId, String typeId,
			PagingParams paging) {
		return retrieveEntries(ownerId, typeId, paging);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public ClipboardMetaDTO addEntryMeta(String entryId, ClipboardMetaDTO meta)
			throws QClipboardException {
		LOGGER.log(Level.FINEST,
				"Adding metadatum {0} to clipboard entry with id {1}",
				new String[] { meta.getName(), entryId });

		ClbMetadata metaEntity = ConverterUtil.convertToMetaEntity(meta);
		metaEntity.setClbContentId(findEntry(entryId));
		em.persist(metaEntity);
		meta.setId(metaEntity.getId());

		return meta;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateEntryMeta(ClipboardMetaDTO meta)
			throws QClipboardException {
		LOGGER.log(Level.FINEST,
				"Updating cliboard entry metadatum with id {0}", meta.getId());

		ClbMetadata metaEntity = findMeta(meta.getId());
		metaEntity.setMetaName(meta.getName());
		metaEntity.setMetaValue(meta.getValue());
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void removeEntryMeta(ClipboardMetaDTO meta)
			throws QClipboardException {
		LOGGER.log(Level.FINEST,
				"Deleting cliboard entry metadatum with id {0}", meta.getId());

		ClbMetadata metaEntity = findMeta(meta.getId());
		em.remove(metaEntity);
	}

}
