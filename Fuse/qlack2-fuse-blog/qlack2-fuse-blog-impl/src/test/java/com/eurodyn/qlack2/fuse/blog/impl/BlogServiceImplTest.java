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

import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.blog.api.BlogService;
import com.eurodyn.qlack2.fuse.blog.api.PostService;
import com.eurodyn.qlack2.fuse.blog.api.dto.*;
import com.eurodyn.qlack2.fuse.blog.api.exception.QBlogExists;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgBlog;
import com.eurodyn.qlack2.fuse.blog.impl.model.BlgLayout;
import com.eurodyn.qlack2.fuse.blog.impl.util.ConverterUtil;
import junit.framework.TestCase;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


/**
 * @author European Dynamics SA
 */
public class BlogServiceImplTest {
	private static final Logger LOGGER = Logger.getLogger(BlogServiceImplTest.class.getName());
	private static EntityTransaction tx;
	private BlogService blogService;
	private PostService postService;
	private EntityManager em ;


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
	public void setup() throws Exception {

		blogService = BlogTests.getBlogService();
		postService = BlogTests.getPostService();
		em = BlogTests.getEm();

		EntityTransaction cleanTableTransaction = em.getTransaction();
		cleanTableTransaction.begin();
		cleanBlgBlog();
		cleanBlgLayout();
		cleanTableTransaction.commit();

		if (tx == null) {
			tx = BlogTests.getEm().getTransaction();
		}
		tx.begin();
	}

	@After
	public void tearDown() throws Exception {

		if (tx.getRollbackOnly()){
			tx.rollback();
		}
		else
		tx.commit();
	}

	private void cleanBlgBlog() {

		try{
			em.createQuery("delete from BlgBlog").executeUpdate();
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	private void cleanBlgLayout() {

		try{
			em.createQuery("delete from BlgLayout").executeUpdate();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}



	@Test(expected = QBlogExists.class)
	public void testCreateBlogForExistingBlog(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");
		BlogDTO blogDTO = ConverterUtil.blogToBlogDTO(blog);

		blogService.createBlog(blogDTO);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateBlogForEmptyDTO(){

		BlogDTO blogDTO = new BlogDTO();
		String blog = blogService.createBlog(blogDTO);
	}

	@Test
	public void testCreateBlog(){

		BlogDTO blogDTO =  BlogTestUtil.createBlogDTO("TestData");
		blogService.createBlog(blogDTO);
		List<BlgBlog> blog = fetchBlogForUser(blogDTO.getName());
		Assert.assertTrue(blog.size()>0);
	}

	private List<BlgBlog> fetchBlogForUser(String name) {

		return em.createQuery("select blog from BlgBlog blog where blog.name=:name")
				.setParameter("name", name)
				.getResultList();
	}

	@Test
	public void testDeleteBlog(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");

		blogService.deleteBlog(blog.getId());
		List<BlgBlog> blogList = fetchBlogForUser(blog.getName());
		Assert.assertFalse(blogList.size() > 0);
	}

	@Test
	public void testEditBlog(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");
		BlogDTO blogToBeModified = BlogTestUtil.createBlogDTO("TestData1");
		blogToBeModified.setId(blog.getId());
		blogToBeModified.setLayoutId(blog.getBlgLayoutId().getId());
		blogService.editBlog(blogToBeModified);
		BlgBlog modifiedBlogFromDB = fetchBlogForUser(blogToBeModified.getName()).get(0);
		Assert.assertEquals(modifiedBlogFromDB.getName(), blogToBeModified.getName());
	}

	
	@Test
	//Potential bug
	public void testFindBlogForUser(){

		BlogTestUtil.createAndPersistBlog("TestBlog");
		BlogTestUtil.createAndPersistBlog("SecondTestBlog");
		List<BlogDTO> blogs = blogService.findBlogsForUser("TestBlogUserId");
		Assert.assertEquals(2, blogs.size());
	}

	@Test
	public void testFlagBlog(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");

		FlagBlogDTO flagBlogDTO = createFlagBlogDto("TestData");
		flagBlogDTO.setBlogId("TestBlog");
		blogService.flagBlog(flagBlogDTO);
		List<BlgBlog> fetchedBlog = fetchBlogById(blog.getId());
		TestCase.assertNotNull(fetchedBlog.get(0).getBlgFlags());
	}

	private List<BlgBlog> fetchBlogById(String id) {

		return em.createQuery("select blog from BlgBlog blog where blog.id=:id")
				.setParameter("id",id)
				.getResultList();
	}

	private FlagBlogDTO createFlagBlogDto(String testData) {

		FlagBlogDTO flagBlogDTO = new FlagBlogDTO();
		flagBlogDTO.setDateFlagged(new Date().getTime());
		flagBlogDTO.setFlagDescription(testData);
		flagBlogDTO.setUserId(testData);
		flagBlogDTO.setFlagName(testData);
		flagBlogDTO.setId(testData);
		return flagBlogDTO;
	}

	@Test
	public void testLayoutBlog(){

		BlgLayout layout = BlogTestUtil.createAndPersistLayout("TestLayoutId");
		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");
		blogService.layoutBlog(ConverterUtil.layoutToLayoutDTO(layout), blog.getId());
		Assert.assertEquals(layout, blog.getBlgLayoutId());
	}





	@Test
	public void testManageRssFeed(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");
		blogService.manageRssFeed(blog.getId(), true);
		Assert.assertEquals(true, blog.getRssFeedEnabled());
	}



	@Test
	public void testGetBlogByName(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");
		BlogDTO blogDto = blogService.getBlogByName("TestBlog");
		Assert.assertEquals(blogDto.getName(), blog.getName());
	}

	@Test
	//Potential bug. application code throws exception
	public void testGetTagsForBlog(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");
		BlgBlog blogWithTags = BlogTestUtil.createBlog("testblog");
		BlogTestUtil.addTagsToBlog(blogWithTags);
		BlogTestUtil.persistBlog(blogWithTags);
		List<BlogTagDTO> blogTags = blogService.getTagsForBlog(blogWithTags.getId(), true);
		Assert.assertEquals(blogTags.size(), 0);
	}

	@Test
	public void testGetCategoriesForBlog(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");
		List<BlogCategoryDTO> categoryDTOs = blogService.getCategoriesForBlog(blog.getId());
		Assert.assertEquals(categoryDTOs.size(),0);
	}

	@Test
	//Potential Bug
	public void testGetDashBoardXml(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");

		String dashboardXml = blogService.getDashBoardXml(blog.getId(), new PagingParams(5,1));
		Assert.assertTrue(String.class.isAssignableFrom(dashboardXml.getClass()));
	}

	@Test
	//Potential Bug
	public void testGetDashBoard(){

		BlgBlog blog = BlogTestUtil.createAndPersistBlog("TestBlog");

		BlogDashboardDTO dashboardDTO = blogService.getDashBoard(blog.getId(),new PagingParams(5,1));
		Assert.assertEquals(dashboardDTO.getAllPosts(),0);
		Assert.assertEquals(dashboardDTO.getArchivedPosts(),0);
		Assert.assertEquals(dashboardDTO.getMostCommentedPosts(),0);
		Assert.assertEquals(dashboardDTO.getPublishedPosts(),0);
		Assert.assertEquals(dashboardDTO.getRecentComments().size(), 0);
		Assert.assertEquals(dashboardDTO.getUnpublishedPosts(),0);
	}
}
