package com.eurodyn.qlack2.webdesktop.apps.appmanagement.web.dto;


public class TreeApplicationRDTO {
	private String id;
	private String key;
	private String translationsGroup;
	private String icon;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getIcon() {
		return icon;
	}


	public void setIcon(String icon) {
		this.icon = icon;
	}


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
}
