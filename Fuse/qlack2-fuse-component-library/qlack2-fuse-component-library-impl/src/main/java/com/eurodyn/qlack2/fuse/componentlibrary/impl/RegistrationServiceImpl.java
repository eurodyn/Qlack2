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

import com.eurodyn.qlack2.fuse.componentlibrary.api.RegistrationService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.dto.ComponentDTO;
import com.eurodyn.qlack2.fuse.componentlibrary.api.exception.QComponentLibraryException;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadget;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadgetHasUser;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

/**
 * A Stateless Session EJB providing registration services for gadgets. For
 * details regarding the functionality offered see the respective interfaces.
 * 
 * @author European Dynamics SA
 */
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
	public static final Logger LOGGER = Logger.getLogger(RegistrationServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-componentlibrary")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param ComponentDTO
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public ComponentDTO registerGadget(ComponentDTO ComponentDTO) {
		ApiGadget g = new ApiGadget();
		g.setTitle(ComponentDTO.getTitle());
		g.setRegisteredOn(System.currentTimeMillis());
		g.setOwner(ComponentDTO.getOwnerUserID());
		g.setEnabled(true);
		g.setBoxLink(ComponentDTO.getBoxLink());
		g.setPrivateKey(UUID.randomUUID().toString());
		ComponentDTO.setPrivateKey(g.getPrivateKey());

		if (ComponentDTO.getAuthor() != null) {
			g.setAuthor(ComponentDTO.getAuthor());
		}
		if (ComponentDTO.getDescription() != null) {
			g.setDescription(ComponentDTO.getDescription());
		}
		if (ComponentDTO.getInfoLink() != null) {
			g.setInfoLink(ComponentDTO.getInfoLink());
		}
		if (ComponentDTO.getPageLink() != null) {
			g.setPageLink(ComponentDTO.getPageLink());
		}
		if (ComponentDTO.getIconLink() != null) {
			g.setIconLink(ComponentDTO.getIconLink());
		}
		if (ComponentDTO.getConfigPage() != null) {
			g.setConfigPage(ComponentDTO.getConfigPage());
		}
		em.persist(g);
		ComponentDTO.setId(g.getId());
		LOGGER.log(Level.FINEST, "Persisted gadget with id {0}.", g.getId());

		return ComponentDTO;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void unregisterGadget(String gadgetID) {
		LOGGER.log(Level.FINEST, "Removing gadget with id {0}.", gadgetID);
		ApiGadget g = (ApiGadget) em.find(ApiGadget.class, gadgetID);

		em.remove(g);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param ComponentDTO
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void updateGadget(ComponentDTO ComponentDTO) {
		ApiGadget g = (ApiGadget) em.find(ApiGadget.class, ComponentDTO.getId());
		LOGGER.log(Level.FINEST, "Updating gadget with id {0}.", ComponentDTO.getId());
		g.setTitle(ComponentDTO.getTitle());
		g.setBoxLink(ComponentDTO.getBoxLink());

		if (ComponentDTO.getAuthor() != null) {
			g.setAuthor(ComponentDTO.getAuthor());
		}
		if (ComponentDTO.getDescription() != null) {
			g.setDescription(ComponentDTO.getDescription());
		}
		if (ComponentDTO.getInfoLink() != null) {
			g.setInfoLink(ComponentDTO.getInfoLink());
		}
		if (ComponentDTO.getPageLink() != null) {
			g.setPageLink(ComponentDTO.getPageLink());
		}
		if (ComponentDTO.getIconLink() != null) {
			g.setIconLink(ComponentDTO.getIconLink());
		}
		if (ComponentDTO.getConfigPage() != null) {
			g.setConfigPage(ComponentDTO.getConfigPage());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void enableGadget(String gadgetID) {
		LOGGER.log(Level.FINEST, "Enabling gadget with id {0}.", gadgetID);
		ApiGadget g = (ApiGadget) em.find(ApiGadget.class, gadgetID);
		g.setEnabled(true);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void disableGadget(String gadgetID) {
		LOGGER.log(Level.FINEST, "Disabling gadget with id {0}.", gadgetID);
		ApiGadget g = (ApiGadget) em.find(ApiGadget.class, gadgetID);
		g.setEnabled(false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean isGadgetEnabled(String gadgetID) {
		LOGGER.log(Level.FINEST, "Checking if gadget with id ''{0}'' is enabled.", gadgetID);
		ApiGadget g = (ApiGadget) em.find(ApiGadget.class, gadgetID);

		return g.isEnabled();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String getGadgetIDFromGadgetUserKey(String gadgetUserKey) {
		String retVal = null;
		ApiGadgetHasUser ghu = em.find(ApiGadgetHasUser.class, gadgetUserKey);
		if (ghu != null) {
			retVal = ghu.getGadget().getId();
		}

		return retVal;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String getUserIDFromGadgetUserKey(String gadgetUserKey) {
		String retVal = null;
		ApiGadgetHasUser ghu = em.find(ApiGadgetHasUser.class, gadgetUserKey);
		if (ghu != null) {
			retVal = ghu.getUserId();
		}

		return retVal;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public boolean isValidSecretKey(String gadgetSecretKey) {
		boolean retVal = false;

		Query q = em.createQuery("select g from ApiGadget g where g.privateKey = :privateKey");
		q.setParameter("privateKey", gadgetSecretKey);
		List l = q.getResultList();
		if ((l != null) && (l.size() > 0)) {
			retVal = true;
		}

		return retVal;
	}

}
