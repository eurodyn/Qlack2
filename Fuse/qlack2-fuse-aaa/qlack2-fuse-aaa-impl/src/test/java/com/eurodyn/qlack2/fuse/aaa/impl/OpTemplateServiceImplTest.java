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

import com.eurodyn.qlack2.fuse.aaa.api.OpTemplateService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplate;
import com.eurodyn.qlack2.fuse.aaa.impl.model.OpTemplateHasOperation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Operation;
import com.eurodyn.qlack2.fuse.aaa.impl.model.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author European Dynamics SA
 */
public class OpTemplateServiceImplTest {

    private OpTemplateService opTemplateService;
    private EntityManager em ;
    private EntityTransaction tr;

    @BeforeClass
    public static void init() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }
    @Before
    public void setUp() throws Exception {

        opTemplateService = AllAAATests.getOpTemplateService();
        em = AllAAATests.getEm();

        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.clear();
        EntityTransaction sanitizeDB = em.getTransaction();
        sanitizeDB.begin();

        TestDBUtil.cleanTable("Operation");
        TestDBUtil.cleanTable("OpTemplate");
        TestDBUtil.cleanTable("Resource");
        TestDBUtil.cleanTable("OpTemplateHasOperation");
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
    public void createTemplate() throws Exception {

        OpTemplateDTO opTemplateDTO = TestsUtil.createOpTemplateDTO();
        String templateId = opTemplateService.createTemplate(opTemplateDTO);

        OpTemplate opTemplate = TestDBUtil.fetchSingleResultFromDB("OpTemplate");

        assertEquals(opTemplateDTO.getName(), opTemplate.getName());
    }

    @Test(expected = NoResultException.class)
    public void deleteTemplateByID() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();

        TestDBUtil.persistToDB(opTemplate);
        opTemplateService.deleteTemplateByID(opTemplate.getId());

        TestDBUtil.fetchSingleResultFromDB("OpTemplate");
    }

    @Test(expected = NoResultException.class)
    public void deleteTemplateByName() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        TestDBUtil.persistToDB(opTemplate);

        opTemplateService.deleteTemplateByName(opTemplate.getName());

        TestDBUtil.fetchSingleResultFromDB("OpTemplate");
    }


    @Test
    public void getTemplateByID() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();

        TestDBUtil.persistToDB(opTemplate);
        OpTemplateDTO templateDTO = opTemplateService.getTemplateByID(opTemplate.getId());

        assertNotNull(templateDTO);
        assertEquals(opTemplate.getName(), templateDTO.getName());
        assertEquals(opTemplate.getDescription(), templateDTO.getDescription());

    }

    @Test
    public void getTemplateByName() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();

        TestDBUtil.persistToDB(opTemplate);
        OpTemplateDTO templateDTO = opTemplateService.getTemplateByName(opTemplate.getName());

        assertNotNull(templateDTO);
        assertEquals(opTemplate.getName(), templateDTO.getName());
        assertEquals(opTemplate.getDescription(), templateDTO.getDescription());

    }

    @Test
    public void addOperation() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        OpTemplateHasOperation opTemplateHasOperation = TestsUtil.createOpTemplateHasOperation();
        Operation operation = TestsUtil.createOperation();

        opTemplateHasOperation.setOperation(operation);
        opTemplateHasOperation.setTemplate(opTemplate);

        List<OpTemplateHasOperation> operationsList = new ArrayList<>();
        operationsList.add(opTemplateHasOperation);
        opTemplate.setOpTemplateHasOperations(operationsList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(opTemplate);
        TestDBUtil.persistToDB(opTemplateHasOperation);

        opTemplateService.addOperation(opTemplate.getId(), operation.getName(), true);

        List<OpTemplateHasOperation> operations = opTemplate.getOpTemplateHasOperations();

        assertTrue(operations.size() >0);
    }

    @Test
    public void addOperation1() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        OpTemplateHasOperation opTemplateHasOperation = TestsUtil.createOpTemplateHasOperation();
        Operation operation = TestsUtil.createOperation();
        Resource resource = TestsUtil.createResource();

        opTemplateHasOperation.setOperation(operation);
        opTemplateHasOperation.setTemplate(opTemplate);
        opTemplateHasOperation.setResource(resource);

        List<OpTemplateHasOperation> operationsList = new ArrayList<>();
        operationsList.add(opTemplateHasOperation);
        opTemplate.setOpTemplateHasOperations(operationsList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(opTemplate);
        TestDBUtil.persistToDB(resource);
        TestDBUtil.persistToDB(opTemplateHasOperation);

        opTemplateService.addOperation(opTemplate.getId(), operation.getName(),resource.getId(), true);

        List<OpTemplateHasOperation> operations = opTemplate.getOpTemplateHasOperations();

        assertTrue(operations.size() >0);
    }

    @Test
    public void removeOperation() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        OpTemplateHasOperation opTemplateHasOperation = TestsUtil.createOpTemplateHasOperation();
        Operation operation = TestsUtil.createOperation();

        opTemplateHasOperation.setOperation(operation);
        opTemplateHasOperation.setTemplate(opTemplate);

        List<OpTemplateHasOperation> operationsList = new ArrayList<>();
        operationsList.add(opTemplateHasOperation);
        opTemplate.setOpTemplateHasOperations(operationsList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(opTemplate);
        TestDBUtil.persistToDB(opTemplateHasOperation);

        opTemplateService.removeOperation(opTemplate.getId(), operation.getName());

        List<OpTemplateHasOperation> operationsFromDB = TestDBUtil.fetchResultSet("OpTemplateHasOperation");

        assertTrue(operationsFromDB.size() ==0);
    }

    @Test
    public void removeOperation1() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        OpTemplateHasOperation opTemplateHasOperation = TestsUtil.createOpTemplateHasOperation();
        Operation operation = TestsUtil.createOperation();
        Resource resource = TestsUtil.createResource();

        opTemplateHasOperation.setOperation(operation);
        opTemplateHasOperation.setTemplate(opTemplate);
        opTemplateHasOperation.setResource(resource);

        List<OpTemplateHasOperation> operationsList = new ArrayList<>();
        operationsList.add(opTemplateHasOperation);
        opTemplate.setOpTemplateHasOperations(operationsList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(opTemplate);
        TestDBUtil.persistToDB(resource);
        TestDBUtil.persistToDB(opTemplateHasOperation);

        opTemplateService.removeOperation(opTemplate.getId(), operation.getName(), resource.getId());

        List<OpTemplateHasOperation> operationsFromDB = TestDBUtil.fetchResultSet("OpTemplateHasOperation");

        assertTrue(operationsFromDB.size() ==0);
    }

    @Test
    public void getOperationAccess() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        OpTemplateHasOperation opTemplateHasOperation = TestsUtil.createOpTemplateHasOperation();
        Operation operation = TestsUtil.createOperation();

        opTemplateHasOperation.setOperation(operation);
        opTemplateHasOperation.setTemplate(opTemplate);
        opTemplateHasOperation.setDeny(true);

        List<OpTemplateHasOperation> operationsList = new ArrayList<>();
        operationsList.add(opTemplateHasOperation);
        opTemplate.setOpTemplateHasOperations(operationsList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(opTemplate);
        TestDBUtil.persistToDB(opTemplateHasOperation);

        assertTrue(opTemplateService.getOperationAccess(opTemplate.getId(), operation.getName()));
    }

    @Test
    public void getOperationAccess1() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        OpTemplateHasOperation opTemplateHasOperation = TestsUtil.createOpTemplateHasOperation();
        Operation operation = TestsUtil.createOperation();
        Resource resource = TestsUtil.createResource();

        opTemplateHasOperation.setOperation(operation);
        opTemplateHasOperation.setTemplate(opTemplate);
        opTemplateHasOperation.setResource(resource);
        opTemplateHasOperation.setDeny(true);

        List<OpTemplateHasOperation> operationsList = new ArrayList<>();
        operationsList.add(opTemplateHasOperation);
        opTemplate.setOpTemplateHasOperations(operationsList);

        TestDBUtil.persistToDB(operation);
        TestDBUtil.persistToDB(opTemplate);
        TestDBUtil.persistToDB(resource);
        TestDBUtil.persistToDB(opTemplateHasOperation);

        boolean opeartionAccessStatus = opTemplateService.getOperationAccess(opTemplate.getId(), operation.getName(), resource.getId());
        assertTrue(opeartionAccessStatus);
    }

    @Test
    public void updateTemplate() throws Exception {

        OpTemplate opTemplate = TestsUtil.createOpTemplate();
        TestDBUtil.persistToDB(opTemplate);

        OpTemplateDTO modifiedDTO = TestsUtil.createOpTemplateDTO();
        TestsUtil.modifyOpTemplateDTO(modifiedDTO);

        opTemplateService.updateTemplate(modifiedDTO);

        OpTemplate opTemplateFromDB = TestDBUtil.fetchSingleResultFromDB("OpTemplate");
        assertEquals(modifiedDTO.getName(), opTemplateFromDB.getName());
        assertEquals(modifiedDTO.getDescription(), opTemplateFromDB.getDescription());
    }

}