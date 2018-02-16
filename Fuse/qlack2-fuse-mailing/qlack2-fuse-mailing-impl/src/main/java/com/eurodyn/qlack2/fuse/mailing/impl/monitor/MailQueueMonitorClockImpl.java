package com.eurodyn.qlack2.fuse.mailing.impl.monitor;

import com.eurodyn.qlack2.fuse.mailing.api.MailQueueMonitorClock;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.aries.blueprint.annotation.config.ConfigProperty;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = {MailQueueMonitorClock.class})
public class MailQueueMonitorClockImpl implements MailQueueMonitorClock, Runnable {

  /**
   * Logger reference
   */
  private static final Logger LOGGER =
    Logger.getLogger(MailQueueMonitorClockImpl.class.getName());

  private Thread thread;

  @ConfigProperty("${interval}")
  private long interval;

  @Inject
  private MailQueueMonitor monitor;

  @Override
  @PostConstruct
  public void start() {
    if (thread != null) {
      LOGGER.log(Level.FINE, "Mail queue monitor is started.");
      return;
    }

    LOGGER.log(Level.FINE, "Mail queue monitor starting ...");
    thread = new Thread(this, "Mail queue monitor thread");
    thread.start();
  }

  @Override
  @PreDestroy
  public void stop() {
    if (thread == null) {
      LOGGER.log(Level.FINE, "Mail queue monitor is stopped.");
      return;
    }

    LOGGER.log(Level.FINE, "Mail queue monitor stopping ...");
    thread.interrupt();
    thread = null;
  }

  @Override
  public void status() {
    LOGGER.log(Level.FINE, "Checking mail queue monitor status ...");
    if (thread != null && thread.isAlive()) {
      LOGGER.log(Level.FINE, "Mail queue monitor is running.");
    } else {
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
    try {
      LOGGER.log(Level.FINEST, "Mail queue monitor executing ...");
      monitor.checkAndSendQueued();
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Mail queue processing produced an error.", e);
    }
  }

}
