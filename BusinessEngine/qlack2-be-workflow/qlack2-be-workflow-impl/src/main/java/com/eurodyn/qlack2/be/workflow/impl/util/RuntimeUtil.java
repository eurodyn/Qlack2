package com.eurodyn.qlack2.be.workflow.impl.util;

import java.util.List;

import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowInstanceDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Workflow;
import com.eurodyn.qlack2.be.workflow.impl.model.WorkflowVersion;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.WorkflowRuntimeService;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.ProcessInstanceDesc;

/**
 *
 * Utility class to convert
 * 1.transfer object to entity
 * 2.entity to transfer object.
 *
 * @author European Dynamics SA
 */
public class RuntimeUtil {
	private ConverterUtil converterUtil;
	private WorkflowRuntimeService workflowRuntimeService;
	private AuditClientService auditClientService;
	
	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}
	
	public void setWorkflowRuntimeService(WorkflowRuntimeService workflowRuntimeService) {
		this.workflowRuntimeService = workflowRuntimeService;
	}
	
	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}
	
	public void deleteWorkflowInstancesForWorkflow (Workflow workflow, String userId)
	{		
		for (WorkflowVersion version : workflow.getWorkflowVersions())
		{
			if (version.getProcessId() != null)
			{
				List <ProcessInstanceDesc> processInstances = workflowRuntimeService.getProcessInstancesByProcessId(version.getProcessId());
				for (ProcessInstanceDesc instanceLog : processInstances)
				{
					if (!instanceLog.getStateDesc().equals("Completed") &&
							!instanceLog.getStateDesc().equals("Aborted"))
					{
						workflowRuntimeService.stopWorkflowInstance(instanceLog.getProcessInstanceId());
						
						AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
								.workflowVersionToAuditWorkflowInstanceDTO(version, instanceLog.getProcessInstanceId());
						auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.STOP.toString(), 
								GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
								null, userId,
								auditWorkflowInstanceDTO);
					}
					workflowRuntimeService.deleteWorkflowInstance(instanceLog.getProcessInstanceId());
					
					AuditWorkflowInstanceDTO auditWorkflowInstanceDTO = converterUtil
							.workflowVersionToAuditWorkflowInstanceDTO(version, instanceLog.getProcessInstanceId());
					auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.DELETE.toString(), 
							GROUP.WORKFLOW_VERSION_INSTANCE.toString(),
							null, userId,
							auditWorkflowInstanceDTO);
				}
			}
		}		
	}
}
