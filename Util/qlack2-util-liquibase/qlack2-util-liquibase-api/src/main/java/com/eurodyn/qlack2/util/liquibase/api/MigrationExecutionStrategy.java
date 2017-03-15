package com.eurodyn.qlack2.util.liquibase.api;

/**
 * The list of available migration execution strategies.
 */
public enum MigrationExecutionStrategy {
    ASAP,           // Changelogs are executed as soon as they are discovered.
    START_LEVEL,    // Changelogs are queued until a predefined SL is reached.
    MANUAL          // Manual execution of changelogs via shell commands.
}
