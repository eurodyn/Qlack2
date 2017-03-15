/*
* Copyright EUROPEAN DYNAMICS SA <info@eurodyn.com>
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
package com.eurodyn.qlack2.fuse.caching.redis.impl;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CacheServiceImpl implements CacheService {
	public static final Logger LOGGER = Logger.getLogger(CacheServiceImpl.class.getName());
	private final String namespaceSeparator = ":";
	private JedisPool pool;
	private String host;
	private int port;
	private int maxIdle;
	private int maxTotal;
	private int minIdle;
	
	public CacheServiceImpl() {
		super();
	}
	
	public CacheServiceImpl(String host, int port, int maxIdle, int maxTotal, int minIdle) {
		super();
		this.host = host;
		this.port = port;
		this.maxIdle = maxIdle;
		this.maxTotal = maxTotal;
		this.minIdle = minIdle;
		init();
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	private String keyName(String namespace, String key) {
		return namespace + namespaceSeparator + key;
	}
	
	public void init() {
		LOGGER.log(Level.CONFIG, "Initialising Redis pool to {0}:{1} [maxIdle={2}, minIdle={3}, maxTotal={4}].", 
				new Object[]{host, port, maxIdle, minIdle, maxTotal});
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(maxIdle);
		config.setMaxTotal(maxTotal);
		config.setMinIdle(minIdle);		
		pool = new JedisPool(config, host, port);
	}

	public void destroy() {
		pool.destroy();
	}

	@Override
	public void set(String key, String value) {
		set(key, value, 0);
	}

	@Override
	public void set(String key, String value, long expiresAfterMsec) {
		try (Jedis jedis = pool.getResource()) {
			if (expiresAfterMsec != 0) {
				jedis.setex(key, (int)(expiresAfterMsec/1000), value);
			} else {
				jedis.set(key, value);				
			}
		}
	}

	@Override
	public void deleteByKeyName(String key) {
		try (Jedis jedis = pool.getResource()) {
			jedis.del(key);
		}
		
	}

	@Override
	public String get(String key) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.get(key);
		}
	}

	@Override
	public void deleteByKeyPattern(String pattern) {
		try (Jedis jedis = pool.getResource()) {
			Set<String> filter = Sets.filter(getKeyNames(), Predicates.containsPattern(pattern));
			jedis.del(filter.toArray(new String[filter.size()]));
		}
	}

	@Override
	public void deleteByKeyPrefix(String prefix) { 
		deleteByKeyPattern(prefix + ".*");
	}
	@Override
	public Set<String> getKeyNames() {
		try (Jedis jedis = pool.getResource()) {
			return jedis.keys("*");
		}
	}

	@Override
	public void clear() {
		try (Jedis jedis = pool.getResource()) {
			jedis.flushAll();
		}
	}

}
