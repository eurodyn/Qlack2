package com.eurodyn.qlack2.fuse.crypto.impl.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.eurodyn.qlack2.fuse.crypto.api.CryptoService;
import com.eurodyn.qlack2.fuse.crypto.api.dto.CreateKeyPairRequest;
import com.eurodyn.qlack2.fuse.crypto.api.dto.KeystoreKey;
import com.eurodyn.qlack2.fuse.crypto.impl.conf.ITTestConf;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class CryptoServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  CryptoService cryptoService;

  @Inject
  BundleContext bundleContext;

  @Test
  public void hmacSha256() throws InvalidKeyException, NoSuchAlgorithmException {
    String secret = "abc123";
    String message = "Hello world";
    String hmac = "1274974a679bd37e3325a335ef86b1253d3bdf4075c66090fa36d24d81812343";
    assertEquals(hmac, cryptoService.hmacSha256(secret, message, Charset.defaultCharset()));
  }

  @Test
  public void md5() throws NoSuchAlgorithmException {
    String message = "Hello world";
    String md5 = "3e25960a79dbc69b674cd4ec67a72c62";
    assertEquals(md5, cryptoService.md5(message));
  }

  @Test
  public void getSecurityProviders() {
    String knownProvider = "SUN";
    assertTrue(
      cryptoService.getSecurityProviders().stream()
        .filter(o -> o.getName().indexOf(knownProvider) > -1).count() > 0);
  }

  @Test
  public void getAlgorithmTypes() {
    String knownAlgorithm = "Cipher";
    assertTrue(cryptoService.getAlgorithmTypes().contains(knownAlgorithm));
  }

  @Test
  public void getSecurityServicesForAlgorithmType() {
    String knownProvider = "SUN";
    String knownAlgorithm = "KeyFactory";
    assertTrue(
      cryptoService.getSecurityServicesForAlgorithmType(knownAlgorithm).stream()
        .filter(o -> o.getProvider().equals(knownProvider)).count() > 0);
  }

  @Test
  public void generateKey() throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
    Key key = cryptoService.generateKey(256, "AES");
    assertNotNull(key);
    key = cryptoService.generateKey(256, "SunJCE", "AES");
    assertNotNull(key);
  }

  @Test
  public void keyToFromString()
    throws NoSuchAlgorithmException, IOException, NoSuchProviderException {
    Key key = cryptoService.generateKey(256, "AES");
    final String keyString = cryptoService.keyToString(key);
    assertEquals(key, cryptoService.stringToKey(keyString, "AES"));
  }

  @Test
  public void createKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
    CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
    createKeyPairRequest.setGeneratorAlgorithm("RSA");
    KeyPair keyPair = cryptoService.createKeyPair(createKeyPairRequest);
    assertNotNull(keyPair.getPublic());
    assertNotNull(keyPair.getPrivate());

    createKeyPairRequest.setGeneratorProvider("SunRsaSign");
    keyPair = cryptoService.createKeyPair(createKeyPairRequest);
    assertNotNull(keyPair.getPublic());
    assertNotNull(keyPair.getPrivate());

    keyPair = cryptoService.createKeyPair(createKeyPairRequest);
    createKeyPairRequest.setSecretAlgorithm("SecureRandom");
    createKeyPairRequest.setSecretProvider("SUN");
    assertNotNull(keyPair.getPublic());
    assertNotNull(keyPair.getPrivate());
  }

  @Test
  public void encryptDecrypt()
    throws NoSuchAlgorithmException, IOException, NoSuchProviderException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, CertificateException, KeyStoreException, UnrecoverableKeyException {
    String message = "Hello world";

    // Symmetric encryption.
    Key key = cryptoService.generateKey(128, "Blowfish");
    final byte[] encryptedMessageSymmetric = cryptoService
      .encrypt("Blowfish", key, message.getBytes(StandardCharsets.UTF_8));
    assertEquals(message,
      new String(cryptoService.decrypt("Blowfish", key, encryptedMessageSymmetric)));

    // Asymmetric encryption.
    CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
    createKeyPairRequest.setGeneratorAlgorithm("RSA");
    KeyPair keyPair = cryptoService.createKeyPair(createKeyPairRequest);
    final byte[] encryptedMessageAsymmetric = cryptoService
      .encrypt("RSA", keyPair.getPublic(), message.getBytes(StandardCharsets.UTF_8));
    assertEquals(message,
      new String(cryptoService.decrypt("RSA", keyPair.getPrivate(), encryptedMessageAsymmetric)));

    // Read from keystore.
    final KeystoreKey keystoreKey = cryptoService
      .readKeyFromKeystore(bundleContext.getBundle().getResource("/keystore.jks").openStream(),
        "changeit", "key1", "changeit");
    assertNotNull(keystoreKey.getCertificate());
    assertNotNull(keystoreKey.getPrivateKey());
    assertNotNull(keystoreKey.getPublicKey());

  }

}
