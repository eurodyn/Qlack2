package com.eurodyn.qlack2.util.sso;

import javax.ws.rs.Path;
import org.apache.cxf.rs.security.saml.sso.RequestAssertionConsumerService;

/**
 * This is just a wrapper to change the {@link Path}.
 */
@Path("/racs")
public class RACS extends RequestAssertionConsumerService {

}
