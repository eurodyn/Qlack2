package com.eurodyn.qlack2.util.atmosphere.impl.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;

public class AtmosphereUtil {
	public static Map<String, String> extractHeaders(AtmosphereResource r) {
		Map<String, String> headers = new HashMap<>();
		AtmosphereRequest request = r.getRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headers.put(headerName, request.getHeader(headerName));
		}

		return headers;
	}

}
