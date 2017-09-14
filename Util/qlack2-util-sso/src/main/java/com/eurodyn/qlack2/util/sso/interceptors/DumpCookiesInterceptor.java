package com.eurodyn.qlack2.util.sso.interceptors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.cxf.jaxrs.interceptor.JAXRSInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dumps all incoming Cookies to log
 */
public class DumpCookiesInterceptor extends JAXRSInInterceptor {

  private static final Logger LOGGER = Logger.getLogger(DumpCookiesInterceptor.class.getName());

  private String dumpCookie(Cookie cookie) {
    StringBuffer retVal = new StringBuffer("Cookie ");
    retVal.append("Name: " + cookie.getName());
    retVal.append(", Domain: " + cookie.getDomain());
    retVal.append(", Path: " + cookie.getPath());
    retVal.append(", Value: " + cookie.getValue());
    retVal.append(", Age: " + cookie.getMaxAge());
    retVal.append(", Secure: " + cookie.getSecure());

    return retVal.toString();
  }

  @Override
  public void handleMessage(Message message) {
    HttpServletRequest request = (HttpServletRequest) message
      .get(AbstractHTTPDestination.HTTP_REQUEST);
    LOGGER.log(Level.FINEST, "DumpCookiesInterceptor for: " + request.getRequestURI() + " / "
      + request.getRequestURL());
    if (request.getCookies() != null) {
      Arrays.stream(request.getCookies()).forEach(o -> {
        LOGGER.log(Level.FINEST, dumpCookie(o));
      });
    }
  }
}
