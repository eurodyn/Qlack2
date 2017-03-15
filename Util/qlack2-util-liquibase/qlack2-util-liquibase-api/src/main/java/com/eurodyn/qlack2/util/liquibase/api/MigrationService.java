package com.eurodyn.qlack2.util.liquibase.api;

import org.osgi.framework.Bundle;

/**
 * The specification of the service being exposed to OSGi, so that third-party
 * code can programmatically interact with this component.
 */
public interface MigrationService {
    /**
     * Lists all currently queued changelogs.
     * @return
     */
    QChangeLogEntry[] list();

    /**
     * Executes pending changelogs.
     */
    void run();

    /**
     * Queues a new bundle.
     * @param bundle
     */
    void registerBundleForMigrations(Bundle bundle);

    /**
     * Dequeues a previously queued bundle.
     * @param bundle
     */
    void unregisterBundleFromMigrations(Bundle bundle);
}
