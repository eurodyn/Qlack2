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
package com.eurodyn.qlack2.fuse.caching.impl.local;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import com.google.common.base.Predicates;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheServiceImpl extends CacheService {
  public static final Logger LOGGER = Logger.getLogger(CacheServiceImpl.class.getName());
  private Cache<String, Object> cache;

  public CacheServiceImpl() {
    init();
  }

  public void init() {
    if (isActive()) {
      LOGGER.log(Level.CONFIG, MessageFormat.format("Initialising cache: {0}.", this.getClass().getName()));
      final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();

      if (getMaxEntries() > 0) {
        cacheBuilder.maximumSize(getMaxEntries());
        LOGGER.log(Level.FINE, MessageFormat.format("Setting cache''s max entries to: {0}.", getMaxEntries()));
      } else {
        LOGGER.log(Level.FINE, "No upper bound for cache's max entries.");
      }

      if (getExpiryTime() > 0) {
        cacheBuilder.expireAfterWrite(getExpiryTime(), TimeUnit.MILLISECONDS);
        LOGGER.log(Level.FINE, MessageFormat.format("Setting cache''s expiry time to: {0}.", getExpiryTime()));
      } else {
        LOGGER.log(Level.FINE, "No upper bound for cache's expiration time.");
      }

      cache = cacheBuilder.build();
    } else {
      LOGGER.log(Level.CONFIG, "Cache is configured as inactive.");
    }
  }

  @Override
  public void set(String key, Object value) {
    if (isActive()) {
      cache.put(key, value);
    }
  }

  @Override
  public void deleteByKeyName(String key) {
    if (isActive()) {
      cache.invalidate(key);
    }
  }

  @Override
  public Object get(String key) {
    if (isActive()) {
      return cache.getIfPresent(key);
    } else {
      return null;
    }
  }

  @Override
  public void deleteByKeyPattern(String pattern) {
    if (isActive()) {
      cache.invalidateAll(Sets.filter(cache.asMap().keySet(), Predicates.containsPattern(pattern)));
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
    if (isActive()) {
      return cache.asMap().keySet();
    } else {
      return new HashSet<>();
    }
  }

  @Override
  public void clear() {
    if (isActive()) {
      cache.invalidateAll();
    }
  }

}
