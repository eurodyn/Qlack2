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
package com.eurodyn.qlack2.fuse.ticketserver.impl.util;

import com.eurodyn.qlack2.fuse.ticketserver.api.TicketDTO;
import com.eurodyn.qlack2.fuse.ticketserver.impl.model.Ticket;

public class ConverterUtil {
	public static TicketDTO ticketToTicketDTO(Ticket entity) {
		if (entity == null) {
			return null;
		}
		TicketDTO dto = new TicketDTO();
		dto.setId(entity.getId());
		dto.setPayload(entity.getPayload());
		dto.setCreatedAt(entity.getCreatedAt());
		dto.setLastModifiedAt(entity.getLastModifiedAt());
		dto.setValidUntil(entity.getValidUntil());
		dto.setCreatedBy(entity.getCreatedBy());
		dto.setRevoked(entity.isRevoked());
		dto.setAutoExtendDuration(entity.getAutoExtendDuration());
		dto.setAutoExtendValidUntil(entity.getAutoExtendUntil());

		return dto;
	}
}
