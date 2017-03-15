package com.eurodyn.qlack2.util.liquibase.impl;

import com.eurodyn.qlack2.util.liquibase.api.MigrationDiscoveryMode;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;
import org.osgi.framework.Bundle;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@OsgiServiceProvider(classes = {EventHandler.class})
@Properties(
        @Property(name = "event.topics", value = "org/osgi/framework/BundleEvent/STOPPED")
)
public class QChangeLogRemoveListener implements EventHandler {
    /**
     * JUL reference.
     */
    private final static Logger LOGGER = Logger.getLogger(QChangeLogRemoveListener.class.getName());

    @Inject
    private MigrationExecutor migrationExecutor;

    @Inject
    BundleProcessor bundleProcessor;

    @Inject
    Settings settings;

    public void handleEvent(Event event) {
        if (settings.getDiscoveryMode().equals(MigrationDiscoveryMode.AUTO)) {
            /** Check if the bundle has headers for liquibase update */
            Bundle bundle = (Bundle) event.getProperty("bundle");
            final String liquibaseChangeLogHeader = bundle.getHeaders().get(Settings.HEADER_Q_LIQUIBASE_CHANGELOG);

            if (liquibaseChangeLogHeader != null) {
                LOGGER.log(Level.FINE, "Removing bundle with Liquibase changelogs: {0}.", bundle.getSymbolicName());
                bundleProcessor.unregisterBundle(bundle);
            } else {
                LOGGER.log(Level.FINEST, "Bundle {0} ignored for dequeuing since it does not contain a {1} header.",
                        new Object[]{bundle.getSymbolicName(), Settings.HEADER_Q_LIQUIBASE_CHANGELOG});
            }
        }
    }
}
