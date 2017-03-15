package com.eurodyn.qlack2.be.workflow.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.workflow.api.RuntimeService;
import com.eurodyn.qlack2.be.workflow.api.WorkflowService;
import com.eurodyn.qlack2.be.workflow.api.WorkflowVersionService;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.be.workflow.api.request.runtime.DeleteWorkflowInstancesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.version.UpdateWorkflowVersionRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.CreateWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.DeleteWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.GetWorkflowByNameRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.GetWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.GetWorkflowsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.UpdateWorkflowRequest;
import com.eurodyn.qlack2.be.workflow.api.util.Constants;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Category;
import com.eurodyn.qlack2.be.workflow.impl.model.Workflow;
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
import com.eurodyn.qlack2.webdesktop.api.request.security.CreateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.UpdateSecureResourceRequest;


public class WorkflowServiceImpl implements WorkflowService {

	private static final Logger LOGGER = Logger.getLogger(WorkflowServiceImpl.class.getName());

	private IDMService idmService;
	private EntityManager em;
	private WorkflowVersionService workflowVersionService;
	private RuntimeService runtimeService;
	private SecurityUtil securityUtil;
	private ConverterUtil converterUtil;
	private SecurityService securityService;
	private AuditClientService auditClientService;
	private EventPublisherService eventPublisher;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setWorkflowVersionService(WorkflowVersionService workflowVersionService) {
		this.workflowVersionService = workflowVersionService;
	}

	public WorkflowVersionService getWorkflowVersionService() {
		return workflowVersionService;
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
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

	@ValidateTicket
	@Override
	public List<WorkflowDTO> getWorkflows(GetWorkflowsRequest req) {
		LOGGER.log(Level.FINE, "Getting workflows for project with ID {0}", req.getProjectId());

		return converterUtil.workflowToWorkflowDTOList(Workflow.findByProjectId(em, req.getProjectId()));
	}

	@ValidateTicket
	@Override
	public WorkflowDTO getWorkflow(GetWorkflowRequest req) {
		LOGGER.log(Level.FINE, "Getting workflow with ID {0}", req.getWorkflowId());

		Workflow workflow = Workflow.find(em, req.getWorkflowId());

		if (workflow == null)
			return null;

		AuditWorkflowDTO auditWorkflowDTO = converterUtil.workflowToAuditWorkflowDTO(workflow);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.VIEW
				.toString(), GROUP.WORKFLOW.toString(), null, req.getSignedTicket().getUserID(), auditWorkflowDTO);

		return converterUtil.workflowToWorkflowDTO(workflow);
	}

	@ValidateTicket
	@Override
	public WorkflowDTO getWorkflowByName(GetWorkflowByNameRequest request) {
		LOGGER.log(Level.FINE, "Getting workflow with name {0}", request.getName());
		String workflowName = request.getName();

		Workflow workflow = Workflow.findByName(em, workflowName);
		if (workflow == null)
			return null;

		AuditWorkflowDTO auditWorkflowDTO = converterUtil.workflowToAuditWorkflowDTO(workflow);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.VIEW
				.toString(), GROUP.WORKFLOW.toString(), null, request.getSignedTicket().getUserID(), auditWorkflowDTO);

		return converterUtil.workflowToWorkflowDTO(workflow);
	}

	@ValidateTicket
	@Override
	public String createWorkflow(CreateWorkflowRequest request) {
		LOGGER.log(Level.FINE, "Creating workflow with name {0}", request.getName());
		securityUtil.checkCreateWorkflowOperation(request.getSignedTicket(), request.getProjectId());

		Workflow workflow = new Workflow();
		String workflowId = workflow.getId();
		workflow.setProjectId(request.getProjectId());
		workflow.setName(request.getName());
		workflow.setDescription(request.getDescription());
		workflow.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		workflow.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		workflow.setCreatedBy(request.getSignedTicket().getUserID());
		workflow.setCreatedOn(millis);
		workflow.setLastModifiedBy(request.getSignedTicket().getUserID());
		workflow.setLastModifiedOn(millis);

		em.persist(workflow);
		// Create resource
		CreateSecureResourceRequest resourceRequest = new CreateSecureResourceRequest(
				workflow.getId(), workflow.getName(), "Workflow");
		securityService.createSecureResource(resourceRequest);

		publishEvent(request.getSignedTicket(), Constants.EVENT_CREATE, workflowId);

		AuditWorkflowDTO auditWorkflowDTO = converterUtil.workflowToAuditWorkflowDTO(workflow);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.CREATE.toString(), GROUP.WORKFLOW.toString(),
				null, request.getSignedTicket().getUserID(), auditWorkflowDTO);

		return workflowId;
	}

	@ValidateTicket
	@Override
	public void updateWorkflow(UpdateWorkflowRequest request) {
		LOGGER.log(Level.FINE, "Updating workflow with ID {0}", request.getId());

		Workflow workflow = Workflow.find(em, request.getId());
		if (workflow == null) {
			return;
		}

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, workflow.getId(),
				workflow.getProjectId());

		workflow.setName(request.getName());
		workflow.setDescription(request.getDescription());
		workflow.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		workflow.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		workflow.setLastModifiedBy(request.getSignedTicket().getUserID());
		workflow.setLastModifiedOn(millis);

		UpdateSecureResourceRequest resourceRequest = new UpdateSecureResourceRequest(
				workflow.getId(), workflow.getName(), "Workflow");
		securityService.updateSecureResource(resourceRequest);

		UpdateWorkflowVersionRequest versionRequest = request.getVersionRequest();
		if (versionRequest != null && versionRequest.getId() != null)
			workflowVersionService.updateWorkflowVersion(request);

		publishEvent(request.getSignedTicket(), Constants.EVENT_UPDATE, workflow.getId());

		AuditWorkflowDTO auditWorkflowDTO = converterUtil.workflowToAuditWorkflowDTO(workflow);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.UPDATE.toString(), GROUP.WORKFLOW.toString(),
				null, request.getSignedTicket().getUserID(), auditWorkflowDTO);
	}

	@ValidateTicket
	@Override
	public void deleteWorkflow(DeleteWorkflowRequest request) {
		LOGGER.log(Level.FINE, "Deleting workflow with ID {0}", request.getId());

		Workflow workflow = Workflow.find(em, request.getId());

		if (workflow == null) {
			return;
		}

		securityUtil.checkWorkflowOperation(request.getSignedTicket(),
				WorkflowConstants.OP_WFL_MANAGE_WORKFLOW, workflow.getId(),
				workflow.getProjectId());

		AuditWorkflowDTO auditWorkflowDTO = converterUtil.workflowToAuditWorkflowDTO(workflow);

		DeleteWorkflowInstancesRequest deleteinstancesRequest = new DeleteWorkflowInstancesRequest();
		deleteinstancesRequest.setWorkflowId(request.getId());
		deleteinstancesRequest.setSignedTicket(request.getSignedTicket());
		runtimeService.deleteWorkflowInstancesForWorkflow(deleteinstancesRequest);

		em.remove(workflow);

		// Delete resource from aaa
		DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(request.getId());
		securityService.deleteSecureResource(resourceRequest);

		publishEvent(request.getSignedTicket(), Constants.EVENT_DELETE, request.getId());

		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.DELETE.toString(), GROUP.WORKFLOW.toString(),
				null, request.getSignedTicket().getUserID(), auditWorkflowDTO);
	}

	private void publishEvent(SignedTicket signedTicket, String event, String workflowId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_WORKFLOW_ID, workflowId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/be/workflow/"
				+ Constants.RESOURCE_TYPE_WORKFLOW + "/" + event);
	}
}
