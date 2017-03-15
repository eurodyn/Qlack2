package com.eurodyn.qlack2.be.forms.orbeon.util;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.util.Constants;

import javax.ws.rs.core.HttpHeaders;

public class Utils {

	private Utils() {
	}

	public static void sign(QSignedRequest request, HttpHeaders headers) {
		String ticket = Constants.getTicketHeader(headers.getRequestHeaders());
		request.setSignedTicket(SignedTicket.fromVal(ticket));
	}

}
