package com.eurodyn.qlack2.fuse.crypto.api.dto;

public class SecurityService {
  private String provider;
  private String algorithm;
  private String type;

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public SecurityService(String provider, String algorithm, String type) {
    this.provider = provider;
    this.algorithm = algorithm;
    this.type = type;
  }

  @Override
  public String toString() {
    return "SecurityService{" +
        "provider='" + provider + '\'' +
        ", algorithm='" + algorithm + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
