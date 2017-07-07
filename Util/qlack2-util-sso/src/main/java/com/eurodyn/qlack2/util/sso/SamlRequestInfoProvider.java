package com.eurodyn.qlack2.util.sso;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.rs.security.saml.sso.SamlRequestInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A message body writer for {@link SamlRequestInfo}. This provider allows classes returning
 * SamlRequestInfo objects to render their information into an HTML form which is automatically
 * submitted once displayed. Using this provider you can transparently (e.g. with no additional
 * HTML/JSP/etc. files) support an HTTP-POST redirect to your IdP for your SAML authentication
 * request.
 */
@Provider
public class SamlRequestInfoProvider implements MessageBodyReader<SamlRequestInfo>,
    MessageBodyWriter<SamlRequestInfo> {

  @Override
  public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations,
      MediaType mediaType) {
    return false;
  }

  @Override
  public SamlRequestInfo readFrom(Class<SamlRequestInfo> aClass, Type type,
      Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> multivaluedMap,
      InputStream inputStream) throws IOException, WebApplicationException {
    return null;
  }

  @Override
  public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations,
      MediaType mediaType) {
    return true;
  }

  @Override
  public long getSize(SamlRequestInfo samlRequestInfo, Class<?> aClass, Type type,
      Annotation[] annotations, MediaType mediaType) {
    return 0;
  }

  @Override
  public void writeTo(SamlRequestInfo samlRequestInfo, Class<?> aClass, Type type,
      Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap,
      OutputStream outputStream) throws IOException, WebApplicationException {
    //language=HTML
    String reply =
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
        + "<body onLoad=\"document.forms[0].submit();\">\n"
        + "   <form action=\"" + samlRequestInfo.getIdpServiceAddress() + "\" method=\"POST\">\n"
        + "        <input type=\"hidden\" name=\"SAMLRequest\"\n"
        + "                value=\"" + samlRequestInfo.getSamlRequest() + "\"/>\n"
        + "        <input type=\"hidden\" name=\"RelayState\"\n"
        + "                value=\"" + samlRequestInfo.getRelayState() + "\"/>\n"
        // Uncomment this and remove 'onLoad' event for debugging.
        // + "         <input type=\"submit\" value=\"Continue\"/>\n"
        + "   </form>\n"
        + "</body>\n"
        + "</html>";
    outputStream.write(reply.getBytes());
  }
}
