package com.eurodyn.qlack2.util.sso;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.cxf.rs.security.saml.sso.SSOConstants;
import org.keycloak.saml.BaseSAML2BindingBuilder;
import org.keycloak.saml.SAML2LogoutRequestBuilder;
import org.keycloak.saml.common.util.DocumentUtil;
import org.w3c.dom.Document;

/**
 * <p>Create a SAML LogoutRequest using the HTTP-POST binding.</p>
 * <p>The request is included in a self sending XHTML form containing that is send as response to
 * the caller. So that the form can be sent automatically, the current URL location of the browser
 * (window.location.href) must be set to this service address.</p>
 */
@Path("glo")
public class SamlHttpPostLogoutService {

  private String issuerId;
  private String idpLogoutAddress;
  private String signatureUsername;
  private String keystorePassword;
  private String keystoreFile;

  @GET
  public Response logout(@CookieParam(SSOConstants.RELAY_STATE) Cookie relayStateCookie)
    throws Exception {

    ResponseBuilder responseBuilder = Response.status(200);

    // create the self sending XHTML form
    StringBuilder response = createForm(relayStateCookie);

    return responseBuilder.entity(response.toString()).build();

  }

  private StringBuilder createForm(@CookieParam(SSOConstants.RELAY_STATE) Cookie relayStateCookie)
    throws Exception {
    StringBuilder response = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
      .append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"")
      .append("\"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">")
      .append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">")
      .append("<body onload=\"document.forms[0].submit()\">")
      .append("<noscript>")
      .append("<p>")
      .append("<strong>Note:</strong> Since your browser does not support JavaScript,")
      .append("you must press the Continue button once to proceed.")
      .append("</p>")
      .append("</noscript>")
      .append("<form method=\"post\" action=\"").append(idpLogoutAddress).append("\" >");

    // add the SAML LogoutRequest form parameter
    response.append("<div><input type=\"hidden\" name=\"SAMLRequest\" value=\"");
    String signedSAMLLogoutRequest = createSignedSAMLLogoutRequest();
    response.append(Base64.getEncoder().encodeToString(signedSAMLLogoutRequest.getBytes()))
      .append("\" />");

    // add the RelayState form parameter if exists
    if (relayStateCookie != null) {
      response.append("<input type=\"hidden\" name=\"RelayState\" value=\"")
        .append(relayStateCookie.getValue()).append("\" />");
    }

    // JavaScript code to send the form automatically
    response.append("</div><noscript>")
      .append("<div>")
      .append("<input type=\"submit\" value=\"Continue\"/>")
      .append("</div>")
      .append("</noscript>")
      .append("</form>")
      .append("</body>")
      .append("</html>");
    return response;
  }

  private String createSignedSAMLLogoutRequest() throws Exception {
    SAML2LogoutRequestBuilder builder = new SAML2LogoutRequestBuilder();
    Document doc = builder.issuer(issuerId).destination(idpLogoutAddress).buildDocument();

    BaseSAML2BindingBuilder ccc = new BaseSAML2BindingBuilder();
    ccc.signWith(signatureUsername, getKeyPair(), (X509Certificate) getCertificate());
    ccc.signDocument(doc);

    return DocumentUtil.asString(doc);
  }

  private Certificate getCertificate() throws Exception {
    FileInputStream is = new FileInputStream(keystoreFile);

    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    keystore.load(is, keystorePassword.toCharArray());

    Key key = keystore.getKey(signatureUsername, keystorePassword.toCharArray());
    if (key instanceof PrivateKey) {
      // Get certificate of public key
      return keystore.getCertificate(signatureUsername);
    }
    return null;
  }

  private KeyPair getKeyPair() throws Exception {
    FileInputStream is = new FileInputStream(keystoreFile);

    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
    keystore.load(is, keystorePassword.toCharArray());

    Key key = keystore.getKey(signatureUsername, keystorePassword.toCharArray());
    if (key instanceof PrivateKey) {
      // Get certificate of public key
      Certificate cert = keystore.getCertificate(signatureUsername);

      // Get public key
      PublicKey publicKey = cert.getPublicKey();

      // Return a key pair
      return new KeyPair(publicKey, (PrivateKey) key);
    }
    return null;
  }

  public void setIssuerId(String issuerId) {
    this.issuerId = issuerId;
  }

  public void setIdpLogoutAddress(String idpLogoutAddress) {
    this.idpLogoutAddress = idpLogoutAddress;
  }

  public void setSignatureUsername(String signatureUsername) {
    this.signatureUsername = signatureUsername;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

  public void setKeystoreFile(String keystoreFile) {
    this.keystoreFile = keystoreFile;
  }

}