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
package com.eurodyn.qlack2.fuse.blog.impl;

import com.eurodyn.qlack2.fuse.blog.api.LayoutService;
import com.eurodyn.qlack2.fuse.blog.api.dto.LayoutDTO;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgLayout;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author European Dynamics SA
 */
public class LayoutServiceImplTest {

    private EntityManager em;
    private EntityTransaction tx;
    private LayoutService layoutService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        if (!BlogTests.suiteRunning) {
            BlogTests.init();
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (!BlogTests.suiteRunning) {
            BlogTests.tearDownAfterClass();
        }
    }

    @Before
    public void setUp() throws Exception {

        em = BlogTests.getEm();
        layoutService = BlogTests.getLayoutService();

        EntityTransaction trns = em.getTransaction();
        trns.begin();
        clearBlog();
        clearLayout();

        trns.commit();

        if (tx == null) {
            tx = BlogTests.getEm().getTransaction();
        }
        tx.begin();

    }

    private void clearBlog() {

        em.createQuery("delete from BlgBlog").executeUpdate();
    }

    private void clearLayout() {

        em.createQuery("delete from BlgLayout").executeUpdate();
    }

    @After
    public void tearDown() throws Exception {

        if (! tx.getRollbackOnly()){

            tx.commit();
        }else
            tx.rollback();
    }

    @Test
    public void createLayout() throws Exception {

        LayoutDTO layoutDTO = createLayoutDTO("TestData");
        String layoutId = layoutService.createLayout(layoutDTO);

        BlgLayout layoutFromDB = getLayoutsFromDBByName(layoutDTO.getName()).get(0);

        assertEquals(layoutId, layoutFromDB.getId());
    }

    private LayoutDTO createLayoutDTO(String testData) {

        LayoutDTO layoutDTO = new LayoutDTO();
        layoutDTO.setName(testData);
        layoutDTO.setHome(testData);

        return  layoutDTO;
    }

    @Test
    public void editLayout() throws Exception {

        BlgLayout layout = BlogTestUtil.createAndPersistLayout("TestData");

        LayoutDTO modifiedLayout = createLayoutDTO("TestData1");
        modifiedLayout.setId(layout.getId());

        layoutService.editLayout(modifiedLayout);

        BlgLayout modifiedLayoutInDB = getLayoutsFromDBByName("TestData1").get(0);

        assertEquals(modifiedLayout.getHome(), modifiedLayoutInDB.getHome());

    }

    private void persistLayoutToDB(LayoutDTO layoutDTO) {


        BlgLayout blgLayout = new BlgLayout();
        blgLayout.setHome(layoutDTO.getHome());
        blgLayout.setName(layoutDTO.getName());
        em.persist(blgLayout);
    }

    @Test
    public void deleteLayout() throws Exception {

        LayoutDTO layoutDTO = createLayoutDTO("TestData");
        persistLayoutToDB(layoutDTO);

        BlgLayout layoutInDB = getLayoutsFromDBByName(layoutDTO.getName()).get(0);
        layoutService.deleteLayout(layoutInDB.getId());

        List<BlgLayout> layoutsRemainingInDB = getLayoutsFromDBByName(layoutDTO.getName());
        assertEquals(layoutsRemainingInDB.size(),0);
    }

    @Test
    public void getLayouts() throws Exception {

        List <LayoutDTO> layoutDTOs = createLayoutDTOs();
        persistLayoutsToDB(layoutDTOs);

        List<LayoutDTO> layoutsFromDB = layoutService.getLayouts();

        assertEquals(layoutDTOs.size(), layoutsFromDB.size());

    }

    private void persistLayoutsToDB(List<LayoutDTO> layoutDTOs) {

        for (LayoutDTO layoutDTO: layoutDTOs){
            persistLayoutToDB(layoutDTO);
        }

    }

    private List<LayoutDTO> createLayoutDTOs() {


        List<LayoutDTO> layoutDTOs = new ArrayList<>();
        layoutDTOs.add(createLayoutDTO("TestData"));
        layoutDTOs.add(createLayoutDTO("TestData1"));

        return layoutDTOs;
    }

    @Test
    public void getLayout() throws Exception {

        BlgLayout layout = BlogTestUtil.createAndPersistLayout("TestData");
        LayoutDTO layoutFromDB = layoutService.getLayout(layout.getId());

        assertEquals(layout.getId(), layoutFromDB.getId());
    }

    @Test
    public void getLayoutByName() throws Exception {

        LayoutDTO layoutDTO = createLayoutDTO("TestData");
        persistLayoutToDB(layoutDTO);

        LayoutDTO layout = layoutService.getLayoutByName(layoutDTO.getName());

        BlgLayout layoutFromDB = getLayoutsFromDBByName(layoutDTO.getName()).get(0);

        assertEquals(layoutFromDB.getName(), layoutDTO.getName());
    }

    private List<BlgLayout> getLayoutsFromDBByName(String name){

        return em.createQuery("select layouts from BlgLayout layouts where layouts.name=:name")
                .setParameter("name", name)
                .getResultList();
    }

}