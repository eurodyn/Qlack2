package com.eurodyn.qlack2.be.workflow.web.rest;

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.eurodyn.qlack2.be.workflow.api.WorkflowVersionService;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.CountWorkflowVersionsLockedByOtherUserRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.DeleteWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.ExportWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.FinaliseWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.GetWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.LockWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.UnlockWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.EnableTestingVersionRequest;
import com.eurodyn.qlack2.be.workflow.web.util.Utils;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

@Path("/workflow-versions")
public class WorkflowVersionRest {

	private static final Logger LOGGER = Logger.getLogger(WorkflowVersionRest.class.getName());

	@Context
	private HttpHeaders headers;

	private WorkflowVersionService workflowVersionService;

	public void setWorkflowVersionService(WorkflowVersionService workflowVersionService) {
		this.workflowVersionService = workflowVersionService;
	}

	@GET
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public WorkflowVersionDTO getWorkflowVersion(@PathParam("versionId") String versionId){
		GetWorkflowVersionRequest req = new GetWorkflowVersionRequest();
		req.setVersionId(versionId);
		Utils.sign(req, headers);
		return workflowVersionService.getWorkflowVersion(req);
	}

	@DELETE
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteWorkflowVersion(@PathParam("versionId") String versionId){
		DeleteWorkflowVersionRequest req = new DeleteWorkflowVersionRequest();
		req.setId(versionId);
		Utils.sign(req, headers);
		workflowVersionService.deleteWorkflowVersion(req);
	}

	@PUT
	@Path("{versionId}/lock")
	@Produces(MediaType.APPLICATION_JSON)
	public void lockWorkflowVersion(@PathParam("versionId") String versionId){
		LockWorkflowVersionRequest req = new LockWorkflowVersionRequest();
		req.setId(versionId);
		Utils.sign(req, headers);
		workflowVersionService.lockWorkflowVersion(req);
	}

	@PUT
	@Path("{versionId}/unlock")
	@Produces(MediaType.APPLICATION_JSON)
	public void unlockWorkflowVersion(@PathParam("versionId") String versionId){
		UnlockWorkflowVersionRequest req = new UnlockWorkflowVersionRequest();
		req.setId(versionId);
		Utils.sign(req, headers);
		workflowVersionService.unlockWorkflowVersion(req);
	}

	@PUT
	@Path("{versionId}/finalise")
	@Produces(MediaType.APPLICATION_JSON)
	public void finaliseWorkflowVersion(@PathParam("versionId") String versionId){
		FinaliseWorkflowVersionRequest req = new FinaliseWorkflowVersionRequest();
		req.setId(versionId);
		Utils.sign(req, headers);
		workflowVersionService.finaliseWorkflowVersion(req);
	}
	
	@PUT
    @Path("{versionId}/enableTesting")
    @Produces(MediaType.APPLICATION_JSON)
    public void enableTestingWorkflowVersion(@PathParam("versionId") String versionId) {
		EnableTestingVersionRequest req = new EnableTestingVersionRequest();
        req.setId(versionId);
        req.setEnableTesting(true);

        Utils.sign(req, headers);
        workflowVersionService.enableTestingWorkflowVersion(req);
    }

    @PUT
    @Path("{versionId}/disableTesting")
    @Produces(MediaType.APPLICATION_JSON)
    public void disableTestingForFormVersion(@PathParam("versionId") String versionId) {
    	EnableTestingVersionRequest req = new EnableTestingVersionRequest();
        req.setId(versionId);
        req.setEnableTesting(false);

        Utils.sign(req, headers);
        workflowVersionService.enableTestingWorkflowVersion(req);
    }
        
    @GET
    @Path("{versionId}/export")
    public Response exportWorkflowVersion(@PathParam("versionId") String versionId, @QueryParam("ticket") String ticket) {
    	ExportWorkflowVersionRequest req = new ExportWorkflowVersionRequest();
    	req.setVersionId(versionId);
    	req.setSignedTicket(SignedTicket.fromVal(ticket));

    	byte[] content = workflowVersionService.exportWorkflowVersion(req);

    	ResponseBuilder rb = Response.ok(content)
    								.type("application/xml")
    								.header("Content-disposition", "attachment; filename=workflow.version.xml");
    	return rb.build();
    }
    
    @GET
	@Path("{versionId}/can-finalise")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean checkWorkflowVersionCanFinalise(@PathParam("versionId") String versionId){
    	FinaliseWorkflowVersionRequest req = new FinaliseWorkflowVersionRequest();
		req.setId(versionId);
		Utils.sign(req, headers);
		return workflowVersionService.checkWorkflowVersionCanFinalise(req);
	}

	@GET
	@Path("/condition-types")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getConditionTypes() {
		EmptySignedRequest req = new EmptySignedRequest();

		Utils.sign(req, headers);
		return workflowVersionService.getConditionTypes(req);
	}

}
