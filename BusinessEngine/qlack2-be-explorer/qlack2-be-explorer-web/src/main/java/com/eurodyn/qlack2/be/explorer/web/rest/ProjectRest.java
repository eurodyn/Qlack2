package com.eurodyn.qlack2.be.explorer.web.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.eurodyn.qlack2.be.explorer.api.ProjectService;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.ProjectListCriteriaBuilder;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.SortColumn;
import com.eurodyn.qlack2.be.explorer.api.criteria.ProjectListCriteria.SortType;
import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.request.project.CreateProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.DeleteProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectsRequest;
import com.eurodyn.qlack2.be.explorer.api.request.project.UpdateProjectRequest;
import com.eurodyn.qlack2.be.explorer.web.dto.ProjectRDTO;
import com.eurodyn.qlack2.be.explorer.web.util.Utils;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;

@Path("/projects")
public class ProjectRest {
	@Context
	private HttpHeaders headers;
	private ProjectService projectService;

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectDTO> getProjects() {
		ProjectListCriteria criteria = ProjectListCriteriaBuilder
				.createCriteria()
				.sortByColumn(SortColumn.NAME, SortType.ASCENDING)
				.build();
		GetProjectsRequest sreq = new GetProjectsRequest(criteria);
		Utils.sign(sreq, headers);
		return projectService.getProjects(sreq);
	}

	@GET
	@Path("{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProjectDTO getProject(@PathParam("projectId") String projectId) {
		GetProjectRequest sreq = new GetProjectRequest(projectId);
		Utils.sign(sreq, headers);
		return projectService.getProject(sreq);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument
	public String createProject(ProjectRDTO project) {
		CreateProjectRequest sreq = new CreateProjectRequest();
		sreq.setName(project.getName());
		sreq.setDescription(project.getDescription());
		sreq.setActive(project.isActive());
		sreq.setRules(project.isRules());
		sreq.setWorkflows(project.isWorkflows());
		sreq.setForms(project.isForms());
		Utils.sign(sreq, headers);
		return projectService.createProject(sreq);
	}

	@PUT
	@Path("{projectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateProject(@PathParam("projectId") String projectId,
			ProjectRDTO project) {
		UpdateProjectRequest sreq = new UpdateProjectRequest();
		sreq.setId(projectId);
		sreq.setName(project.getName());
		sreq.setDescription(project.getDescription());
		sreq.setActive(project.isActive());
		sreq.setRules(project.isRules());
		sreq.setWorkflows(project.isWorkflows());
		sreq.setForms(project.isForms());
		Utils.sign(sreq, headers);
		projectService.updateProject(sreq);
	}

	@DELETE
	@Path("{projectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteProject(@PathParam("projectId") String projectId) {
		DeleteProjectRequest sreq = new DeleteProjectRequest(projectId);
		Utils.sign(sreq, headers);
		projectService.deleteProject(sreq);
	}

}
