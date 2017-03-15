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
package com.eurodyn.qlack2.fuse.forum.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.forum.api.ForumService;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumConstants;
import com.eurodyn.qlack2.fuse.forum.api.dto.ForumDTO;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumException;
import com.eurodyn.qlack2.fuse.forum.api.exception.QForumTitleExists;
import com.eurodyn.qlack2.fuse.forum.api.exception.QInvalidModeration;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmForum;
import com.eurodyn.qlack2.fuse.forum.impl.model.FrmTopic;
import com.eurodyn.qlack2.fuse.forum.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.forum.impl.util.LookupHelper;

/**
 * A Stateless Session EJB providing services to manage a forum. For details
 * regarding the functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA
 */
@Transactional
public class ForumServiceImpl implements ForumService {
	public static final Logger LOGGER = Logger.getLogger(ForumServiceImpl.class
			.getName());
	// The persistence context for database operations.
	@PersistenceContext(unitName = "fuse-forum")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forum
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public ForumDTO createForum(ForumDTO forum) throws QForumException {
		LOGGER.log(Level.FINEST, "Creating a new forum with title {0}",
				forum.getTitle());

		// Check if forum title is unique
		Query q = em
				.createQuery("SELECT f FROM FrmForum f  WHERE f.title = :title");
		q.setParameter("title", forum.getTitle());
		List resultList = q.getResultList();
		if (!resultList.isEmpty()) {
			throw new QForumTitleExists("A forum with the title "
					+ forum.getTitle() + " already exists.");
		}

		// Create forum
		forum.setCreatedOn(System.currentTimeMillis());
		forum.setStatus(ForumConstants.FORUM_STATUS_UNLOCKED);
		if (forum.getModerated() == null) {
			forum.setModerated(ForumConstants.FORUM_SUPPORTS_MODERATION);
		}
		forum.setArchived(false);
		FrmForum forumEntity = ConverterUtil.convert2ForumModel(forum);
		em.persist(forumEntity);

		forum.setId(forumEntity.getId());

		return forum;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forum
	 *            {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void updateForum(ForumDTO forum) throws QForumException {
		LOGGER.log(Level.FINEST, "Updating forum with id {0}", forum.getId());

		FrmForum forumEntity = LookupHelper.retrieveForum(forum.getId(), em);
		LookupHelper.checkForumStatus(forumEntity.getStatus());
		LookupHelper.checkForumArchived(forumEntity.isArchived());

		if (forum.getTitle() != null) {
			Query query = em
					.createQuery("SELECT f FROM FrmForum f WHERE f.title = :title");
			query.setParameter("title", forum.getTitle());
			List<FrmForum> queryResult = query.getResultList();
			if (!queryResult.isEmpty()) {
				FrmForum retrievedForum = queryResult.get(0);
				if (!retrievedForum.getId().equals(forum.getId())) {
					throw new QForumTitleExists("A forum with the title "
							+ forum.getTitle() + " already exists.");
				}
			}
			forumEntity.setTitle(forum.getTitle());
		}
		if (forum.getDescription() != null) {
			forumEntity.setDescription(forum.getDescription());
		}
		if (forum.getLogo() != null) {
			forumEntity.setLogo(forum.getLogo());
		}
		if (forum.getModerated() != null) {
			// Do not allow the moderated property to be changed to
			// not-moderated
			// if there are pending messages
			if (forum.getModerated() == ForumConstants.FORUM_NOT_MODERATED) {
				Query q = em
						.createQuery("SELECT COUNT(m) FROM FrmMessage m "
								+ "WHERE m.frmTopicId.frmForumId.id = :forumId AND m.moderationStatus = :status");
				q.setParameter("forumId", forum.getId());
				q.setParameter("status",
						ForumConstants.MODERATION_STATUS_PENDING);
				long pendingMessages = (Long) q.getSingleResult();
				if (pendingMessages > 0) {
					throw new QInvalidModeration(
							"The forum has "
									+ "pending messages; it's status cannot change to not-moderated");
				}
			}

			forumEntity.setModerated(forum.getModerated());
			// If the moderated property was changed to moderated or
			// not-moderated
			// also update the topics' moderated property
			if ((forum.getModerated() == ForumConstants.FORUM_NOT_MODERATED)
					|| (forum.getModerated() == ForumConstants.FORUM_MODERATED)) {
				Set<FrmTopic> topics = forumEntity.getFrmTopics();
				for (FrmTopic topic : topics) {
					if (forum.getModerated() == ForumConstants.FORUM_NOT_MODERATED) {
						topic.setModerated(false);
					} else {
						topic.setModerated(true);
					}
					em.merge(topic);
				}
			}
		}
		em.merge(forumEntity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteForum(String forumId) throws QForumException {
		LOGGER.log(Level.FINEST, "Deleting forum with id {0}", forumId);

		FrmForum forumEntity = LookupHelper.retrieveForum(forumId, em);
		em.remove(forumEntity);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @param statistics
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public ForumDTO getForumById(String forumId, boolean statistics)
			throws QForumException {
		// Get the information about the forum.
		FrmForum forumEntity = em.find(FrmForum.class, forumId);
		if (forumEntity == null) {
			return null;
		}
		ForumDTO forum = ConverterUtil.convert2ForumDTO(forumEntity);

		// Calculate message & topic statistics if requested to do so.
		if (statistics) {
			// Accepted messages
			Query q = em.createQuery("SELECT COUNT(m) FROM FrmMessage m "
					+ "WHERE m.frmTopicId.frmForumId.id = :forumId "
					+ "  and m.moderationStatus = :status");
			q.setParameter("forumId", forumId);
			q.setParameter("status", ForumConstants.MODERATION_STATUS_ACCEPTED);
			forum.setMessagesAccepted((Long) q.getSingleResult());

			// Rejected messages
			q.setParameter("status", ForumConstants.MODERATION_STATUS_REJECTED);
			forum.setMessagesRejected((Long) q.getSingleResult());

			// Pending messages
			q.setParameter("status", ForumConstants.MODERATION_STATUS_PENDING);
			forum.setMessagesPending((Long) q.getSingleResult());

			// Accepted topics
			q = em.createQuery("SELECT COUNT(t) FROM FrmTopic t "
					+ "WHERE t.frmForumId.id = :forumId "
					+ "  and t.moderationStatus = :status");
			q.setParameter("forumId", forumId);
			q.setParameter("status", ForumConstants.MODERATION_STATUS_ACCEPTED);
			forum.setTopicsAccepted((Long) q.getSingleResult());

			// Rejected topics
			q.setParameter("status", ForumConstants.MODERATION_STATUS_REJECTED);
			forum.setTopicsRejected((Long) q.getSingleResult());

			// Pending topics
			q.setParameter("status", ForumConstants.MODERATION_STATUS_PENDING);
			forum.setTopicsPending((Long) q.getSingleResult());
		}

		return forum;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param includeLocked
	 *            {@inheritDoc}
	 * @param archived
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<ForumDTO> listForums(boolean includeLocked, Boolean archived) {
		LOGGER.log(
				Level.FINEST,
				"Retrieving forums without paging params, "
						+ "includeLocked = {0}, archived = {1} ",
				new String[] { String.valueOf(includeLocked),
						String.valueOf(archived) });

		return listForums(includeLocked, archived, null);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param includeLocked
	 *            {@inheritDoc}
	 * @param archived
	 *            {@inheritDoc}
	 * @param pagingParams
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public List<ForumDTO> listForums(boolean includeLocked, Boolean archived,
			PagingParams pagingParams) {
		LOGGER.log(
				Level.FINEST,
				"Retrieving forums with paging params, "
						+ "includeLocked = {0}, archived = {1} ",
				new String[] { String.valueOf(includeLocked),
						String.valueOf(archived) });

		List<ForumDTO> resultList = new ArrayList();

		String queryString = "SELECT f FROM FrmForum f";
		if (!includeLocked) {
			queryString = queryString.concat(" WHERE f.status <> :status");
		}
		if (archived != null) {
			if (!includeLocked) {
				queryString = queryString.concat(" AND");
			} else {
				queryString = queryString.concat(" WHERE");
			}
			queryString = queryString.concat(" f.archived = :archived");
		}
		Query query = em.createQuery(queryString);
		if (!includeLocked) {
			query.setParameter("status", ForumConstants.FORUM_STATUS_LOCKED);
		}
		if (archived != null) {
			query.setParameter("archived", archived);
		}
		if ((pagingParams != null) && (pagingParams.getCurrentPage() > -1)) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1)
					* pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}
		List<FrmForum> queryResults = query.getResultList();

		for (FrmForum forum : queryResults) {
			resultList.add(ConverterUtil.convert2ForumDTO(forum));
		}

		return resultList;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean lockForum(String forumId) throws QForumException {
		LOGGER.log(Level.FINEST, "Locking forum with id {0}", forumId);

		FrmForum forumEntity = LookupHelper.retrieveForum(forumId, em);
		if (forumEntity.getStatus() == ForumConstants.FORUM_STATUS_UNLOCKED) {
			forumEntity.setStatus(ForumConstants.FORUM_STATUS_LOCKED);
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean unlockForum(String forumId) throws QForumException {
		LOGGER.log(Level.FINEST, "Unlocking forum with id {0}", forumId);

		FrmForum forumEntity = LookupHelper.retrieveForum(forumId, em);
		if (forumEntity.getStatus() == ForumConstants.FORUM_STATUS_LOCKED) {
			forumEntity.setStatus(ForumConstants.FORUM_STATUS_UNLOCKED);
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public short getForumStatus(String forumId) throws QForumException {
		LOGGER.log(Level.FINEST, "Retrieving the status of forum with id {0}",
				forumId);

		FrmForum forumEntity = LookupHelper.retrieveForum(forumId, em);

		return forumEntity.getStatus();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean archiveForum(String forumId) throws QForumException {
		LOGGER.log(Level.FINEST, "Archiving forum with id {0}", forumId);

		FrmForum forumEntity = LookupHelper.retrieveForum(forumId, em);
		if (!forumEntity.isArchived()) {
			forumEntity.setArchived(true);
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param forumId
	 *            {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QForumException
	 *             {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public boolean unarchiveForum(String forumId) throws QForumException {
		LOGGER.log(Level.FINEST, "Unarchiving forum with id {0}", forumId);

		FrmForum forumEntity = LookupHelper.retrieveForum(forumId, em);
		if (forumEntity.isArchived()) {
			forumEntity.setArchived(false);
			return true;
		}

		return false;
	}

}
