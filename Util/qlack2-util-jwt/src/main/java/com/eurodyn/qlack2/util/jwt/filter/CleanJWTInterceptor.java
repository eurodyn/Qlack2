package com.eurodyn.qlack2.util.jwt.filter;

import com.eurodyn.qlack2.common.util.util.TokenHolder;
import org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor;
import org.apache.cxf.message.Message;

/**
 * Since most web/app servers use a pool of reusable threads, you should always
 * cleanup thread-local variables. This
 * interceptor automates this process at the end of each CXF/JAXRS request.
 */
public class CleanJWTInterceptor extends JAXRSOutInterceptor {

  /* (non-Javadoc)
   * @see org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor#handleMessage(org.apache.cxf.message.Message)
   */
  @Override
  public void handleMessage(Message message) {
    TokenHolder.removeToken();
  }
}
