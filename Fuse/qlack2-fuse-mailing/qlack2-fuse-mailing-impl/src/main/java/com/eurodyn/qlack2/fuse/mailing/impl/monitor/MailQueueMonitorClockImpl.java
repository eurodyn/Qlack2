package com.eurodyn.qlack2.fuse.mailing.impl.monitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eurodyn.qlack2.fuse.mailing.api.MailQueueMonitorClock;

public class MailQueueMonitorClockImpl implements MailQueueMonitorClock, Runnable {
	private static final Logger LOGGER =
			Logger.getLogger(MailQueueMonitorClockImpl.class.getName());

	private Thread thread;

	private boolean autostart;
	private long interval;

	private MailQueueMonitor monitor;

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setMonitor(MailQueueMonitor monitor) {
		this.monitor = monitor;
	}

	public void init() {
		if (autostart) {
			start();
		}
	}

	public void destroy() {
		stop();
	}

	@Override
	public synchronized void start() {
		if (thread != null) {
			LOGGER.log(Level.FINE, "Mail queue monitor is started.");
			return;
		}

		LOGGER.log(Level.FINE, "Mail queue monitor starting ...");
		thread = new Thread(this, "Mail queue monitor thread");
		thread.start();
	}

	@Override
	public synchronized void stop() {
		if (thread == null) {
			LOGGER.log(Level.FINE, "Mail queue monitor is stopped.");
			return;
		}

		LOGGER.log(Level.FINE, "Mail queue monitor stopping ...");
		thread.interrupt();
		thread = null;
	}

	@Override
	public synchronized void status() {
		LOGGER.log(Level.FINE, "Checking mail queue monitor status ...");
		if (thread != null && thread.isAlive()) {
			LOGGER.log(Level.FINE, "Mail queue monitor is running.");
		}
		else {
			LOGGER.log(Level.FINE, "Mail queue monitor is stopped.");
		}
	}

	@Override
	public void run() {
		try {
			Thread current = Thread.currentThread();
			while (!current.isInterrupted()) {
				tick();
				Thread.sleep(interval);
			}
		} catch (InterruptedException ex) {
			LOGGER.log(Level.FINEST, "Mail queue monitor interrupted.");
		}
	}

	private void tick() {
		LOGGER.log(Level.FINEST, "Mail queue monitor executing ...");
		monitor.checkAndSendQueued();
	}

}
