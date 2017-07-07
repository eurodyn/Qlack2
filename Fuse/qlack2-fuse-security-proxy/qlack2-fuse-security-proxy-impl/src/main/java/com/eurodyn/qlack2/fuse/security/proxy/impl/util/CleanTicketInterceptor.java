package com.eurodyn.qlack2.fuse.security.proxy.impl.util;

import com.eurodyn.qlack2.common.util.util.TokenHolder;
import org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor;
import org.apache.cxf.message.Message;

import java.util.logging.Logger;

/**
 * Since most web/app servers use a pool of reusable threads, you should always
 * cleanup the ticket held in a TokenHolder at the end of processing. This
 * interceptor automates this process at the end of each CXF/JAXRS request.
 */
public class CleanTicketInterceptor extends JAXRSOutInterceptor {

  private static final Logger LOGGER = Logger.getLogger(CleanTicketInterceptor.class.getName());

  /* (non-Javadoc)
   * @see org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor#handleMessage(org.apache.cxf.message.Message)
   */
  @Override
  public void handleMessage(Message message) {
    TokenHolder.removeToken();
  }
}
