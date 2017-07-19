package com.eurodyn.qlack2.util.sso.dto;

/**
 * Created by kperp on 18.07.2017.
 */
public class LogoutResponse {
  private String mainApplicationAddress;

  public LogoutResponse(String mainApplicationAddress) {
    this.mainApplicationAddress = mainApplicationAddress;
  }

  public String getMainApplicationAddress() {
    return this.mainApplicationAddress;
  }
}
