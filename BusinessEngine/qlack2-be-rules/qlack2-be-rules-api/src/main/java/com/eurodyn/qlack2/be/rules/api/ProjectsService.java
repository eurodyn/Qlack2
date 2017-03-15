package com.eurodyn.qlack2.be.rules.api;

import java.util.List;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.rules.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectWithResourcesRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface ProjectsService {

	List<ProjectDTO> getProjects(EmptyRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	List<ProjectDTO> getRecentProjects(GetRecentProjectsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	ProjectDetailsDTO getProjectWithResources(GetProjectWithResourcesRequest request)
			throws QInvalidTicketException, QAuthorisationException;

}
