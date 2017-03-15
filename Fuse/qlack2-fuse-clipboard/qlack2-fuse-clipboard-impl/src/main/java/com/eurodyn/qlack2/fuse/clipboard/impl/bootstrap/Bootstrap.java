package com.eurodyn.qlack2.fuse.clipboard.impl.bootstrap;

import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import org.ops4j.pax.cdi.api.OsgiService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Bootstrap {
    @Inject
    @OsgiService
    /** Make sure liquibase migrations are executed before allowing access
     * to this bundle.
     */
    LiquibaseBootMigrationsDoneService liquibaseBootMigrationsDoneService;

    @PostConstruct
    public void init() {
    }
}
