package com.eurodyn.qlack2.fuse.security.proxy.impl.util;

import java.util.logging.Logger;

import org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor;
import org.apache.cxf.message.Message;

import com.eurodyn.qlack2.common.util.util.TokenHolder;

/**
 * Since most web/app servers use a pool of reusable threads, you should always
 * cleanup the ticket held in a TokenHolder at the end of processing. This 
 * interceptors automated this process at the end of each CXF/JAXRS request.
 *
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
