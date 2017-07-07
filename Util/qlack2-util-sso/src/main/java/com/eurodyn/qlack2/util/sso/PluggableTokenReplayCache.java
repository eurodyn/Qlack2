package com.eurodyn.qlack2.util.sso;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import org.apache.cxf.rs.security.saml.sso.TokenReplayCache;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * An implementation of the {@link TokenReplayCache} interface based on qlack2-fuse-caching module,
 * so that the underlying caching implementation is pluggable. If you need to support SSO in a
 * clustered environment make sure you choose a qlack2-fuse-caching implementation that supports a
 * distributed/central cache (e.g. qlack2-fuse-caching-redis).
 */
public class PluggableTokenReplayCache implements TokenReplayCache<String> {

  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger.getLogger(PluggableTokenReplayCache.class.getName());

  /** The cache service to be used is dynamically configured based on the actual caching service
   * that is deployed (e.g. caching service is pluggable). */
  private CacheService cacheService;

  /**
   * A prefix to be applied to all keys.
   */
  private String keyPrefix = "";

  public void setCacheService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  public void setKeyPrefix(String keyPrefix) {
    this.keyPrefix = keyPrefix;
  }

  private String applyPrefix(String id) {
    return keyPrefix + id;
  }

  @Override
  public String getId(String id) {
    String retVal = null;
    final Object o = cacheService.get(applyPrefix(id));
    if (o != null) {
      retVal = (String)o;
    }

    return retVal;
  }

  @Override
  public void putId(String id) {
    putId(id, 0);
  }

  @Override
  public void putId(String id, long ttl) {
    if (id == null || "".equals(id)) {
      return;
    }

    /** TTL is ignored, as it is set on the cache-level (since not all caching implementations
     * support TTL on a per-key basis).
     */
    cacheService.set(applyPrefix(id), id);
  }

  @Override
  public void close() throws IOException {
    // NoOp - Underling cache implementation is getting destroyed automatically.
  }
}
