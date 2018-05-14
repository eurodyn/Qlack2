package com.eurodyn.qlack2.util.sso;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.rs.security.saml.sso.SSOConstants;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;

/**
 * A logout service that remove the login information from the state provider and deletes the
 * associated cookies. It can be accessed under two paths 'logout' and 'slo'.
 */
@Path("{a:logout|slo}")
public class LogoutService {

  private SPStateManager stateProvider;
  private String mainApplicationAddress;

  @GET
  public Response logout(@CookieParam(SSOConstants.SECURITY_CONTEXT_TOKEN) Cookie ssoCookie,
      @CookieParam(SSOConstants.RELAY_STATE) Cookie relayStateCookie) {
    this.doLogout(ssoCookie);
    // delete the web sso cookie
    NewCookie newSSOCookie = new NewCookie(ssoCookie.getName(), StringUtils.EMPTY, "/", null,
        StringUtils.EMPTY, 0, false);
    // delete the relay state cookie
    NewCookie newRelayStateCookie = new NewCookie(relayStateCookie.getName(), StringUtils.EMPTY,
        "/", null, StringUtils.EMPTY, 0, false);
    return Response.status(302).cookie(newSSOCookie, newRelayStateCookie).header("location", mainApplicationAddress)
        .build();
  }

  @POST
  public Response postLogout(@CookieParam(SSOConstants.SECURITY_CONTEXT_TOKEN) Cookie ssoCookie,
      @CookieParam(SSOConstants.RELAY_STATE) Cookie relayStateCookie) {
    return this.logout(ssoCookie, relayStateCookie);
  }

  private void doLogout(Cookie context) {
    if (context != null) {
      this.stateProvider.removeResponseState(context.getValue());
    } else {
      throw ExceptionUtils.toBadRequestException((Throwable) null, (Response) null);
    }
  }

  public void setStateProvider(SPStateManager stateProvider) {
    this.stateProvider = stateProvider;
  }

  public void setMainApplicationAddress(String mainApplicationAddress) {
    this.mainApplicationAddress = mainApplicationAddress;
  }

}