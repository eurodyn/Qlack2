package com.eurodyn.qlack2.be.workflow.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

//import com.eurodyn.qlack2.be.workflow.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.workflow.api.ProjectService;
import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.ProjectResourcesDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.workflow.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoriesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectResourcesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetProjectWorkingSetsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.GetWorkingSetRulesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.project.UpdateRecentProjectRequest;
import com.eurodyn.qlack2.be.workflow.web.dto.tree.CompositeNodeDTO;
import com.eurodyn.qlack2.be.workflow.web.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.web.util.Utils;

@Path("/projects")
public class ProjectRest {

	private static final Logger LOGGER = Logger.getLogger(ProjectRest.class.getName());

	@Context
	private HttpHeaders headers;

	private ProjectService projectService;

	public void setProjectService(ProjectService projectService) {
		this.projectService = projectService;
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProjectDTO> getProjects() {
            EmptySignedRequest req = new EmptySignedRequest();

            Utils.sign(req, headers);
            return projectService.getProjects(req);
    }
	
	@GET
    @Path("{projectId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ProjectDTO getProject(@PathParam("projectId") String projectId) {
            GetProjectRequest req = new GetProjectRequest();
            req.setProjectId(projectId);

            Utils.sign(req, headers);
            return projectService.getProject(req);
    }

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
            return projectService.getRecentProjects(req);
    }

    @PUT
    @Path("recent/{projectId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void updateForm(@PathParam("projectId") String projectId) {
            UpdateRecentProjectRequest req = new UpdateRecentProjectRequest();
            req.setProjectId(projectId);

            Utils.sign(req, headers);
            projectService.updateRecentProject(req);
    }

	@GET
	@Path("{projectId}/resources")
	@Produces(MediaType.APPLICATION_JSON)
	public CompositeNodeDTO getProjectTreeResources(@PathParam("projectId") String projectId){
		GetProjectResourcesRequest req = new GetProjectResourcesRequest();
		req.setProjectId(projectId);
		req.setUpdateRecentProjects(true);

		Utils.sign(req, headers);
		ProjectResourcesDTO projectDTO = projectService.getProjectResources(req);

		CompositeNodeDTO compositeNode = ConverterUtil.convertResourcesToTree(projectDTO);
		return compositeNode;
	}
	
	@GET
	@Path("resources")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CompositeNodeDTO> getAllTreeResources() {
		EmptySignedRequest projectReq = new EmptySignedRequest();
		Utils.sign(projectReq, headers);
		List<ProjectDTO> projects = projectService.getProjects(projectReq);
		
		List<CompositeNodeDTO> nodes = new ArrayList<>(projects.size());
		for (ProjectDTO project : projects) {
			GetProjectResourcesRequest req = new GetProjectResourcesRequest();
			req.setProjectId(project.getId());	
			Utils.sign(req, headers);
			ProjectResourcesDTO projectDetails = projectService.getProjectResources(req);
			CompositeNodeDTO compositeNode = ConverterUtil.convertResourcesToTree(projectDetails);
			nodes.add(compositeNode);
		}
		return nodes;
	}

	@GET
	@Path("{projectId}/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CategoryDTO> getProjectCategories(@PathParam("projectId") String projectId) {
		GetCategoriesRequest req = new GetCategoriesRequest();
		req.setProjectId(projectId);

		Utils.sign(req, headers);
		return projectService.getCategoriesByProjectId(req);
	}

	@GET
	@Path("{projectId}/working-sets")
	@Produces(MediaType.APPLICATION_JSON)
	public List<WorkingSetDTO> getProjectWorkingSets(@PathParam("projectId") String projectId) {
		GetProjectWorkingSetsRequest req = new GetProjectWorkingSetsRequest();
		req.setProjectId(projectId);

		Utils.sign(req, headers);
		return projectService.getProjectWorkingSets(req);
	}

	@GET
	@Path("{projectId}/rules")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RuleDTO> getWorkingSetRules(@PathParam("projectId") String projectId, @QueryParam("workingSetId") String workingSetId) {
		GetWorkingSetRulesRequest req = new GetWorkingSetRulesRequest();
		req.setProjectId(projectId);
		req.setWorkingSetId(workingSetId);

		Utils.sign(req, headers);
		return projectService.getWorkingSetRules(req);
	}
}
