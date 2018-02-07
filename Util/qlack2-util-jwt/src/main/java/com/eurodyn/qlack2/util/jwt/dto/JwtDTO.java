package com.eurodyn.qlack2.util.jwt.dto;

/**
 * A placeholder for a JWT.
 */
public class JwtDTO {
  private String jwt;

  public JwtDTO(String jwt) {
    this.jwt = jwt;
  }

  public String getJwt() {
    return jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }
}
