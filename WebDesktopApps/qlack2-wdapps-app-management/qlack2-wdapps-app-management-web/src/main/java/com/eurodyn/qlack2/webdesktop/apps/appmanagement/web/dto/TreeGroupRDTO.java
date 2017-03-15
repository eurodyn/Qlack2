package com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.dto;

import java.util.List;

public class TreeGroupRDTO {
	private String key;
	private String translationsGroup;
	private List<TreeApplicationRDTO> applications;


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getTranslationsGroup() {
		return translationsGroup;
	}


	public void setTranslationsGroup(String translationsGroup) {
		this.translationsGroup = translationsGroup;
	}


	public List<TreeApplicationRDTO> getApplications() {
		return applications;
	}


	public void setApplications(List<TreeApplicationRDTO> applications) {
		this.applications = applications;
	}
}
