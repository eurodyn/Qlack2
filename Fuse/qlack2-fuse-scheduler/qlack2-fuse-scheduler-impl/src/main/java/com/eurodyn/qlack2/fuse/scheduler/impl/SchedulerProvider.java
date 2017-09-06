package com.eurodyn.qlack2.fuse.scheduler.impl;

import com.eurodyn.qlack2.fuse.scheduler.api.exception.QSchedulerException;
import com.eurodyn.qlack2.fuse.scheduler.impl.utils.Constants;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.apache.aries.blueprint.annotation.config.ConfigProperty;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.text.MessageFormat;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Transactional
@Singleton
public class SchedulerProvider {

  private static final Logger logger = Logger.getLogger(SchedulerProvider.class.getName());

  /**
   * The container-managed DS to be used by the API of the scheduler
   */
  @ConfigProperty("${managedDS}")
  private String managedDS;

  /**
   * The non-managed DS to be used internally by the scheduler
   */
  @ConfigProperty("${nonManagedDS}")
  private String nonManagedDS;

  /**
   * Reference to the scheduler
   */
  private Scheduler scheduler;

  private Properties getConfigProperties() {
    Properties properties = new Properties();

    /** Set the Job Store as CMT */
    properties.put(StdSchedulerFactory.PROP_JOB_STORE_CLASS, Constants.QUARTZ_JOBSTORE_CLASS);

    /** Quartz tables' prefix */
    properties.put(StdSchedulerFactory.PROP_TABLE_PREFIX, Constants.QUARTZ_TABLE_PREFIX);

    /** Set the number of threads to use for the scheduler */
    properties.put(StdSchedulerFactory.PROP_THREAD_POOL_PREFIX + ".threadCount", String.valueOf(
      Constants.QUARTZ_THREADS));

    /** The prefix of scheduler's tables in the DB */
    properties.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".tablePrefix",
      Constants.QUARTZ_TABLE_PREFIX);

    /** The JNDI name for the managed & non-managed datasource */
    properties
      .put(StdSchedulerFactory.PROP_DATASOURCE_PREFIX + ".managed.jndiURL", managedDS);
    properties.put(StdSchedulerFactory.PROP_DATASOURCE_PREFIX + ".nonManaged.jndiURL",
      nonManagedDS);

    /** The driver to use for the Job Store - we use a generic JDBC driver not targeting a specific DB */
    properties.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".driverDelegateClass",
      Constants.QUARTZ_DRIVERDELEGATE_CLASS);

    /** The names of the managed & non-managed DSs */
    properties.put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".dataSource", "managed");
    properties
      .put(StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".nonManagedTXDataSource", "nonManaged");

    /** Perform a JNDI lookup each time a new connection is required */
    properties.put(StdSchedulerFactory.PROP_DATASOURCE_PREFIX + ".nonManaged.jndiAlwaysLookup",
      true);
    properties.put(StdSchedulerFactory.PROP_DATASOURCE_PREFIX + ".managed.jndiAlwaysLookup",
      true);

    /** Enable clustering */
    properties.put("org.quartz.jobStore.isClustered", "true");
    properties.put("org.quartz.scheduler.instanceId", "AUTO");

    /** Debug output */
    for (Entry<Object, Object> entry : properties.entrySet()) {
      logger
        .log(Level.CONFIG, "Property {0} = {1}", new Object[]{entry.getKey(), entry.getValue()});
    }

    return properties;
  }

  /**
   * Initialize the Scheduler instance.
   */
  private void initScheduler() throws SchedulerException {
    logger.log(Level.CONFIG, "Initialising default scheduler...");
    StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();

    /** Set TCCL to scheduler bundle, so that we can find the ServiceInvokerJob class */
    ClassLoader initClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(SchedulerProvider.class.getClassLoader());
    try {
      schedulerFactory.initialize(getConfigProperties());
      scheduler = schedulerFactory.getScheduler();
    } finally {
      Thread.currentThread().setContextClassLoader(initClassLoader);
    }

    logger.log(Level.CONFIG, "Default scheduler initialised.");
  }


  /**
   * Start the Scheduler instance.
   */
  @PostConstruct
  public void startScheduler() throws QSchedulerException {
    try {
      if (scheduler == null) {
        initScheduler();
      }
      logger.log(Level.CONFIG, MessageFormat.format("Starting scheduler with {0} seconds delay...",
        Constants.QUARTZ_STARTUP_DELAY_SEC));
      scheduler.startDelayed(Constants.QUARTZ_STARTUP_DELAY_SEC);
      logger.log(Level.CONFIG, "Scheduler started [{0}].", scheduler);
    } catch (SchedulerException ex) {
      throw new QSchedulerException("Cannot start the scheduler", ex);
    }
  }

  /**
   * Shutdown the Scheduler instance.
   */
  @PreDestroy
  public void shutdownScheduler() throws QSchedulerException {
    try {
      if (scheduler != null) {
        logger.log(Level.CONFIG, "Shutting-down scheduler...");
        scheduler.shutdown();
        logger.log(Level.CONFIG, "Scheduler shutdown.");
      }
    } catch (SchedulerException ex) {
      throw new QSchedulerException("Cannot shutdown the scheduler", ex);
    }
  }

  public Scheduler getScheduler() {
    return scheduler;
  }

}
