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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.FriendsService;
import com.eurodyn.qlack2.fuse.simm.api.dto.FriendDTO;
import com.eurodyn.qlack2.fuse.simm.api.exception.QAlreadyMember;
import com.eurodyn.qlack2.fuse.simm.api.exception.QInvalidID;
import com.eurodyn.qlack2.fuse.simm.api.exception.QNotUnique;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;
import com.eurodyn.qlack2.fuse.simm.impl.model.QSimFriends;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimFriends;
import com.eurodyn.qlack2.fuse.simm.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.simm.impl.util.SimValidationUtil;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 *
 * @author European Dynamics SA
 */
@Transactional
public class FriendsServiceImpl implements FriendsService {
	private static final Logger LOGGER = Logger
			.getLogger(FriendsServiceImpl.class.getName());

	@PersistenceContext(unitName = "fuse-simm")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}
	/**
	 * This method request for friendship for provided User ID. This method
	 * leaves confirmed_on value as null in order to indicate that a friendship
	 * has been requested but it has not become approved yet. Note that friend
	 * bindings are not bidirectional in low-level, i.e. two rows are created on
	 * the db instead of one, however, at this stage where the friendship is not
	 * yet confirmed only one row exists (that can also help you indicate who
	 * has initiated this friend request). When the friend request is accepted
	 * by the other party, then the second row in the db is created.
	 *
	 * @param userID
	 *            User ID
	 * @param friendID
	 *            friendID
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             friendID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void requestFriendship(String userID, String friendID)
			throws QSIMMException {
		SimFriends friend = findByIDs(userID, friendID);
		if (friend != null) {
			LOGGER.log(Level.SEVERE, "User is already present in friend list.");
			throw new QAlreadyMember("User is already present in friend list.");
		}

		try {
			SimFriends friends = new SimFriends();
			friends.setUserId(userID);
			friends.setFriendId(friendID);
			friends.setCreatedOn(new Date().getTime());
			em.persist(friends);

			// Post a notification about the event.
//			if (PropertiesLoaderSingleton.getInstance()
//					.getProperty("QlackFuse.SIMM.realtime.JMS.notifications")
//					.equals("true")) {
//				SIMMMessage message = new SIMMMessage();
//				message.setType(SIMMMessage.MSGTYPE__FRIENDSHIP_REQUESTED);
//				message.setSrcUserID(userID);
//				message.setStringProperty(SIMMMessage.PRIVATE_USERID, friendID);
//				try {
//					Messenger.post(connectionFactory, notificationTopic,
//							message);
//				} catch (JMSException ex) {
//					LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//					throw new QSIMMException(
//							QSIMMException.CODES.ERR_SIMM_0033,
//							ex.getLocalizedMessage());
//				}
//			}
		} catch (EntityExistsException e) {
			throw new QAlreadyMember("User is already present in friend list.");
		} catch (IllegalArgumentException e) {
			throw new QInvalidID("Provided userId or FreindID is not valid ID.");
		}
	}

	/**
	 * This method find a SimFriends model object for provided friendID and User
	 * ID.
	 *
	 * @param userID
	 *            User ID
	 * @param friendID
	 *            friendID
	 * @return SimFriends Model object for group and user.
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             friendID is null
	 */
	private SimFriends findByIDs(String userID, String friendID)
			throws QSIMMException {
		SimValidationUtil.validateUserIdAndFriendId(userID, friendID);

		Query query = em.createQuery("select fr from SimFriends fr "
				+ "where fr.userId = :userID and fr.friendId = :friendID");
		query.setParameter("friendID", friendID);
		query.setParameter("userID", userID);
		try {
			return (SimFriends) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		} catch (NonUniqueResultException ex) {
			throw new QNotUnique(
					"More than one friend found with the provided userId and friend.");
		}
	}

	/**
	 * This method request for friendship with many friend for provided User ID.
	 * This method leave confirmed on value null.
	 *
	 * @param userID
	 *            User ID
	 * @param friendsIDs
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             friendID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void requestFriendships(String userID, String[] friendsIDs)
			throws QSIMMException {
		for (String friend : friendsIDs) {
			requestFriendship(userID, friend);
		}
	}

	/**
	 * This method remove (Remove from database) friendship for provided User
	 * ID.
	 *
	 * @param userID
	 *            User ID
	 * @param friendID
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             friendID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void rejectFriendship(String userID, String friendID)
			throws QSIMMException {
		SimFriends friend = findByIDs(userID, friendID);
		SimFriends inverseFriend = findByIDs(friendID, userID);
		SimValidationUtil.validateFriendObjectPair(friend, inverseFriend);
		String confirmationDate = null;
		if (friend != null) {
			if (friend.getConfirmedOn() != null) {
				confirmationDate = String.valueOf(friend.getConfirmedOn());
			}
			em.remove(friend);
		}
		if (inverseFriend != null) {
			if (inverseFriend.getConfirmedOn() != null) {
				confirmationDate = String.valueOf(inverseFriend
						.getConfirmedOn());
			}
			em.remove(inverseFriend);
		}

		// Post a notification about the event.
//		if (PropertiesLoaderSingleton.getInstance()
//				.getProperty("QlackFuse.SIMM.realtime.JMS.notifications")
//				.equals("true")) {
//			SIMMMessage message = new SIMMMessage();
//			message.setType(SIMMMessage.MSGTYPE__FRIENDSHIP_REJECTED);
//			message.setSrcUserID(userID);
//			message.setStringProperty(SIMMMessage.PROPERTY__FRIEND_ID, friendID);
//			message.setStringProperty(
//					SIMMMessage.PROPERTY__FRIENDSHIP_CONFIRMED_ON,
//					confirmationDate);
//			message.setStringProperty(SIMMMessage.PRIVATE_USERID, friendID);
//			try {
//				Messenger.post(connectionFactory, notificationTopic, message);
//				message.setStringProperty(SIMMMessage.PRIVATE_USERID, userID);
//				Messenger.post(connectionFactory, notificationTopic, message);
//			} catch (JMSException ex) {
//				LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//				throw new QSIMMException(QSIMMException.CODES.ERR_SIMM_0033,
//						ex.getLocalizedMessage());
//			}
//		}
	}

	/**
	 * This method remove (Remove from database) friendship with many friend for
	 * provided User ID.
	 *
	 * @param userID
	 *            User ID
	 * @param friendsIDs
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             friendID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void rejectFriendships(String userID, String[] friendsIDs)
			throws QSIMMException {
		for (String friend : friendsIDs) {
			rejectFriendship(userID, friend);
		}
	}

	/**
	 * This method update confirmation of friendship by updating field confirmed
	 * on with current date .
	 *
	 * @param userID
	 *            User ID
	 * @param friendID
	 * @return
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             friendID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public FriendDTO acceptFriendship(String userID, String friendID)
			throws QSIMMException {
		Long now = System.currentTimeMillis();

		// We first update the part of the relationship from the side of the
		// user that initiated it.
		SimFriends srcFriend = findByIDs(friendID, userID);
		SimValidationUtil.validateFriendObject(srcFriend);
		srcFriend.setConfirmedOn(now);
		em.merge(srcFriend);

		// And then, we create a new row for the other side. This double-insert
		// allows us to keep
		// different attributes for each side of the relationship (i.e.
		// assigning friend to groups, etc.)
		SimFriends trgFriend = new SimFriends();
		trgFriend.setUserId(srcFriend.getFriendId());
		trgFriend.setFriendId(srcFriend.getUserId());
		trgFriend.setCreatedOn(now);
		trgFriend.setConfirmedOn(now);
		em.persist(trgFriend);

		// Post a notification about the event.
//		if (PropertiesLoaderSingleton.getInstance()
//				.getProperty("QlackFuse.SIMM.realtime.JMS.notifications")
//				.equals("true")) {
//			SIMMMessage message = new SIMMMessage();
//			message.setType(SIMMMessage.MSGTYPE__FRIENDSHIP_ACCEPTED);
//			message.setSrcUserID(userID);
//			message.setStringProperty(SIMMMessage.PROPERTY__FRIEND_ID, friendID);
//			message.setStringProperty(
//					SIMMMessage.PROPERTY__FRIENDSHIP_CONFIRMED_ON,
//					String.valueOf(now));
//			message.setStringProperty(SIMMMessage.PRIVATE_USERID, userID);
//			try {
//				Messenger.post(connectionFactory, notificationTopic, message);
//				message.setStringProperty(SIMMMessage.PRIVATE_USERID, friendID);
//				Messenger.post(connectionFactory, notificationTopic, message);
//			} catch (JMSException ex) {
//				LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
//				throw new QSIMMException(QSIMMException.CODES.ERR_SIMM_0033,
//						ex.getLocalizedMessage());
//			}
//		}

		return ConverterUtil.convertFriendsModelToDTO(srcFriend, userID);
	}

	/**
	 * This method update confirmation (with many friends) of friendship by
	 * updating field confirmed on with current date .
	 *
	 * @param userID
	 *            User ID
	 * @param friendsIDs
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null or provided
	 *             friendID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public FriendDTO[] acceptFriendships(String userID, String[] friendsIDs)
			throws QSIMMException {
		FriendDTO friendDTOs[] = new FriendDTO[friendsIDs.length];
		int index = 0;
		for (String friend : friendsIDs) {
			friendDTOs[index++] = acceptFriendship(userID, friend);
		}
		return friendDTOs;
	}

	/**
	 * This method returns all friends including pending friend requests.
	 *
	 * @param userID
	 *            User ID
	 * @param pagingParams
	 * @return array of FriendDTO
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public FriendDTO[] getFriends(String userID, PagingParams pagingParams)
			throws QSIMMException {
		SimValidationUtil.validateUserId(userID);
		StringBuilder quString = new StringBuilder(
				"select fr from SimFriends fr ");
		quString.append("where fr.userId = :userID order by fr.createdOn desc");
		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		
		if ((pagingParams != null) && (pagingParams.getCurrentPage() > -1)) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1)
					* pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}
		
		FriendDTO[] friends = ConverterUtil.getFriendDTOArrayForFriendsQuery(
				query, userID);

		return friends;
	}

	/**
	 * This method returns all active friendships.
	 *
	 * @param userID
	 *            User ID
	 * @param pagingParams
	 * @return array of FriendDTO
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public FriendDTO[] getEstablishedFriends(String userID,
			PagingParams pagingParams) throws QSIMMException {
		SimValidationUtil.validateUserId(userID);
		StringBuilder quString = new StringBuilder(
				"select fr from SimFriends fr ");
		quString.append("where fr.userId = :userID and fr.confirmedOn is not null order by fr.createdOn desc");
		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		
		if ((pagingParams != null) && (pagingParams.getCurrentPage() > -1)) {
		query.setFirstResult((pagingParams.getCurrentPage() - 1)
				* pagingParams.getPageSize());
		query.setMaxResults(pagingParams.getPageSize());
		}
		
		FriendDTO[] friends = ConverterUtil.getFriendDTOArrayForFriendsQuery(
				query, userID);

		return friends;
	}

	/**
	 * This method returns all friends IDs.
	 *
	 * @param userID
	 *            User ID
	 * @return array of string as friends ID
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String[] getFriendsIDs(String userID) throws QSIMMException {
		SimValidationUtil.validateUserId(userID);
		StringBuilder quString = new StringBuilder(
				"select fr from SimFriends fr ");
		quString.append("where fr.userId = :userID ");
		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		String[] friendIds = ConverterUtil.getFriendIdsArrayForFriendsQuery(
				query, userID);

		return friendIds;
	}
	
	
	
	/**
	 * This method returns the total amount of logged in user's friends.
	 *
	 * @param userID
	 *            User ID
	 * @return long of total amount of logged in user's friends.
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public long getAllFriends(String userID) throws QSIMMException {
		QSimFriends qsimFriends = QSimFriends.simFriends;
		long  simFriends = new JPAQueryFactory(em).selectFrom(qsimFriends).
				where(qsimFriends.userId.eq(userID)).fetchCount();
		
		return simFriends;
	}
	

	/**
	 * This method returns all the pending friends requests the user has made.
	 *
	 * @param userID
	 *            User ID
	 * @return array of FriendDTO
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public FriendDTO[] getFriendsOwnRequest(String userID)
			throws QSIMMException {
		SimValidationUtil.validateUserId(userID);

		StringBuilder quString = new StringBuilder(
				"select fr from SimFriends fr ");
		quString.append("where fr.userId = :userID ");
		quString.append("  and fr.confirmedOn is null ");
		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		return ConverterUtil.getFriendDTOArrayForFriendsQuery(query, userID);
	}

	/**
	 * This method returns all the pending friends requests other users have
	 * made to you (i.e. other users asking you to add them as a friend but for
	 * which you haven't replied yet).
	 *
	 * @param userID
	 *            User ID
	 * @return array of FriendDTO
	 * @throws QSIMMException
	 *             Throws exception if provided userID is null
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public FriendDTO[] getFriendsRemoteRequest(String userID)
			throws QSIMMException {
		SimValidationUtil.validateUserId(userID);

		StringBuilder quString = new StringBuilder(
				"select fr from SimFriends fr ");
		quString.append("where fr.friendId = :userID ");
		quString.append("  and fr.confirmedOn is null ");
		Query query = em.createQuery(quString.toString());
		query.setParameter("userID", userID);
		return ConverterUtil.getFriendDTOArrayForFriendsQuery(query, userID);
	}

}
