/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License").
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package com.eurodyn.qlack2.fuse.crypto.api;

import com.eurodyn.qlack2.fuse.crypto.api.dto.CreateKeyPairRequest;
import com.eurodyn.qlack2.fuse.crypto.api.dto.KeystoreKey;
import com.eurodyn.qlack2.fuse.crypto.api.dto.SecurityProvider;
import com.eurodyn.qlack2.fuse.crypto.api.dto.SecurityService;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

public interface CryptoService {

  /**
   * Generates a SHA-256 based HMAC.
   *
   * @param secret The secret text to use.
   * @param message The message to hash.
   * @param charset The character set to use.
   * @return Returns the text (hex) representation of the
   */
  String hmacSha256(String secret, String message, Charset charset)
    throws NoSuchAlgorithmException, InvalidKeyException;

  /**
   * Calculates the MD5 hash of a message.
   *
   * @param message The message to hash.
   * @return Returns the text (hex) representation of the hash.
   */
  String md5(String message) throws NoSuchAlgorithmException;

  /**
   * Returns a list with all registered security providers.
   *
   * @return Returns a list with all registered security providers.
   */
  List<SecurityProvider> getSecurityProviders();

  /**
   * Returns a list with all registered security services for the specific security provier.
   *
   * @param providerName The name of the provider to get the security services for.
   * @return Returns the list of security services for the specific provider.
   */
  List<SecurityService> getSecurityServices(String providerName);

  /**
   * Returns a list of security services providing a specific algorithm.
   *
   * @param algorithmType The algorithm type to search for.
   * @return Returns a list of security services providing a specific algorithm.
   */
  List<SecurityService> getSecurityServicesForAlgorithmType(String algorithmType);

  /**
   * Returns all available security algorithm types.
   *
   * @return Returns all available security algorithm types.
   */
  List<String> getAlgorithmTypes();

  /**
   * Generates a new key.
   *
   * @param bits The bits of the key.
   * @param provider The provider of the key.
   * @param algorithm The algorithm to use to create the key.
   * @return Returns the generated key.
   */
  Key generateKey(int bits, String provider, String algorithm)
    throws NoSuchAlgorithmException, NoSuchProviderException;

  /**
   * Generates a new key.
   *
   * @param bits The bits of the key.
   * @param algorithm The algorithm to use to create the key.
   * @return Returns the generated key.
   */
  Key generateKey(int bits, String algorithm)
    throws IOException, NoSuchAlgorithmException, NoSuchProviderException;

  /**
   * Returns a text (base64) representation of a key.
   *
   * @param key The key to convert to text.
   * @return Returns the text representation of a key.
   */
  String keyToString(Key key);

  /**
   * Converts a text representation of a key (in base64) back to a key.
   *
   * @param key The text representation of the key.
   * @param keyAlgorithm The algorithm with which this key was created.
   * @return Returns the Key based on the text representation passed.
   */
  Key stringToKey(String key, String keyAlgorithm);

  /**
   * Generates a new public/private key-pair.
   *
   * @param createKeyPairRequest The details of the key-pair to generate.
   * @return Returns the generated key-pair.
   */
  KeyPair createKeyPair(CreateKeyPairRequest createKeyPairRequest)
    throws NoSuchProviderException, NoSuchAlgorithmException;

  /**
   * Encrypts a message.
   *
   * @param algorithm The algorithm to use to encrypt. A security provider providing this algorithm
   * will be randomly chosen.
   * @param key The key to use to encrypt.
   * @param clearText The message to encrypt.
   * @return Returns the encrypted message.
   */
  byte[] encrypt(String algorithm, Key key, byte[] clearText)
    throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
    BadPaddingException, NoSuchProviderException, InvalidKeyException;

  /**
   * Encrypts a message.
   *
   * @param provider The security provider to use for encryption.
   * @param algorithm The algorithm to use to encrypt.
   * @param key The key to use to encrypt.
   * @param clearText The message to encrypt.
   * @return Returns the encrypted message.
   */
  byte[] encrypt(String provider, String algorithm, Key key, byte[] clearText)
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
    NoSuchProviderException, BadPaddingException, IllegalBlockSizeException;

