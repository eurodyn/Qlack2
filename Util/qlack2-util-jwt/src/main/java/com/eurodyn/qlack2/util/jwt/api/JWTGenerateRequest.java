package com.eurodyn.qlack2.util.jwt.api;

import java.util.HashMap;
import java.util.Map;

/**
 * A class encapsulating all data necessary to create a JWT.
 */
public class JWTGenerateRequest {
  // The secret to be used to sign the JWT. Use only Latin-1/ISO-8859-1 characters.
  private String secret;

  // The subject of this JWT. It can be anything as long as it makes sense to your application.
  private String subject;

  // The issuer of this JWT. It can be anything as long as it makes sense to your application.
  private String issuer;

  // The Id to isse the JWT with.
  private String id;

  // Claims to be included in the JWT.
  private Map<String, Object> claims = new HashMap<>();

  // The Time-To-Live (TTL) for the token in milliseconds. This is effectively setting the
  // expiration date for the JWT.
  private long ttl;

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public long getTtl() {
    return ttl;
  }

  public void setTtl(long ttl) {
    this.ttl = ttl;
  }

  public Map<String, Object> getClaims() {
    return claims;
  }

  public void setClaims(Map<String, Object> claims) {
    this.claims = claims;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void addClaim(String name, String value) {
    claims.put(name, value);
  }
}
