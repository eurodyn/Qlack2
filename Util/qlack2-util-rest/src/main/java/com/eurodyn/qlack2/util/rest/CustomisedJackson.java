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
package com.eurodyn.qlack2.util.rest;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomisedJackson extends ObjectMapper {
	private static final long serialVersionUID = 2642094423231515852L;

	public CustomisedJackson() {
		super();
	}
	
	public void setAcceptSingleValueAsArray(boolean state) {
		super.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, state);
	}
	
	public void setSerialiseNullValues(boolean b) {
		super.setSerializationInclusion(b ? Include.ALWAYS : Include.NON_EMPTY);
	}
}
