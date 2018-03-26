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
package com.eurodyn.qlack2.fuse.aaa.impl;

import com.eurodyn.qlack2.common.util.exception.QDoesNotExistException;
import com.eurodyn.qlack2.fuse.aaa.api.AccountingService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionAttributeDTO;
import com.eurodyn.qlack2.fuse.aaa.api.dto.SessionDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.QSession;
import com.eurodyn.qlack2.fuse.aaa.impl.model.QSessionAttribute;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Session;
import com.eurodyn.qlack2.fuse.aaa.impl.model.SessionAttribute;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.util.ConverterUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides accounting information for the user.
 * For details regarding the functionality offered see the respective interfaces.
 *
 * @author European Dynamics SA
 */
@Transactional
@Singleton
@OsgiServiceProvider(classes = {AccountingService.class})
public class AccountingServiceImpl implements AccountingService {

  // JUL reference.
  private static final Logger LOGGER = Logger.getLogger(AccountingServiceImpl.class.getName());

  @PersistenceContext(unitName = "fuse-aaa")
  private EntityManager em;

  // QuertyDSL helpers.
  private static QSession qSession = QSession.session;
  private static QSessionAttribute qSessionAttribute = QSessionAttribute.sessionAttribute;

  @Override
  public String createSession(SessionDTO session) {
    Session entity = ConverterUtil.sessionDTOToSession(session, em);
    if (entity.getCreatedOn() == 0) {
      entity.setCreatedOn(DateTime.now().getMillis());
    }
    em.persist(entity);
    if (entity.getSessionAttributes() != null) {
      for (SessionAttribute attribute : entity.getSessionAttributes()) {
        em.persist(attribute);
      }
    }
    return entity.getId();
  }


  @Override
  public void terminateSession(String sessionID) {
    Session sessionEntity = Session.find(sessionID, em);
    if (sessionEntity != null) {
      sessionEntity.setTerminatedOn(DateTime.now().getMillis());
    } else {
      LOGGER
        .log(Level.WARNING, "Requested to terminate a session that does not exist, session ID: {0}",
          sessionID);
    }
  }

  @Override
  public void terminateSessionByApplicationSessionId(String applicationSessionId) {
    final Session session = new JPAQueryFactory(em)
      .selectFrom(qSession)
      .where(qSession.applicationSessionId.eq(applicationSessionId))
      .fetchOne();
    if (session != null) {
      terminateSession(session.getId());
    } else {
      throw new QDoesNotExistException(MessageFormat
        .format("Session with application session Id {0} could not be found to be terminated.",
          applicationSessionId));
    }
  }


  @Override
  public SessionDTO getSession(String sessionID) {
    return ConverterUtil.sessionToSessionDTO(Session.find(sessionID, em));
  }


  @Override
  public Long getSessionDuration(String sessionID) {
    Session session = Session.find(sessionID, em);
    if (session.getTerminatedOn() == null) {
      return null;
    }
    return session.getTerminatedOn() - session.getCreatedOn();
  }

  @Override
  public Long getUserLastLogIn(String userID) {
    Query q = em.createQuery("SELECT MAX(s.createdOn) FROM Session s WHERE s.user = :user");
    q.setParameter("user", User.find(userID, em));
    List<Long> queryResult = q.getResultList();
    if (CollectionUtils.isEmpty(queryResult)) {
      return null;
    }
    return queryResult.get(0);
  }

  @Override
  public Long getUserLastLogOut(String userID) {
    Query q = em.createQuery("SELECT MAX(s.terminatedOn) FROM Session s WHERE s.user = :user");
    q.setParameter("user", User.find(userID, em));
    List<Long> queryResult = q.getResultList();
    if (CollectionUtils.isEmpty(queryResult)) {
      return null;
    }
    return queryResult.get(0);
  }

