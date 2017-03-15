
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

import com.eurodyn.qlack2.fuse.aaa.api.ResourceService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;
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

import static org.junit.Assert.assertEquals;

/**
 *
 * @author European Dynamics SA
 */
public class ResourceServiceImplTest {

    private EntityManager em;
    private EntityTransaction tr;
    private ResourceService resourceService;

    @BeforeClass
    public static void beforeClass() throws Exception {

        if (! AllAAATests.suiteRunning){
            AllAAATests.init();
        }
    }

    @Before
    public void setUp() throws Exception {

        em = AllAAATests.getEm();
        resourceService = AllAAATests.getResourceService();

        org.hibernate.Session session = em.unwrap(org.hibernate.Session.class);
        session.clear();
        EntityTransaction sanitizeDB = em.getTransaction();
        sanitizeDB.begin();

        TestDBUtil.cleanTable("Resource");
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
    public void createResource() throws Exception {

        ResourceDTO resourceDTO = TestsUtil.createResourceDTO();
        String resourceId = resourceService.createResource(resourceDTO);

        Resource resourceFromDB = TestDBUtil.fetchSingleResultFromDB("Resource");

        assertEquals(resourceId, resourceFromDB.getId());

    }

    @Test
    public void updateResource() throws Exception {

        Resource resource = TestsUtil.createResource();
        TestDBUtil.persistToDB(resource);

        ResourceDTO modifiedResource = TestsUtil.createResourceDTO();
        TestsUtil.modifyResourceDTO(modifiedResource);

        resourceService.updateResource(modifiedResource);

        Resource resourceFromDB = TestDBUtil.fetchSingleResultFromDB("Resource");

        assertEquals(resourceFromDB.getName(), modifiedResource.getName());
        assertEquals(resourceFromDB.getDescription(), modifiedResource.getDescription());
        assertEquals(resourceFromDB.getObjectId(), modifiedResource.getObjectID());


    }

    @Test(expected = NoResultException.class)
    public void deleteResource() throws Exception {

        Resource resource = TestsUtil.createResource();
        TestDBUtil.persistToDB(resource);

        resourceService.deleteResource(resource.getId());
        TestDBUtil.fetchSingleResultFromDB("Resource");
    }

    @Test
    public void deleteResources() throws Exception {


        Resource firstResource = TestsUtil.createResource();
        Resource secondResource = TestsUtil.createResource();
        TestsUtil.modifyResource(secondResource);

        List<String> resourceIds = new ArrayList<>();
        resourceIds.add(firstResource.getId());
        resourceIds.add(secondResource.getId());

        TestDBUtil.persistToDB(firstResource);
        TestDBUtil.persistToDB(secondResource);

        resourceService.deleteResources(resourceIds);

        List<Resource> resourcesFromDB = TestDBUtil.fetchResultSet("Resource");

        assertEquals(0, resourcesFromDB.size());
    }

    @Test(expected = NoResultException.class)
    public void deleteResourceByObjectId() throws Exception {

        Resource resource = TestsUtil.createResource();
        TestDBUtil.persistToDB(resource);

        resourceService.deleteResourceByObjectId(resource.getObjectId());
        TestDBUtil.fetchSingleResultFromDB("Resource");
    }

    @Test
    public void deleteResourcesByObjectIds() throws Exception {

        Resource firstResource = TestsUtil.createResource();
        Resource secondResource = TestsUtil.createResource();
        TestsUtil.modifyResource(secondResource);

        List<String> objectIds = new ArrayList<>();
        objectIds.add(firstResource.getObjectId());
        objectIds.add(secondResource.getObjectId());

        TestDBUtil.persistToDB(firstResource);
        TestDBUtil.persistToDB(secondResource);

        resourceService.deleteResourcesByObjectIds(objectIds);

        List<Resource> resourcesFromDB = TestDBUtil.fetchResultSet("Resource");

        assertEquals(0, resourcesFromDB.size());
    }

    @Test
    public void getResourceById() throws Exception {

        Resource resource = TestsUtil.createResource();
        TestDBUtil.persistToDB(resource);

        ResourceDTO resourceFromAPP = resourceService.getResourceById(resource.getId());

        assertEquals(resourceFromAPP.getDescription(), resource.getDescription());
        assertEquals(resourceFromAPP.getName(), resource.getName());
        assertEquals(resourceFromAPP.getObjectID(), resource.getObjectId());

    }

    @Test
    public void getResourceByObjectId() throws Exception {

        Resource resource = TestsUtil.createResource();
        TestDBUtil.persistToDB(resource);

        ResourceDTO resourceFromAPP = resourceService.getResourceByObjectId(resource.getObjectId());

        assertEquals(resourceFromAPP.getDescription(), resource.getDescription());
        assertEquals(resourceFromAPP.getName(), resource.getName());
        assertEquals(resourceFromAPP.getObjectID(), resource.getObjectId());
    }

}