/**
 * 
 */
package com.eurodyn.qlack2.fuse.caching.local;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;

/**
 * @author European Dynamics SA
 *
 */
public class CacheServiceImplTest {
	private static CacheService cache;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		cache = new CacheServiceImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cache.clear();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#set(java.lang.String, java.lang.String, long)}.
	 */
	@Test
	public void testSetStringStringLong() {
		cache.set("testKey", "testVal", 2000);
		assertEquals("testVal", cache.get("testKey"));
	}

	/**
	 * Test method for {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#set(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSetStringString() {
		cache.set("testKey", "testVal");
		assertEquals("testVal", cache.get("testKey"));
	}

	/**
	 * Test method for {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#delete(java.lang.String)}.
	 */
	@Test
	public void testDeleteString() {
		cache.set("testKey", "testVal");
		assertEquals("testVal", cache.get("testKey"));
		cache.deleteByKeyName("testKey");
		assertNull("testVal", cache.get("testKey"));
	}

	/**
	 * Test method for {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#get(java.lang.String)}.
	 */
	@Test
	public void testGetString() {
		cache.set("testKey", "testVal");
		assertEquals("testVal", cache.get("testKey"));
	}
	
	@Test
	public void testDeletePattern() {
		cache.set("ns1:key1", "test1");
		cache.set("ns1:key2", "test2");
		cache.set("ns2:key3", "test3");
		cache.deleteByKeyPattern("ns1.*");
		assertNull(cache.get("ns1:key1"));
		assertNull(cache.get("ns1:key2"));
		assertEquals("test3", cache.get("ns2:key3"));
	}

	@Test
	public void testGetKeyNames() {
		cache.set("ns1:key1", "test1");
		cache.set("ns1:key2", "test2");
		cache.set("ns2:key3", "test3");
		assertNotNull(cache.getKeyNames());
		assertEquals(3, cache.getKeyNames().size());
	}

	@Test
	public void testDeleteByPrefix() {
		cache.set("ns1:key1", "test1");
		cache.set("ns1:key2", "test2");
		cache.set("ns2:key3", "test3");
		cache.deleteByKeyPrefix("ns1");
		assertNull(cache.get("ns1:key1"));
		assertNull(cache.get("ns1:key2"));
		assertEquals("test3", cache.get("ns2:key3"));
	}
}
