package com.eurodyn.qlack2.be.rules.web.util;

import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.util.Constants;

import javax.ws.rs.core.HttpHeaders;

public class Utils {

	private Utils() {
	}

	public static EmptyRequest sign(HttpHeaders headers) {
		EmptyRequest request = new EmptyRequest();

		String ticket = Constants.getTicketHeader(headers.getRequestHeaders());
		request.setSignedTicket(SignedTicket.fromVal(ticket));
		return request;
	}

	public static <T extends QSignedRequest> T sign(T request, HttpHeaders headers) {
		String ticket = Constants.getTicketHeader(headers.getRequestHeaders());
		request.setSignedTicket(SignedTicket.fromVal(ticket));
		return request;
	}

}
