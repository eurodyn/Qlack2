package com.eurodyn.qlack2.util.atmosphere.impl.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;

import com.eurodyn.qlack2.util.atmosphere.api.AtmosphereSecurityService;
import com.eurodyn.qlack2.util.atmosphere.impl.util.AtmosphereUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;

public class AtmosphereHandler extends AbstractReflectorAtmosphereHandler {
	private static final Logger LOGGER = Logger
			.getLogger(AtmosphereHandler.class.getName());
	private final ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
	private AtmosphereSecurityService securityService;
	// Keep a reference to the AtmosphereResources created for each user. The
	// reason a list of UUIDs is kept (instead of a single one) is to support
	// the case of the same user logging in from different browser windows.
	private Map<String, List<String>> userIDToUUID = new ConcurrentHashMap<>();

	public void setSecurityService(AtmosphereSecurityService securityService) {
		this.securityService = securityService;
	}

	private void addAtmosphereUUIDForUser(String userID, String atmosphereUUID) {
		List<String> uuids = userIDToUUID.get(userID);
		if (uuids == null) {
			uuids = new ArrayList<>();
		}
		if (!uuids.contains(atmosphereUUID)) {
			LOGGER.log(Level.FINEST,
					"Associated Atmosphere UUID {0} with userID {1}.",
					new Object[] { atmosphereUUID, userID });
			uuids.add(atmosphereUUID);
			userIDToUUID.put(userID, uuids);
		} else {
			LOGGER.log(Level.FINEST,
					"Atmosphere UUID {0} is already associated with userID {1}. "
					+ "If this is a reconnect or you are using long-polling it is ok.",
					new Object[] { atmosphereUUID, userID });
		}
	}

	public List<String> getUUIDForUserID(String userID) {
		return userIDToUUID.get(userID);
	}

	public List<String> removeUUIDForUserID(String userID, String uuid) {
		List<String> uuidForUserID = getUUIDForUserID(userID);
		uuidForUserID.remove(uuid);

		return uuidForUserID;
	}

	@Override
	public void onRequest(AtmosphereResource atmosphereResource)
			throws IOException {
		AtmosphereRequest atmosphereRequest = atmosphereResource.getRequest();
		if (atmosphereRequest.getMethod().equalsIgnoreCase("GET")) {
			atmosphereResource.suspend();

			String userID = securityService.getUserID(AtmosphereUtil
					.extractHeaders(atmosphereResource));
			if (userID != null) {
				addAtmosphereUUIDForUser(userID, atmosphereResource.uuid());
			} else {
				LOGGER.log(Level.WARNING,
						"Could not find a userID for AtmosphereResource {0}.",
						atmosphereResource.uuid());
			}
		}
	}

	@Override
	public void onStateChange(AtmosphereResourceEvent event) throws IOException {
		// If the Atmosphere Connection was closed by the user (i.e. exited the
		// application or closed the browser window) remove the mapping for this
		// user. Note that there is a chance this event is not properly
		// detected, however during this client's next subscription event 
		// stray resources will be manually detected and their mappings will be 
		// removed from the  map.
		if (event.isClosedByClient() || event.isClosedByApplication()) {
			LOGGER.log(Level.FINEST,
					"Detected browser-close event for AtmosphereResource {0}."
							+ " Clearing up mappings.", event.getResource()
							.uuid());
			String userID = securityService.getUserID(AtmosphereUtil
					.extractHeaders(event.getResource()));
			if (userID != null) {
				removeUUIDForUserID(userID, event.getResource().uuid());
			} else {
				LOGGER.log(
						Level.WARNING,
						"Tried to remove user mapping for "
								+ "AtmosphereResource {0}, however the userID could not be found.",
						event.getResource().uuid());
			}
		}
		super.onStateChange(event);
	}

	private String debugEvent(AtmosphereResourceEvent event) {
		StringBuffer retVal = new StringBuffer();
		retVal.append("isCancelled: " + event.isCancelled());
		retVal.append("\nisClosedByApplication: "
				+ event.isClosedByApplication());
		retVal.append("\nisClosedByClient: " + event.isClosedByClient());
		retVal.append("\nisResumedOnTimeout: " + event.isResumedOnTimeout());
		retVal.append("\nisResuming: " + event.isResuming());
		retVal.append("\nisSuspended: " + event.isSuspended());

		return retVal.toString();
	}

}