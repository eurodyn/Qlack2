package com.eurodyn.qlack2.fuse.idm.it;

import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateRequest;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateSSORequest;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class IDMServiceImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    IDMService idmService;

    @Test
    public void authenticateReq(){
        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setUsername(UUID.randomUUID().toString());
        authenticateRequest.setPassword(TestConst.generateRandomString());

        Assert.assertNotNull(idmService.authenticate(authenticateRequest));
    }

    @Test
    public void authenticateSSO(){
        AuthenticateSSORequest authenticateSSORequest = new AuthenticateSSORequest();
        authenticateSSORequest.setUsername(UUID.randomUUID().toString());

        Assert.assertNotNull(idmService.authenticate(authenticateSSORequest));
    }

    @Test
    public void validateTicket(){
        Calendar Cale = new GregorianCalendar(2029,9,21);
        Date date = Cale.getTime();

        SignedTicket signedTicket = new SignedTicket();
        signedTicket.setTicketID(UUID.randomUUID().toString());
        signedTicket.setUsername(TestConst.generateRandomString());
        signedTicket.setSignature(TestConst.generateRandomString());
        signedTicket.setAutoExtendDuration(date.getTime());
        signedTicket.setAutoExtendValidUntil(date.getTime());
        signedTicket.setUserID(UUID.randomUUID().toString());
        signedTicket.setValidUntil(date.getTime());

        ValidateTicketRequest validateTicketRequest = new ValidateTicketRequest();
        validateTicketRequest.setSignedTicket(signedTicket);

        Assert.assertNotNull(idmService.validateTicket(validateTicketRequest));
    }

}

