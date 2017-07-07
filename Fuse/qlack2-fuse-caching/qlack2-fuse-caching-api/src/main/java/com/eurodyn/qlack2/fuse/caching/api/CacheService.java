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

public abstract class CacheService {

  /**
   * An inactive cache, does not store not retrieve any values. This flag is useful to debug your
   * cache population logic on an already running cache without having to redeploy your application.
   */
  boolean active = true;

  /**
   * The maximum number of entries a cache will hold. Leave this value at '0' to indicate an
   * unbounded cache.
   */
  long maxEntries = 0;

  /**
   * The amount of time (in msec) after which an entry in the caceh expires. Leave this value at '0'
   * to indicate items that never expire.
   */
  long expiryTime = 0;

  /**
   * The URL of the cache. This is a convenience property to allow different cache implementations
   * to specify connections parameters, etc. You need to check the documentation of each specific
   * cache implementation regarding the format of this property.
   */
  String cacheURL = "";

  public String getCacheURL() {
    return cacheURL;
  }

  public void setCacheURL(String cacheURL) {
    this.cacheURL = cacheURL;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public long getMaxEntries() {
    return maxEntries;
  }

  public void setMaxEntries(long maxEntries) {
    this.maxEntries = maxEntries;
  }

  public long getExpiryTime() {
    return expiryTime;
  }

  public void setExpiryTime(long expiryTime) {
    this.expiryTime = expiryTime;
  }

  /**
   * Adds a new key to the cache.
   *
   * @param key The key to create.
   * @param value The value of the key to associated with.
   */
  public abstract void set(String key, Object value);

  /**
   * Deletes a key from the cache.
   *
   * @param key The key to be deleted.
   */
  public abstract void deleteByKeyName(String key);

  /**
   * Deletes all keys that match the given regular expression. Note that
   * according to how each concrete implementation of the cache is actually
   * storing keys, this can be very inefficient in very large caches.
   *
   * @param pattern Java regular expression.
   */
  public abstract void deleteByKeyPattern(String pattern);

  /**
   * Deletes all keys that start with the given prefix. Note that
   * according to how each concrete implementation of the cache is actually
   * storing keys, this can be very inefficient in very large caches.
   *
   * @param prefix The prefix to lookup keys with.
   */
  public abstract void deleteByKeyPrefix(String prefix);

  /**
   * Gets a key from the cache.
   *
   * @param key The key to be retrieved.
   * @return The key value or null if the key does not exist.
   */
  public abstract Object get(String key);

  /**
   * The names of the keys currently cached.
   *
   * @return The names of the keys currently cached.
   */
  public abstract Set<String> getKeyNames();

  /**
   * Entirely clears cache's entries.
   */
  public abstract void clear();
}
