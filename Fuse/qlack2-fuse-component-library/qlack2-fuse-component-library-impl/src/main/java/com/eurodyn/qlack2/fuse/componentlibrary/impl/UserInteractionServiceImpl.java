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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.eurodyn.qlack2.fuse.componentlibrary.api.DirectoryService;
import com.eurodyn.qlack2.fuse.componentlibrary.api.UserInteractionService;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadget;
import com.eurodyn.qlack2.fuse.componentlibrary.impl.model.ApiGadgetHasUser;

/**
 * A Stateless Session EJB providing services to manage the end-user interacting
 * with a gadget. For details regarding the functionality offered see the
 * respective interfaces.
 *
 * @author European Dynamics SA
 */
@Transactional
public class UserInteractionServiceImpl implements UserInteractionService {
	public static final Logger LOGGER = Logger.getLogger(UserInteractionServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-componentlibrary")
	private EntityManager em;
	private DirectoryService gadgetDirectoryService;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setGadgetDirectoryService(DirectoryService gadgetDirectoryService) {
		this.gadgetDirectoryService = gadgetDirectoryService;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void addGadgetToHomepage(String gadgetID, String userID, boolean notification) {
		LOGGER.log(Level.FINEST, "Adding gadget {0} to user {1}.", new String[] { gadgetID, userID });

		// Check if the particular gadget has not been already added to the
		// user's homepage.
		Query q = em
				.createQuery("SELECT a FROM ApiGadgetHasUser a WHERE a.gadget.id = :gadgetId AND a.userId = :userId");
		q.setParameter("gadgetId", gadgetID);
		q.setParameter("userId", userID);
		List queryResult = q.getResultList();

		if (queryResult != null && queryResult.size() > 0) {
		} else {
			ApiGadget gadget = (ApiGadget) em.find(ApiGadget.class, gadgetID);
			ApiGadgetHasUser aghu = new ApiGadgetHasUser();
			aghu.setGadget(gadget);
			aghu.setState(Byte.valueOf("1"));
			aghu.setUserId(userID);
			em.persist(aghu);

			// Reorder gadgets.
			reorderGadgets(gadgetDirectoryService.getGadgetIDsForUserID(userID), userID);

			// Let the gadget know it was added.
			// if (notification) {
			// Map objectData = new HashMap();
			// objectData.put("action", "http_call");
			// objectData.put("url",
			// gadget.getConfigPage() + "?gadgetAdded=" + aghu.getId() +
			// "&gadgetSecretKey=" + gadget.getPrivateKey());
			// try {
			// sendJMSMessageToQlackAPIQueue(objectData);
			// } catch (JMSException ex) {
			// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			// throw new QlackFuseAPIException(CODES.ERR_API_0004,
			// ex.getLocalizedMessage());
			// }
			// }
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 * @return String {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public String removeGadgetFromHomepage(String gadgetID, String userID, boolean notification) {
		String retVal = null;
		LOGGER.log(Level.FINEST, "Removing gadget {0} from user {1}.", new String[] { gadgetID, userID });

		// Remove the gadget from the home/group page.
		Query q = em.createQuery(
				"select g from ApiGadgetHasUser g where g.userId = :userID and g.gadget = :gadget");
		q.setParameter("userID", userID);
		q.setParameter("gadget", (ApiGadget) em.find(ApiGadget.class, gadgetID));
		ApiGadgetHasUser ghu = (ApiGadgetHasUser) q.getSingleResult();
		retVal = ghu.getId();
		String configURL = ghu.getGadget().getConfigPage();
		String secretKey = ghu.getGadget().getPrivateKey();
		em.remove(ghu);

		// Remove any privileges previously granted to this gadget.
		q = em.createQuery("delete from ApiGadgetHasPermission ghp " +
				" where ghp.gadget.id = :gadgetID "
				+ " and ghp.userId = :userID");
		q.setParameter("gadgetID", gadgetID);
		q.setParameter("userID", userID);
		q.executeUpdate();

		// Let the gadget know it was removed.
		// if (notification) {
		// Map objectData = new HashMap();
		// objectData.put("action", "http_call");
		// objectData.put("url",
		// configURL + "?gadgetRemoved=" + retVal + "&gadgetSecretKey=" +
		// secretKey);
		// try {
		// sendJMSMessageToQlackAPIQueue(objectData);
		// } catch (JMSException ex) {
		// LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		// throw new QlackFuseAPIException(CODES.ERR_API_0004,
		// ex.getLocalizedMessage());
		// }
		// }

		// Reorder gadgets.
		reorderGadgets(gadgetDirectoryService.getGadgetIDsForUserID(userID), userID);

		return retVal;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 * @param groupID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void addGadgetToGroupPage(String gadgetID, String groupID, boolean notification) {
		LOGGER.log(Level.FINEST, "Adding gadget {0} to group {1}. This call is delegated.",
				new String[] { gadgetID, groupID });
		addGadgetToHomepage(gadgetID, groupID, notification);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 * @param groupID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void removeGadgetFromGroupPage(String gadgetID, String groupID, boolean notification) {
		LOGGER.log(Level.FINEST, "Removing gadget {0} from group {1}. This call is delegated",
				new String[] { gadgetID, groupID });
		removeGadgetFromHomepage(gadgetID, groupID, notification);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param list
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void reorderGadgets(List<String> list, String userID) {
		Query q = em.createQuery("select ghu from ApiGadgetHasUser ghu "
				+ "where ghu.gadget.id = :gadgetID "
				+ "  and ghu.userId = :userID");
		q.setParameter("userID", userID);
		for (int i = 0; i < list.size(); i++) {
			q.setParameter("gadgetID", list.get(i));
			((ApiGadgetHasUser) q.getSingleResult()).setDisplayOrder((byte) i);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param gadgetID
	 *            {@inheritDoc}
	 * @param state
	 *            {@inheritDoc}
	 * @param userID
	 *            {@inheritDoc}
	 */
	@Override
	@Transactional(TxType.REQUIRED)
	public void setState(String gadgetID, int state, String userID) {
		Query q = em.createQuery("select ghu from ApiGadgetHasUser ghu "
				+ "where ghu.gadget.id = :gadgetID "
				+ "  and ghu.userId = :userID");
		q.setParameter("userID", userID);
		q.setParameter("gadgetID", gadgetID);
		((ApiGadgetHasUser) q.getSingleResult()).setState((byte) state);
	}

	// private Message createJMSMessageForjmsQlackAPIQueue(Session session,
	// Object messageData) throws JMSException {
	// ObjectMessage om = session.createObjectMessage();
	// om.setObject((HashMap)messageData);
	//
	// return om;
	// }

	// private void sendJMSMessageToQlackAPIQueue(Object messageData) throws
	// JMSException {
	// Connection connection = null;
	// Session session = null;
	// try {
	// connection = QlackConnectionFactory.createConnection();
	// session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	// MessageProducer messageProducer = session.createProducer(QlackAPIQueue);
	// messageProducer.send(createJMSMessageForjmsQlackAPIQueue(session,
	// messageData));
	// } finally {
	// if (session != null) {
	// try {
	// session.close();
	// } catch (JMSException e) {
	// Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot
	// close session", e);
	// }
	// }
	// if (connection != null) {
	// connection.close();
	// }
	// }
	// }

}
