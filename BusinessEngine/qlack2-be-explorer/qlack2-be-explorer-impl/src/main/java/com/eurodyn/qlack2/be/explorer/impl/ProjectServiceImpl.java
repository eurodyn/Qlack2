package com.eurodyn.qlack2.be.explorer.impl;

import com.eurodyn.qlack2.be.explorer.api.ProjectService;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria;
import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.request.project.*;
import com.eurodyn.qlack2.be.explorer.api.util.Constants;
import com.eurodyn.qlack2.be.explorer.impl.model.Project;
import com.eurodyn.qlack2.be.explorer.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.explorer.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.explorer.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.explorer.impl.util.ConverterBean;
import com.eurodyn.qlack2.be.explorer.impl.util.SecureOperation;
import com.eurodyn.qlack2.common.util.search.PagingParams;
import com.eurodyn.qlack2.fuse.auditing.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.*;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectServiceImpl implements ProjectService {
	private static final Logger LOGGER = Logger.getLogger(ProjectServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private IDMService idmService;
	private SecurityService security;
	private ConverterBean converter;
	private EventPublisherService eventPublisher;
	private AuditClientService audit;

	@PersistenceContext(unitName = "explorer")
	private EntityManager em;

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setSecurity(SecurityService security) {
		this.security = security;
	}

	public void setConverter(ConverterBean converter) {
		this.converter = converter;
	}

	public void setEventPublisher(EventPublisherService eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	private void publishEvent(SignedTicket signedTicket, String event, String projectId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_PROJECT_ID, projectId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/be/explorer/"
				+ Constants.RESOURCE_TYPE_PROJECT + "/" + event);
	}

	private boolean canViewProject(SignedTicket ticket, String projectId) {
		Boolean isPermitted = security.isPermitted(new IsPermittedRequest(
				ticket, SecureOperation.EXP_VIEW_PROJECT.toString(), projectId));
		return isPermitted != null && isPermitted;
	}

	private void checkCanViewProject(SignedTicket ticket, String projectId) {
		security.requirePermitted(new RequirePermittedRequest(
				ticket, SecureOperation.EXP_VIEW_PROJECT.toString(), projectId));
	}

	private void checkCanManageProject(SignedTicket ticket) {
		security.requirePermitted(new RequirePermittedRequest(
				ticket, SecureOperation.EXP_MANAGE_PROJECT.toString()));
	}

	private void checkCanManageProject(SignedTicket ticket, String projectId) {
		security.requirePermitted(new RequirePermittedRequest(
				ticket, SecureOperation.EXP_MANAGE_PROJECT.toString(), projectId));
	}

	// --

	@Override
	@ValidateTicket
	public List<ProjectDTO> getProjects(GetProjectsRequest req) {
		LOGGER.log(Level.FINE, "Retrieving list of projects from projects explorer");

		ProjectListCriteria criteria = req.getCriteria();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Project> cq = cb.createQuery(Project.class);
		Root<Project> root = cq.from(Project.class);

		// Filter projects based on criteria
		if (criteria.getActive() != null) {
			Predicate pr = cb.equal(root.get("active"), criteria.getActive());
			cq = addPredicate(cq, cb, pr);
		}
		if (criteria.getRules() != null) {
			Predicate pr = cb.equal(root.get("rules"), criteria.getRules());
			cq = addPredicate(cq, cb, pr);
		}
		if (criteria.getWorkflows() != null) {
			Predicate pr = cb.equal(root.get("workflows"), criteria.getWorkflows());
			cq = addPredicate(cq, cb, pr);
		}
		if (criteria.getForms() != null) {
			Predicate pr = cb.equal(root.get("forms"), criteria.getForms());
			cq = addPredicate(cq, cb, pr);
		}

		// Apply sorting.
		Expression<?> orderExpr = root.get(criteria.getSortColumn());
		Order order = criteria.isAscending() ? cb.asc(orderExpr) : cb.desc(orderExpr);
		cq = cq.orderBy(order);

		TypedQuery<Project> query = em.createQuery(cq);

		// Apply pagination
		PagingParams paging = criteria.getPaging();
		if (paging != null && paging.getCurrentPage() > -1) {
			query.setFirstResult((paging.getCurrentPage() - 1) * paging.getPageSize());
			query.setMaxResults(paging.getPageSize());
		}

		List<Project> projects = query.getResultList();
		List<ProjectDTO> retVal = new ArrayList<>(projects.size());
		for (Project project : projects) {
			boolean canViewProject = canViewProject(req.getSignedTicket(), project.getId());
			if (canViewProject) {
				retVal.add(converter.convert(project, req.getSignedTicket()));
			} else {
				LOGGER.log(Level.FINE,
						"Project with ID {0} is not returned for user with ticket {1} since the user "
								+ "is not permitted access", new String[] {
								project.getId(),
								req.getSignedTicket().toString() });
			}
		}

		audit.audit(LEVEL.QBE_EXPLORER.toString(), EVENT.VIEW.toString(), GROUP.PROJECT.toString(),
				null, req.getSignedTicket().getUserID(), null);

		return retVal;
	}

	private <T> CriteriaQuery<T> addPredicate(CriteriaQuery<T> query, CriteriaBuilder cb, Predicate pr) {
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
	public ProjectDTO getProject(GetProjectRequest req) {
		LOGGER.log(Level.FINE, "Getting project with ID {0}", req.getId());

		checkCanViewProject(req.getSignedTicket(), req.getId());
		Project project = em.find(Project.class, req.getId());
		ProjectDTO projectDto = converter.convert(project, req.getSignedTicket());

		audit.audit(LEVEL.QBE_EXPLORER.toString(), EVENT.VIEW.toString(), GROUP.PROJECT.toString(),
				null, req.getSignedTicket().getUserID(), projectDto);

		return projectDto;
	}

	@Override
	@ValidateTicket
	public ProjectDTO getProjectByName(GetProjectByNameRequest req) {
		LOGGER.log(Level.FINE, "Getting project with name {0}", req.getName());

		Project project = Project.findByName(em, req.getName());
		checkCanViewProject(req.getSignedTicket(), project.getId());

		ProjectDTO projectDTO = converter.convert(project, req.getSignedTicket());

		audit.audit(LEVEL.QBE_EXPLORER.toString(), EVENT.VIEW.toString(), GROUP.PROJECT.toString(),
				null, req.getSignedTicket().getUserID(), projectDTO);

		return projectDTO;
	}

	@Override
	@ValidateTicket
	public String createProject(CreateProjectRequest req) {
		LOGGER.log(Level.FINE, "Creating new project with name {0}", req.getName());

		checkCanManageProject(req.getSignedTicket());

		long now = DateTime.now().getMillis();

		Project project = new Project();
		project.setName(req.getName());
		project.setDescription(req.getDescription());
		project.setActive(req.isActive());
		project.setRules(req.isRules());
		project.setWorkflows(req.isWorkflows());
		project.setForms(req.isForms());
		project.setCreatedOn(now);
		project.setCreatedBy(req.getSignedTicket().getUserID());
		project.setLastModifiedOn(now);
		project.setLastModifiedBy(req.getSignedTicket().getUserID());
		em.persist(project);

		// Create secure resource for the new project
		CreateSecureResourceRequest rreq = new CreateSecureResourceRequest(
				project.getId(), project.getName(), "Project " + project.getName());
		security.createSecureResource(rreq);

		// Assign all permissions for the newly created project to the user who created it
		AllowSecureOperationForUserRequest allowReq = new AllowSecureOperationForUserRequest();
		allowReq.setSignedTicket(req.getSignedTicket());
		allowReq.setResourceObjectId(project.getId());
		allowReq.setUserId(req.getSignedTicket().getUserID());

		allowReq.setOperationName(SecureOperation.EXP_VIEW_PROJECT.toString());
		security.allowSecureOperationForUser(allowReq);

		allowReq.setOperationName(SecureOperation.EXP_MANAGE_PROJECT.toString());
		security.allowSecureOperationForUser(allowReq);

		// And also assign the EXP_MANAGED operation to the user in order to
		// be able to manage their permissions on the new project via the UI
		allowReq.setOperationName(SecureOperation.EXP_MANAGED.toString());
		security.allowSecureOperationForUser(allowReq);

		publishEvent(req.getSignedTicket(), Constants.EVENT_CREATE, project.getId());

		ProjectDTO projectDTO = converter.convert(project, req.getSignedTicket());
		audit.audit(LEVEL.QBE_EXPLORER.toString(), EVENT.CREATE.toString(), GROUP.PROJECT.toString(),
				null, req.getSignedTicket().getUserID(), projectDTO);

		return project.getId();
	}

	@Override
	@ValidateTicket
	public void updateProject(UpdateProjectRequest req) {
		LOGGER.log(Level.FINE, "Updating project with ID {0}", req.getId());

		checkCanManageProject(req.getSignedTicket(), req.getId());

		long now = DateTime.now().getMillis();

		Project project = em.find(Project.class, req.getId());
		project.setName(req.getName());
		project.setDescription(req.getDescription());
		project.setActive(req.isActive());
		project.setRules(req.isRules());
		project.setWorkflows(req.isWorkflows());
		project.setForms(req.isForms());
		project.setLastModifiedOn(now);
		project.setLastModifiedBy(req.getSignedTicket().getUserID());

		// Update the project's secure resource in case the project name was updated
		UpdateSecureResourceRequest rreq = new UpdateSecureResourceRequest(
				project.getId(), project.getName(), "Project " + project.getName());
		security.updateSecureResource(rreq);

		publishEvent(req.getSignedTicket(), Constants.EVENT_UPDATE, project.getId());

		ProjectDTO projectDTO = converter.convert(project, req.getSignedTicket());
		audit.audit(LEVEL.QBE_EXPLORER.toString(), EVENT.UPDATE.toString(), GROUP.PROJECT.toString(),
				null, req.getSignedTicket().getUserID(), projectDTO);
	}

	@Override
	@ValidateTicket
	public void deleteProject(DeleteProjectRequest req) {
		LOGGER.log(Level.FINE, "Deleting project with ID {0}", req.getId());

		checkCanManageProject(req.getSignedTicket(), req.getId());

		Project project = em.find(Project.class, req.getId());
		ProjectDTO projectDTO = converter.convert(project, req.getSignedTicket());
		em.remove(project);

		DeleteSecureResourceRequest rreq = new DeleteSecureResourceRequest(req.getId());
		security.deleteSecureResource(rreq);

		publishEvent(req.getSignedTicket(), Constants.EVENT_DELETE, req.getId());

		audit.audit(LEVEL.QBE_EXPLORER.toString(), EVENT.DELETE.toString(), GROUP.PROJECT.toString(),
				null, req.getSignedTicket().getUserID(), projectDTO);
	}

}
