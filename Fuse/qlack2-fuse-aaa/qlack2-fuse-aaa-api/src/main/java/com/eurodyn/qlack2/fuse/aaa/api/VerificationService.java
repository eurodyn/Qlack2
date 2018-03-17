package com.eurodyn.qlack2.fuse.aaa.api;

/**
 * Allows you to create and handle verification tokens. Verifications tokens may
 * be used when you need to validate that the user performing an multi-part
 * action is the same user that initiated the first part of such action. A
 * classic example is when a user is registering with an application and you
 * want to validate its email address: You send an email to the user's
 * registered email address providing a URL-callback the user needs to click;
 * this URL contains the verification token previously issued during
 * registration.
 *
 * @author European Dynamics SA
 */
public interface VerificationService {

  /**
   * Creates a new verification token.
   *
   * @param userId The user Id for which this token is created.
   * @param expiresOn The date as EPOCH at which this token expires and can't be
   * used anymore.
   * @param data Any kind of data that should be associated with this
   * verification token.
   * @return The ID of the generated token.
   */
  String createVerificationToken(String userId, long expiresOn, String data);

  /**
   * Creates a new verification token.
   *
   * @param userId The user Id for which this token is created.
   * @param expiresOn The date as EPOCH at which this token expires and can't be
   * used anymore.
   * @return The ID of the generated token.
   */
  String createVerificationToken(String userId, long expiresOn);

  /**
   * Checks whether a token is valid. Validity check consists of the following items:
   * <ul>
   * <li>A token with the given ID exists</li>
   * <li>The token has not expired</li>
   * </ul>
   *
   * @param tokenID The token to verify.
   * @return Returns the user Id associated with this token or null if a token could not be found.
   */
  String verifyToken(String tokenID);

  /**
   * Delete an existing token.
   *
   * @param tokenID The token to delete.
   */
  void deleteToken(String tokenID);

  /**
   * Retrieves the payload of a previously created token. Be warned that this
   * method does not check the token for validity, so use it carefully -
   * probably in tandem with verifyToken().
   *
   * @param tokenID The ID of the token to fetch.
   * @return The token's payload data or null. Note that in case a token has
   * been created without any payload data, you will not be able using
   * this method to differentiate whether the token was found but with
   * empty data or not found at all (as in both cases you will receive
   * a null value); this is another reason why you should always
   * verify a token before calling this method.
   */
  String getTokenPayload(String tokenID);

  /**
   * Retrieves the user Id associated with a previously created token. Be warned that this
   * method does not check the token for validity, so use it carefully -
   * probably in tandem with verifyToken().
   * @param tokenID The ID of the token to fetch.
   * @return The token's user Id or null if the token was not found.
   */
  String getTokenUser(String tokenID);
}
