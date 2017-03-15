package com.eurodyn.qlack2.util.hibernate.validator.resolver;

import java.util.Locale;

import javax.validation.MessageInterpolator;

public class PlainMessageInterpolator implements MessageInterpolator {

	public PlainMessageInterpolator() {
	}

	@Override
	public String interpolate(String messageTemplate, Context context) {
		return context.getConstraintDescriptor().getAttributes().get("message").toString(); 
	}

	@Override
	public String interpolate(String messageTemplate, Context context,
			Locale locale) {
		return interpolate(messageTemplate, context);
	}
}
