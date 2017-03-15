package com.eurodyn.qlack2.fuse.mailing.api;

public interface MailQueueMonitorClock {

	/**
	 * Start the mail queue monitor.
	 */
	void start();

	/**
	 * Stop the mail queue monitor.
	 */
	void stop();

	/**
	 * Check the status of the mail queue monitor.
	 */
	void status();

}