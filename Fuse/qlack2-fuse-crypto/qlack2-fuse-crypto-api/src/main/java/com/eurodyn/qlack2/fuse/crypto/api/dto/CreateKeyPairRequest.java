package com.eurodyn.qlack2.fuse.crypto.api.dto;

/**
 * A request to create a key-pair, providing default values for most elements.
 */
public class CreateKeyPairRequest {
  // The name of the provider to use while generating the key-pair. If left empty, a provider
  // implementing the requested generatorAlgorithm  will be picked up by the JVM.
  private String generatorProvider;

  // The algorithm to use while generating the key-pair.
  private String generatorAlgorithm;

  // The name of provider to use while initialising the key-pair generator. If left empty,
  // a default SecureRandom.getInstanceStrong() will be used.
  private String secretProvider;

  // The name of algorithm to use while initialising the key-pair generator. If left empty,
  // a default SecureRandom.getInstanceStrong() will be used.
  private String secretAlgorithm;

  // The default bits of the key.
  private int keySize = 2048;

  public String getGeneratorProvider() {
    return generatorProvider;
  }

  public void setGeneratorProvider(String generatorProvider) {
    this.generatorProvider = generatorProvider;
  }

  public String getGeneratorAlgorithm() {
    return generatorAlgorithm;
  }

  public void setGeneratorAlgorithm(String generatorAlgorithm) {
    this.generatorAlgorithm = generatorAlgorithm;
  }

  public String getSecretProvider() {
    return secretProvider;
  }

  public void setSecretProvider(String secretProvider) {
    this.secretProvider = secretProvider;
  }

  public int getKeySize() {
    return keySize;
  }

  public void setKeySize(int keySize) {
    this.keySize = keySize;
  }

  public String getSecretAlgorithm() {
    return secretAlgorithm;
  }

  public void setSecretAlgorithm(String secretAlgorithm) {
    this.secretAlgorithm = secretAlgorithm;
  }
}
