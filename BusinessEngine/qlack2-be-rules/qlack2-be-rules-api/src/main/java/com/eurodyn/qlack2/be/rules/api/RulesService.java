package com.eurodyn.qlack2.be.rules.api;

import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteRuleVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeRuleResult;
import com.eurodyn.qlack2.be.rules.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectRulesRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.CreateRuleRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.DeleteRuleRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.GetRuleByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.GetRuleRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.UpdateRuleRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.CountMatchingRuleNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.CreateRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.DeleteRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.EnableTestingRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.ExportRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.FinalizeRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetProjectRuleVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleIdByVersionIdRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionByRuleAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionIdentifierRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.ImportRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.LockRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.UnlockRuleVersionRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public interface RulesService {

	// -- Rules

	List<RuleDTO> getRules(GetProjectRulesRequest request)
		throws QInvalidTicketException, QAuthorisationException;

	RuleDTO getRule(GetRuleRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	RuleDTO getRuleByProjectAndName(GetRuleByProjectAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createRule(CreateRuleRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void updateRule(UpdateRuleRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteRuleResult canDeleteRule(DeleteRuleRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteRule(DeleteRuleRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	// -- Rule versions

	List<RuleVersionIdentifierDTO> getProjectRuleVersions(GetProjectRuleVersionsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	RuleVersionIdentifierDTO getRuleVersionIdentifier(GetRuleVersionIdentifierRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String getRuleVersionIdByName(GetRuleVersionIdByNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	List<RuleVersionDTO> getRuleVersions(GetRuleVersionsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	RuleVersionDTO getRuleVersion(GetRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	RuleVersionDTO getRuleVersionByRuleAndName(GetRuleVersionByRuleAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String getRuleIdByVersionId(GetRuleIdByVersionIdRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createRuleVersion(CreateRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	long countMatchingRuleName(CountMatchingRuleNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteRuleVersionResult canDeleteRuleVersion(DeleteRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteRuleVersion(DeleteRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void lockRuleVersion(LockRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void unlockRuleVersion(UnlockRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	CanEnableTestingRuleResult canEnableTestingRuleVersion(EnableTestingRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDisableTestingRuleResult canDisableTestingRuleVersion(EnableTestingRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void enableTestingRuleVersion(EnableTestingRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	CanFinalizeRuleResult canFinalizeRuleVersion(FinalizeRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void finalizeRuleVersion(FinalizeRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	byte[] exportRuleVersion(ExportRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

	String importRuleVersion(ImportRuleVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

	// -- Helpers

	boolean canModifyRuleVersionIdList(SignedTicket ticket, List<String> versionIds);

}
