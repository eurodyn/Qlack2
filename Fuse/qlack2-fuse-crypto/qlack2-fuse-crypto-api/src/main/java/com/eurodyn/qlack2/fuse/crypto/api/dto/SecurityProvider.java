package com.eurodyn.qlack2.fuse.crypto.api.dto;

public class SecurityProvider {

  private String name;
  private double version;
  private String info;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getVersion() {
    return version;
  }

  public void setVersion(double version) {
    this.version = version;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public SecurityProvider(String name, double version, String info) {
    this.name = name;
    this.version = version;
    this.info = info;
  }

  @Override
  public String toString() {
    return "SecurityProvider{" +
        "name='" + name + '\'' +
        ", version=" + version +
        ", info='" + info + '\'' +
        '}';
  }
}
