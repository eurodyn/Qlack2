package com.eurodyn.qlack2.util.sso.interceptors;

import com.eurodyn.qlack2.util.sso.dto.WebSSOHolder;
import org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor;
import org.apache.cxf.message.Message;

/**
 * Since most web/app servers use a pool of reusable threads, you should always
 * cleanup the values held in a ThreadLocal at the end of processing. This
 * interceptor automates this process at the end of each CXF/JAXRS request.
 *
 */
public class WebSSOCookieInterceptorCleanup extends JAXRSOutInterceptor {
  /* (non-Javadoc)
     * @see org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor#handleMessage(org.apache.cxf.message.Message)
     */
  @Override
  public void handleMessage(Message message) {
    WebSSOHolder.removeAttributes();
  }
}
