package com.eurodyn.qlack2.be.rules.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
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
import com.eurodyn.qlack2.be.rules.api.ProjectsService;
import com.eurodyn.qlack2.be.rules.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelDTO;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectWithResourcesRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditProjectDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.RecentProject;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.RuleConstants;
import com.eurodyn.qlack2.be.rules.impl.util.SecurityUtils;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class ProjectsServiceImpl implements ProjectsService {
	private static final Logger LOGGER = Logger.getLogger(ProjectsServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private IDMService idmService;

	private AuditClientService audit;

	private ProjectService projectExplorerService;

	private EntityManager em;

	private ConverterUtil mapper;

	private AuditConverterUtil auditMapper;

	private SecurityUtils securityUtils;

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	public void setProjectExplorerService(ProjectService projectExplorerService) {
		this.projectExplorerService = projectExplorerService;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setMapper(ConverterUtil mapper) {
		this.mapper = mapper;
	}

	public void setAuditMapper(AuditConverterUtil auditMapper) {
		this.auditMapper = auditMapper;
	}

	public void setSecurityUtils(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	@ValidateTicket
	@Override
	public List<ProjectDTO> getProjects(EmptyRequest request) {
		LOGGER.log(Level.FINE, "Get projects with rules.");

		ProjectListCriteria criteria = ProjectListCriteriaBuilder.createCriteria()
				.withActive(true)
				.withRules(true)
				.sortByColumn(SortColumn.NAME, SortType.ASCENDING)
				.build();

		GetProjectsRequest explorerRequest = new GetProjectsRequest(criteria);
		explorerRequest.setSignedTicket(request.getSignedTicket());

		return projectExplorerService.getProjects(explorerRequest);
	}

	@ValidateTicket
	@Override
	public List<ProjectDTO> getRecentProjects(GetRecentProjectsRequest request) {
		SignedTicket ticket = request.getSignedTicket();
		String userId = ticket.getUserID();

		String sort = request.getSort();
		String order = request.getOrder();
		Integer start = request.getStart();
		Integer size = request.getSize();

		LOGGER.log(Level.FINE, "Get recent projects.");

		CriteriaBuilder cBuilder = em.getCriteriaBuilder();
		CriteriaQuery<RecentProject> cQuery = cBuilder.createQuery(RecentProject.class);
		Root<RecentProject> cRoot = cQuery.from(RecentProject.class);

		// Filter projects based on criteria
		if (userId != null) {
			Path<String> userField = cRoot.get(RuleConstants.FIELD_LAST_ACCESSED_BY);
			Predicate cPredicate = cBuilder.equal(userField, userId);
			cQuery = addPredicate(cQuery, cBuilder, cPredicate);
		}

		// Apply sorting
		if (sort != null) {
			Path<Object> sortField = cRoot.get(sort);
			Order cOrder = RuleConstants.SORT_ASCENDING.equals(order) ? cBuilder.asc(sortField) : cBuilder.desc(sortField);
			cQuery = cQuery.orderBy(cOrder);
		}

		TypedQuery<RecentProject> query = em.createQuery(cQuery);

		// Apply pagination
		if (start != null && size != null) {
			query.setFirstResult(start * size);
			query.setMaxResults(size);
		}

		List<RecentProject> recentProjects = query.getResultList();

		List<ProjectDTO> projects = new ArrayList<>(recentProjects.size());
		for (RecentProject recentProject : recentProjects) {
			GetProjectRequest explorerRequest = new GetProjectRequest(recentProject.getProjectId());
			explorerRequest.setSignedTicket(ticket);

			ProjectDTO project = projectExplorerService.getProject(explorerRequest);
			projects.add(project);
		}

		return projects;
	}

	private void updateRecentProject(SignedTicket ticket, String projectId) {
		String userId = ticket.getUserID();

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		RecentProject recentProject = RecentProject.getRecentProjectByProjectIdAndUserId(em, projectId, userId);
		if (recentProject != null) {
			recentProject.setLastAccessedOn(millis);
		} else {
			// The user has not recently accessed this project -> create a new entry
			recentProject = new RecentProject();
			recentProject.setProjectId(projectId);
			recentProject.setLastAccessedBy(userId);
			recentProject.setLastAccessedOn(millis);

			em.persist(recentProject);

			// If there exist more than 4 recently used projects for the specified user,
			// then remove the oldest project, to keep the list of recently accessed projects small.
			CriteriaBuilder cBuilder = em.getCriteriaBuilder();
			CriteriaQuery<RecentProject> cQuery = cBuilder.createQuery(RecentProject.class);
			Root<RecentProject> cRoot = cQuery.from(RecentProject.class);

			// Filter projects based on criteria
			if (userId != null) {
				Path<String> userField = cRoot.get(RuleConstants.FIELD_LAST_ACCESSED_BY);
				Predicate cPredicate = cBuilder.equal(userField, userId);
				cQuery = addPredicate(cQuery, cBuilder, cPredicate);
			}

			// Apply sorting
			cQuery = cQuery.orderBy(cBuilder.desc(cRoot.get(RuleConstants.FIELD_LAST_ACCESSED_ON)));

			TypedQuery<RecentProject> query = em.createQuery(cQuery);

			List<RecentProject> recentProjects = query.getResultList();

			if (recentProjects.size() > RuleConstants.RECENT_PROJECTS_SIZE) {
				int index = recentProjects.size() - 1;
				while (index >= RuleConstants.RECENT_PROJECTS_SIZE) {
					RecentProject rp = recentProjects.get(index);
					em.remove(rp);
					index--;
				}
			}
		}
	}

	private <T> CriteriaQuery<T> addPredicate(CriteriaQuery<T> query, CriteriaBuilder builder, Predicate predicate) {
		if (query.getRestriction() != null) {
			query = query.where(builder.and(query.getRestriction(), predicate));
		} else {
			query = query.where(predicate);
		}
		return query;
	}

	@ValidateTicket
	@Override
	public ProjectDetailsDTO getProjectWithResources(GetProjectWithResourcesRequest request) {
		SignedTicket ticket = request.getSignedTicket();
		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Get project {0} with resources.", projectId);

		GetProjectRequest projectRequest = new GetProjectRequest(projectId);
		projectRequest.setSignedTicket(ticket);

		ProjectDTO project = projectExplorerService.getProject(projectRequest);

		// fetch, security filter and map
		ProjectDetailsDTO projectDto = new ProjectDetailsDTO();
		projectDto.setId(projectId);
		projectDto.setName(project.getName());

		List<Category> categories = Category.findByProjectId(em, projectId);
		List<CategoryDTO> categoryDtos = mapper.mapCategoryList(categories, ticket);
		projectDto.setCategories(categoryDtos);

		List<WorkingSet> sets = WorkingSet.findByProjectId(em, projectId);
		List<WorkingSetDTO> setDtos = new ArrayList<>();
		for (WorkingSet set : sets) {
			if (securityUtils.canViewWorkingSet(ticket, set)) {
				setDtos.add(mapper.mapWorkingSet(set, ticket));
			}
		}
		projectDto.setWorkingSets(setDtos);

		List<Rule> rules = Rule.findByProjectId(em, projectId);
		List<RuleDTO> ruleDtos = new ArrayList<>();
		for (Rule rule : rules) {
			if (securityUtils.canViewRule(ticket, rule)) {
				ruleDtos.add(mapper.mapRule(rule, ticket));
			}
		}
		projectDto.setRules(ruleDtos);

		List<DataModel> models = DataModel.findByProjectId(em, projectId);
		List<DataModelDTO> modelDtos = new ArrayList<>();
		for (DataModel model : models) {
			if (securityUtils.canViewDataModel(ticket, model)) {
				modelDtos.add(mapper.mapDataModel(model, ticket));
			}
		}
		projectDto.setDataModels(modelDtos);

		List<Library> libraries = Library.findByProjectId(em, projectId);
		List<LibraryDTO> libraryDtos = new ArrayList<>();
		for (Library library : libraries) {
			if (securityUtils.canViewLibrary(ticket, library)) {
				libraryDtos.add(mapper.mapLibrary(library, ticket));
			}
		}
		projectDto.setLibraries(libraryDtos);

		// record project access
		boolean updateRecentProjects = request.isUpdateRecentProjects();
		if (updateRecentProjects) {
			updateRecentProject(ticket, projectId);
		}

		AuditProjectDTO auditDto = auditMapper.mapProject(project);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.PROJECT.toString(),
					null, ticket.getUserID(), auditDto);

		return projectDto;
	}

}
