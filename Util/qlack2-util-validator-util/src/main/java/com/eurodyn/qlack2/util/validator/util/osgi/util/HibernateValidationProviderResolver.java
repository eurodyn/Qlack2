/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.util.validator.util.osgi.util;

import java.util.Collections;
import java.util.List;

import javax.validation.ValidationProviderResolver;

import org.hibernate.validator.HibernateValidator;

@Deprecated
public class HibernateValidationProviderResolver implements
		ValidationProviderResolver {

	@Override
	public List getValidationProviders() {
		return Collections.singletonList(new HibernateValidator());
	}

}