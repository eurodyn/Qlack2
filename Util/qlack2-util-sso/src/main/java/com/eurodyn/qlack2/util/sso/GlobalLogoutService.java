package com.eurodyn.qlack2.util.sso;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.keycloak.saml.BaseSAML2BindingBuilder;
import org.keycloak.saml.SAML2LogoutRequestBuilder;
import org.keycloak.saml.common.util.DocumentUtil;
import org.w3c.dom.Document;

/**
 * <p>Initiates a global logout constructing a self sending HTML form containing a signed
 * SAMLLogoutRequest that is send as response to the caller.</p>
 * <p>So that the form can be sent automatically, the current URL location of the browser
 * window.location.href) must be set to this service address.</p>
 */
@Path("glo")
public class GlobalLogoutService {

  private String issuerId;
  private String idpLogoutAddress;
  private String signatureUsername;
  private String keystorePassword;
  private String keystoreFile;

  @GET
  public Response logout() throws Exception {

    ResponseBuilder responseBuilder = Response.status(200);

    // create the self sending HTML form
    StringBuilder response = new StringBuilder(
      "<form name=\"samlLogoutForm\" method=\"post\" action=\"").append(idpLogoutAddress)
      .append("\" ><input type=\"hidden\" name=\"SAMLLogoutRequest\" value=\"");

    String signedSAMLLogoutRequest = createSignedSAMLLogoutRequest();

    response.append(StringEscapeUtils.escapeHtml4(signedSAMLLogoutRequest)).append(
      "\" /><input type=\"submit\" value=\"Submit\" style=\"display:none;\"/> </form>")
      .append(
        "<SCRIPT TYPE=\"text/JavaScript\">document.forms[\"samlLogoutForm\"].submit();</SCRIPT>");

    return responseBuilder.entity(response.toString()).build();

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