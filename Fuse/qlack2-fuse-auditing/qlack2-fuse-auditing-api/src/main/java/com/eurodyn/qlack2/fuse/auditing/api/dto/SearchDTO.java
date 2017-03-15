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
package com.eurodyn.qlack2.fuse.auditing.api.dto;

import java.io.Serializable;
import java.util.List;

import com.eurodyn.qlack2.fuse.auditing.api.enums.AuditLogColumns;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SearchOperator;

public class SearchDTO implements Serializable {
	private static final long serialVersionUID = -4536635995820002527L;

	private List<String> value;

	private AuditLogColumns column;

	private SearchOperator operator;

	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

	public AuditLogColumns getColumn() {
		return column;
	}

	public void setColumn(AuditLogColumns column) {
		this.column = column;
	}

	public SearchOperator getOperator() {
		return operator;
	}

	public void setOperator(SearchOperator operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return "SearchDTO{" + "value=" + value + ", column=" + column
				+ ", operator=" + operator + '}';
	}

}
