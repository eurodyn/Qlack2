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
package com.eurodyn.qlack2.fuse.simm.impl;

import static com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants.GROUP_STATUS_APPROVED;
import static com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants.GROUP_STATUS_SUSPENDED;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.SocialGroupService;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupAttributeDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QNotUnique;
import com.eurodyn.qlack2.fuse.simm.api.exception.QNullLookupEntry;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;
import com.eurodyn.qlack2.fuse.simm.impl.model.QSimGroup;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroup;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroupAttribute;
import com.eurodyn.qlack2.fuse.simm.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.simm.impl.util.SimValidationUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * Methods for group management like CRUD operation for Group and GroupUser
 * Mapping table.
 *
 * @author Shambhu
 */
@Transactional
public class SocialGroupServiceImpl implements SocialGroupService {
	@PersistenceContext(unitName = "fuse-simm")
	private EntityManager em;
	private static final Logger LOGGER = Logger.getLogger(SocialGroupServiceImpl.class.getName());
	public static final String ADD_INDEX_DATA = "INDEX";
	public static final String UPDATE_INDEX = "UPDATE_INDEX";
	public static final String REMOVE_INDEX = "REMOVE_INDEX";

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * This method creates Group and returns the original groupDTO populated
	 * with the ID of the newly created group.
	 *
	 * @param groupDTO
	 *            SocialGroupDTO
	 * @return groupDTO
	 * @throws QSIMMException
	 *             Throws exception if provided groupDTO is null or provided
	 *             group name already exists.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupDTO createGroup(SocialGroupDTO groupDTO) throws QSIMMException {
		SimValidationUtil.validateNullForGroupDTO(groupDTO);
		SocialGroupDTO groupDTOLocal = null;

		SimGroup group = ConverterUtil.convertGroupDTOToModel(groupDTO);
		try {
			em.persist(group);
			if (group.getSimGroupAttributes() != null) {
				for (SimGroupAttribute attribute : group.getSimGroupAttributes()) {
					em.persist(attribute);
				}
			}
		} catch (EntityExistsException e) {
			throw new QNotUnique("Group already exists as provided groupDTO.");
		}

		groupDTOLocal = ConverterUtil.convertGroupModelToDTO(group);

		return groupDTOLocal;
	}

