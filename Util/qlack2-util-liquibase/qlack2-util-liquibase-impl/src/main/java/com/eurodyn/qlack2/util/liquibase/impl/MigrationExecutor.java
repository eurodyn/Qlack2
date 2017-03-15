package com.eurodyn.qlack2.util.liquibase.impl;

import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import com.eurodyn.qlack2.util.liquibase.api.MigrationExecutionStrategy;
import com.eurodyn.qlack2.util.liquibase.api.QChangeLogEntry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.eurodyn.qlack2.util.liquibase.api.MigrationExecutionStrategy.START_LEVEL;

/**
 * This is the component in which everything else is wrapped together. It also
 * provides methods to queue/dequeue chagelogs and bootstraps monitoring threads
 * to check for the availability of the underlying datasource as well as the
 * current start-level of the OSGi framework.
 */
@Singleton
public class MigrationExecutor {
    /**
     * JUL reference
     */
    private final static Logger LOGGER = Logger.getLogger(MigrationExecutor.class.getName());

    /** The PID under which config admin stores the configuration of this service */
    private final String PID = "com.eurodyn.qlack2.util.liquibase";

    /**
     * The queue with changelogs to be executed
     */
    public static Queue<QChangeLogEntry> queue = new PriorityQueue<>(
            new QChangeLogEntry.QChangeLogEntryComparator());

    /**
     * The datasource to use for migrations
     */
    private static DataSource dataSource;

    /**
     * The framework's current start-level. Note:
     * 1/ This is not updated in perfect real-time.
     * 2/ The maximum start-level observed (and reported by this variable) is the
     * one specified in Settings.bootCompleteSL.
     * 3/ This value is updated only when Settings.migrationExecutionStrategy is
     * 'START_LEVEL'.
     */
    private static int currentStartLevel = 0;

    /**
     * A thread responsible to check for prerequisites of the migration
     */
    private MigrationAgent migrationAgent;

    /**
     * A flag to not try to initialise liquibase on the same database multiple
     * times.
     */
    private static boolean changeLogHistoryInitialised = false;

    /**
     * The config-admin settings of this component
     */
    @Inject
    private Settings settings;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private ConfigurationAdmin configurationAdmin;

    /**
     * Component's default bootstrap logic
     */
    @PostConstruct
    public void init() {
        LOGGER.log(Level.CONFIG, "MigrationExecutor initialising.");

        if (!settings.isConfigSet()) {
            LOGGER.log(Level.WARNING, "Config admin did not propagate " +
                    "configuration on time. Trying to fetch configuration " +
                    "manually.");
            try {
                final Configuration configuration = configurationAdmin.getConfiguration(PID);
                settings.setConfigAdmin(configuration.getProperties());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not fetch manually " +
                        "configuration from config admin.", e);
            }

            if (!settings.isConfigSet()) {
                throw new RuntimeException("Could not obtain values from config admin service");
            }
        }

        /** Initialise the agent that performs the migrations */
        migrationAgent = new MigrationAgent(settings, bundleContext);
        migrationAgent.start();

        LOGGER.log(Level.CONFIG, "MigrationExecutor initialised with " +
                "migration strategy: {0}", settings.getMigrationExecutionStrategy().toString());
    }

    /**
     * Component's default shutdown logic, properly terminating previously
     * fired-up threads.
     *
     * @throws InterruptedException
     */
    @PreDestroy
    public void shutdown() throws InterruptedException {
        migrationAgent.terminate();
        migrationAgent.unregisterLiquibaseBootMigrationsDoneServiceServiceRegistration();
        migrationAgent.join();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static int getCurrentStartLevel() {
        return currentStartLevel;
    }

    public static void setCurrentStartLevel(int currentStartLevel) {
        MigrationExecutor.currentStartLevel = currentStartLevel;
    }

    public static void setDataSource(DataSource dataSource) {
        MigrationExecutor.dataSource = dataSource;
    }

    public static boolean isDatasourceInjected() {
        return dataSource != null;
    }

    public static boolean isChangeLogHistoryInitialised() {
        return changeLogHistoryInitialised;
    }

    public static void setChangeLogHistoryInitialised(boolean changeLogHistoryInitialised) {
        MigrationExecutor.changeLogHistoryInitialised = changeLogHistoryInitialised;
    }

    /**
     * Enqueues a changelog to be executed when preconditions are met.
     *
     * @param changeLogEntry
     */
    public void enqueue(QChangeLogEntry changeLogEntry) {
        LOGGER.log(Level.FINE, "Enqueuing {0}.", changeLogEntry.getBundle().getSymbolicName());
        if (queue.contains(changeLogEntry)) {
            LOGGER.log(Level.FINE, "Bundle {0} is already queued.", changeLogEntry.getBundle().getSymbolicName());
        } else {
            queue.add(changeLogEntry);
            LOGGER.log(Level.FINE, "Bundle {0} successfully queued.", changeLogEntry.getBundle().getSymbolicName());
        }
    }

    /**
     * Dequeues a previously enqueued changelog. This is, usually, taking place
     * when an already deployed bundle is stopped or updated.
     *
     * @param bundleSymbolicName
     */
    public void dequeue(String bundleSymbolicName) {
        if (queue.contains(bundleSymbolicName)) {
            queue.remove(bundleSymbolicName);
            LOGGER.log(Level.FINE, "Dequeuing {0}.", bundleSymbolicName);
        } else {
            LOGGER.log(Level.FINEST, "Can not dequeue bundle not already queued: {0}.", bundleSymbolicName);
        }
    }

    /**
     * An entrypoint to allow external callers to manually trigger changelogs
     * exeucution.
     */
    public static void runMigrations() {
        QChangeLogRunner.runMigrations(MigrationExecutor.getDataSource());
    }
}

