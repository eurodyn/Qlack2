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

import com.eurodyn.qlack2.common.util.reflection.ReflectionUtil;
import com.eurodyn.qlack2.fuse.aaa.api.*;
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

@RunWith(Suite.class)
@Suite.SuiteClasses({
		com.eurodyn.qlack2.fuse.aaa.impl.AccountingServiceImplTest.class,
		com.eurodyn.qlack2.fuse.aaa.impl.JSONConfigServiceImplTest.class,
		com.eurodyn.qlack2.fuse.aaa.impl.OperationServiceImplTest.class,
		com.eurodyn.qlack2.fuse.aaa.impl.OpTemplateServiceImplTest.class,
		com.eurodyn.qlack2.fuse.aaa.impl.ResourceServiceImplTest.class,
		com.eurodyn.qlack2.fuse.aaa.impl.UserGroupServiceImplTest.class,
		com.eurodyn.qlack2.fuse.aaa.impl.UserServiceImplTest.class,
		com.eurodyn.qlack2.fuse.aaa.impl.VerificationServiceImplTest.class
})
public class AllAAATests {
	private static EntityManagerFactory emf;
	private static EntityManager em;
	public static Boolean suiteRunning = false;
	private static AccountingService accountingService;
	private static JSONConfigService jsonConfigService;
	private static OperationService operationService;

	public static VerificationService getVerificationService() {
		return verificationService;
	}

	public static void setVerificationService(VerificationService verificationService) {
		AllAAATests.verificationService = verificationService;
	}

	private static VerificationService verificationService;

	public static UserService getUserService() {
		return userService;
	}

	private static UserService userService;

	public static OpTemplateService getOpTemplateService() {
		return opTemplateService;
	}

	public static AccountingService getAccountingService() {
		return accountingService;
	}

	public static JSONConfigService getJsonConfigService() {
		return jsonConfigService;
	}

	public static OperationService getOperationService() {
		return operationService;
	}

	public static ResourceService getResourceService() {
		return resourceService;
	}

	public static UserGroupService getUserGroupService() {
		return userGroupService;
	}

	private static OpTemplateService opTemplateService;
	private static ResourceService resourceService;
	private static UserGroupService userGroupService;

	public static void init() throws Exception {
		// Create the Entity Manager Factory.
		emf = Persistence.createEntityManagerFactory("fuse-aaa");

		// Run Liquibase.
		em = emf.createEntityManager();
		Session session = (Session) em.getDelegate();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();
		Connection connection = cp.getConnection();
		Database database = DatabaseFactory.getInstance()
				.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		Liquibase liquibase = new Liquibase("db/qlack2-fuse-aaa-impl.liquibase.changelog.xml",
				new ClassLoaderResourceAccessor(), database);
		liquibase.update(new Contexts(), new LabelExpression());

		// Setup services.
		accountingService = new AccountingServiceImpl();
		jsonConfigService = new JSONConfigServiceImpl();
		operationService = new OperationServiceImpl();
		opTemplateService = new OpTemplateServiceImpl();
		resourceService = new ResourceServiceImpl();
		userGroupService = new UserGroupServiceImpl();
		userService = new UserServiceImpl();
		verificationService = new VerificationServiceImpl();

		ReflectionUtil.setPrivateField(accountingService, "em", em);
		ReflectionUtil.setPrivateField(operationService, "em", em);
		ReflectionUtil.setPrivateField(opTemplateService, "em", em);
		ReflectionUtil.setPrivateField(resourceService, "em", em);
		ReflectionUtil.setPrivateField(userGroupService, "em", em);
		ReflectionUtil.setPrivateField(userService, "em", em);
		ReflectionUtil.setPrivateField(verificationService, "em", em);
		ReflectionUtil.setPrivateField(userService, "accountingService", accountingService);
		ReflectionUtil.setPrivateField(jsonConfigService, "em", em);
		ReflectionUtil.setPrivateField(jsonConfigService, "groupService", userGroupService);
		ReflectionUtil.setPrivateField(jsonConfigService, "templateService", opTemplateService);
		ReflectionUtil.setPrivateField(jsonConfigService, "operationService", operationService);
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		suiteRunning = true;
		init();
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
