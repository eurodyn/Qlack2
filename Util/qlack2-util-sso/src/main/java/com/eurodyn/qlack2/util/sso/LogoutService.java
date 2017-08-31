package com.eurodyn.qlack2.util.sso;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;
import org.apache.cxf.rs.security.saml.sso.state.SPStateManager;


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
  public Response logout(@CookieParam(Constants.WEB_SSO_COOKIE_NAME) Cookie context) {
    this.doLogout(context);
    return Response.status(302).header("location", mainApplicationAddress).build();
  }

  @POST
  public Response postLogout(@CookieParam(Constants.WEB_SSO_COOKIE_NAME) Cookie context) {
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
