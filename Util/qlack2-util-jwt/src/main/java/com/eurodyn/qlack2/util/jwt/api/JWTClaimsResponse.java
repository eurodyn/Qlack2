package com.eurodyn.qlack2.util.jwt.api;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.Map;

/**
 * Encapsulates a reply from a verification request. It contains an indicator of whether the JWT
 * was found to be valid, together with an error description in case the token was invalid.
 */
public class JWTClaimsResponse {
  // Indicates whether this JWT successfully passed verifiction or not.
  private boolean valid;

  // The error message resulted during an unsuccessfull verification.
  private String errorMessage;

  // The claims found on the JWT. This map contains all standard claims as well as custom claims
  // placed into the original JWT. Standard claims are named after their RFC-defined names
  // (https://tools.ietf.org/html/rfc7519), however this class also provide helper getters to
  // access them (e.g. getIssuer(), getIssuedAt(), etc.
  private Map<String, Object> claims;

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Map<String, Object> getClaims() {
    return claims;
  }

  public void setClaims(Map<String, Object> claims) {
    this.claims = claims;
  }

  public String getIssuer() {
    return (String)claims.get(Claims.ISSUER);
  }

  public String getId() {
    return (String)claims.get(Claims.ID);
  }

  public Date getIssuedAt() {
    return new Date((int)claims.get(Claims.ISSUED_AT) * 1000l);
  }

  public Date getExpiresAt() {
    return new Date((int)claims.get(Claims.EXPIRATION) * 1000l);
  }

  public String getSubject() {
    return (String)claims.get(Claims.SUBJECT);
  }
}
