package com.eurodyn.qlack2.util.sso.dto;

import org.junit.Test;

import java.util.UUID;

public class WebSSOHolderTest {

  @Test
  public void testEmptyAttributes() {
    WebSSOHolder.getAttributes();
  }

  @Test
  public void testEmptyAttribute() {
    WebSSOHolder.getAttribute(UUID.randomUUID().toString());
  }

}