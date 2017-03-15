package com.eurodyn.qlack2.webdesktop.apps.appmanagement.api;

import java.util.List;

import com.eurodyn.qlack2.webdesktop.api.dto.ApplicationInfo;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.GetApplicationDetailsRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.GetGroupApplicationsRequest;
import com.eurodyn.qlack2.webdesktop.apps.appmanagement.api.request.UpdateApplicationRequest;

public interface ApplicationService {
	List<String> getGroupKeys(EmptySignedRequest sreq);
	
	List<ApplicationInfo> getGroupApplications(GetGroupApplicationsRequest sreq);
	
	ApplicationInfo getApplicationDetails(GetApplicationDetailsRequest sreq);
	
	void updateApplication(UpdateApplicationRequest sreq);
}