	/**
	 * This method finds whether a group already exist with the same name by
	 * providing group name if exists, it throws QSIMMException exception
	 *
	 * @param groupName
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean groupNameAlreadyExists(String groupName, String groupId) {
		boolean retVal = false;

		StringBuilder quString = new StringBuilder("select g from SimGroup g ");
		quString.append("where g.name = :groupName");
		
		if (groupId != null){
			quString.append(" and g.id != :groupId");
		}
			
		Query query = em.createQuery(quString.toString());
		query.setParameter("groupName", groupName);
		if (groupId != null){
			query.setParameter("groupId", groupId);
		}

		List o = query.getResultList();
		if ((o != null) && (o.size() > 0)) {
			retVal = true;
		}

		return retVal;
	}

	/**
	 * This method returns users as array of user IDs that belong to provided
	 * group ID
	 *
	 * @param groupID
	 *            Group ID
	 * @return IDs as array of String
	 * @throws QSIMMException
	 *             Throws exception if provided group ID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String[] getGroupUsers(String groupID, byte[] status, PagingParams paging) throws QSIMMException {
		SimValidationUtil.validateNullForGroupIDForGroup(groupID);
		StringBuilder quString = new StringBuilder("select gu from SimGroupHasUser gu ");
		quString.append("where gu.groupId.id = :groupID");
		if (status != null) {
			quString.append(" and gu.status in (:status)");
		}

		Query query = em.createQuery(quString.toString());
		query.setParameter("groupID", groupID);
		if (status != null) {
			query.setParameter("status", Arrays.asList(status));
		}

		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			query.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			query.setMaxResults(paging.getPageSize());

		}
		String[] groupUsers = ConverterUtil.getUserIdStringIdsArrayForGroupQuery(query);

		return groupUsers;
	}

	/**
	 * This method retrieves group DTO for provided group ID
	 *
	 * @param groupID
	 * @return group DTO
	 * @throws QSIMMException
	 *             Throws exception if provided groupDTO is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupDTO viewGroup(String groupID) throws QSIMMException {
		SimValidationUtil.validateNullForGroupIDForGroup(groupID);
		SimGroup group = em.find(SimGroup.class, groupID);
		if (group == null) {
			return null;
		} else {
			return ConverterUtil.convertGroupModelToDTO(group);
		}
	}

	/**
	 * This method updates group for provided Group DTO
	 *
	 * @param groupDTO
	 * @return groupDTO
	 * @throws QSIMMException
	 *             Throws exception provided groupDTO is null or provided group
	 *             name already exists.
	 *
	 */
	@Override
	public SocialGroupDTO updateGroup(SocialGroupDTO groupDTO) throws QSIMMException {
		// SimValidationUtil.validateNullForIdInGroupDTO(groupDTO);
		// SimGroup group = ConverterUtil.convertGroupDTOToModel(groupDTO);

		SimGroup group = SimGroup.find(groupDTO.getId(), em);
		// Update group fields
		if (groupDTO.getParentGroupId() != null) {
			group.setParentGroupId(SimGroup.find(groupDTO.getParentGroupId(), em));
		}
		group.setName(groupDTO.getName());
		group.setSlugify(groupDTO.getSlugify());
		group.setLogo(groupDTO.getLogo());
		group.setDescription(groupDTO.getDescription());
		group.setPrivacy(groupDTO.getPrivacy());
		group.setTags(groupDTO.getTags());
		group.setStatus(groupDTO.getStatus());
		group.setCreatedOn(groupDTO.getCreatedOn());

		// Update attributes of group
		if (groupDTO.getSocialGroupAttributes() != null) {
			for (SocialGroupAttributeDTO groupAttribute : groupDTO.getSocialGroupAttributes()) {
				updateGroupAttribute(groupAttribute);

			}
		}

		// Flush persistence context
		em.flush();
		return viewGroup(groupDTO.getId());
	}

	@Override
	public void updateGroupAttribute(SocialGroupAttributeDTO groupAttributeDTO) {
		// Find group attribute from groupId if exists
		SimGroupAttribute groupAttribute = SimGroupAttribute.findByGroupIdAndName(groupAttributeDTO.getGroupId(), groupAttributeDTO.getName(), em);
		if (groupAttribute != null) {
			groupAttribute.setBindata(groupAttributeDTO.getBinData());
			groupAttribute.setContentType(groupAttributeDTO.getContentType());
			groupAttribute.setData(groupAttributeDTO.getData());
			groupAttribute.setName(groupAttributeDTO.getName());
			em.merge(groupAttribute);
		} else {
			groupAttribute = new SimGroupAttribute();
			groupAttribute = ConverterUtil.socialGroupAttributeDTOMapper.toSocialGroupAttribute(groupAttributeDTO,
					groupAttribute.getGroup());
			em.persist(groupAttribute);
		}
	}