  @Override
  public Long getUserLastLogInDuration(String userID) {
    Query q = em.createQuery("SELECT s FROM Session s WHERE s.user = :user "
      + "AND s.terminatedOn = (SELECT MAX(s.terminatedOn) FROM Session s WHERE s.user = :user)");
    q.setParameter("user", User.find(userID, em));
    List<Session> queryResult = q.getResultList();
    // Also checking the terminatedOn value of the retrieved result in case
    // there is no terminated session and thus the query
    // SELECT MAX(s.terminatedOn) FROM AaaSession s WHERE s.user = :user
    // returns null.
    if ((CollectionUtils.isEmpty(queryResult)) || (queryResult.get(0).getTerminatedOn() == null)) {
      return null;
    }
    Session session = queryResult.get(0);
    return session.getTerminatedOn() - session.getCreatedOn();
  }

  @Override
  public long getNoOfTimesUserLoggedIn(String userID) {
    Query q = em.createQuery("SELECT COUNT(s) FROM Session s WHERE s.user = :user");
    q.setParameter("user", User.find(userID, em));
    return (Long) q.getSingleResult();
  }

  @Override
  public Set<String> filterOnlineUsers(Collection<String> userIDs) {
    Query query = em.createQuery(
      "SELECT DISTINCT s.user.id FROM Session s "
        + "WHERE s.terminatedOn IS NULL "
        + "AND s.user.id in (:userIDs)");
    query.setParameter("userIDs", userIDs);
    return new HashSet<String>(query.getResultList());
  }


  @Override
  public void updateAttribute(SessionAttributeDTO attribute,
    boolean createIfMissing) {
    Collection<SessionAttributeDTO> attributes = new ArrayList<>(1);
    attributes.add(attribute);
    updateAttributes(attributes, createIfMissing);
  }

  @Override
  public void updateAttributes(Collection<SessionAttributeDTO> attributes,
    boolean createIfMissing) {
    for (SessionAttributeDTO attributeDTO : attributes) {
      SessionAttribute attribute = Session.findAttribute(
        attributeDTO.getSessionId(), attributeDTO.getName(), em);
      if ((attribute == null) && createIfMissing) {
        attribute = new SessionAttribute();
        attribute.setName(attributeDTO.getName());
        attribute.setSession(Session.find(attributeDTO.getSessionId(), em));
      }
      attribute.setValue(attributeDTO.getValue());
      em.merge(attribute);
    }
  }

  @Override
  public void deleteAttribute(String sessionID, String attributeName) {
    SessionAttribute attribute = Session.findAttribute(sessionID, attributeName, em);
    em.remove(attribute);
  }

  @Override
  public SessionAttributeDTO getAttribute(String sessionID, String attributeName) {
    return ConverterUtil.sessionAttributeToSessionAttributeDTO(Session
      .findAttribute(sessionID, attributeName, em));
  }

  @Override
  public Set<String> getSessionIDsForAttribute(Collection<String> sessionIDs,
    String attributeName, String attributeValue) {
    String queryString = "SELECT s.id FROM Session s "
      + "JOIN s.sessionAttributes sa "
      + "WHERE sa.name = :attributeName "
      + "AND sa.value = :attributeValue";
    if ((sessionIDs != null) && (sessionIDs.size() > 0)) {
      queryString = queryString.concat(" AND s.id IN (:sessions)");
    }
    Query q = em.createQuery(queryString);
    q.setParameter("attributeName", attributeName);
    q.setParameter("attributeValue", attributeValue);
    if ((sessionIDs != null) && (sessionIDs.size() > 0)) {
      q.setParameter("sessions", sessionIDs);
    }
    return new HashSet<>(q.getResultList());
  }

  @Override
  public boolean isAttributeValueUnique(String userId, String attributeName,
    String attributeValue) {
    long count = new JPAQueryFactory(em)
      .selectFrom(qSessionAttribute)
      .innerJoin(qSessionAttribute.session, qSession)
      .where(qSession.user.id.eq(userId), qSessionAttribute.name.eq(attributeName),
        qSessionAttribute.value.eq(attributeValue))
      .fetchCount();

    return count == 0;
  }

  @Override
  public long deleteOldSessions(long deleteBeforeDate) {
    return new JPAQueryFactory(em)
      .delete(qSession)
      .where(qSession.createdOn.lt(deleteBeforeDate))
      .execute();
  }
}