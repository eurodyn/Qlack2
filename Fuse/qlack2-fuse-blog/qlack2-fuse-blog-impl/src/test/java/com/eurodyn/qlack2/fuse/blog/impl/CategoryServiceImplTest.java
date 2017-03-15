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

import com.eurodyn.qlack2.fuse.blog.api.CategoryService;
import com.eurodyn.qlack2.fuse.blog.api.dto.BlogCategoryDTO;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgCategory;
import com.google.common.collect.Sets;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.eurodyn.qlack2.fuse.blog.impl.BlogTestUtil.createAndPersistBlog;
import static com.eurodyn.qlack2.fuse.blog.impl.BlogTestUtil.createBlogCategoriesDTOs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author European Dynamics SA
 */

public class CategoryServiceImplTest {

    private CategoryService categoryService;
    private EntityManager em;
    private EntityTransaction tx;

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

        categoryService = BlogTests.getCategoryService();
        em = BlogTests.getEm();

        EntityTransaction trns = em.getTransaction();
        trns.begin();
        cleanBlog();
        cleanLayout();
        cleanBlgCategory();
        if (!trns.getRollbackOnly())
            trns.commit();
        else
            trns.rollback();
        if (tx == null) {
            tx = BlogTests.getEm().getTransaction();
        }
        tx.begin();
    }

    private void cleanBlgCategory() {

        em.createQuery("delete from BlgCategory").executeUpdate();
    }

    private void cleanLayout() {

        em.createQuery("delete from BlgLayout").executeUpdate();
    }

    private void cleanBlog() {

        em.createQuery("delete from BlgBlog").executeUpdate();
    }

    @After
    public void tearDown() throws Exception {

        if (tx.getRollbackOnly())
            tx.rollback();
        else
            tx.commit();
    }

    @Test
    public void createCategory() throws Exception {

        BlogCategoryDTO blogCategoryDTO = BlogTestUtil.createBlogCategoryDTO("TestData");
        BlgBlog blog = createAndPersistBlog("TestBlog");
        blogCategoryDTO.setBlogId(blog.getId());

        String category = categoryService.createCategory(blogCategoryDTO);

        boolean isCategoryPresent = findCategoryById(category).size() > 0;
        assertTrue(isCategoryPresent);
    }

    private List<BlgCategory> findCategoryById(String categoryId) {

        return em.createQuery("select Blc from BlgCategory Blc where Blc.id=:categoryId")
                .setParameter("categoryId", categoryId)
                .getResultList();
    }


    @Test
    public void createCategories() throws Exception {

        List<BlogCategoryDTO> blogCategoryDTOList = createBlogCategoriesDTOs();
        categoryService.createCategories(blogCategoryDTOList);
        List<BlgCategory> categoriesList = findAllCategories();
        assertTrue(isAllCategoryInDB(blogCategoryDTOList, categoriesList));
    }

    private boolean isAllCategoryInDB(List<BlogCategoryDTO> blogCategoryDTOList, List<BlgCategory> categoriesList) {

        Set<String> dtoIds = blogCategoryDTOList.stream().map(category -> category.getName())
                .collect(Collectors.toSet());
        Set<String> categoryIds = categoriesList.stream().map(category -> category.getName())
                .collect(Collectors.toSet());
        Set<String> commonElements = Sets.intersection(dtoIds, categoryIds);

        return (blogCategoryDTOList.size() == commonElements.size());
    }

    private List<BlgCategory> findAllCategories() {

        return em.createQuery("SELECT blgc from BlgCategory blgc")
                .getResultList();
    }


    @Test
    public void editCategory() throws Exception {

        BlogCategoryDTO blogCategoryDTO = BlogTestUtil.createBlogCategoryDTO("TestData");
        BlgCategory blogCategory = BlogTestUtil.persistBlogCategory(blogCategoryDTO);
        BlogCategoryDTO modifiedDTO = BlogTestUtil.createBlogCategoryDTO("ModifiedDTO");
        modifiedDTO.setId(blogCategory.getId());
        modifiedDTO.setBlogId(blogCategory.getBlogId().getId());
        categoryService.editCategory(modifiedDTO);
        BlgCategory categoryFromDB = fetchCategoryByName(modifiedDTO.getName()).get(0);
        assertEquals(categoryFromDB.getName(), modifiedDTO.getName());
    }

    private List<BlgCategory> fetchCategoryByName(String name) {

        return em.createQuery("select blg from BlgCategory blg where blg.name=:name")
                .setParameter("name", name)
                .getResultList();
    }

    private List<BlgCategory> fetchCategoryById(String id) {

        return em.createQuery("select blg from BlgCategory blg where blg.id=:id")
                .setParameter("id", id)
                .getResultList();
    }

    @Test
    public void editCategories() throws Exception {

        BlogCategoryDTO blogCategoryDTO = BlogTestUtil.createBlogCategoryDTO("TestData");
        BlogCategoryDTO secondBlogCategoryDTO = BlogTestUtil.createBlogCategoryDTO("TestData2");
        BlogTestUtil.modifyBlogCategoryDTO(secondBlogCategoryDTO);
        BlgCategory blogCategory = BlogTestUtil.persistBlogCategory(blogCategoryDTO);
        BlgCategory secondBlogCategory = BlogTestUtil.persistBlogCategory(secondBlogCategoryDTO);
        BlogCategoryDTO modifiedDTO = BlogTestUtil.createBlogCategoryDTO("ModifiedDTO");
        BlogCategoryDTO secondModifiedDTO = BlogTestUtil.createBlogCategoryDTO("SecondModifiedDTO");
        modifiedDTO.setId(blogCategory.getId());
        modifiedDTO.setBlogId(blogCategory.getBlogId().getId());
        secondModifiedDTO.setId(secondBlogCategory.getId());
        secondModifiedDTO.setBlogId(secondBlogCategory.getBlogId().getId());
        List<BlogCategoryDTO> modifiedBlogs = new ArrayList<>();
        modifiedBlogs.add(modifiedDTO);
        modifiedBlogs.add(secondModifiedDTO);
        categoryService.editCategories(modifiedBlogs);
        List<BlgCategory> categoriesFromDB = fetchDBCategories();
        assertTrue(isAllCategoryInDB(modifiedBlogs, categoriesFromDB));
    }

    private List<BlgCategory> fetchDBCategories() {

        return em.createQuery("select blgc from BlgCategory blgc")
                .getResultList();
    }

    @Test
    public void deleteCategory() throws Exception {

        BlogCategoryDTO blogCategoryDTO = BlogTestUtil.createBlogCategoryDTO("TestData");
        BlgCategory blgCategory = BlogTestUtil.persistBlogCategory(blogCategoryDTO);
        categoryService.deleteCategory(blgCategory.getId());
        List<BlgCategory> blgCategoryList = fetchCategoryByName(blogCategoryDTO.getName());
        assertEquals(0, blgCategoryList.size());
    }


    @Test
    //Potential bug throws NPE
    public void findCategory() throws Exception {

        BlogCategoryDTO blogCategoryDTO = BlogTestUtil.createBlogCategoryDTO("TestData");
        BlgCategory blgCategory = BlogTestUtil.persistBlogCategory(blogCategoryDTO);
        blogCategoryDTO.setId(blgCategory.getId());
        BlogCategoryDTO blogCategoryInDB = categoryService.findCategory(blogCategoryDTO.getId());
        Assert.assertNotNull(blogCategoryInDB);
    }

}