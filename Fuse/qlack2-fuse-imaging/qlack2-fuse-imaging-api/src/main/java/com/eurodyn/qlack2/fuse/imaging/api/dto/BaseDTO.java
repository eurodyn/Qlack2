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
package com.eurodyn.qlack2.fuse.imaging.api.dto;

import java.io.Serializable;

/**
 * $Id: BaseDTO.java 35 2013-03-20 10:18:14Z ehond $
 *
 * http://www.qlack.com Copyright 2013 - European Dynamics SA - All rights
 * reserved.
 *
 * This source code can only be used with explicit permission of its owner.
 */
public class BaseDTO extends AttributeDTO implements Serializable {

	private static final long serialVersionUID = -5465643192637918032L;
	private String id;
	// This user id is used to identify the user calling an action in order to
	// use it when generating notification messages
	// or when this information should be stored along with the relevant item in
	// the db. Please note that it *should not* be
	// used for security checks.
	private String srcUserId;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getSrcUserId() {
		return srcUserId;
	}

	public void setSrcUserId(String srcUserId) {
		this.srcUserId = srcUserId;
	}
}
