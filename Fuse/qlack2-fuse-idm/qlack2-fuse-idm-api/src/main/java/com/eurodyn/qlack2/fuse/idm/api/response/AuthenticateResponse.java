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
package com.eurodyn.qlack2.fuse.idm.api.response;

import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class AuthenticateResponse {
	private SignedTicket signedTicket;
	// Indicates whether this user was found to be active or not. What is
	// regarded as an 'active stauts' is specified on the .cfg file of this
	// bundle.
	private boolean active;
	// The status of the user is set only when the user is found to be inactive,
	// so that end-applications can differentiate the reason why a user could
	// not be authenticated.
	private Integer status;

	public SignedTicket getSignedTicket() {
		return signedTicket;
	}

	public void setSignedTicket(SignedTicket signedTicket) {
		this.signedTicket = signedTicket;
	}

	public AuthenticateResponse(SignedTicket signedTicket) {
		this.signedTicket = signedTicket;
	}

	public AuthenticateResponse() {

	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
