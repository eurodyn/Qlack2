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

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.GroupUserService;
import com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SocialGroupUserDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QAlreadyGroupUser;
import com.eurodyn.qlack2.fuse.simm.api.exception.QInvalidID;
import com.eurodyn.qlack2.fuse.simm.api.exception.QNotInvited;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;
import com.eurodyn.qlack2.fuse.simm.impl.model.QSimGroup;
import com.eurodyn.qlack2.fuse.simm.impl.model.QSimGroupHasUser;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroup;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimGroupHasUser;
import com.eurodyn.qlack2.fuse.simm.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.simm.impl.util.SimValidationUtil;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants.*;

/**
 *
 * @author Shambhu
 */
@Transactional
public class GroupUserServiceImpl implements GroupUserService {
	private static final Logger LOGGER = Logger.getLogger(GroupUserServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-simm")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * Check whether user has been invited
	 *
	 * @param userID
	 * @param groupID
	 * @return
	 * @throws QSIMMException
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean isInvited(String userID, String groupID) throws QSIMMException {
		SimGroupHasUser groupUser = findUserGroup(userID, groupID);
		return groupUser != null ? groupUser.getStatus() == GROUP_USER_STATUS_INVITED : false;
	}

	/**
	 * This method joins the user in a group according to the group type.
	 * 
	 * @param userId
	 *            user ID
	 * @param groupId
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             groupID is null or if user has already joined the group
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupDTO requestToJoinGroup(String userId, String groupId) throws QSIMMException {
		SimValidationUtil.validateUserIdAndGroupId(userId, groupId);
		SimGroup group = null;
		group = em.find(SimGroup.class, groupId);
		SimGroupHasUser groupUser = null;
		if (group == null) {
			throw new QInvalidID("Group does not exist with the provided groupId.");
		}
		if (group.getPrivacy() != null) {
			switch (group.getPrivacy()) {
			case GROUP_PRIVACY_PUBLIC:
				if (isInvited(userId, groupId)) {
					groupUser = findUserGroup(userId, groupId);
					groupUser.setStatus(GROUP_USER_STATUS_ACCEPTED);
					groupUser.setJoinedOnDate(System.currentTimeMillis());
					// em.persist(groupUser);
				} else if (findUserGroup(userId, groupId) != null) {
					throw new QAlreadyGroupUser("GroupUser with provided userId and groupId already exists in group.");
				} else {
					groupUser = new SimGroupHasUser(group, userId, GROUP_USER_STATUS_ACCEPTED,
							System.currentTimeMillis());
					em.persist(groupUser);
				}
				break;
			case GROUP_PRIVACY_INVITED:
				if (isInvited(userId, groupId)) {
					groupUser = findUserGroup(userId, groupId);
					groupUser.setStatus(GROUP_USER_STATUS_ACCEPTED);
					groupUser.setJoinedOnDate(System.currentTimeMillis());
					// em.persist(groupUser);
				} else if (findUserGroup(userId, groupId) != null) {
					throw new QAlreadyGroupUser("GroupUser with provided userId and groupId already exists in group.");
				} else {
					groupUser = new SimGroupHasUser(group, userId, GROUP_USER_STATUS_REQUESTED_NEW, 0);
					em.persist(groupUser);
				}
				break;
			case GROUP_PRIVACY_PRIVATE:
				if (isInvited(userId, groupId)) {
					groupUser = findUserGroup(userId, groupId);
					groupUser.setStatus(GROUP_USER_STATUS_ACCEPTED);
					groupUser.setJoinedOnDate(System.currentTimeMillis());
					// em.persist(groupUser);
				} else if (findUserGroup(userId, groupId) != null) {
					throw new QAlreadyGroupUser("GroupUser with provided userId and groupId already exists in group.");
				} else {
					LOGGER.log(Level.INFO, "User ''{0}'' is not invited in group ''{1}''.",
							new String[] { userId, groupId });
					throw new QNotInvited("User can not join this group unless an invitation has been received first.");
				}
				break;
			}
		}
		return ConverterUtil.convertGroupModelToDTO(group);
	}

	/**
	 * This method finds a GroupUser with given group ID and user ID
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            Group ID
	 * @return SimGroupHasUser Model object for userGroup.
	 */
	private SimGroupHasUser findUserGroup(String userID, String groupID) {
		SimValidationUtil.validateUserIdAndGroupId(userID, groupID);
		QSimGroupHasUser qsimGroupHasUser = QSimGroupHasUser.simGroupHasUser;
		JPAQuery<SimGroupHasUser> q = new JPAQueryFactory(em).selectFrom(qsimGroupHasUser)
				.where(qsimGroupHasUser.groupId.id.eq(groupID).and(qsimGroupHasUser.userId.eq(userID)));
		SimGroupHasUser l = q.fetchOne();
		return l;
	}

	/**
	 * This method changes status of user in group as accepted
	 * (GROUP_USER_STATUS_ACCEPTED = 1) to join the group.
	 *
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if user has not been joined the group.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void acceptUserJoin(String userID, String groupID) throws QSIMMException {
		LOGGER.log(Level.FINEST, "Accepting user ''{0}'' join to group ''{1}''.", new String[] { userID, groupID });
		SimValidationUtil.validateUserIdAndGroupId(userID, groupID);
		// TODO remove group find we don't use it
		// SimGroup group = em.find(SimGroup.class, groupID);
		SimGroupHasUser groupUser = findUserGroup(userID, groupID);
		SimValidationUtil.validateGroupUserModelObject(groupUser);
		groupUser.setJoinedOnDate(System.currentTimeMillis());
		groupUser.setStatus(GROUP_USER_STATUS_ACCEPTED);
		em.merge(groupUser);
	}

	/**
	 * This method rejects a request of a user to join a group. Note that the
	 * actual user request is **deleted** from the database.
	 *
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if user has not been joined the group.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void rejectUserJoin(String userID, String groupID) throws QSIMMException {
		SimValidationUtil.validateUserIdAndGroupId(userID, groupID);
		// TODO see CHANGES
		// SimGroup group = em.find(SimGroup.class, groupID);
		SimGroupHasUser groupUser = findUserGroup(userID, groupID);
		SimValidationUtil.validateGroupUserModelObject(groupUser);
		em.remove(groupUser);
	}

	@Override
	public void banUser(String userID, String groupID) throws QSIMMException {
		SimValidationUtil.validateUserIdAndGroupId(userID, groupID);
		SimGroupHasUser groupUser = findUserGroup(userID, groupID);
		SimValidationUtil.validateGroupUserModelObject(groupUser);
		groupUser.setStatus(GROUP_USER_STATUS_BANNED);
		em.merge(groupUser);
	}

	/**
	 * This method removes the user for provided group, it physically removes
	 * the user from group.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @throws QSIMMException
	 *             Throws exception if user has not been joined the group.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void leaveGroup(String userID, String groupID) throws QSIMMException {
		SimValidationUtil.validateUserIdAndGroupId(userID, groupID);
		SimGroupHasUser groupUser = findUserGroup(userID, groupID);
		SimValidationUtil.validateGroupUserModelObject(groupUser);

		em.remove(groupUser);
	}

	/**
	 * This method is not implemented yet.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void shareGroup(String userID, String groupID) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * This method returns SocialGroupUserDTO for supplied group ID and User ID.
	 * 
	 * @param userID
	 *            User ID
	 * @param groupID
	 *            group ID
	 * @return SocialGroupUserDTO
	 * @throws QSIMMException
	 *             Throws exception if provided userID or groupID is null.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupUserDTO getGroupUser(String userID, String groupID) throws QSIMMException {
		SimValidationUtil.validateUserIdAndGroupId(userID, groupID);
		SimGroupHasUser groupUser = findUserGroup(userID, groupID);

		if (groupUser == null) {
			return null;
		} else {
			return ConverterUtil.convertGroupUserModelToDTO(groupUser);
		}
	}

	/**
	 * This method returns a list with all the users which are members in the
	 * same groups as the passed-in user.
	 * 
	 * @param userID
	 * @return array of userIds
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String[] getMembersForUserGroups(String userID) throws QSIMMException {
		SimValidationUtil.validateNullForuserID(userID);
		StringBuilder quString = new StringBuilder("select sghu1.userId from SimGroupHasUser sghu1 ");
		quString.append("where sghu1.groupId in (");
		quString.append("  select sghu2.groupId from SimGroupHasUser sghu2 ");
		quString.append("  where sghu2.userId = :userID) and sghu1.userId <> :userID and sghu1.status = :status");

		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		query.setParameter("status", GROUP_USER_STATUS_ACCEPTED);
		@SuppressWarnings("unchecked")
		List<String> results = query.getResultList();
		String[] retVal = new String[results.size()];
		int counter = 0;
		for (String i : results) {
			retVal[counter++] = i;
		}

		return retVal;
	}

	/**
	 * This method returns array of SocialGroupUserDTO for provided group ID.
	 * 
	 * @param groupID
	 * @param status
	 *            byte array of status
	 * @param paging
	 * @return array of GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if provided groupID is null.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupUserDTO[] listContactsForGroup(String groupID, byte[] status, PagingParams paging)
			throws QSIMMException {
		SimValidationUtil.validateNullForGroupIDForGroup(groupID);
		StringBuilder quString = new StringBuilder("select gu from SimGroupHasUser gu, SimGroup g ");
		quString.append("where gu.groupId.id = :groupID ");
		if (status != null && status.length > 0) {
			quString.append(" and gu.status in (:status) ");
		}
		quString.append("and g.id = gu.groupId.id order by gu.userId");
		Query query = em.createQuery(quString.toString());
		query.setParameter("groupID", groupID);
		if (status != null && status.length > 0) {
			query.setParameter("status", Arrays.asList(status));
		}
		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			query.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			query.setMaxResults(paging.getPageSize());
		}

		SocialGroupUserDTO[] groupUsers = ConverterUtil.sortGroupUserDTOs(query);

		return groupUsers;
	}

	/**
	 * This method returns the groups joined by a specific User.
	 * 
	 * @param userID
	 * @param status
	 *            byte array, if status is null then this will return for all
	 *            status
	 * @return array of GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupDTO[] listGroupsForUser(String userID, String searchTerm, byte[] status, PagingParams paging)
			throws QSIMMException {
		SimValidationUtil.validateNullForuserID(userID);
		List<Byte> statusList = new ArrayList<Byte>();
		if (status != null && status.length > 0) {
			for (byte s : status) {
				statusList.add(s);
			}
		}

		StringBuilder quString = new StringBuilder("select g from SimGroup g, SimGroupHasUser gu ");
		quString.append("where g.id=gu.groupId.id and gu.userId = :userID ");
		if (searchTerm != null) {
			quString.append(" and upper(g.name) like :searchTerm");
		}
		if (statusList.size() > 0) {
			quString.append(" and gu.status in (:status) ");
		}

		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		if (searchTerm != null) {
			query.setParameter("searchTerm", "%" + searchTerm.toUpperCase() + "%");
		}
		if (statusList.size() > 0) {
			query.setParameter("status", statusList);
		}
		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			query.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			query.setMaxResults(paging.getPageSize());
		}

		SocialGroupDTO[] groupDTOs = ConverterUtil.getGroupDTOArrayForGroupQuery(query);

		return groupDTOs;
	}

	/**
	 * Returns all Contacts from all groups of provided user ID in which this
	 * user has been joined
	 *
	 * @param userID
	 * @param status
	 *            byte array of status
	 * @param paging
	 * @return array of GroupDTOs
	 * @throws QSIMMException
	 *             Throws exception if Provided userID is null.
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupUserDTO[] listContactsForUser(String userID, byte[] status, PagingParams paging)
			throws QSIMMException {
		SimValidationUtil.validateNullForuserID(userID);
		List<Byte> statusList = new ArrayList<Byte>();
		if (status != null && status.length > 0) {
			for (byte s : status) {
				statusList.add(s);
			}
		}
		StringBuilder quString = new StringBuilder("select gu from SimGroupHasUser gu, SimGroup g where ");
		quString.append("g.id = gu.groupId.id and gu.userId <> :userID ");
		if (statusList.size() > 0) {
			quString.append(" and gu.status in (:status) ");
		}
		quString.append(
				"and gu.groupId.id in (select guin.groupId.id from SimGroupHasUser guin where guin.userId = :userIdIn )");
		quString.append(" order by gu.userId");

		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		query.setParameter("userIdIn", userID);
		if (statusList.size() > 0) {
			query.setParameter("status", statusList);
		}

		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			query.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			query.setMaxResults(paging.getPageSize());
		}

		SocialGroupUserDTO[] groupUserDTOs = ConverterUtil.sortGroupUserDTOs(query);

		return groupUserDTOs;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public List<SocialGroupDTO> listAvailableGroups(String userID, boolean isMember) {
		List<SocialGroupDTO> retVal = new ArrayList<>();
		// MapStruct refs.
		QSimGroup qGroup = QSimGroup.simGroup;
		QSimGroupHasUser qGroupUser = QSimGroupHasUser.simGroupHasUser;

		JPAQuery<Tuple> q = null;

		// Create the query.
		if (isMember) {
			// Gets all groups where user with given id has accepted status
			q = new JPAQueryFactory(em)
					.select(qGroup.id, qGroup.name, qGroup.description, qGroup.privacy, qGroup.createdOn,
							qGroup.slugify,
							new JPAQueryFactory(em).select(qGroupUser.count()).from(qGroupUser)
									.where(qGroupUser.groupId.id.eq(qGroup.id)
											.and(qGroupUser.status.eq(GROUP_USER_STATUS_ACCEPTED))))
					.from(qGroup).leftJoin(qGroup.simGroupHasUsers, qGroupUser).where(qGroupUser.status
							.eq(SIMMConstants.GROUP_USER_STATUS_ACCEPTED).and(qGroupUser.userId.eq(userID)))

					.groupBy(qGroup.id);
		} else {
			// Gets all groups with privacy type public and by invitation
			// and groups with privacy type private and user with given id is
			// invited or accepted on it
			q = new JPAQueryFactory(em)
					.select(qGroup.id, qGroup.name, qGroup.description, qGroup.privacy, qGroup.createdOn,
							qGroup.slugify,
							new JPAQueryFactory(em).select(qGroupUser.count()).from(qGroupUser)
									.where(qGroupUser.groupId.id.eq(qGroup.id)
											.and(qGroupUser.status.eq(GROUP_USER_STATUS_ACCEPTED))))
					.from(qGroup)
					.leftJoin(qGroup.simGroupHasUsers,
							qGroupUser)
					.where((qGroup.privacy.eq(SIMMConstants.GROUP_PRIVACY_PUBLIC))
							.or(qGroup.privacy.eq(SIMMConstants.GROUP_PRIVACY_INVITED))
							.or(qGroup.privacy.eq(SIMMConstants.GROUP_PRIVACY_PRIVATE)
									.and((qGroupUser.status.eq(SIMMConstants.GROUP_USER_STATUS_INVITED)
											.or(qGroupUser.status.eq(SIMMConstants.GROUP_USER_STATUS_ACCEPTED)))
													.and(qGroupUser.userId.eq(userID)))))
					.groupBy(qGroup.id);
		}

		// Execute the query and convert it to the return type.
		for (Tuple tuple : q.fetch()) {
			SocialGroupDTO dto = new SocialGroupDTO();
			dto.setId(tuple.get(qGroup.id));
			dto.setName(tuple.get(qGroup.name));
			dto.setDescription(tuple.get(qGroup.description));
			dto.setPrivacy(tuple.get(qGroup.privacy));
			dto.setCreatedOn(tuple.get(qGroup.createdOn));
			dto.setSlugify(tuple.get(qGroup.slugify));
			// TODO is there a better way to get the count without referencing
			// by index?
			// It counts only the users with status accepted
			dto.setMembers(tuple.get(6, Long.class));
			retVal.add(dto);
		}

		return retVal;
	}

	/**
	 * This method returns all group users for provided userID and status for
	 * eg. status = GROUP_USER_STATUS_INVITED implies all the group invitations
	 * to a user that are not yet responded by the user.
	 * 
	 * @param userID
	 * @param status
	 * @return array of SocialGroupUserDTO
	 * @throws QSIMMException
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public SocialGroupUserDTO[] getAllContactsForStatus(String userID, byte status) throws QSIMMException {
		SimValidationUtil.validateNullForUserIdAndStatus(userID, status);
		StringBuilder quString = new StringBuilder("select gu from SimGroupHasUser gu, SimGroup g where ");
		quString.append("g.id = gu.groupId.id and gu.userId = :userID and gu.status = :status");

		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		query.setParameter("status", status);
		SocialGroupUserDTO[] groupUserDTOs = ConverterUtil.getGroupUserDTOArrayForGroupQuery(query);

		return groupUserDTOs;
	}

	/**
	 * Invite user map the user with group and status as invited
	 *
	 * @param userID
	 * @param groupDTO
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void inviteUser(String userID, SocialGroupDTO groupDTO) throws QSIMMException {
		LOGGER.log(Level.INFO, "Inviting user ''{0}'' to group ''{1}''.", new String[] { userID, groupDTO.getId() });
		SimGroupHasUser groupUser = new SimGroupHasUser();
		SimGroup group = em.find(SimGroup.class, groupDTO.getId());
		groupUser.setGroupId(group);
		groupUser.setUserId(userID);
		groupUser.setStatus(GROUP_USER_STATUS_INVITED);
		em.persist(groupUser);
	}

}
