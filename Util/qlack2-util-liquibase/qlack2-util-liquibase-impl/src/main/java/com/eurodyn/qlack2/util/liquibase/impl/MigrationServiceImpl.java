package com.eurodyn.qlack2.util.liquibase.impl;

import com.eurodyn.qlack2.util.liquibase.api.MigrationService;
import com.eurodyn.qlack2.util.liquibase.api.QChangeLogEntry;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.osgi.framework.Bundle;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.eurodyn.qlack2.util.liquibase.impl.MigrationExecutor.queue;

@Singleton
@OsgiServiceProvider(classes = {MigrationService.class})
public class MigrationServiceImpl implements MigrationService {
    @Inject
    BundleProcessor bundleProcessor;

    @Override
    public QChangeLogEntry[] list() {
        return queue.toArray(new QChangeLogEntry[queue.size()]);
    }

    @Override
    public void run() {
        MigrationExecutor.runMigrations();
    }

    @Override
    public void registerBundleForMigrations(Bundle bundle) {
        bundleProcessor.registerBundle(bundle);
    }

    @Override
    public void unregisterBundleFromMigrations(Bundle bundle) {
        bundleProcessor.unregisterBundle(bundle);
    }
}
