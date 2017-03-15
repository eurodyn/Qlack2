package com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.ApplicationService;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.GetApplicationDetailsRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.GetGroupApplicationsRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.UpdateApplicationRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.dto.ApplicationRDTO;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.dto.TreeApplicationRDTO;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.dto.TreeGroupRDTO;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.util.ConverterUtil;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.util.Utils;

@Path("/applications")
public class ApplicationRest {

	@Context
	private HttpHeaders headers;
	private ApplicationService applicationService;

	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	private void generateTreeApplications(TreeGroupRDTO group, Collection<ApplicationInfo> applications) {
		for (ApplicationInfo info : applications) {
			TreeApplicationRDTO treeApplication = new TreeApplicationRDTO();
			treeApplication.setId(info.getIdentification().getUniqueId());
			treeApplication.setKey(info.getIdentification().getTitleKey());
			treeApplication.setTranslationsGroup(info.getInstantiation().getTranslationsGroup());
			treeApplication.setIcon("../../"
					+ info.getInstantiation().getPath()
					+ info.getMenu().getIconSmall());
			group.getApplications().add(treeApplication);
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TreeGroupRDTO> getApplicationsAsTree() {
		EmptySignedRequest sreq = new EmptySignedRequest();
		Utils.sign(sreq, headers);
		List<String> groupKeys = applicationService.getGroupKeys(sreq);
		List<TreeGroupRDTO> appGroups = new ArrayList<TreeGroupRDTO>(groupKeys.size());

		for (String groupKey : groupKeys) {
			TreeGroupRDTO group = new TreeGroupRDTO();
			group.setKey(groupKey);
			group.setApplications(new ArrayList<TreeApplicationRDTO>());
			GetGroupApplicationsRequest appsReq = new GetGroupApplicationsRequest(groupKey);
			Utils.sign(appsReq, headers);
			List<ApplicationInfo> applications = applicationService.getGroupApplications(appsReq);
			generateTreeApplications(group, applications);
			appGroups.add(group);
		}

		return appGroups;
	}

	@GET
	@Path("{appUUID}")
	@Produces(MediaType.APPLICATION_JSON)
	public ApplicationRDTO getApplicationDetails(@PathParam("appUUID") String appUUID) {
		GetApplicationDetailsRequest req = new GetApplicationDetailsRequest(appUUID);
		Utils.sign(req, headers);
		ApplicationInfo appInfo = applicationService.getApplicationDetails(req);
		return ConverterUtil.applicationInfoToApplicationRDTO(appInfo);
	}

	@PUT
	@Path("{appUUID}")
	@Produces(MediaType.APPLICATION_JSON)
	public void updateApplication(@PathParam("appUUID") String appUUID, ApplicationRDTO application) {
		GetApplicationDetailsRequest getReq = new GetApplicationDetailsRequest(appUUID);
		Utils.sign(getReq, headers);
		ApplicationInfo appInfo = applicationService.getApplicationDetails(getReq);

		appInfo.getInstantiation().setRestrictAccess(application.isRestricted());
		appInfo.setActive(application.isActive());
		UpdateApplicationRequest updateReq = new UpdateApplicationRequest(appInfo);
		Utils.sign(updateReq, headers);
		applicationService.updateApplication(updateReq);
	}

}
