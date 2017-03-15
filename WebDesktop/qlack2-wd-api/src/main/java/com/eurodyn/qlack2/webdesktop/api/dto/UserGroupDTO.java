package com.eurodyn.qlack2.webdesktop.api.dto;

import java.util.List;

public class UserGroupDTO {
	private String id;
	private String name;
	private String description;
	private List<UserGroupDTO> childGroups;
	private UserGroupDTO parentGroup;
	private List<String> users;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<UserGroupDTO> getChildGroups() {
		return childGroups;
	}

	public void setChildGroups(List<UserGroupDTO> children) {
		this.childGroups = children;
	}

	public UserGroupDTO getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(UserGroupDTO parentGroup) {
		this.parentGroup = parentGroup;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
}
