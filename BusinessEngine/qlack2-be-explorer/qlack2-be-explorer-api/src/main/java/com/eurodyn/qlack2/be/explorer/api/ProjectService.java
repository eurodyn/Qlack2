package com.eurodyn.qlack2.be.explorer.api;

import java.util.List;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.request.project.CreateProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.DeleteProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectByNameRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectsRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.UpdateProjectRequest;

public interface ProjectService {
	List<ProjectDTO> getProjects(GetProjectsRequest req);
	ProjectDTO getProject(GetProjectRequest req);
	ProjectDTO getProjectByName(GetProjectByNameRequest req);
	String createProject(CreateProjectRequest req);
	void updateProject(UpdateProjectRequest req);
	void deleteProject(DeleteProjectRequest req);
}
