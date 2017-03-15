package com.eurodyn.qlack2.fuse.workflow.runtime.api;

import java.util.List;
import java.util.Map;














//import org.jbpm.process.audit.ProcessInstanceLog;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.ProcessInstanceDesc;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.TaskSummary;

public interface WorkflowRuntimeService {
	
	Long startWorkflowInstance(String processId, String content, Map<String, Object> parameters);
	
	//List<ProcessInstanceLog> getProcessInstancesByProcessId(String processId);
	
	List<ProcessInstanceDesc> getProcessInstancesByProcessId(String processId);
	
	void stopWorkflowInstance(Long processInstanceId);
	
	void suspendWorkflowInstance(Long processInstanceId);
	
	void resumeWorkflowInstance(Long processInstanceId);
	
	void deleteWorkflowInstance(Long processInstanceId);
	
	void signalProcessInstance(Long processInstanceId, String signalName, Object event);
	
	TaskSummary getTaskDetails(Long processInstanceId, Long taskId);
	
	List<TaskSummary> getTasksAssignedAsPotentialOwner(Long processInstanceId, String userId, List<String> statusList);
	
	void acceptTask(Long processInstanceId, Long taskId, String userId);
	
	void startTask(Long processInstanceId, Long taskId, String userId);
	
	void completeTask(Long processInstanceId, Long taskId, String userId, Map<String, Object> data);
	
	List<Long> getTasksByProcessInstanceId(Long processInstanceId);
	
	Object getVariableInstance(Long processInstanceId, String variableName);

	List<TaskSummary> getAllTasksAssignedAsPotentialOwner(String userId, List<String> statusList);

	ProcessInstanceDesc getProcessInstanceDetails(Long processInstanceId);

	void setVariableInstance(Long processInstanceId, String variableName, Object data);
}
