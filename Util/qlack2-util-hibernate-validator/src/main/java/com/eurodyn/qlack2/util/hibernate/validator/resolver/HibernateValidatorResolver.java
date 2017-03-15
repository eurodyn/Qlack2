package com.eurodyn.qlack2.util.hibernate.validator.resolver;

import java.util.Collections;
import java.util.List;

import javax.validation.ValidationProviderResolver;

import org.hibernate.validator.HibernateValidator;

public class HibernateValidatorResolver implements ValidationProviderResolver {

	@Override
	public List getValidationProviders() {
		return Collections.singletonList(new HibernateValidator());
	}

}