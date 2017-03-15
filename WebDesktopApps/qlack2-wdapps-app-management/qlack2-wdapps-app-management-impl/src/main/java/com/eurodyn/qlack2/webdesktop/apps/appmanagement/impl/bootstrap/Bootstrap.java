package com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl.bootstrap;

import com.eurodyn.qlack2.fuse.aaa.api.JSONConfigService;
import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
import com.eurodyn.qlack2.util.liquibase.api.LiquibaseBootMigrationsDoneService;
import com.eurodyn.qlack2.webdesktop.api.ApplicationRegistrationService;
import org.ops4j.pax.cdi.api.OsgiService;
import org.osgi.framework.BundleContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Bootstrap {
    private final String YAML = "OSGI-INF/custom-wd-app.yaml";

    @Inject
    @OsgiService
    BundleUpdateService bundleUpdateService;

    @Inject
    @OsgiService
    JSONConfigService jsonConfigService;

    @Inject
    @OsgiService
    ApplicationRegistrationService applicationRegistrationService;

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
        /** Update translations */
        bundleUpdateService.processBundle(bundleContext.getBundle(), "custom-qlack-lexicon.yaml");

        /** Update AAA */
        jsonConfigService.processBundle(bundleContext.getBundle());

        /** Register with WD **/
        applicationRegistrationService.registerApplication(
                bundleContext.getBundle(), YAML);
    }
}
