package com.eurodyn.qlack2.util.sso;


import com.eurodyn.qlack2.util.sso.dto.SAMLAttributeDTO;
import com.eurodyn.qlack2.util.sso.dto.WebSSOHolder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper filter to allow testing of your application without an IdP. It aims to replace
 * {@link org.apache.cxf.rs.security.saml.sso.SamlRedirectBindingFilter} or
 * {@link org.apache.cxf.rs.security.saml.sso.SamlPostBindingFilter} with a filter that does not
 * actually take into account SSO/SAMLv2 but instead injects user-defined SAML claims into the
 * calling sequence.
 */
public class FakeSSOFilter implements ContainerRequestFilter {

  /**
   * A list of fake attributes to include. Make sure this an even numbered list, with the name
   * of the attribute followed by the value of the attribute, e.g.
   * <pre>attr1,my-attr1-value,attr2,my-attr2-value</pre>
   */
  private List<String> fakeAttributes;

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
    WebSSOHolder.setAttributes(getFakeAttributesAsSAMLAttributes());
  }
}
