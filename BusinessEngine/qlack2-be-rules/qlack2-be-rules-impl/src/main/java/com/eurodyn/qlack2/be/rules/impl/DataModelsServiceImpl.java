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
import com.eurodyn.qlack2.be.rules.api.dto.DataModelDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldType;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldTypeDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteDataModelVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanUpdateEnabledForTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlDataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.rules.api.exception.QInheritanceCycleException;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.exception.QNonUniqueFieldNamesException;
import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.CreateDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.DeleteDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.GetDataModelByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.GetDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.UpdateDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.CreateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.DeleteDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.EnableTestingDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.ExportDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.FinalizeDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelIdByVersionIdRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionByDataModelAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.ImportDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.LockDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UnlockDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UpdateDataModelFieldRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UpdateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectDataModelsRequest;
import com.eurodyn.qlack2.be.rules.api.util.Constants;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditDataModelDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditDataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelField;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.KnowledgeBaseUtil;
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

public class DataModelsServiceImpl implements DataModelsService {
	private static final Logger LOGGER = Logger.getLogger(DataModelsServiceImpl.class.getName());

	private static final String DEFAULT_PACKAGE = "model";

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

	// -- Data models

	@ValidateTicket
	@Override
	public List<DataModelDTO> getDataModels(GetProjectDataModelsRequest request) {
		String projectId = request.getProjectId();
		boolean filterEmpty = request.isFilterEmpty();

		LOGGER.log(Level.FINE, "Get data models for project {0}.", projectId);

		List<DataModel> models = DataModel.findByProjectId(em, projectId);

		List<DataModelDTO> modelDtos = new ArrayList<>();
		for (DataModel model : models) {
			if (!filterEmpty || !model.getVersions().isEmpty()) {
				// do not check security, summary is always viewable
				modelDtos.add(mapper.mapDataModelSummary(model));
			}
		}

		return modelDtos;
	}

	@ValidateTicket
	@Override
	public DataModelDTO getDataModel(GetDataModelRequest request) {
		String modelId = request.getId();

		LOGGER.log(Level.FINE, "Get data model {0}.", modelId);

		DataModel model = DataModel.findById(em, modelId);
		if (model == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewDataModel(ticket, model);

		DataModelDTO modelDto = mapper.mapDataModel(model, ticket);

		List<DataModelVersion> versions = DataModelVersion.findByDataModelId(em, modelId);
		modelDto.setVersions(mapper.mapDataModelVersionSummaryList(versions, ticket));

		AuditDataModelDTO auditDto = auditMapper.mapDataModel(model);
		auditDto.setVersions(auditMapper.mapDataModelVersionList(versions));
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.DATA_MODEL.toString(),
					null, ticket.getUserID(), auditDto);

		return modelDto;
	}

	@ValidateTicket
	@Override
	public DataModelDTO getDataModelByProjectAndName(GetDataModelByProjectAndNameRequest request) {
		String projectId = request.getProjectId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get data model by project {0} and name {1}.", new Object[]{projectId, name});

		DataModel model = DataModel.findByProjectAndName(em, projectId, name);
		if (model == null) {
			return null;
		}

		DataModelDTO modelDto = mapper.mapDataModelSummary(model);

		return modelDto;
	}

	@ValidateTicket
	@Override
	public String createDataModel(CreateDataModelRequest request) {
		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Create data model in project {0}.", projectId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanCreateDataModel(ticket, projectId);

		DataModel model = new DataModel();
		String modelId = model.getId();

		model.setProjectId(projectId);
		model.setName(request.getName());
		model.setDescription(request.getDescription());
		model.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		model.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		model.setCreatedBy(ticket.getUserID());
		model.setCreatedOn(millis);
		model.setLastModifiedBy(ticket.getUserID());
		model.setLastModifiedOn(millis);

		em.persist(model);

		CreateSecureResourceRequest resourceRequest = new CreateSecureResourceRequest(modelId, model.getName(), "Data Model");
		securityService.createSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_CREATE, modelId);

