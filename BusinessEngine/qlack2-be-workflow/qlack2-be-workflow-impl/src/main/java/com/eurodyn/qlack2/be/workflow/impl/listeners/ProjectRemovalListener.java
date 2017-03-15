package com.eurodyn.qlack2.be.workflow.impl.listeners;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.eurodyn.qlack2.be.explorer.api.util.Constants;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditCategoryDTO;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditWorkflowDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Category;
import com.eurodyn.qlack2.be.workflow.impl.model.RecentProject;
import com.eurodyn.qlack2.be.workflow.impl.model.Workflow;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.workflow.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.RuntimeUtil;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;

public class ProjectRemovalListener implements EventHandler {
	private static final Logger LOGGER = Logger
			.getLogger(ProjectRemovalListener.class.getName());

	private EntityManager em;
	private AuditClientService auditClientService;
	private ConverterUtil converterUtil;
	private SecurityService securityService;
	private RuntimeUtil runtimeUtil;

	@Override
	public void handleEvent(Event event) {
		String projectId = (String) event.getProperty(Constants.EVENT_DATA_PROJECT_ID);
		LOGGER.log(Level.FINE, "Project with ID " + projectId
				+ " has been removed from Projects Explorer. "
				+ "Project resources will be removed from Workflow Manager");

		// Delete project from recent project
		RecentProject recentProject = RecentProject
				.getRecentProjectByProjectId(em, projectId);
		if (recentProject != null) {
			em.remove(recentProject);
		}

		// Delete all project resources
		List<Category> categories = Category.findByProjectId(em, projectId);
		for (Category category : categories)
		{
			AuditCategoryDTO auditCategoryDTO = converterUtil.categoryToAuditCategoryDTO(category);
			em.remove(category);	
			auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.DELETE.toString(), GROUP.CATEGORY.toString(),
					null, "SYSTEM", auditCategoryDTO);			
		}

		List<Workflow> workflows = Workflow.findByProjectId(em, projectId);
		for (Workflow workflow : workflows)
		{
			AuditWorkflowDTO auditWorkflowDTO = converterUtil.workflowToAuditWorkflowDTO(workflow);		
			runtimeUtil.deleteWorkflowInstancesForWorkflow(workflow, "SYSTEM");		
			em.remove(workflow);		
			DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(workflow.getId());
			securityService.deleteSecureResource(resourceRequest);			
			auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.DELETE.toString(), GROUP.WORKFLOW.toString(),
					null, "SYSTEM", auditWorkflowDTO);
		}
		
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}
	
	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}
	
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
	
	public void setRuntimeUtil(RuntimeUtil runtimeUtil) {
		this.runtimeUtil = runtimeUtil;
	}

}
