package com.eurodyn.qlack2.util.sso.interceptors;

import com.eurodyn.qlack2.util.sso.dto.SAMLAttributeDTO;
import com.eurodyn.qlack2.util.sso.dto.WebSSOHolder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.interceptor.JAXRSInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.rs.security.saml.sso.SSOConstants;
import org.apache.cxf.rs.security.saml.sso.state.ResponseState;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * A JAX-RS interceptor integrated with CXF's SSO implementation. It intercepts all incoming
 * requests, checks whether a Web SSO cookie is present (see {@link SSOConstants}), it obtains the
 * SAML assertions for this cookie from the State Manager used during SSO filtering, it decodes all
 * SAML attributes present in the SAML assertion and places them in a {@link ThreadLocal} using
 * {@link com.eurodyn.qlack2.util.sso.dto.WebSSOHolder} to be available further down your calling
 * stack.
 *
 * Some IdPs return attributes with identical names differentiated by some other value (e.g. XML
 * attribute) inside the {@code<Attribute>} tag. For example:
 * <pre>{@code
 *    <saml2:Attribute Name="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"
 *         a:OriginalIssuer="urn:eiam.admin.ch:idp:e-id:CH-LOGIN"
 *         xmlns:a="http://schemas.xmlsoap.org/ws/2009/09/identity/claims">
 *        <saml2:AttributeValue xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 * xsi:type="xs:string">
 *           email1@example.com
 *        </saml2:AttributeValue>
 *    </saml2:Attribute>
 *    <saml2:Attribute Name="http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"
 *         a:OriginalIssuer="uri:eiam.admin.ch:feds"
 *         xmlns:a="http://schemas.xmlsoap.org/ws/2009/09/identity/claims">
 *        <saml2:AttributeValue xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 * xsi:type="xs:string">
 *           email1@example.com
 *        </saml2:AttributeValue>
 *    </saml2:Attribute>
 * }</pre>
 * To be able for the interceptor to still provide you a useful attributes list you can use the
 * 'additionalAttributeAttributes' property. On this property you can define a list of additional
 * attributes to be extracted from the SAML assertions and those attributes will become available
 * under {@link SAMLAttributeDTO}'s additionalAttributes property. For example:
 * <pre>{@code
 * <bean class="com.eurodyn.qlack2.util.sso.interceptors.WebSSOCookieInterceptor">
 *   <property name="additionalAttributeAttributes">
 *     <list>
 *       <value>http://schemas.xmlsoap.org/ws/2009/09/identity/claim</value>
 *       <value>OriginalIssuer</value>
 *     </list>
 *   </parameter>
 * </bean>
 * }</pre>
 * The list of additionalAttributeAttributes should always have an even number of items, the first
 * one being the namespace of the attributes whereas the second one being the name of the
 * attribute.
 */
public class WebSSOCookieInterceptor extends JAXRSInInterceptor {

  private static final Logger LOGGER = Logger.getLogger(WebSSOCookieInterceptor.class.getName());
  private final static String samlNS = "urn:oasis:names:tc:SAML:2.0:assertion";
  private final static String samlAttribute = "Attribute";
  private final static String samlAttributeName = "Name";
  private final static String samlAttributeValue = "AttributeValue";
  private List<String> additionalAttributeAttributes;
  private SPStateManager stateProvider;

  public void setStateProvider(SPStateManager stateProvider) {
    this.stateProvider = stateProvider;
  }

  public void setAdditionalAttributeAttributes(
    List<String> additionalAttributeAttributes) {
    this.additionalAttributeAttributes = additionalAttributeAttributes;
  }

  private List<SAMLAttributeDTO> getAttributes(String samlAssertion)
    throws ParserConfigurationException, IOException, SAXException {
    List<SAMLAttributeDTO> retVal = new ArrayList<>();

    LOGGER.log(Level.FINE, "Getting attributes from: {0}.", samlAssertion);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    InputSource is = new InputSource(new StringReader(samlAssertion));
    Document doc = builder.parse(is);

    final NodeList attributes = doc.getElementsByTagNameNS(samlNS, samlAttribute);
    for (int i = 0; i < attributes.getLength(); i++) {
      final Element item = (Element) attributes.item(i);

      /** Read attribute name and value */
      String attributeName = item.getAttributes().getNamedItem(samlAttributeName).getTextContent();
      String attributeValue = item.getElementsByTagNameNS(samlNS, samlAttributeValue)
        .item(0).getTextContent();
      final SAMLAttributeDTO samlAttributeDTO = new SAMLAttributeDTO(attributeName, attributeValue);

      /** Find additional attributes */
      if (CollectionUtils.isNotEmpty(additionalAttributeAttributes)) {
        for (int j = 0; j < additionalAttributeAttributes.size(); j += 2) {
          String additionalAttributeNS = additionalAttributeAttributes.get(j);
          String additionalAttributeName = additionalAttributeAttributes.get(j + 1);
          String additionalAttribute = item.getAttributes().getNamedItemNS(
            additionalAttributeNS, additionalAttributeName).getTextContent();
          if (StringUtils.isNotEmpty(additionalAttribute)) {
            samlAttributeDTO.addAdditionalAttribute(
              new SAMLAttributeDTO(additionalAttributeNS + ":" + additionalAttributeName,
                additionalAttribute));
          }
        }
      }
      retVal.add(samlAttributeDTO);
    }

    return retVal;
  }

  private String dumpCookie(Cookie cookie) {
    StringBuffer retVal = new StringBuffer("Cookie ");
    retVal.append("Name: " + cookie.getName());
    retVal.append(", Domain: " + cookie.getDomain());
    retVal.append(", Path: " + cookie.getPath());
    retVal.append(", Value: " + cookie.getValue());
    retVal.append(", Age: " + cookie.getMaxAge());
    retVal.append(", Secure: " + cookie.getSecure());

    return retVal.toString();
  }

  @Override
  public void handleMessage(Message message) {
    /** Security note: Make sure this interceptor is behind your SP-filter, so that access to the
     * SAML attributes held in stateProvider are only available to authenticated users.
     */
    HttpServletRequest request = (HttpServletRequest) message
      .get(AbstractHTTPDestination.HTTP_REQUEST);
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies()).forEach(o -> {
        LOGGER.log(Level.FINEST, dumpCookie(o));
      });

      final List<Cookie> ssoCookies = Arrays.stream(request.getCookies())
        .filter(c -> c.getName().equals(SSOConstants.SECURITY_CONTEXT_TOKEN))
        .collect(Collectors.toList());
      if (ssoCookies.size() == 1) {
        String cookieVal = ssoCookies.get(0).getValue();
        LOGGER.log(Level.FINE, "Found SSO cookie with value: {0}.", cookieVal);
        final ResponseState responseState = stateProvider.getResponseState(cookieVal);
        if (responseState != null) {
          LOGGER.log(Level.FINE, "Found SAML assertion for cookie with value: {0}.", cookieVal);
          try {
            WebSSOHolder.setAttributes(getAttributes(responseState.getAssertion()));
          } catch (IOException | SAXException | ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, "Could not parse attributes in SAML.", e);
          }
        } else {
          LOGGER.log(Level.FINE, "Could not find SAML assertion for cookie with value: {0}.",
            cookieVal);
        }
      }
    }
  }

}
