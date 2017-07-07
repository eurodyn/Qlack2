package com.eurodyn.qlack2.util.sso.dto;

import java.util.ArrayList;
import java.util.List;

public class SAMLAttributeDTO {
  private String name;
  private String value;
  private List<SAMLAttributeDTO> additionalAttributes = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void addAdditionalAttribute(SAMLAttributeDTO additionalAttribute) {
    this.additionalAttributes.add(additionalAttribute);
  }

  public List<SAMLAttributeDTO> getAdditionalAttributes() {
    return additionalAttributes;
  }

  public void setAdditionalAttributes(
    List<SAMLAttributeDTO> additionalAttributes) {
    this.additionalAttributes = additionalAttributes;
  }

  public SAMLAttributeDTO(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String toString() {
    return "SAMLAttributeDTO{" +
      "name='" + name + '\'' +
      ", value='" + value + '\'' +
      ", additionalAttributes=" + additionalAttributes +
      '}';
  }
}
