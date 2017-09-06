package com.eurodyn.qlack2.fuse.crypto.impl;

import com.eurodyn.qlack2.fuse.crypto.api.CryptoService;
import org.junit.Assert;

import java.nio.charset.Charset;

public class CryptoServiceImplTest {

  @org.junit.Test
  public void hmacSha256() throws Exception {
    CryptoService cryptoService = new CryptoServiceImpl();
    String plainText = "Hello world!";
    String secret = "mysecret";
    String sha256Text = "0bf0c541c460bb09c05064c6d7f7bf061d67edbfd1c949ce2e163f10b4f1b1b4";

    Assert.assertEquals(sha256Text, cryptoService.hmacSha256(secret, plainText, Charset.forName
      ("UTF-8")));
  }

  @org.junit.Test
  public void md5() throws Exception {
    CryptoService cryptoService = new CryptoServiceImpl();
    String plainText = "Hello world!";
    String md5Text = "86fb269d190d2c85f6e0468ceca42a20";

    Assert.assertEquals(md5Text, cryptoService.md5(plainText));
  }

}