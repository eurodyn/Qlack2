package com.eurodyn.qlack2.fuse.scheduler.impl;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.simpl.RAMJobStore;

import com.eurodyn.qlack2.fuse.scheduler.api.exception.QSchedulerException;

public class SchedulerProvider {

	private static final Logger logger = Logger.getLogger(SchedulerProvider.class.getName());

	private static final String DATA_SOURCE_NAME = "quartz";

	private static final String DATA_SOURCE_NON_MANAGED_TX_NAME = "quartzNonManagedTX";

	private static final String PROP_THREAD_POOL_THREAD_COUNT =
			StdSchedulerFactory.PROP_THREAD_POOL_PREFIX + ".threadCount";

	private static final String PROP_JOB_STORE_DRIVER_DELEGATE_CLASS =
			StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".driverDelegateClass";

	private static final String PROP_JOB_STORE_TABLE_PREFIX =
			StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".tablePrefix";

	private static final String PROP_JOB_STORE_IS_CLUSTERED =
			StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".isClustered";

	private static final String PROP_JOB_STORE_DATA_SOURCE =
			StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".dataSource";

	private static final String PROP_JOB_STORE_NON_MANAGED_TX_DATA_SOURCE =
			StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".nonManagedTXDataSource";

	private static final String PROP_JOB_STORE_DONT_SET_AUTO_COMMIT_FALSE =
			StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".dontSetAutoCommitFalse";

	private static final String PROP_JOB_STORE_DONT_SET_NON_MANAGED_TX_AUTO_COMMIT_FALSE =
			StdSchedulerFactory.PROP_JOB_STORE_PREFIX + ".dontSetNonManagedTXConnectionAutoCommitFalse";

	private static final String PROP_DATA_SOURCE_JNDI_URL =
			StdSchedulerFactory.PROP_DATASOURCE_PREFIX + "." + DATA_SOURCE_NAME + ".jndiURL";

	private static final String PROP_DATA_SOURCE_NON_MANAGED_TX_JNDI_URL =
			StdSchedulerFactory.PROP_DATASOURCE_PREFIX + "." + DATA_SOURCE_NON_MANAGED_TX_NAME + ".jndiURL";

	private Long schedulerIdleWaitTime;
	private Integer threadPoolThreadCount;

	private String jobStoreClass = RAMJobStore.class.getName();
	private String jobStoreDriverDelegateClass;
	private Boolean jobStoreIsClustered;
	private Boolean jobStoreDontSetAutoCommitFalse = true;
	private Boolean jobStoreDontSetNonManagedTXConnectionAutoCommitFalse = false;

	private String dataSourceJndiURL;
	private String dataSourceNonManagedTXJndiURL;

	public void setSchedulerIdleWaitTime(Long schedulerIdleWaitTime) {
		this.schedulerIdleWaitTime = schedulerIdleWaitTime;
	}

	public void setThreadPoolThreadCount(Integer threadPoolThreadCount) {
		this.threadPoolThreadCount = threadPoolThreadCount;
	}

	public void setJobStoreClass(String jobStoreClass) {
		this.jobStoreClass = jobStoreClass;
	}

	public void setJobStoreDriverDelegateClass(String jobStoreDriverDelegateClass) {
		this.jobStoreDriverDelegateClass = jobStoreDriverDelegateClass;
	}

	public void setJobStoreIsClustered(Boolean jobStoreIsClustered) {
		this.jobStoreIsClustered = jobStoreIsClustered;
	}

	public void setJobStoreDontSetAutoCommitFalse(Boolean jobStoreDontSetAutoCommitFalse) {
		this.jobStoreDontSetAutoCommitFalse = jobStoreDontSetAutoCommitFalse;
	}

	public void setJobStoreDontSetNonManagedTXConnectionAutoCommitFalse(Boolean jobStoreDontSetNonManagedTXConnectionAutoCommitFalse) {
		this.jobStoreDontSetNonManagedTXConnectionAutoCommitFalse = jobStoreDontSetNonManagedTXConnectionAutoCommitFalse;
	}

	public void setDataSourceJndiURL(String dataSourceJndiURL) {
		this.dataSourceJndiURL = dataSourceJndiURL;
	}

