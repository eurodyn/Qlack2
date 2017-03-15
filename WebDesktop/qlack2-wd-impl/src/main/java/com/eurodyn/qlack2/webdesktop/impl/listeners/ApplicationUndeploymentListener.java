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
import java.util.logging.Logger;

@Transactional
@OsgiServiceProvider(classes = {EventHandler.class})
@Properties({
	@Property(name = "event.topics", value="org/osgi/framework/BundleEvent/STOPPED")
})
@Singleton
public class ApplicationUndeploymentListener implements EventHandler {
	private static final Logger LOGGER = Logger.getLogger(ApplicationUndeploymentListener.class.getName());

	@Inject
	private ApplicationRegistrationService applicationRegistrationService;

	@Override
	public void handleEvent(Event event) {
		applicationRegistrationService.unregisterApplication(
				((Bundle)event.getProperty("bundle")).getSymbolicName());
	}

}
