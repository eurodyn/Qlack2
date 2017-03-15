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
package com.eurodyn.qlack2.fuse.ticketserver.api;

import java.util.Collection;
import java.util.Set;

import com.eurodyn.qlack2.fuse.ticketserver.api.criteria.TicketSearchCriteria;
import com.eurodyn.qlack2.fuse.ticketserver.api.exception.QTicketRevokedException;

public interface TicketServerService {
	/**
	 * Creates a new ticket.
	 * @return The ID of the newly created ticket
	 */
	String createTicket(TicketDTO ticketDTO);

	/**
	 * Deletes a ticket given its ID.
	 * @param ticketID The ID of the ticket to delete
	 */
	void deleteTicket(String ticketID);

	/**
	 * Deletes one or more tickets given their IDs.
	 * @param ticketIDs The IDs of the tickets to delete.
	 */
	void deleteTickets(Collection<String> ticketIDs);

	/**
	 * Checks if a ticket is valid given its ID. A ticket is valid if
	 * its validUntil date has not passed and it has not been revoked.
	 * @param ticketID The ID of the ticket to check.
	 * @return true if the ticket is valid, false otherwise.
	 */
	boolean isValid(String ticketID);

	/**
	 * Returns the timestamp until which a ticket will remain valid.
	 * @param ticketID The ID of the ticket to check
	 * @return The valid until date of the ticket as a timestamp
	 */
	Long getValidUntil(String ticketID);

	/**
	 * Revokes a ticket
	 * @param ticketID The ID of the ticket to revoke
	 */
	void revoke(String ticketID);

	/**
	 * Revokes one or more tickets
	 * @param ticketIDs The IDs of the tickets to revoke
	 */
	void revoke(Collection<String> ticketIDs);

	/**
	 * Extends the validity of a ticket.
	 * @param ticketID The ID of the ticket to extend.
	 * @param validUntil The new valid until date to set for the ticket.
	 * @throws QTicketRevokedException If the specified ticket has been revoked
	 */
	void extendValidity(String ticketID, Long validUntil);

	/**
	 * Extends the auto extend validity date of a ticket.
	 * @param ticketID The ID of the ticket to extend.
	 * @param validUntil The new valid until date to set for the ticket.
	 * @throws QTicketRevokedException If the specified ticket has been revoked
	 */
	void extendAutoExtendValidity(String ticketID, Long validUntil);

	/**
	 * Retrieves a ticket by its ID
	 * @param ticketID The ID of the ticket to retrieve
	 * @return The ticket details or null if a ticket with the given ID
	 * does not exist.
	 */
	TicketDTO getTicket(String ticketID);

	/**
	 * Retrieves a set of tickets based on the defined search criteria
	 * @param criteria The search criteria to use. Search criteria can be constructed using
	 * the com.eurodyn.qlack2.fuse.ticketserver.api.criteria.TicketSearchCriteria.TicketSearchCriteriaBuilder
	 * @return The tickets matching the defined criteria
	 */
	Set<TicketDTO> findTickets(TicketSearchCriteria criteria);

	/**
	 * Deletes all expired tickets (ticket the valid until date fo which has passed).
	 */
	void cleanupExpired();

	/**
	 * Deletes all revoked tickets
	 */
	void cleanupRevoked();

}
