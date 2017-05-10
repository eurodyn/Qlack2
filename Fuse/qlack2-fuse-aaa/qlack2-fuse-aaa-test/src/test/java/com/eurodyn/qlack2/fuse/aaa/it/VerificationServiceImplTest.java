package com.eurodyn.qlack2.fuse.aaa.it;

import com.eurodyn.qlack2.fuse.aaa.api.VerificationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;

/**
 * @author European Dynamics SA
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class VerificationServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    VerificationService verificationService;

    @Test
    public void createVerificationToken(){
        Assert.assertNotNull(verificationService.createVerificationToken(TestConst.VERIFICATION_EXPIRES_ON,TestConst.VERIFICATION_DATA));
    }

    @Test
    public void createVerificationTokenArgs(){
        Assert.assertNotNull(verificationService.createVerificationToken(TestConst.VERIFICATION_EXPIRES_ON));
    }

    @Test
    public void verifyToken(){
        String tokenID= verificationService.createVerificationToken(TestConst.VERIFICATION_EXPIRES_ON);
        Assert.assertNotNull(verificationService.verifyToken(tokenID));
    }

    @Test
    public void deleteToken(){
        String tokenID = verificationService.createVerificationToken(TestConst.VERIFICATION_EXPIRES_ON);
        Assert.assertNotNull(tokenID);

        verificationService.deleteToken(tokenID);
        Assert.assertFalse(verificationService.verifyToken(tokenID));
    }

    @Test
    public void getTokenPayload(){
        String tokenID = verificationService.createVerificationToken(TestConst.VERIFICATION_EXPIRES_ON,TestConst.VERIFICATION_DATA);
        Assert.assertNotNull(tokenID);

        Assert.assertNotNull(verificationService.getTokenPayload(tokenID));
    }

}
