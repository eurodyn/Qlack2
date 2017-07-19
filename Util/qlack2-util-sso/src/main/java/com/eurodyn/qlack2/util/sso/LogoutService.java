package com.eurodyn.qlack2.util.sso;
import com.eurodyn.qlack2.util.sso.dto.LogoutResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;
import org.apache.cxf.security.SecurityContext;


/**
 * Created by kperp on 18.07.2017.
 */
@Path("logout")
public class LogoutService {
  private SPStateManager stateProvider;
  private String mainApplicationAddress;

  public LogoutService() {

  }

  @GET
  public Response logout(@CookieParam("org.apache.cxf.websso.context") Cookie context) {
    this.doLogout(context);
    return Response.status(302).header("location", mainApplicationAddress).build();
  }

  @POST
  public Response postLogout(@CookieParam("org.apache.cxf.websso.context") Cookie context) {
    return this.logout(context);
  }

  private void doLogout(Cookie context) {
    if(context != null) {
      this.stateProvider.removeResponseState(context.getValue());
    } else {
      throw ExceptionUtils.toBadRequestException((Throwable)null, (Response)null);
    }
  }

  public void setStateProvider(SPStateManager stateProvider) {
    this.stateProvider = stateProvider;
  }

  public void setMainApplicationAddress(String mainApplicationAddress) {
    this.mainApplicationAddress = mainApplicationAddress;
  }
}
