package com.eurodyn.qlack2.util.liquibase.impl;

import com.eurodyn.qlack2.util.liquibase.api.QChangeLogEntry;
import org.osgi.framework.Bundle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class BundleProcessor {
    /** JUL reference */
    private final static Logger LOGGER = Logger.getLogger(BundleProcessor.class.getName());

    @Inject
    private MigrationExecutor migrationExecutor;

    @Inject
    Settings settings;

    private QChangeLogEntry addLiquibaseConfig(QChangeLogEntry changeLogEntry) {
        final Map<String, String> liquibase = settings.getLiquibase();
        if (liquibase != null) {
            for (String key : liquibase.keySet()) {
                changeLogEntry.setProperty(key, liquibase.get(key));
            }
        }

        return changeLogEntry;
    }

    public void registerBundle(Bundle bundle) {
        /** Check if the bundle has headers for liquibase update */
        final String liquibaseChangeLogHeader = bundle.getHeaders().get(Settings.HEADER_Q_LIQUIBASE_CHANGELOG);

        LOGGER.log(Level.FINE, "Found a bundle with Liquibase changelogs: {0}.", bundle.getSymbolicName());
        QChangeLogEntry changeLogEntry = new QChangeLogEntry();
        changeLogEntry.setAddedOn(Instant.now());
        changeLogEntry.setBundle(bundle);
        changeLogEntry.setChangeLog(liquibaseChangeLogHeader);
        String priorityHeader = bundle.getHeaders().get(Settings.HEADER_Q_LIQUIBASE_PRIORITY);
        if (priorityHeader != null) {
            changeLogEntry.setPriority(Integer.parseInt(priorityHeader));
        } else {
            changeLogEntry.setPriority(Settings.LIQUIBASE_DEFAULT_PRIORITY);
        }
        changeLogEntry = addLiquibaseConfig(changeLogEntry);
        migrationExecutor.enqueue(changeLogEntry);
    }

    public void unregisterBundle(Bundle bundle) {
        migrationExecutor.dequeue(bundle.getSymbolicName());
    }
}
