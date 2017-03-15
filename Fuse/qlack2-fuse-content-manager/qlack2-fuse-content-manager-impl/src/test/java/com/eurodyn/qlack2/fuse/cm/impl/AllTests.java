package com.eurodyn.qlack2.fuse.cm.impl;

import java.sql.Connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.tika.config.TikaConfig;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.eurodyn.qlack2.common.util.reflection.ReflectionUtil;
import com.eurodyn.qlack2.fuse.cm.api.ConcurrencyControlService;
import com.eurodyn.qlack2.fuse.cm.api.DocumentService;
import com.eurodyn.qlack2.fuse.cm.api.VersionService;
import com.eurodyn.qlack2.fuse.cm.impl.storage.DBStorage;
import com.eurodyn.qlack2.fuse.cm.impl.storage.FSStorage;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		com.eurodyn.qlack2.fuse.cm.impl.VersionServiceImplTest.class
})
public class AllTests {
	private static EntityManagerFactory emf;
	private static EntityManager em;
	private static ConcurrencyControlService concurrencyControlService;
	public static Boolean suiteRunning = false;
	private static VersionService versionService;
	private static DocumentService documentService;
	private static DBStorage dbStorage;
	private static FSStorage fsStorage;
		
	private static TemporaryFolder tmpFolder = new TemporaryFolder();
	
	public static void init() throws Exception {
		// Create the Entity Manager Factory.
		emf = Persistence.createEntityManagerFactory("fuse-contentmanager");

		// Run Liquibase.
		em = emf.createEntityManager();
		Session session = (Session) em.getDelegate();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();
		Connection connection = cp.getConnection();
		Database database = DatabaseFactory.getInstance()
				.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		Liquibase liquibase = new Liquibase("db/qlack2-fuse-content-manager-impl.liquibase.changelog.xml",
				new ClassLoaderResourceAccessor(), database);
		liquibase.update(new Contexts(), new LabelExpression());

		// Setup services.
		concurrencyControlService = new ConcurrencyControlServiceImpl();
		ReflectionUtil.setPrivateField(concurrencyControlService, "em", em);
		
		dbStorage = new DBStorage();
		ReflectionUtil.setPrivateField(dbStorage, "em", AllTests.getEm());
		fsStorage = new FSStorage();
		tmpFolder.create();
		ReflectionUtil.setPrivateField(fsStorage, "rootFS", tmpFolder.getRoot().getAbsolutePath());
		
		versionService = new VersionServiceImpl();
		ReflectionUtil.setPrivateField(versionService, "em", AllTests.getEm());
		ReflectionUtil.setPrivateField(versionService, "concurrencyControlService", 
				AllTests.getConcurrencyControlService());
		// Set default storage engine to DB. Individual testing methods in need
		// to test specific storage engines can override it.
		ReflectionUtil.setPrivateField(versionService, "storageEngine", dbStorage);
		ReflectionUtil.setPrivateField(versionService, "tika", new TikaConfig());
		
		documentService = new DocumentServiceImpl();
		ReflectionUtil.setPrivateField(documentService, "em", AllTests.getEm());
		ReflectionUtil.setPrivateField(documentService, "concurrencyControlService", 
				AllTests.getConcurrencyControlService());
		ReflectionUtil.setPrivateField(documentService, "versionService", versionService);
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
		tmpFolder.delete();
	}

	/**
	 * @return the concurrencyControlService
	 */
	public static ConcurrencyControlService getConcurrencyControlService() {
		return concurrencyControlService;
	}

	/**
	 * @return the em
	 */
	public static EntityManager getEm() {
		return em;
	}

	/**
	 * @return the versionService
	 */
	public static VersionService getVersionService() {
		return versionService;
	}

	/**
	 * @return the documentService
	 */
	public static DocumentService getDocumentService() {
		return documentService;
	}

	/**
	 * @return the dbStorage
	 */
	public static DBStorage getDbStorage() {
		return dbStorage;
	}

	/**
	 * @return the fsStorage
	 */
	public static FSStorage getFsStorage() {
		return fsStorage;
	}
	
}
