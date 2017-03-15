package com.eurodyn.qlack2.be.forms.impl;

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

import com.eurodyn.qlack2.be.explorer.api.ProjectService;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.ProjectListCriteriaBuilder;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.SortColumn;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.SortType;
import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectsRequest;
import com.eurodyn.qlack2.be.forms.api.ProjectsService;
import com.eurodyn.qlack2.be.forms.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.forms.api.dto.FormDTO;
import com.eurodyn.qlack2.be.forms.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.forms.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.forms.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectCategoriesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectResourcesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectWorkingSetsRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetWorkingSetRulesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.UpdateRecentProjectRequest;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditProjectDTO;
import com.eurodyn.qlack2.be.forms.impl.model.Category;
import com.eurodyn.qlack2.be.forms.impl.model.Form;
import com.eurodyn.qlack2.be.forms.impl.model.RecentProject;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.forms.impl.util.Constants;
import com.eurodyn.qlack2.be.forms.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.forms.impl.util.SecureOperation;
import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetProjectRuleVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetProjectWorkingSetVersionsRequest;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;

public class ProjectsServiceImpl implements ProjectsService {
	private static final Logger LOGGER = Logger
			.getLogger(ProjectsServiceImpl.class.getName());

	private ProjectService projectExplorerService;

	private IDMService idmService;

	private ConverterUtil converterUtil;

	private SecurityService securityService;

	private List<WorkingSetsService> workingSetsServiceList;

	private List<RulesService> rulesServiceList;

	private AuditClientService auditClientService;

	private EntityManager em;

	@Override
	@ValidateTicket
	public List<ProjectDTO> getProjects(EmptySignedRequest request) {
		LOGGER.log(Level.FINE,
				"Retrieving list of projects by requesting them from projects explorer");

		ProjectListCriteriaBuilder criteriaBuilder = ProjectListCriteriaBuilder
				.createCriteria();
		criteriaBuilder.withActive(true);
		criteriaBuilder.withForms(true);
		criteriaBuilder.sortByColumn(SortColumn.NAME, SortType.ASCENDING);

		ProjectListCriteria criteria = criteriaBuilder.build();

		GetProjectsRequest explorerRequest = new GetProjectsRequest(criteria);
		explorerRequest.setSignedTicket(request.getSignedTicket());
		List<ProjectDTO> projects = projectExplorerService
				.getProjects(explorerRequest);

		return projects;
	}

