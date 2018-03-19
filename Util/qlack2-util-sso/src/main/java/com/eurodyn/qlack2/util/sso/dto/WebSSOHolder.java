package com.eurodyn.qlack2.util.sso.dto;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A ThreadLocal holder for SAML attributed decoded from the incoming SAML Response.
 */
public class WebSSOHolder {
  // The TL held list of attributes found.
  private static final ThreadLocal<List<SAMLAttributeDTO>> attributes = new ThreadLocal<>();

  /**
   * Returns all attributes found.
   * @return The list of attributes found.
   */
  public static List<SAMLAttributeDTO> getAttributes() {
    return attributes.get();
  }

  /**
   * Returns the first attribute matching the requested name.
   * @param attributeName The attribute name to lookup.
   * @return The matching attribute or an {@link Optional}.empty().
   */
  public static Optional<SAMLAttributeDTO> getAttribute(String attributeName) {
    return CollectionUtils.isNotEmpty(attributes.get()) ? attributes.get().stream()
      .filter(attr -> attr.getName().equals(attributeName))
      .findFirst() : Optional.empty();
  }

  /**
   * Returns all the attributes matching the requested name.
   * @param attributeName The attribute name to lookup.
   * @return The matching attribute or an empty list.
   */
  public static List<SAMLAttributeDTO> getAttributes(String attributeName) {
    return CollectionUtils.isNotEmpty(attributes.get()) ? attributes.get().stream()
      .filter(attr -> attr.getName().equals(attributeName)).collect(Collectors.toList())
      : new ArrayList<>();
  }

  /**
   * Sets the value of all attributes.
   * @param newAttributes The new attributes to set.
   */
  public static void setAttributes(List<SAMLAttributeDTO> newAttributes) {
    attributes.set(newAttributes);
  }

  /**
   * Removes all attributes found.
   */
  public static void removeAttributes() {
    attributes.remove();
  }
}
