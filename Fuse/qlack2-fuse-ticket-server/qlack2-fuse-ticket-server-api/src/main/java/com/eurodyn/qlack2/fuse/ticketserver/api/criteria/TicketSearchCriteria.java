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
package com.eurodyn.qlack2.fuse.ticketserver.api.criteria;

public class TicketSearchCriteria {
	public enum PayloadMatch {
		EXACT,
		CONTAINS
	}
	
	private String payload;
	private PayloadMatch payloadMatch;
	private Boolean revoked;
	private Boolean expired;
	private Long targetDate;
	
	public static class TicketSearchCriteriaBuilder {
		private String payload;
		private PayloadMatch payloadMatch;
		private Boolean revoked;
		private Boolean expired;
		private Long targetDate;
		
		private TicketSearchCriteriaBuilder() {}
		
		public static TicketSearchCriteriaBuilder createCriteria() {
			return new TicketSearchCriteriaBuilder();
		}

		public TicketSearchCriteria build() {
			TicketSearchCriteria criteria = new TicketSearchCriteria();
			criteria.setExpired(expired);
			criteria.setPayload(payload);
			criteria.setPayloadMatch(payloadMatch);
			criteria.setRevoked(revoked);
			criteria.setTargetDate(targetDate);
			return criteria;
		}
		
		public TicketSearchCriteriaBuilder revoked(boolean revoked) {
			this.revoked = revoked;
			return this;
		}
		
		public TicketSearchCriteriaBuilder expired(boolean expired) {
			this.expired = expired;
			return this;
		}
		
		public TicketSearchCriteriaBuilder withPayload(String payload, PayloadMatch payloadMatch) {
			this.payload = payload;
			this.payloadMatch = payloadMatch;
			return this;
		}
		
		public TicketSearchCriteriaBuilder withTargetDate(long targetDate) {
			this.targetDate = targetDate;
			return this;
		}
	}
	
	private TicketSearchCriteria() {}

	public String getPayload() {
		return payload;
	}

	private void setPayload(String payload) {
		this.payload = payload;
	}

	public PayloadMatch getPayloadMatch() {
		return payloadMatch;
	}

	public void setPayloadMatch(PayloadMatch payloadMatch) {
		this.payloadMatch = payloadMatch;
	}

	public Boolean getRevoked() {
		return revoked;
	}

	private void setRevoked(Boolean revoked) {
		this.revoked = revoked;
	}

	public Boolean getExpired() {
		return expired;
	}

	private void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public Long getTargetDate() {
		return targetDate;
	}

	private void setTargetDate(Long targetDate) {
		this.targetDate = targetDate;
	}
}
