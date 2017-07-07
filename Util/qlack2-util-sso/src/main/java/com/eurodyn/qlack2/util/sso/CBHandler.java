package com.eurodyn.qlack2.util.sso;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.wss4j.common.ext.WSPasswordCallback;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class to be used as a callback-handler when the password of the underlying keystore
 * is required.
 */
public class CBHandler implements CallbackHandler {

  /**
   * JUL reference
   */
  private final static Logger LOGGER = Logger.getLogger(CBHandler.class.getName());

  /**
   * The name of the variable holding the keystore password in the properties file specifying the
   * details of the signature.
   */
  private final String KEYSTORE_PASSWORD_KEYNAME = "org.apache.ws.security.crypto.merlin.keystore.password";

  /**
   * The location of the signature properties file to be set by blueprint.
   */
  private String signaturePropertiesFile;

  /**
   * The password of the keystore as discovered during the intialisation of this instance.
   */
  private String keystorePassword;

  public void setSignaturePropertiesFile(String signaturePropertiesFile) {
    this.signaturePropertiesFile = signaturePropertiesFile;
  }

  /**
   * Find and cache the keystore password during initialisation.
   */
  public void init() {
    /** Read the signaturePropertiesFile to find the password for the keystore */
    try (FileReader reader = new FileReader(signaturePropertiesFile)) {
      Properties p = new Properties();
      p.load(reader);
      keystorePassword = (String) p.get(KEYSTORE_PASSWORD_KEYNAME);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Could not read 'signaturePropertiesFile'.", e);
    }
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    if (callbacks.length > 0) {
      WSPasswordCallback wsPasswordCallback = (WSPasswordCallback) callbacks[0];
      wsPasswordCallback.setPassword(keystorePassword);
    }
  }
}
