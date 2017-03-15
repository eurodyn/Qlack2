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

import com.eurodyn.qlack2.fuse.aaa.api.JSONConfigService;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Application;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author European Dynamics SA
 */
public class JSONConfigServiceImplTest {

    private JSONConfigService jsonConfigService;
    private EntityManager em;
    private EntityTransaction tr;

    @BeforeClass
    public static void beforeClass() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }
    @Before
    public void setUp() throws Exception {

        em = AllAAATests.getEm();

        if (tr == null){
            tr = em.getTransaction();
        }
        tr.begin();

        jsonConfigService = AllAAATests.getJsonConfigService();
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
    public void parseConfig() throws Exception {


        long recordCount = TestDBUtil.fetchRecordCount("Application");
        assertEquals(0, recordCount);

        jsonConfigService.parseConfig("test", Paths.get(TestConstants.JSON_CONFIG_FILE).toUri().toURL());

        Application application = TestDBUtil.fetchSingleResultFromDB("Application");
        long recordCountAfterParsing = TestDBUtil.fetchRecordCount("Application");

        assertEquals(1, recordCountAfterParsing);
        assertEquals("test", application.getSymbolicName());
    }

}