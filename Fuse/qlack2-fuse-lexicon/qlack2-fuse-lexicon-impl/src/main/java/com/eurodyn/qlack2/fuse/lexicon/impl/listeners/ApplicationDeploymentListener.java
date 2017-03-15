package com.eurodyn.qlack2.fuse.lexicon.impl.listeners;

import com.eurodyn.qlack2.fuse.lexicon.api.BundleUpdateService;
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
    private final String DEFAULT_LEXICON_YAML = "qlack-lexicon.yaml";

    @Inject
    private BundleUpdateService bundleUpdateService;

    @Override
    public void handleEvent(Event event) {
        Bundle bundle = (Bundle) event.getProperty("bundle");
        Enumeration<URL> entries = bundle
                .findEntries("OSGI-INF", DEFAULT_LEXICON_YAML, false);
        if ((entries != null) && (entries.hasMoreElements())) {
            bundleUpdateService.processBundle(bundle, DEFAULT_LEXICON_YAML);
        }
    }
}
