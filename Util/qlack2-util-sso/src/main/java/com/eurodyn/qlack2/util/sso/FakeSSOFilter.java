package com.eurodyn.qlack2.util.sso;


import com.eurodyn.qlack2.util.sso.dto.SAMLAttributeDTO;
import com.eurodyn.qlack2.util.sso.dto.WebSSOHolder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.rs.security.saml.sso.SSOConstants;

/**
 * This is a helper filter to allow testing of your application without an IdP. It aims to replace
 * {@link org.apache.cxf.rs.security.saml.sso.SamlRedirectBindingFilter} or
 * {@link org.apache.cxf.rs.security.saml.sso.SamlPostBindingFilter} with a filter that does not
 * actually take into account SSO/SAMLv2 but instead injects user-defined SAML claims into the
 * calling sequence.
 */
@Priority(Priorities.AUTHENTICATION)
public class FakeSSOFilter implements ContainerRequestFilter, ContainerResponseFilter {

  /**
   * A list of fake attributes to include. Make sure this an even numbered list, with the name
   * of the attribute followed by the value of the attribute, e.g.
   * <pre>attr1,my-attr1-value,attr2,my-attr2-value</pre>
   */
  private List<String> fakeAttributes;

  private static Map<String, List<SAMLAttributeDTO>> sessions = new ConcurrentHashMap<>();

  public List<SAMLAttributeDTO> getFakeAttributesAsSAMLAttributesFromRequestParms(String uid,
      String email, List<String> roles) {
    List<SAMLAttributeDTO> samlAttrs = new ArrayList<>();
    samlAttrs.add(new SAMLAttributeDTO("uid", uid));
    samlAttrs.add(new SAMLAttributeDTO("email", email));
    if (roles != null && !roles.isEmpty()) {
      samlAttrs.add(new SAMLAttributeDTO("role", StringUtils.join(roles, ",")));
    }
    return samlAttrs;
  }

  public List<SAMLAttributeDTO> getFakeAttributesAsSAMLAttributes() {
    List<SAMLAttributeDTO> retVal = new ArrayList<>();

    for (int i = 0; i < fakeAttributes.size(); i += 2) {
      retVal.add(new SAMLAttributeDTO(fakeAttributes.get(i), fakeAttributes.get(i + 1)));
    }

    return retVal;
  }

  public List<String> getFakeAttributes() {
    return fakeAttributes;
  }

  public void setFakeAttributes(List<String> fakeAttributes) {
    this.fakeAttributes = fakeAttributes;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    List<SAMLAttributeDTO> samlAttrs = new ArrayList<>();
    String cookieFromRequest = getCookieValue(requestContext);
    if (cookieFromRequest != null) {
      Entry<String, Cookie> cookieEntry = extractCookieFromRequestContext(requestContext);
      if (cookieEntry != null && sessions.get(getCookieValue(requestContext)) != null) {
        samlAttrs = sessions.get(getCookieValue(requestContext));
      }
    }

    if (getCookieValue(requestContext) != null && requestContext.getUriInfo().getQueryParameters() != null && !requestContext.getUriInfo().getQueryParameters().isEmpty()) {
      List<String> uid = requestContext.getUriInfo().getQueryParameters().get("uid");
      List<String> email = requestContext.getUriInfo().getQueryParameters().get("email");
      List<String> role = requestContext.getUriInfo().getQueryParameters().get("role");
      if (uid != null && email != null && !uid.isEmpty() && !email.isEmpty()) {
        samlAttrs = getFakeAttributesAsSAMLAttributesFromRequestParms(uid.get(0), email.get(0), role);
        sessions.put(getCookieValue(requestContext), samlAttrs);
      }
    }
    if (samlAttrs.isEmpty()) {
      samlAttrs = getFakeAttributesAsSAMLAttributes();
    }
    WebSSOHolder.setAttributes(samlAttrs);
  }

  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
    throws IOException {
    String cookieUuid = UUID.randomUUID().toString();
    String cookieFromRequest = getCookieValue(requestContext);
    if (cookieFromRequest != null) {
      cookieUuid = cookieFromRequest;
    }
    /** Set a fake Cookie, so that front-end checks believe the user was properly authenticated. */
    responseContext.getHeaders().add("Set-Cookie", SSOConstants.SECURITY_CONTEXT_TOKEN +
      "=fake-sso-cookie_" + cookieUuid + "; Path=/");
  }

  private String getCookieValue(ContainerRequestContext requestContext) {
    Entry<String, Cookie> cookieEntry = extractCookieFromRequestContext(requestContext);
    if (cookieEntry != null) {
      return getUuidFromCookie(cookieEntry.getValue().getValue());
    }
    return null;
  }

  private Entry<String, Cookie> extractCookieFromRequestContext(ContainerRequestContext requestContext) {
    return requestContext.getCookies().entrySet().stream()
        .filter(k -> SSOConstants.SECURITY_CONTEXT_TOKEN.equals(k.getKey())).findFirst().orElse(null);
  }

  private String getUuidFromCookie(String cxfCookie) {
    return cxfCookie.substring(cxfCookie.lastIndexOf('_') + 1);
  }
}
