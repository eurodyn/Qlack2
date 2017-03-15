/*
\* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
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
package com.eurodyn.qlack2.fuse.caching.api;

import java.util.Set;

public interface CacheService {
	/**
	 * Adds a key to the cache with a predetermined expiration time. Note that
	 * not all concrete cache implementations support expirations on a
	 * key-level.
	 * 
	 * @param key
	 *            The key to create.
	 * @param value
	 *            The value of the key to associated with.
	 * @param expiresAfterMsec
	 *            The number of msec after which the key will automatically be
	 *            removed (provided the underlying concrete cache implementation
	 *            supports it).
	 */
	void set(String key, String value, long expiresAfterMsec);

	/**
	 * Adds a new key to the cache.
	 * @param key
	 *            The key to create.
	 * @param value
	 *            The value of the key to associated with.
	 */            
	void set(String key, String value);

	/**
	 * Deletes a key from the cache.
	 * @param key The key to be deleted.
	 */
	void deleteByKeyName(String key);

	/**
	 * Deletes all keys that match the given regular expression. Note that
	 * according to how each concrete implementation of the cache is actually
	 * storing keys, this can be very inefficient in very large caches.
	 * 
	 * @param pattern
	 *            Java regular expression.
	 */
	void deleteByKeyPattern(String pattern);
	
	/**
	 * Deletes all keys that start with the given prefix. Note that
	 * according to how each concrete implementation of the cache is actually
	 * storing keys, this can be very inefficient in very large caches.
	 * 
	 * @param prefix
	 *            The prefix to lookup keys with.
	 */
	void deleteByKeyPrefix(String prefix);

	/**
	 * Gets a key from the cache.
	 * @param key The key to be retrieved.
	 * @return The key value or null if the key does not exist.
	 */
	String get(String key);
	
	/**
	 * The names of the keys currently cached.
	 * @return The names of the keys currently cached.
	 */
	Set<String> getKeyNames();
	
	/**
	 * Entirely clears cache's entries.
	 */
	void clear();
}
