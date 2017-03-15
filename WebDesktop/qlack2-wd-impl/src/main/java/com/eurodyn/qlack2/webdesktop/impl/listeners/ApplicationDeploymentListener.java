package com.eurodyn.qlack2.webdesktop.impl.listeners;

import com.eurodyn.qlack2.webdesktop.api.ApplicationRegistrationService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;
import org.ops4j.pax.cdi.api.Properties;
import org.ops4j.pax.cdi.api.Property;
import org.osgi.framework.Bundle;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

@Transactional
@OsgiServiceProvider(classes = {EventHandler.class})
@Properties({
        @Property(name = "event.topics", value = "org/osgi/framework/BundleEvent/STARTED")
})
@Singleton
public class ApplicationDeploymentListener implements EventHandler {
    private Logger LOGGER = Logger.getLogger(ApplicationDeploymentListener.class.getName());

    @Inject
    private ApplicationRegistrationService applicationRegistrationService;

    @Override
    public void handleEvent(Event event) {
        Bundle bundle = (Bundle) event.getProperty("bundle");
        Enumeration<URL> entries = bundle
                .findEntries("OSGI-INF", "wd-app.yaml", false);
        if ((entries != null) && (entries.hasMoreElements())) {
            applicationRegistrationService.registerApplication(bundle, "OSGI-INF/wd-app.yaml");
        }
    }
}
