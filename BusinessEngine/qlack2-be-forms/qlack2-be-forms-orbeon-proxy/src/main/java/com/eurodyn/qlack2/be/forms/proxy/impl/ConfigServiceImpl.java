package com.eurodyn.qlack2.be.forms.proxy.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eurodyn.qlack2.be.forms.proxy.api.ConfigService;

public class ConfigServiceImpl implements ConfigService {

	private List<String> allowedOriginUris;

	@Override
	public List<String> getAllowedOriginUris() {
		return allowedOriginUris;
	}

	public void setAllowedOrigins(String allowedOrigins) {
		allowedOriginUris = new ArrayList<>();

		if (allowedOrigins != null) {
			allowedOriginUris.addAll(Arrays.asList(allowedOrigins.split(",")));
		}
	}

}
