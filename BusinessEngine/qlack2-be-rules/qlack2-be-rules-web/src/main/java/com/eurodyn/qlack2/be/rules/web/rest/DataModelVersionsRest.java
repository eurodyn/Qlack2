package com.eurodyn.qlack2.be.rules.web.rest;

import java.util.List;

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

import com.eurodyn.qlack2.be.rules.api.DataModelsService;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldTypeDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteDataModelVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanUpdateEnabledForTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.DeleteDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.EnableTestingDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.ExportDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.FinalizeDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.LockDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UnlockDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UpdateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelVersionRestDTO;
import com.eurodyn.qlack2.be.rules.web.util.RestConverterUtil;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

@Path("/data-model-versions")
public class DataModelVersionsRest {

	private DataModelsService modelsService;

	private RestConverterUtil mapper = RestConverterUtil.INSTANCE;

	@Context
	private HttpHeaders headers;

	public void setDataModelsService(DataModelsService modelsService) {
		this.modelsService = modelsService;
	}

	@GET
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public DataModelVersionDTO getDataModelVersion(@PathParam("versionId") String versionId) {

		GetDataModelVersionRequest request = new GetDataModelVersionRequest();
		request.setId(versionId);

		return modelsService.getDataModelVersion(Utils.sign(request, headers));
	}

	@GET
	@Path("{versionId}/canDelete")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDeleteDataModelVersionResult canDeleteDataModelVersion(@PathParam("versionId") String versionId) {

		DeleteDataModelVersionRequest request = new DeleteDataModelVersionRequest();
		request.setId(versionId);

		return modelsService.canDeleteDataModelVersion(Utils.sign(request, headers));
	}

	@DELETE
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteDataModelVersion(@PathParam("versionId") String versionId) {

		DeleteDataModelVersionRequest request = new DeleteDataModelVersionRequest();
		request.setId(versionId);

		modelsService.deleteDataModelVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/lock")
	@Produces(MediaType.APPLICATION_JSON)
	public void lockDataModelVersion(@PathParam("versionId") String versionId) {

		LockDataModelVersionRequest request = new LockDataModelVersionRequest();
		request.setId(versionId);

		modelsService.lockDataModelVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/unlock")
	@Produces(MediaType.APPLICATION_JSON)
	public void unlockDataModelVersion(@PathParam("versionId") String versionId) {

		UnlockDataModelVersionRequest request = new UnlockDataModelVersionRequest();
		request.setId(versionId);

		modelsService.unlockDataModelVersion(Utils.sign(request, headers));
	}

	@POST
	@Path("{versionId}/canUpdateEnabledForTesting")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// no validation
	public CanUpdateEnabledForTestingDataModelResult canUpdateEnabledForTestingDataModelVersion(
			@PathParam("versionId") String versionId, DataModelVersionRestDTO versionRestDto) {

		UpdateDataModelVersionRequest request = mapper.mapUpdateDataModelVersion(versionRestDto);
		request.setId(versionId);
		Utils.sign(request, headers);

		return modelsService.canUpdateEnabledForTestingDataModelVersion(request);
	}

	@GET
	@Path("{versionId}/canEnableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public CanEnableTestingDataModelResult canEnableTestingDataModelVersion(@PathParam("versionId") String versionId) {

		EnableTestingDataModelVersionRequest request = new EnableTestingDataModelVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return modelsService.canEnableTestingDataModelVersion(request);
	}

	@GET
	@Path("{versionId}/canDisableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDisableTestingDataModelResult canDisableTestingDataModelVersion(@PathParam("versionId") String versionId) {

		EnableTestingDataModelVersionRequest request = new EnableTestingDataModelVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return modelsService.canDisableTestingDataModelVersion(request);
	}

	@GET
	@Path("{versionId}/canFinalize")
	@Produces(MediaType.APPLICATION_JSON)
	public CanFinalizeDataModelResult canFinalizeDataModelVersion(@PathParam("versionId") String versionId) {

		FinalizeDataModelVersionRequest request = new FinalizeDataModelVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return modelsService.canFinalizeDataModelVersion(request);
	}

	@PUT
	@Path("{versionId}/enableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void enableTestingDataModelVersion(@PathParam("versionId") String versionId) {

		EnableTestingDataModelVersionRequest request = new EnableTestingDataModelVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(true);

		modelsService.enableTestingDataModelVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/disableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void disableTestingDataModelVersion(@PathParam("versionId") String versionId) {

		EnableTestingDataModelVersionRequest request = new EnableTestingDataModelVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(false);

		modelsService.enableTestingDataModelVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/finalize")
	@Produces(MediaType.APPLICATION_JSON)
	public void finalizeDataModelVersion(@PathParam("versionId") String versionId) {

		FinalizeDataModelVersionRequest request = new FinalizeDataModelVersionRequest();
		request.setId(versionId);

		modelsService.finalizeDataModelVersion(Utils.sign(request, headers));
	}

	@GET
	@Path("{versionId}/export")
	@Produces(MediaType.APPLICATION_XML)
	public Response exportDataModelVersion(@PathParam("versionId") String versionId, @QueryParam("ticket") String ticket) {

		ExportDataModelVersionRequest request = new ExportDataModelVersionRequest();
		request.setId(versionId);
		request.setSignedTicket(SignedTicket.fromVal(ticket));

		byte[] xml = modelsService.exportDataModelVersion(request);

		return Response.ok(xml)
				.header("Content-Disposition", "attachment; filename=data-model-version.xml")
				.build();
	}

	@GET
	@Path("field-types")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DataModelFieldTypeDTO> getDataModelFieldTypes() {

		return modelsService.getDataModelFieldTypes(Utils.sign(headers));
	}

}
