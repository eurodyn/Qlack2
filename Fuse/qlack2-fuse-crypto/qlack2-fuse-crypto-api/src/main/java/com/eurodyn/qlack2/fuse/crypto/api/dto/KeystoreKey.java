package com.eurodyn.qlack2.fuse.crypto.api.dto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class KeystoreKey {
  private Certificate certificate;
  private PublicKey publicKey;
  private PrivateKey privateKey;

  public KeystoreKey() {
  }

  public KeystoreKey(Certificate certificate, PublicKey publicKey,
      PrivateKey privateKey) {
    this.certificate = certificate;
    this.publicKey = publicKey;
    this.privateKey = privateKey;
  }

  public Certificate getCertificate() {
    return certificate;
  }

  public void setCertificate(Certificate certificate) {
    this.certificate = certificate;
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(PublicKey publicKey) {
    this.publicKey = publicKey;
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  public void setPrivateKey(PrivateKey privateKey) {
    this.privateKey = privateKey;
  }
}
