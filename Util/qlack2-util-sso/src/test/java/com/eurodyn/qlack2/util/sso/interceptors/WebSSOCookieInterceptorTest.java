package com.eurodyn.qlack2.util.sso.interceptors;

import com.eurodyn.qlack2.util.sso.dto.SAMLAttributeDTO;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class WebSSOCookieInterceptorTest {

  /**
   * Note: Testing private methods is not recommended, however as we need to test specific XMLs
   * here without actually trying to parse a whole message from CXF we make an exception.
   */
  @Test
  public void testGetAttributes()
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = WebSSOCookieInterceptor.class.getDeclaredMethod("getAttributes", String.class);
    method.setAccessible(true);
    String xml = new Scanner(Thread.currentThread().getContextClassLoader()
      .getResourceAsStream("saml-assertions.xml"), "UTF-8").useDelimiter("\\A")
      .next();
    WebSSOCookieInterceptor webSSOCookieInterceptor = new WebSSOCookieInterceptor();
    List<SAMLAttributeDTO> attributes = (List<SAMLAttributeDTO>) method
      .invoke(webSSOCookieInterceptor, xml);

    Assert.assertEquals(3, attributes.size());

    Assert.assertEquals("mail", attributes.get(0).getName());
    Assert.assertEquals("email@example.com", attributes.get(0).getValue());

    Assert.assertEquals("uid", attributes.get(1).getName());
    Assert.assertEquals("user1", attributes.get(1).getValue());

    Assert.assertEquals("cn", attributes.get(2).getName());
    Assert.assertEquals("myfullname", attributes.get(2).getValue());
  }

  @Test
  public void testGetAttributesWithAdditionalAttributes()
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = WebSSOCookieInterceptor.class.getDeclaredMethod("getAttributes", String.class);
    method.setAccessible(true);
    String xml = new Scanner(Thread.currentThread().getContextClassLoader()
      .getResourceAsStream("saml-assertions-with-additional-attributes.xml"), "UTF-8")
      .useDelimiter("\\A")
      .next();
    WebSSOCookieInterceptor webSSOCookieInterceptor = new WebSSOCookieInterceptor();
    webSSOCookieInterceptor.setAdditionalAttributeAttributes(
      Arrays.asList("http://schemas.xmlsoap.org/ws/2009/09/identity/claims", "OriginalIssuer"));

    List<SAMLAttributeDTO> attributes = (List<SAMLAttributeDTO>) method
      .invoke(webSSOCookieInterceptor, xml);

    Assert.assertEquals(2, attributes.size());

    Assert.assertEquals("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", attributes.get(0).getName());
    Assert.assertEquals("example@example.com", attributes.get(0).getValue());
    Assert.assertEquals(1, attributes.get(0).getAdditionalAttributes().size());
    Assert.assertEquals("http://schemas.xmlsoap.org/ws/2009/09/identity/claims:OriginalIssuer",
      attributes.get(0).getAdditionalAttributes().get(0).getName());
    Assert.assertEquals("urn:eiam.admin.ch:idp:e-id:CH-LOGIN",
      attributes.get(0).getAdditionalAttributes().get(0).getValue());

    Assert.assertEquals("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress", attributes.get(1).getName());
    Assert.assertEquals("example2@example.com", attributes.get(1).getValue());
    Assert.assertEquals(1, attributes.get(1).getAdditionalAttributes().size());
    Assert.assertEquals("http://schemas.xmlsoap.org/ws/2009/09/identity/claims:OriginalIssuer",
      attributes.get(1).getAdditionalAttributes().get(0).getName());
    Assert.assertEquals("uri:eiam.admin.ch:feds",
      attributes.get(1).getAdditionalAttributes().get(0).getValue());
  }
}