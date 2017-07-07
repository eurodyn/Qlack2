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
package com.eurodyn.qlack2.fuse.caching.impl.memcached;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.OperationTimeoutException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Memcached backed caching client. The caching URL represents the IP address and the port of
 * Memcached, e.g. my-memcached-server:1234.
 */
public class CacheServiceImpl extends CacheService {

  public static final Logger LOGGER = Logger.getLogger(CacheServiceImpl.class.getName());
  private MemcachedClient cache;

  /**
   * As Memcached does not support enlisting of its keys, this is a workaround
   * to allow deleting keys by a regular expression. Note that for very large
   * caches this may not be the most effective usage pattern, however if you
   * *need* to use Memcached this is your only option.
   */
  Set<String> keys;

  public CacheServiceImpl() {
    try {
      if (isActive()) {
        cache = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil
          .getAddresses(getCacheURL()));
        LOGGER.fine(MessageFormat.format("Using cache server {0}.", getCacheURL()));
        keys = Sets.newConcurrentHashSet();
      }
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Cache client could not be initialised.", e);
    }
  }

  public MemcachedClient getCache() {
    return cache;
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
  public void set(String key, Object value) {
    if (isActive()) {
      try {
        /**
         * Memcached expresses EPOCH in seconds, therefore we need to convert the msec passed EPOCH.
         */
        int expiresAtEpochSec =
          getExpiryTime() > 0 ? (int) ((System.currentTimeMillis() + getExpiryTime()) / 1000l) : 0;
        cache.set(key, expiresAtEpochSec, value);
        LOGGER.log(Level.FINEST, "Added to memcached key {0}, with " + "value {1}.",
          new String[]{key, value.toString()});
        keys.add(key);
      } catch (OperationTimeoutException | CancellationException e) {
        LOGGER.log(Level.FINEST, "Could not add the key to the cache.", e);
      }
    }
  }

  @Override
  public void deleteByKeyName(String key) {
    if (isActive()) {
      try {
        cache.delete(key);
        keys.remove(key);
        LOGGER.log(Level.FINEST, "Deleted from memcached key {0}.", key);
      } catch (OperationTimeoutException | CancellationException e) {
        LOGGER.log(Level.FINEST, "Could not delete the key from the cache.", e);
      }
    }
  }

  @Override
  public Object get(String key) {
    Object retVal = null;

    if (isActive()) {
      try {
        if (cache != null) {
          retVal = cache.get(key);
        }
      } catch (OperationTimeoutException | CancellationException e) {
        LOGGER.log(Level.FINEST, "Could not get the key to the cache.", e);
      }
    }

    return retVal;
  }

  @Override
  public void deleteByKeyPattern(String pattern) {
    if (isActive()) {
      Set<String> filter = Sets.filter(keys, Predicates.containsPattern(pattern));
      for (String key : filter) {
        deleteByKeyName(key);
      }
    }
  }

  @Override
  public void deleteByKeyPrefix(String prefix) {
    if (isActive()) {
      deleteByKeyPattern(prefix + ".*");
    }
  }

  @Override
  public Set<String> getKeyNames() {
    Set<String> retVal = new HashSet<>();

    if (isActive()) {
      retVal = keys;
    }

    return retVal;
  }

  @Override
  public void clear() {
    if (isActive()) {
      cache.flush();
      keys.clear();
    }
  }
}
