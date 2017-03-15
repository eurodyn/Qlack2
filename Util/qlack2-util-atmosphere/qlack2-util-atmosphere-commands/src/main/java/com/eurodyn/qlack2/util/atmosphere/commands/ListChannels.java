package com.eurodyn.qlack2.util.atmosphere.commands;

import java.util.Collection;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;

import com.eurodyn.qlack2.util.atmosphere.api.AtmosphereService;

@Command(scope = "qlack-util-atmosphere", name = "list-channels", description = "Lists available channels.")
@Service
public class ListChannels implements Action {
	@Reference
	private AtmosphereService qlackAtmosphereService;

	@Override
	public Object execute() {
		AtmosphereFramework f = qlackAtmosphereService.getFramework();
		Collection<Broadcaster> lookupAll = f.getBroadcasterFactory()
				.lookupAll();
		for (Broadcaster b : lookupAll) {
			Collection<AtmosphereResource> atmosphereResources = b
					.getAtmosphereResources();
			System.out.println(b.getID() + " (" + atmosphereResources.size()
					+ " subscribers)");
			for (AtmosphereResource r : atmosphereResources) {
				System.out.println("\t " + r.uuid());
			}
		}

		return null;
	}
}
