package com.eurodyn.qlack2.util.liquibase.impl;

import com.eurodyn.qlack2.util.liquibase.api.QChangeLogEntry;
import liquibase.Liquibase;
import liquibase.changelog.ChangeLogHistoryServiceFactory;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.osgi.OSGiResourceAccessor;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.core.xml.XMLChangeLogSAXParser;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Executes a changelog and updates the underlying database.
 */
public class QChangeLogRunner {
    /** JUL reference */
    private final static Logger LOGGER = Logger.getLogger(QChangeLogRunner.class.getName());

    private  static void runMigration(QChangeLogEntry changeLogEntry, Connection connection) {
        LOGGER.log(Level.INFO, "Executing: Priority {0}, ChangeLog: {1}.",
                new Object[]{changeLogEntry.getPriority(), changeLogEntry.getChangeLog()});
        try {
            /** Get a connection and setup specific database vendor handler */
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new JdbcConnection(connection));

            /** Initialise changelog history table */
            if (!MigrationExecutor.isChangeLogHistoryInitialised()) {
                ChangeLogHistoryServiceFactory.getInstance().getChangeLogService(database).init();
                MigrationExecutor.setChangeLogHistoryInitialised(true);
            }

            /** Setup changelog parameters */
            ChangeLogParameters changeLogParameters = new ChangeLogParameters();
            for (String s : changeLogEntry.getProperties().keySet()) {
                changeLogParameters.set(s, changeLogEntry.getProperty(s));
            }

            /** Setup the XLM parser for this changelog */
            ChangeLogParser changeLogParser = new XMLChangeLogSAXParser();
            final DatabaseChangeLog dbChangeLog = changeLogParser.parse(changeLogEntry.getChangeLog(),
                    changeLogParameters,
                    new OSGiResourceAccessor(changeLogEntry.getBundle()));

            /** Update (no context support) */
            Liquibase liquibase = new Liquibase(dbChangeLog, new OSGiResourceAccessor(
                    changeLogEntry.getBundle()), database);
            liquibase.update("");
        } catch (LiquibaseException e) {
            LOGGER.log(Level.SEVERE, MessageFormat.format(
                    "Could not execute changelog {0}.", changeLogEntry.getChangeLog()), e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Could not close database connection, " +
                        "possible connection leak.");
            }
        }
    }

    public static void runMigrations(DataSource dataSource) {
        while (MigrationExecutor.queue.size() > 0) {
            final QChangeLogEntry qChangeLogEntry = MigrationExecutor.queue.remove();
            try {
                QChangeLogRunner.runMigration(qChangeLogEntry, dataSource.getConnection());
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }
}
