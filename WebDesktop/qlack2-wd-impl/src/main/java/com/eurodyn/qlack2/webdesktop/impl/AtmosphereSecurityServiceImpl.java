package com.eurodyn.qlack2.webdesktop.impl;

import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.util.atmosphere.api.AtmosphereSecurityService;
import com.eurodyn.qlack2.webdesktop.api.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.ops4j.pax.cdi.api.OsgiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
//@OsgiServiceProvider(classes = {AtmosphereSecurityService.class})
public class AtmosphereSecurityServiceImpl implements AtmosphereSecurityService {
	private static final Logger LOGGER = Logger
			.getLogger(AtmosphereSecurityServiceImpl.class.getName());
	@OsgiService @Inject
	private IDMService idmService;

	private boolean validateSignedTicket(SignedTicket signedTicket) {
		return idmService.validateTicket(
				new ValidateTicketRequest(signedTicket)).isValid();
	}

	// When the client sends GET requests the ticket is URL-encoded,
	// so we should detect this and decode it accordingly.
	private String decodeSecurityHeader(String securityHeader) {
		String retVal = securityHeader;
		if (securityHeader.startsWith("%")) {
			try {
				retVal = URLDecoder.decode(securityHeader, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.log(Level.SEVERE, "Could not URL decode header.", e);
			}
		}

		return retVal;
	}

	@Override
	public boolean isValidRequest(Map<String, String> requestHeaders) {
		String securityHeader = requestHeaders
				.get(Constants.getTicketHeaderName(requestHeaders));

		if (StringUtils.isBlank(securityHeader)) {
			LOGGER.log(Level.WARNING, "Did not find a security header {0}.",
					Constants.getTicketHeaderName(requestHeaders));
			return false;
		} else {
			securityHeader = decodeSecurityHeader(securityHeader);
			LOGGER.log(Level.FINEST, "Security header found: {0}.",
					securityHeader);

			return validateSignedTicket(SignedTicket.fromVal(securityHeader));
		}
	}

	@Override
	public String getUserID(Map<String, String> requestHeaders) {
		String securityHeader = requestHeaders
				.get(Constants.getTicketHeaderName(requestHeaders));
		if (StringUtils.isBlank(securityHeader)) {
			LOGGER.log(Level.WARNING, "Did not find a security header {0}.",
					Constants.getTicketHeaderName(requestHeaders));
			return null;
		} else {
			securityHeader = decodeSecurityHeader(securityHeader);
			SignedTicket ticket = SignedTicket.fromVal(securityHeader);
			if (validateSignedTicket(ticket)) {
				return ticket.getUserID();
			} else {
				return null;
			}
		}
	}

	@Override
	public boolean canSubscribe(String topic, String userID) {
		boolean retVal = true;

		// Allow users to subscribe only to their own private channel.
		if (retVal) {
			if (topic.startsWith(Constants.ATMOSPHERE_PRIVATE_TOPIC_PREFIX)
					&& !topic.equals(Constants.ATMOSPHERE_PRIVATE_TOPIC_PREFIX
							+ "/" + userID)) {
				retVal = false;
			}

		}

		return retVal;
	}

}
