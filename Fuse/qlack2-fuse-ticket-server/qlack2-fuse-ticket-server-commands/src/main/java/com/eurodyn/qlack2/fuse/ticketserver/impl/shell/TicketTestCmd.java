package com.eurodyn.qlack2.fuse.ticketserver.impl.shell;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.eurodyn.qlack2.fuse.ticketserver.api.TicketDTO;
import com.eurodyn.qlack2.fuse.ticketserver.api.TicketServerService;

@Command(scope = "qlack-fuse-ticket-server", name = "ticket-validity-check", description = "Check whether a ticket is valid.")
@Service
public class TicketTestCmd implements Action {
	@Argument(index = 0, name = "ticketID", description = "The ticket ID to test for validity.", required = true, multiValued = false)
	private String ticketID;

	@Reference
	TicketServerService ticketServerService;
	
	@Override
	public Object execute() {
		TicketDTO ticketDTO = new TicketDTO();
		System.out.println("Ticket created: " + ticketServerService.createTicket(ticketDTO));
		return "";
		
		//return "Ticket validity: " + ticketServerService.isValid(ticketID);
	}
}
