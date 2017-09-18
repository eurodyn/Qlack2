package com.eurodyn.qlack2.fuse.crypto.impl;

import com.eurodyn.qlack2.fuse.crypto.api.CryptoService;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.ops4j.pax.exam.util.Filter;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
public class CryptoServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    CryptoService cryptoService;

    @Test
    public void hmacSha256() {
        try {
            Assert.assertNotNull(cryptoService.hmacSha256(TestConst.secret, TestConst.message, TestConst.charset));
        } catch (Exception e) {

        }
    }

    @Test
    public void md5() {
        try {
            Assert.assertNotNull(cryptoService.md5(TestConst.message));
        } catch (Exception e) {

       }
    }

}
