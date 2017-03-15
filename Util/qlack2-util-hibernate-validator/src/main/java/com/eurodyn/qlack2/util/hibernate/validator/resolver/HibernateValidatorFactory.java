package com.eurodyn.qlack2.util.hibernate.validator.resolver;

import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

public class HibernateValidatorFactory implements ValidatorFactory {
	Configuration<?> configuration = Validation.byDefaultProvider()
			.providerResolver(new HibernateValidatorResolver())
			.configure();
	ValidatorFactory factory = configuration
			.messageInterpolator(new PlainMessageInterpolator())
			.buildValidatorFactory();
	
	@Override
	public Validator getValidator() {
		return factory.getValidator();
	}
	@Override
	public ValidatorContext usingContext() {
		return null;
	}
	@Override
	public MessageInterpolator getMessageInterpolator() {
		return new PlainMessageInterpolator();
	}
	@Override
	public TraversableResolver getTraversableResolver() {
		return factory.getTraversableResolver();
	}
	@Override
	public ConstraintValidatorFactory getConstraintValidatorFactory() {
		return factory.getConstraintValidatorFactory();
	}
	@Override
	public ParameterNameProvider getParameterNameProvider() {
		return null;
	}
	@Override
	public <T> T unwrap(Class<T> type) {
		return factory.unwrap(type);
	}
	@Override
	public void close() {
		factory.close();
	}
}
