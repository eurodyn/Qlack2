package com.eurodyn.qlack2.be.rules.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.rules.api.DataModelsService;
import com.eurodyn.qlack2.be.rules.api.LibraryVersionService;
import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.client.RulesResourceConsumer;
import com.eurodyn.qlack2.be.rules.api.client.RulesResourceConsumer.ResourceType;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteWorkingSetVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanGetWorkingSetVersionModelsJarResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanUpdateEnabledForTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlDataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlLibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlRuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlWorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.EnableTestingDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.FinalizeDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.EnableTestingLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.FinaliseLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.EnableTestingRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.FinalizeRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.CreateWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.DeleteWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.GetWorkingSetByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.GetWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.UpdateWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.CreateWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.DeleteWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.EnableTestingWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.ExportWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.FinalizeWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetProjectWorkingSetVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionByWorkingSetAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionDataModelsJarRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdentifierRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.ImportWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.LockWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.UnlockWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.UpdateWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.util.Constants;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditWorkingSetDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditWorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.DataModelsJarUtil;
import com.eurodyn.qlack2.be.rules.impl.util.KnowledgeBaseUtil;
import com.eurodyn.qlack2.be.rules.impl.util.SecurityUtils;
import com.eurodyn.qlack2.be.rules.impl.util.VersionStateUtils;
import com.eurodyn.qlack2.be.rules.impl.util.XmlConverterUtil;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.CreateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.UpdateSecureResourceRequest;

public class WorkingSetsServiceImpl implements WorkingSetsService {
	private static final Logger LOGGER = Logger.getLogger(WorkingSetsServiceImpl.class.getName());

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

	private DataModelsJarUtil dataModelsJarUtil;

	private KnowledgeBaseUtil knowledgeBaseUtil;

	private RulesService rulesService;

	private DataModelsService modelsService;

	private LibraryVersionService libraryVersionService;

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

	public void setDataModelsJarUtil(DataModelsJarUtil dataModelsJarUtil) {
		this.dataModelsJarUtil = dataModelsJarUtil;
	}

	public void setKnowledgeBaseUtil(KnowledgeBaseUtil knowledgeBaseUtil) {
		this.knowledgeBaseUtil = knowledgeBaseUtil;
	}

	public void setRulesService(RulesService rulesService) {
		this.rulesService = rulesService;
	}

	public void setDataModelsService(DataModelsService modelsService) {
		this.modelsService = modelsService;
	}

	public void setLibraryVersionService(LibraryVersionService libraryVersionService) {
		this.libraryVersionService = libraryVersionService;
	}

	public void setConsumers(List<RulesResourceConsumer> consumers) {
		this.consumers = consumers;
	}

	// -- Working Sets

	@ValidateTicket
	@Override
	public WorkingSetDTO getWorkingSet(GetWorkingSetRequest request) {
		String workingSetId = request.getId();

		LOGGER.log(Level.FINE, "Get working set {0}.", workingSetId);

		WorkingSet workingSet = WorkingSet.findById(em, workingSetId);
		if (workingSet == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewWorkingSet(ticket, workingSet);

		WorkingSetDTO workingSetDto = mapper.mapWorkingSet(workingSet, ticket);

		List<WorkingSetVersion> versions = WorkingSetVersion.findByWorkingSetId(em, workingSetId);
		workingSetDto.setVersions(mapper.mapWorkingSetVersionSummaryList(versions, ticket));

		AuditWorkingSetDTO auditDto = auditMapper.mapWorkingSet(workingSet);
		auditDto.setVersions(auditMapper.mapWorkingSetVersionList(versions));
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.WORKING_SET.toString(),
					null, ticket.getUserID(), auditDto);

		return workingSetDto;
	}

	@ValidateTicket
	@Override
	public WorkingSetDTO getWorkingSetByProjectAndName(GetWorkingSetByProjectAndNameRequest request) {
		String projectId = request.getProjectId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get working set by project {0} and name {1}.", new Object[]{projectId, name});

		WorkingSet workingSet = WorkingSet.findByProjectAndName(em, projectId, name);
		if (workingSet == null) {
			return null;
		}

		WorkingSetDTO workingSetDto = mapper.mapWorkingSetSummary(workingSet);

		return workingSetDto;
	}

	@ValidateTicket
	@Override
	public String createWorkingSet(CreateWorkingSetRequest request) {
		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Create working set in project {0}.", projectId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanCreateWorkingSet(ticket, projectId);

		WorkingSet workingSet = new WorkingSet();
		String workingSetId = workingSet.getId();

		workingSet.setProjectId(projectId);
		workingSet.setName(request.getName());
		workingSet.setDescription(request.getDescription());
		workingSet.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		workingSet.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		workingSet.setCreatedBy(ticket.getUserID());
		workingSet.setCreatedOn(millis);
		workingSet.setLastModifiedBy(ticket.getUserID());
		workingSet.setLastModifiedOn(millis);

		em.persist(workingSet);

		CreateSecureResourceRequest resourceRequest = new CreateSecureResourceRequest(workingSetId, workingSet.getName(), "Working Set");
		securityService.createSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_CREATE, workingSetId);

