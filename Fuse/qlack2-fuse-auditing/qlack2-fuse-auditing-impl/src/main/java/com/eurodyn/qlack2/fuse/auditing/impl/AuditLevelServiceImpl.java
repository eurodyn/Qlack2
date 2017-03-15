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
package com.eurodyn.qlack2.fuse.auditing.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.auditing.api.AuditLevelService;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLevelDTO;
import com.eurodyn.qlack2.fuse.auditing.impl.model.AuditLevel;
import com.eurodyn.qlack2.fuse.auditing.impl.util.ConverterUtil;

@Transactional
public class AuditLevelServiceImpl implements AuditLevelService {
	private static final Logger LOGGER = Logger
			.getLogger(AuditLevelServiceImpl.class.getSimpleName());
	// Reference to the persistence context.
	@PersistenceContext(unitName = "fuse-audit")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param level
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String addLevel(AuditLevelDTO level) {
		LOGGER.log(Level.FINER, "Adding custom Audit level ''{0}''.", level);
		AuditLevel alLevel = ConverterUtil.convertToAuditLevelModel(level);
		alLevel.setCreatedOn(System.currentTimeMillis());
		em.persist(alLevel);
		return alLevel.getId();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param levelId
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteLevelById(String levelId) {
		LOGGER.log(Level.FINER, "Deleting Audit level with id ''{0}''.",
				levelId);
		em.remove(em.find(AuditLevel.class, levelId));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param levelName
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteLevelByName(String levelName) {
		LOGGER.log(Level.FINER, "Deleting Audit level with name ''{0}''.",
				levelName);
		em.remove(AuditLevel.findByName(em, levelName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param level
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void updateLevel(AuditLevelDTO level) {
		LOGGER.log(Level.FINER, "Updating Audit level ''{0}'',", level);
		AuditLevel lev = ConverterUtil.convertToAuditLevelModel(level);
		em.merge(lev);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param levelName
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public AuditLevelDTO getAuditLevelByName(String levelName) {
		LOGGER.log(Level.FINER, "Searching Audit level by name ''{0}''.",
				levelName);
		return ConverterUtil.convertToAuditLevelDTO(AuditLevel.findByName(em, levelName));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<AuditLevelDTO> listAuditLevels() {
		LOGGER.log(Level.FINER, "Retrieving all audit levels");
		return ConverterUtil.convertToAuditLevelList(AuditLevel.findAll(em));
	}

}