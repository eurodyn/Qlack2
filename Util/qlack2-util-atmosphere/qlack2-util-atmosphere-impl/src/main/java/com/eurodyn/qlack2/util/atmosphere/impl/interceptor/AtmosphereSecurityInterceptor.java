package com.eurodyn.qlack2.util.atmosphere.impl.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereInterceptorAdapter;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;

import com.eurodyn.qlack2.util.atmosphere.api.AtmosphereSecurityService;
import com.eurodyn.qlack2.util.atmosphere.impl.util.AtmosphereUtil;

public class AtmosphereSecurityInterceptor extends
		AtmosphereInterceptorAdapter {
	private static final Logger LOGGER = Logger
			.getLogger(AtmosphereSecurityInterceptor.class.getName());
	private AtmosphereSecurityService securityService;

	public void setSecurityService(AtmosphereSecurityService securityService) {
		this.securityService = securityService;
	}

	@Override
	public PRIORITY priority() {
		return PRIORITY.BEFORE_DEFAULT;
	}

	@Override
	public Action inspect(AtmosphereResource atmosphereResource) {
		LOGGER.log(Level.FINEST, "Inspecting Atmosphere request.");
		if (securityService.isValidRequest(AtmosphereUtil.extractHeaders(atmosphereResource))) {
			LOGGER.log(Level.FINEST,
					"Request contains a valid security header.");
			return Action.CONTINUE;
		} else {
			LOGGER.log(Level.WARNING,
					"Request contains an invalid security header.");
			AtmosphereResponse response = atmosphereResource.getResponse();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return Action.CANCELLED;
		}
	}

}
