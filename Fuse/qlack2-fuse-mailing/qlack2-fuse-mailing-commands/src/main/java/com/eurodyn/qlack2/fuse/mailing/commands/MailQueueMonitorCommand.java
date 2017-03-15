package com.eurodyn.qlack2.fuse.mailing.commands;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import com.eurodyn.qlack2.fuse.mailing.api.MailQueueMonitorClock;

@Command(scope = "qlack", name = "mail-queue-monitor", description = "Control the mailing queue monitor")
@Service
public final class MailQueueMonitorCommand implements Action {

	@Argument(index = 0, name = "action", description = "The action to execute", required = true, multiValued = false)
	private String action;

	@Reference
	private MailQueueMonitorClock clock;

	@Override
	public Object execute() {

		if (action.equals("start")) {
			clock.start();
		}
		else if (action.equals("stop")) {
			clock.stop();
		}
		else if (action.equals("status")) {
			clock.status();
		}

		return null;
	}

}
