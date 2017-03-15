package com.eurodyn.qlack2.be.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.frontend.ClientProxyFactoryBean;

import com.eurodyn.qlack2.be.client.connectivity.ClientCredentials;
import com.eurodyn.qlack2.be.client.connectivity.ServerAddress;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.idm.api.request.AuthenticateRequest;
import com.eurodyn.qlack2.fuse.idm.api.response.AuthenticateResponse;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class ServiceInvocationHandler implements InvocationHandler {
	public static final int maxConnectionTries = 2;
	public static final String idmService = "/ws/fuse/IDMService";
	public static final Logger logger = Logger.getLogger(ServiceInvocationHandler.class.getName());

	private final Object proxied;
	private SignedTicket ticket;
	private ClientCredentials credentials;
	private IDMService idmClient;

	public ServiceInvocationHandler(Object proxied,
			final ServerAddress qbeAddress,
			final ClientCredentials credentials, final String cxfPrefix) {
		this.proxied = proxied;
		this.credentials = credentials;
		
		ClientProxyFactoryBean idmFactory = new ClientProxyFactoryBean();
		idmFactory.setServiceClass(IDMService.class);
		idmFactory.setDataBinding(new AegisDatabinding());
		idmFactory.setAddress(qbeAddress.getAddress() + cxfPrefix + idmService);
		idmClient = idmFactory.create(IDMService.class);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retVal = null;
		int tries = 1;
		if (args != null && args.length == 1) {
			Object request = args[0];
			if (request instanceof QSignedRequest) {
				QSignedRequest sreq = (QSignedRequest) request;
				boolean retry = true;
				while (retry && (tries <= maxConnectionTries)) {
					try {
						if (sreq.getSignedTicket() == null) {
							logger.log(Level.FINEST, "Injecting signed ticket into request");
							if (ticket == null) {
								authenticate();
							}
							sreq.setSignedTicket(ticket);
						}

						retVal = method.invoke(proxied, args);
						retry = false;
					} catch (Throwable t) {
						if ((t.getCause() instanceof QInvalidTicketException)
								&& (tries < maxConnectionTries)) {
							logger.log(Level.FINEST, "QInvalidTicketException caught on request try no. {0}", String.valueOf(tries));
							sreq.setSignedTicket(null);
							ticket = null;
							tries++;
						} else {
							throw t.getCause();
						}
					}
				}
			}
		}

		return retVal;
	}

	private void authenticate() {	
		logger.log(Level.FINEST, "Invoking IDM service to get fresh SignedTicket");
		AuthenticateRequest authReq = new AuthenticateRequest(
				credentials.getUsername(), credentials.getPassword());	
		AuthenticateResponse authRes = idmClient.authenticate(authReq);
		ticket = authRes.getSignedTicket();
	}
}
