package com.eurodyn.qlack2.fuse.aaa.tests;

import com.eurodyn.qlack2.fuse.aaa.api.UserService;
import com.eurodyn.qlack2.fuse.aaa.api.VerificationService;
import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.aaa.conf.ITTestConf;
import com.eurodyn.qlack2.fuse.aaa.util.TestConst;
import com.eurodyn.qlack2.fuse.aaa.util.TestUtilities;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class VerificationServiceImplTest extends ITTestConf {

  @Inject
  @Filter(timeout = 1200000)
  VerificationService verificationService;

  @Inject
  @Filter(timeout = 1200000)
  UserService userService;

  private String createTestUser() {
    UserDTO userDTO = TestUtilities.createUserDTO();
    return userService.createUser(userDTO);
  }

  @Test
  public void createVerificationToken() {
    String userId = createTestUser();
    Assert.assertNotNull(verificationService
      .createVerificationToken(userId, TestConst.VERIFICATION_EXPIRES_ON, TestConst.VERIFICATION_DATA));
  }

  @Test
  public void createVerificationTokenArgs() {
    String userId = createTestUser();
    Assert.assertNotNull(
      verificationService.createVerificationToken(userId, TestConst.VERIFICATION_EXPIRES_ON));
  }

  @Test
  public void verifyToken() {
    String userId = createTestUser();
    String tokenID = verificationService.createVerificationToken(userId, TestConst.VERIFICATION_EXPIRES_ON);
    Assert.assertNotNull(verificationService.verifyToken(tokenID));
  }

  @Test
  public void deleteToken() {
    String userId = createTestUser();
    String tokenID = verificationService.createVerificationToken(userId, TestConst.VERIFICATION_EXPIRES_ON);
    Assert.assertNotNull(tokenID);

    verificationService.deleteToken(tokenID);
    Assert.assertNull(verificationService.verifyToken(tokenID));
  }

  @Test
  public void getTokenPayload() {
    String userId = createTestUser();
    String tokenID = verificationService
      .createVerificationToken(userId, TestConst.VERIFICATION_EXPIRES_ON, TestConst.VERIFICATION_DATA);
    Assert.assertNotNull(tokenID);

    Assert.assertNotNull(verificationService.getTokenPayload(tokenID));
  }

}
