package com.eurodyn.qlack2.util.liquibase.api;

/**
 * The list of available discovery modes
 */
public enum MigrationDiscoveryMode {
    // Scripts are discovered using the `Q-Liquibase-ChangeLog` header automatically
    // discovered during bundle deployment.
    AUTO,

    // Your bundle manually queues itself for execution.
    MANUAL
}
