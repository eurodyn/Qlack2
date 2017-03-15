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

import java.util.Arrays;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.simm.api.PostingService;
import com.eurodyn.qlack2.fuse.simm.api.dto.PostItemDTO;
import com.eurodyn.qlack2.fuse.simm.api.dto.SIMMConstants;
import com.eurodyn.qlack2.fuse.simm.api.exception.QSIMMException;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimHomepageActivity;
import com.eurodyn.qlack2.fuse.simm.impl.model.SimHomepageActivityBin;
import com.eurodyn.qlack2.fuse.simm.impl.util.ConverterUtil;

/**
 * A Stateless Session EJB and also web service implementation class providing
 * methods for Activity operation for Group Home page
 *
 * @author European Dynamics SA
 */
@Transactional
public class PostingServiceImpl implements PostingService {
	private static final Logger LOGGER = Logger.getLogger(PostingServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-simm")
	private EntityManager em;
	public static final String ADD_INDEX_DATA = "INDEX";
	public static final String UPDATE_INDEX = "UPDATE_INDEX";
	public static final String REMOVE_INDEX = "REMOVE_INDEX";

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * Create a activity
	 * 
	 * @param pi
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public PostItemDTO createActivity(PostItemDTO pi) throws QSIMMException {
		SimHomepageActivity homepageActivity = new SimHomepageActivity();
		homepageActivity.setCategoryId(StringUtils.isEmpty(pi.getCategoryID()) ? "0" : pi.getCategoryID());
		homepageActivity.setCreatedByUserId(pi.getCreatedByUserID() != null ? pi.getCreatedByUserID() : "system");
		homepageActivity.setCreatedOn(pi.getCreatedOn());
		homepageActivity.setDescription(pi.getDescription());
		homepageActivity.setHomepageId(pi.getHomepageID());
		homepageActivity.setLink(pi.getLink());
		if (pi.getParentHomepageID() != null) {
			homepageActivity.setParentHpageActvtId(
					(SimHomepageActivity) em.find(SimHomepageActivity.class, pi.getParentHomepageID()));
		}
		homepageActivity.setStatus(pi.getStatus());
		homepageActivity.setTitle(pi.getTitle());
		homepageActivity.setCategoryIcon(pi.getCategoryIcon());
		em.persist(homepageActivity);
		pi.setId(homepageActivity.getId());
		if (pi.getBinContents() != null) {
			for (int i = 0; i < pi.getBinContents().length; i++) {
				SimHomepageActivityBin shab = new SimHomepageActivityBin();
				shab.setActivityId(homepageActivity);
				shab.setBinData(pi.getBinContents()[i]);
				shab.setBinOrder(i);
				shab.setCreatedOn(System.currentTimeMillis());
				em.persist(shab);
			}
		}

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.SIMM.realtime.JMS.notifications").equals("true"))
		// {
		// SIMMMessage message = new SIMMMessage();
		// message.setType(SIMMMessage.MSGTYPE__ACTIVITY_CREATED);
		// message.setSrcUserID(pi.getCreatedByUserID());
		// message.setStringProperty(SIMMMessage.PROPERTY__HOMEPAGE_ID,
		// pi.getHomepageID());
		// message.setStringProperty(SIMMMessage.PROPERTY__ACTIVITY_ID,
		// pi.getId());
		// if (pi.getParentHomepageID() != null) {
		// message.setStringProperty(SIMMMessage.PROPERTY__PARENT_ACTIVITY_ID,
		// pi.getParentHomepageID());
		// message.setStringProperty(SIMMMessage.PROPERTY__PARENT_ACTIVITY_TITLE,
		// homepageActivity.getParentHpageActvtId().getTitle());
		// }
		// message.setStringProperty(SIMMMessage.PROPERTY__ACTIVITY_CATEGORY_ID,
		// pi.getCategoryID());
		// message.setStringProperty(SIMMMessage.PROPERTY__ACTIVITY_TITLE,
		// pi.getTitle());
		// if
		// (!org.apache.commons.lang.StringUtils.isEmpty(pi.getDescription())) {
		// message.setBody(pi.getDescription());
		// }
		// message.setStringProperty(SIMMMessage.PRIVATE_USERID,
		// pi.getHomepageID());
		// try {
		// // First we publish a message for the owner of the homepage.
		// Messenger.post(connectionFactory, notificationTopic, message);
		// //Then to the creator of the activity if not the same as the owner
		// if (!pi.getCreatedByUserID().equals(pi.getHomepageID())) {
		// message.setStringProperty(SIMMMessage.PRIVATE_USERID,
		// pi.getCreatedByUserID());
		// Messenger.post(connectionFactory, notificationTopic, message);
		// }
		// // And then to all other users that are associated with that
		// particular activity.
		// // Association is determined from the remaining 'createdByUserId'
		// that may have
		// // provided contribution to this particular activity. Users already
		// notified are not
		// // notified twice.
		// if (pi.getParentHomepageID() != null) {
		// String parentUserId =
		// homepageActivity.getParentHpageActvtId().getCreatedByUserId();
		// if ((!parentUserId.equals(pi.getCreatedByUserID())) &&
		// (!parentUserId.equals(pi.getHomepageID()))) {
		// message.setStringProperty(SIMMMessage.PRIVATE_USERID, parentUserId);
		// Messenger.post(connectionFactory, notificationTopic, message);
		// }
		// Query q = em.createQuery(
		// "select distinct sha.createdByUserId from SimHomepageActivity sha "
		// + "where sha.parentHpageActvtId = :parentID ");
		// q.setParameter("parentID", homepageActivity.getParentHpageActvtId());
		// Iterator<String> recipientsIterator = q.getResultList().iterator();
		// while (recipientsIterator.hasNext()) {
		// String userId = recipientsIterator.next();
		// if ((!userId.equals(pi.getCreatedByUserID())) &&
		// (!userId.equals(pi.getHomepageID()))
		// && (!userId.equals(parentUserId))) {
		// message.setStringProperty(SIMMMessage.PRIVATE_USERID, userId);
		// Messenger.post(connectionFactory, notificationTopic, message);
		// }
		// }
		// }
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QSIMMException(QSIMMException.CODES.ERR_SIMM_0033,
		// ex.getLocalizedMessage());
		// }
		// }

		return pi;
	}

	/**
	 * This method returns DTO of Home Page Activities
	 *
	 * @param homepageID
	 * @param paging
	 * @param status
	 * @param includeChildren
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public PostItemDTO[] getHomePageActivities(String homepageID, PagingParams paging,
			byte[] status, boolean includeChildren, boolean includeBinary) {
		return getHomePagesActivities(new String[] { homepageID }, paging, status, includeChildren,
				includeBinary);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public PostItemDTO[] getHomePagesActivities(String[] homepageIDs, PagingParams paging,
			byte[] status, boolean includeChildren, boolean includeBinary) {
		Query q = em.createQuery(
				"select ha from SimHomepageActivity ha "
						+ "  where ha.homepageId in (:homepages) "
						+ "    and ha.status in (:status) "
						+ "and ha.parentHpageActvtId is null"
						+ "   order by ha.createdOn desc");
		q.setParameter("status", Arrays.asList(status));
		q.setParameter("homepages", Arrays.asList(homepageIDs));

		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			q.setFirstResult((paging.getCurrentPage() - 1)
					* paging.getPageSize());
			q.setMaxResults(paging.getPageSize());
		}

		PostItemDTO[] postItems = ConverterUtil.SIMHomePageActivity_PostItemDTO(q, includeChildren,
				includeBinary);

		return postItems;
	}

	/**
	 * Get all children of a activity
	 *
	 * @param parentId
	 * @param paging
	 * @param status
	 * @param orderAscending
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public PostItemDTO[] getActivityChildren(String parentId, PagingParams paging, byte[] status,
			boolean orderAscending) {
		return getActivityChildren(parentId, paging, status, orderAscending, null);
	}

	/**
	 * Get Children of a activity in particular category
	 *
	 * @param parentId
	 * @param paging
	 * @param status
	 * @param orderAscending
	 * @param activityCategoryID
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public PostItemDTO[] getActivityChildren(String parentId, PagingParams paging,
			byte[] status, boolean orderAscending, String activityCategoryID) {
		String queryString = "select ha from SimHomepageActivity ha "
				+ "where ha.parentHpageActvtId.id = :parentId "
				+ "and ha.status in (:status)";
		if (activityCategoryID != null) {
			queryString = queryString.concat(" and ha.categoryId = :categoryID");
		}
		if (orderAscending) {
			queryString = queryString.concat(" order by ha.createdOn asc");
		} else {
			queryString = queryString.concat(" order by ha.createdOn desc");
		}
		Query q = em.createQuery(queryString);
		q.setParameter("status", Arrays.asList(status));
		q.setParameter("parentId", parentId);
		if (activityCategoryID != null) {
			q.setParameter("categoryID", activityCategoryID);
		}

		if ((paging != null) && (paging.getCurrentPage() > -1)) {
			q.setFirstResult((paging.getCurrentPage() - 1)
					* paging.getPageSize());
			q.setMaxResults(paging.getPageSize());

		}

		PostItemDTO[] postItems = ConverterUtil.SIMHomePageActivity_PostItemDTO(q, false, false);

		return postItems;
	}

	/**
	 * Get no of children of a activity
	 *
	 * @param parentId
	 * @param status
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public long getChildrenNumber(String parentId, byte[] status) {
		return getChildrenNumber(parentId, status, null);
	}

	/**
	 * Get no of children of a activity in a category
	 * 
	 * @param parentId
	 * @param status
	 * @param activityCategoryID
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public long getChildrenNumber(String parentId, byte[] status, String activityCategoryID) {
		String queryString = "select count(ha) from SimHomepageActivity ha "
				+ "where ha.parentHpageActvtId.id = :parentId "
				+ "and ha.status in (:status)";
		if (activityCategoryID != null) {
			queryString = queryString.concat(" and ha.categoryId = :categoryID");
		}
		Query q = em.createQuery(queryString);
		q.setParameter("status", Arrays.asList(status));
		q.setParameter("parentId", parentId);
		if (activityCategoryID != null) {
			q.setParameter("categoryID", activityCategoryID);
		}

		return ((Long) q.getSingleResult()).longValue();
	}

	/**
	 *
	 * @param homepageID
	 * @param activityCategoryID
	 * @param status
	 * @return
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public PostItemDTO getLastActivityOfType(String homepageID, String activityCategoryID, byte[] status) {
		Query q = em.createQuery(
				"select ha from SimHomepageActivity ha "
						+ "  where ha.homepageId = :homepageID "
						+ "    and ha.status in (:status)"
						+ "    and ha.categoryId = :categoryID "
						+ "order by ha.createdOn desc");
		q.setParameter("categoryID", activityCategoryID);
		q.setParameter("status", Arrays.asList(status));
		q.setParameter("homepageID", homepageID);
		q.setMaxResults(1);

		PostItemDTO[] results = ConverterUtil.SIMHomePageActivity_PostItemDTO(q, false, false);

		return (results != null ? results[0] : null);
	}

	/**
	 * Approve a activity
	 * 
	 * @param homePageActivityID
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void approveActivity(String homePageActivityID) {
		SimHomepageActivity homePageActivity = findActivityByID(homePageActivityID);
		homePageActivity.setStatus(SIMMConstants.HOME_PAGE_ACTIVITY_STATUS_APPROVED);
		em.persist(homePageActivity);
	}

	/**
	 * Update a activity
	 * 
	 * @param pi
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void updateActivity(PostItemDTO pi) throws QSIMMException {
		SimHomepageActivity homepageActivity = findActivityByID(pi.getId());
		homepageActivity.setCategoryId(pi.getCategoryID());
		homepageActivity.setCreatedByUserId(pi.getCreatedByUserID() != null ? pi.getCreatedByUserID() : "system");
		homepageActivity.setCreatedOn(pi.getCreatedOn());
		homepageActivity.setDescription(pi.getDescription());
		homepageActivity.setHomepageId(pi.getHomepageID());
		homepageActivity.setLink(pi.getLink());
		if (pi.getParentHomepageID() != null) {
			homepageActivity.setParentHpageActvtId(
					(SimHomepageActivity) em.find(SimHomepageActivity.class, pi.getParentHomepageID()));
		}
		homepageActivity.setStatus(pi.getStatus());
		homepageActivity.setTitle(pi.getTitle());

		if (pi.getBinContents() != null) {
			homepageActivity.getSimHomepageActivityBins().clear();
			for (int i = 0; i < pi.getBinContents().length; i++) {
				SimHomepageActivityBin shab = new SimHomepageActivityBin();
				shab.setActivityId(homepageActivity);
				shab.setBinData(pi.getBinContents()[i]);
				shab.setBinOrder(i);
				shab.setCreatedOn(System.currentTimeMillis());
				em.persist(shab);
			}
		}

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.SIMM.realtime.JMS.notifications").equals("true"))
		// {
		// SIMMMessage message = new SIMMMessage();
		// message.setType(SIMMMessage.MSGTYPE__ACTIVITY_UPDATED);
		// message.setSrcUserID(pi.getCreatedByUserID());
		// message.setStringProperty(SIMMMessage.PROPERTY__HOMEPAGE_ID,
		// pi.getHomepageID());
		// message.setStringProperty(SIMMMessage.PROPERTY__ACTIVITY_CATEGORY_ID,
		// pi.getCategoryID());
		// message.setStringProperty(SIMMMessage.PROPERTY__ACTIVITY_TITLE,
		// pi.getTitle());
		// if
		// (!org.apache.commons.lang.StringUtils.isEmpty(pi.getDescription())) {
		// message.setBody(pi.getDescription());
		// }
		// message.setStringProperty(SIMMMessage.PRIVATE_USERID,
		// pi.getHomepageID());
		// try {
		// Messenger.post(connectionFactory, notificationTopic, message);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QSIMMException(QSIMMException.CODES.ERR_SIMM_0033,
		// ex.getLocalizedMessage());
		// }
		// }
	}

	/**
	 * delete a activity
	 * 
	 * @param activity
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteActivity(PostItemDTO activity) throws QSIMMException {
		SimHomepageActivity homePageActivity = findActivityByID(activity.getId());

		String parentId = homePageActivity.getParentHpageActvtId() == null ? null
				: homePageActivity.getParentHpageActvtId().getId();
		String parentTitle = homePageActivity.getParentHpageActvtId() == null ? null
				: homePageActivity.getParentHpageActvtId().getTitle();

		em.remove(homePageActivity);

		// Post a notification about the event.
		// if
		// (PropertiesLoaderSingleton.getInstance().getProperty("QlackFuse.SIMM.realtime.JMS.notifications").equals("true"))
		// {
		// SIMMMessage message = new SIMMMessage();
		// message.setType(SIMMMessage.MSGTYPE__ACTIVITY_DELETED);
		// message.setSrcUserID(activity.getSrcUserId());
		// message.setStringProperty(SIMMMessage.PROPERTY__HOMEPAGE_ID,
		// homePageActivity.getHomepageId());
		// message.setStringProperty(SIMMMessage.PROPERTY__ACTIVITY_ID,
		// homePageActivity.getId());
		// message.setStringProperty(SIMMMessage.PROPERTY__PARENT_ACTIVITY_ID,
		// parentId);
		// message.setStringProperty(SIMMMessage.PROPERTY__PARENT_ACTIVITY_TITLE,
		// parentTitle);
		// message.setStringProperty(SIMMMessage.PROPERTY__ACTIVITY_CATEGORY_ID,
		// homePageActivity.getCategoryId());
		// message.setStringProperty(SIMMMessage.PRIVATE_USERID,
		// homePageActivity.getHomepageId());
		// try {
		// // First we publish a message for the owner of the homepage.
		// Messenger.post(connectionFactory, notificationTopic, message);
		// //Then to the creator of the activity if not the same as the owner
		// if
		// (!homePageActivity.getCreatedByUserId().equals(homePageActivity.getHomepageId()))
		// {
		// message.setStringProperty(SIMMMessage.PRIVATE_USERID,
		// homePageActivity.getCreatedByUserId());
		// Messenger.post(connectionFactory, notificationTopic, message);
		// }
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QSIMMException(QSIMMException.CODES.ERR_SIMM_0033,
		// ex.getLocalizedMessage());
		// }
		// }
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public PostItemDTO getActivity(String activityId, boolean includeChildren,
			boolean includeBinary) {
		SimHomepageActivity homePageActivity = findActivityByID(activityId);
		if (homePageActivity == null) {
			return null;
		}

		return ConverterUtil.SIMHomePageActivity_PostItemDTO(homePageActivity, includeChildren,
				includeBinary);
	}

	/**
	 * Find a activity
	 * 
	 * @param activityID
	 * @return
	 */
	private SimHomepageActivity findActivityByID(String activityID) {
		return em.find(SimHomepageActivity.class, activityID);
	}

}
