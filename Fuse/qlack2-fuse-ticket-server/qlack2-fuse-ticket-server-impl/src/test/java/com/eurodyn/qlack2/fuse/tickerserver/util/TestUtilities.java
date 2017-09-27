package com.eurodyn.qlack2.fuse.tickerserver.util;

import com.eurodyn.qlack2.fuse.ticketserver.api.TicketDTO;

import java.util.Date;
import java.util.UUID;

public class TestUtilities {

    public static TicketDTO createTicketDTO(){
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(UUID.randomUUID().toString());
        ticketDTO.setAutoExtendValidUntil(new Date().getTime());
        ticketDTO.setLastModifiedAt(new Date().getTime());
        ticketDTO.setCreatedAt(new Date().getTime());
        ticketDTO.setAutoExtendDuration(new Date().getTime());
        ticketDTO.setPayload(TestConst.generateRandomString());
        ticketDTO.setRevoked(false);
        ticketDTO.setValidUntil(new Date().getTime());

        return ticketDTO;
    }
}
