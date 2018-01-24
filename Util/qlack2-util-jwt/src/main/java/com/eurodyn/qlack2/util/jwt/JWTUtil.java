package com.eurodyn.qlack2.util.jwt;

import com.eurodyn.qlack2.util.jwt.api.JWTClaimsRequest;
import com.eurodyn.qlack2.util.jwt.api.JWTClaimsResponse;
import com.eurodyn.qlack2.util.jwt.api.JWTGenerateRequest;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.binary.Base64;

import java.security.Key;
import java.util.Date;

/**
 * A utility class to generate and valiate JSON Web Tokens.
 */
public class JWTUtil {

  private JWTUtil() {
  }

  /**
   * Creates a new JWS based on the parameters specified.
   *
   * @param request The parameters to be used to create the JWT.
   */
  public static final String generateToken(final JWTGenerateRequest request) {
    // The JWT signature algorithm to be used to sign the token.
    final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // Set current time.
    final long nowMillis = System.currentTimeMillis();
    final Date now = new Date(nowMillis);

    // We will sign our JWT with our ApiKey secret
    final byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(
      Base64.encodeBase64String(request.getSecret().getBytes()));
    final Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    // Set the JWT claims.
    final JwtBuilder builder = Jwts.builder()
      .setId(request.getId())
      .setIssuedAt(now)
      .setSubject(request.getSubject())
      .setIssuer(request.getIssuer())
      .signWith(signatureAlgorithm, signingKey);

    // If it has been specified, add the expiration for the token.
    if (request.getTtl() >= 0) {
      final long expMillis = nowMillis + request.getTtl();
      final Date exp = new Date(expMillis);
      builder.setExpiration(exp);
    }

    // Add additional claims if any.
    if (request.getClaims() != null) {
      builder.addClaims(request.getClaims());
    }

    // Builds the JWT and serializes it to a compact, URL-safe string.
    return builder.compact();
  }

  /**
   * Returns the claims found in a JWT while it is also verifying the token.
   *
   * @param request The JWT to be verified together with the secret used to sign it.
   */
  public static JWTClaimsResponse getClaims(JWTClaimsRequest request) {
    JWTClaimsResponse response = new JWTClaimsResponse();

    try {
      response.setClaims(
        Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(
          Base64.encodeBase64String(request.getSecret().getBytes())))
          .setAllowedClockSkewSeconds(request.getAllowedTimeSkew())
          .parseClaimsJws(request.getJwt()).getBody());
      response.setValid(true);
    } catch (Exception e) {
      response.setValid(false);
      response.setErrorMessage(e.getMessage());
    }

    return response;
  }
}
