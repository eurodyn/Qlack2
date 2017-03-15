package com.eurodyn.qlack2.be.rules.web.rest;

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

import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteRuleVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeRuleResult;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.DeleteRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.EnableTestingRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.ExportRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.FinalizeRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.LockRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.UnlockRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

@Path("/rule-versions")
public class RuleVersionsRest {

	private RulesService rulesService;

	@Context
	private HttpHeaders headers;

	public void setRulesService(RulesService rulesService) {
		this.rulesService = rulesService;
	}

	@GET
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RuleVersionDTO getRuleVersion(@PathParam("versionId") String versionId) {

		GetRuleVersionRequest request = new GetRuleVersionRequest();
		request.setId(versionId);

		return rulesService.getRuleVersion(Utils.sign(request, headers));
	}

	@GET
	@Path("{versionId}/canDelete")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDeleteRuleVersionResult canDeleteRuleVersion(@PathParam("versionId") String versionId) {

		DeleteRuleVersionRequest request = new DeleteRuleVersionRequest();
		request.setId(versionId);

		return rulesService.canDeleteRuleVersion(Utils.sign(request, headers));
	}

	@DELETE
	@Path("{versionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteRuleVersion(@PathParam("versionId") String versionId) {

		DeleteRuleVersionRequest request = new DeleteRuleVersionRequest();
		request.setId(versionId);

		rulesService.deleteRuleVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/lock")
	@Produces(MediaType.APPLICATION_JSON)
	public void lockRuleVersion(@PathParam("versionId") String versionId) {

		LockRuleVersionRequest request = new LockRuleVersionRequest();
		request.setId(versionId);

		rulesService.lockRuleVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/unlock")
	@Produces(MediaType.APPLICATION_JSON)
	public void unlockRuleVersion(@PathParam("versionId") String versionId) {

		UnlockRuleVersionRequest request = new UnlockRuleVersionRequest();
		request.setId(versionId);

		rulesService.unlockRuleVersion(Utils.sign(request, headers));
	}

	@GET
	@Path("{versionId}/canEnableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public CanEnableTestingRuleResult canEnableTestingRuleVersion(@PathParam("versionId") String versionId) {

		EnableTestingRuleVersionRequest request = new EnableTestingRuleVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return rulesService.canEnableTestingRuleVersion(request);
	}

	@GET
	@Path("{versionId}/canDisableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDisableTestingRuleResult canDisableTestingRuleVersion(@PathParam("versionId") String versionId) {

		EnableTestingRuleVersionRequest request = new EnableTestingRuleVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return rulesService.canDisableTestingRuleVersion(request);
	}

	@GET
	@Path("{versionId}/canFinalize")
	@Produces(MediaType.APPLICATION_JSON)
	public CanFinalizeRuleResult canFinalizeRuleVersion(@PathParam("versionId") String versionId) {

		FinalizeRuleVersionRequest request = new FinalizeRuleVersionRequest();
		request.setId(versionId);
		Utils.sign(request, headers);

		return rulesService.canFinalizeRuleVersion(request);
	}

	@PUT
	@Path("{versionId}/enableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void enableTestingRuleVersion(@PathParam("versionId") String versionId) {

		EnableTestingRuleVersionRequest request = new EnableTestingRuleVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(true);

		rulesService.enableTestingRuleVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/disableTesting")
	@Produces(MediaType.APPLICATION_JSON)
	public void disableTestingRuleVersion(@PathParam("versionId") String versionId) {

		EnableTestingRuleVersionRequest request = new EnableTestingRuleVersionRequest();
		request.setId(versionId);
		request.setEnableTesting(false);

		rulesService.enableTestingRuleVersion(Utils.sign(request, headers));
	}

	@PUT
	@Path("{versionId}/finalize")
	@Produces(MediaType.APPLICATION_JSON)
	public void finalizeRuleVersion(@PathParam("versionId") String versionId) {

		FinalizeRuleVersionRequest request = new FinalizeRuleVersionRequest();
		request.setId(versionId);

		rulesService.finalizeRuleVersion(Utils.sign(request, headers));
	}

	@GET
	@Path("{versionId}/export")
	@Produces(MediaType.APPLICATION_XML)
	public Response exportRuleVersion(@PathParam("versionId") String versionId, @QueryParam("ticket") String ticket) {

		ExportRuleVersionRequest request = new ExportRuleVersionRequest();
		request.setId(versionId);
		request.setSignedTicket(SignedTicket.fromVal(ticket));

		byte[] xml = rulesService.exportRuleVersion(request);

		return Response.ok(xml)
				.header("Content-Disposition", "attachment; filename=rule-version.xml")
				.build();
	}

}
