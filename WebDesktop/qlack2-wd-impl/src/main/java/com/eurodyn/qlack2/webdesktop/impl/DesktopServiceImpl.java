package com.eurodyn.qlack2.webdesktop.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.ops4j.pax.cdi.api.OsgiService;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import com.eurodyn.qlack2.fuse.aaa.api.OperationService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicketHolder;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopService;
import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.AddAppToUserDesktopRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.AtmosphereSubscriptionsManagementRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.GetAppAccessRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.GetAppDetailsRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.GetAppsForGroupRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.GetDesktopIconsForUserRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.RemoveAppFromUserDesktopRequest;
import com.eurodyn.qlack2.webdesktop.api.request.desktop.UpdateApplicationRequest;
import com.eurodyn.qlack2.webdesktop.api.util.Constants;
import com.eurodyn.qlack2.webdesktop.impl.model.Application;
import com.eurodyn.qlack2.webdesktop.impl.model.DesktopIcon;
import com.eurodyn.qlack2.webdesktop.impl.util.ConverterUtil;

@Singleton
@OsgiServiceProvider(classes = {DesktopService.class})
@Transactional
public class DesktopServiceImpl extends BaseService implements DesktopService {
	private static final Logger LOGGER = Logger.getLogger(DesktopServiceImpl.class.getName());
	@PersistenceContext(unitName = "webdesktop")
	private EntityManager em;
	@OsgiService @Inject
	private OperationService operationService;
	@OsgiService @Inject
	private IDMService idmService;

	// The only two application groups are generic applications and system
	// appications.
	private final static String APP_GROUP_GENERIC = "Applications";
	private final static String APP_GROUP_SYSTEM = "System";
	
	@Deprecated
	private boolean canAccessApp(SignedTicket ticket, String appId) {
		Boolean permission = operationService.isPermitted(ticket.getUserID(), Constants.OP_ACCESS_APPLICATION, appId);
		return (permission != null) && permission;
	}
	
	private boolean canAccessApp(String userID, String appId) {
		Boolean permission = operationService.isPermitted(userID, Constants.OP_ACCESS_APPLICATION, appId);
		return (permission != null) && permission;
	}

	private boolean canUpdateApp(SignedTicket ticket) {
		Boolean permission = operationService.isPermitted(ticket.getUserID(), Constants.OP_UPDATE_APPLICATION);
		return (permission != null) && permission;
	}

	@Override
	public List<ApplicationInfo> getAllApps(boolean activeOnly) {
		List<Application> applications = Application.getAllApps(activeOnly, em);

		List<ApplicationInfo> appInfo = new ArrayList<>(applications.size());
		for (Application application : applications) {
			// Check that the user is allowed access to the application
			// before returning it if the application has restricted access.
			if ((!application.isRestrictAccess()) || canAccessApp(getUserID(), application.getAppUuid())) {
				appInfo.add(ConverterUtil.applicationToApplicationInfo(application));
			} else {
				LOGGER.log(
						Level.FINEST,
						"Application with UUID {0} is not returned for user with ticket {1} since the user "
								+ "is not permitted access",
						new String[] { application.getAppUuid(), getTicket().getTicketID() });
			}
		}

		return appInfo;
	}

	@Override
	public ApplicationInfo getAppDetails(GetAppDetailsRequest sreq) {
		Application application = em.find(Application.class, sreq.getAppID());
		if ((!application.isRestrictAccess()) || canAccessApp(sreq.getSignedTicket(), application.getAppUuid())) {
			return ConverterUtil.applicationToApplicationInfo(application);
		} else {
			throw new QAuthorisationException(
					sreq.getSignedTicket().getUserID(),
					sreq.getSignedTicket().toString(),
					Constants.OP_ACCESS_APPLICATION,
					application.getAppUuid());
		}
	}

