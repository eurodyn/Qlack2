package com.eurodyn.qlack2.be.forms.api;

import java.util.List;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.forms.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.forms.api.dto.FormDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.forms.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.forms.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectCategoriesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectFormsRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectResourcesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectWorkingSetsRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetWorkingSetRulesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.UpdateRecentProjectRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface ProjectsService {

	/**
	 * Retrieves the list of the available projects
	 *
	 * @param request
	 * @return
	 */
	List<ProjectDTO> getProjects(EmptySignedRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves the list of the recently accessed projects
	 *
	 * @param request
	 * @return
	 */
	List<ProjectDTO> getRecentProjects(GetRecentProjectsRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves the project
	 *
	 * @param request
	 * @return
	 */
	ProjectDTO getProject(GetProjectRequest request)
			throws QInvalidTicketException;

	/**
	 * Updates the recently accessed project.
	 *
	 * @param request
	 */
	void updateRecentProject(UpdateRecentProjectRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves the project resources.
	 *
	 * @param request
	 * @return
	 */
	ProjectDetailsDTO getProjectResources(GetProjectResourcesRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves the categories of a project.
	 *
	 * @param request
	 * @return
	 */
	List<CategoryDTO> getProjectCategories(GetProjectCategoriesRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves the working sets of a project.
	 *
	 * @param request
	 * @return
	 */
	List<WorkingSetDTO> getProjectWorkingSets(
			GetProjectWorkingSetsRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves the rules of a working set.
	 *
	 * @param request
	 * @return
	 */
	List<RuleDTO> getWorkingSetRules(GetWorkingSetRulesRequest request)
			throws QInvalidTicketException;

}
