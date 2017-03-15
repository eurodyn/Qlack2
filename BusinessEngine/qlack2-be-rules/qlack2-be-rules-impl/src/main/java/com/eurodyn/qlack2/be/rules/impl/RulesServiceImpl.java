package com.eurodyn.qlack2.be.rules.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.client.RulesResourceConsumer;
import com.eurodyn.qlack2.be.rules.api.client.RulesResourceConsumer.ResourceType;
import com.eurodyn.qlack2.be.rules.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteRuleVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeRuleResult;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlRuleVersionDTO;
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
import com.eurodyn.qlack2.be.rules.api.request.rule.version.UpdateRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.util.Constants;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuleDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuleVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.KnowledgeBaseUtil;
import com.eurodyn.qlack2.be.rules.impl.util.RuleUtils;
import com.eurodyn.qlack2.be.rules.impl.util.SecurityUtils;
import com.eurodyn.qlack2.be.rules.impl.util.VersionStateUtils;
import com.eurodyn.qlack2.be.rules.impl.util.XmlConverterUtil;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.CreateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.UpdateSecureResourceRequest;

public class RulesServiceImpl implements RulesService {
	private static final Logger LOGGER = Logger.getLogger(RulesServiceImpl.class.getName());

	private static final String DEFAULT_RULE =
			"rule \"{0}\"\n" +
			"    when\n" +
			"    then\n" +
			"end\n";

	@SuppressWarnings("unused")
	private IDMService idmService;

	private SecurityService securityService;

	private AuditClientService audit;

	private EventPublisherService eventPublisher;

	private EntityManager em;

	private ConverterUtil mapper;

	private XmlConverterUtil xmlMapper;

	private AuditConverterUtil auditMapper;

	private SecurityUtils securityUtils;

	private VersionStateUtils versionStateUtils;

	private KnowledgeBaseUtil knowledgeBaseUtil;

	private List<RulesResourceConsumer> consumers;

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	public void setEventPublisher(EventPublisherService eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setMapper(ConverterUtil mapper) {
		this.mapper = mapper;
	}

	public void setXmlMapper(XmlConverterUtil xmlMapper) {
		this.xmlMapper = xmlMapper;
	}

	public void setAuditMapper(AuditConverterUtil auditMapper) {
		this.auditMapper = auditMapper;
	}

	public void setSecurityUtils(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	public void setVersionStateUtils(VersionStateUtils versionStateUtils) {
		this.versionStateUtils = versionStateUtils;
	}

	public void setKnowledgeBaseUtil(KnowledgeBaseUtil knowledgeBaseUtil) {
		this.knowledgeBaseUtil = knowledgeBaseUtil;
	}

	public void setConsumers(List<RulesResourceConsumer> consumers) {
		this.consumers = consumers;
	}

	// -- Rules

	@ValidateTicket
	@Override
	public List<RuleDTO> getRules(GetProjectRulesRequest request) {
		String projectId = request.getProjectId();
		boolean filterEmpty = request.isFilterEmpty();

		LOGGER.log(Level.FINE, "Get rules for project {0}.", projectId);

		List<Rule> rules = Rule.findByProjectId(em, projectId);

		List<RuleDTO> ruleDtos = new ArrayList<>();
		for (Rule rule : rules) {
			if (!filterEmpty || !rule.getVersions().isEmpty()) {
				// do not check security, summary is always viewable
				ruleDtos.add(mapper.mapRuleSummary(rule));
			}
		}

		return ruleDtos;
	}

	@ValidateTicket
	@Override
	public RuleDTO getRule(GetRuleRequest request) {
		String ruleId = request.getId();

		LOGGER.log(Level.FINE, "Get rule {0}.", ruleId);

		Rule rule = Rule.findById(em, ruleId);
		if (rule == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewRule(ticket, rule);

		RuleDTO ruleDto = mapper.mapRule(rule, ticket);

		List<RuleVersion> versions = RuleVersion.findByRuleId(em, ruleId);
		ruleDto.setVersions(mapper.mapRuleVersionSummaryList(versions, ticket));

		AuditRuleDTO auditDto = auditMapper.mapRule(rule);
		auditDto.setVersions(auditMapper.mapRuleVersionList(versions));
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.RULE.toString(),
					null, ticket.getUserID(), auditDto);

		return ruleDto;
	}

	@ValidateTicket
	@Override
	public RuleDTO getRuleByProjectAndName(GetRuleByProjectAndNameRequest request) {
		String projectId = request.getProjectId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get rule by project {0} and name {1}.", new Object[]{projectId, name});

		Rule rule = Rule.findByProjectAndName(em, projectId, name);
		if (rule == null) {
			return null;
		}

		RuleDTO ruleDto = mapper.mapRuleSummary(rule);

		return ruleDto;
	}

	@ValidateTicket
	@Override
	public String createRule(CreateRuleRequest request) {
		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Create rule in project {0}.", projectId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanCreateRule(ticket, projectId);

		Rule rule = new Rule();
		String ruleId = rule.getId();

		rule.setProjectId(projectId);
		rule.setName(request.getName());
		rule.setDescription(request.getDescription());
		rule.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		rule.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		rule.setCreatedBy(ticket.getUserID());
		rule.setCreatedOn(millis);
		rule.setLastModifiedBy(ticket.getUserID());
		rule.setLastModifiedOn(millis);

		em.persist(rule);

		CreateSecureResourceRequest resourceRequest = new CreateSecureResourceRequest(ruleId, rule.getName(), "Rule");
		securityService.createSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_CREATE, ruleId);

		AuditRuleDTO auditDto = auditMapper.mapRule(rule);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.RULE.toString(),
					null, ticket.getUserID(), auditDto);

		return ruleId;
	}

