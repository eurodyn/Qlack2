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
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.exception.QException;
import com.eurodyn.qlack2.fuse.blog.api.LayoutService;
import com.eurodyn.qlack2.fuse.blog.api.dto.LayoutDTO;
import com.eurodyn.qlack2.fuse.blog.api.exception.QInvalidLayout;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgLayout;
import com.eurodyn.qlack2.fuse.blog.impl.util.ConverterUtil;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class LayoutServiceImpl implements LayoutService {
	private static final Logger LOGGER = Logger.getLogger(LayoutServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-blog")
	private EntityManager em;
	private static final String blogLayoutNode = "search_layouts";
	private static final String blogRootNode = "blogs/";

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createLayout(LayoutDTO dto) throws QException {
		LOGGER.log(Level.FINEST, "Creating a new layout with title {0} and home {1}",
				new String[] { dto.getName(), dto.getHome() });

		if (isLayoutNameAlreadyPresent(dto.getName()))
			throw new QInvalidLayout("Layout with title " + dto.getName()+ " already exists.");

		BlgLayout entity = new BlgLayout();
		entity.setHome(dto.getHome());
		entity.setName(dto.getName());
		em.persist(entity);

		return entity.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void editLayout(LayoutDTO layout) throws QException {
		LOGGER.log(Level.FINEST, "Editing layout with id {0}", layout.getId());

		BlgLayout layoutEntity = BlgLayout.find(em, layout.getId());
		
		if (layoutEntity == null) {
			return;
		}

		if (isLayoutNameAlreadyPresent(layout.getName())) 
			throw new QInvalidLayout("Layout with title" + layout.getName() + " already present.");
		
		layoutEntity.setName(layout.getName());
		layoutEntity.setHome(layout.getHome());
		em.merge(layoutEntity);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteLayout(String layoutId) throws QException {
		LOGGER.log(Level.FINEST, "Deleting layout with id {0}", layoutId);

		BlgLayout layoutEntity = BlgLayout.find(em, layoutId);
		
		if (layoutEntity == null) {
			return;
		}

		em.remove(layoutEntity);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<LayoutDTO> getLayouts() {
		LOGGER.log(Level.FINEST, "Retrieving all layouts");

		List<LayoutDTO> result = null;
		List<BlgLayout> queryResult = BlgLayout.findAll(em);
		if ((queryResult != null) && (!queryResult.isEmpty())) {
			result = new ArrayList<LayoutDTO>();
			for (BlgLayout layout : queryResult)
				result.add(ConverterUtil.layoutToLayoutDTO(layout));
		}
		return result;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public LayoutDTO getLayout(String layoutId) throws QException {
		LOGGER.log(Level.FINEST, "Retrieving layout with id {0}", layoutId);

		return ConverterUtil.layoutToLayoutDTO(BlgLayout.find(em, layoutId));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public LayoutDTO getLayoutByName(String layoutName) {
		LOGGER.log(Level.FINEST, "Retrieving layout with title {0}", layoutName);

		return ConverterUtil.layoutToLayoutDTO(BlgLayout.findByName(em, layoutName));
	}
	
	private boolean isLayoutNameAlreadyPresent(String name) {
		return BlgLayout.findByName(em, name)!=null ? true : false;
	}
}
