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

import com.eurodyn.qlack2.fuse.aaa.api.VerificationService;
import com.eurodyn.qlack2.fuse.aaa.impl.model.VerificationToken;
import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author European Dynamics SA
 */
public class VerificationServiceImplTest {

    private EntityManager em;
    private EntityTransaction tr;
    private VerificationService verificationService;

    @BeforeClass
    public static void beforeClass() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }

    @Before
    public void setUp() throws Exception {

        em = AllAAATests.getEm();
        verificationService = AllAAATests.getVerificationService();

        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.clear();
        EntityTransaction sanitizeDB = em.getTransaction();
        sanitizeDB.begin();

        TestDBUtil.cleanTable("VerificationToken");
        sanitizeDB.commit();

        if (tr == null){
            tr = em.getTransaction();
        }
        tr.begin();
    }

    @After
    public void tearDown() throws Exception {

        if (tr.getRollbackOnly()){
            tr.rollback();
        }
        else
            tr.commit();
    }

    @Test
    public void createVerificationToken() throws Exception {

        String verificationTokenId = verificationService.createVerificationToken(TestConstants.VERIFICATION_EXPIRES_ON);

        VerificationToken tokenFromDB = TestDBUtil.fetchSingleResultFromDB("VerificationToken");
        assertEquals(verificationTokenId, tokenFromDB.getId());

    }

    @Test
    public void createVerificationToken1() throws Exception {

        String verificationTokenId = verificationService.createVerificationToken(TestConstants.VERIFICATION_EXPIRES_ON, TestConstants.VERIFICATION_DATA);
        VerificationToken tokenFromDB = TestDBUtil.fetchSingleResultFromDB("VerificationToken");

        assertEquals(verificationTokenId, tokenFromDB.getId());
        assertEquals(tokenFromDB.getData(), TestConstants.VERIFICATION_DATA);

    }

    @Test
    public void verifyToken() throws Exception {

        VerificationToken token = TestsUtil.createVerificationToken();
        token.setExpiresOn(Instant.now().getMillis()*2);

        TestDBUtil.persistToDB(token);
        boolean verification = verificationService.verifyToken(token.getId());

        assertTrue(verification);

    }

    @Test(expected = NoResultException.class)
    public void deleteToken() throws Exception {

        VerificationToken token = TestsUtil.createVerificationToken();

        TestDBUtil.persistToDB(token);
        verificationService.deleteToken(token.getId());

        VerificationToken tokenFromDB = TestDBUtil.fetchSingleResultFromDB("VerificationToken");
    }

}