	/**
	 * This method deletes group for provided group ID. Note that because of the
	 * referential integrity enforced on the database level, all users that have
	 * joined this group are losing this association. this group from group_user
	 * table
	 * 
	 * @param groupID
	 * @throws QSIMMException
	 *             Throws exception if provided group ID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteGroup(String groupID) throws QSIMMException {
		SimValidationUtil.validateNullForGroupIDForGroup(groupID);
		try {
			SimGroup group = em.find(SimGroup.class, groupID);
			em.remove(group);
			LOGGER.log(Level.INFO, "Deleted Group: {0}.", groupID);
		} catch (IllegalArgumentException e) {
			throw new QNullLookupEntry("Group does not exist with the provided groupId.");
		}
	}

	/**
	 * This method returns Group DTO for provided pagination parameter
	 *
	 * @param paging
	 * @return GroupDTOs
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupDTO[] listGroups(PagingParams paging) {
		Query query = em.createQuery("select g from SimGroup g order by g.name");
		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			query.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			query.setMaxResults(paging.getPageSize());
		}

		SocialGroupDTO[] groupDTOs = ConverterUtil.getGroupDTOArrayForGroupQuery(query);

		return groupDTOs;
	}

	/**
	 * This method changes status of Group to suspend (GROUP_STATUS_SUSPEND = 0)
	 * for provided group ID
	 *
	 * @param groupID
	 *            groupID
	 * @throws QSIMMException
	 *             Throws exception if provided group ID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void suspendGroup(String groupID) throws QSIMMException {
		SimValidationUtil.validateNullForGroupIDForGroup(groupID);
		SocialGroupDTO groupDTO = viewGroup(groupID);
		groupDTO.setStatus(GROUP_STATUS_SUSPENDED);
		updateGroup(groupDTO);
	}

	/**
	 * This method changes status of Group to approved (GROUP_STATUS_APPROVED =
	 * 1) for provided group ID
	 *
	 * @param groupID
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if provided group ID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void resumeGroup(String groupID) throws QSIMMException {
		SimValidationUtil.validateNullForGroupIDForGroup(groupID);
		SocialGroupDTO groupDTO = viewGroup(groupID);
		groupDTO.setStatus(GROUP_STATUS_APPROVED);
		updateGroup(groupDTO);
	}

	/**
	 * This method is not implemented yet
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void sendGroupInvitation() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * This method performs search on the title and the description of the
	 * group.
	 *
	 * @param searchTerm
	 *            searchTerm
	 * @param paging
	 *            paging
	 * @param privacy
	 * @return GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if provided searchTerm is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupDTO[] searchGroups(String searchTerm, PagingParams paging, byte[] privacy) throws QSIMMException {
		SimValidationUtil.validateNullForSearchTermForGroup(searchTerm);

		StringBuilder quString = new StringBuilder("select grp from SimGroup grp ");
		quString.append("where upper(grp.description) like :descSearchTerm ");
		quString.append("or UPPER(grp.name) like :nameSearchTerm and grp.privacy in (:privacy)");
		Query query = em.createQuery(quString.toString());
		query.setParameter("descSearchTerm", "%" + searchTerm.toUpperCase() + "%");
		query.setParameter("nameSearchTerm", "%" + searchTerm.toUpperCase() + "%");
		query.setParameter("privacy", Arrays.asList(privacy));

		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			query.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			query.setMaxResults(paging.getPageSize());
		}

		return ConverterUtil.getGroupDTOArrayForGroupQuery(query);
	}

	/**
	 * This method finds group for a group name.
	 *
	 * @param groupName
	 *            groupName
	 * @return GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if provided group name is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupDTO findGroupByName(String groupName) throws QSIMMException {
		SimValidationUtil.validateNullForSearchTermForGroup(groupName);

		Query query = em.createQuery("select grp from SimGroup grp " + "where grp.name = :nameSearchTerm");
		query.setParameter("nameSearchTerm", groupName);
		SocialGroupDTO[] groupDTOs = ConverterUtil.getGroupDTOArrayForGroupQuery(query);
		if (groupDTOs.length > 0) {
			return groupDTOs[0];
		} else {
			return null;
		}
	}

	/**
	 * This method finds whether a group already exist with the same URL by
	 * providing group URL if exists, it throws QSIMMException exception
	 *
	 * @param groupURL
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean groupURLAlreadyExists(String groupURL, String groupId) {
		boolean retVal = false;

		StringBuilder quString = new StringBuilder("select g from SimGroup g ");
		quString.append("where g.slugify = :groupURL");
		
		if (groupId != null){
			quString.append(" and g.id != :groupId");
		}

		Query query = em.createQuery(quString.toString());
		query.setParameter("groupURL", groupURL);
		if (groupId != null){
			query.setParameter("groupId", groupId);
		}

		List o = query.getResultList();
		if ((o != null) && (o.size() > 0)) {
			retVal = true;
		}

		return retVal;
	}

	@Override
	public SocialGroupDTO findGroupByURL(String groupURL) {
		QSimGroup qgroup = QSimGroup.simGroup;
		SimGroup simGroup = new JPAQueryFactory(em).selectFrom(qgroup).where(qgroup.slugify.eq(groupURL)).fetchOne();
		if (simGroup != null){
			return ConverterUtil.convertGroupModelToDTO(simGroup);
		} else {
			return null;
		}
	}

}