	@Override
	@ValidateTicket
	public List<ProjectDTO> getRecentProjects(GetRecentProjectsRequest request) {
		LOGGER.log(Level.FINE,
				"Retrieving list of recently accessed projects in forms component");

		String userId = request.getSignedTicket().getUserID();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RecentProject> cq = cb.createQuery(RecentProject.class);
		Root<RecentProject> root = cq.from(RecentProject.class);

		// Filter projects based on criteria
		if (userId != null) {
			Predicate pr = cb.equal(root.get(Constants.FIELD_LAST_ACCESSED_BY),
					userId);
			cq = addPredicate(cq, cb, pr);
		}

		// Apply sorting.
		if (request.getSort() != null) {
			Expression orderExpr = root.get(request.getSort());
			Order order = (Constants.SORT_ASCENDING.equals(request.getOrder())) ? cb
					.asc(orderExpr) : cb.desc(orderExpr);
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
	public ProjectDTO getProject(
			com.eurodyn.qlack2.be.forms.api.request.project.GetProjectRequest request) {
		LOGGER.log(
				Level.FINE,
				"Getting project with ID {0} by requesting it from projects explorer",
				request.getProjectId());

		String projectId = request.getProjectId();

		GetProjectRequest projectRequest = new GetProjectRequest(projectId);
		projectRequest.setSignedTicket(request.getSignedTicket());

		return projectExplorerService.getProject(projectRequest);
	}

	@Override
	@ValidateTicket
	public void updateRecentProject(UpdateRecentProjectRequest request) {
		LOGGER.log(
				Level.FINE,
				"Adding project with ID {0} to the list of recently accessed projects",
				request.getProjectId());

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
			// The user has not recently accessed this project -> create a new
			// entry
			recentProject = new RecentProject();
			recentProject.setProjectId(projectId);
			recentProject.setLastAccessedBy(userId);
			recentProject.setLastAccessedOn(millis);

			em.persist(recentProject);

			// If there exist more than 4 recently used projects for the
			// specified user,
			// then remove the oldest project, to keep the list of recently
			// accessed projects small.
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RecentProject> cq = cb
					.createQuery(RecentProject.class);
			Root<RecentProject> root = cq.from(RecentProject.class);

			// Filter projects based on criteria
			if (userId != null) {
				Predicate pr = cb.equal(
						root.get(Constants.FIELD_LAST_ACCESSED_BY), userId);
				cq = addPredicate(cq, cb, pr);
			}

			// Apply sorting.
			cq = cq.orderBy(cb.desc(root.get(Constants.FIELD_LAST_ACCESSED_ON)));

			TypedQuery<RecentProject> query = em.createQuery(cq);
			List<RecentProject> recentProjects = query.getResultList();

			if (recentProjects.size() > Constants.RECENT_PROJECTS_SIZE) {
				int index = recentProjects.size() - 1;
				while (index >= Constants.RECENT_PROJECTS_SIZE) {
					RecentProject rp = recentProjects.get(index);
					em.remove(rp);
					index--;
				}
			}
		}
	}

	@Override
	@ValidateTicket
	public ProjectDetailsDTO getProjectResources(
			GetProjectResourcesRequest request) {
		LOGGER.log(Level.FINE, "Retrieving resources for project with ID {0}",
				request.getProjectId());

		String projectId = request.getProjectId();

		SignedTicket signedTicket = request.getSignedTicket();

		GetProjectRequest projectRequest = new GetProjectRequest(projectId);
		projectRequest.setSignedTicket(request.getSignedTicket());

		ProjectDTO project = projectExplorerService.getProject(projectRequest);

		// fetch and map
		ProjectDetailsDTO projectDTO = new ProjectDetailsDTO();
		projectDTO.setId(projectId);
		projectDTO.setName(project.getName());

		List<Category> categories = Category.getCategoriesForProjectId(em,
				projectId);
		List<CategoryDTO> categoryDTOs = new ArrayList<>();

		for (Category category : categories) {
			CategoryDTO categoryDTO = converterUtil.categoryToCategoryDTO(
					category, request.getSignedTicket());

			if (category.getForms() != null && !category.getForms().isEmpty()) {
				List<FormDTO> formDTOs = new ArrayList<>();

				for (Form form : category.getForms()) {
					// Check that the operation FRM_VIEW_FORM is allowed for
					// each form or the operation FRM_VIEW_FORM is allowed for
					// the project of the form.
					Boolean isOnFormPermitted = securityService
							.isPermitted(new IsPermittedRequest(signedTicket,
									SecureOperation.FRM_VIEW_FORM.toString(),
									form.getId()));

					boolean permitted = false;
					if (isOnFormPermitted == null) {
						Boolean isOnProjectPermitted = securityService
								.isPermitted(new IsPermittedRequest(
										signedTicket,
										SecureOperation.FRM_VIEW_FORM
												.toString(), form
												.getProjectId()));
						if (isOnProjectPermitted != null
								&& isOnProjectPermitted) {
							permitted = true;
						}
					} else if (isOnFormPermitted) {
						permitted = true;
					}

					if (permitted) {
						FormDTO formDTO = converterUtil.formToFormDTO(form,
								signedTicket);
						if (formDTO != null) {
							formDTOs.add(formDTO);
						}
					} else {
						LOGGER.log(Level.FINE,
								"Form with ID {0} is not returned for user with ticket {1} since the user "
										+ "is not permitted access",
								new String[] { form.getId(),
										request.getSignedTicket().toString() });
					}
				}
				categoryDTO.setForms(formDTOs);
			}
			categoryDTOs.add(categoryDTO);
		}
		projectDTO.setCategories(categoryDTOs);

		// Retrieve uncategorised forms, check their permissions and convert
		// them to DTOs
		List<Form> forms = Form
				.getUncategorisedFormsForProjectId(em, projectId);
		List<FormDTO> formDTOs = new ArrayList<>();
		for (Form form : forms) {
			// Check that the operation FRM_VIEW_FORM is allowed for
			// each form or the operation FRM_VIEW_FORM is allowed for
			// the project of the form.
			Boolean isOnFormPermitted = securityService
					.isPermitted(new IsPermittedRequest(signedTicket,
							SecureOperation.FRM_VIEW_FORM.toString(), form
									.getId()));

			boolean permitted = false;
			if (isOnFormPermitted == null) {
				Boolean isOnProjectPermitted = securityService
						.isPermitted(new IsPermittedRequest(signedTicket,
								SecureOperation.FRM_VIEW_FORM.toString(), form
										.getProjectId()));
				if (isOnProjectPermitted != null && isOnProjectPermitted) {
					permitted = true;
				}
			} else if (isOnFormPermitted) {
				permitted = true;
			}

			if (permitted) {
				FormDTO formDTO = converterUtil.formToFormDTO(form,
						signedTicket);
				if (formDTO != null) {
					formDTOs.add(formDTO);
				}
			} else {
				LOGGER.log(Level.FINE,
						"Form with ID {0} is not returned for user with ticket {1} since the user "
								+ "is not permitted access", new String[] {
								form.getId(),
								request.getSignedTicket().toString() });
			}
		}
		projectDTO.setForms(formDTOs);

		// Update recent project
		if (request.isUpdateRecentProjects()) {
			UpdateRecentProjectRequest recentProjectReq = new UpdateRecentProjectRequest();
			recentProjectReq.setProjectId(projectId);
			recentProjectReq.setSignedTicket(signedTicket);

			updateRecentProject(recentProjectReq);
		}

		AuditProjectDTO auditProjectDTO = converterUtil
				.projectToAuditProjectDTO(project);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.VIEW
				.toString(), GROUP.PROJECT.toString(), null, request
				.getSignedTicket().getUserID(), auditProjectDTO);

		return projectDTO;
	}

