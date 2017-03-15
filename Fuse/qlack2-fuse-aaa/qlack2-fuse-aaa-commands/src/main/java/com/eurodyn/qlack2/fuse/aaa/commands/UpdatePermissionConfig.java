/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License").
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package com.eurodyn.qlack2.fuse.aaa.commands;

import java.net.URL;
import java.util.Enumeration;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.eurodyn.qlack2.fuse.aaa.api.JSONConfigService;

@Command(scope = "qlack", name = "aaa-update-permission-config", description = "Checks bundles for permissions configurations and parses them.")
@Service
public final class UpdatePermissionConfig implements Action {
	@Reference
	private JSONConfigService jsonConfigService;

	@Reference
	private BundleContext bundleContext;
	
	@Override
	public Object execute() {
		Bundle[] existingBundles = bundleContext.getBundles();
		for (Bundle bundle : existingBundles) {
			if (bundle.getState() == Bundle.ACTIVE) {
				// Check if this bundles contains a configuration file.
				Enumeration<URL> entries = bundle
						.findEntries("OSGI-INF", "qlack-aaa-config.json", false);

				// If no configuration file found, return doing nothing.
				if ((entries == null) || (!entries.hasMoreElements())) {
					continue;
				}

				// Get the configuration file.
				URL url = entries.nextElement();

				// Process the configuration file.
				jsonConfigService.parseConfig(bundle.getSymbolicName(), url);
			}
		}
		return null;
	}

}
