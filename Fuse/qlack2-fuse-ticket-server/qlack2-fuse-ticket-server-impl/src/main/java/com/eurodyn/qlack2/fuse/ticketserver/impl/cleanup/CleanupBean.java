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
package com.eurodyn.qlack2.fuse.ticketserver.impl.cleanup;

import com.eurodyn.qlack2.fuse.ticketserver.api.TicketServerService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CleanupBean implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(CleanupBean.class.getName());
	private Thread cleanupThread;
	
	private TicketServerService ticketService;
	private long cleanupInterval;
	private boolean cleanupExpired;
	private boolean cleanupRevoked;
	private boolean enableCleanup;
	private long startupDelay;
	
	public void setEnableCleanup(boolean enableCleanup) {
		this.enableCleanup = enableCleanup;
	}

	public void setTicketService(TicketServerService ticketService) {
		this.ticketService = ticketService;
	}

	public void setCleanupInterval(long cleanupInterval) {
		this.cleanupInterval = cleanupInterval;
	}

	public void setCleanupExpired(boolean cleanupExpired) {
		this.cleanupExpired = cleanupExpired;
	}

	public void setCleanupRevoked(boolean cleanupRevoked) {
		this.cleanupRevoked = cleanupRevoked;
	}

	public void setStartupDelay(long startupDelay) {
		this.startupDelay = startupDelay;
	}

	public void init() {
		// No need to start the cleanup thread if the configuration does not 
		// allow us to cleanup any tickets.
		if (enableCleanup && (cleanupExpired || cleanupRevoked)) {
			cleanupThread = new Thread(this);
			cleanupThread.start();
			LOGGER.log(Level.FINE, "Started tickets cleanup daemon.");
		}
	}
	
	public void destroy() {
		if (cleanupThread != null && cleanupThread.isAlive()) {
			cleanupThread.interrupt();
		}
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(startupDelay);
			while (!Thread.currentThread().isInterrupted()) {
				if (cleanupExpired) {
					ticketService.cleanupExpired();
				}
				if (cleanupRevoked) {
					ticketService.cleanupRevoked();
				}
				Thread.sleep(cleanupInterval);
			}
		} catch (InterruptedException ex) {
			LOGGER.log(Level.FINEST, "Ticket cleanup daemon interrupted.");
		}
	}
}
