package com.eurodyn.qlack2.be.forms.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.forms.api.ProjectsService;
import com.eurodyn.qlack2.be.forms.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.forms.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.forms.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.forms.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectCategoriesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectResourcesRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetProjectWorkingSetsRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.forms.api.request.project.GetWorkingSetRulesRequest;
import com.eurodyn.qlack2.be.forms.web.dto.tree.CompositeNode;
import com.eurodyn.qlack2.be.forms.web.util.ConverterUtil;
import com.eurodyn.qlack2.be.forms.web.util.Utils;


@Path("/projects")
public class ProjectsRest {
	private static final Logger LOGGER = Logger.getLogger(ProjectsRest.class.getName());

	@Context
	private HttpHeaders headers;

	private ProjectsService projectsService;

	/**
	 * Retrieves list of projects for Forms Component UI
	 *
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectDTO> getProjects() {
		EmptySignedRequest req = new EmptySignedRequest();

		Utils.sign(req, headers);
		return projectsService.getProjects(req);
	}

	@GET
	@Path("{projectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProjectDTO getProject(@PathParam("projectId") String projectId) {
		GetProjectRequest req = new GetProjectRequest();
		req.setProjectId(projectId);

		Utils.sign(req, headers);
		return projectsService.getProject(req);
	}


	/**
	 * Retrieves list of recently accessed projects for Forms Component UI
	 *
	 * @return
	 */
	@GET
	@Path("recent")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectDTO> getRecentProjects(@QueryParam("sort") String sort, @QueryParam("order") String order,
			@QueryParam("start") Integer start, @QueryParam("size") Integer size) {
		GetRecentProjectsRequest req = new GetRecentProjectsRequest();
		req.setSort(sort);
		req.setOrder(order);
		req.setStart(start);
		req.setSize(size);

		Utils.sign(req, headers);
		return projectsService.getRecentProjects(req);
	}

	/**
	 * Retrieves the resources (forms and categories) of a project in a tree
	 * like structure
	 *
	 * @param projectId
	 * @return
	 */
	@GET
	@Path("{projectId}/resources")
	@Produces(MediaType.APPLICATION_JSON)
	public CompositeNode getFormResourcesAsTree(
			@PathParam("projectId") String projectId) {
		GetProjectResourcesRequest req = new GetProjectResourcesRequest();
		req.setProjectId(projectId);
		req.setUpdateRecentProjects(true);

		Utils.sign(req, headers);
		ProjectDetailsDTO projectDTO = projectsService.getProjectResources(req);

		CompositeNode compositeNode = ConverterUtil.convert(projectDTO);
		return compositeNode;
	}

	/**
	 * Retrieves the resources (forms and categories) of all projects in a tree
	 * like structure
	 *
	 * @return
	 */
	@GET
	@Path("resources")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CompositeNode> getFormResourcesAsTree() {
		EmptySignedRequest projectReq = new EmptySignedRequest();
		Utils.sign(projectReq, headers);
		List<ProjectDTO> projects = projectsService.getProjects(projectReq);

		List<CompositeNode> nodes = new ArrayList<>(projects.size());
		for (ProjectDTO project : projects) {
			GetProjectResourcesRequest req = new GetProjectResourcesRequest();
			req.setProjectId(project.getId());
			req.setUpdateRecentProjects(false);

			Utils.sign(req, headers);
			ProjectDetailsDTO projectDetails = projectsService.getProjectResources(req);
			CompositeNode compositeNode = ConverterUtil.convert(projectDetails);
			nodes.add(compositeNode);
		}
		return nodes;
	}

	/**
	 * Retrieves the categories of a project.
	 *
	 * @param projectId
	 * @return
	 */
	@GET
	@Path("{projectId}/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CategoryDTO> getProjectCategories(@PathParam("projectId") String projectId) {
		GetProjectCategoriesRequest req = new GetProjectCategoriesRequest();
		req.setProjectId(projectId);

		Utils.sign(req, headers);
		return projectsService.getProjectCategories(req);
	}

	/**
	 * Retrieves the working sets of a project.
	 *
	 * @param projectId
	 * @return
	 */
	@GET
	@Path("{projectId}/working-sets")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WorkingSetDTO> getProjectWorkingSets(@PathParam("projectId") String projectId) {
		GetProjectWorkingSetsRequest req = new GetProjectWorkingSetsRequest();
		req.setProjectId(projectId);

		Utils.sign(req, headers);
		return projectsService.getProjectWorkingSets(req);
	}

	/**
	 * Retrieves the rules of a working set.
	 *
	 * @param projectId
	 * @param workingSetId
	 * @return
	 */
	@GET
	@Path("{projectId}/rules")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RuleDTO> getWorkingSetRules(@PathParam("projectId") String projectId,
			@QueryParam("workingSetId") String workingSetId) {
		GetWorkingSetRulesRequest req = new GetWorkingSetRulesRequest();
		req.setProjectId(projectId);
		req.setWorkingSetId(workingSetId);

		Utils.sign(req, headers);
		return projectsService.getWorkingSetRules(req);
	}

	public void setProjectsService(ProjectsService projectsService) {
		this.projectsService = projectsService;
	}
}
