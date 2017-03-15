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
package com.eurodyn.qlack2.fuse.ticketserver.impl;

import com.eurodyn.qlack2.fuse.ticketserver.api.TicketDTO;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketServerService;
import com.eurodyn.qlack2.fuse.ticketserver.api.criteria.TicketSearchCriteria;
import com.eurodyn.qlack2.fuse.ticketserver.api.criteria.TicketSearchCriteria.PayloadMatch;
import com.eurodyn.qlack2.fuse.ticketserver.api.exception.QTicketRevokedException;
import com.eurodyn.qlack2.fuse.ticketserver.impl.model.Ticket;
import com.eurodyn.qlack2.fuse.ticketserver.impl.util.ConverterUtil;
import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.text.MessageFormat;
import java.util.*;

@Singleton
@OsgiServiceProvider(classes = {TicketServerService.class})
@Transactional
public class TicketServerServiceImpl implements TicketServerService {
	@PersistenceContext(unitName = "fuse-ticketserver")
	private EntityManager em;

	@OsgiService
	@Inject
	private LiquibaseBootMigrationsDoneService liquibaseBootMigrationsDoneService;

	@Override
	public String createTicket(TicketDTO ticketDTO) {
		DateTime now = DateTime.now();
		Ticket ticket = new Ticket();
		ticket.setId(UUID.randomUUID().toString());
		ticket.setCreatedAt(now.getMillis());
		if (StringUtils.isNotBlank(ticketDTO.getCreatedBy())) {
			ticket.setCreatedBy(ticketDTO.getCreatedBy());
		}
		if (StringUtils.isNotBlank(ticketDTO.getPayload())) {
			ticket.setPayload(ticketDTO.getPayload());
		}
		ticket.setRevoked(false);

		if (ticketDTO.getValidUntil() != null) {
			ticket.setValidUntil(ticketDTO.getValidUntil());
		}
		if (ticketDTO.getAutoExtendValidUntil() != null) {
			ticket.setAutoExtendUntil(ticketDTO.getAutoExtendValidUntil());
		}
		if (ticketDTO.getAutoExtendDuration() != null) {
			ticket.setAutoExtendDuration(ticketDTO.getAutoExtendDuration());
		}

		em.persist(ticket);

		return ticket.getId().toString();
	}

	@Override
	public void deleteTicket(String ticketID) {
		Ticket ticket = em.find(Ticket.class, ticketID);
		em.remove(ticket);
	}

	@Override
	public void deleteTickets(Collection<String> ticketIDs) {
		for (String ticketID : ticketIDs) {
			deleteTicket(ticketID);
		}
	}

	@Override
	public boolean isValid(String ticketID) {
		boolean retVal = false;
		Ticket ticket = em.find(Ticket.class, ticketID);
		if (!ticket.isRevoked()
				&& ((ticket.getValidUntil() == null) || (DateTime.now()
						.getMillis() < ticket.getValidUntil()))) {
			retVal = true;
		}

		// Check if the ticket should be auto-extended.
		// Only a valid ticket, with a expiring original duration, having a
		// auto-extend duration > 0 can be auto-extended.
		if (retVal && ticket.getValidUntil() != null
				&& ticket.getAutoExtendDuration() != null
				&& ticket.getAutoExtendDuration().longValue() > 0) {
			long now = DateTime.now().getMillis();
			long newValidUntil;
			if (ticket.getAutoExtendUntil() == null
					|| (now + ticket.getAutoExtendDuration().longValue() < ticket
							.getAutoExtendUntil())) {
				newValidUntil = now
						+ ticket.getAutoExtendDuration().longValue();
			} else {
				newValidUntil = ticket.getAutoExtendUntil();
			}
			ticket.setValidUntil(newValidUntil);
		}

		return retVal;
	}

	@Override
	public Long getValidUntil(String ticketID) {
		Ticket ticket = em.find(Ticket.class, ticketID);
		return ticket.getValidUntil();
	}

	@Override
	public void revoke(String ticketID) {
		Ticket ticket = em.find(Ticket.class, ticketID);
		if (ticket.isRevoked()) {
			throw new QTicketRevokedException(MessageFormat.format(
					"Cannot revoke ticket with ID {0}; the ticket is already "
							+ "revoked", new Object[] { ticketID }));
		}
		ticket.setRevoked(true);
		ticket.setLastModifiedAt(DateTime.now().getMillis());
	}

