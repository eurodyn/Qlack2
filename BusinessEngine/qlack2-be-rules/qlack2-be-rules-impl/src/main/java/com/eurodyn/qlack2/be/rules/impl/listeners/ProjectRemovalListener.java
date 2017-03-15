package com.eurodyn.qlack2.be.rules.impl.listeners;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.eurodyn.qlack2.be.explorer.api.util.Constants;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelField;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.RecentProject;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersionKnowledgeBase;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.rules.api.RulesRuntimeService;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;

public class ProjectRemovalListener implements EventHandler {
	private static final Logger LOGGER = Logger.getLogger(ProjectRemovalListener.class.getName());

	private EntityManager em;

	private AuditClientService audit;

	private SecurityService securityService;

	private RulesRuntimeService rulesRuntimeService;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setRulesRuntimeService(RulesRuntimeService rulesRuntimeService) {
		this.rulesRuntimeService = rulesRuntimeService;
	}

	@Override
	public void handleEvent(Event event) {
		String projectId = (String) event.getProperty(Constants.EVENT_DATA_PROJECT_ID);
		LOGGER.log(Level.FINE, "Project {0} has been removed from Projects Explorer.", projectId);

		LOGGER.log(Level.FINE, "Remove project resources form Rules Manager");

		removeRecentProject(projectId);

		removeWorkingSets(projectId);

		removeRules(projectId);

		removeLibraries(projectId);

		removeDataModels(projectId);

		removeCategories(projectId);
	}

	private void removeRecentProject(String projectId) {
		RecentProject recentProject = RecentProject.getRecentProjectByProjectId(em, projectId);
		if (recentProject != null) {
			em.remove(recentProject);
		}
	}

	private void removeWorkingSets(String projectId) {
		List<WorkingSet> workingSets = WorkingSet.findByProjectId(em, projectId);
		for (WorkingSet workingSet : workingSets) {
			String workingSetId = workingSet.getId();

			for (WorkingSetVersion version : workingSet.getVersions()) {
				WorkingSetVersionKnowledgeBase knowledgeBase = version.getKnowledgeBase();
				if (knowledgeBase != null) {
					String kbaseId = knowledgeBase.getKnowledgeBaseId();
					rulesRuntimeService.destroyKnowledgeBase(kbaseId);
				}
			}

			em.remove(workingSet);

			DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(workingSetId);
			securityService.deleteSecureResource(resourceRequest);

			audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.WORKING_SET.toString(),
						null, "SYSTEM", workingSetId);

		}
	}

	private void removeRules(String projectId) {
		List<Rule> rules = Rule.findByProjectId(em, projectId);
		for (Rule rule : rules) {
			String ruleId = rule.getId();

			em.remove(rule);

			DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(ruleId);
			securityService.deleteSecureResource(resourceRequest);

			audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RULE.toString(),
						null, "SYSTEM", ruleId);
		}
	}

	private void removeLibraries(String projectId) {
		List<Library> libraries = Library.findByProjectId(em, projectId);
		for (Library library : libraries) {
			String libraryId = library.getId();

			em.remove(library);

			DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(libraryId);
			securityService.deleteSecureResource(resourceRequest);

			audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RULE.toString(),
						null, "SYSTEM", libraryId);
		}
	}

	private void removeDataModels(String projectId) {
		List<DataModel> models = DataModel.findByProjectId(em, projectId);

		// break model inter-dependencies
		for (DataModel model : models) {
			for (DataModelVersion version : model.getVersions()) {
				version.setParentModel(null);
				for (DataModelField field : version.getFields()) {
					field.setFieldModelType(null);
				}
			}
		}

		em.flush();

		// remove
		for (DataModel model : models) {
			String modelId = model.getId();

			em.remove(model);

			DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(modelId);
			securityService.deleteSecureResource(resourceRequest);

			audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RULE.toString(),
						null, "SYSTEM", modelId);
		}
	}

	private void removeCategories(String projectId) {
		List<Category> categories = Category.findByProjectId(em, projectId);
		for (Category category : categories) {
			String categoryId = category.getId();

			em.remove(category);

			audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.RULE.toString(),
						null, "SYSTEM", categoryId);
		}
	}

}
