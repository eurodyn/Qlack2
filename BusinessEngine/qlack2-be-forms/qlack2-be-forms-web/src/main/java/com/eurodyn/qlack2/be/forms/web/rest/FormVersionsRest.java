package com.eurodyn.qlack2.be.forms.web.rest;

import com.eurodyn.qlack2.be.forms.api.FormVersionsService;
import com.eurodyn.qlack2.be.forms.api.dto.FormVersionDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.*;
import com.eurodyn.qlack2.be.forms.web.util.Utils;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import java.util.List;
import java.util.logging.Logger;

@Path("/form-versions")
public class FormVersionsRest {
	private static final Logger LOGGER = Logger
			.getLogger(FormVersionsRest.class.getName());

	@Context
	private HttpHeaders headers;

	private FormVersionsService formVersionsService;

	/**
	 * Retrieves the metadata of a form version
	 *
	 * @param formVersionId
	 * @return
	 */
	@GET
	@Path("{formVersionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public FormVersionDetailsDTO getFormVersion(
			@PathParam("formVersionId") String formVersionId) {
		GetFormVersionRequest req = new GetFormVersionRequest();
		req.setFormVersionId(formVersionId);

		Utils.sign(req, headers);
		return formVersionsService.getFormVersion(req);
	}

	/**
	 * Deletes a form version.
	 *
	 * @param formVersionId
	 */
	@DELETE
	@Path("{formVersionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteFormVersion(
			@PathParam("formVersionId") String formVersionId) {
		DeleteFormVersionRequest req = new DeleteFormVersionRequest();
		req.setFormVersionId(formVersionId);

		Utils.sign(req, headers);
		formVersionsService.deleteFormVersion(req);
	}

	/**
	 * Locks a form version.
	 *
	 * @param formVersionId
	 */
	@PUT
	@Path("{formVersionId}/lock")
	@Produces(MediaType.APPLICATION_JSON)
	public void lockFormVersion(@PathParam("formVersionId") String formVersionId) {
		LockFormVersionRequest req = new LockFormVersionRequest();
		req.setFormVersionId(formVersionId);

		Utils.sign(req, headers);
		formVersionsService.lockFormVersion(req);
	}

	/**
	 * Unlocks a form version.
	 *
	 * @param formVersionId
	 */
	@PUT
	@Path("{formVersionId}/unlock")
	@Produces(MediaType.APPLICATION_JSON)
	public void unlockFormVersion(
			@PathParam("formVersionId") String formVersionId) {
		UnlockFormVersionRequest req = new UnlockFormVersionRequest();
		req.setFormVersionId(formVersionId);

		Utils.sign(req, headers);
		formVersionsService.unlockFormVersion(req);
	}

	/**
	 * Retrieves the number of form versions locked by another user.
	 *
	 * @param formVersionId
	 * @return
	 */
	@GET
	@Path("{formVersionId}/canFinalise")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean canFinaliseFormVersion(
			@PathParam("formVersionId") String formVersionId) {
		CanFinaliseFormVersionRequest req = new CanFinaliseFormVersionRequest();
		req.setFormVersionId(formVersionId);

		Utils.sign(req, headers);

		return formVersionsService.canFinaliseFormVersion(req);
	}

	/**
	 * Finalises a form version.
	 *
	 * @param formVersionId
	 */
	@PUT
	@Path("{formVersionId}/finalise")
	@Produces(MediaType.APPLICATION_JSON)
	public void finaliseFormVersion(
			@PathParam("formVersionId") String formVersionId) {
		FinaliseFormVersionRequest req = new FinaliseFormVersionRequest();
		req.setFormVersionId(formVersionId);

		Utils.sign(req, headers);
		formVersionsService.finaliseFormVersion(req);
	}

	@PUT
	@Path("{formVersionId}/enableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void enableTestingForFormVersion(
			@PathParam("formVersionId") String formVersionId) {
		EnableTestingRequest req = new EnableTestingRequest();
		req.setFormVersionId(formVersionId);
		req.setEnableTesting(true);

		Utils.sign(req, headers);
		formVersionsService.enableTestingForFormVersion(req);
	}

	@PUT
	@Path("{formVersionId}/disableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void disableTestingForFormVersion(
			@PathParam("formVersionId") String formVersionId) {
		EnableTestingRequest req = new EnableTestingRequest();
		req.setFormVersionId(formVersionId);
		req.setEnableTesting(false);

		Utils.sign(req, headers);
		formVersionsService.enableTestingForFormVersion(req);
	}

	/**
	 * Exports a form version.
	 *
	 * @param formVersionId
	 * @return
	 */
	@GET
	@Path("{formVersionId}/export")
	public Response exportFormVersion(
			@PathParam("formVersionId") String formVersionId,
			@QueryParam("ticket") String ticket) {
		ExportFormVersionRequest req = new ExportFormVersionRequest();
		req.setFormVersionId(formVersionId);
		req.setSignedTicket(SignedTicket.fromVal(ticket));

		byte[] content = formVersionsService.exportFormVersion(req);

		ResponseBuilder rb = Response
				.ok(content)
				.type("application/xml")
				.header("Content-disposition",
						"attachment; filename=form.version.xml");
		return rb.build();
	}

	/**
	 * Retrieves the validation condition types.
	 *
	 * @return
	 */
	@GET
	@Path("/condition-types")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getConditionTypes() {
		EmptySignedRequest req = new EmptySignedRequest();

		Utils.sign(req, headers);
		return formVersionsService.getConditionTypes(req);
	}

	public void setFormVersionsService(FormVersionsService formVersionsService) {
		this.formVersionsService = formVersionsService;
	}
}
