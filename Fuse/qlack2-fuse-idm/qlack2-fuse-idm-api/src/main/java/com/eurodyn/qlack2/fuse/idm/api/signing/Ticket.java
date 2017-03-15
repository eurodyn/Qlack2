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
package com.eurodyn.qlack2.fuse.idm.api.signing;
import java.io.Serializable;

/**
 * An abstraction of a Ticket token. See also {@link SignedTicket} which is
 * a wrapper implementation allowing Tickets to be digitally signed. To
 * dynamically specify which of the fields of the Ticket take part in the
 * signing process, annotate such fields with @Signed.
 * @author European Dynamics SA
 *
 */
public class Ticket implements Serializable {
	private static final long serialVersionUID = 2388706055382441037L;
	@Signed
	private String ticketID;
	@Signed
	private Long validUntil;
	private Long autoExtendValidUntil;
	private Long autoExtendDuration;
	@Signed
	private String userID;
	@Signed
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public Long getAutoExtendValidUntil() {
		return autoExtendValidUntil;
	}

	public void setAutoExtendValidUntil(Long autoExtendValidUntil) {
		this.autoExtendValidUntil = autoExtendValidUntil;
	}

	public Long getAutoExtendDuration() {
		return autoExtendDuration;
	}

	public void setAutoExtendDuration(Long autoExtendDuration) {
		this.autoExtendDuration = autoExtendDuration;
	}

	public Long getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Long validUntil) {
		this.validUntil = validUntil;
	}

	public Ticket() {

	}

	public String getTicketID() {
		return ticketID;
	}

	public void setTicketID(String ticketID) {
		this.ticketID = ticketID;
	}

	public String toString() {
		return ticketID + ":" + userID;
	}
}