	public void setDataSourceNonManagedTXJndiURL(String dataSourceNonManagedTXJndiURL) {
		this.dataSourceNonManagedTXJndiURL = dataSourceNonManagedTXJndiURL;
	}

	private Scheduler scheduler;

	/**
	 * Initialize the Scheduler instance.
	 *
	 * @throws SchedulerException
	 */
	private void initScheduler() throws SchedulerException {
		logger.log(Level.CONFIG, "Initialising default scheduler...");
		StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();

		Properties properties = getConfigProperties();

		// Set TCCL to scheduler bundle, so that we can find the ServiceInvokerJob class
		ClassLoader initClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(SchedulerProvider.class.getClassLoader());
		try {
			schedulerFactory.initialize(properties);
			scheduler = schedulerFactory.getScheduler();
		} finally {
			Thread.currentThread().setContextClassLoader(initClassLoader);
		}

		logger.log(Level.CONFIG, "Default scheduler initialised.");
	}

	private Properties getConfigProperties() {
		Properties properties = new Properties();

		properties.put(StdSchedulerFactory.PROP_SCHED_SKIP_UPDATE_CHECK, "true");

		if (schedulerIdleWaitTime != null) {
			properties.put(StdSchedulerFactory.PROP_SCHED_IDLE_WAIT_TIME, Long.toString(schedulerIdleWaitTime));
		}

		if (threadPoolThreadCount != null) {
			properties.put(PROP_THREAD_POOL_THREAD_COUNT, Integer.toString(threadPoolThreadCount));
		}

		properties.put(StdSchedulerFactory.PROP_JOB_STORE_CLASS, jobStoreClass);

		if (jobStoreClass.equals(JobStoreTX.class.getName()) || jobStoreClass.equals(JobStoreCMT.class.getName())) {
			if (jobStoreDriverDelegateClass != null) {
				properties.put(PROP_JOB_STORE_DRIVER_DELEGATE_CLASS, jobStoreDriverDelegateClass);
			}

			properties.put(PROP_JOB_STORE_TABLE_PREFIX, "sch_");

			if (jobStoreIsClustered != null) {
				properties.put(PROP_JOB_STORE_IS_CLUSTERED, Boolean.toString(jobStoreIsClustered));
			}

			properties.put(PROP_JOB_STORE_DATA_SOURCE, DATA_SOURCE_NAME);
			properties.put(PROP_JOB_STORE_DONT_SET_AUTO_COMMIT_FALSE, Boolean.toString(jobStoreDontSetAutoCommitFalse));
			properties.put(PROP_DATA_SOURCE_JNDI_URL, dataSourceJndiURL);

			if (jobStoreClass.equals(JobStoreCMT.class.getName())) {
				properties.put(PROP_JOB_STORE_NON_MANAGED_TX_DATA_SOURCE, DATA_SOURCE_NON_MANAGED_TX_NAME);
				properties.put(PROP_JOB_STORE_DONT_SET_NON_MANAGED_TX_AUTO_COMMIT_FALSE, Boolean.toString(jobStoreDontSetNonManagedTXConnectionAutoCommitFalse));
				properties.put(PROP_DATA_SOURCE_NON_MANAGED_TX_JNDI_URL, dataSourceNonManagedTXJndiURL);
			}
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			logger.log(Level.CONFIG, "Property {0} = {1}", new Object[]{ entry.getKey(), entry.getValue() });
		}
		return properties;
	}

	/**
	 * Start the Scheduler instance.
	 *
	 * @throws QSchedulerException
	 */
	public void startScheduler() throws QSchedulerException {
		try {
			if (scheduler == null) {
				initScheduler();
			}

			logger.log(Level.CONFIG, "Starting scheduler...");
			// XXX start delayed for OSGi services to become avail ?
			scheduler.start();
			logger.log(Level.CONFIG, "Scheduler started [{0}].", scheduler);
		} catch (SchedulerException ex) {
			throw new QSchedulerException("Cannot start the scheduler", ex);
		}
	}

	/**
	 * Shutdown the Scheduler instance.
	 *
	 * @throws QSchedulerException
	 */
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
