package com.eurodyn.qlack2.webdesktop.api;

import org.osgi.framework.Bundle;

public interface ApplicationRegistrationService {
    void registerApplication(Bundle bundle, String yamlLocation);
    void unregisterApplication(String bundleSymbolicName);
}
