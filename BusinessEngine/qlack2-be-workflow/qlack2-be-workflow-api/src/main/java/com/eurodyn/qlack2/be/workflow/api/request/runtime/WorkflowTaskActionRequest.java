package com.eurodyn.qlack2.be.workflow.api.request.runtime;

import java.util.List;
import java.util.Map;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

public class WorkflowTaskActionRequest extends QSignedRequest {

	private String id;
	private String projectId;
	private Long processInstanceId;
	private Long taskId;
	private String userId;
	Map<String, Object> data;
	List<String> statusList;

	public WorkflowTaskActionRequest() {
		super();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String id) {
		this.userId = id;
	}
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public List<String> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

}