	@Override
	public boolean getAppAccess(GetAppAccessRequest sreq) {
		Application application = em.find(Application.class, sreq.getAppID());
		if ((!application.isRestrictAccess()) || canAccessApp(sreq.getSignedTicket(), application.getAppUuid())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateApplication(UpdateApplicationRequest sreq) {
		if (canUpdateApp(sreq.getSignedTicket())) {
			Application existingApplicationEntry = em.find(
					Application.class, sreq.getAppInfo().getIdentification().getUniqueId());			
			Application application = ConverterUtil.applicationInfoToApplication(sreq.getAppInfo());
			application.setAddedOn(existingApplicationEntry.getAddedOn());
			application.setLastDeployedOn(existingApplicationEntry.getLastDeployedOn());
			application.setBundleSymbolicName(existingApplicationEntry.getBundleSymbolicName());
			
			em.merge(application);
		}
		else {
			throw new QAuthorisationException(
					sreq.getSignedTicket().getUserID(),
					sreq.getSignedTicket().getTicketID(),
					Constants.OP_UPDATE_APPLICATION,
					null);
		}
	}

	private void checkUserIsDesktopOwner(SignedTicket ticket, String desktopOwnerId) {
		// Users are only allowed to manage their own desktop icons
		if (!desktopOwnerId.equals(ticket.getUserID())) {
			throw new QAuthorisationException(
					"User with ticket "
							+ ticket.toString()
							+ " is not allowed to manage the desktop icons of user with ID "
							+ desktopOwnerId
							+ "; users are only allowed to manage their own desktop icons");
		}
	}

	@Override
	public List<ApplicationInfo> getDesktopIconsForUser(GetDesktopIconsForUserRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		String desktopOwnerId = sreq.getDesktopOwnerID();

		checkUserIsDesktopOwner(ticket, desktopOwnerId);

		List<DesktopIcon> icons = DesktopIcon.getDesktopIconsForUser(desktopOwnerId, em);
		List<ApplicationInfo> retVal = new ArrayList<>(icons.size());
		for (DesktopIcon icon : icons) {
			// Check that the user is still allowed access to the application
			// before returning it if the application has restricted access.
			Application application = icon.getApplication();
			if ((!application.isRestrictAccess()) || canAccessApp(ticket, application.getAppUuid())) {
				retVal.add(ConverterUtil.applicationToApplicationInfo(application));
			} else {
				LOGGER.log(
						Level.FINE,
						"Desktop application with UUID {0} is not returned for user with ticket {1} since the user "
								+ "is no longer permitted access",
						new String[] { application.getAppUuid(), ticket.toString() });
			}
		}

		return retVal;
	}

	@Override
	public void addAppToUserDesktop(AddAppToUserDesktopRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		String desktopOwnerId = sreq.getDesktopOwnerID();

		checkUserIsDesktopOwner(ticket, desktopOwnerId);

		DesktopIcon existingIcon = DesktopIcon.getDesktopIconForUserAndApplication(desktopOwnerId, sreq.getAppID(), em);
		if (existingIcon == null) {
			Application application = em.find(Application.class, sreq.getAppID());
			DesktopIcon icon = new DesktopIcon();
			icon.setUserId(desktopOwnerId);
			icon.setApplication(application);
			em.persist(icon);
		} else {
			LOGGER.log(
					Level.FINE,
					"Application with UUID {0} could not be added to the "
							+ "desktop of user with ID {1}; the desktop icon already exists",
					new String[] { sreq.getAppID(), desktopOwnerId });
		}
	}

	@Override
	public void removeAppFromUserDesktop(RemoveAppFromUserDesktopRequest sreq) {
		SignedTicket ticket = sreq.getSignedTicket();
		String desktopOwnerId = sreq.getDesktopOwnerID();

		checkUserIsDesktopOwner(ticket, desktopOwnerId);

		DesktopIcon existingIcon = DesktopIcon.getDesktopIconForUserAndApplication(desktopOwnerId, sreq.getAppID(), em);
		if (existingIcon != null) {
			em.remove(existingIcon);
		} else {
			LOGGER.log(
					Level.FINE,
					"Application with UUID {0} could not be removed from the "
							+ "desktop of user with ID {1}; the desktop icon did not exist",
					new String[] { sreq.getAppID(), desktopOwnerId });
		}
	}

	@Override
	@ValidateTicketHolder
	public void manageAtmosphereSubscriptions(
			AtmosphereSubscriptionsManagementRequest sreq) {
		if (sreq.getRequestType() == AtmosphereSubscriptionsManagementRequest.TYPE.subscribe) {
//			atmosphereService.subscribe(sreq.getSignedTicket().getUserID(), sreq.getTopic());
		} else {
//			atmosphereService.unsubscribe(sreq.getSignedTicket().getUserID(), sreq.getTopic());
		}
	}

	@Override
	public List<String> getGroups(EmptySignedRequest sreq) {
		return Arrays.asList(APP_GROUP_GENERIC, APP_GROUP_SYSTEM);
	}

	@Override
	public List<ApplicationInfo> getAppsForGroup(GetAppsForGroupRequest sreq) {
		List<ApplicationInfo> allApps = getAllApps(sreq.getActive() != null ? sreq.getActive() : true);
		if (sreq.getGroupKeyname() != null) {
			if (sreq.getGroupKeyname().equals(APP_GROUP_SYSTEM)) {
				return allApps.stream().filter(p -> p.getMenu().isSystem()).collect(Collectors.toList());
			} else if (sreq.getGroupKeyname().equals(APP_GROUP_GENERIC)) {
				return allApps.stream().filter(p -> !p.getMenu().isSystem()).collect(Collectors.toList());
			} else {
				return new ArrayList<ApplicationInfo>();
			}
		} else {
			return allApps;
		}
	}

}