	@Override
	public void revoke(Collection<String> ticketIDs) {
		for (String ticketID : ticketIDs) {
			revoke(ticketID);
		}
	}

	@Override
	public void extendValidity(String ticketID, Long validUntil) {
		Ticket ticket = em.find(Ticket.class, ticketID);
		if (ticket.isRevoked()) {
			throw new QTicketRevokedException(MessageFormat.format(
					"Cannot extend the validity of ticket with ID "
							+ "{0}; the ticket is revoked.",
					new Object[] { ticketID }));
		}
		ticket.setValidUntil(validUntil);
		ticket.setLastModifiedAt(DateTime.now().getMillis());
	}

	@Override
	public void extendAutoExtendValidity(String ticketID, Long validUntil) {
		Ticket ticket = em.find(Ticket.class, ticketID);
		if (ticket.isRevoked()) {
			throw new QTicketRevokedException(MessageFormat.format(
					"Cannot extend the auto extend date of ticket with ID {0} "
							+ "; the ticket is revoked.",
					new Object[] { ticketID }));
		}
		ticket.setAutoExtendUntil(validUntil);
		ticket.setLastModifiedAt(DateTime.now().getMillis());
	}

	@Override
	public TicketDTO getTicket(String ticketID) {
		return ConverterUtil.ticketToTicketDTO(em.find(Ticket.class, ticketID));
	}

	@Override
	public Set<TicketDTO> findTickets(TicketSearchCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
		Root<Ticket> root = cq.from(Ticket.class);

		// Apply criteria
		if (StringUtils.isNotBlank(criteria.getPayload())) {
			Predicate pr = null;
			if (criteria.getPayloadMatch() == PayloadMatch.EXACT) {
				pr = cb.equal(root.<String> get("payload"),
						criteria.getPayload());
			} else if (criteria.getPayloadMatch() == PayloadMatch.CONTAINS) {
				pr = cb.like(root.<String> get("payload"),
						criteria.getPayload());
			}
			if (cq.getRestriction() != null) {
				cq = cq.where(cb.and(cq.getRestriction(), pr));
			} else {
				cq = cq.where(pr);
			}
			// cq = cq.where(cb.and(cq.getRestriction(), pr));
		}
		if (criteria.getExpired() != null) {
			Predicate pr = null;
			Long targetDate = (criteria.getTargetDate() != null) ? criteria
					.getTargetDate() : DateTime.now().getMillis();
			if (criteria.getExpired()) {
				pr = cb.lt(root.<Long> get("validUntil"), targetDate);
			} else {
				pr = cb.ge(root.<Long> get("validUntil"), targetDate);
			}
			if (cq.getRestriction() != null) {
				cq = cq.where(cb.and(cq.getRestriction(), pr));
			} else {
				cq = cq.where(pr);
			}
		}
		if (criteria.getRevoked() != null) {
			Predicate pr = cb.equal(root.<Boolean> get("revoked"),
					criteria.getRevoked());
			if (cq.getRestriction() != null) {
				cq = cq.where(cb.and(cq.getRestriction(), pr));
			}else{
				cq = cq.where(pr);
			}
		}

		TypedQuery<Ticket> query = em.createQuery(cq);
		List<Ticket> queryResult = query.getResultList();
		Set<TicketDTO> retVal = new HashSet<>(queryResult.size());
		for (Ticket ticket : queryResult) {
			retVal.add(ConverterUtil.ticketToTicketDTO(ticket));
		}
		return retVal;
	}

	@Override
	public void cleanupExpired() {
		Query query = em
				.createQuery("DELETE FROM Ticket t WHERE t.validUntil < :currentDate");
		query.setParameter("currentDate", DateTime.now().getMillis());
		query.executeUpdate();

		// Flush and clear the entity manager so as to pick up the deletion of
		// the tickets above
		// since the delete query bypasses the first-level cache of the entity
		// manager
		em.flush();
		em.clear();
	}

	@Override
	public void cleanupRevoked() {
		Query query = em
				.createQuery("DELETE FROM Ticket t WHERE t.revoked = true");
		query.executeUpdate();

		// Flush and clear the entity manager so as to pick up the deletion of
		// the tickets above
		// since the delete query bypasses the first-level cache of the entity
		// manager

		em.flush();
		em.clear();
	}
}
