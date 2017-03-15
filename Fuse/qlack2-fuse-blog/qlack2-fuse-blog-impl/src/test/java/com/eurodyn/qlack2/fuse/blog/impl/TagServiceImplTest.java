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

import com.eurodyn.qlack2.fuse.blog.api.TagService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogTagDTO;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgTag;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author European Dynamics SA
 */
public class TagServiceImplTest {

    private EntityTransaction tx;
    private EntityManager em;
    private TagService tagService;

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
        tagService = BlogTests.getTagService();

        EntityTransaction trns = em.getTransaction();
        trns.begin();
        cleanBlgTag();
        em.flush();
        trns.commit();


        if (tx == null)
            tx = em.getTransaction();

        tx.begin();
    }

    private void cleanBlgTag() {

        em.createQuery("delete from BlgTag").executeUpdate();
    }

    @After
    public void tearDown() throws Exception {

        if (tx.getRollbackOnly())
            tx.rollback();
        else
          tx.commit();

    }

    @Test
    public void createTag() throws Exception {

        BlogTagDTO blogTagDTO = createTagDTO("TestData");

        String tagFromDB = tagService.createTag(blogTagDTO);

        BlgTag blgTagFromDB = getBlogTagFromDB(blogTagDTO.getName()).get(0);

        assertEquals(blgTagFromDB.getName(), blogTagDTO.getName());



    }

    private List<BlgTag> getBlogTagFromDB(String tagName) {

       return  em.createQuery("SELECT tag from BlgTag tag where tag.name=:tagName" ).setParameter("tagName", tagName)
               .getResultList();
    }

    private BlogTagDTO createTagDTO(String data) {

        BlogTagDTO blogTagDTO = new BlogTagDTO();
//        blogTagDTO.setId(data);
        blogTagDTO.setName(data);
        blogTagDTO.setDescription(data);
        blogTagDTO.setPosts(0);
        return blogTagDTO;
    }

    @Test
    public void deleteTag() throws Exception {

        BlogTagDTO blogTagDTO = createTagDTO("TestData");
        persistTagToDB(blogTagDTO);

        BlgTag blgTag = getBlogTagFromDB(blogTagDTO.getName()).get(0);
        tagService.deleteTag(blgTag.getId());

        List<BlgTag> blogTagDTOs = getBlogTagFromDB(blogTagDTO.getName());

        assertEquals(0, blogTagDTOs.size());
    }

    private List<BlogTagDTO> getBlogTagsFromDB() {
        return null;
    }

    private void persistTagToDB(BlogTagDTO blogTagDTO) {

        BlgTag blgTag = new BlgTag();
        blgTag.setName(blogTagDTO.getName());
        blgTag.setDescription(blogTagDTO.getDescription());
        em.persist(blgTag);

    }

    @Test
    //Potential Bug, fails in conversion
    public void findTag() throws Exception {

        //this test fails due to a potential bug
        BlogTagDTO blogTagDTO = createTagDTO("TestData");
        persistTagToDB(blogTagDTO);

        BlgTag tagFromDB = getBlogTagFromDB(blogTagDTO.getName()).get(0);
        BlogTagDTO tagFound = tagService.findTag(tagFromDB.getId());

        assertEquals(blogTagDTO.getName(), tagFound.getName());

    }

    @Test
    //Potential Bug
    public void findTagByName() throws Exception {

        //this also fails due to a potential bug
        BlogTagDTO blogTagDTO = createTagDTO("TestData");
        persistTagToDB(blogTagDTO);

        BlogTagDTO tagFromDB = tagService.findTagByName(blogTagDTO.getName());

        assertEquals(tagFromDB.getName(), blogTagDTO.getName());

    }

    @Test
    //Potential Bug
    public void findAllTags() throws Exception {

        //fails due to a poetentail bug while converting tag to TagDTO
        List <BlogTagDTO> blogTagDTOs = createMultipleTags();

        persistTagsToDB(blogTagDTOs);

        List <BlogTagDTO> tagsFromDB = tagService.findAllTags();


        for (BlogTagDTO tag : tagsFromDB){

            assertTrue( hasEquivalentTag(blogTagDTOs, tag.getName()));
        }
    }

    private boolean hasEquivalentTag(List<BlogTagDTO> blogTagDTOs, String name) {

        for (BlogTagDTO tag : blogTagDTOs){

            if (tag.getName() == name)
                return true ;
        }
        return false;
    }


    private void persistTagsToDB(List<BlogTagDTO> blogTagDTOs) {

        for (BlogTagDTO tag : blogTagDTOs){
            persistTagToDB(tag);
        }

    }

    private List<BlogTagDTO> createMultipleTags() {

        List<BlogTagDTO> blogTagDTOs = new ArrayList<BlogTagDTO>();

        blogTagDTOs.add(createTagDTO("TestData1"));
        blogTagDTOs.add(createTagDTO("TestData2"));
        return blogTagDTOs;
    }

}