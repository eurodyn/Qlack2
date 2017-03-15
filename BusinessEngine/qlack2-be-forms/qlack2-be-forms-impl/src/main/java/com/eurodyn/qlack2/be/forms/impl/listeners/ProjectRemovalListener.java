package com.eurodyn.qlack2.be.forms.impl.listeners;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.eurodyn.qlack2.be.explorer.api.util.Constants;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditCategoryDTO;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditFormDTO;
import com.eurodyn.qlack2.be.forms.impl.model.Category;
import com.eurodyn.qlack2.be.forms.impl.model.Form;
import com.eurodyn.qlack2.be.forms.impl.model.FormVersion;
import com.eurodyn.qlack2.be.forms.impl.model.RecentProject;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.forms.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;

public class ProjectRemovalListener implements EventHandler {
	private static final Logger LOGGER = Logger
			.getLogger(ProjectRemovalListener.class.getName());

	private EntityManager em;

	private AuditClientService auditClientService;
	private ConverterUtil converterUtil;
	private SecurityService securityService;
	private GroupService groupService;

	@Override
	public void handleEvent(Event event) {
		String projectId = (String) event
				.getProperty(Constants.EVENT_DATA_PROJECT_ID);
		LOGGER.log(Level.FINE, "Project with ID " + projectId
				+ " has been removed from Projects Explorer. "
				+ "Project resources will be removed form Forms Manager");

		// Delete project from recent project
		RecentProject recentProject = RecentProject
				.getRecentProjectByProjectId(em, projectId);
		if (recentProject != null) {
			em.remove(recentProject);
		}

		// Delete all project resources
		List<Category> categories = Category.getCategoriesForProjectId(em,
				projectId);
		for (Category category : categories) {
			AuditCategoryDTO auditCategoryDTO = converterUtil
					.categoryToAuditCategoryDTO(category);
			em.remove(category);
			auditClientService.audit(LEVEL.QBE_FORMS.toString(),
					EVENT.DELETE.toString(), GROUP.CATEGORY.toString(), null,
					"SYSTEM", auditCategoryDTO);
		}

		List<Form> forms = Form.getFormsForProjectId(em, projectId);
		for (Form form : forms) {
			List<FormVersion> formVersions = form.getFormVersions();
			if (formVersions != null) {
				for (FormVersion formVersion : formVersions) {
					// Retrieve lexicon group based on the form version id which
					// is by
					// convention the name of the group
					GroupDTO group = groupService.getGroupByName(formVersion
							.getId());

					if (group != null) {
						// Delete group to delete all translations
						groupService.deleteGroup(group.getId());
					}
				}
			}

			AuditFormDTO auditFormDTO = converterUtil.formToAuditFormDTO(form);

			em.remove(form);

			// Delete resource from aaa
			DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(
					form.getId());
			securityService.deleteSecureResource(resourceRequest);

			auditClientService.audit(LEVEL.QBE_FORMS.toString(),
					EVENT.DELETE.toString(), GROUP.FORM.toString(), null,
					"SYSTEM", auditFormDTO);
		}

	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}

	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

}
