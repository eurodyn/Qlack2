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

import com.eurodyn.qlack2.fuse.auditing.api.enums.AuditLogColumns;
import com.eurodyn.qlack2.fuse.auditing.api.enums.SortOperator;

public class SortDTO implements Serializable {

	private AuditLogColumns column;

	private SortOperator operator;

	public AuditLogColumns getColumn() {
		return column;
	}

	public void setColumn(AuditLogColumns column) {
		this.column = column;
	}

	public SortOperator getOperator() {
		return operator;
	}

	public void setOperator(SortOperator operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		return "SortDTO{" + "column=" + column + ", operator=" + operator + '}';
	}

	public AuditLogColumns getAuditLogColumnEnumByName(String columnName) {
		AuditLogColumns[] values = AuditLogColumns.values();
		for (AuditLogColumns enumeration : values) {
			if (enumeration.name().equals(columnName)) {
				return enumeration;
			}
		}

		return null;
	}

}
