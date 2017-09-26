package com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl;

import com.eurodyn.qlack2.fuse.auditing.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopService;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.GetAppDetailsRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.GetAppsForGroupRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.ApplicationService;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.GetApplicationDetailsRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.GetGroupApplicationsRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.UpdateApplicationRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.util.Constants;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.impl.util.AuditConstants.LEVEL;
import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Transactional
@OsgiServiceProvider(classes = { ApplicationService.class })
public class ApplicationServiceImpl implements ApplicationService {
	private static final Logger LOGGER = Logger.getLogger(ApplicationServiceImpl.class.getName());

	@Inject
	@OsgiService
	private IDMService idmService;

	@Inject
	@OsgiService
	private DesktopService desktopService;

	@Inject
	@OsgiService
	private EventPublisherService eventPublisher;

	@Inject
	@OsgiService
	private AuditClientService audit;

	private void publishEvent(SignedTicket signedTicket, String event, String projectId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_APPLICATION_ID, projectId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/wd/apps/appmanagement/"
				+ Constants.RESOURCE_TYPE_APPLICATION + "/" + event);
	}

	@Override
	@ValidateTicket
	public List<String> getGroupKeys(EmptySignedRequest sreq) {
		// Authorization is taken care of by the WebDesktop.
		// This means that users can only manage applications they have access to in the Web Desktop
		com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest();
		wdReq.setSignedTicket(sreq.getSignedTicket());
		return desktopService.getGroups(wdReq);
	}

	@Override
	@ValidateTicket
	public List<ApplicationInfo> getGroupApplications(GetGroupApplicationsRequest sreq) {
		GetAppsForGroupRequest wdReq = new GetAppsForGroupRequest(sreq.getGroupKeyname(), null);
		wdReq.setSignedTicket(sreq.getSignedTicket());
		return desktopService.getAppsForGroup(wdReq);
	}

	@Override
	@ValidateTicket
	public ApplicationInfo getApplicationDetails(GetApplicationDetailsRequest sreq) {
		LOGGER.log(Level.FINE, "Retrieving details of application with UUID {0}", sreq.getAppUUID());

		// We pass the signed ticket to the Web Desktop to let it handle authorization
		GetAppDetailsRequest wdReq = new GetAppDetailsRequest(sreq.getAppUUID());
		wdReq.setSignedTicket(sreq.getSignedTicket());
		ApplicationInfo retVal = desktopService.getAppDetails(wdReq);

		audit.audit(LEVEL.WD_APPMANAGEMENT.toString(), EVENT.VIEW.toString(), GROUP.APPLICATION.toString(),
				null, sreq.getSignedTicket().getUserID(), retVal);

		return retVal;
	}

	@Override
	@ValidateTicket
	public void updateApplication(UpdateApplicationRequest sreq) {
		String appId = sreq.getApplicationInfo().getIdentification().getUniqueId();
		LOGGER.log(Level.FINE, "Updating details of application with UUID {0}", appId);

		// We pass the signed ticket to the Web Desktop to let it handle authorization
		com.eurodyn.qlack2.webdesktop.api.request.desktop.UpdateApplicationRequest wdReq =
				new com.eurodyn.qlack2.webdesktop.api.request.desktop.UpdateApplicationRequest(sreq.getApplicationInfo());
		wdReq.setSignedTicket(sreq.getSignedTicket());
		desktopService.updateApplication(wdReq);

		publishEvent(sreq.getSignedTicket(), Constants.EVENT_UPDATE, appId);

		audit.audit(LEVEL.WD_APPMANAGEMENT.toString(), EVENT.UPDATE.toString(), GROUP.APPLICATION.toString(),
				null, sreq.getSignedTicket().getUserID(), sreq.getApplicationInfo());
	}

}
