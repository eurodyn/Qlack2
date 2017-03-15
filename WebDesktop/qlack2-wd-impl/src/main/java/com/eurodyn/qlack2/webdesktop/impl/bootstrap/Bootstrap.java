package com.eurodyn.qlack2.webdesktop.impl.bootstrap;

import com.eurodyn.qlack2.fuse.aaa.api.JSONConfigService;
import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.framework.BundleContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Bootstrap {
    @Inject
    @OsgiService
    BundleUpdateService bundleUpdateService;

    @Inject
    @OsgiService
    JSONConfigService jsonConfigService;

    @Inject
    BundleContext bundleContext;

    @Inject
    @OsgiService
    /** Make sure liquibase migrations are executed before allowing access
     * to this bundle.
     */
    LiquibaseBootMigrationsDoneService liquibaseBootMigrationsDoneService;

    @PostConstruct
    public void init() {
        /** Update translations **/
        bundleUpdateService.processBundle(bundleContext.getBundle(), "custom-qlack-lexicon.yaml");

        /** Update AAA */
        jsonConfigService.processBundle(bundleContext.getBundle());
    }
}
