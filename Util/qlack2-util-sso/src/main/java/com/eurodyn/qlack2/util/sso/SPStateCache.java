package com.eurodyn.qlack2.util.sso;

import com.eurodyn.qlack2.fuse.caching.api.CacheService;
import org.apache.cxf.rs.security.saml.sso.state.RequestState;
import org.apache.cxf.rs.security.saml.sso.state.ResponseState;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the {@link SPStateManager} interface based on qlack2-fuse-caching module, so
 * that the underlying caching implementation is pluggable. If you need to support SSO in a
 * clustered environment make sure you choose a qlack2-fuse-caching implementation that supports a
 * distributed/central cache (e.g. qlack2-fuse-caching-redis).
 *
 * Note on the TTL of this cache: This cache maintains the connection between an incoming user
 * request and a previously established SSO session. Each incoming request comes with a cookie
 * specifying an ID identifying the previously established SSO session. The cache is keeping the
 * association of the value of this cookie (the 'key' of the cache) to the SAML assertions offered
 * for this user (the 'value' of this cache). Thus, for as long as there is a entry on this cache
 * matching the cookie value you may considered your user as 'trusted' and 'authenticated'. It is up
 * to you to define for how long this should take place by specifying the TTL of the underlying
 * cache service. In case the incoming user request does not have an SSO cookie, or if the value of
 * the cookie can not be found in cache, the user is redirected back to the IdP to re-authenticate
 * (you should be aware that the IdP maintains its own cookie identifying previously logged-in
 * users, so such redirection may actually be transparent to your end-user).
 */
public class SPStateCache implements SPStateManager {

  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger.getLogger(SPStateCache.class.getName());

  /**
   * The cache service to be used is dynamically configured based on the actual caching service
   * that is deployed (e.g. caching service is pluggable).
   */
  private CacheService cacheService;

  /**
   * A prefix to be applied to all keys.
   */
  private String keyPrefix = "";

  private String requestCachePrefix = "request:";
  private String responseCachePrefix = "response:";

  public void setCacheService(CacheService cacheService) {
    this.cacheService = cacheService;
  }

  public void setKeyPrefix(String keyPrefix) {
    this.keyPrefix = keyPrefix;
  }

  private String applyRequestPrefix(String key) {
    if (key.startsWith(keyPrefix + requestCachePrefix)) {
      return key;
    }
    return keyPrefix + requestCachePrefix + key;
  }

  private String applyResponsePrefix(String key) {
    if (key.startsWith(keyPrefix + responseCachePrefix)) {
      return key;
    }
    return keyPrefix + responseCachePrefix + key;
  }

  @Override
  public ResponseState getResponseState(String contextKey) {
    ResponseState retVal = null;
    String key = applyResponsePrefix(contextKey);
    final Object o = cacheService.get(key);
    if (o != null) {
      retVal = (ResponseState) o;
    }
    LOGGER.log(Level.FINE, MessageFormat.format("getResponseState for key: {0} = {1}",
      new Object[]{key, retVal}));

    return retVal;
  }

  @Override
  public void setResponseState(String contextKey, ResponseState state) {
    String key = applyResponsePrefix(contextKey);
    LOGGER.log(Level.FINE, MessageFormat.format("setResponseState for key: {0} = {1}",
      new Object[]{key, state}));
    cacheService.set(key, state);
  }

  @Override
  public void setRequestState(String relayState, RequestState state) {
    String key = applyRequestPrefix(relayState);
    LOGGER.log(Level.FINE, MessageFormat.format("setRequestState for key: {0} = {1}",
      new Object[]{key, state}));
    cacheService.set(key, state);
  }

  public RequestState getRequestState(String relayState) {
    String key = applyRequestPrefix(relayState);
    LOGGER.log(Level.FINE, MessageFormat.format("removeRequestState for key: {0}", key));
    RequestState retVal = null;
    final Object o = cacheService.get(key);
    if (o != null) {
      retVal = (RequestState) o;
    }

    return retVal;
  }

  @Override
  public RequestState removeRequestState(String contextKey) {
    String key = applyRequestPrefix(contextKey);
    LOGGER.log(Level.FINE, MessageFormat.format("removeRequestState for key: {0}", key));
    final RequestState requestState = getRequestState(key);
    if (requestState != null) {
      cacheService.deleteByKeyName(key);
    }

    return requestState;
  }

  @Override
  public ResponseState removeResponseState(String contextKey) {
    String key = applyResponsePrefix(contextKey);
    LOGGER.log(Level.FINE, MessageFormat.format("removeResponseState for key: {0}", key));
    final ResponseState responseState = getResponseState(key);
    if (responseState != null) {
      cacheService.deleteByKeyName(key);
    }

    return responseState;
  }

  @Override
  public void close() throws IOException {
    // NoOp - Underling cache implementation is getting destroyed automatically.
  }
}