		AuditWorkingSetDTO auditDto = auditMapper.mapWorkingSet(workingSet);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.WORKING_SET.toString(),
					null, ticket.getUserID(), auditDto);

		return workingSetId;
	}

	@ValidateTicket
	@Override
	public void updateWorkingSet(UpdateWorkingSetRequest request) {
		String workingSetId = request.getId();

		LOGGER.log(Level.FINE, "Update working set {0}.", workingSetId);

		WorkingSet workingSet = WorkingSet.findById(em, workingSetId);
		if (workingSet == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		workingSet.setName(request.getName());
		workingSet.setDescription(request.getDescription());
		workingSet.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		workingSet.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		workingSet.setLastModifiedBy(ticket.getUserID());
		workingSet.setLastModifiedOn(millis);

		UpdateSecureResourceRequest resourceRequest = new UpdateSecureResourceRequest(workingSetId, workingSet.getName(), "Working Set");
		securityService.updateSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_UPDATE, workingSetId);

		AuditWorkingSetDTO auditDto = auditMapper.mapWorkingSet(workingSet);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.WORKING_SET.toString(),
					null, ticket.getUserID(), auditDto);

		UpdateWorkingSetVersionRequest versionRequest = request.getVersionRequest();
		if (versionRequest != null) {
			updateWorkingSetVersion(ticket, workingSet, versionRequest);
		}
	}

	@ValidateTicket
	@Override
	public CanDeleteWorkingSetResult canDeleteWorkingSet(DeleteWorkingSetRequest request) {
		String workingSetId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete working set {0}.", workingSetId);

		WorkingSet workingSet = WorkingSet.findById(em, workingSetId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewWorkingSet(ticket, workingSet);

		AuditWorkingSetDTO auditDto = auditMapper.mapWorkingSet(workingSet);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.WORKING_SET.toString(),
					null, ticket.getUserID(), auditDto);

		long countLockedByOther = WorkingSetVersion.countLockedByOtherUser(em, workingSetId, ticket.getUserID());
		if (countLockedByOther > 0) {
			CanDeleteWorkingSetResult result = new CanDeleteWorkingSetResult();
			result.setResult(false);

			result.setLockedByOtherUser(true);
			return result;
		}

		List<String> versionIds = WorkingSetVersion.findIdsByWorkingSetId(em, workingSetId);
		boolean consumersCanRemoveResource = consumersCanRemoveResources(versionIds);
		if (!consumersCanRemoveResource) {
			CanDeleteWorkingSetResult result = new CanDeleteWorkingSetResult();
			result.setResult(false);

			result.setUsedByOtherComponents(true);
			return result;
		}

		CanDeleteWorkingSetResult result = new CanDeleteWorkingSetResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteWorkingSet(DeleteWorkingSetRequest request) {
		String workingSetId = request.getId();

		LOGGER.log(Level.FINE, "Delete working set {0}.", workingSetId);

		WorkingSet workingSet = WorkingSet.findById(em, workingSetId);
		if (workingSet == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		long countLockedByOther = WorkingSetVersion.countLockedByOtherUser(em, workingSetId, ticket.getUserID());
		if (countLockedByOther > 0) {
			throw new QInvalidActionException("This working set has versions locked by other users.");
		}

		List<String> versionIds = WorkingSetVersion.findIdsByWorkingSetId(em, workingSetId);
		boolean consumersCanRemoveResource = consumersCanRemoveResources(versionIds);
		if (!consumersCanRemoveResource) {
			throw new QInvalidActionException("This working set has versions used by other components.");
		}

		for (WorkingSetVersion version : workingSet.getVersions()) {
			knowledgeBaseUtil.destroyKnowledgeBase(ticket, version);
		}

		AuditWorkingSetDTO auditDto = auditMapper.mapWorkingSet(workingSet);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.WORKING_SET.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(workingSet);

		DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(workingSetId);
		securityService.deleteSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_DELETE, workingSetId);
	}

	// -- Working Set versions

	@ValidateTicket
	@Override
	public List<WorkingSetVersionIdentifierDTO> getProjectWorkingSetVersions(GetProjectWorkingSetVersionsRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Get working set versions for project {0}.", projectId);

		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findByProjectId(em, projectId);

		List<WorkingSetVersionIdentifierDTO> workingSetVersionDtos = new ArrayList<>();
		for (WorkingSetVersion version : workingSetVersions) {
			WorkingSet workingSet = version.getWorkingSet();
			if (securityUtils.canViewWorkingSet(ticket, workingSet)) {
				WorkingSetVersionIdentifierDTO versionDto = mapper.mapWorkingSetVersionIdentifier(version);
				workingSetVersionDtos.add(versionDto);
			}
		}

		return workingSetVersionDtos;
	}

	@ValidateTicket
	@Override
	public WorkingSetVersionIdentifierDTO getWorkingSetVersionIdentifier(GetWorkingSetVersionIdentifierRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get working set version {0} identifier.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewWorkingSet(ticket, workingSet);

		WorkingSetVersionIdentifierDTO versionDto = mapper.mapWorkingSetVersionIdentifier(version);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public String getWorkingSetVersionIdByName(GetWorkingSetVersionIdByNameRequest request) {
		String projectId = request.getProjectId();
		String workingSetName = request.getWorkingSetName();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get working set version id by project {0}, working set {1} and name {2}.",
				new Object[]{projectId, workingSetName, name});

		String versionId = WorkingSetVersion.findIdByName(em, projectId, workingSetName, name);

		return versionId;
	}

	@ValidateTicket
	@Override
	public WorkingSetVersionDTO getWorkingSetVersion(GetWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewWorkingSet(ticket, workingSet);

		WorkingSetVersionDTO versionDto = mapper.mapWorkingSetVersion(version, ticket);

		// fetch contained rules, data models, libraries

		List<RuleVersion> ruleVersions = RuleVersion.findByWorkingSetVersionId(em, versionId);
		List<RuleVersionDTO> ruleVersionDtos = mapper.mapRuleVersionSummaryList(ruleVersions, ticket);
		versionDto.setRules(ruleVersionDtos);

		List<DataModelVersion> modelVersions = DataModelVersion.findByWorkingSetVersionId(em, versionId);
		List<DataModelVersionDTO> modelVersionDtos = mapper.mapDataModelVersionSummaryList(modelVersions, ticket);
		versionDto.setDataModels(modelVersionDtos);

		List<LibraryVersion> libraryVersions = LibraryVersion.findByWorkingSetVersionId(em, versionId);
		List<LibraryVersionDTO> libraryVersionDtos = mapper.mapLibraryVersionSummaryList(libraryVersions, ticket);
		versionDto.setLibraries(libraryVersionDtos);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		auditDto.setRuleVersionIds(auditMapper.mapRuleVersionIdList(ruleVersions));
		auditDto.setDataModelVersionIds(auditMapper.mapDataModelVersionIdList(modelVersions));
		auditDto.setLibraryVersionIds(auditMapper.mapLibraryVersionIdList(libraryVersions));
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public CanGetWorkingSetVersionModelsJarResult canGetWorkingSetVersionDataModelsJar(GetWorkingSetVersionDataModelsJarRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can download models.jar for working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewWorkingSet(ticket, workingSet);

		// can optimize by checking project permission first
		List<String> notPermittedVersions = new ArrayList<>();
		List<DataModelVersion> dataModelVersions = DataModelVersion.findByWorkingSetVersionId(em, versionId);
		for (DataModelVersion dataModelVersion : dataModelVersions) {
			DataModel dataModel = dataModelVersion.getDataModel();
			if (!securityUtils.canViewDataModel(ticket, dataModel)) {
				notPermittedVersions.add(dataModel.getName() + " / " + dataModelVersion.getName());
			}
		}

		if (!notPermittedVersions.isEmpty()) {
			CanGetWorkingSetVersionModelsJarResult result = new CanGetWorkingSetVersionModelsJarResult();
			result.setResult(false);

			result.setPermissionDenied(true);
			result.setDataModelVersions(notPermittedVersions);
			return result;
		}

		CanGetWorkingSetVersionModelsJarResult result = new CanGetWorkingSetVersionModelsJarResult();
		result.setResult(true);
		return result;
	}

	@ValidateTicket
	@Override
	public byte[] getWorkingSetVersionDataModelsJar(GetWorkingSetVersionDataModelsJarRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Download models.jar for working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewWorkingSet(ticket, workingSet);

		VersionState state = version.getState();
		if (state != VersionState.TESTING && state != VersionState.FINAL) {
			throw new QInvalidActionException("Only enabled for testing and finalized versions provide a JAR file for the contained data models.");
		}

		checkCanViewDataModels(ticket, versionId);

		byte[] jar = version.getDataModelsJar();
		if (jar == null) {
			dataModelsJarUtil.createDataModelsJar(version);
			jar = version.getDataModelsJar();
		}

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW_MODELS_JAR.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return jar;
	}

	@ValidateTicket
	@Override
	public WorkingSetVersionDTO getWorkingSetVersionByWorkingSetAndName(GetWorkingSetVersionByWorkingSetAndNameRequest request) {
		String workingSetId = request.getWorkingSetId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get working set version by working set {0} and name {1}.", new Object[]{workingSetId, name});

		WorkingSetVersion version = WorkingSetVersion.findByWorkingSetAndName(em, workingSetId, name);
		if (version == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		WorkingSetVersionDTO versionDto = mapper.mapWorkingSetVersionSummary(version, ticket);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public String createWorkingSetVersion(CreateWorkingSetVersionRequest request) {
		String workingSetId = request.getWorkingSetId();
		WorkingSet workingSet = WorkingSet.findById(em, workingSetId); // load in full for security

		LOGGER.log(Level.FINE, "Create working set version for working set {0}.", workingSetId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		WorkingSetVersion version = new WorkingSetVersion();
		String versionId = version.getId();
		version.setWorkingSet(workingSet);

		version.setName(request.getName());
		version.setDescription(request.getDescription());

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

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		// XXX audit resources
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	private void copyFromBaseVersion(WorkingSetVersion version, String baseVersionId) {
		WorkingSetVersion baseVersion = WorkingSetVersion.findById(em, baseVersionId);
		if (baseVersion == null) {
			throw new IllegalArgumentException("Base working set version does not exist.");
		}

		WorkingSet workingSet = version.getWorkingSet();
		WorkingSet baseWorkingSet = baseVersion.getWorkingSet();
		if (!baseWorkingSet.getId().equals(workingSet.getId())) {
			throw new IllegalArgumentException("Base working set version does not belong to current working set.");
		}

		List<RuleVersion> ruleVersions = new ArrayList<>();
		List<RuleVersion> baseRuleVersions = RuleVersion.findByWorkingSetVersionId(em, baseVersionId);
		for (RuleVersion baseRuleVersion : baseRuleVersions) {
			ruleVersions.add(baseRuleVersion);
		}
		version.setRules(ruleVersions);

		List<DataModelVersion> modelVersions = new ArrayList<>();
		List<DataModelVersion> baseModelVersions = DataModelVersion.findByWorkingSetVersionId(em, baseVersionId);
		for (DataModelVersion baseModelVersion : baseModelVersions) {
			modelVersions.add(baseModelVersion);
		}
		version.setDataModels(modelVersions);

		List<LibraryVersion> libraryVersions = new ArrayList<>();
		List<LibraryVersion> baseLibraryVersions = LibraryVersion.findByWorkingSetVersionId(em, baseVersionId);
		for (LibraryVersion baseLibraryVersion : baseLibraryVersions) {
			libraryVersions.add(baseLibraryVersion);
		}
		version.setLibraries(libraryVersions);
	}

	private void updateWorkingSetVersion(SignedTicket ticket, WorkingSet workingSet, UpdateWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Update working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		versionStateUtils.checkWorkingSetVersionNotFinalized(version);
		versionStateUtils.checkCanModifyWorkingSetVersion(ticket.getUserID(), version);

		WorkingSet existingWorkingSet = version.getWorkingSet();
		if (!workingSet.getId().equals(existingWorkingSet.getId())) {
			throw new IllegalArgumentException("Working set version does not belong to working set.");
		}

		version.setDescription(request.getDescription());

		if (version.getState() == VersionState.TESTING) {
			checkCanUpdateEnabledForTestingWorkingSetVersion(request);
		}

		String projectId = workingSet.getProjectId();

		updateWorkingSetVersionRules(projectId, version, request);

		updateWorkingSetVersionDataModels(projectId, version, request);

		updateWorkingSetVersionLibraries(projectId, version, request);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		VersionState state = version.getState();
		if (state == VersionState.TESTING) {
			version.setDataModelsJar(null);
			knowledgeBaseUtil.destroyKnowledgeBase(ticket, version);
		}

		publishVersionEvent(ticket, Constants.EVENT_UPDATE, versionId);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		// XXX audit resources
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	private void updateWorkingSetVersionRules(String projectId, WorkingSetVersion version, UpdateWorkingSetVersionRequest request) {

		List<RuleVersion> ruleVersions = new ArrayList<>();
		if (request.getRuleVersionIds() != null) {
			for (String ruleVersionId : request.getRuleVersionIds()) {
				RuleVersion ruleVersion = em.find(RuleVersion.class, ruleVersionId);

				Rule rule = ruleVersion.getRule();
				if (!projectId.equals(rule.getProjectId())) {
					throw new IllegalArgumentException("Rule version does not belong to working set project.");
				}

				ruleVersions.add(ruleVersion);
			}
		}
		version.setRules(ruleVersions);
	}

	private void updateWorkingSetVersionDataModels(String projectId, WorkingSetVersion version, UpdateWorkingSetVersionRequest request) {

		List<DataModelVersion> modelVersions = new ArrayList<>();
		if (request.getDataModelVersionIds() != null) {
			for (String modelVersionId : request.getDataModelVersionIds()) {
				DataModelVersion modelVersion = em.find(DataModelVersion.class, modelVersionId);

				DataModel model = modelVersion.getDataModel();
				if (!projectId.equals(model.getProjectId())) {
					throw new IllegalArgumentException("Data model version does not belong to working set project.");
				}

				modelVersions.add(modelVersion);
			}
		}
		version.setDataModels(modelVersions);
	}

	private void updateWorkingSetVersionLibraries(String projectId, WorkingSetVersion version, UpdateWorkingSetVersionRequest request) {

		List<LibraryVersion> libraryVersions = new ArrayList<>();
		if (request.getLibraryVersionIds() != null) {
			for (String libraryVersionId : request.getLibraryVersionIds()) {
				LibraryVersion libraryVersion = em.find(LibraryVersion.class, libraryVersionId);

				Library library = libraryVersion.getLibrary();
				if (!projectId.equals(library.getProjectId())) {
					throw new IllegalArgumentException("Library version does not belong to working set project.");
				}

				libraryVersions.add(libraryVersion);
			}
		}
		version.setLibraries(libraryVersions);
	}

	@ValidateTicket
	@Override
	public CanDeleteWorkingSetVersionResult canDeleteWorkingSetVersion(DeleteWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		boolean consumersCanRemoveResource = consumersCanRemoveResource(versionId);
		if (!consumersCanRemoveResource) {
			CanDeleteWorkingSetVersionResult result = new CanDeleteWorkingSetVersionResult();
			result.setResult(false);

			result.setUsedByOtherComponents(true);
			return result;
		}

		CanDeleteWorkingSetVersionResult result = new CanDeleteWorkingSetVersionResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteWorkingSetVersion(DeleteWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Delete working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		versionStateUtils.checkCanModifyWorkingSetVersion(ticket.getUserID(), version);

		boolean consumersCanRemoveResource = consumersCanRemoveResource(versionId);
		if (!consumersCanRemoveResource) {
			throw new QInvalidActionException("The working set version is used by other components.");
		}

		knowledgeBaseUtil.destroyKnowledgeBase(ticket, version);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.WORKING_SET_VERSION.toString(),
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
			if (!consumer.canRemoveResource(versionId, ResourceType.WORKING_SET_VERSION)) {
				return false;
			}
		}

		return true;
	}

	@ValidateTicket
	@Override
	public void lockWorkingSetVersion(LockWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Lock working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanLockWorkingSetVersion(ticket, workingSet);

		versionStateUtils.checkWorkingSetVersionNotFinalized(version);
		versionStateUtils.checkCanLockWorkingSetVersion(ticket.getUserID(), version);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLockedBy(ticket.getUserID());
		version.setLockedOn(millis);

		publishVersionEvent(ticket, Constants.EVENT_LOCK, versionId);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.LOCK.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void unlockWorkingSetVersion(UnlockWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Unlock working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUnlockWorkingSetVersion(ticket, workingSet);

		versionStateUtils.checkWorkingSetVersionNotFinalized(version);

		boolean canUnlockAny = securityUtils.canUnlockAnyWorkingSetVersion(ticket, workingSet);
		versionStateUtils.checkCanUnlockWorkingSetVersion(ticket.getUserID(), canUnlockAny, version);

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_UNLOCK, versionId);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UNLOCK.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	// -- EnableTesting / Finalize (cascadable)

	@ValidateTicket
	@Override
	public CanUpdateEnabledForTestingWorkingSetResult canUpdateEnabledForTestingWorkingSetVersion(UpdateWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can update enabled-for-testing working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		if (version.getState() != VersionState.TESTING) {
			CanUpdateEnabledForTestingWorkingSetResult result = new CanUpdateEnabledForTestingWorkingSetResult();
			result.setResult(true);
			return result;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		List<String> ruleVersionIds = request.getRuleVersionIds();
		List<String> dataModelVersionIds = request.getDataModelVersionIds();
		List<String> libraryVersionIds = request.getLibraryVersionIds();

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		auditDto.setRuleVersionIds(ruleVersionIds);
		auditDto.setDataModelVersionIds(dataModelVersionIds);
		auditDto.setLibraryVersionIds(libraryVersionIds);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_UPDATE.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		// Check contained data model dependencies
		List<String> notContainedDataModelVersionIds = modelsService.checkDataModelVersionClosureContained(dataModelVersionIds);
		if (!notContainedDataModelVersionIds.isEmpty()) {
			CanUpdateEnabledForTestingWorkingSetResult result = new CanUpdateEnabledForTestingWorkingSetResult();
			result.setResult(false);

			result.setIncomplete(true);

			List<String> notContainedDataModelVersionNames = new ArrayList<>();
			for (String notContainedDataModelVersionId : notContainedDataModelVersionIds) {
				DataModelVersion dataModelVersion = DataModelVersion.findById(em, notContainedDataModelVersionId);
				notContainedDataModelVersionNames.add(dataModelVersion.getDataModel().getName() + " / " + dataModelVersion.getName());
			}
			result.setDataModelVersions(notContainedDataModelVersionNames);
			return result;
		}

		// Load contained resources in full
		List<RuleVersion> ruleVersions = new ArrayList<>();
		List<DataModelVersion> dataModelVersions = new ArrayList<>();
		List<LibraryVersion> libraryVersions = new ArrayList<>();

		for (String ruleVersionId : ruleVersionIds) {
			RuleVersion ruleVersion = em.find(RuleVersion.class, ruleVersionId);
			ruleVersions.add(ruleVersion);
		}

		for (String dataModelVersionId : dataModelVersionIds) {
			DataModelVersion dataModelVersion = em.find(DataModelVersion.class, dataModelVersionId);
			dataModelVersions.add(dataModelVersion);
		}

		for (String libraryVersionId : libraryVersionIds) {
			LibraryVersion libraryVersion = em.find(LibraryVersion.class, libraryVersionId);
			libraryVersions.add(libraryVersion);
		}

		// Report not enabled-for-testing contained resources
		// No need to check data models recursively, since dependencies should be contained
		List<RuleVersion> notTestingRuleVersions = findNotTestingRuleVersions(ruleVersions);
		List<DataModelVersion> notTestingDataModelVersions = findNotTestingDataModelVersions(dataModelVersions);
		List<LibraryVersion> notTestingLibraryVersions = findNotTestingLibraryVersions(libraryVersions);

		if (!notTestingRuleVersions.isEmpty()
				|| !notTestingDataModelVersions.isEmpty()
				|| !notTestingLibraryVersions.isEmpty()) {

			CanUpdateEnabledForTestingWorkingSetResult result = new CanUpdateEnabledForTestingWorkingSetResult();
			result.setResult(false);

			result.setRestrict(true);

			List<String> ruleVersionNames = new ArrayList<>();
			for (RuleVersion ruleVersion : notTestingRuleVersions) {
				ruleVersionNames.add(ruleVersion.getRule().getName() + " / " + ruleVersion.getName());
			}
			result.setRuleVersions(ruleVersionNames);

			List<String> dataModelVersionNames = new ArrayList<>();
			for (DataModelVersion dataModelVersion : notTestingDataModelVersions) {
				dataModelVersionNames.add(dataModelVersion.getDataModel().getName() + " / " + dataModelVersion.getName());
			}
			result.setDataModelVersions(dataModelVersionNames);

			List<String> libraryVersionNames = new ArrayList<>();
			for (LibraryVersion libraryVersion : notTestingLibraryVersions) {
				libraryVersionNames.add(libraryVersion.getLibrary().getName() + " / " + libraryVersion.getName());
			}
			result.setLibraryVersions(libraryVersionNames);

			return result;
		}

		CanUpdateEnabledForTestingWorkingSetResult result = new CanUpdateEnabledForTestingWorkingSetResult();
		result.setResult(true);
		return result;
	}

	private void checkCanUpdateEnabledForTestingWorkingSetVersion(UpdateWorkingSetVersionRequest request) {
		List<String> ruleVersionIds = request.getRuleVersionIds();
		List<String> dataModelVersionIds = request.getDataModelVersionIds();
		List<String> libraryVersionIds = request.getLibraryVersionIds();

		// Check contained data model dependencies
		List<String> notContainedDataModelVersionIds = modelsService.checkDataModelVersionClosureContained(dataModelVersionIds);
		if (!notContainedDataModelVersionIds.isEmpty()) {
			throw new QInvalidActionException("Not all data model dependencies are contained in the working set");
		}

		// Load contained resources in full
		List<RuleVersion> ruleVersions = new ArrayList<>();
		List<DataModelVersion> dataModelVersions = new ArrayList<>();
		List<LibraryVersion> libraryVersions = new ArrayList<>();

		for (String ruleVersionId : ruleVersionIds) {
			RuleVersion ruleVersion = em.find(RuleVersion.class, ruleVersionId);
			ruleVersions.add(ruleVersion);
		}

		for (String dataModelVersionId : dataModelVersionIds) {
			DataModelVersion dataModelVersion = em.find(DataModelVersion.class, dataModelVersionId);
			dataModelVersions.add(dataModelVersion);
		}

		for (String libraryVersionId : libraryVersionIds) {
			LibraryVersion libraryVersion = em.find(LibraryVersion.class, libraryVersionId);
			libraryVersions.add(libraryVersion);
		}

		// Check not enabled-for-testing contained resources
		// No need to check data models recursively, since dependencies should be contained
		List<RuleVersion> notTestingRuleVersions = findNotTestingRuleVersions(ruleVersions);
		List<DataModelVersion> notTestingDataModelVersions = findNotTestingDataModelVersions(dataModelVersions);
		List<LibraryVersion> notTestingLibraryVersions = findNotTestingLibraryVersions(libraryVersions);

		if (!notTestingRuleVersions.isEmpty()) {
			throw new QInvalidActionException("Not all rule versions contained in the working set are enabled-for-testing");
		}

		if (!notTestingDataModelVersions.isEmpty()) {
			throw new QInvalidActionException("Not all data model versions contained in the working set are enabled-for-testing");
		}

		if (!notTestingLibraryVersions.isEmpty()) {
			throw new QInvalidActionException("Not all library versions contained in the working set are enabled-for-testing");
		}
	}

	@ValidateTicket
	@Override
	public CanEnableTestingWorkingSetResult canEnableTestingWorkingSetVersion(EnableTestingWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can enable testing working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		// Load contained resources
		List<RuleVersion> ruleVersions = RuleVersion.findByWorkingSetVersionId(em, versionId);
		List<DataModelVersion> dataModelVersions = DataModelVersion.findByWorkingSetVersionId(em, versionId);
		List<LibraryVersion> libraryVersions = LibraryVersion.findByWorkingSetVersionId(em, versionId);

		List<String> ruleVersionIds = new ArrayList<>();
		for (RuleVersion ruleVersion : ruleVersions) {
			ruleVersionIds.add(ruleVersion.getId());
		}

		List<String> dataModelVersionIds = new ArrayList<>();
		for (DataModelVersion dataModelVersion : dataModelVersions) {
			dataModelVersionIds.add(dataModelVersion.getId());
		}

		List<String> libraryVersionIds = new ArrayList<>();
		for (LibraryVersion libraryVersion : libraryVersions) {
			libraryVersionIds.add(libraryVersion.getId());
		}

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		auditDto.setRuleVersionIds(ruleVersionIds);
		auditDto.setDataModelVersionIds(dataModelVersionIds);
		auditDto.setLibraryVersionIds(libraryVersionIds);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_ENABLE_TESTING.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		// Check contained data model dependencies
		List<String> notContainedDataModelVersionIds = modelsService.checkDataModelVersionClosureContained(dataModelVersionIds);
		if (!notContainedDataModelVersionIds.isEmpty()) {
			CanEnableTestingWorkingSetResult result = new CanEnableTestingWorkingSetResult();
			result.setResult(false);

			result.setIncomplete(true);

			List<String> notContainedDataModelVersionNames = new ArrayList<>();
			for (String notContainedDataModelVersionId : notContainedDataModelVersionIds) {
				DataModelVersion dataModelVersion = DataModelVersion.findById(em, notContainedDataModelVersionId);
				notContainedDataModelVersionNames.add(dataModelVersion.getDataModel().getName() + " / " + dataModelVersion.getName());
			}
			result.setDataModelVersions(notContainedDataModelVersionNames);
			return result;
		}

		// Check permissions on contained resources
		// No need to check data models recursively, since dependencies should be contained
		boolean canModifyRules = rulesService.canModifyRuleVersionIdList(ticket, ruleVersionIds);
		if (!canModifyRules) {
			CanEnableTestingWorkingSetResult result = new CanEnableTestingWorkingSetResult();
			result.setResult(false);
			return result;
		}

		boolean canModifyDataModels = modelsService.canModifyDataModelVersionIdList(ticket, dataModelVersionIds);
		if (!canModifyDataModels) {
			CanEnableTestingWorkingSetResult result = new CanEnableTestingWorkingSetResult();
			result.setResult(false);
			return result;
		}

		boolean canModifyLibraries = libraryVersionService.canModifyLibraryVersionIdList(ticket, libraryVersionIds);
		if (!canModifyLibraries) {
			CanEnableTestingWorkingSetResult result = new CanEnableTestingWorkingSetResult();
			result.setResult(false);
			return result;
		}

		// Report not enabled-for-testing contained resources
		// No need to check data models recursively, since dependencies should be contained
		List<RuleVersion> notTestingRuleVersions = findNotTestingRuleVersions(ruleVersions);
		List<DataModelVersion> notTestingDataModelVersions = findNotTestingDataModelVersions(dataModelVersions);
		List<LibraryVersion> notTestingLibraryVersions = findNotTestingLibraryVersions(libraryVersions);

		if (!notTestingRuleVersions.isEmpty()
				|| !notTestingDataModelVersions.isEmpty()
				|| !notTestingLibraryVersions.isEmpty()) {

			CanEnableTestingWorkingSetResult result = new CanEnableTestingWorkingSetResult();
			result.setResult(true);

			result.setCascade(true);

			List<String> ruleVersionNames = new ArrayList<>();
			for (RuleVersion ruleVersion : notTestingRuleVersions) {
				ruleVersionNames.add(ruleVersion.getRule().getName() + " / " + ruleVersion.getName());
			}
			result.setRuleVersions(ruleVersionNames);

			List<String> dataModelVersionNames = new ArrayList<>();
			for (DataModelVersion dataModelVersion : notTestingDataModelVersions) {
				dataModelVersionNames.add(dataModelVersion.getDataModel().getName() + " / " + dataModelVersion.getName());
			}
			result.setDataModelVersions(dataModelVersionNames);

			List<String> libraryVersionNames = new ArrayList<>();
			for (LibraryVersion libraryVersion : notTestingLibraryVersions) {
				libraryVersionNames.add(libraryVersion.getLibrary().getName() + " / " + libraryVersion.getName());
			}
			result.setLibraryVersions(libraryVersionNames);

			return result;
		}

		CanEnableTestingWorkingSetResult result = new CanEnableTestingWorkingSetResult();
		result.setResult(true);
		return result;
	}

	@ValidateTicket
	@Override
	public CanDisableTestingWorkingSetResult canDisableTestingWorkingSetVersion(EnableTestingWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can disable testing working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DISABLE_TESTING.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		boolean consumersCanRemoveResource = consumersCanRemoveResource(versionId);
		if (!consumersCanRemoveResource) {
			CanDisableTestingWorkingSetResult result = new CanDisableTestingWorkingSetResult();
			result.setResult(false);

			result.setUsedByOtherComponents(true);
			return result;
		}

		CanDisableTestingWorkingSetResult result = new CanDisableTestingWorkingSetResult();
		result.setResult(true);
		return result;
	}

	@ValidateTicket
	@Override
	public CanFinalizeWorkingSetResult canFinalizeWorkingSetVersion(FinalizeWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can finalize working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		// Load contained resources
		List<RuleVersion> ruleVersions = RuleVersion.findByWorkingSetVersionId(em, versionId);
		List<DataModelVersion> dataModelVersions = DataModelVersion.findByWorkingSetVersionId(em, versionId);
		List<LibraryVersion> libraryVersions = LibraryVersion.findByWorkingSetVersionId(em, versionId);

		List<String> ruleVersionIds = new ArrayList<>();
		for (RuleVersion ruleVersion : ruleVersions) {
			ruleVersionIds.add(ruleVersion.getId());
		}

		List<String> dataModelVersionIds = new ArrayList<>();
		for (DataModelVersion dataModelVersion : dataModelVersions) {
			dataModelVersionIds.add(dataModelVersion.getId());
		}

		List<String> libraryVersionIds = new ArrayList<>();
		for (LibraryVersion libraryVersion : libraryVersions) {
			libraryVersionIds.add(libraryVersion.getId());
		}

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		auditDto.setRuleVersionIds(ruleVersionIds);
		auditDto.setDataModelVersionIds(dataModelVersionIds);
		auditDto.setLibraryVersionIds(libraryVersionIds);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_FINALISE.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		// Check contained data model dependencies
		List<String> notContainedDataModelVersionIds = modelsService.checkDataModelVersionClosureContained(dataModelVersionIds);
		if (!notContainedDataModelVersionIds.isEmpty()) {
			CanFinalizeWorkingSetResult result = new CanFinalizeWorkingSetResult();
			result.setResult(false);

			result.setIncomplete(true);

			List<String> notContainedDataModelVersionNames = new ArrayList<>();
			for (String notContainedDataModelVersionId : notContainedDataModelVersionIds) {
				DataModelVersion dataModelVersion = DataModelVersion.findById(em, notContainedDataModelVersionId);
				notContainedDataModelVersionNames.add(dataModelVersion.getDataModel().getName() + " / " + dataModelVersion.getName());
			}
			result.setDataModelVersions(notContainedDataModelVersionNames);
			return result;
		}

		// Check permissions on contained resources
		// No need to check data models recursively, since dependencies should be contained
		boolean canFinalizeRules = rulesService.canModifyRuleVersionIdList(ticket, ruleVersionIds);
		if (!canFinalizeRules) {
			CanFinalizeWorkingSetResult result = new CanFinalizeWorkingSetResult();
			result.setResult(false);
			return result;
		}

		boolean canFinalizeDataModels = modelsService.canModifyDataModelVersionIdList(ticket, dataModelVersionIds);
		if (!canFinalizeDataModels) {
			CanFinalizeWorkingSetResult result = new CanFinalizeWorkingSetResult();
			result.setResult(false);
			return result;
		}

		boolean canFinalizeLibraries = libraryVersionService.canModifyLibraryVersionIdList(ticket, libraryVersionIds);
		if (!canFinalizeLibraries) {
			CanFinalizeWorkingSetResult result = new CanFinalizeWorkingSetResult();
			result.setResult(false);
			return result;
		}

		// Report not finalized contained resources
		// No need to check data models recursively, since dependencies should be contained
		List<RuleVersion> notFinalizedRuleVersions = findNotFinalizedRuleVersions(ruleVersions);
		List<DataModelVersion> notFinalizedDataModelVersions = findNotFinalizedDataModelVersions(dataModelVersions);
		List<LibraryVersion> notFinalizedLibraryVersions = findNotFinalizedLibraryVersions(libraryVersions);

		if (!notFinalizedRuleVersions.isEmpty()
				|| !notFinalizedDataModelVersions.isEmpty()
				|| !notFinalizedLibraryVersions.isEmpty()) {

			CanFinalizeWorkingSetResult result = new CanFinalizeWorkingSetResult();
			result.setResult(true);

			result.setCascade(true);

			List<String> ruleVersionNames = new ArrayList<>();
			for (RuleVersion ruleVersion : notFinalizedRuleVersions) {
				ruleVersionNames.add(ruleVersion.getRule().getName() + " / " + ruleVersion.getName());
			}
			result.setRuleVersions(ruleVersionNames);

			List<String> dataModelVersionNames = new ArrayList<>();
			for (DataModelVersion dataModelVersion : notFinalizedDataModelVersions) {
				dataModelVersionNames.add(dataModelVersion.getDataModel().getName() + " / " + dataModelVersion.getName());
			}
			result.setDataModelVersions(dataModelVersionNames);

			List<String> libraryVersionNames = new ArrayList<>();
			for (LibraryVersion libraryVersion : notFinalizedLibraryVersions) {
				libraryVersionNames.add(libraryVersion.getLibrary().getName() + " / " + libraryVersion.getName());
			}
			result.setLibraryVersions(libraryVersionNames);

			return result;
		}

		CanFinalizeWorkingSetResult result = new CanFinalizeWorkingSetResult();
		result.setResult(true);
		return result;
	}

	@ValidateTicket
	@Override
	public void enableTestingWorkingSetVersion(EnableTestingWorkingSetVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();
		boolean enableTesting = request.isEnableTesting();

		LOGGER.log(Level.FINE, "Enable testing working set version {0} ({1}).", new Object[]{versionId, enableTesting});

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		if (enableTesting) {
			// Load contained resources
			List<RuleVersion> ruleVersions = RuleVersion.findByWorkingSetVersionId(em, versionId);
			List<DataModelVersion> dataModelVersions = DataModelVersion.findByWorkingSetVersionId(em, versionId);
			List<LibraryVersion> libraryVersions = LibraryVersion.findByWorkingSetVersionId(em, versionId);

			List<String> ruleVersionIds = new ArrayList<>();
			for (RuleVersion ruleVersion : ruleVersions) {
				ruleVersionIds.add(ruleVersion.getId());
			}

			List<String> dataModelVersionIds = new ArrayList<>();
			for (DataModelVersion dataModelVersion : dataModelVersions) {
				dataModelVersionIds.add(dataModelVersion.getId());
			}

			List<String> libraryVersionIds = new ArrayList<>();
			for (LibraryVersion libraryVersion : libraryVersions) {
				libraryVersionIds.add(libraryVersion.getId());
			}

			// Check contained data model dependencies
			List<String> notContainedDataModelVersionIds = modelsService.checkDataModelVersionClosureContained(dataModelVersionIds);
			if (!notContainedDataModelVersionIds.isEmpty()) {
				throw new QInvalidActionException("Not all data model dependencies are contained in the working set");
			}

			// Find not enabled-for-testing contained resources
			// No need to check data models recursively, since dependencies should be contained
			List<RuleVersion> notTestingRuleVersions = findNotTestingRuleVersions(ruleVersions);
			List<DataModelVersion> notTestingDataModelVersions = findNotTestingDataModelVersions(dataModelVersions);
			List<LibraryVersion> notTestingLibraryVersions = findNotTestingLibraryVersions(libraryVersions);

			for (RuleVersion ruleVersion : notTestingRuleVersions) {
				EnableTestingRuleVersionRequest ruleVersionRequest = new EnableTestingRuleVersionRequest();
				ruleVersionRequest.setId(ruleVersion.getId());
				ruleVersionRequest.setEnableTesting(true);
				ruleVersionRequest.setSignedTicket(ticket);

				rulesService.enableTestingRuleVersion(ruleVersionRequest);
			}

			for (DataModelVersion dataModelVersion : notTestingDataModelVersions) {
				EnableTestingDataModelVersionRequest dataModelVersionRequest = new EnableTestingDataModelVersionRequest();
				dataModelVersionRequest.setId(dataModelVersion.getId());
				dataModelVersionRequest.setEnableTesting(true);
				dataModelVersionRequest.setSignedTicket(ticket);

				modelsService.enableTestingDataModelVersionNoCascade(dataModelVersionRequest);
			}

			for (LibraryVersion libraryVersion : notTestingLibraryVersions) {
				EnableTestingLibraryVersionRequest libraryVersionRequest = new EnableTestingLibraryVersionRequest();
				libraryVersionRequest.setId(libraryVersion.getId());
				libraryVersionRequest.setEnableTesting(true);
				libraryVersionRequest.setSignedTicket(ticket);

				libraryVersionService.enableTestingLibraryVersion(libraryVersionRequest);
			}

			// enable testing working set
			enableTestingWorkingSetVersionSingle(ticket, version, true);
		}
		else {
			boolean consumersCanRemoveResource = consumersCanRemoveResource(versionId);
			if (!consumersCanRemoveResource) {
				throw new QInvalidActionException("The working set version is used by other QBE components.");
			}

			enableTestingWorkingSetVersionSingle(ticket, version, false);

			version.setDataModelsJar(null);
			knowledgeBaseUtil.destroyKnowledgeBase(ticket, version);
		}
	}

	private void enableTestingWorkingSetVersionSingle(SignedTicket ticket, WorkingSetVersion version, boolean enableTesting) {
		String versionId = version.getId();

		WorkingSet workingSet = version.getWorkingSet();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		versionStateUtils.checkWorkingSetVersionNotFinalized(version);
		versionStateUtils.checkCanModifyWorkingSetVersion(ticket.getUserID(), version);

		if (enableTesting) {
			version.setState(VersionState.TESTING);
		}
		else {
			version.setState(VersionState.DRAFT);
		}

		String stringEvent = enableTesting ? Constants.EVENT_ENABLE_TESTING : Constants.EVENT_DISABLE_TESTING;
		publishVersionEvent(ticket, stringEvent, versionId);

		EVENT event = enableTesting ? EVENT.ENABLE_TESTING : EVENT.DISABLE_TESTING;
		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), event.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void finalizeWorkingSetVersion(FinalizeWorkingSetVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Finalize working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		// Load contained resources
		List<RuleVersion> ruleVersions = RuleVersion.findByWorkingSetVersionId(em, versionId);
		List<DataModelVersion> dataModelVersions = DataModelVersion.findByWorkingSetVersionId(em, versionId);
		List<LibraryVersion> libraryVersions = LibraryVersion.findByWorkingSetVersionId(em, versionId);

		List<String> ruleVersionIds = new ArrayList<>();
		for (RuleVersion ruleVersion : ruleVersions) {
			ruleVersionIds.add(ruleVersion.getId());
		}

		List<String> dataModelVersionIds = new ArrayList<>();
		for (DataModelVersion dataModelVersion : dataModelVersions) {
			dataModelVersionIds.add(dataModelVersion.getId());
		}

		List<String> libraryVersionIds = new ArrayList<>();
		for (LibraryVersion libraryVersion : libraryVersions) {
			libraryVersionIds.add(libraryVersion.getId());
		}

		// Check contained data model dependencies
		List<String> notContainedDataModelVersionIds = modelsService.checkDataModelVersionClosureContained(dataModelVersionIds);
		if (!notContainedDataModelVersionIds.isEmpty()) {
			throw new QInvalidActionException("Not all data model dependencies are contained in the working set");
		}

		// Find not finalized contained resources
		// No need to check data models recursively, since dependencies should be contained
		List<RuleVersion> notFinalizedRuleVersions = findNotFinalizedRuleVersions(ruleVersions);
		List<DataModelVersion> notFinalizedDataModelVersions = findNotFinalizedDataModelVersions(dataModelVersions);
		List<LibraryVersion> notFinalizedLibraryVersions = findNotFinalizedLibraryVersions(libraryVersions);

		for (RuleVersion ruleVersion : notFinalizedRuleVersions) {
			FinalizeRuleVersionRequest ruleVersionRequest = new FinalizeRuleVersionRequest();
			ruleVersionRequest.setId(ruleVersion.getId());
			ruleVersionRequest.setSignedTicket(ticket);

			rulesService.finalizeRuleVersion(ruleVersionRequest);
		}

		for (DataModelVersion dataModelVersion : notFinalizedDataModelVersions) {
			FinalizeDataModelVersionRequest dataModelVersionRequest = new FinalizeDataModelVersionRequest();
			dataModelVersionRequest.setId(dataModelVersion.getId());
			dataModelVersionRequest.setSignedTicket(ticket);

			modelsService.finalizeDataModelVersionNoCascade(dataModelVersionRequest);
		}

		for (LibraryVersion libraryVersion : notFinalizedLibraryVersions) {
			FinaliseLibraryVersionRequest libraryVersionRequest = new FinaliseLibraryVersionRequest();
			libraryVersionRequest.setId(libraryVersion.getId());
			libraryVersionRequest.setSignedTicket(ticket);

			libraryVersionService.finaliseLibraryVersion(libraryVersionRequest);
		}

		// Finalize working set
		finalizeWorkingSetVersionSingle(ticket, version);
	}

	private void finalizeWorkingSetVersionSingle(SignedTicket ticket, WorkingSetVersion version) {
		WorkingSet workingSet = version.getWorkingSet();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		versionStateUtils.checkWorkingSetVersionNotFinalized(version);
		versionStateUtils.checkCanModifyWorkingSetVersion(ticket.getUserID(), version);

		version.setState(VersionState.FINAL);

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_FINALISE, version.getId());

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.FINALISE.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public byte[] exportWorkingSetVersion(ExportWorkingSetVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Export working set version {0}.", versionId);

		WorkingSetVersion version = WorkingSetVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		WorkingSet workingSet = version.getWorkingSet();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewWorkingSet(ticket, workingSet);

		if (version.getState() != VersionState.FINAL) {
			throw new QInvalidActionException("Version is not finalized.");
		}

		checkCanViewRules(ticket, versionId);
		checkCanViewDataModels(ticket, versionId);
		checkCanViewLibraries(ticket, versionId);

		XmlWorkingSetVersionDTO xmlVersionDto = xmlMapper.mapWorkingSet(version);

		byte[] xml = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// XXX generated XML is immutable, generate once and cache ?
			JAXBContext jaxbContext = JAXBContext.newInstance(XmlWorkingSetVersionDTO.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(xmlVersionDto, baos);

			xml = baos.toByteArray();
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot export working set version.", e);
		}

		publishVersionEvent(ticket, Constants.EVENT_EXPORT, versionId);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		// XXX audit resources
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.EXPORT.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return xml;
	}

	@ValidateTicket
	@Override
	public String importWorkingSetVersion(ImportWorkingSetVersionRequest request) {
		String workingSetId = request.getWorkingSetId();
		byte[] xml = request.getXml();

		LOGGER.log(Level.FINE, "Import working set version in working set {0}.", workingSetId);

		WorkingSet workingSet = WorkingSet.findById(em, workingSetId);
		if (workingSet == null) {
			throw new IllegalArgumentException("Working set does not exist");
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateWorkingSet(ticket, workingSet);

		XmlWorkingSetVersionDTO xmlVersion = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xml);

			JAXBContext jaxbContext = JAXBContext.newInstance(XmlWorkingSetVersionDTO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			xmlVersion = (XmlWorkingSetVersionDTO) jaxbUnmarshaller.unmarshal(bais);
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot import working set version.", e);
		}

		WorkingSetVersion existingWorkingSetVersion = WorkingSetVersion.findByWorkingSetAndName(em, workingSetId, xmlVersion.getName());
		if (existingWorkingSetVersion != null) {
			throw new QImportExportException("Another working set version with the same name already exists.");
		}

		WorkingSetVersion version = new WorkingSetVersion();
		String versionId = version.getId();
		version.setWorkingSet(workingSet);

		version.setName(xmlVersion.getName());
		version.setDescription(xmlVersion.getDescription());

		String projectId = workingSet.getProjectId();

		List<RuleVersion> ruleVersions = importRuleVersions(ticket, projectId, xmlVersion.getRuleVersions().getRuleVersions());
		version.setRules(ruleVersions);

		checkRulesUnique(ruleVersions);

		List<DataModelVersion> dataModelVersions = importDataModelVersions(ticket, projectId, xmlVersion.getDataModelVersions().getDataModelVersions());
		version.setDataModels(dataModelVersions);

		checkDataModelsUnique(dataModelVersions);

		List<LibraryVersion> libraryVersions = importLibraryVersions(ticket, projectId, xmlVersion.getLibraryVersions().getLibraryVersions());
		version.setLibraries(libraryVersions);

		checkLibrariesUnique(libraryVersions);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setState(VersionState.DRAFT);
		version.setCreatedBy(ticket.getUserID());
		version.setCreatedOn(millis);
		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		em.persist(version);

		publishVersionEvent(ticket, Constants.EVENT_IMPORT, versionId);

		AuditWorkingSetVersionDTO auditDto = auditMapper.mapWorkingSetVersion(version);
		// XXX audit resources
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.IMPORT.toString(), GROUP.WORKING_SET_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	// -- Helpers

	private List<RuleVersion> importRuleVersions(SignedTicket ticket, String projectId, List<XmlRuleVersionDTO> xmlRuleVersions) {
		List<RuleVersion> ruleVersions = new ArrayList<>();

		if (xmlRuleVersions == null) {
			return ruleVersions;
		}

		for (XmlRuleVersionDTO xmlRuleVersion : xmlRuleVersions) {
			String ruleName = xmlRuleVersion.getRuleName();
			String ruleVersionName = xmlRuleVersion.getName();

			Rule rule = Rule.findByProjectAndName(em, projectId, ruleName);
			if (rule == null) {
				throw new QImportExportException("Cannot find rule " + ruleName + " in project " + projectId);
			}

			String ruleId = rule.getId();

			RuleVersion existingRuleVersion = RuleVersion.findByRuleAndName(em, ruleId, ruleVersionName);
			if (existingRuleVersion != null) {
				LOGGER.log(Level.FINE, "Rule version {0} / {1} exists, skipping import", new Object[]{ruleName, ruleVersionName});
				ruleVersions.add(existingRuleVersion);
			}
			else {
				LOGGER.log(Level.FINE, "Import rule version {0} / {1}", new Object[]{ruleName, ruleVersionName});

				securityUtils.checkCanUpdateRule(ticket, rule);

				RuleVersion ruleVersion = xmlMapper.mapRuleVersion(ticket, rule, xmlRuleVersion);
				xmlMapper.computeDroolsRuleName(em, projectId, ruleId, ruleVersion, xmlRuleVersion);

				em.persist(ruleVersion);

				ruleVersions.add(ruleVersion);
			}
		}

		return ruleVersions;
	}

	private List<DataModelVersion> importDataModelVersions(SignedTicket ticket, String projectId, List<XmlDataModelVersionDTO> xmlDataModelVersions) {
		List<DataModelVersion> dataModelVersions = new ArrayList<>();

		if (xmlDataModelVersions == null) {
			return dataModelVersions;
		}

		List<XmlDataModelVersionDTO> newXmlDataModelVersions = new ArrayList<>();

		for (XmlDataModelVersionDTO xmlDataModelVersion : xmlDataModelVersions) {
			String modelName = xmlDataModelVersion.getDataModelName();
			String modelVersionName = xmlDataModelVersion.getName();

			DataModel model = DataModel.findByProjectAndName(em, projectId, modelName);
			if (model == null) {
				throw new QImportExportException("Cannot find data model " + modelName + " in project " + projectId);
			}

			String modelId = model.getId();

			DataModelVersion existingDataModelVersion = DataModelVersion.findByDataModelAndName(em, modelId, modelVersionName);
			if (existingDataModelVersion != null) {
				LOGGER.log(Level.FINE, "Data model version {0} / {1} exists, skipping import", new Object[]{modelName, modelVersionName});
				dataModelVersions.add(existingDataModelVersion);
			}
			else {
				LOGGER.log(Level.FINE, "Import data model version {0} / {1}", new Object[]{modelName, modelVersionName});

				securityUtils.checkCanUpdateDataModel(ticket, model);

				DataModelVersion modelVersion = xmlMapper.mapDataModelVersion(ticket, model, xmlDataModelVersion);

				em.persist(modelVersion);

				newXmlDataModelVersions.add(xmlDataModelVersion);

				dataModelVersions.add(modelVersion);
			}
		}

		for (XmlDataModelVersionDTO xmlDataModelVersion : newXmlDataModelVersions) {
			String modelName = xmlDataModelVersion.getDataModelName();
			String modelVersionName = xmlDataModelVersion.getName();

			DataModel model = DataModel.findByProjectAndName(em, projectId, modelName);

			DataModelVersion modelVersion = DataModelVersion.findByDataModelAndName(em, model.getId(), modelVersionName);

			xmlMapper.mapDataModelVersionParent(em, projectId, modelVersion, xmlDataModelVersion);
			DataModelVersion parentModelVersion = modelVersion.getParentModel();
			if (parentModelVersion != null) {
				boolean hasCycle = hasInheritanceCycle(modelVersion.getId(), parentModelVersion);
				if (hasCycle) {
					throw new QImportExportException("The imported data model versions have inheritance cycles.");
				}
			}

			xmlMapper.mapDataModelVersionFields(em, projectId, modelVersion, xmlDataModelVersion);
		}

		return dataModelVersions;
	}

	private boolean hasInheritanceCycle(String versionId, DataModelVersion parentVersion) {
		DataModelVersion currentParentVersion = parentVersion;
		while (currentParentVersion != null) {
			if (currentParentVersion.getId().equals(versionId)) {
				return true;
			}

			currentParentVersion = currentParentVersion.getParentModel();
		}
		return false;
	}

	private List<LibraryVersion> importLibraryVersions(SignedTicket ticket, String projectId, List<XmlLibraryVersionDTO> xmlLibraryVersions) {
		List<LibraryVersion> libraryVersions = new ArrayList<>();

		if (xmlLibraryVersions == null) {
			return libraryVersions;
		}

		for (XmlLibraryVersionDTO xmlLibraryVersion : xmlLibraryVersions) {
			String libraryName = xmlLibraryVersion.getLibraryName();
			String libraryVersionName = xmlLibraryVersion.getName();

			Library library = Library.findByProjectAndName(em, projectId, libraryName);
			if (library == null) {
				throw new QImportExportException("Cannot find library " + libraryName + " in project " + projectId);
			}

			String libraryId = library.getId();

			String existingLibraryVersionId = LibraryVersion.getLibraryVersionIdByName(em, libraryVersionName, libraryId);
			if (existingLibraryVersionId != null) {
				LOGGER.log(Level.FINE, "Library version {0} / {1} exists, skipping import", new Object[]{libraryName, libraryVersionName});
				LibraryVersion existingLibraryVersion = LibraryVersion.findById(em, existingLibraryVersionId);
				libraryVersions.add(existingLibraryVersion);
			}
			else {
				LOGGER.log(Level.FINE, "Import library version {0} / {1}", new Object[]{libraryName, libraryVersionName});

				securityUtils.checkCanUpdateLibrary(ticket, library);

				LibraryVersion libraryVersion = xmlMapper.mapLibraryVersion(ticket, library, xmlLibraryVersion);

				em.persist(libraryVersion);

				libraryVersions.add(libraryVersion);
			}
		}

		return libraryVersions;
	}

	// -- Helpers

	private void checkRulesUnique(List<RuleVersion> versions) {
		Set<String> ruleIds = new HashSet<>();

		for (RuleVersion version : versions) {
			String ruleId = version.getRule().getId();

			if (ruleIds.contains(ruleId)) {
				throw new QImportExportException("The working set contains multiple versions of the same rule.");
			}

			ruleIds.add(ruleId);
		}
	}

	private void checkDataModelsUnique(List<DataModelVersion> versions) {
		Set<String> modelIds = new HashSet<>();

		for (DataModelVersion version : versions) {
			String modelId = version.getDataModel().getId();

			if (modelIds.contains(modelId)) {
				throw new QImportExportException("The working set contains multiple versions of the same data model.");
			}

			modelIds.add(modelId);
		}
	}

	private void checkLibrariesUnique(List<LibraryVersion> versions) {
		Set<String> libraryIds = new HashSet<>();

		for (LibraryVersion version : versions) {
			String libraryId = version.getLibrary().getId();

			if (libraryIds.contains(libraryId)) {
				throw new QImportExportException("The working set contains multiple versions of the same library.");
			}

			libraryIds.add(libraryId);
		}
	}

	// -- Helpers

	private List<RuleVersion> findNotFinalizedRuleVersions(List<RuleVersion> versions) {
		List<RuleVersion> notFinalizedVersions = new ArrayList<>();
		for (RuleVersion version : versions) {
			if (version.getState() != VersionState.FINAL) {
				notFinalizedVersions.add(version);
			}
		}
		return notFinalizedVersions;
	}

	private List<DataModelVersion> findNotFinalizedDataModelVersions(List<DataModelVersion> versions) {
		List<DataModelVersion> notFinalizedVersions = new ArrayList<>();
		for (DataModelVersion version : versions) {
			if (version.getState() != VersionState.FINAL) {
				notFinalizedVersions.add(version);
			}
		}
		return notFinalizedVersions;
	}

	private List<LibraryVersion> findNotFinalizedLibraryVersions(List<LibraryVersion> versions) {
		List<LibraryVersion> notFinalizedVersions = new ArrayList<>();
		for (LibraryVersion version : versions) {
			if (version.getState() != VersionState.FINAL) {
				notFinalizedVersions.add(version);
			}
		}
		return notFinalizedVersions;
	}

	private List<RuleVersion> findNotTestingRuleVersions(List<RuleVersion> versions) {
		List<RuleVersion> notTestingVersions = new ArrayList<>();
		for (RuleVersion version : versions) {
			if (version.getState() != VersionState.TESTING && version.getState() != VersionState.FINAL) {
				notTestingVersions.add(version);
			}
		}
		return notTestingVersions;
	}

	private List<DataModelVersion> findNotTestingDataModelVersions(List<DataModelVersion> versions) {
		List<DataModelVersion> notTestingVersions = new ArrayList<>();
		for (DataModelVersion version : versions) {
			if (version.getState() != VersionState.TESTING && version.getState() != VersionState.FINAL) {
				notTestingVersions.add(version);
			}
		}
		return notTestingVersions;
	}

	private List<LibraryVersion> findNotTestingLibraryVersions(List<LibraryVersion> versions) {
		List<LibraryVersion> notTestingVersions = new ArrayList<>();
		for (LibraryVersion version : versions) {
			if (version.getState() != VersionState.TESTING && version.getState() != VersionState.FINAL) {
				notTestingVersions.add(version);
			}
		}
		return notTestingVersions;
	}

	// -- Helpers

	private void checkCanViewRules(SignedTicket ticket, String versionId) throws QAuthorisationException {
		List<RuleVersion> ruleVersions = RuleVersion.findByWorkingSetVersionId(em, versionId);
		for (RuleVersion ruleVersion : ruleVersions) {
			Rule rule = ruleVersion.getRule();
			if (!securityUtils.canViewRule(ticket, rule)) {
				throw new QAuthorisationException("You do not have the required permissions on the contained rules.");
			}
		}
	}

	private void checkCanViewDataModels(SignedTicket ticket, String versionId) throws QAuthorisationException {
		List<DataModelVersion> dataModelVersions = DataModelVersion.findByWorkingSetVersionId(em, versionId);
		for (DataModelVersion dataModelVersion : dataModelVersions) {
			DataModel dataModel = dataModelVersion.getDataModel();
			if (!securityUtils.canViewDataModel(ticket, dataModel)) {
				throw new QAuthorisationException("You do not have the required permissions on the contained data models.");
			}
		}
	}

	private void checkCanViewLibraries(SignedTicket ticket, String versionId) throws QAuthorisationException {
		List<LibraryVersion> libraryVersions = LibraryVersion.findByWorkingSetVersionId(em, versionId);
		for (LibraryVersion libraryVersion : libraryVersions) {
			Library library = libraryVersion.getLibrary();
			if (!securityUtils.canViewLibrary(ticket, library)) {
				throw new QAuthorisationException("You do not have the required permissions on the contained libraries.");
			}
		}
	}

	// -- Helpers

	private void publishEvent(SignedTicket ticket, String event, String workingSetId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_WORKING_SET_ID, workingSetId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_WORKING_SET + "/" + event);
	}

	private void publishVersionEvent(SignedTicket ticket, String event, String versionId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_WORKING_SET_VERSION_ID, versionId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_WORKING_SET_VERSION + "/" + event);
	}

}