/**
 * This thread checks if preconditions are met and listens for newly enqueued
 * changelogs to be executed.
 */
class MigrationAgent extends Thread {
    /**
     * JUL reference
     */
    private final static Logger LOGGER = Logger.getLogger(MigrationAgent.class.getName());
    private final BundleContext bundleContext;

    /**
     * A flag denoting that this thread should terminate
     */
    private boolean terminate = false;

    /**
     * A reference to the settings of this component
     */
    private Settings settings;

    /**
     * The amount of time to delay/sleep between loops
     */
    private long loopDelay = 1000;

    /**
     * A flag to monitor whether migrations queued during boot have been
     * executed.
     */
    private ServiceRegistration<LiquibaseBootMigrationsDoneService>
            liquibaseBootMigrationsDoneServiceServiceRegistration;

    MigrationAgent(Settings settings, BundleContext bundleContext) {
        this.settings = settings;
        this.bundleContext = bundleContext;
    }

    void terminate() {
        terminate = true;
    }

    /**
     * Unregister the liquibaseBootMigrationsDoneServiceServiceRegistration service.
     * (yeap, that's a long name!).
     */
    public void unregisterLiquibaseBootMigrationsDoneServiceServiceRegistration() {
        if (liquibaseBootMigrationsDoneServiceServiceRegistration != null) {
            liquibaseBootMigrationsDoneServiceServiceRegistration.unregister();
        }
    }

    private void registerMigrationsDoneService() {
        if (liquibaseBootMigrationsDoneServiceServiceRegistration == null) {
            LOGGER.log(Level.FINE, "Initial migrations executed. Registering " +
                    "LiquibaseBootMigrationsDoneService service.");
            LiquibaseBootMigrationsDoneService liquibaseBootMigrationsDoneService =
                    new LiquibaseBootMigrationsDoneServiceImpl();
            liquibaseBootMigrationsDoneServiceServiceRegistration =
                    bundleContext.registerService(LiquibaseBootMigrationsDoneService.class,
                            liquibaseBootMigrationsDoneService, null);
        }
    }

    public void run() {
        LOGGER.log(Level.FINE, "MigrationAgent thread started.");
        /** Check if migration initialisation conditions are met. */

        /** First, check the datasource is available. **/
        if (settings.getDatasource() != null) {
            while (!terminate && MigrationExecutor.getDataSource() == null) {
                try {
                    Thread.sleep(loopDelay);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            if (MigrationExecutor.getDataSource() != null) {
                LOGGER.log(Level.FINE, "Datasource injection requirement satisfied.");
            } else {
                if (terminate) {
                    LOGGER.log(Level.SEVERE, "Datasource could not be injected because thread was terminated.");
                } else {
                    LOGGER.log(Level.SEVERE, "Datasource could not be injected because it could not be found.");
                }
            }
        } else {
            LOGGER.log(Level.SEVERE, "Configuration does not specify a datasource. Terminating thread.");
            terminate();
        }

        /** Second, check migration strategy conditions are met. */
        if (!terminate) {
            switch (settings.getMigrationExecutionStrategy()) {
                case START_LEVEL:
                    LOGGER.log(Level.FINE, "Waiting for SL{0}.", settings.getBootCompleteSL());
                    while (!terminate && MigrationExecutor.getCurrentStartLevel() < settings.getBootCompleteSL()) {
                        try {
                            Thread.sleep(loopDelay);
                        } catch (InterruptedException e) {
                            LOGGER.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                    if (MigrationExecutor.getCurrentStartLevel() == settings.getBootCompleteSL()) {
                        LOGGER.log(Level.FINE, "SL{0} successfully reached.", settings.getBootCompleteSL());
                    } else {
                        LOGGER.log(Level.SEVERE, "SL{0} was not reached, terminating thread.", settings.getBootCompleteSL());
                        terminate();
                    }
                    break;
            }
        }

        /** Start listening for changes and apply them. */
        if (!terminate && Arrays.asList(MigrationExecutionStrategy.ASAP, START_LEVEL).contains(settings.getMigrationExecutionStrategy())) {
            /** Introduce startup delay */
            if (settings.getInitialExecutionDelay() > 0) {
                LOGGER.log(Level.FINE, "Start-up delay of {0}ms", settings.getInitialExecutionDelay());
                try {
                    Thread.sleep(settings.getInitialExecutionDelay());
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }

            /** Loop listening for changelogs */
            LOGGER.log(Level.INFO, "Start listening and executing migrations.");
            while (!terminate) {
                if (MigrationExecutor.queue.size() > 0) {
                    QChangeLogRunner.runMigrations(MigrationExecutor.getDataSource());
                }
                try {
                    Thread.sleep(loopDelay);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }

                /** At this point all queued migrations have been executed, so
                 * register the LiquibaseBootMigrationsDoneService in OSGi, so
                 * that other bundles depending on it can start accessing the
                 * database.
                 */
                if (liquibaseBootMigrationsDoneServiceServiceRegistration == null) {
                    registerMigrationsDoneService();
                }
            }
        }

        LOGGER.log(Level.INFO, "MigrationAgent thread terminated. Only manual migrations can now be executed.");
        if (liquibaseBootMigrationsDoneServiceServiceRegistration == null) {
            registerMigrationsDoneService();
        }
    }
}