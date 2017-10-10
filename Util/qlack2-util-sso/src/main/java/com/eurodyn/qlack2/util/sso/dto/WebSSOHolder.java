package com.eurodyn.qlack2.util.sso.dto;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * A ThreadLocal holder for SAML attributed decoded from the incoming SAML Response.
 */
public class WebSSOHolder {

  private static final ThreadLocal<List<SAMLAttributeDTO>> attributes = new ThreadLocal<>();

  public static List<SAMLAttributeDTO> getAttributes() {
    return attributes.get();
  }

  public static Optional<SAMLAttributeDTO> getAttribute(String attributeName) {

    return CollectionUtils.isNotEmpty(attributes.get()) ? attributes.get().stream()
      .filter(attr -> attr.getName().equals(attributeName))
      .findFirst() : null;
  }

  public static void setAttributes(List<SAMLAttributeDTO> newAttributes) {
    attributes.set(newAttributes);
  }

  public static void removeAttributes() {
    attributes.remove();
  }
}
