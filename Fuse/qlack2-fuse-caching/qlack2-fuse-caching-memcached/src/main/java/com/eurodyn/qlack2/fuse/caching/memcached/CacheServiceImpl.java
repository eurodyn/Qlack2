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
package com.eurodyn.qlack2.fuse.caching.memcached;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

public class CacheServiceImpl implements CacheService {
	public static final Logger LOGGER = Logger.getLogger(CacheServiceImpl.class.getName());
	private MemcachedClient cache;
	private int directPort;
	private String directIp;
	private String localIp;
	// Set to '0' for no expiration (however, note that your key can still be
	// evicted from the cache).
	private long defaultExpirationAfterMsec;
	private Boolean active;

	// As Memcached does not support enlisting of its keys, this is a workaround
	// to allow deleting keys by a regular expression. Note that for very large
	// caches this may not be the most effective usage pattern, however if you
	// *need* to use Memcached this is your only option.
	Set<String> keys;

	public CacheServiceImpl() {
		super();
	}

	public CacheServiceImpl(int directPort, String directIp, String localIp, long defaultExpirationAfterMsec,
			Boolean active) {
		super();
		this.directPort = directPort;
		this.directIp = directIp;
		this.localIp = localIp;
		this.defaultExpirationAfterMsec = defaultExpirationAfterMsec;
		this.active = active;
		this.init();
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public void setCache(MemcachedClient cache) {
		this.cache = cache;
	}

	public void setDirectPort(int directPort) {
		this.directPort = directPort;
	}

	public String getDirectIp() {
		return directIp;
	}

	public void setDirectIp(String directIp) {
		this.directIp = directIp;
	}

	public void setDefaultExpirationAfterMsec(long defaultExpirationAfterMsec) {
		this.defaultExpirationAfterMsec = defaultExpirationAfterMsec;
	}

	public MemcachedClient getCache() {
		return cache;
	}

	public int getDirectPort() {
		return directPort;
	}

	public String getLocalIp() {
		return localIp;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	public void init() {
		try {
			if (active) {
				cache = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil
						.getAddresses((StringUtils.isNotBlank(directIp) ? directIp : "localhost") + ":" + directPort));
				LOGGER.fine(MessageFormat.format("Using cache server {0}:{1}.", directIp, String.valueOf(directPort)));
				keys = Sets.newConcurrentHashSet();
			} else {
				LOGGER.fine("Cache deactiveted.");
			}

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cache client could not be initialised.", e);
		}
	}

	public void destroy() {
		if (cache != null) {
			cache.shutdown();
			cache = null;
			keys.clear();
			keys = null;
		}
	}

	@Override
	public void set(String key, String value) {
		set(key, value, defaultExpirationAfterMsec);
	}

	@Override
	public void set(String key, String value, long expiresAfterMsec) {
		// Memcache expresses EPOCH in seconds, therefore we need
		// to convert the msec passed EPOCH.
		try {
			int expiresAtEpochSec = expiresAfterMsec != 0
					? (int) ((System.currentTimeMillis() + expiresAfterMsec) / 1000l) : 0;
			cache.set(key, expiresAtEpochSec, value);
			LOGGER.log(Level.FINEST, "Added to memcached key {0}, with " + "value {1}.",
					new String[] { key, value.toString() });
			keys.add(key);
		} catch (OperationTimeoutException | CancellationException e) {
			// In case something went wrong we simply discard the message,
			// since a cache may not necessarily be available.
			LOGGER.log(Level.FINEST, "Could not add the key to the cache.", e);
		}
	}

	@Override
	public void deleteByKeyName(String key) {
		try {
			cache.delete(key);
			keys.remove(key);
			LOGGER.log(Level.FINEST, "Deleted from memcached key {0}.", key);
		} catch (OperationTimeoutException | CancellationException e) {
			// In case something went wrong we simply discard the message,
			// since a cache may not necessarily be available.
			LOGGER.log(Level.FINEST, "Could not delete the key from the cache.", e);
		}
	}

	@Override
	public String get(String key) {
		String retVal = null;
		try {
			if (cache != null) {
				retVal = (String) cache.get(key);
			}
		} catch (OperationTimeoutException | CancellationException e) {
			// In case something went wrong we simply discard the message,
			// since a cache may not necessarily be available.
			LOGGER.log(Level.FINEST, "Could not get the key to the cache.", e);
		}

		return retVal;
	}

	@Override
	public void deleteByKeyPattern(String pattern) {
		Set<String> filter = Sets.filter(keys, Predicates.containsPattern(pattern));
		for (String key : filter) {
			deleteByKeyName(key);
		}
	}

	@Override
	public void deleteByKeyPrefix(String prefix) { 
		deleteByKeyPattern(prefix + ".*");
	}
	
	@Override
	public Set<String> getKeyNames() {
		return keys;
	}

	@Override
	public void clear() {
		cache.flush();
		keys.clear();
	}

}