	@Override
	@ValidateTicket
	public List<CategoryDTO> getProjectCategories(
			GetProjectCategoriesRequest request) {
		LOGGER.log(Level.FINE,
				"Retrieving list of categories for project with ID {0}",
				request.getProjectId());

		String projectId = request.getProjectId();

		List<Category> categories = Category.getCategoriesForProjectId(em,
				projectId);
		List<CategoryDTO> categoryDTOs = converterUtil
				.categoriesToCategoryDTOList(categories,
						request.getSignedTicket());

		return categoryDTOs;
	}

	@Override
	@ValidateTicket
	public List<WorkingSetDTO> getProjectWorkingSets(
			GetProjectWorkingSetsRequest request) {
		LOGGER.log(
				Level.FINE,
				"Retrieving list of working sets for project with ID {0} by requesting them from rules component",
				request.getProjectId());

		if (workingSetsServiceList.size() == 0) {
			return null;
		}

		String projectId = request.getProjectId();
		GetProjectWorkingSetVersionsRequest workingSetsRequest = new GetProjectWorkingSetVersionsRequest(
				projectId);
		workingSetsRequest.setSignedTicket(request.getSignedTicket());
		List<WorkingSetVersionIdentifierDTO> workingSetVersions = workingSetsServiceList
				.get(0).getProjectWorkingSetVersions(workingSetsRequest);
		return converterUtil
				.workingSetVersionIdentifierDTOsToWorkingSetDTOList(workingSetVersions);
	}

	@Override
	@ValidateTicket
	public List<RuleDTO> getWorkingSetRules(GetWorkingSetRulesRequest request) {
		LOGGER.log(
				Level.FINE,
				"Retrieving list of rules for project with ID {0} by requesting them from rules component",
				request.getProjectId());

		if (rulesServiceList.size() == 0) {
			return null;
		}

		String projectId = request.getProjectId();
		GetProjectRuleVersionsRequest rulesRequest = new GetProjectRuleVersionsRequest(
				projectId);
		rulesRequest.setSignedTicket(request.getSignedTicket());
		List<RuleVersionIdentifierDTO> ruleVersions = rulesServiceList.get(0)
				.getProjectRuleVersions(rulesRequest);
		return converterUtil
				.ruleVersionIdentifierDTOsToRuleDTOList(ruleVersions);
	}

	public void setProjectExplorerService(ProjectService projectExplorerService) {
		this.projectExplorerService = projectExplorerService;
	}

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
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

	public void setEm(EntityManager em) {
		this.em = em;
	}

}
