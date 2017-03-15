package com.eurodyn.qlack2.be.workflow.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.WorkflowVersionService;
import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.XMLWorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidDataException;
import com.eurodyn.qlack2.be.workflow.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.CountWorkflowVersionsLockedByOtherUserRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.CreateWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.DeleteWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.EnableTestingVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.ExportWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.FinaliseWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.GetWorkflowVersionIdByNameRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.GetWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.GetWorkflowVersionsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.ImportWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.LockWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.UnlockWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.UpdateWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.UpdateWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.util.Constants;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Condition;
import com.eurodyn.qlack2.be.workflow.impl.model.ConditionType;
import com.eurodyn.qlack2.be.workflow.impl.model.State;
import com.eurodyn.qlack2.be.workflow.impl.model.Workflow;
import com.eurodyn.qlack2.be.workflow.impl.model.WorkflowVersion;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.workflow.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.SecurityUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.WorkflowConstants;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;

public class WorkflowVersionServiceImpl implements WorkflowVersionService {
	private static final Logger LOGGER = Logger.getLogger(WorkflowVersionServiceImpl.class.getName());

	private IDMService idmService;
	private EntityManager em;
	private SecurityUtil securityUtil;
	private ConverterUtil converterUtil;
	private SecurityService securityService;
	private AuditClientService auditClientService;
	private EventPublisherService eventPublisher;
	private List<WorkingSetsService> workingSetsServiceList;
	private List<RulesService> rulesServiceList;

