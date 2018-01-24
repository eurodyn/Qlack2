package com.eurodyn.qlack2.util.jwt;

import static org.awaitility.Awaitility.await;

import com.eurodyn.qlack2.util.jwt.api.JWTClaimsRequest;
import com.eurodyn.qlack2.util.jwt.api.JWTClaimsResponse;
import com.eurodyn.qlack2.util.jwt.api.JWTGenerateRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JWTUtilTest {

  @Test
  public void testGenerateToken() {
    // Generate random values for the request.
    String id = UUID.randomUUID().toString();
    String secret = UUID.randomUUID().toString();
    String issuer = UUID.randomUUID().toString();
    String subject = UUID.randomUUID().toString();

    // Prepare generation request.
    JWTGenerateRequest request = new JWTGenerateRequest();
    request.setId(id);
    request.setIssuer(issuer);
    request.setSubject(subject);
    request.setSecret(secret);
    request.setTtl(10000);

    // Generate JWT and check it is not empty.
    final String jwt = JWTUtil.generateToken(request);
    System.out.println("Secret used = " + secret);
    System.out.println("JWT = " + jwt);
    Assert.assertNotNull(jwt);

    // Decode JWT and compare with original values.
    Claims claims = Jwts.parser()
      .setSigningKey(DatatypeConverter.parseBase64Binary(
        Base64.encodeBase64String(request.getSecret().getBytes())))
      .parseClaimsJws(jwt).getBody();
    Assert.assertEquals(issuer, claims.getIssuer());
    Assert.assertEquals(subject, claims.getSubject());
    Assert.assertEquals(id, claims.getId());
  }

  @Test
  public void testGetClaims() {
    // Generate random values for the request.
    String id = UUID.randomUUID().toString();
    String secret = UUID.randomUUID().toString();
    String issuer = UUID.randomUUID().toString();
    String subject = UUID.randomUUID().toString();

    // Prepare generation request.
    JWTGenerateRequest request = new JWTGenerateRequest();
    request.setId(id);
    request.setIssuer(issuer);
    request.setSubject(subject);
    request.setSecret(secret);
    request.setTtl(10000);

    // Generate a valid JWT and verify it.
    String jwt = JWTUtil.generateToken(request);
    JWTClaimsResponse response = JWTUtil.getClaims(new JWTClaimsRequest(jwt, secret));
    Assert.assertTrue(response.isValid());
    Assert.assertEquals(id, response.getId());
    Assert.assertEquals(issuer, response.getIssuer());
    Assert.assertEquals(subject, response.getSubject());
    Assert.assertEquals(response.getExpiresAt().getTime() - response.getIssuedAt().getTime(),  10000);

    // Change JWT by making it invalid and try to verify it.
    jwt += UUID.randomUUID().toString();
    response = JWTUtil.getClaims(new JWTClaimsRequest(jwt, secret));
    Assert.assertFalse(response.isValid());

    // Generate an invalid/expired JWT and verify it.
    request.setTtl(0);
    jwt = JWTUtil.generateToken(request);
    await().atLeast(100, TimeUnit.MILLISECONDS);
    response = JWTUtil.getClaims(new JWTClaimsRequest(jwt, secret));
    Assert.assertFalse(response.isValid());

    // Test time skew using the above invalid JWT.
    final JWTClaimsRequest jwtVerifyRequest = new JWTClaimsRequest(jwt, secret);
    jwtVerifyRequest.setAllowedTimeSkew(10000);
    response = JWTUtil.getClaims(jwtVerifyRequest);
    Assert.assertTrue(response.isValid());
  }
}