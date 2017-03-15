package com.eurodyn.qlack2.be.rules.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.be.rules.api.RulesRuntimeManagementService;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.exception.QRuntimeManagementException;
import com.eurodyn.qlack2.be.rules.api.request.runtime.CreateKnowledgeBaseRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.CreateKnowledgeSessionRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.DeleteFactRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.DestroyKnowledgeSessionRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.FireRulesRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetFactRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetFactsRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetGlobalRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.GetQueryResultsRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.InsertFactRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.InsertFactsRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.SetGlobalRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.StatelessExecuteRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.StatelessMultiExecuteRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.WorkingSetRuleVersionPair;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuntimeFactDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuntimeFactsDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuntimeGlobalDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuntimeQueryDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuntimeSessionDTO;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersionKnowledgeBase;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.KnowledgeBaseUtil;
import com.eurodyn.qlack2.be.rules.impl.util.SecurityUtils;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.rules.api.RulesRuntimeService;
import com.eurodyn.qlack2.fuse.rules.api.StatelessExecutionResults;

public class RulesRuntimeManagementServiceImpl implements RulesRuntimeManagementService {
	private static final Logger LOGGER = Logger.getLogger(RulesRuntimeManagementServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private IDMService idmService;

	private AuditClientService audit;

	private EntityManager em;

	private SecurityUtils securityUtils;

	private RulesRuntimeService rulesRuntimeService;

	private KnowledgeBaseUtil knowledgeBaseUtil;

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setSecurityUtils(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	public void setRulesRuntimeService(RulesRuntimeService rulesRuntimeService) {
		this.rulesRuntimeService = rulesRuntimeService;
	}

	public void setKnowledgeBaseUtil(KnowledgeBaseUtil knowledgeBaseUtil) {
		this.knowledgeBaseUtil = knowledgeBaseUtil;
	}

	private String doCreateKnowledgeBase(SignedTicket ticket, String workingSetVersionId) {
		WorkingSetVersion workingSetVersion = WorkingSetVersion.findById(em, workingSetVersionId);
		if (workingSetVersion == null) {
			throw new QRuntimeManagementException("Cannot find working set version.");
		}

		VersionState state = workingSetVersion.getState();
		if (state != VersionState.TESTING && state != VersionState.FINAL) {
			throw new QRuntimeManagementException("Only final and enabled-for-testing working set versions can be used for creating knowledge bases.");
		}

		String kbaseId = null;
		WorkingSetVersionKnowledgeBase existingKnowledgeBase = workingSetVersion.getKnowledgeBase();
		if (existingKnowledgeBase != null) {
			kbaseId = existingKnowledgeBase.getKnowledgeBaseId();
		}
		else {
			WorkingSetVersionKnowledgeBase knowledgeBase = knowledgeBaseUtil.createKnowledgeBase(ticket, workingSetVersion);
			kbaseId = knowledgeBase.getKnowledgeBaseId();
		}

		return kbaseId;
	}
	
	@ValidateTicket
	@Override
	public String createKnowledgeBase(CreateKnowledgeBaseRequest request) {
		String workingSetVersionId = request.getWorkingSetVersionId();

		LOGGER.log(Level.FINE, "Create KnowledgeBase for working set version {0}.", new Object[]{workingSetVersionId});

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteWorkingSet(ticket, workingSetVersionId);

		String kbaseId = WorkingSetVersionKnowledgeBase.findKnowledgeBaseIdByWorkingSetVersionId(em, workingSetVersionId);
		if (kbaseId == null) {
			kbaseId = doCreateKnowledgeBase(ticket, workingSetVersionId);
		}

		return kbaseId;
	}

	@ValidateTicket
	@Override
	public StatelessExecutionResults statelessExecute(StatelessExecuteRequest request) {
		String workingSetVersionId = request.getPair().getWorkingSetVersionId();
		String ruleVersionId = request.getPair().getRuleVersionId();

		LOGGER.log(Level.FINE, "Stateless execute for working set version {0} and rule version.", new Object[]{workingSetVersionId, ruleVersionId});

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteWorkingSet(ticket, workingSetVersionId);

		String kbaseId = WorkingSetVersionKnowledgeBase.findKnowledgeBaseIdByWorkingSetVersionId(em, workingSetVersionId);
		if (kbaseId == null) {
			kbaseId = doCreateKnowledgeBase(ticket, workingSetVersionId);
		}

		String ruleName = null;
		if (ruleVersionId != null) {
			RuleVersion ruleVersion = RuleVersion.findById(em, ruleVersionId);
			if (ruleVersion == null) {
				throw new QRuntimeManagementException("Cannot find rule version.");
			}

			if (!ruleVersionContainedInWorkingSetVersion(ruleVersion, workingSetVersionId)) {
				throw new QRuntimeManagementException("Rule version does not belong to working set version.");
			}

			ruleName = ruleVersion.getRuleName();
		}

		Map<String, byte[]> inputGlobals = request.getGlobals();
		if (inputGlobals == null) {
			inputGlobals = new LinkedHashMap<>();  // XXX throw NPE ?
		}

		List<byte[]> inputFacts = request.getFacts();
		if (inputFacts == null) {
			inputFacts = new ArrayList<>(); // XXX throw NPE ?
		}

		StatelessExecutionResults results = rulesRuntimeService.statelessExecute(kbaseId, ruleName, inputGlobals, inputFacts);

		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.EXECUTE.toString(), GROUP.RUNTIME_KSESSION.toString(),
					null, ticket.getUserID(), request.getPair());

		return results;
	}

	@ValidateTicket
	@Override
	public StatelessExecutionResults statelessMultiExecute(StatelessMultiExecuteRequest request) {
		List<WorkingSetRuleVersionPair> pairs = request.getPairs();

		LOGGER.log(Level.FINE, "Stateless multi-execute");

		StatelessExecutionResults results = null;
		Map<String, byte[]> globals = request.getGlobals();
		List<byte[]> facts = request.getFacts();

		for (WorkingSetRuleVersionPair pair : pairs) {
			StatelessExecuteRequest singleRequest = new StatelessExecuteRequest();
			singleRequest.setPair(pair);
			singleRequest.setGlobals(globals);
			singleRequest.setFacts(facts);
			singleRequest.setSignedTicket(request.getSignedTicket());

			results = statelessExecute(singleRequest);
			globals = results.getGlobals();
			facts = results.getFacts();
		}

		return results;
	}

	@ValidateTicket
	@Override
	public String createKnowledgeSession(CreateKnowledgeSessionRequest request) {
		String workingSetVersionId = request.getWorkingSetVersionId();

		LOGGER.log(Level.FINE, "Create knowledge session for working set version {0}", workingSetVersionId);

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteWorkingSet(ticket, workingSetVersionId);

		String kbaseId = WorkingSetVersionKnowledgeBase.findKnowledgeBaseIdByWorkingSetVersionId(em, workingSetVersionId);
		if (kbaseId == null) {
			kbaseId = doCreateKnowledgeBase(ticket, workingSetVersionId);
		}

		String sessionId = rulesRuntimeService.createKnowledgeSession(kbaseId);

		AuditRuntimeSessionDTO auditDto = new AuditRuntimeSessionDTO();
		auditDto.setWorkingSetVersionId(workingSetVersionId);
		auditDto.setSessionId(sessionId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.RUNTIME_KSESSION.toString(),
					null, ticket.getUserID(), auditDto);

		return sessionId;
	}

	@ValidateTicket
	@Override
	public void destroyKnowledgeSession(DestroyKnowledgeSessionRequest request) {
		String sessionId = request.getSessionId();

		LOGGER.log(Level.FINE, "Destroy knowledge session {0}", sessionId);

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		rulesRuntimeService.destroyKnowledgeSession(sessionId);

		AuditRuntimeSessionDTO auditDto = new AuditRuntimeSessionDTO();
		auditDto.setWorkingSetVersionId(null);
		auditDto.setSessionId(sessionId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RUNTIME_KSESSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void setGlobal(SetGlobalRequest request) {
		String sessionId = request.getSessionId();
		String globalId = request.getGlobalId();
		byte[] global = request.getGlobal();

		LOGGER.log(Level.FINE, "Set global in session {0} with id {1}", new Object[]{sessionId, globalId});

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		rulesRuntimeService.setGlobal(sessionId, globalId, global);

		AuditRuntimeGlobalDTO auditDto = new AuditRuntimeGlobalDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setGlobalId(globalId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.SET.toString(), GROUP.RUNTIME_GLOBAL.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public byte[] getGlobal(GetGlobalRequest request) {
		String sessionId = request.getSessionId();
		String globalId = request.getGlobalId();

		LOGGER.log(Level.FINE, "Get global from session {0} with id {1}", new Object[]{sessionId, globalId});

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		byte[] global = rulesRuntimeService.getGlobal(sessionId, globalId);

		AuditRuntimeGlobalDTO auditDto = new AuditRuntimeGlobalDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setGlobalId(globalId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.GET.toString(), GROUP.RUNTIME_GLOBAL.toString(),
					null, ticket.getUserID(), auditDto);

		return global;
	}

	@ValidateTicket
	@Override
	public String insertFact(InsertFactRequest request) {
		String sessionId = request.getSessionId();
		byte[] fact = request.getFact();

		LOGGER.log(Level.FINE, "Insert fact in session {0}.", sessionId);

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		String factId = rulesRuntimeService.insertFact(sessionId, fact);

		LOGGER.log(Level.FINE, "Inserted fact in session {0} with id {1}.", new Object[]{sessionId, factId});

		AuditRuntimeFactDTO auditDto = new AuditRuntimeFactDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setFactId(factId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.SET.toString(), GROUP.RUNTIME_FACT.toString(),
					null, ticket.getUserID(), auditDto);

		return factId;
	}

	@ValidateTicket
	@Override
	public List<String> insertFacts(InsertFactsRequest request) {
		String sessionId = request.getSessionId();
		List<byte[]> facts = request.getFacts();

		LOGGER.log(Level.FINE, "Insert multiple facts in session {0}.", sessionId);

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		List<String> factIds = rulesRuntimeService.insertFacts(sessionId, facts);

		LOGGER.log(Level.FINE, "Inserted multiple fact in session {0} with ids:", sessionId);
		for (String factId : factIds) {
			LOGGER.log(Level.FINE, "\t{0}", factId);
		}

		AuditRuntimeFactsDTO auditDto = new AuditRuntimeFactsDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setFactIds(factIds);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.MULTI_SET.toString(), GROUP.RUNTIME_FACT.toString(),
					null, ticket.getUserID(), auditDto);

		return factIds;
	}

	@ValidateTicket
	@Override
	public byte[] getFact(GetFactRequest request) {
		String sessionId = request.getSessionId();
		String factId = request.getFactId();

		LOGGER.log(Level.FINE, "Get fact from session {0} with id {1}.", new Object[]{sessionId, factId});

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		byte[] fact = rulesRuntimeService.getFact(sessionId, factId);

		AuditRuntimeFactDTO auditDto = new AuditRuntimeFactDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setFactId(factId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.GET.toString(), GROUP.RUNTIME_FACT.toString(),
					null, ticket.getUserID(), auditDto);

		return fact;
	}

	@ValidateTicket
	@Override
	public List<byte[]> getFacts(GetFactsRequest request) {
		String sessionId = request.getSessionId();
		List<String> factIds = request.getFactIds();

		LOGGER.log(Level.FINE, "Get multiple facts from session {0} with ids:", sessionId);
		for (String factId : factIds) {
			LOGGER.log(Level.FINE, "\t{0}", factId);
		}

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		List<byte[]> facts = rulesRuntimeService.getFacts(sessionId, factIds);

		AuditRuntimeFactsDTO auditDto = new AuditRuntimeFactsDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setFactIds(factIds);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.MULTI_GET.toString(), GROUP.RUNTIME_FACT.toString(),
					null, ticket.getUserID(), auditDto);

		return facts;
	}

	@ValidateTicket
	@Override
	public void deleteFact(DeleteFactRequest request) {
		String sessionId = request.getSessionId();
		String factId = request.getFactId();

		LOGGER.log(Level.FINE, "Delete fact from session {0} with id {1}.", new Object[]{sessionId, factId});

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		rulesRuntimeService.deleteFact(sessionId, factId);

		AuditRuntimeFactDTO auditDto = new AuditRuntimeFactDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setFactId(factId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RUNTIME_FACT.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void fireRules(FireRulesRequest request) {
		String sessionId = request.getSessionId();
		List<String> ruleVersionIds = request.getRuleVersionIds();

		LOGGER.log(Level.FINE, "Fire rules in session {0}.", sessionId);

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		String baseId = rulesRuntimeService.findKnowledgeBaseIdBySessionId(sessionId);
		String workingSetVersionId = WorkingSetVersionKnowledgeBase.findWorkingSetVersionIdByKnowledgeBaseId(em, baseId);

		List<String> rules = null;
		if (ruleVersionIds != null) {
			rules = new ArrayList<>();
			for (String ruleVersionId : ruleVersionIds) {
				RuleVersion ruleVersion = RuleVersion.findById(em, ruleVersionId);
				if (ruleVersion == null) {
					throw new QRuntimeManagementException("Cannot find rule version.");
				}

				if (!ruleVersionContainedInWorkingSetVersion(ruleVersion, workingSetVersionId)) {
					throw new QRuntimeManagementException("Rule version does not belong to working set version.");
				}

				String ruleName = ruleVersion.getRuleName();

				LOGGER.log(Level.FINE, "Filter rules for rule version id {0} and drools rule name {1}.", new Object[]{ruleVersionId, ruleName});

				rules.add(ruleName);
			}
		}

		rulesRuntimeService.fireRules(sessionId, rules);

		AuditRuntimeSessionDTO auditDto = new AuditRuntimeSessionDTO();
		auditDto.setWorkingSetVersionId(workingSetVersionId);
		auditDto.setSessionId(sessionId);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.FIRE.toString(), GROUP.RUNTIME_KSESSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public List<Map<String, byte[]>> getQueryResults(GetQueryResultsRequest request) {
		String sessionId = request.getSessionId();
		String query = request.getQuery();

		LOGGER.log(Level.FINE, "Get query results from session {0} for query {1}.", new Object[]{sessionId, query});

		SignedTicket ticket = request.getSignedTicket();
		checkCanExecuteSession(ticket, sessionId);

		List<byte[]> arguments = request.getArguments();

		List<Map<String, byte[]>> results = rulesRuntimeService.getQueryResults(sessionId, query, arguments);

		Set<String> identifiers = null;
		if (!results.isEmpty()) {
			identifiers = results.get(0).keySet();
		}

		AuditRuntimeQueryDTO auditDto = new AuditRuntimeQueryDTO();
		auditDto.setSessionId(sessionId);
		auditDto.setQuery(query);
		auditDto.setIdentifiers(identifiers);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.GET_RESULTS.toString(), GROUP.RUNTIME_QUERY.toString(),
					null, ticket.getUserID(), auditDto);

		return results;
	}

	// -- Helpers

	private void checkCanExecuteWorkingSet(SignedTicket ticket, String workingSetVersionId) {
		WorkingSetVersion workingSetVersion = em.find(WorkingSetVersion.class, workingSetVersionId);
		WorkingSet workingSet = workingSetVersion.getWorkingSet();

		securityUtils.checkCanExecuteWorkingSet(ticket, workingSet);
	}

	private void checkCanExecuteSession(SignedTicket ticket, String sessionId) {
		String baseId = rulesRuntimeService.findKnowledgeBaseIdBySessionId(sessionId);

		String workingSetVersionId = WorkingSetVersionKnowledgeBase.findWorkingSetVersionIdByKnowledgeBaseId(em, baseId);

		checkCanExecuteWorkingSet(ticket, workingSetVersionId);
	}

	private boolean ruleVersionContainedInWorkingSetVersion(RuleVersion ruleVersion, String workingSetVersionId) {
		for (WorkingSetVersion workingSetVersion : ruleVersion.getWorkingSets()) {
			if (workingSetVersion.getId().equals(workingSetVersionId)) {
				return true;
			}
		}
		return false;
	}

}