	@Override
	@ValidateTicket
	public WorkflowVersionDTO getWorkflowVersion(GetWorkflowVersionRequest request) {
		LOGGER.log(Level.FINE, "Getting workflow version with ID {0}", request.getVersionId());

		WorkflowVersion version = WorkflowVersion.find(em, request.getVersionId());

		if (version == null) {
			return null;
		}

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
			WorkflowConstants.OP_WFL_VIEW_WORKFLOW, version.getWorkflow().getId(),
			version.getWorkflow().getProjectId());

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(version);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.VIEW.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditWorkflowVersionDTO);

		return converterUtil.workflowVersionToWorkflowVersionDTO(version);
	}

	@Override
	@ValidateTicket
	public List<WorkflowVersionDTO> getWorkflowVersions(GetWorkflowVersionsRequest request) {
		LOGGER.log(Level.FINE, "Getting workflow versions for workflow with ID {0}", request.getWorkflowId());
		String workflowId = request.getWorkflowId();

		Workflow workflow = Workflow.find(em, workflowId);

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_VIEW_WORKFLOW, workflow.getId(),
				workflow.getProjectId());

		List<WorkflowVersionDTO> workflowVersionDTOs = converterUtil.workflowVersionToWorkflowVersionDTOList(workflow.getWorkflowVersions());
		return workflowVersionDTOs;
	}

	@Override
	@ValidateTicket
	public Long countWorkflowVersionsLockedByOtherUser(CountWorkflowVersionsLockedByOtherUserRequest req) {
		LOGGER.log(Level.FINE, "Counting workflow version with ID {0}", req.getWorkflowId());
		Workflow workflow = Workflow.find(em, req.getWorkflowId());

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
			WorkflowConstants.OP_WFL_VIEW_WORKFLOW, workflow.getId(), workflow.getProjectId());

		Long count = WorkflowVersion.countWorkflowVersionsLockedByOtherUser(em, workflow.getId(), req.getSignedTicket().getUserID());
		return count;
	}

	@Override
	@ValidateTicket
	public boolean checkWorkflowVersionCanFinalise(FinaliseWorkflowVersionRequest req) {
		LOGGER.log(Level.FINE, "Checking for finalization workflow version with ID {0}", req.getId());
		WorkflowVersion version = WorkflowVersion.find(em, req.getId());

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, version.getWorkflow().getId(), version.getWorkflow().getProjectId());

		if (workingSetsServiceList.size() == 0) {
			throw new QInvalidActionException("No working set service found.");
		}

		if (rulesServiceList.size() == 0) {
			throw new QInvalidActionException("No rule service found");
		}

		for (Condition condition : version.getConditions())
		{
			GetWorkingSetVersionRequest workingSetVersionRequest = new GetWorkingSetVersionRequest();
			workingSetVersionRequest.setSignedTicket(req.getSignedTicket());
			workingSetVersionRequest.setId(condition.getWorkingSetId());
			WorkingSetVersionDTO workingSetVersion = workingSetsServiceList.get(0).getWorkingSetVersion(workingSetVersionRequest);
			if (workingSetVersion.getState() != VersionState.FINAL)
				return false;

			GetRuleVersionRequest ruleVersionRequest = new GetRuleVersionRequest();
			ruleVersionRequest.setSignedTicket(req.getSignedTicket());
			ruleVersionRequest.setId(condition.getRuleId());
			RuleVersionDTO ruleVersion = rulesServiceList.get(0).getRuleVersion(ruleVersionRequest);
			if (ruleVersion.getState() != VersionState.FINAL)
				return false;
		}

		return true;
	}

	@Override
	@ValidateTicket
	public String getWorkflowVersionIdByName(GetWorkflowVersionIdByNameRequest req) {
		LOGGER.log(Level.FINE, "Getting workflow version id with name {0}", req.getWorkflowVersionName());
		String workflowVersionName = req.getWorkflowVersionName();
		Workflow workflow = Workflow.find(em, req.getWorkflowId());

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
				WorkflowConstants.OP_WFL_VIEW_WORKFLOW, workflow.getId(),
				workflow.getProjectId());

		return WorkflowVersion.getWorkflowVersionIdByName(em, workflowVersionName, workflow.getId());
	}

	@Override
	@ValidateTicket
	public String createWorkflowVersion(CreateWorkflowVersionRequest req) {
		LOGGER.log(Level.FINE, "Creating workflow version with name {0}", req.getName());
		String workflowId = req.getWorkflowId();

		Workflow workflow = Workflow.find(em, workflowId);

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, workflow.getId(), workflow.getProjectId());

		DateTime now = DateTime.now();
		long millis = now.getMillis();
		WorkflowVersion workflowVersion = new WorkflowVersion();
		workflowVersion.setName(req.getName());
		workflowVersion.setDescription(req.getDescription());
		workflowVersion.setContent(req.getContent());
		workflowVersion.setWorkflow(workflow);
		workflowVersion.setState(State.DRAFT);
		workflowVersion.setCreatedBy(req.getSignedTicket().getUserID());
		workflowVersion.setCreatedOn(millis);
		workflowVersion.setLastModifiedBy(req.getSignedTicket().getUserID());
		workflowVersion.setLastModifiedOn(millis);

		em.persist(workflowVersion);

		if (req.getConditions() != null) {
			// create conditions
			Map<String, ConditionDTO> conditionDTOs = new HashMap<>();
			for (ConditionDTO conditionDTO : req.getConditions()) {
				conditionDTOs.put(conditionDTO.getId(), conditionDTO);
			}

			checkConditionValidations(conditionDTOs);

			Map<String, Condition> conditions = createConditions(conditionDTOs,	workflowVersion);

			workflowVersion.setConditions(new ArrayList(conditions.values()));
		}

		publishEvent(req.getSignedTicket(), Constants.EVENT_CREATE, workflowVersion.getId());

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(workflowVersion);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.CREATE.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				req.getSignedTicket().getUserID(), auditWorkflowVersionDTO);

		return workflowVersion.getId();
	}

	private Map<String, Condition> createConditions(
			Map<String, ConditionDTO> conditionDTOs, WorkflowVersion workflowVersion) {
		// Map that holds the original id of the condition and the
		// created condition.
		Map<String, Condition> conditions = new HashMap<>();

		// Create conditions and fix parent relationships.
		for (ConditionDTO conditionDTO : conditionDTOs.values()) {
			Condition condition = conditions.get(conditionDTO.getId());

			if (condition == null) {
				condition = createCondition(conditionDTO, conditionDTOs, conditions, workflowVersion);
			}
		}
		return conditions;
	}

	private Condition createCondition(ConditionDTO conditionDTO,
			Map<String, ConditionDTO> conditionDTOs,
			Map<String, Condition> conditions, WorkflowVersion version) {
		Condition condition = em.find(Condition.class, conditionDTO.getId());

		if (condition == null) {
			condition = new Condition();
			condition.setId(conditionDTO.getId());
		}

		condition.setName(conditionDTO.getName());
		condition.setConditionType(ConditionType.values()[conditionDTO
				.getConditionType()]);
		condition.setWorkflowVersion(version);
		condition.setWorkingSetId(conditionDTO.getWorkingSetId());
		condition.setRuleId(conditionDTO.getRuleId());

		if (conditionDTO.getParentCondition() != null) {
			// check if parent has already been created
			Condition parentCondition = conditions.get(conditionDTO
					.getParentCondition().getId());
			if (parentCondition == null) {
				// Recursively create parent condition
				ConditionDTO parentConditionDTO = conditionDTOs
						.get(conditionDTO.getParentCondition().getId());
				parentCondition = createCondition(parentConditionDTO,
						conditionDTOs, conditions, version);

				conditions.put(parentConditionDTO.getId(), parentCondition);
			}
			condition.setParent(parentCondition);
		}

		em.persist(condition);

		conditions.put(conditionDTO.getId(), condition);
		return condition;
	}

	@ValidateTicket
	@Override
	public void updateWorkflowVersion(UpdateWorkflowRequest wRequest) {
		LOGGER.log(Level.FINE, "Updating workflow version with ID {0}", wRequest.getId());
		UpdateWorkflowVersionRequest request = wRequest.getVersionRequest();

		WorkflowVersion version = WorkflowVersion.find(em, request.getId());
		if (version == null) {
			return;
		}

		securityUtil.checkWorkflowOperation(wRequest.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, version.getWorkflow().getId(), version.getWorkflow().getProjectId());

		if (version.getState() == State.FINAL)
			throw new QInvalidActionException("You are not allowed to update a version which is finalized.");

		if (WorkflowVersion.checkWorkflowVersionLockedByOtherUser(em, version.getId(),
				wRequest.getSignedTicket().getUserID()))
			throw new QInvalidActionException("You are not allowed to update a version which is locked by another user.");

		if (request.getVersionConditions() != null) {
			List<Condition> oldConditions = version.getConditions();

			Map<String, ConditionDTO> conditionDTOs = new HashMap<>();
			for (ConditionDTO conditionDTO : request.getVersionConditions()) {
				conditionDTOs.put(conditionDTO.getId(), conditionDTO);
			}

			checkConditionValidations(conditionDTOs);

			// Create conditions
			Map<String, Condition> newConditions = createConditions(
					conditionDTOs, version);

			// Loop over the old conditions to remove the ones that do
			// not exist any more
			for (Condition oldCondition : oldConditions) {
				if (!newConditions.containsKey(oldCondition.getId())) {
					em.remove(oldCondition);
				}
			}
			version.setConditions(new ArrayList<>(newConditions.values()));
		}
		version.setDescription(request.getDescription());
		version.setContent(request.getContent());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLastModifiedBy(wRequest.getSignedTicket().getUserID());
		version.setLastModifiedOn(millis);

		publishEvent(wRequest.getSignedTicket(), Constants.EVENT_UPDATE, version.getId());

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(version);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.UPDATE.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				wRequest.getSignedTicket().getUserID(), auditWorkflowVersionDTO);
	}

	private void checkConditionValidations(Map<String, ConditionDTO> conditions)
	{
		Set<String> conditionNames = new HashSet<>();
		int index = 0;
		for (ConditionDTO condition : conditions.values()) {

			// Check that the condition name is unique
			boolean isAdded = conditionNames.add(condition.getName());

			if (!isAdded) {
				QInvalidDataException myException = new QInvalidDataException();
				myException.setErrorCode("validation.error.condition.unique.name");
				myException.setInvalidDataSource("versionDetails.versionConditions[" + index + "].name");
				myException.setInvalidDataValue(condition.getName());
				throw myException;
			}

			// Check that the parent condition exists and has not been removed
			if (condition.getParentCondition() != null) {
				if (!conditions.containsKey(condition.getParentCondition()
						.getId())) {
					QInvalidDataException myException = new QInvalidDataException();
					myException.setErrorCode("validation.error.condition.is.parent");
					myException.setInvalidDataSource("versionDetails.versionConditions[" + index + "].parentCondition");
					myException.setInvalidDataValue(condition.getParentCondition().getId());
					throw myException;
				}

				//check if it is self-parent
				if (condition.getParentCondition().getId().equals(condition.getId()))
				{
					QInvalidDataException myException = new QInvalidDataException();
					myException.setErrorCode("validation.error.condition.has.parent.itself");
					myException.setInvalidDataSource("versionDetails.versionConditions[" + index + "].parentCondition");
					myException.setInvalidDataValue(condition.getParentCondition().getId());
					throw myException;
				}

				// Check that there is no cyclic dependency in the conditions
				ConditionDTO checkedCondition = condition.getParentCondition();
				while (checkedCondition != null) {
					if (checkedCondition.getId().equals(condition.getId())) {
						QInvalidDataException myException = new QInvalidDataException();
						myException.setErrorCode("validation.error.condition.cyclic.dependency");
						myException.setInvalidDataSource("versionDetails.versionConditions[" + index + "].parentCondition");
						myException.setInvalidDataValue(checkedCondition.getId());
						throw myException;
					}

					ConditionDTO childCondition = conditions.get(checkedCondition.getId());
					checkedCondition = (childCondition != null) ? childCondition.getParentCondition() : null;
				}
				index++;
			}
		}
	}

	@ValidateTicket
	@Override
	public void deleteWorkflowVersion(DeleteWorkflowVersionRequest request) {
		LOGGER.log(Level.FINE, "Deleting workflow version with ID {0}", request.getId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getId());
		if (version == null) {
			return;
		}

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, version.getWorkflow().getId(), version.getWorkflow().getProjectId());

		if (WorkflowVersion.checkWorkflowVersionLockedByOtherUser(em, version.getId(),
				request.getSignedTicket().getUserID()))
			throw new QInvalidActionException("You are not allowed to delete a version which is locked by another user.");

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(version);

		em.remove(version);

		publishEvent(request.getSignedTicket(), Constants.EVENT_DELETE, request.getId());

		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.DELETE.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditWorkflowVersionDTO);
	}

	@Override
	@ValidateTicket
	public void lockWorkflowVersion(LockWorkflowVersionRequest req) {
		LOGGER.log(Level.FINE, "Locking workflow version with ID {0}", req.getId());
		WorkflowVersion version = WorkflowVersion.find(em, req.getId());

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
				WorkflowConstants.OP_WFL_LOCK_WORKFLOW, version.getWorkflow().getId(), version.getWorkflow().getProjectId());

		if (version.getState() == State.FINAL)
			throw new QInvalidActionException("You cannot lock a workflow version which is finalised.");

		if (version.getLockedBy() != null) {
			throw new QInvalidActionException("The workflow version is already locked.");
		}

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLockedBy(req.getSignedTicket().getUserID());
		version.setLockedOn(millis);

		publishEvent(req.getSignedTicket(), Constants.EVENT_LOCK, req.getId());

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(version);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.LOCK.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				req.getSignedTicket().getUserID(), auditWorkflowVersionDTO);
	}

	@Override
	@ValidateTicket
	public void unlockWorkflowVersion(UnlockWorkflowVersionRequest req) {
		LOGGER.log(Level.FINE, "Unlocking workflow version with ID {0}", req.getId());
		String userID = req.getSignedTicket().getUserID();
		WorkflowVersion version = WorkflowVersion.find(em, req.getId());

		securityUtil.checkUnlockWorkflowOperation(req.getSignedTicket(), version.getWorkflow().getId(), version.getWorkflow().getProjectId());

		if (version.getState() == State.FINAL)
			throw new QInvalidActionException("You cannot unlock a workflow version which is finalised.");

		if (version.getLockedBy() == null) {
			throw new QInvalidActionException(
					"Workflow version is already unlocked.");
		} else if (version.getLockedBy() != null
				&& !version.getLockedBy().equals(req.getSignedTicket().getUserID())) {
			Boolean isOnWorkflowPermitted = securityService.isPermitted(new IsPermittedRequest(req.getSignedTicket(),
					WorkflowConstants.OP_WFL_UNLOCK_ANY_WORKFLOW, version.getWorkflow()
							.getId()));

			Boolean isOnProjectPermitted = securityService.isPermitted(new IsPermittedRequest(req.getSignedTicket(),
					WorkflowConstants.OP_WFL_UNLOCK_ANY_WORKFLOW, version.getWorkflow()
							.getProjectId()));

			if (!isOnWorkflowPermitted && !isOnProjectPermitted) {
				throw new QInvalidActionException(
						"The workflow version is locked by other user.");
			}
		}

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishEvent(req.getSignedTicket(), Constants.EVENT_UNLOCK, req.getId());

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(version);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.UNLOCK.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				req.getSignedTicket().getUserID(), auditWorkflowVersionDTO);
	}

	@Override
	@ValidateTicket
	public void finaliseWorkflowVersion(FinaliseWorkflowVersionRequest req) {
		LOGGER.log(Level.FINE, "Finalizing workflow version with ID {0}", req.getId());
		WorkflowVersion version = WorkflowVersion.find(em, req.getId());

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, version.getWorkflow().getId(), version.getWorkflow().getProjectId());

		if (version.getState() == State.FINAL)
			throw new QInvalidActionException("Workflow version is already finalised.");

		if (version.getLockedBy() != null
				&& !version.getLockedBy().equals(req.getSignedTicket().getUserID()))
			throw new QInvalidActionException("The workflow version is locked by other user.");

		String processId = findProcessIdFromVersionContent(version.getContent());
		if (processId != null && processId != "")
		{
			if (WorkflowVersion.processIdExists(em, processId))
				throw new QInvalidActionException("The id of process ypu provided already exists in another final or enabled testing workflow version.");

			version.setState(State.FINAL);
			version.setLockedBy(null);
			version.setLockedOn(null);
			version.setProcessId(processId);
		}
		else
			throw new QInvalidActionException("The BPMN content of workflow version does not contain the element id of the process or it is empty.");

		publishEvent(req.getSignedTicket(), Constants.EVENT_FINALISE, req.getId());

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(version);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.FINALISE.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				req.getSignedTicket().getUserID(), auditWorkflowVersionDTO);
	}

	@Override
    @ValidateTicket
    public void enableTestingWorkflowVersion(EnableTestingVersionRequest req) {
		if (req.isEnableTesting())
			LOGGER.log(Level.FINE, "Enable testing for workflow version with ID {0}", req.getId());
		else
			LOGGER.log(Level.FINE, "Disable testing for workflow version with ID {0}", req.getId());

		WorkflowVersion version = WorkflowVersion.find(em, req.getId());

		securityUtil.checkWorkflowOperation(req.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, version.getWorkflow().getId(), version.getWorkflow().getProjectId());

		if (version.getState() == State.FINAL)
			throw new QInvalidActionException("Workflow version is already finalised.");

		if (version.getLockedBy() != null
				&& !version.getLockedBy().equals(req.getSignedTicket().getUserID()))
			throw new QInvalidActionException("The workflow version is locked by other user.");
		String processId = null;

		if (req.isEnableTesting())
		{
			processId = findProcessIdFromVersionContent(version.getContent());
			if (processId != null && processId != "")
			{
				if (WorkflowVersion.processIdExists(em, processId))
					throw new QInvalidActionException("The id of process ypu provided already exists in another final or enabled testing workflow version.");
			}
			else
				throw new QInvalidActionException("The BPMN content of workflow version does not contain the element id of the process or it is empty.");
		}
		version.setEnableTesting(req.isEnableTesting());
		version.setProcessId(processId);

		if (req.isEnableTesting())
			publishEvent(req.getSignedTicket(), Constants.EVENT_ENABLE_TESTING, req.getId());
		else
			publishEvent(req.getSignedTicket(), Constants.EVENT_DISABLE_TESTING, req.getId());

		AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
				workflowVersionToAuditWorkflowVersionDetailsDTO(version);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	req.isEnableTesting() ? EVENT.ENABLE_TESTING.toString() : EVENT.DISABLE_TESTING.toString(),
				GROUP.WORKFLOW_VERSION.toString(), null,
				req.getSignedTicket().getUserID(), auditWorkflowVersionDTO);
    }

	@Override
	@ValidateTicket
	public List<Integer> getConditionTypes(EmptySignedRequest request) {
		LOGGER.log(Level.FINE, "Getting condition types.");
		List<Integer> conditionTypes = new ArrayList<>();

		for (ConditionType type : ConditionType.values()) {
			conditionTypes.add(type.ordinal());
		}
		return conditionTypes;
	}

	@Override
	@ValidateTicket
	public String importWorkflowVersion(ImportWorkflowVersionRequest request) {
		String retVal = null;

		try {
			ByteArrayInputStream reader = new ByteArrayInputStream(request.getVersionContent());
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLWorkflowVersionDTO.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			XMLWorkflowVersionDTO importVersion = (XMLWorkflowVersionDTO) jaxbUnmarshaller.unmarshal(reader);
			System.out.println(importVersion);

			Workflow workflow = Workflow.find(em, request.getWorkflowId());

			securityUtil.checkWorkflowOperation(request.getSignedTicket(),
					WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, workflow.getId(), workflow.getProjectId());

			DateTime now = DateTime.now();
			long millis = now.getMillis();
			WorkflowVersion workflowVersion = new WorkflowVersion();
			workflowVersion.setName(importVersion.getName());
			workflowVersion.setDescription(importVersion.getDescription());
			workflowVersion.setContent(importVersion.getContent());
			workflowVersion.setProcessId(importVersion.getProcessId());
			workflowVersion.setWorkflow(workflow);
			workflowVersion.setState(State.DRAFT);
			workflowVersion.setCreatedBy(request.getSignedTicket().getUserID());
			workflowVersion.setCreatedOn(millis);
			workflowVersion.setLastModifiedBy(request.getSignedTicket().getUserID());
			workflowVersion.setLastModifiedOn(millis);

			em.persist(workflowVersion);

			if (importVersion.getConditions() != null) {
				// create conditions
				Map<String, ConditionDTO> conditionDTOs = new HashMap<>();
				List <ConditionDTO> conditionsFromXML = converterUtil.XMLConditionDTOToConditionDTOList(importVersion.getConditions().getConditions(), workflow.getProjectId(), request.getSignedTicket());
				for (ConditionDTO conditionDTO : conditionsFromXML) {
					conditionDTOs.put(conditionDTO.getId(), conditionDTO);
				}

				checkConditionValidations(conditionDTOs);

				Map<String, Condition> conditions = createConditions(conditionDTOs,	workflowVersion);

				workflowVersion.setConditions(new ArrayList(conditions.values()));
			}

			retVal = workflowVersion.getId();
			publishEvent(request.getSignedTicket(), Constants.EVENT_CREATE, workflowVersion.getId());

			AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
					workflowVersionToAuditWorkflowVersionDetailsDTO(workflowVersion);
			auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.IMPORT.toString(),
					GROUP.WORKFLOW_VERSION.toString(), null,
					request.getSignedTicket().getUserID(), auditWorkflowVersionDTO);

		} catch (JAXBException e) {
			 LOGGER.log(Level.SEVERE, "Could not marshal initial data", e);
			 throw new QInvalidActionException(e.toString());
		}
		return retVal;
	}

	@Override
	@ValidateTicket
	public byte[] exportWorkflowVersion(ExportWorkflowVersionRequest request) {
		LOGGER.log(Level.FINE, "Exporting workflow version with ID {0}", request.getVersionId());
		WorkflowVersion version = WorkflowVersion.find(em, request.getVersionId());
		ByteArrayOutputStream writer = new ByteArrayOutputStream();

		if (version != null) {
			securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_VIEW_WORKFLOW, version.getWorkflow().getId(),
				version.getWorkflow().getProjectId());

			XMLWorkflowVersionDTO exportVersion = converterUtil.workflowVersionToXMLWorkflowVersionDTO(version, request.getSignedTicket());

			try {
				JAXBContext context = JAXBContext.newInstance(XMLWorkflowVersionDTO.class);
				Marshaller jaxbMarshaller = context.createMarshaller();
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				jaxbMarshaller.marshal(exportVersion, writer);

			 } catch (JAXBException e) {
				 LOGGER.log(Level.SEVERE, "Could not marshal initial data", e);
				 throw new QInvalidActionException(e.toString());
			 }

			AuditWorkflowVersionDTO auditWorkflowVersionDTO = converterUtil.
					workflowVersionToAuditWorkflowVersionDetailsDTO(version);
			auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(),	EVENT.EXPORT.toString(),
					GROUP.WORKFLOW_VERSION.toString(), null,
					request.getSignedTicket().getUserID(), auditWorkflowVersionDTO);
		}

		return writer.toByteArray();
	}

	private String findProcessIdFromVersionContent(String content)
	{
		String processId = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document document = db.parse(new InputSource(new StringReader(content)));
	        NodeList nodeList = document.getElementsByTagNameNS("*", "process");
	        if (nodeList.getLength() > 0)
	        	if (nodeList.item(0).getAttributes().getNamedItem("id")!=null)
	        		processId = nodeList.item(0).getAttributes().getNamedItem("id").getNodeValue();
	        	else
	        		LOGGER.log(Level.INFO, "findProcessIdFromVersionContent: id attribute not found.");
	        else
        		LOGGER.log(Level.INFO, "findProcessIdFromVersionContent: process element not found.");
	        LOGGER.log(Level.INFO, "ProcessId: " + processId);
		}
		catch(Exception e)
		{
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw new QInvalidActionException(e.toString());
		}

		return processId;
	}

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setSecurityUtil(SecurityUtil securityUtil) {
		this.securityUtil = securityUtil;
	}

	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}

	public void setEventPublisher(EventPublisherService eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setWorkingSetsServiceList(
			List<WorkingSetsService> workingSetsServiceList) {
		this.workingSetsServiceList = workingSetsServiceList;
	}

	public void setRulesServiceList(List<RulesService> rulesServiceList) {
		this.rulesServiceList = rulesServiceList;
	}

	private void publishEvent(SignedTicket signedTicket, String event, String workflowVersionId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_WORKFLOW_VERSION_ID, workflowVersionId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/be/workflow/"
				+ Constants.RESOURCE_TYPE_WORKFLOW_VERSION + "/" + event);
	}
}
