package com.eurodyn.qlack2.be.workflow.api;

import java.util.List;

import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.UpdateRecentProjectRequest;
import com.eurodyn.qlack2.be.workflow.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
//import com.eurodyn.qlack2.be.workflow.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.ProjectResourcesDTO;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoriesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectResourcesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectWorkingSetsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetWorkingSetRulesRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface ProjectService {
	
	List<ProjectDTO> getProjects(EmptySignedRequest request) throws QInvalidTicketException;
	
	List<ProjectDTO> getRecentProjects(GetRecentProjectsRequest request) throws QInvalidTicketException;

	ProjectDTO getProject(GetProjectRequest request) throws QInvalidTicketException;

	void updateRecentProject(UpdateRecentProjectRequest request) throws QInvalidTicketException;
	
	ProjectResourcesDTO getProjectResources(GetProjectResourcesRequest request) throws QInvalidTicketException;
	
	List<CategoryDTO> getCategoriesByProjectId(GetCategoriesRequest request) throws QInvalidTicketException;
	
	List<WorkingSetDTO> getProjectWorkingSets(GetProjectWorkingSetsRequest request) throws QInvalidTicketException;

	List<RuleDTO> getWorkingSetRules(GetWorkingSetRulesRequest request) throws QInvalidTicketException;
}
