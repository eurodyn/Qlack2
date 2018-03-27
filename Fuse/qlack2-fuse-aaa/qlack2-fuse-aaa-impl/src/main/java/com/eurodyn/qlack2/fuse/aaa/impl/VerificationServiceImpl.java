package com.eurodyn.qlack2.fuse.aaa.impl;

import com.eurodyn.qlack2.fuse.aaa.api.VerificationService;
import com.eurodyn.qlack2.fuse.aaa.impl.model.QVerificationToken;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;
import com.eurodyn.qlack2.fuse.aaa.impl.model.VerificationToken;
import com.querydsl.jpa.impl.JPAQueryFactory;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.joda.time.Instant;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.util.UUID;

@Singleton
@Transactional
@OsgiServiceProvider(classes = {VerificationService.class})
public class VerificationServiceImpl implements VerificationService {

  @PersistenceContext(unitName = "fuse-aaa")
  private EntityManager em;

  @Override
  public String createVerificationToken(String userId, long expiresOn) {
    return createVerificationToken(userId, expiresOn, null);
  }

  @Override
  public String createVerificationToken(String userId, long expiresOn, String data) {
    VerificationToken vt = new VerificationToken();
    vt.setUser(User.find(userId, em));
    vt.setCreatedOn(Instant.now().getMillis());
    if (data != null) {
      vt.setData(data);
    }
    vt.setExpiresOn(expiresOn);
    vt.setId(UUID.randomUUID().toString());
    em.persist(vt);

    return vt.getId();
  }

  @Override
  public String verifyToken(String tokenID) {
    String userId = null;
    VerificationToken vt = em.find(VerificationToken.class, tokenID);
    if (vt != null && vt.getExpiresOn() >= Instant.now().getMillis()) {
      userId = vt.getUser().getId();
    }

    return userId;
  }

  @Override
  public void deleteToken(String tokenID) {
    VerificationToken vt = em.find(VerificationToken.class, tokenID);
    if (vt != null) {
      em.remove(vt);
    }

    // Each time a token is deleted perform some housekeeping to also delete any other expired
    // tokens.
    QVerificationToken qvt = QVerificationToken.verificationToken;
    new JPAQueryFactory(em).delete(qvt)
      .where(qvt.expiresOn.lt(Instant.now().getMillis())).execute();
  }

  @Override
  public String getTokenPayload(String tokenID) {
    VerificationToken vt = em.find(VerificationToken.class, tokenID);
    if (vt != null) {
      return vt.getData();
    } else {
      return null;
    }
  }

  @Override
  public String getTokenUser(String tokenID) {
    String userId = null;

    VerificationToken vt = em.find(VerificationToken.class, tokenID);
    if (vt != null) {
      userId = vt.getUser().getId();
    }

    return userId;
  }

}
