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
package com.eurodyn.qlack2.fuse.componentlibrary.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.fuse.componentlibrary.api.DirectoryService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadget;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadgetHasUser;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.util.ConverterUtil;

/**
 * A Stateless Session EJB providing directory services for gadgets. For details
 * regarding the functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA
 */
@Transactional
public class DirectoryServiceImpl implements DirectoryService {
	public static final Logger LOGGER = Logger.getLogger(DirectoryServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-componentlibrary")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<ComponentDTO> listGadgets(boolean includeDisabled) {
		List<ComponentDTO> retVal = new ArrayList<>();
		
		Query q;
		if (includeDisabled) {
			 q = em.createQuery("select g from ApiGadget g order by g.title");
		} else {
			q = em.createQuery("select g from ApiGadget g where g.enabled= true order by g.title");	
		}
			
		for (Iterator<ApiGadget> i = q.getResultList().iterator(); i.hasNext();) {
			ApiGadget a = i.next();
			retVal.add(ConverterUtil.gadgetToComponentDTO(a));
		}
		LOGGER.log(Level.FINEST, "Found {0} gadgets.", retVal.size());

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param searchTerm
	 *            {@inheritDoc}
	 * @param includeDisabled
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<ComponentDTO> searchGadgets(String searchTerm, boolean includeDisabled) {
		List<ComponentDTO> retVal = new ArrayList<>();

		if (StringUtils.isEmpty(searchTerm)) {
			return retVal;
		}

		Query q;
		if (includeDisabled) {
			 q = em.createQuery("select a from ApiGadget a where (a.title like :title or a.description like :description) order by a.title");
		} else {
			q = em.createQuery("select a from ApiGadget a where (a.title like :title or a.description like :description) and a.enabled = :enabled order by a.title");
			q.setParameter("enabled", true);
		}
		q.setParameter("title", "%" + searchTerm + "%");
		q.setParameter("description", "%" + searchTerm + "%");
		for (Iterator<ApiGadget> i = q.getResultList().iterator(); i.hasNext();) {
			ApiGadget a = i.next();
			retVal.add(ConverterUtil.gadgetToComponentDTO(a));
		}
		LOGGER.log(Level.FINEST, "Found {0} gadgets.", retVal.size());

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param userID
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<ComponentDTO> getGadgetsForUserID(String userID) {
		List<ComponentDTO> retVal = new ArrayList<>();

		Query q = em.createQuery("select ghu from ApiGadgetHasUser ghu "
				+ "where ghu.userId = :userID " + "order by ghu.displayOrder");
		q.setParameter("userID", userID);
		for (Iterator<ApiGadgetHasUser> i = q.getResultList().iterator(); i
				.hasNext();) {
			ApiGadgetHasUser a = i.next();
			ComponentDTO ComponentDTO = ConverterUtil.gadgetToComponentDTO(a.getGadget());
			ComponentDTO.setState(a.getState() != null ? a.getState()
					.byteValue() : Byte.valueOf("1"));
			ComponentDTO.setDisplayOrder(a.getDisplayOrder() != null ? a
					.getDisplayOrder().byteValue() : Byte.valueOf("0"));
			ComponentDTO.setUserKey(a.getId());
			retVal.add(ComponentDTO);
		}

		LOGGER.log(Level.FINEST, "Found {0} gadgets.", retVal.size());

		return retVal;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<String> getGadgetIDsForUserID(String userID) {
		List<String> retVal = new ArrayList<>();

		Query q = em.createQuery("select ghu from ApiGadgetHasUser ghu "
				+ "where ghu.userId = :userID " + "order by ghu.displayOrder");
		q.setParameter("userID", userID);
		for (Iterator<ApiGadgetHasUser> i = q.getResultList().iterator(); i
				.hasNext();) {
			ApiGadgetHasUser a = i.next();
			retVal.add(a.getGadget().getId());
		}

		LOGGER.log(Level.FINEST, "Found {0} gadgets.", retVal.size());

		return retVal;
	}
}