		AuditDataModelDTO auditDto = auditMapper.mapDataModel(model);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.DATA_MODEL.toString(),
					null, ticket.getUserID(), auditDto);

		return modelId;
	}

	@ValidateTicket
	@Override
	public void updateDataModel(UpdateDataModelRequest request) {
		String modelId = request.getId();

		LOGGER.log(Level.FINE, "Update data model {0}.", modelId);

		DataModel model = DataModel.findById(em, modelId);
		if (model == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		model.setName(request.getName());
		model.setDescription(request.getDescription());
		model.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		model.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		model.setLastModifiedBy(ticket.getUserID());
		model.setLastModifiedOn(millis);

		UpdateSecureResourceRequest resourceRequest = new UpdateSecureResourceRequest(modelId, model.getName(), "Data Model");
		securityService.updateSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_UPDATE, modelId);

		AuditDataModelDTO auditDto = auditMapper.mapDataModel(model);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.DATA_MODEL.toString(),
					null, ticket.getUserID(), auditDto);

		UpdateDataModelVersionRequest versionRequest = request.getVersionRequest();
		if (versionRequest != null) {
			updateDataModelVersion(ticket, model, versionRequest);
		}
	}

	@ValidateTicket
	@Override
	public CanDeleteDataModelResult canDeleteDataModel(DeleteDataModelRequest request) {
		String modelId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete data model {0}.", modelId);

		DataModel model = DataModel.findById(em, modelId);
		if (model == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewDataModel(ticket, model);

		AuditDataModelDTO auditDto = auditMapper.mapDataModel(model);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.DATA_MODEL.toString(),
					null, ticket.getUserID(), auditDto);

		long countWorkingSets = WorkingSetVersion.countContainingDataModel(em, modelId);
		if (countWorkingSets > 0) {
			CanDeleteDataModelResult result = new CanDeleteDataModelResult();
			result.setResult(false);

			result.setContainedInWorkingSet(true);
			return result;
		}

		long countChildren = DataModelVersion.countChildrenOfModel(em, modelId);
		if (countChildren > 0) {
			CanDeleteDataModelResult result = new CanDeleteDataModelResult();
			result.setResult(false);

			result.setParentOfDataModel(true);
			return result;
		}

		long countContainers = DataModelVersion.countContainersOfModel(em, modelId);
		if (countContainers > 0) {
			CanDeleteDataModelResult result = new CanDeleteDataModelResult();
			result.setResult(false);

			result.setContainedInDataModel(true);
			return result;
		}

		long countLockedByOther = DataModelVersion.countLockedByOtherUser(em, modelId, ticket.getUserID());
		if (countLockedByOther > 0) {
			CanDeleteDataModelResult result = new CanDeleteDataModelResult();
			result.setResult(false);

			result.setLockedByOtherUser(true);
			return result;
		}

		CanDeleteDataModelResult result = new CanDeleteDataModelResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteDataModel(DeleteDataModelRequest request) {
		String modelId = request.getId();

		LOGGER.log(Level.FINE, "Delete data model {0}.", modelId);

		DataModel model = DataModel.findById(em, modelId);
		if (model == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		long countWorkingSets = WorkingSetVersion.countContainingDataModel(em, modelId);
		if (countWorkingSets > 0) {
			throw new QInvalidActionException("This data model has versions contained in working sets.");
		}

		long countChildren = DataModelVersion.countChildrenOfModel(em, modelId);
		if (countChildren > 0) {
			throw new QInvalidActionException("This data model has versions which are parents of other data model versions.");
		}

		long countContainers = DataModelVersion.countContainersOfModel(em, modelId);
		if (countContainers > 0) {
			throw new QInvalidActionException("This data model has versions which are types of fields contained in other data model versions.");
		}

		long countLockedByOther = DataModelVersion.countLockedByOtherUser(em, modelId, ticket.getUserID());
		if (countLockedByOther > 0) {
			throw new QInvalidActionException("This data model has versions locked by other users.");
		}

		AuditDataModelDTO auditDto = auditMapper.mapDataModel(model);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.DATA_MODEL.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(model);

		DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(modelId);
		securityService.deleteSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_DELETE, modelId);
	}

	// -- Data model versions

	@ValidateTicket
	@Override
	public List<DataModelFieldTypeDTO> getDataModelFieldTypes(EmptyRequest request) {

		LOGGER.log(Level.FINE, "Get data model field types.");

		// XXX compute this once
		List<DataModelFieldTypeDTO> types = new ArrayList<>();

		for (DataModelFieldType type : DataModelFieldType.values()) {
			DataModelFieldTypeDTO typeDto = new DataModelFieldTypeDTO();
			typeDto.setId(String.valueOf(type.ordinal()));
			typeDto.setName(type.name());
			types.add(typeDto);
		}

		return types;
	}

	@ValidateTicket
	@Override
	public List<DataModelVersionDTO> getDataModelVersions(GetDataModelVersionsRequest request) {
		String modelId = request.getId();

		LOGGER.log(Level.FINE, "Get data model versions for data model {0}.", modelId);

		SignedTicket ticket = request.getSignedTicket();

		// do not check security, summary is always viewable
		List<DataModelVersion> versions = DataModelVersion.findByDataModelId(em, modelId);

		String filterCycles = request.getFilterCycles();
		if (filterCycles != null) {
			versions = filterVersionsThatCauseCycles(versions, filterCycles);
		}

		List<DataModelVersionDTO> versionDtos = mapper.mapDataModelVersionSummaryList(versions, ticket);

		return versionDtos;
	}

	@ValidateTicket
	@Override
	public DataModelVersionDTO getDataModelVersion(GetDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		DataModel model = version.getDataModel();
		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewDataModel(ticket, model);

		DataModelVersionDTO versionDto = mapper.mapDataModelVersion(version, ticket);

		List<DataModelField> fields = DataModelField.findByContainerModelId(em, versionId);
		List<DataModelFieldDTO> fieldDtos = mapper.mapDataModelFieldList(fields);
		versionDto.setFields(fieldDtos);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		auditDto.setFields(fieldDtos);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public DataModelVersionDTO getDataModelVersionByDataModelAndName(GetDataModelVersionByDataModelAndNameRequest request) {
		String modelId = request.getDataModelId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get data model version by data model {0} and name {1}.", new Object[]{modelId, name});

		DataModelVersion version = DataModelVersion.findByDataModelAndName(em, modelId, name);
		if (version == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		DataModelVersionDTO versionDto = mapper.mapDataModelVersionSummary(version, ticket);

		return versionDto;
	}

	@ValidateTicket
	@Override
	public String getDataModelIdByVersionId(GetDataModelIdByVersionIdRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get data model id for data model version {0}.", versionId);

		String modelId = DataModelVersion.findDataModelIdById(em, versionId);

		return modelId;
	}

	@ValidateTicket
	@Override
	public String createDataModelVersion(CreateDataModelVersionRequest request) {
		String modelId = request.getDataModelId();
		DataModel model = DataModel.findById(em, modelId); // load in full for security

		LOGGER.log(Level.FINE, "Create data model version for data model {0}.", modelId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		DataModelVersion version = new DataModelVersion();
		String versionId = version.getId();
		version.setDataModel(model);

		version.setName(request.getName());
		version.setDescription(request.getDescription());
		version.setModelPackage(DEFAULT_PACKAGE); // XXX must set default value

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

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		// XXX audit fields ?
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	private void copyFromBaseVersion(DataModelVersion version, String baseVersionId) {
		DataModelVersion baseVersion = DataModelVersion.findById(em, baseVersionId);
		if (baseVersion == null) {
			throw new IllegalArgumentException("Base data model version does not exist.");
		}

		DataModel model = version.getDataModel();
		DataModel baseModel = baseVersion.getDataModel();
		if (!baseModel.getId().equals(model.getId())) {
			throw new IllegalArgumentException("Base data model version does not belong to current data model.");
		}

		version.setModelPackage(baseVersion.getModelPackage());
		version.setParentModel(baseVersion.getParentModel());

		List<DataModelField> fields = new ArrayList<>();
		List<DataModelField> baseFields = DataModelField.findByContainerModelId(em, baseVersionId);
		for (DataModelField baseField : baseFields) {
			DataModelField field = new DataModelField();
			field.setContainerModel(version);
			field.setName(baseField.getName());
			field.setFieldPrimitiveType(baseField.getFieldPrimitiveType());
			field.setFieldModelType(baseField.getFieldModelType());

			fields.add(field);
		}
		version.setFields(fields);
	}

	private void updateDataModelVersion(SignedTicket ticket, DataModel model, UpdateDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Update data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		versionStateUtils.checkDataModelVersionNotFinalized(version);
		versionStateUtils.checkCanModifyDataModelVersion(ticket.getUserID(), version);

		DataModel existingModel = version.getDataModel();
		if (!model.getId().equals(existingModel.getId())) {
			throw new IllegalArgumentException("Data model version does not belong to data model.");
		}

		version.setDescription(request.getDescription());
		version.setModelPackage(request.getModelPackage());

		if (version.getState() == VersionState.TESTING) {
			checkCanUpdateEnabledForTestingDataModelVersion(request);
		}

		String parentModelVersionId = request.getParentModelVersionId();
		if (parentModelVersionId != null && !parentModelVersionId.isEmpty()) {
			DataModelVersion parentModelVersion = DataModelVersion.findById(em, parentModelVersionId);
			if (parentModelVersion == null) {
				throw new IllegalArgumentException("Parent data model version does not exit.");
			}

			DataModel parentModel = parentModelVersion.getDataModel();
			if (!parentModel.getProjectId().equals(model.getProjectId())) {
				throw new IllegalArgumentException("Parent data model version does not belong to data model project.");
			}

			boolean hasCycle = hasInheritanceCycle(versionId, parentModelVersion);
			if (hasCycle) {
				throw new QInheritanceCycleException();
			}

			version.setParentModel(parentModelVersion);
		}
		else {
			version.setParentModel(null);
		}

		checkDataModelFieldsUniqueNames(request.getFieldRequests());

		List<DataModelField> fields = DataModelField.findByContainerModelId(em, versionId);
		updateDataModelFields(version, fields, request.getFieldRequests());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		if (version.getState() == VersionState.TESTING) {
			invalidateWorkingSets(ticket, version);
		}

		publishVersionEvent(ticket, Constants.EVENT_UPDATE, versionId);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		// XXX audit fields
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	private void invalidateWorkingSets(SignedTicket ticket, DataModelVersion version) {
		String versionId = version.getId();
		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingDataModelVersion(em, versionId);
		for (WorkingSetVersion workingSetVersion : workingSetVersions) {
			if (workingSetVersion.getState() == VersionState.TESTING) {
				workingSetVersion.setDataModelsJar(null);
				knowledgeBaseUtil.destroyKnowledgeBase(ticket, workingSetVersion);
			}
		}
	}

	private List<DataModelVersion> filterVersionsThatCauseCycles(List<DataModelVersion> parentVersions, String versionId) {
		List<DataModelVersion> filteredParentVersions = new ArrayList<>();
		for (DataModelVersion parentVersion : parentVersions) {
			if (hasInheritanceCycle(versionId, parentVersion)) {
				continue;
			}

			filteredParentVersions.add(parentVersion);
		}
		return filteredParentVersions;
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

	private void checkDataModelFieldsUniqueNames(List<UpdateDataModelFieldRequest> requests) {
		Set<String> names = new HashSet<>();

		for (UpdateDataModelFieldRequest request : requests) {
			String name = request.getName();

			if (names.contains(name)) {
				throw new QNonUniqueFieldNamesException();
			}

			names.add(name);
		}

	}

	private void updateDataModelFields(DataModelVersion version, List<DataModelField> fields, List<UpdateDataModelFieldRequest> requests) {

		for (DataModelField field : fields) {
			UpdateDataModelFieldRequest request = findFieldRequestById(requests, field.getId());
			if (request != null) {
				updateField(field, request);
			}
			else {
				em.remove(field);
			}
		}

		for (UpdateDataModelFieldRequest request : requests) {
			if (request.getId() == null) {
				DataModelField field = new DataModelField();
				field.setContainerModel(version);
				updateField(field, request);
				em.persist(field);
			}
		}
	}

	private void updateField(DataModelField field, UpdateDataModelFieldRequest request) {
		field.setName(request.getName());

		String fieldTypeId = request.getFieldTypeId();
		DataModelFieldType primitiveFieldType = findFieldTypeById(fieldTypeId);
		if (primitiveFieldType != null) {
			field.setFieldPrimitiveType(primitiveFieldType);
			field.setFieldModelType(null);
		}
		else {
			// XXX check data model belongs to same project
			String fieldTypeVersionId = request.getFieldTypeVersionId();
			DataModelVersion fieldModelType = em.getReference(DataModelVersion.class, fieldTypeVersionId);
			field.setFieldPrimitiveType(null);
			field.setFieldModelType(fieldModelType);
		}
	}

	private static UpdateDataModelFieldRequest findFieldRequestById(List<UpdateDataModelFieldRequest> requests, String id) {
		for (UpdateDataModelFieldRequest request : requests) {
			if (id.equals(request.getId())) {
				return request;
			}
		}
		return null;
	}

	private static DataModelFieldType findFieldTypeById(String id) {
		for (DataModelFieldType type : DataModelFieldType.values()) {
			if (id.equals(String.valueOf(type.ordinal()))) {
				return type;
			}
		}
		return null;
	}

	@ValidateTicket
	@Override
	public CanDeleteDataModelVersionResult canDeleteDataModelVersion(DeleteDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		DataModel model = version.getDataModel();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingDataModelVersion(em, versionId);
		if (!workingSetVersions.isEmpty()) {
			CanDeleteDataModelVersionResult result = new CanDeleteDataModelVersionResult();
			result.setResult(false);

			result.setContainedInWorkingSetVersions(true);
			List<String> workingSetNames = new ArrayList<>();
			for (WorkingSetVersion workingSetVersion : workingSetVersions) {
				workingSetNames.add(workingSetVersion.getWorkingSet().getName() + " / " + workingSetVersion.getName());
			}
			result.setWorkingSetVersions(workingSetNames);

			return result;
		}

		List<DataModelVersion> children = DataModelVersion.findChildren(em, versionId);
		if (!children.isEmpty()) {
			CanDeleteDataModelVersionResult result = new CanDeleteDataModelVersionResult();
			result.setResult(false);

			result.setParentOfDataModelVersions(true);
			List<String> childrenNames = new ArrayList<>();
			for (DataModelVersion child : children) {
				childrenNames.add(child.getDataModel().getName() + " / " + child.getName());
			}
			result.setDataModelVersions(childrenNames);

			return result;
		}

		List<DataModelVersion> containers = DataModelVersion.findContainers(em, versionId);
		if (!containers.isEmpty()) {
			CanDeleteDataModelVersionResult result = new CanDeleteDataModelVersionResult();
			result.setResult(false);

			result.setContainedInDataModelVersions(true);
			List<String> containerNames = new ArrayList<>();
			for (DataModelVersion child : containers) {
				containerNames.add(child.getDataModel().getName() + " / " + child.getName());
			}
			result.setDataModelVersions(containerNames);

			return result;
		}

		CanDeleteDataModelVersionResult result = new CanDeleteDataModelVersionResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteDataModelVersion(DeleteDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Delete data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		DataModel model = version.getDataModel();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		versionStateUtils.checkCanModifyDataModelVersion(ticket.getUserID(), version);

		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingDataModelVersion(em, versionId);
		if (!workingSetVersions.isEmpty()) {
			throw new QInvalidActionException("The data model version is contained in a working set version.");
		}

		List<DataModelVersion> children = DataModelVersion.findChildren(em, versionId);
		if (!children.isEmpty()) {
			throw new QInvalidActionException("The data model version is the parent of other data model versions.");
		}

		List<DataModelVersion> containers = DataModelVersion.findContainers(em, versionId);
		if (!containers.isEmpty()) {
			throw new QInvalidActionException("The data model version is the type of fields of other data model versions.");
		}

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(version);

		publishVersionEvent(ticket, Constants.EVENT_DELETE, versionId);
	}

	@ValidateTicket
	@Override
	public void lockDataModelVersion(LockDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Lock data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		DataModel model = version.getDataModel();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanLockDataModelVersion(ticket, model);

		versionStateUtils.checkDataModelVersionNotFinalized(version);
		versionStateUtils.checkCanLockDataModelVersion(ticket.getUserID(), version);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLockedBy(ticket.getUserID());
		version.setLockedOn(millis);

		publishVersionEvent(ticket, Constants.EVENT_LOCK, versionId);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.LOCK.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void unlockDataModelVersion(UnlockDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Unlock data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		DataModel model = version.getDataModel();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUnlockDataModelVersion(ticket, model);

		versionStateUtils.checkDataModelVersionNotFinalized(version);

		boolean canUnlockAny = securityUtils.canUnlockAnyDataModelVersion(ticket, model);
		versionStateUtils.checkCanUnlockDataModelVersion(ticket.getUserID(), canUnlockAny, version);

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_UNLOCK, versionId);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UNLOCK.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	// -- EnableTesting / Finalize (cascadable)

	@ValidateTicket
	@Override
	public CanUpdateEnabledForTestingDataModelResult canUpdateEnabledForTestingDataModelVersion(UpdateDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can update enabled-for-testing data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		if (version.getState() != VersionState.TESTING) {
			CanUpdateEnabledForTestingDataModelResult result = new CanUpdateEnabledForTestingDataModelResult();
			result.setResult(true);
			return result;
		}

		DataModel model = version.getDataModel();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_UPDATE.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		Set<String> dependencyVersionIds = findDependencyVersionIds(request);

		List<WorkingSetVersion> violatedWorkingSetVersions = findViolatedWorkingSetVersions(version.getWorkingSets(), dependencyVersionIds);

		if (!violatedWorkingSetVersions.isEmpty()) {
			CanUpdateEnabledForTestingDataModelResult result = new CanUpdateEnabledForTestingDataModelResult();
			result.setResult(false);

			result.setRestrict(true);
			List<String> violatedWorkingSetVersionNames = new ArrayList<>();
			for (WorkingSetVersion workingSetVersion : violatedWorkingSetVersions) {
				violatedWorkingSetVersionNames.add(workingSetVersion.getWorkingSet().getName() + " / " + workingSetVersion.getName());
			}
			result.setWorkingSetVersions(violatedWorkingSetVersionNames);
			return result;
		}

		CanUpdateEnabledForTestingDataModelResult result = new CanUpdateEnabledForTestingDataModelResult();
		result.setResult(true);
		return result;
	}

	private void checkCanUpdateEnabledForTestingDataModelVersion(UpdateDataModelVersionRequest request) {
		String versionId = request.getId();

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Set<String> dependencyVersionIds = findDependencyVersionIds(request);

		List<WorkingSetVersion> violatedWorkingSetVersions = findViolatedWorkingSetVersions(version.getWorkingSets(), dependencyVersionIds);

		if (!violatedWorkingSetVersions.isEmpty()) {
			throw new QInvalidActionException("Update of the data model version will violate enabled-for-testing working sets");
		}
	}

	private Set<String> findDependencyVersionIds(UpdateDataModelVersionRequest request) {
		Set<String> dependencyVersionIds = new HashSet<>();
		String parentModelVersionId = request.getParentModelVersionId();
		if (parentModelVersionId != null) {
			dependencyVersionIds.add(parentModelVersionId);
		}

		for (UpdateDataModelFieldRequest fieldRequest : request.getFieldRequests()) {
			String fieldTypeVersionId = fieldRequest.getFieldTypeVersionId();
			if (fieldTypeVersionId != null) {
				dependencyVersionIds.add(fieldTypeVersionId);
			}
		}

		return dependencyVersionIds;
	}

	private List<WorkingSetVersion> findViolatedWorkingSetVersions(List<WorkingSetVersion> workingSetVersions, Set<String> dependencyVersionIds) {
		List<WorkingSetVersion> violatedWorkingSetVersions = new ArrayList<>();
		for (WorkingSetVersion workingSetVersion : workingSetVersions) {
			if (workingSetVersion.getState() == VersionState.TESTING) {
				List<String> containedVersionIds = DataModelVersion.findIdsByWorkingSetVersionId(em, workingSetVersion.getId());
				if (!containedVersionIds.containsAll(dependencyVersionIds)) {
					violatedWorkingSetVersions.add(workingSetVersion);
				}
			}
		}
		return violatedWorkingSetVersions;
	}

	@ValidateTicket
	@Override
	public CanEnableTestingDataModelResult canEnableTestingDataModelVersion(EnableTestingDataModelVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can enable testing data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_ENABLE_TESTING.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		CollectNotTestingAction versionAction = new CollectNotTestingAction();
		Set<String> versionVisited = new HashSet<>();
		visitNodeClosure(version, versionAction, versionVisited);

		List<DataModelVersion> notTestingVersions = versionAction.getNotTestingVersions();
		notTestingVersions.remove(version);

		if (!notTestingVersions.isEmpty()) {
			boolean canModifyList = canModifyDataModelVersionList(ticket, notTestingVersions);
			if (!canModifyList) {
				CanEnableTestingDataModelResult result = new CanEnableTestingDataModelResult();
				result.setResult(false);
				return result;
			}
			else {
				CanEnableTestingDataModelResult result = new CanEnableTestingDataModelResult();
				result.setResult(true);

				result.setCascade(true);
				List<String> versionNames = new ArrayList<>();
				for (DataModelVersion notTestingVersion : notTestingVersions) {
					versionNames.add(notTestingVersion.getDataModel().getName() + " / " + notTestingVersion.getName());
				}
				result.setVersions(versionNames);
				return result;
			}
		}
		else {
			CanEnableTestingDataModelResult result = new CanEnableTestingDataModelResult();
			result.setResult(true);
			return result;
		}
	}

	@ValidateTicket
	@Override
	public CanDisableTestingDataModelResult canDisableTestingDataModelVersion(EnableTestingDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can disable testing data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		DataModel model = version.getDataModel();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DISABLE_TESTING.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		List<WorkingSetVersion> enabledTestingWorkingSetVersions = filterEnabledTestingWorkingSetVersions(version.getWorkingSets());
		if (!enabledTestingWorkingSetVersions.isEmpty()) {
			List<String> workingSetVersionNames = new ArrayList<>();
			for (WorkingSetVersion workingSetVersion : enabledTestingWorkingSetVersions) {
				workingSetVersionNames.add(workingSetVersion.getWorkingSet().getName() + " / " + workingSetVersion.getName());
			}

			CanDisableTestingDataModelResult result = new CanDisableTestingDataModelResult();
			result.setResult(false);
			result.setContainedInWorkingSetVersions(true);
			result.setWorkingSetVersions(workingSetVersionNames);
			return result;
		}

		CanDisableTestingDataModelResult result = new CanDisableTestingDataModelResult();
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
	public CanFinalizeDataModelResult canFinalizeDataModelVersion(FinalizeDataModelVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can finalize data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_FINALISE.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		CollectNotFinalizedAction versionAction = new CollectNotFinalizedAction();
		Set<String> versionVisited = new HashSet<>();
		visitNodeClosure(version, versionAction, versionVisited);

		List<DataModelVersion> notFinalizedVersions = versionAction.getNotFinalizedVersions();
		notFinalizedVersions.remove(version);

		if (!notFinalizedVersions.isEmpty()) {
			boolean canModifyList = canModifyDataModelVersionList(ticket, notFinalizedVersions);
			if (!canModifyList) {
				CanFinalizeDataModelResult result = new CanFinalizeDataModelResult();
				result.setResult(false);
				return result;
			}
			else {
				CanFinalizeDataModelResult result = new CanFinalizeDataModelResult();
				result.setResult(true);

				result.setCascade(true);
				List<String> versionNames = new ArrayList<>();
				for (DataModelVersion notFinalizedVersion : notFinalizedVersions) {
					versionNames.add(notFinalizedVersion.getDataModel().getName() + " / " + notFinalizedVersion.getName());
				}
				result.setVersions(versionNames);
				return result;
			}
		}
		else {
			CanFinalizeDataModelResult result = new CanFinalizeDataModelResult();
			result.setResult(true);
			return result;
		}
	}

	@Override
	public boolean canModifyDataModelVersionIdList(SignedTicket ticket, List<String> versionIds) {
		List<DataModelVersion> versions = new ArrayList<>();
		for (String versionId : versionIds) {
			DataModelVersion version = DataModelVersion.findById(em, versionId);
			versions.add(version);
		}
		return canModifyDataModelVersionList(ticket, versions);
	}

	private boolean canModifyDataModelVersionList(SignedTicket ticket, List<DataModelVersion> versions) {
		for (DataModelVersion version : versions) {
			if (!canModifySingleDataModelVersion(ticket, version)) {
				return false;
			}
		}
		return true;
	}

	private boolean canModifySingleDataModelVersion(SignedTicket ticket, DataModelVersion version) {
		DataModel model = version.getDataModel();
		if (!securityUtils.canUpdateDataModel(ticket, model)) {
			return false;
		}

		if (!versionStateUtils.canModifyDataModelVersion(ticket.getUserID(), version)) {
			return false;
		}

		return true;
	}

	@ValidateTicket
	@Override
	public void enableTestingDataModelVersion(EnableTestingDataModelVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();
		boolean enableTesting = request.isEnableTesting();

		LOGGER.log(Level.FINE, "Enable testing data model version {0} ({1}).", new Object[]{versionId, enableTesting});

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		if (enableTesting) {
			CollectNotTestingAction versionAction = new CollectNotTestingAction();
			Set<String> versionVisited = new HashSet<>();
			visitNodeClosure(version, versionAction, versionVisited);

			List<DataModelVersion> notTestingVersions = versionAction.getNotTestingVersions();
			enableTestingDataModelVersionList(ticket, notTestingVersions);
		} else {
			List<WorkingSetVersion> enabledTestingWorkingSetVersions = filterEnabledTestingWorkingSetVersions(version.getWorkingSets());
			if (!enabledTestingWorkingSetVersions.isEmpty()) {
				throw new QInvalidActionException("This data model version is contained in working set versions with testing enabled.");
			}

			enableTestingDataModelVersionSingle(ticket, version, false);
		}
	}

	@Override
	public void enableTestingDataModelVersionNoCascade(EnableTestingDataModelVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();
		boolean enableTesting = request.isEnableTesting();

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		enableTestingDataModelVersionSingle(ticket, version, enableTesting);
	}

	private void enableTestingDataModelVersionList(SignedTicket ticket, List<DataModelVersion> versions) {
		for (DataModelVersion version : versions) {
			enableTestingDataModelVersionSingle(ticket, version, true);
		}
	}

	private void enableTestingDataModelVersionSingle(SignedTicket ticket, DataModelVersion version, boolean enableTesting) {
		DataModel model = version.getDataModel();

		LOGGER.log(Level.FINE, "Enable testing single data model version {0} ({1}).", new Object[]{version.getId(), enableTesting});

		securityUtils.checkCanUpdateDataModel(ticket, model);

		versionStateUtils.checkDataModelVersionNotFinalized(version);
		versionStateUtils.checkCanModifyDataModelVersion(ticket.getUserID(), version);

		if (enableTesting) {
			version.setState(VersionState.TESTING);
		}
		else {
			version.setState(VersionState.DRAFT);
		}

		String stringEvent = enableTesting ? Constants.EVENT_ENABLE_TESTING : Constants.EVENT_DISABLE_TESTING;
		publishVersionEvent(ticket, stringEvent, version.getId());

		EVENT event = enableTesting ? EVENT.ENABLE_TESTING : EVENT.DISABLE_TESTING;
		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), event.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void finalizeDataModelVersion(FinalizeDataModelVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Finalize data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		CollectNotFinalizedAction versionAction = new CollectNotFinalizedAction();
		Set<String> versionVisited = new HashSet<>();
		visitNodeClosure(version, versionAction, versionVisited);

		List<DataModelVersion> notFinalizedVersions = versionAction.getNotFinalizedVersions();
		finalizeDataModelVersionList(ticket, notFinalizedVersions);
	}

	@Override
	public void finalizeDataModelVersionNoCascade(FinalizeDataModelVersionRequest request) {
		SignedTicket ticket = request.getSignedTicket();

		String versionId = request.getId();

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		finalizeDataModelVersionSingle(ticket, version);
	}

	private void finalizeDataModelVersionList(SignedTicket ticket, List<DataModelVersion> versions) {
		for (DataModelVersion version : versions) {
			finalizeDataModelVersionSingle(ticket, version);
		}
	}

	private void finalizeDataModelVersionSingle(SignedTicket ticket, DataModelVersion version) {
		DataModel model = version.getDataModel();

		LOGGER.log(Level.FINE, "Finalize single data model version {0}.", version.getId());

		securityUtils.checkCanUpdateDataModel(ticket, model);

		versionStateUtils.checkDataModelVersionNotFinalized(version);
		versionStateUtils.checkCanModifyDataModelVersion(ticket.getUserID(), version);

		version.setState(VersionState.FINAL);

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_FINALISE, version.getId());

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.FINALISE.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public byte[] exportDataModelVersion(ExportDataModelVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Export data model version {0}.", versionId);

		DataModelVersion version = DataModelVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		DataModel model = version.getDataModel();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewDataModel(ticket, model);

		if (version.getState() != VersionState.FINAL) {
			throw new QInvalidActionException("Version is not finalized.");
		}

		XmlDataModelVersionDTO xmlVersionDto = xmlMapper.mapDataModel(version);

		byte[] xml = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// XXX generated XML is immutable, generate once and cache ?
			JAXBContext jaxbContext = JAXBContext.newInstance(XmlDataModelVersionDTO.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(xmlVersionDto, baos);

			xml = baos.toByteArray();
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot export data model version.", e);
		}

		publishVersionEvent(ticket, Constants.EVENT_EXPORT, versionId);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.EXPORT.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return xml;
	}

	@ValidateTicket
	@Override
	public String importDataModelVersion(ImportDataModelVersionRequest request) {
		String modelId = request.getDataModelId();
		byte[] xml = request.getXml();

		LOGGER.log(Level.FINE, "Import data model version in data model {0}.", modelId);

		DataModel model = DataModel.findById(em, modelId);
		if (model == null) {
			throw new IllegalArgumentException("Data model does not exist");
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateDataModel(ticket, model);

		XmlDataModelVersionDTO xmlVersion = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xml);

			JAXBContext jaxbContext = JAXBContext.newInstance(XmlDataModelVersionDTO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			xmlVersion = (XmlDataModelVersionDTO) jaxbUnmarshaller.unmarshal(bais);
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot import data model version.", e);
		}

		DataModelVersion existingDataModelVersion = DataModelVersion.findByDataModelAndName(em, modelId, xmlVersion.getName());
		if (existingDataModelVersion != null) {
			throw new QImportExportException("Another data model version with the same name already exists.");
		}

		DataModelVersion version = xmlMapper.mapDataModelVersion(ticket, model, xmlVersion);
		String versionId = version.getId();

		em.persist(version);

		String projectId = model.getProjectId();

		xmlMapper.mapDataModelVersionParent(em, projectId, version, xmlVersion);
		DataModelVersion parentModelVersion = version.getParentModel();
		if (parentModelVersion != null) {
			// the imported data model is new, only need to check for self-reference
			boolean hasSelfRef = parentModelVersion.getId().equals(versionId);
			if (hasSelfRef) {
				throw new QImportExportException("The imported data model extends itself.");
			}
		}

		xmlMapper.mapDataModelVersionFields(em, projectId, version, xmlVersion);

		publishVersionEvent(ticket, Constants.EVENT_IMPORT, versionId);

		AuditDataModelVersionDTO auditDto = auditMapper.mapDataModelVersion(version);
		// XXX audit fields ?
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.IMPORT.toString(), GROUP.DATA_MODEL_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	// -- Helpers

	@Override
	public List<String> checkDataModelVersionClosureContained(List<String> containedVersionIds) {

		CollectNotContainedAction versionAction = new CollectNotContainedAction(containedVersionIds);
		Set<String> versionVisited = new HashSet<>();
		for (String containedVersionId : containedVersionIds) {
			DataModelVersion version = DataModelVersion.findById(em, containedVersionId);
			visitNodeClosure(version, versionAction, versionVisited);
		}

		List<DataModelVersion> notContainedVersions = versionAction.getNotContainedVersions();
		List<String> notContainedVersionIds = new ArrayList<>();
		for (DataModelVersion notContainedVersion : notContainedVersions) {
			notContainedVersionIds.add(notContainedVersion.getId());
		}
		return notContainedVersionIds;
	}

	private static interface DataModelVersionAction {
		void preApply(DataModelVersion version);

		void postApply(DataModelVersion version);
	}

	private static class CollectNotContainedAction implements DataModelVersionAction {
		private List<String> versionIds;

		private List<DataModelVersion> notContainedVersions;

		CollectNotContainedAction(List<String> versionIds) {
			this.versionIds = versionIds;
			this.notContainedVersions = new ArrayList<>();
		}

		public List<DataModelVersion> getNotContainedVersions() {
			return notContainedVersions;
		}

		@Override
		public void preApply(DataModelVersion version) {
			String versionId = version.getId();

			if (!versionIds.contains(versionId)) {
				notContainedVersions.add(version);
			}
		}

		@Override
		public void postApply(DataModelVersion version) {
		}
	}

	private static class CollectNotTestingAction implements DataModelVersionAction {
		private List<DataModelVersion> notTestingVersions;

		CollectNotTestingAction() {
			this.notTestingVersions = new ArrayList<>();
		}

		public List<DataModelVersion> getNotTestingVersions() {
			return notTestingVersions;
		}

		@Override
		public void preApply(DataModelVersion version) {
			VersionState versionState = version.getState();

			if (versionState != VersionState.TESTING && versionState != VersionState.FINAL) {
				notTestingVersions.add(version);
			}
		}

		@Override
		public void postApply(DataModelVersion version) {
		}
	}

	private static class CollectNotFinalizedAction implements DataModelVersionAction {
		private List<DataModelVersion> notFinalizedVersions;

		CollectNotFinalizedAction() {
			this.notFinalizedVersions = new ArrayList<>();
		}

		public List<DataModelVersion> getNotFinalizedVersions() {
			return notFinalizedVersions;
		}

		@Override
		public void preApply(DataModelVersion version) {
			VersionState versionState = version.getState();

			if (versionState != VersionState.FINAL) {
				notFinalizedVersions.add(version);
			}
		}

		@Override
		public void postApply(DataModelVersion version) {
		}
	}

	private void visitNodeClosure(DataModelVersion version, DataModelVersionAction versionAction, Set<String> versionVisited) {
		String versionId = version.getId();

		boolean visited = versionVisited.contains(versionId);
		if (visited) {
			return;
		}

		versionVisited.add(versionId);

		versionAction.preApply(version);

		// parent
		DataModelVersion parentModelVersion = version.getParentModel();
		if (parentModelVersion != null) {
			visitNodeClosure(parentModelVersion, versionAction, versionVisited);
		}

		// fields
		for (DataModelField field : version.getFields()) {
			DataModelVersion fieldModelVersion = field.getFieldModelType();
			if (fieldModelVersion != null) {
				visitNodeClosure(fieldModelVersion, versionAction, versionVisited);
			}
		}

		versionAction.postApply(version);
	}

	// -- Helpers

	private void publishEvent(SignedTicket ticket, String event, String modelId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_DATA_MODEL_ID, modelId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_DATA_MODEL + "/" + event);
	}

	private void publishVersionEvent(SignedTicket ticket, String event, String versionId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_DATA_MODEL_VERSION_ID, versionId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_DATA_MODEL_VERSION + "/" + event);
	}

}