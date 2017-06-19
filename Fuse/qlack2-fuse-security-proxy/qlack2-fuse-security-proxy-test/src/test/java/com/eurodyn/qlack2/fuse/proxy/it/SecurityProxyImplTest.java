package com.eurodyn.qlack2.fuse.proxy.it;

import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateRequest;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.security.proxy.api.SecurityProxy;
import com.eurodyn.qlack2.fuse.security.proxy.api.dto.CheckPermissionRDTO;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketDTO;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketServerService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.ops4j.pax.exam.util.Filter;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.*;
import static org.mockito.Mockito.*;

/**
 * @author European Dynamics SA.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SecurityProxyImplTest extends ITTestConf {

    @Inject
    @Filter(timeout = 1200000)
    SecurityProxy securityProxy;

    @Inject
    @Filter(timeout = 1200000)
    TicketServerService ticketServerService;

    SecurityProxy securityProxySpy = spy(SecurityProxy.class);
    @Context  HttpHeaders httpHeaders;

    @Test
    public void authenticate(){
        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setUsername("user");
        authenticateRequest.setPassword("pass");

        Assert.assertNotNull(securityProxy.authenticate(authenticateRequest));
    }

    @Test
    public void genericPermissions(){
        Set<String> setHeaders = new HashSet<>();
        setHeaders.add("X-Qlack-Fuse-IDM-SecurityProxy");

        doReturn(setHeaders).when(securityProxySpy).genericPermissions(httpHeaders);
        Assert.assertEquals(setHeaders, securityProxySpy.genericPermissions(httpHeaders));
    }

    @Test
    public void checkPermission(){
        CheckPermissionRDTO checkPermissionRDTO = new CheckPermissionRDTO();
        checkPermissionRDTO.setObjectID(UUID.randomUUID().toString());
        checkPermissionRDTO.setPermission("testPersmision01");

        doReturn(true).when(securityProxySpy).checkPermission(checkPermissionRDTO,httpHeaders);
        Assert.assertEquals(true, securityProxySpy.checkPermission(checkPermissionRDTO,httpHeaders));
    }

    @Test
    public void isTicketValid(){
        TicketDTO ticketDTO = TestUtilities.createTicketDTO();
        String ticketID = ticketServerService.createTicket(ticketDTO);
        Assert.assertNotNull(ticketID);

        SignedTicket signedTicket = new SignedTicket();
        signedTicket.setUsername(UUID.randomUUID().toString());
        signedTicket.setTicketID(ticketID);
        signedTicket.setUserID(UUID.randomUUID().toString());
        signedTicket.setValidUntil(new Date().getTime());
        signedTicket.setAutoExtendDuration(new Date().getTime());
        signedTicket.setAutoExtendValidUntil(new Date().getTime());

        ValidateTicketRequest validateTicketRequest = new ValidateTicketRequest();
        validateTicketRequest.setSignedTicket(signedTicket);

        Assert.assertNotNull(securityProxy.isTicketValid(validateTicketRequest));
    }

    @Test
    public void getUserDetails(){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.randomUUID().toString());
        userDTO.setUsername("userTest");
        userDTO.setUsername("passTest");

        doReturn(userDTO).when(securityProxySpy).getUserDetails(httpHeaders);
        Assert.assertEquals(userDTO, securityProxySpy.getUserDetails(httpHeaders));
    }

}