  /**
   * Decrypts a message.
   *
   * @param algorithm The algorithm to use for decryption. A security provider providing this
   * algorithm will be randomly chosen.
   * @param key The key to use for decryption.
   * @param encryptedMessage The encrypted message to decrypt.
   * @return Returns the decrypted message.
   */
  byte[] decrypt(String algorithm, Key key, byte[] encryptedMessage)
    throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
    BadPaddingException, NoSuchProviderException, InvalidKeyException;

  /**
   * Decrypts a message.
   *
   * @param provider The security provider to use for decryption.
   * @param algorithm The algorithm to use for decryption.
   * @param key The key to use for decryption.
   * @param encryptedMessage The encrypted message to decrypt.
   * @return Returns the decrypted message.
   */
  byte[] decrypt(String provider, String algorithm, Key key, byte[] encryptedMessage)
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
    NoSuchProviderException, BadPaddingException, IllegalBlockSizeException;

  /**
   * Reads a key from a keystore file.
   * @param keystore The keystore to read from.
   * @param keystorePassword The password of the keystore.
   * @param keyName The name of the key to read.
   * @param keyPassword The password of the key.
   * @param keystoreType The type of the keystore.
   * @param keystoreProvider The provider of the keystore.
   * @return Returns the key (including possible certificate, public and private keys).
   * @throws KeyStoreException
   * @throws IOException
   * @throws CertificateException
   * @throws NoSuchAlgorithmException
   * @throws UnrecoverableKeyException
   * @throws NoSuchProviderException
   */
  KeystoreKey readKeyFromKeystore(InputStream keystore, String keystorePassword,
    String keyName,
    String keyPassword, String keystoreType, String keystoreProvider)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException;

  /**
   * Reads a key from a keystore file.
   * @param keystore The keystore to read from.
   * @param keystorePassword The password of the keystore.
   * @param keyName The name of the key to read.
   * @param keyPassword The password of the key.
   * @return Returns the key (including possible certificate, public and private keys).
   * @throws KeyStoreException
   * @throws IOException
   * @throws CertificateException
   * @throws NoSuchAlgorithmException
   * @throws UnrecoverableKeyException
   * @throws NoSuchProviderException
   */
  KeystoreKey readKeyFromKeystore(InputStream keystore, String keystorePassword,
    String keyName,
    String keyPassword)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException;

  /**
   * Reads a key from a keystore file.
   * @param keystore The keystore to read from.
   * @param keystorePassword The password of the keystore.
   * @param keyName The name of the key to read.
   * @param keyPassword The password of the key.
   * @param keystoreType The type of the keystore.
   * @param keystoreProvider The provider of the keystore.
   * @return Returns the key (including possible certificate, public and private keys).
   * @throws KeyStoreException
   * @throws IOException
   * @throws CertificateException
   * @throws NoSuchAlgorithmException
   * @throws UnrecoverableKeyException
   * @throws NoSuchProviderException
   */
  KeystoreKey readKeyFromKeystore(File keystore, String keystorePassword, String keyName,
    String keyPassword, String keystoreType, String keystoreProvider)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException;

  /**
   * Reads a key from a keystore file.
   * @param keystore The keystore to read from.
   * @param keystorePassword The password of the keystore.
   * @param keyName The name of the key to read.
   * @param keyPassword The password of the key.
   * @return Returns the key (including possible certificate, public and private keys).
   * @throws KeyStoreException
   * @throws IOException
   * @throws CertificateException
   * @throws NoSuchAlgorithmException
   * @throws UnrecoverableKeyException
   * @throws NoSuchProviderException
   */
  KeystoreKey readKeyFromKeystore(File keystore, String keystorePassword, String keyName,
    String keyPassword)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException;
}
