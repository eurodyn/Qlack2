/**
 * 
 */
package com.eurodyn.qlack2.fuse.caching.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eurodyn.qlack2.common.util.net.NetUtils;
import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import com.eurodyn.qlack2.fuse.caching.redis.impl.CacheServiceImpl;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

/**
 * @author European Dynamics SA
 *
 */
public class CacheServiceImplTest {
	private static CacheService cache;
	// The Docker client to talk to Docker Engine.
	private static DockerClient docker;
	// The container to startup.
	private static CreateContainerResponse container;
	// The port exposed from the container.
	private static int randomLocalPort;
	// Default Docker Engine params.
	private static String dockerHost = "tcp://127.0.0.1:2375";
	// Memcached version.
	private static final String REDIS_IMAGE = "redis:3.0.5";
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dockerHost = StringUtils.isNotEmpty(System.getenv("DOCKER_HOST"))
				? System.getenv("DOCKER_HOST")
				: dockerHost;
		dockerHost = dockerHost.replaceAll("^tcp", "http");
				
		// Setup link to Docker Engine.
		System.out.println("Preparing Docker containers for testing using: " +
				dockerHost);
		DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
				.withServerAddress("https://index.docker.io/v1/")
				.withUri(dockerHost)
				.build();
		docker = DockerClientBuilder.getInstance(config).build();

		// Pull required images.
		docker.pullImageCmd(REDIS_IMAGE).exec(new PullImageResultCallback()).awaitSuccess();
		
		// Start containers.
		ExposedPort tcp6379 = ExposedPort.tcp(6379);		
		randomLocalPort = NetUtils.getAvailablePort();
		Ports portBindings = new Ports();
		portBindings.bind(tcp6379, Ports.Binding(randomLocalPort));
		container = docker.createContainerCmd(REDIS_IMAGE)
				.withExposedPorts(ExposedPort.tcp(6379))
				.withPortBindings(portBindings)
				.exec();
		docker.startContainerCmd(container.getId()).exec();
		
		// Setup cache service.
		cache = new CacheServiceImpl(NetUtils.getURIHost(dockerHost), randomLocalPort, 10, 10, 10);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("Stopping Docker containers for testing...");
		docker.stopContainerCmd(container.getId()).exec();
		docker.waitContainerCmd(container.getId()).exec();
		docker.removeContainerCmd(container.getId()).exec();
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
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#set(java.lang.String, java.lang.String, long)}
	 * .
	 */
	@Test
	public void testSetStringStringLong() {
		String key = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();
		
		cache.set(key, val, 2000);
		assertEquals(val, cache.get(key));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNull(cache.get(key));
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#set(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testSetStringString() {
		String key = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();
		
		cache.set(key, val);
		assertEquals(val, cache.get(key));
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#delete(java.lang.String)}
	 * .
	 */
	@Test
	public void testDeleteString() {
		String key = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();
		
		cache.set(key, val);
		assertEquals(val, cache.get(key));
		cache.deleteByKeyName(key);
		assertNull(val, cache.get(key));
	}

	/**
	 * Test method for
	 * {@link com.eurodyn.qlack2.fuse.caching.local.CacheServiceImpl#get(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetString() {
		String key = UUID.randomUUID().toString();
		String val = UUID.randomUUID().toString();
		
		cache.set(key, val);
		assertEquals(val, cache.get(key));
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
		assertEquals(1, cache.getKeyNames().size());
	}

	@Test
	public void testDeletePrefix() {
		cache.set("ns1:key1", "test1");
		cache.set("ns1:key2", "test2");
		cache.set("ns2:key3", "test3");
		
		cache.deleteByKeyPrefix("ns1");
		assertNull(cache.get("ns1:key1"));
		assertNull(cache.get("ns1:key2"));
		assertEquals("test3", cache.get("ns2:key3"));
		assertEquals(1, cache.getKeyNames().size());
	}
	@Test
	public void testGetKeyNames() {
		cache.set("ns1:key1", "test1");
		cache.set("ns1:key2", "test2");
		cache.set("ns2:key3", "test3");
		assertNotNull(cache.getKeyNames());
		assertEquals(3, cache.getKeyNames().size());
	}
}
