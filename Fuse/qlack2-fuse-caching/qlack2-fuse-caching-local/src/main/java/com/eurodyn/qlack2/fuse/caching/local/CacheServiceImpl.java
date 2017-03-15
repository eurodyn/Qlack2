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
package com.eurodyn.qlack2.fuse.caching.local;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import com.google.common.base.Predicates;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CacheServiceImpl implements CacheService {
    public static final Logger LOGGER = Logger.getLogger(CacheServiceImpl.class.getName());
    private long maxEntries = -1;
    private Cache<String, String> cache;

    private boolean active;

    public CacheServiceImpl(long maxEntries) {
        this.maxEntries = maxEntries;
        cache = CacheBuilder.newBuilder().maximumSize(maxEntries).build();
    }

    public CacheServiceImpl() {
        if (maxEntries > -1) {
            cache = CacheBuilder.newBuilder().maximumSize(maxEntries).build();
        } else {
            cache = CacheBuilder.newBuilder().build();
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setMaxEntries(long maxEntries) {
        this.maxEntries = maxEntries;
    }

    @Override
    public void set(String key, String value, long expiresAfterMsec) {
        LOGGER.log(Level.WARNING, "Local cache implementation does not support custom expiration on a key-level.");
        cache.put(key, value);
    }

    @Override
    public void set(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void deleteByKeyName(String key) {
        cache.invalidate(key);
    }

    @Override
    public String get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void deleteByKeyPattern(String pattern) {
        cache.invalidateAll(Sets.filter(cache.asMap().keySet(), Predicates.containsPattern(pattern)));
    }

    @Override
    public void deleteByKeyPrefix(String prefix) {
        deleteByKeyPattern(prefix + ".*");
    }

    @Override
    public Set<String> getKeyNames() {
        return cache.asMap().keySet();
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }
}
