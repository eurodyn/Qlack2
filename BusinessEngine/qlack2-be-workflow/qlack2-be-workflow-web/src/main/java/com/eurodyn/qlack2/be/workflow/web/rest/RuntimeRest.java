package com.eurodyn.qlack2.be.workflow.web.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.eurodyn.qlack2.be.workflow.api.RuntimeService;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowInstanceDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowRuntimeErrorLogDTO;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowInstancesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.GetWorkflowRuntimeErrorLogRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowInstanceActionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.WorkflowInstanceRequest;
import com.eurodyn.qlack2.be.workflow.web.dto.RuntimeParametersRDTO;
import com.eurodyn.qlack2.be.workflow.web.util.Utils;


@Path("/runtime")
public class RuntimeRest {
	
	private static final Logger LOGGER = Logger.getLogger(RuntimeRest.class.getName());

	@Context
	private HttpHeaders headers;

	//private WorkflowService workflowService;
	//private WorkflowVersionService workflowVersionService;
	private RuntimeService runtimeService;
	
	/*public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	public void setWorkflowVersionService(WorkflowVersionService workflowVersionService) {
		this.workflowVersionService = workflowVersionService;
	} */
	
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	@GET
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Long startWorkflowInstance(@PathParam("versionId") String versionId){
		WorkflowInstanceRequest req = new WorkflowInstanceRequest();
		req.setId(versionId);
		Utils.sign(req, headers);
		return runtimeService.startWorkflowInstance(req);
	}
	
	@PUT
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Long startWorkflowInstance(@PathParam("versionId") String versionId, RuntimeParametersRDTO params){
		WorkflowInstanceRequest req = new WorkflowInstanceRequest();
		req.setId(versionId);
		req.setParameters(params.getParameters());
		Utils.sign(req, headers);
		return runtimeService.startWorkflowInstance(req);
	}
	
	@GET
	@Path("{projectId}/workflow-instances")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WorkflowInstanceDTO> getWorkflowInstances(@PathParam("projectId") String projectId){
		GetWorkflowInstancesRequest req = new GetWorkflowInstancesRequest();
		req.setProjectId(projectId);
		Utils.sign(req, headers);
		return runtimeService.getWorkflowInstances(req);
	}
	
	@PUT
	@Path("{versionId}/stop/{processInstanceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void stopWorkflowInstance(@PathParam("versionId") String versionId, @PathParam("processInstanceId") String processInstanceId){
		WorkflowInstanceActionRequest req = new WorkflowInstanceActionRequest();
		req.setId(versionId);
		req.setProcessInstanceId(Long.parseLong(processInstanceId));
		Utils.sign(req, headers);
		runtimeService.stopWorkflowInstance(req);
	}
	
	@PUT
	@Path("{versionId}/suspend/{processInstanceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void suspendWorkflowInstance(@PathParam("versionId") String versionId, @PathParam("processInstanceId") String processInstanceId){
		WorkflowInstanceActionRequest req = new WorkflowInstanceActionRequest();
		req.setId(versionId);
		req.setProcessInstanceId(Long.parseLong(processInstanceId));
		Utils.sign(req, headers);
		runtimeService.suspendWorkflowInstance(req);
	}
	
	@PUT
	@Path("{versionId}/resume/{processInstanceId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void resumeWorkflowInstance(@PathParam("versionId") String versionId, @PathParam("processInstanceId") String processInstanceId){
		WorkflowInstanceActionRequest req = new WorkflowInstanceActionRequest();
		req.setId(versionId);
		req.setProcessInstanceId(Long.parseLong(processInstanceId));
		Utils.sign(req, headers);
		runtimeService.resumeWorkflowInstance(req);
	}
	
	@DELETE
	@Path("{versionId}/{processInstanceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteWorkflowInstance(@PathParam("versionId") String versionId, @PathParam("processInstanceId") String processInstanceId){
		WorkflowInstanceActionRequest req = new WorkflowInstanceActionRequest();
		req.setId(versionId);
		req.setProcessInstanceId(Long.parseLong(processInstanceId));
		Utils.sign(req, headers);
		runtimeService.deleteWorkflowInstance(req);
	}
	
	@GET
	@Path("{projectId}/workflow-runtime-logs")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WorkflowRuntimeErrorLogDTO> getWorkflowErrorAuditLogs(@PathParam("projectId") String projectId){
		GetWorkflowRuntimeErrorLogRequest req = new GetWorkflowRuntimeErrorLogRequest();
		req.setProjectId(projectId);
		Utils.sign(req, headers);
		return runtimeService.getWorkflowErrorAuditLogs(req);
	}

}
