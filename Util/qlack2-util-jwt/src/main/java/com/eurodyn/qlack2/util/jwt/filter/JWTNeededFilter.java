package com.eurodyn.qlack2.util.jwt.filter;

import com.eurodyn.qlack2.common.util.util.TokenHolder;
import com.eurodyn.qlack2.util.jwt.JWTUtil;
import com.eurodyn.qlack2.util.jwt.annotations.JWTNeeded;
import com.eurodyn.qlack2.util.jwt.api.JWTClaimsRequest;
import com.eurodyn.qlack2.util.jwt.api.JWTClaimsResponse;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An authentication filter checking for a JWT and validating it. It also keeps the validated JWT
 * inside a {@link TokenHolder} class.
 */
@Provider
@JWTNeeded
@Priority(Priorities.AUTHENTICATION)
public class JWTNeededFilter implements ContainerRequestFilter {
  // Logger reference.
  private static final Logger LOGGER = Logger.getLogger(JWTNeededFilter.class.getName());

  // The secret to use to verify the signature in JWT.
  private String secret;

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    LOGGER.log(Level.FINEST, "Setting secret to: {0}", secret);
    this.secret = secret;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    // Get the HTTP Authorization header from the request
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    if (StringUtils.isNotBlank(authorizationHeader)) {
      // Extract the token from the HTTP Authorization header
      String jwt = authorizationHeader.substring("Bearer".length()).trim();

      // Validate JWT.
      if (StringUtils.isNotEmpty(jwt)) {
        final JWTClaimsResponse claims = JWTUtil.getClaims(new JWTClaimsRequest(jwt, secret));
        if (!claims.isValid()) {
          LOGGER.log(Level.INFO, "Request had an invalid JWT: {0}", claims.getErrorMessage());
          requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        } else {
          // Keep JWT in a thread-local variable.
          // IMPORTANT: Always setup a CXF interceptor filter to clean-up TokenHolder.
          TokenHolder.setToken(jwt);
        }
      } else {
        LOGGER.log(Level.INFO, "Could not find a JWT.");
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
      }
    } else {
      LOGGER.log(Level.INFO, "Could not find {0} header.", HttpHeaders.AUTHORIZATION);
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
  }
}