package com.eurodyn.qlack2.be.rules.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteWorkingSetVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanGetWorkingSetVersionModelsJarResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanUpdateEnabledForTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.DeleteWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.EnableTestingWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.ExportWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.FinalizeWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionDataModelsJarRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.LockWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.UnlockWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.UpdateWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetVersionRestDTO;
import com.eurodyn.qlack2.be.rules.web.util.RestConverterUtil;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

@Path("/working-set-versions")
public class WorkingSetVersionsRest {

	private WorkingSetsService workingSetsService;

	private RestConverterUtil mapper = RestConverterUtil.INSTANCE;

	@Context
	private HttpHeaders headers;

	public void setWorkingSetsService(WorkingSetsService workingSetsService) {
		this.workingSetsService = workingSetsService;
	}

	@GET
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public WorkingSetVersionDTO getWorkingSetVersion(@PathParam("versionId") String versionId) {

		GetWorkingSetVersionRequest request = new GetWorkingSetVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return workingSetsService.getWorkingSetVersion(request);
	}

	@GET
	@Path("{versionId}/models.jar/canGet")
	@Produces(MediaType.APPLICATION_JSON)
	public CanGetWorkingSetVersionModelsJarResult canGetWorkingSetVersionDataModelsJar(@PathParam("versionId") String versionId) {

		GetWorkingSetVersionDataModelsJarRequest request = new GetWorkingSetVersionDataModelsJarRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return workingSetsService.canGetWorkingSetVersionDataModelsJar(request);
	}

	@GET
	@Path("{versionId}/models.jar")
	@Produces("application/java-archive")
	public Response getWorkingSetVersionDataModelsJar(@PathParam("versionId") String versionId, @QueryParam("ticket") String ticket) {

		GetWorkingSetVersionDataModelsJarRequest request = new GetWorkingSetVersionDataModelsJarRequest();
		request.setId(versionId);
		request.setSignedTicket(SignedTicket.fromVal(ticket));

		byte[] jar = workingSetsService.getWorkingSetVersionDataModelsJar(request);

		return Response.ok(jar)
				.header("Content-Disposition", "attachment; filename=models.jar")
				.build();
	}

	@GET
	@Path("{versionId}/canDelete")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDeleteWorkingSetVersionResult canDeleteWorkingSetVersion(@PathParam("versionId") String versionId) {

		DeleteWorkingSetVersionRequest request = new DeleteWorkingSetVersionRequest();
		request.setId(versionId);

		return workingSetsService.canDeleteWorkingSetVersion(Utils.sign(request, headers));
	}

	@DELETE
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteWorkingSetVersion(@PathParam("versionId") String versionId) {

		DeleteWorkingSetVersionRequest request = new DeleteWorkingSetVersionRequest();
		request.setId(versionId);

		workingSetsService.deleteWorkingSetVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/lock")
	@Produces(MediaType.APPLICATION_JSON)
	public void lockWorkingSetVersion(@PathParam("versionId") String versionId) {

		LockWorkingSetVersionRequest request = new LockWorkingSetVersionRequest();
		request.setId(versionId);

		workingSetsService.lockWorkingSetVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/unlock")
	@Produces(MediaType.APPLICATION_JSON)
	public void unlockWorkingSetVersion(@PathParam("versionId") String versionId) {

		UnlockWorkingSetVersionRequest request = new UnlockWorkingSetVersionRequest();
		request.setId(versionId);

		workingSetsService.unlockWorkingSetVersion(Utils.sign(request, headers));
	}

	@POST
	@Path("{versionId}/canUpdateEnabledForTesting")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// no validation
	public CanUpdateEnabledForTestingWorkingSetResult canUpdateEnabledForTestingWorkingSetVersion(
			@PathParam("versionId") String versionId, WorkingSetVersionRestDTO workingSetVersionRestDto) {

		UpdateWorkingSetVersionRequest request = mapper.mapUpdateWorkingSetVersion(workingSetVersionRestDto);
		request.setId(versionId);
		Utils.sign(request, headers);

		return workingSetsService.canUpdateEnabledForTestingWorkingSetVersion(request);
	}

	@GET
	@Path("{versionId}/canEnableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public CanEnableTestingWorkingSetResult canEnableTestingWorkingSetVersion(@PathParam("versionId") String versionId) {

		EnableTestingWorkingSetVersionRequest request = new EnableTestingWorkingSetVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return workingSetsService.canEnableTestingWorkingSetVersion(request);
	}

	@GET
	@Path("{versionId}/canDisableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDisableTestingWorkingSetResult canDisableTestingWorkingSetVersion(@PathParam("versionId") String versionId) {

		EnableTestingWorkingSetVersionRequest request = new EnableTestingWorkingSetVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return workingSetsService.canDisableTestingWorkingSetVersion(request);
	}

	@GET
	@Path("{versionId}/canFinalize")
	@Produces(MediaType.APPLICATION_JSON)
	public CanFinalizeWorkingSetResult canFinalizeWorkingSetVersion(@PathParam("versionId") String versionId) {

		FinalizeWorkingSetVersionRequest request = new FinalizeWorkingSetVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return workingSetsService.canFinalizeWorkingSetVersion(request);
	}

	@PUT
	@Path("{versionId}/enableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void enableTestingWorkingSetVersion(@PathParam("versionId") String versionId) {

		EnableTestingWorkingSetVersionRequest request = new EnableTestingWorkingSetVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(true);

		workingSetsService.enableTestingWorkingSetVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/disableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void disableTestingWorkingSetVersion(@PathParam("versionId") String versionId) {

		EnableTestingWorkingSetVersionRequest request = new EnableTestingWorkingSetVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(false);

		workingSetsService.enableTestingWorkingSetVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/finalize")
	@Produces(MediaType.APPLICATION_JSON)
	public void finalizeWorkingSetVersion(@PathParam("versionId") String versionId) {

		FinalizeWorkingSetVersionRequest request = new FinalizeWorkingSetVersionRequest();
		request.setId(versionId);

		workingSetsService.finalizeWorkingSetVersion(Utils.sign(request, headers));
	}

	@GET
	@Path("{versionId}/export")
	@Produces(MediaType.APPLICATION_XML)
	public Response exportWorkingSetVersion(@PathParam("versionId") String versionId, @QueryParam("ticket") String ticket) {

		ExportWorkingSetVersionRequest request = new ExportWorkingSetVersionRequest();
		request.setId(versionId);
		request.setSignedTicket(SignedTicket.fromVal(ticket));

		byte[] xml = workingSetsService.exportWorkingSetVersion(request);

		return Response.ok(xml)
				.header("Content-Disposition", "attachment; filename=working-set-version.xml")
				.build();
	}

}
