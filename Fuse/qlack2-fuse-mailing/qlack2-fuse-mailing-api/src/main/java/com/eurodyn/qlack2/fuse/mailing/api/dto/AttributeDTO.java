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
package com.eurodyn.qlack2.fuse.mailing.api.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author European Dynamics SA
 */
public class AttributeDTO implements Serializable {
	private static final long serialVersionUID = -4465643192637918032L;

	private Map<String, Object> attribute = new HashMap<String, Object>();

	public Object clearAttribute(String key) {
		return this.getAttribute().remove(key);
	}

	public Object getAttribute(String key) {
		return this.getAttribute().get(key);
	}

	public void setAttribute(String key, Object value) {
		this.getAttribute().put(key, value);
	}

	public Map<String, Object> getAttribute() {
		return attribute;
	}

	public void setAttribute(Map<String, Object> attribute) {
		this.attribute = attribute;
	}

}