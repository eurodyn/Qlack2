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
package com.eurodyn.qlack2.fuse.caching.impl.redis;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Redis backed caching client. The caching URL is:
 * redis-host:redis-port:maxTotal:minIdle:maxIdle
 */
public class CacheServiceImpl extends CacheService {

  public static final Logger LOGGER = Logger.getLogger(CacheServiceImpl.class.getName());
  private final String namespaceSeparator = ":";
  private JedisPool pool;
  private ObjectMapper mapper = new ObjectMapper();

  public CacheServiceImpl() {
    if (isActive()) {
      LOGGER.log(Level.CONFIG, "Initialising Redis pool to {0}.", getCacheURL());
      JedisPoolConfig config = new JedisPoolConfig();
      String[] cacheURLArgs = getCacheURL().split(":");
      config.setMaxTotal(Integer.parseInt(cacheURLArgs[2]));
      config.setMaxIdle(Integer.parseInt(cacheURLArgs[3]));
      config.setMinIdle(Integer.parseInt(cacheURLArgs[4]));
      pool = new JedisPool(config, cacheURLArgs[0], Integer.parseInt(cacheURLArgs[1]));
    }
  }

  private String keyName(String namespace, String key) {
    return namespace + namespaceSeparator + key;
  }

  public void destroy() {
    pool.destroy();
  }

  @Override
  public void set(String key, Object value) {
    if (isActive()) {
      try {
        /** Convert Object to String, since Redis only holds Strings */
        String serialisedValue;
        if (!(value instanceof String)) {
          serialisedValue = mapper.writeValueAsString(value);
        } else {
          serialisedValue = (String) value;
        }

        try (Jedis jedis = pool.getResource()) {
          if (getExpiryTime() > 0) {
            jedis.setex(key, (int) (getExpiryTime() / 1000), serialisedValue);
          } else {
            jedis.set(key, serialisedValue);
          }
        }
      } catch (JsonProcessingException e) {
        LOGGER.log(Level.SEVERE, "Could not serialise value.", e);
      }
    }
  }

  @Override
  public void deleteByKeyName(String key) {
    if (isActive()) {
      try (Jedis jedis = pool.getResource()) {
        jedis.del(key);
      }
    }
  }

  @Override
  public Object get(String key) {
    if (isActive()) {
      try (Jedis jedis = pool.getResource()) {
        return jedis.get(key);
      }
    } else {
      return null;
    }
  }

  @Override
  public void deleteByKeyPattern(String pattern) {
    if (isActive()) {
      try (Jedis jedis = pool.getResource()) {
        Set<String> filter = Sets.filter(getKeyNames(), Predicates.containsPattern(pattern));
        jedis.del(filter.toArray(new String[filter.size()]));
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
      try (Jedis jedis = pool.getResource()) {
        retVal = jedis.keys("*");
      }
    }

    return retVal;
  }

  @Override
  public void clear() {
    if (isActive()) {
      try (Jedis jedis = pool.getResource()) {
        jedis.flushAll();
      }
    }
  }

}
