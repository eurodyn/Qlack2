package com.eurodyn.qlack2.be.workflow.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.ProjectListCriteriaBuilder;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.SortColumn;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.SortType;
import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectsRequest;
import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetProjectRuleVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetProjectWorkingSetVersionsRequest;
import com.eurodyn.qlack2.be.workflow.api.ProjectService;
import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.ProjectResourcesDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.workflow.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoriesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectResourcesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectWorkingSetsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetWorkingSetRulesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.UpdateRecentProjectRequest;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditProjectDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Category;
import com.eurodyn.qlack2.be.workflow.impl.model.RecentProject;
import com.eurodyn.qlack2.be.workflow.impl.model.Workflow;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.workflow.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.SecureOperation;
import com.eurodyn.qlack2.be.workflow.impl.util.WorkflowConstants;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;


public class ProjectServiceImpl implements ProjectService {
	
	private static final Logger LOGGER = Logger.getLogger(ProjectServiceImpl.class.getName());
	
	private EntityManager em;
	private IDMService idmService;
	private ConverterUtil converterUtil;
	private SecurityService securityService;
	private com.eurodyn.qlack2.be.explorer.api.ProjectService projectExplorerService;
	private List<WorkingSetsService> workingSetsServiceList;
	private List<RulesService> rulesServiceList;
	private AuditClientService auditClientService;

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}
	
	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}
	
	public void setprojectExplorerService(com.eurodyn.qlack2.be.explorer.api.ProjectService projectExplorerService) {
		this.projectExplorerService = projectExplorerService;
	}
	
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
	
	public void setWorkingSetsServiceList(
			List<WorkingSetsService> workingSetsServiceList) {
		this.workingSetsServiceList = workingSetsServiceList;
	}

	public void setRulesServiceList(List<RulesService> rulesServiceList) {
		this.rulesServiceList = rulesServiceList;
	}
	
	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}

	@Override
	@ValidateTicket
	public List<ProjectDTO> getProjects(EmptySignedRequest request) {
		LOGGER.log(Level.FINE, "Retrieving list of projects.");
		
		ProjectListCriteriaBuilder criteriaBuilder = ProjectListCriteriaBuilder
				.createCriteria();
		criteriaBuilder.withActive(true);
		criteriaBuilder.withWorkflows(true);
		criteriaBuilder.sortByColumn(SortColumn.NAME, SortType.ASCENDING);

		ProjectListCriteria criteria = criteriaBuilder.build();

		GetProjectsRequest explorerRequest = new GetProjectsRequest(criteria);
		explorerRequest.setSignedTicket(request.getSignedTicket());
		List<ProjectDTO> projects = projectExplorerService.getProjects(explorerRequest);

		return projects;
	}
	
	@Override
	@ValidateTicket
	public List<ProjectDTO> getRecentProjects(GetRecentProjectsRequest request) {
		LOGGER.log(Level.FINE, "Retrieving list of recent projects.");
		
		String userId = request.getSignedTicket().getUserID();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RecentProject> cq = cb.createQuery(RecentProject.class);
		Root<RecentProject> root = cq.from(RecentProject.class);

		// Filter projects based on criteria
		if (userId != null) {
			Predicate pr = cb.equal(root.get(WorkflowConstants.FIELD_LAST_ACCESSED_BY), userId);
			cq = addPredicate(cq, cb, pr);
		}

		// Apply sorting.
		if (request.getSort() != null) {
			Expression orderExpr = root.get(request.getSort());
			Order order = (WorkflowConstants.SORT_ASCENDING.equals(request.getOrder())) ? cb.asc(orderExpr)
					: cb.desc(orderExpr);
			cq = cq.orderBy(order);
		}

		TypedQuery<RecentProject> query = em.createQuery(cq);
		// Apply pagination
		if (request.getStart() != null && request.getSize() != null) {
			query.setFirstResult(request.getStart() * request.getSize());
			query.setMaxResults(request.getSize());
		}

		List<RecentProject> recentProjects = query.getResultList();

		List<ProjectDTO> retVal = new ArrayList<>(recentProjects.size());
		for (RecentProject recentProject : recentProjects) {
			GetProjectRequest explorerRequest = new GetProjectRequest(
					recentProject.getProjectId());
			explorerRequest.setSignedTicket(request.getSignedTicket());

			retVal.add(projectExplorerService.getProject(explorerRequest));
		}

		return retVal;
	}

	private <T> CriteriaQuery<T> addPredicate(CriteriaQuery<T> query,
			CriteriaBuilder cb, Predicate pr) {
		CriteriaQuery<T> cq = query;
		if (cq.getRestriction() != null) {
			cq = cq.where(cb.and(cq.getRestriction(), pr));
		} else {
			cq = cq.where(pr);
		}
		return cq;
	}

	@Override
	@ValidateTicket
	public ProjectDTO getProject(com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectRequest request) {
		LOGGER.log(Level.FINE, "Retrieving project with ID {0}.", request.getProjectId());
		
		String projectId = request.getProjectId();

		GetProjectRequest projectRequest = new GetProjectRequest(projectId);
		projectRequest.setSignedTicket(request.getSignedTicket());

		return projectExplorerService.getProject(projectRequest);
	}

	@Override
	@ValidateTicket
	public void updateRecentProject(UpdateRecentProjectRequest request) {
		LOGGER.log(Level.FINE, "Updating recent projects.");
		
		String userId = request.getSignedTicket().getUserID();
		String projectId = request.getProjectId();

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		RecentProject recentProject = RecentProject
				.getRecentProjectByProjectIdAndLastAccessedBy(em, projectId,
						userId);
		if (recentProject != null) {
			recentProject.setLastAccessedOn(millis);
		} else {
			//The user has not recently accessed this project -> create a new entry
			recentProject = new RecentProject();
			recentProject.setProjectId(projectId);
			recentProject.setLastAccessedBy(userId);
			recentProject.setLastAccessedOn(millis);

			em.persist(recentProject);

			//If there exist more than 4 recently used projects for the specified user,
			//then remove the oldest project, to keep the list of recently accessed projects small.
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RecentProject> cq = cb
					.createQuery(RecentProject.class);
			Root<RecentProject> root = cq.from(RecentProject.class);

			// Filter projects based on criteria
			if (userId != null) {
				Predicate pr = cb.equal(root.get(WorkflowConstants.FIELD_LAST_ACCESSED_BY), userId);
				cq = addPredicate(cq, cb, pr);
			}

			// Apply sorting.
			cq = cq.orderBy(cb.desc(root.get(WorkflowConstants.FIELD_LAST_ACCESSED_ON)));

			TypedQuery<RecentProject> query = em.createQuery(cq);
			List<RecentProject> recentProjects = query.getResultList();

			if (recentProjects.size() > WorkflowConstants.RECENT_PROJECTS_SIZE) {
				int index = recentProjects.size() - 1;
				while (index >= WorkflowConstants.RECENT_PROJECTS_SIZE) {
					RecentProject rp = recentProjects.get(index);
					em.remove(rp);
					index--;
				}
			}
		}
	}
	
	@Override
	@ValidateTicket
	public ProjectResourcesDTO getProjectResources(GetProjectResourcesRequest req) {
		LOGGER.log(Level.FINE, "Retrieving list of resources for project with ID {0}.", req.getProjectId());
		
		String userID = req.getSignedTicket().getUserID();
		// fetch and map
		SignedTicket signedTicket = req.getSignedTicket();

		GetProjectRequest projectRequest = new GetProjectRequest(req.getProjectId());
		projectRequest.setSignedTicket(req.getSignedTicket());

		ProjectDTO project = projectExplorerService.getProject(projectRequest);
		ProjectResourcesDTO projectDTO = new ProjectResourcesDTO();
		projectDTO.setId(req.getProjectId());
		projectDTO.setName(project.getName());
		
		List<Category> categories = Category.findByProjectId(em, req.getProjectId());
		List<CategoryDTO> categoryDTOs = new ArrayList<>();
		
		for (Category category : categories) {
			CategoryDTO categoryDTO = converterUtil.categoryToCategoryDTO(category, true);

			if (category.getWorkflows() != null && !category.getWorkflows().isEmpty()) {
				List<WorkflowDTO> workflowDTOs = new ArrayList<>();

				for (Workflow workflow : category.getWorkflows()) {
					Boolean isOnWorkflowPermitted = securityService.isPermitted(new IsPermittedRequest(req.getSignedTicket(),
							WorkflowConstants.OP_WFL_VIEW_WORKFLOW, workflow.getId()));

					boolean permitted = false;
					if (isOnWorkflowPermitted == null) {
						Boolean isOnProjectPermitted = securityService
								.isPermitted(new IsPermittedRequest(signedTicket,
													SecureOperation.WFL_VIEW_WORKFLOW.toString(), 
													workflow.getProjectId()));
						if (isOnProjectPermitted != null && isOnProjectPermitted)
							permitted = true;
					} else if (isOnWorkflowPermitted)
						permitted = true;

					if (permitted) {
						WorkflowDTO workflowDTO = converterUtil.workflowToWorkflowDTO(workflow);
						if (workflowDTO != null) {
							workflowDTOs.add(workflowDTO);
						}
					}
				}
				categoryDTO.setWorkflows(workflowDTOs);
			}
			categoryDTOs.add(categoryDTO);
		}
		projectDTO.setCategories(categoryDTOs);
		
		// Retrieve uncategorised workflows
		List<Workflow> workflows = Workflow.findUncategorizedWorkflowsByProjectId(em, req.getProjectId());
		List<WorkflowDTO> workflowDTOs = new ArrayList<>();
		for (Workflow workflow : workflows) {
			Boolean isOnWorkflowPermitted = securityService.isPermitted(new IsPermittedRequest(req.getSignedTicket(),
					WorkflowConstants.OP_WFL_VIEW_WORKFLOW, workflow.getId()));

			boolean permitted = false;
			if (isOnWorkflowPermitted == null) {
				Boolean isOnProjectPermitted = securityService
						.isPermitted(new IsPermittedRequest(signedTicket,
								SecureOperation.WFL_VIEW_WORKFLOW.toString(), 
								workflow.getProjectId()));
				if (isOnProjectPermitted != null && isOnProjectPermitted)
					permitted = true;
			} else if (isOnWorkflowPermitted) 
				permitted = true;

			if (permitted) {
				WorkflowDTO workflowDTO = converterUtil.workflowToWorkflowDTO(workflow);
				if (workflowDTO != null) {
					workflowDTOs.add(workflowDTO);
				}
			}
		}
		projectDTO.setWorkflows(workflowDTOs);
		
		//Update recent project
		if (req.isUpdateRecentProjects()) {
			UpdateRecentProjectRequest recentProjectReq = new UpdateRecentProjectRequest();
			recentProjectReq.setProjectId(req.getProjectId());
			recentProjectReq.setSignedTicket(req.getSignedTicket());
			updateRecentProject(recentProjectReq);
		}
		
		AuditProjectDTO auditProjectDTO = converterUtil.projectToAuditProjectDTO(project);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.VIEW.toString(), 
				GROUP.PROJECT.toString(), null, req.getSignedTicket().getUserID(), auditProjectDTO);
				
		return projectDTO;
	}
	
	@Override
	public List<CategoryDTO> getCategoriesByProjectId(GetCategoriesRequest req) {
		LOGGER.log(Level.FINE, "Retrieving categories for project with ID {0}.", req.getProjectId());
	
		return converterUtil.categoryToCategoryDTOList(Category.findByProjectId(em, req.getProjectId()), req.getSignedTicket(), false);
	}
	
	@Override
	@ValidateTicket
	public List<WorkingSetDTO> getProjectWorkingSets(GetProjectWorkingSetsRequest req) {
		LOGGER.log(Level.FINE, "Retrieving list of working sets for project with ID {0}.", req.getProjectId());
		
		if (workingSetsServiceList.size() == 0) {
			return null;
		}
		
		String projectId = req.getProjectId();

		GetProjectWorkingSetVersionsRequest workingSetsRequest = new GetProjectWorkingSetVersionsRequest(projectId);
		workingSetsRequest.setSignedTicket(req.getSignedTicket());
		List<WorkingSetVersionIdentifierDTO> workingSetVersions = workingSetsServiceList.get(0).getProjectWorkingSetVersions(workingSetsRequest);

		return converterUtil.workingSetVersionIdentifierDTOsToWorkingSetDTOList(workingSetVersions);
	}

	@Override
	@ValidateTicket
	public List<RuleDTO> getWorkingSetRules(GetWorkingSetRulesRequest req) {		
		LOGGER.log(Level.FINE, "Retrieving list of rules for project with ID {0}.", req.getProjectId());
		
		if (rulesServiceList.size() == 0) {
			return null;
		}
		
		String projectId = req.getProjectId();
		String workingSetId = req.getWorkingSetId();
		
		GetProjectRuleVersionsRequest rulesRequest = new GetProjectRuleVersionsRequest(projectId);
		rulesRequest.setSignedTicket(req.getSignedTicket());
		List<RuleVersionIdentifierDTO> ruleVersions = rulesServiceList.get(0).getProjectRuleVersions(rulesRequest);

		return converterUtil.ruleVersionIdentifierDTOsToRuleDTOList(ruleVersions);
	}
}
