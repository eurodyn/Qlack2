package com.eurodyn.qlack2.util.jwt.api;

/**
 * A class encapsulating all data necessary to verify a JWT.
 */
public class JWTClaimsRequest extends JWTToken {
  // The secret used to sign the JWT. Use only Latin-1/ISO-8859-1 characters.
  private String secret;

  // The amount of seconds local and remote clocks can drift to still consider the expiration
  // of the JWT valid.
  private long allowedTimeSkew = 0;

  public JWTClaimsRequest(String jwt, String secret) {
    super();
    this.setJwt(jwt);
    this.secret = secret;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public long getAllowedTimeSkew() {
    return allowedTimeSkew;
  }

  public void setAllowedTimeSkew(long allowedTimeSkew) {
    this.allowedTimeSkew = allowedTimeSkew;
  }
}
