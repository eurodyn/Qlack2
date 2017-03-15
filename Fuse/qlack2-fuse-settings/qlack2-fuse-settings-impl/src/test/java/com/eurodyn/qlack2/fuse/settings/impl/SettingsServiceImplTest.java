/**
 * 
 */
package com.eurodyn.qlack2.fuse.settings.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eurodyn.qlack2.fuse.settings.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.settings.api.dto.SettingDTO;
import com.eurodyn.qlack2.fuse.settings.impl.model.QSetting;
import com.eurodyn.qlack2.fuse.settings.impl.model.Setting;
import com.querydsl.jpa.impl.JPAQueryFactory;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

/**
 * @author European Dynamics SA
 *
 */
public class SettingsServiceImplTest {
	private static EntityManagerFactory emf;
	private static EntityManager em;
	private static SettingsServiceImpl settingService;
	private static EntityTransaction tx;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Create the Entity Manager Factory.
		emf = Persistence.createEntityManagerFactory("fuse-settings");

		// Run Liquibase.
		em = emf.createEntityManager();
		Session session = (Session) em.getDelegate();
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
		ConnectionProvider cp = sfi.getConnectionProvider();
		Connection connection = cp.getConnection();

		Database database = DatabaseFactory.getInstance()
				.findCorrectDatabaseImplementation(new JdbcConnection(connection));
		Liquibase liquibase = new Liquibase("db/qlack2-fuse-settings-impl.liquibase.changelog.xml",
				new ClassLoaderResourceAccessor(), database);
		liquibase.update(new Contexts(), new LabelExpression());

		// Create an instance of the service to be tested.
		settingService = new SettingsServiceImpl();
		settingService.setEm(em);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		em.close();
		emf.close();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		if (tx == null) {
			tx = em.getTransaction();
		}
		tx.begin();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tx.rollback();
	}

	private void createTestSetting(String owner, String key, String group, String val) {
		Setting setting = new Setting();
		setting.setOwner(owner);
		setting.setGroup(group);
		setting.setKey(key);
		setting.setVal(val);
		em.persist(setting);
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.settings.impl.SettingsServiceImpl#getSettings(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetSettings() throws Exception {
		String owner = UUID.randomUUID().toString();
		String key = UUID.randomUUID().toString();
		String group = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();
		createTestSetting(owner, key, group, val);
		SettingDTO settingDTO = settingService.getSetting(owner, key, group);
		assertNotNull(settingDTO);
		assertEquals(owner, settingDTO.getOwner());
		assertEquals(key, settingDTO.getKey());
		assertEquals(group, settingDTO.getGroup());
	}

	/**
	 * Test methogetDefaultSetting
	 * com.eurodyn.qlack2getSettingFromDefaultgsServiceImpl#
	 * getSettingFromDefaultGroup(java.lang.String, java.lang.String,
	 * java.lang.String)} .
	 */
	@Test
	public void testGetSetting() throws Exception {
		String owner = UUID.randomUUID().toString();
		String key = UUID.randomUUID().toString();
		String group = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();

		for (int i = 0; i < 10; i++) {
			createTestSetting(owner, key + i, group, val + i);
		}

		List<SettingDTO> settings = settingService.getSettings(owner, true);
		assertNotNull(settings);
		assertEquals(10, settings.size());
		for (int i = 0; i < 10; i++) {
			assertEquals(owner, settings.get(i).getOwner());
			assertEquals(val + i, settings.get(i).getVal());
		}
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.settings.impl.SettingsServiceImpl#getGroupNames(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetGroupNames() throws Exception {
		String owner = UUID.randomUUID().toString();
		String key = UUID.randomUUID().toString();
		String group = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();
		createTestSetting(owner, key, group + "A", val);
		createTestSetting(owner, key, group + "B", val);
		createTestSetting(owner, key, group + "C", val);

		List<GroupDTO> groupNames = settingService.getGroupNames(owner);
		assertNotNull(groupNames);
		assertEquals(3, groupNames.size());
		assertEquals(group + "A", groupNames.get(0).getName());
		assertEquals(group + "B", groupNames.get(1).getName());
		assertEquals(group + "C", groupNames.get(2).getName());
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.settings.impl.SettingsServiceImpl#getValsForGroup(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetValsForGroup() throws Exception {
		String owner = UUID.randomUUID().toString();
		String key = UUID.randomUUID().toString();
		String group = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();

		for (int i = 0; i < 5; i++) {
			createTestSetting(owner, key + i, group + "A", val + i);
		}
		for (int i = 5; i < 10; i++) {
			createTestSetting(owner, key + i, group + "B", val + i);
		}

		List<SettingDTO> valsForGroupA = settingService.getGroupSettings(owner, group + "A");
		assertNotNull(valsForGroupA);
		assertEquals(5, valsForGroupA.size());

		List<SettingDTO> valsForGroupB = settingService.getGroupSettings(owner, group + "B");
		assertNotNull(valsForGroupB);
		assertEquals(5, valsForGroupB.size());
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.settings.impl.SettingsServiceImpl#createSetting(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)}
	 * .
	 */
	@Test
	public void testCreateSetting() throws Exception {
	/*	String owner = UUID.randomUUID().toString();
		String key = UUID.randomUUID().toString();
		String group = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();

		settingService.createSetting(owner, group, key + "A", val, true, false);
		settingService.createSetting(owner, group, key + "B", val, false, false);

		QSetting qsetting = QSetting.setting;
		JPAQueryFactory f = new JPAQueryFactory(em);
		Setting setting = f.select(qsetting)
				.from(qsetting)
				.where(qsetting.owner.eq(owner).and(qsetting.key.eq(key + "A")).and(qsetting.group.eq(group)))
				.fetchOne();
		assertNotNull(setting);
		assertEquals(true, setting.isSensitive());

		setting = f.select(qsetting)
				.from(qsetting)
				.where(qsetting.owner.eq(owner).and(qsetting.key.eq(key + "B")).and(qsetting.group.eq(group)))
				.fetchOne();
		assertNotNull(setting);
		assertEquals(false, setting.isSensitive());*/
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.settings.impl.SettingsServiceImpl#setVal(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testSetVal() throws Exception {

	}

}
