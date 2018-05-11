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
package com.eurodyn.qlack2.fuse.crypto.impl;


import com.eurodyn.qlack2.fuse.crypto.api.CryptoService;
import com.eurodyn.qlack2.fuse.crypto.api.dto.CreateKeyPairRequest;
import com.eurodyn.qlack2.fuse.crypto.api.dto.KeystoreKey;
import com.eurodyn.qlack2.fuse.crypto.api.dto.SecurityProvider;
import com.eurodyn.qlack2.fuse.crypto.api.dto.SecurityService;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class CryptoServiceImpl implements CryptoService {

  @Override
  public String hmacSha256(String secret, String message, Charset charSet)
    throws NoSuchAlgorithmException, InvalidKeyException {
    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
    SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(),
      "HmacSHA256");
    sha256_HMAC.init(secret_key);

    return Hex.encodeHexString(sha256_HMAC.doFinal(message.getBytes(charSet)));
  }

  @Override
  public String md5(String message) {
    return DigestUtils.md5Hex(message);
  }

  @Override
  public List<SecurityProvider> getSecurityProviders() {
    return Arrays.stream(Security.getProviders())
      .flatMap(o -> Arrays.asList(new SecurityProvider(o.getName(), o.getVersion(), o.getInfo()))
        .stream()
      ).collect(Collectors.toList());
  }

  @Override
  public List<SecurityService> getSecurityServices(String providerName) {
    return getSecurityProviders().stream()
      .filter(o -> o.getName().equals(providerName))
      .flatMap(o -> Arrays.asList(Security.getProvider(o.getName()).getServices()).stream())
      .flatMap(o -> o.stream().flatMap(
        x -> Arrays.asList(new SecurityService(providerName, x.getAlgorithm(), x.getType()))
          .stream()))
      .collect(Collectors.toList());
  }

  @Override
  public List<SecurityService> getSecurityServicesForAlgorithmType(String algorithmType) {
    return getSecurityProviders().stream()
      .flatMap(o -> Arrays.asList(o.getName()).stream())
      .flatMap(o -> getSecurityServices(o).stream())
      .filter(o -> o.getType().equals(algorithmType))
      .collect(Collectors.toList());
  }

  @Override
  public List<String> getAlgorithmTypes() {
    return getSecurityProviders().stream()
      .flatMap(o -> getSecurityServices(o.getName()).stream())
      .flatMap(o -> Arrays.asList(o.getType()).stream())
      .distinct()
      .sorted()
      .collect(Collectors.toList());
  }

  @Override
  public Key generateKey(int bits, String provider, String algorithm)
    throws NoSuchAlgorithmException, NoSuchProviderException {
    KeyGenerator generator;
    if (StringUtils.isBlank(provider)) {
      generator = KeyGenerator.getInstance(algorithm);
    } else {
      generator = KeyGenerator.getInstance(algorithm, provider);
    }
    generator.init(bits, SecureRandom.getInstanceStrong());

    return generator.generateKey();
  }

  @Override
  public Key generateKey(int bits, String algorithm)
    throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
    return generateKey(bits, null, algorithm);
  }

  @Override
  public String keyToString(Key key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  @Override
  public Key stringToKey(String key, String keyAlgorithm) {
    byte[] decodedKey = Base64.getDecoder().decode(key);
    return new SecretKeySpec(decodedKey, 0, decodedKey.length, keyAlgorithm);
  }

  @Override
  public KeyPair createKeyPair(CreateKeyPairRequest createKeyPairRequest)
    throws NoSuchProviderException, NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator;

    // Set the provider and algorithm.
    if (StringUtils.isBlank(createKeyPairRequest.getGeneratorProvider())) {
      keyPairGenerator = KeyPairGenerator.getInstance(createKeyPairRequest.getGeneratorAlgorithm());
    } else {
      keyPairGenerator = KeyPairGenerator
        .getInstance(createKeyPairRequest.getGeneratorAlgorithm(),
          createKeyPairRequest.getGeneratorProvider());
    }

    // Set the secret provider and generator.
    if (StringUtils.isBlank(createKeyPairRequest.getSecretAlgorithm()) || StringUtils
      .isBlank(createKeyPairRequest.getSecretProvider())) {
      keyPairGenerator.initialize(createKeyPairRequest.getKeySize(),
        SecureRandom.getInstanceStrong());
    } else {
      keyPairGenerator.initialize(createKeyPairRequest.getKeySize(),
        SecureRandom.getInstance(createKeyPairRequest.getSecretAlgorithm(),
          createKeyPairRequest.getSecretProvider()));
    }
    return keyPairGenerator.generateKeyPair();
  }

  @Override
  public byte[] encrypt(String algorithm, Key key, byte[] message)
    throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
    BadPaddingException, NoSuchProviderException, InvalidKeyException {
    return encrypt(null, algorithm, key, message);
  }

  @Override
  public byte[] encrypt(String provider, String algorithm, Key key, byte[] message)
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
    NoSuchProviderException, BadPaddingException, IllegalBlockSizeException {
    Cipher cipher;
    if (StringUtils.isBlank(provider)) {
      cipher = Cipher.getInstance(algorithm);
    } else {
      cipher = Cipher.getInstance(algorithm, provider);
    }
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(message);
  }

  @Override
  public byte[] decrypt(String algorithm, Key key, byte[] encryptedMessage)
    throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
    BadPaddingException, NoSuchProviderException, InvalidKeyException {
    return decrypt(null, algorithm, key, encryptedMessage);
  }

  @Override
  public byte[] decrypt(String provider, String algorithm, Key key, byte[] encryptedMessage)
    throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
    NoSuchProviderException, BadPaddingException, IllegalBlockSizeException {
    Cipher cipher;
    if (StringUtils.isBlank(provider)) {
      cipher = Cipher.getInstance(algorithm);
    } else {
      cipher = Cipher.getInstance(algorithm, provider);
    }
    cipher.init(Cipher.DECRYPT_MODE, key);
    return cipher.doFinal(encryptedMessage);
  }

  @Override
  public KeystoreKey readKeyFromKeystore(InputStream keystore, String keystorePassword,
    String keyName,
    String keyPassword, String keystoreType, String keystoreProvider)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException {
    KeyStore ks;
    if (StringUtils.isBlank(keystoreType) || StringUtils.isBlank(keystoreType)) {
      ks = KeyStore.getInstance(KeyStore.getDefaultType());
    } else {
      ks = KeyStore.getInstance(keystoreType, keystoreProvider);
    }
    try (InputStream fis = new BufferedInputStream(keystore)) {
      ks.load(fis, keystorePassword.toCharArray());
      Key key = ks.getKey(keyName, keyPassword.toCharArray());
      Certificate certificate = ks.getCertificate(keyName);
      return new KeystoreKey(certificate, certificate.getPublicKey(), (PrivateKey) key);
    }
  }

  @Override
  public KeystoreKey readKeyFromKeystore(InputStream keystore, String keystorePassword,
    String keyName,
    String keyPassword)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException {
    return readKeyFromKeystore(keystore, keystorePassword, keyName, keyPassword, null, null);
  }


  @Override
  public KeystoreKey readKeyFromKeystore(File keystore, String keystorePassword, String keyName,
    String keyPassword, String keystoreType, String keystoreProvider)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException {
    return readKeyFromKeystore(new FileInputStream(keystore), keystorePassword, keyName,
      keyPassword, keystoreType, keystorePassword);
  }

  @Override
  public KeystoreKey readKeyFromKeystore(File keystore, String keystorePassword, String keyName,
    String keyPassword)
    throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
    UnrecoverableKeyException, NoSuchProviderException {
    return readKeyFromKeystore(keystore, keystorePassword, keyName, keyPassword, null, null);
  }
}