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

import com.eurodyn.qlack2.common.util.reflection.ReflectionUtil;
import com.eurodyn.qlack2.fuse.blog.api.*;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Connection;

/**
 * @author European Dynamics SA
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
		com.eurodyn.qlack2.fuse.blog.impl.BlogServiceImplTest.class,
		com.eurodyn.qlack2.fuse.blog.impl.PostServiceImplTest.class,
		com.eurodyn.qlack2.fuse.blog.impl.CategoryServiceImplTest.class,
		com.eurodyn.qlack2.fuse.blog.impl.CommentServiceImplTest.class,
		com.eurodyn.qlack2.fuse.blog.impl.LayoutServiceImplTest.class,
		com.eurodyn.qlack2.fuse.blog.impl.TagServiceImplTest.class
})
public class BlogTests {
	private static EntityManagerFactory emf;
	private static EntityManager em;
	public static Boolean suiteRunning = false;

	public static BlogService getBlogService() {
		return blogService;
	}

	private static BlogService blogService;

	public static PostService getPostService() {
		return postService;
	}

	private static PostService postService;

	private static CommentService commentService;
	private static CategoryService categoryService;

	public static CommentService getCommentService() {
		return commentService;
	}

	public static CategoryService getCategoryService() {
		return categoryService;
	}

	public static LayoutService getLayoutService() {
		return layoutService;
	}

	public static TagService getTagService() {
		return tagService;
	}

	private static LayoutService layoutService;
	private static TagService tagService;
		

	public static void init() throws Exception {
		// Create the Entity Manager Factory.
		emf = Persistence.createEntityManagerFactory("fuse-blog");

		// Run Liquibase.
		em = emf.createEntityManager();
		Session session = (Session) em.getDelegate();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();
		Connection connection = cp.getConnection();
		Database database = DatabaseFactory.getInstance()
				.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		Liquibase liquibase = new Liquibase("db/qlack2-fuse-blog-impl.liquibase.changelog.xml",
				new ClassLoaderResourceAccessor(), database);
		liquibase.update(new Contexts(), new LabelExpression());

		blogService = new BlogServiceImpl();
		postService = new PostServiceImpl();
		categoryService = new CategoryServiceImpl();
		commentService = new CommentServiceImpl();
		layoutService = new LayoutServiceImpl();
		tagService = new TagServiceImpl();

		ReflectionUtil.setPrivateField(blogService, "em", em);
		ReflectionUtil.setPrivateField(blogService, "postService", postService);
		ReflectionUtil.setPrivateField(postService, "em", em);
		ReflectionUtil.setPrivateField(categoryService, "em", em);
		ReflectionUtil.setPrivateField(commentService, "em", em);
		ReflectionUtil.setPrivateField(layoutService, "em", em);
		ReflectionUtil.setPrivateField(tagService, "em", em);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		suiteRunning = true;
		BlogTests.init();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		em.close();
		emf.close();
	}


	/**
	 * @return the em
	 */
	public static EntityManager getEm() {
		return em;
	}


}