	@ValidateTicket
	@Override
	public void updateRule(UpdateRuleRequest request) {
		String ruleId = request.getId();

		LOGGER.log(Level.FINE, "Update rule {0}.", ruleId);

		Rule rule = Rule.findById(em, ruleId);
		if (rule == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		rule.setName(request.getName());
		rule.setDescription(request.getDescription());
		rule.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		rule.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		rule.setLastModifiedBy(ticket.getUserID());
		rule.setLastModifiedOn(millis);

		UpdateSecureResourceRequest resourceRequest = new UpdateSecureResourceRequest(ruleId, rule.getName(), "Rule");
		securityService.updateSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_UPDATE, ruleId);

		AuditRuleDTO auditDto = auditMapper.mapRule(rule);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.RULE.toString(),
					null, ticket.getUserID(), auditDto);

		UpdateRuleVersionRequest versionRequest = request.getVersionRequest();
		if (versionRequest != null) {
			updateRuleVersion(ticket, rule, versionRequest);
		}
	}

	@ValidateTicket
	@Override
	public CanDeleteRuleResult canDeleteRule(DeleteRuleRequest request) {
		String ruleId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete rule {0}.", ruleId);

		Rule rule = Rule.findById(em, ruleId);
		if (rule == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewRule(ticket, rule);

		AuditRuleDTO auditDto = auditMapper.mapRule(rule);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.RULE.toString(),
					null, ticket.getUserID(), auditDto);

		long countWorkingSets = WorkingSetVersion.countContainingRule(em, ruleId);
		if (countWorkingSets > 0) {
			CanDeleteRuleResult result = new CanDeleteRuleResult();
			result.setResult(false);

			result.setContainedInWorkingSet(true);
			return result;
		}

		long countLockedByOther = RuleVersion.countLockedByOtherUser(em, ruleId, ticket.getUserID());
		if (countLockedByOther > 0) {
			CanDeleteRuleResult result = new CanDeleteRuleResult();
			result.setResult(false);

			result.setLockedByOtherUser(true);
			return result;
		}

		List<String> versionIds = RuleVersion.findIdsByRuleId(em, ruleId);
		boolean consumersCanRemoveResource = consumersCanRemoveResources(versionIds);
		if (!consumersCanRemoveResource) {
			CanDeleteRuleResult result = new CanDeleteRuleResult();
			result.setResult(false);

			result.setUsedByOtherComponents(true);
			return result;
		}

		CanDeleteRuleResult result = new CanDeleteRuleResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteRule(DeleteRuleRequest request) {
		String ruleId = request.getId();

		LOGGER.log(Level.FINE, "Delete rule {0}.", ruleId);

		Rule rule = Rule.findById(em, ruleId);
		if (rule == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		List<RuleVersion> versions = RuleVersion.findByRuleId(em, ruleId);
		versionStateUtils.checkCanModifyRule(ticket.getUserID(), versions);

		long countWorkingSets = WorkingSetVersion.countContainingRule(em, ruleId);
		if (countWorkingSets > 0) {
			throw new QInvalidActionException("This rule has versions contained in working sets.");
		}

		long countLockedByOther = RuleVersion.countLockedByOtherUser(em, ruleId, ticket.getUserID());
		if (countLockedByOther > 0) {
			throw new QInvalidActionException("This rule has versions locked by other users.");
		}

		List<String> versionIds = RuleVersion.findIdsByRuleId(em, ruleId);
		boolean consumersCanRemoveResource = consumersCanRemoveResources(versionIds);
		if (!consumersCanRemoveResource) {
			throw new QInvalidActionException("This rule has versions used by other components.");
		}

		AuditRuleDTO auditDto = auditMapper.mapRule(rule);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RULE.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(rule);

		DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(ruleId);
		securityService.deleteSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_DELETE, ruleId);
	}

	// -- Rule versions

	@ValidateTicket
	@Override
	public List<RuleVersionIdentifierDTO> getProjectRuleVersions(GetProjectRuleVersionsRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Get rule versions for project {0}.", projectId);

		List<RuleVersion> ruleVersions = RuleVersion.findSystemByProjectId(em, projectId);

		List<RuleVersionIdentifierDTO> ruleVersionDtos = new ArrayList<>();
		for (RuleVersion version : ruleVersions) {
			Rule rule = version.getRule();
			if (securityUtils.canViewRule(ticket, rule)) {
				// de-normalize by working set
				for (WorkingSetVersion workingSetVersion : version.getWorkingSets()) {
					RuleVersionIdentifierDTO versionDto = mapper.mapRuleVersionIdentifier(version);
					versionDto.setWorkingSetVersionId(workingSetVersion.getId());
					ruleVersionDtos.add(versionDto);
				}
			}
		}

		return ruleVersionDtos;
	}

	@ValidateTicket
	@Override
	public RuleVersionIdentifierDTO getRuleVersionIdentifier(GetRuleVersionIdentifierRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get rule version {0} identifier.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewRule(ticket, rule);

		RuleVersionIdentifierDTO versionDto = mapper.mapRuleVersionIdentifier(version);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public String getRuleVersionIdByName(GetRuleVersionIdByNameRequest request) {
		String projectId = request.getProjectId();
		String ruleName = request.getRuleName();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get rule version id by project {0}, working set {1} and name {2}.",
				new Object[]{projectId, ruleName, name});

		String versionId = RuleVersion.findIdByName(em, projectId, ruleName, name);

		return versionId;
	}

	@ValidateTicket
	@Override
	public List<RuleVersionDTO> getRuleVersions(GetRuleVersionsRequest request) {
		String ruleId = request.getId();

		LOGGER.log(Level.FINE, "Get rule versions for rule {0}.", ruleId);

		SignedTicket ticket = request.getSignedTicket();

		// do not check security, summary is always viewable
		List<RuleVersion> versions = RuleVersion.findByRuleId(em, ruleId);

		List<RuleVersionDTO> versionDtos = mapper.mapRuleVersionSummaryList(versions, ticket);

		return versionDtos;
	}

	@ValidateTicket
	@Override
	public RuleVersionDTO getRuleVersion(GetRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewRule(ticket, rule);

		RuleVersionDTO versionDto = mapper.mapRuleVersion(version, ticket);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public RuleVersionDTO getRuleVersionByRuleAndName(GetRuleVersionByRuleAndNameRequest request) {
		String ruleId = request.getRuleId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get rule version by rule {0} and name {1}.", new Object[]{ruleId, name});

		RuleVersion version = RuleVersion.findByRuleAndName(em, ruleId, name);
		if (version == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		RuleVersionDTO versionDto = mapper.mapRuleVersionSummary(version, ticket);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public String getRuleIdByVersionId(GetRuleIdByVersionIdRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get rule id for rule version {0}.", versionId);

		String ruleId = RuleVersion.findRuleIdById(em, versionId);

		return ruleId;
	}

	@ValidateTicket
	@Override
	public String createRuleVersion(CreateRuleVersionRequest request) {
		String ruleId = request.getRuleId();

		LOGGER.log(Level.FINE, "Create rule version in rule {0}.", ruleId);

		Rule rule = Rule.findById(em, ruleId); // load in full for security
		if (rule == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		RuleVersion version = new RuleVersion();
		String versionId = version.getId();
		version.setRule(rule);

		String name = request.getName();
		String content = MessageFormat.format(DEFAULT_RULE, name);

		version.setName(name);
		version.setDescription(request.getDescription());
		version.setContent(content); // XXX must set default value
		version.setRuleName(name);

		String baseVersionId = request.getBasedOnId();
		if (baseVersionId != null && !baseVersionId.isEmpty()) {
			copyFromBaseVersion(version, baseVersionId);
		}

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setState(VersionState.DRAFT);
		version.setCreatedBy(ticket.getUserID());
		version.setCreatedOn(millis);
		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		em.persist(version);

		publishVersionEvent(ticket, Constants.EVENT_CREATE, versionId);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	private void copyFromBaseVersion(RuleVersion version, String baseVersionId) {
		RuleVersion baseVersion = RuleVersion.findById(em, baseVersionId);
		if (baseVersion == null) {
			throw new IllegalArgumentException("Base rule version does not exist.");
		}

		Rule rule = version.getRule();
		Rule baseRule = baseVersion.getRule();
		if (!baseRule.getId().equals(rule.getId())) {
			throw new IllegalArgumentException("Base rule version does not belong to current rule.");
		}

		version.setContent(baseVersion.getContent());
		version.setRuleName(baseVersion.getRuleName());
	}

	@ValidateTicket
	@Override
	public long countMatchingRuleName(CountMatchingRuleNameRequest request) {
		String ruleId = request.getRuleId();
		String ruleName = request.getDroolsRuleName();

		LOGGER.log(Level.FINE, "Check rule versions with matching drools rule name {0} excluding rule {1}.", new Object[]{ruleName, ruleId});

		Rule rule = Rule.findById(em, ruleId);
		if (rule == null) {
			throw new IllegalArgumentException("Cannot find rule");
		}

		String projectId = rule.getProjectId();

		long matches = RuleVersion.countMatchingRuleName(em, ruleName, ruleId, projectId);

		return matches;
	}

	private void updateRuleVersion(SignedTicket ticket, Rule rule, UpdateRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Update rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		versionStateUtils.checkRuleVersionNotFinalized(version);
		versionStateUtils.checkCanModifyRuleVersion(ticket.getUserID(), version);

		Rule existingRule = version.getRule();
		if (!rule.getId().equals(existingRule.getId())) {
			throw new IllegalArgumentException("Rule version does not belong to rule.");
		}

		version.setDescription(request.getDescription());
		version.setContent(request.getContent());

		// rule parsing is light
		String ruleName = RuleUtils.findRuleNames(request.getContent()).get(0);
		version.setRuleName(ruleName);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		if (version.getState() == VersionState.TESTING) {
			invalidateWorkingSets(ticket, version);
		}

		publishVersionEvent(ticket, Constants.EVENT_UPDATE, versionId);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	private void invalidateWorkingSets(SignedTicket ticket, RuleVersion version) {
		String versionId = version.getId();
		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingRuleVersion(em, versionId);
		for (WorkingSetVersion workingSetVersion : workingSetVersions) {
			if (workingSetVersion.getState() == VersionState.TESTING) {
				knowledgeBaseUtil.destroyKnowledgeBase(ticket, workingSetVersion);
			}
		}
	}

	@ValidateTicket
	@Override
	public CanDeleteRuleVersionResult canDeleteRuleVersion(DeleteRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingRuleVersion(em, versionId);
		if (!workingSetVersions.isEmpty()) {
			CanDeleteRuleVersionResult result = new CanDeleteRuleVersionResult();
			result.setResult(false);

			result.setContainedInWorkingSetVersions(true);
			List<String> workingSetNames = new ArrayList<>();
			for (WorkingSetVersion workingSetVersion : workingSetVersions) {
				workingSetNames.add(workingSetVersion.getWorkingSet().getName() + " / " + workingSetVersion.getName());
			}
			result.setWorkingSetVersions(workingSetNames);

			return result;
		}

		boolean consumersCanRemoveResource = consumersCanRemoveResource(versionId);
		if (!consumersCanRemoveResource) {
			CanDeleteRuleVersionResult result = new CanDeleteRuleVersionResult();
			result.setResult(false);

			result.setUsedByOtherComponents(true);
			return result;
		}

		CanDeleteRuleVersionResult result = new CanDeleteRuleVersionResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteRuleVersion(DeleteRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Delete rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		versionStateUtils.checkCanModifyRuleVersion(ticket.getUserID(), version);

		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingRuleVersion(em, versionId);
		if (!workingSetVersions.isEmpty()) {
			throw new QInvalidActionException("The rule version is contained in a working set version.");
		}

		boolean consumersCanRemoveResource = consumersCanRemoveResource(versionId);
		if (!consumersCanRemoveResource) {
			throw new QInvalidActionException("The rule version is used by other components.");
		}

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(version);

		publishVersionEvent(ticket, Constants.EVENT_DELETE, versionId);
	}

	private boolean consumersCanRemoveResources(List<String> versionIds) {
		for (String versionId : versionIds) {
			if (!consumersCanRemoveResource(versionId)) {
				return false;
			}
		}

		return true;
	}

	private boolean consumersCanRemoveResource(String versionId) {
		for (RulesResourceConsumer consumer : consumers) {
			if (!consumer.canRemoveResource(versionId, ResourceType.RULE_VERSION)) {
				return false;
			}
		}

		return true;
	}

	@ValidateTicket
	@Override
	public void lockRuleVersion(LockRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Lock rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanLockRuleVersion(ticket, rule);

		versionStateUtils.checkRuleVersionNotFinalized(version);
		versionStateUtils.checkCanLockRuleVersion(ticket.getUserID(), version);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLockedBy(ticket.getUserID());
		version.setLockedOn(millis);

		publishVersionEvent(ticket, Constants.EVENT_LOCK, versionId);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.LOCK.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void unlockRuleVersion(UnlockRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Unlock rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUnlockRuleVersion(ticket, rule);

		versionStateUtils.checkRuleVersionNotFinalized(version);

		boolean canUnlockAny = securityUtils.canUnlockAnyRuleVersion(ticket, rule);
		versionStateUtils.checkCanUnlockRuleVersion(ticket.getUserID(), canUnlockAny, version);

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_UNLOCK, versionId);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UNLOCK.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	// -- EnableTesting / Finalize

	@ValidateTicket
	@Override
	public CanEnableTestingRuleResult canEnableTestingRuleVersion(EnableTestingRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can enable testing rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_ENABLE_TESTING.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		// Always returns true, do not delete for now

		CanEnableTestingRuleResult result = new CanEnableTestingRuleResult();
		result.setResult(true);
		return result;
	}

	@ValidateTicket
	@Override
	public CanFinalizeRuleResult canFinalizeRuleVersion(FinalizeRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can finalize rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_FINALISE.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		// Always returns true, do not delete for now

		CanFinalizeRuleResult result = new CanFinalizeRuleResult();
		result.setResult(true);
		return result;
	}

	@ValidateTicket
	@Override
	public CanDisableTestingRuleResult canDisableTestingRuleVersion(EnableTestingRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can disable testing rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DISABLE_TESTING.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		List<WorkingSetVersion> enabledTestingWorkingSetVersions = filterEnabledTestingWorkingSetVersions(version.getWorkingSets());
		if (!enabledTestingWorkingSetVersions.isEmpty()) {
			List<String> workingSetVersionNames = new ArrayList<>();
			for (WorkingSetVersion workingSetVersion : enabledTestingWorkingSetVersions) {
				workingSetVersionNames.add(workingSetVersion.getWorkingSet().getName() + " / " + workingSetVersion.getName());
			}

			CanDisableTestingRuleResult result = new CanDisableTestingRuleResult();
			result.setResult(false);
			result.setContainedInWorkingSetVersions(true);
			result.setWorkingSetVersions(workingSetVersionNames);
			return result;
		}

		CanDisableTestingRuleResult result = new CanDisableTestingRuleResult();
		result.setResult(true);
		return result;
	}

	private List<WorkingSetVersion> filterEnabledTestingWorkingSetVersions(List<WorkingSetVersion> versions) {
		List<WorkingSetVersion> enabledTestingVersions = new ArrayList<>();
		for (WorkingSetVersion version : versions) {
			if (version.getState() == VersionState.TESTING) {
				enabledTestingVersions.add(version);
			}
		}
		return enabledTestingVersions;
	}

	@ValidateTicket
	@Override
	public void enableTestingRuleVersion(EnableTestingRuleVersionRequest request) {
		String versionId = request.getId();
		boolean enableTesting = request.isEnableTesting();

		LOGGER.log(Level.FINE, "Enable testing rule version {0} ({1}).", new Object[]{versionId, enableTesting});

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		versionStateUtils.checkRuleVersionNotFinalized(version);
		versionStateUtils.checkCanModifyRuleVersion(ticket.getUserID(), version);

		if (enableTesting) {
			version.setState(VersionState.TESTING);
		}
		else {
			List<WorkingSetVersion> enabledTestingWorkingSetVersions = filterEnabledTestingWorkingSetVersions(version.getWorkingSets());
			if (!enabledTestingWorkingSetVersions.isEmpty()) {
				throw new QInvalidActionException("This rule version is contained in working set versions with testing enabled.");
			}

			version.setState(VersionState.DRAFT);
		}

		String stringEvent = enableTesting ? Constants.EVENT_ENABLE_TESTING : Constants.EVENT_DISABLE_TESTING;
		publishVersionEvent(ticket, stringEvent, versionId);

		EVENT event = enableTesting ? EVENT.ENABLE_TESTING : EVENT.DISABLE_TESTING;
		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), event.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void finalizeRuleVersion(FinalizeRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Finalize rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		versionStateUtils.checkRuleVersionNotFinalized(version);
		versionStateUtils.checkCanModifyRuleVersion(ticket.getUserID(), version);

		version.setState(VersionState.FINAL);

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_FINALISE, versionId);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.FINALISE.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public byte[] exportRuleVersion(ExportRuleVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Export rule version {0}.", versionId);

		RuleVersion version = RuleVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Rule rule = version.getRule();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewRule(ticket, rule);

		if (version.getState() != VersionState.FINAL) {
			throw new QInvalidActionException("Version is not finalized.");
		}

		XmlRuleVersionDTO xmlVersionDto = xmlMapper.mapRule(version);

		byte[] xml = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// XXX generated XML is immutable, generate once and cache ?
			JAXBContext jaxbContext = JAXBContext.newInstance(XmlRuleVersionDTO.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(xmlVersionDto, baos);

			xml = baos.toByteArray();
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot export rule version.", e);
		}

		publishVersionEvent(ticket, Constants.EVENT_EXPORT, versionId);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.EXPORT.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return xml;
	}

	@ValidateTicket
	@Override
	public String importRuleVersion(ImportRuleVersionRequest request) {
		String ruleId = request.getRuleId();
		byte[] xml = request.getXml();

		LOGGER.log(Level.FINE, "Import rule version in rule {0}.", ruleId);

		Rule rule = Rule.findById(em, ruleId);
		if (rule == null) {
			throw new IllegalArgumentException("Rule does not exist");
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateRule(ticket, rule);

		XmlRuleVersionDTO xmlVersion = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xml);

			JAXBContext jaxbContext = JAXBContext.newInstance(XmlRuleVersionDTO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			xmlVersion = (XmlRuleVersionDTO) jaxbUnmarshaller.unmarshal(bais);
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot import rule version.", e);
		}

		RuleVersion existingRuleVersion = RuleVersion.findByRuleAndName(em, ruleId, xmlVersion.getName());
		if (existingRuleVersion != null) {
			throw new QImportExportException("Another rule version with the same name already exists.");
		}

		RuleVersion version = xmlMapper.mapRuleVersion(ticket, rule, xmlVersion);
		String versionId = version.getId();

		xmlMapper.computeDroolsRuleName(em, rule.getProjectId(), ruleId, version, xmlVersion);

		em.persist(version);

		publishVersionEvent(ticket, Constants.EVENT_IMPORT, versionId);

		AuditRuleVersionDTO auditDto = auditMapper.mapRuleVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.IMPORT.toString(), GROUP.RULE_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	// -- Helpers

	@Override
	public boolean canModifyRuleVersionIdList(SignedTicket ticket, List<String> versionIds) {
		List<RuleVersion> versions = new ArrayList<>();
		for (String versionId : versionIds) {
			RuleVersion version = RuleVersion.findById(em, versionId);
			versions.add(version);
		}
		return canModifyRuleVersionList(ticket, versions);
	}

	private boolean canModifyRuleVersionList(SignedTicket ticket, List<RuleVersion> versions) {
		for (RuleVersion version : versions) {
			if (!canModifySingleRuleVersion(ticket, version)) {
				return false;
			}
		}
		return true;
	}

	private boolean canModifySingleRuleVersion(SignedTicket ticket, RuleVersion version) {
		Rule rule = version.getRule();
		if (!securityUtils.canUpdateRule(ticket, rule)) {
			return false;
		}

		if (!versionStateUtils.canModifyRuleVersion(ticket.getUserID(), version)) {
			return false;
		}

		return true;
	}

	private void publishEvent(SignedTicket ticket, String event, String ruleId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_RULE_ID, ruleId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_RULE + "/" + event);
	}

	private void publishVersionEvent(SignedTicket ticket, String event, String versionId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_RULE_VERSION_ID, versionId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_RULE_VERSION + "/" + event);
	}

}
