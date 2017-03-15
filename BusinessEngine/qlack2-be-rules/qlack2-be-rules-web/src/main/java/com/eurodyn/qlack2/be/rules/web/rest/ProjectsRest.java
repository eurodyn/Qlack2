package com.eurodyn.qlack2.be.rules.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.rules.api.CategoriesService;
import com.eurodyn.qlack2.be.rules.api.DataModelsService;
import com.eurodyn.qlack2.be.rules.api.LibraryService;
import com.eurodyn.qlack2.be.rules.api.ProjectsService;
import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelDTO;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.ProjectDetailsDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectCategoriesRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectDataModelsRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectLibrariesRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectRulesRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectWithResourcesRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetRecentProjectsRequest;
import com.eurodyn.qlack2.be.rules.web.dto.tree.CompositeNode;
import com.eurodyn.qlack2.be.rules.web.util.TreeConverter;
import com.eurodyn.qlack2.be.rules.web.util.Utils;

@Path("/projects")
public class ProjectsRest {

	private ProjectsService projectsService;

	private CategoriesService categoriesService;

	private RulesService rulesService;

	private DataModelsService modelsService;

	private LibraryService libraryService;

	private TreeConverter treeConverter = TreeConverter.INSTANCE;

	@Context
	private HttpHeaders headers;

	public void setProjectsService(ProjectsService projectsService) {
		this.projectsService = projectsService;
	}

	public void setCategoriesService(CategoriesService categoriesService) {
		this.categoriesService = categoriesService;
	}

	public void setRulesService(RulesService rulesService) {
		this.rulesService = rulesService;
	}

	public void setDataModelsService(DataModelsService modelsService) {
		this.modelsService = modelsService;
	}

	public void setLibraryService(LibraryService libraryService) {
		this.libraryService = libraryService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectDTO> getProjects() {
		EmptyRequest request = new EmptyRequest();
		Utils.sign(request, headers);

		return projectsService.getProjects(request);
	}

	@GET
	@Path("recent")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProjectDTO> getRecentProjects(@QueryParam("sort") String sort, // XXX use @BeanParam ?
											  @QueryParam("order") String order,
											  @QueryParam("start") Integer start,
											  @QueryParam("size") Integer size) {

		GetRecentProjectsRequest request = new GetRecentProjectsRequest();
		request.setSort(sort);
		request.setOrder(order);
		request.setStart(start);
		request.setSize(size);

		return projectsService.getRecentProjects(Utils.sign(request, headers));
	}

	@GET
	@Path("{projectId}/resources")
	@Produces(MediaType.APPLICATION_JSON)
	public CompositeNode getProjectTree(@PathParam("projectId") String projectId) {

		GetProjectWithResourcesRequest request = new GetProjectWithResourcesRequest(projectId);
		Utils.sign(request, headers);

		ProjectDetailsDTO projectDto = projectsService.getProjectWithResources(request);

		CompositeNode root = treeConverter.convert(projectDto);

		return root;
	}

	@GET
	@Path("resources")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CompositeNode> getProjectTree() {
		EmptyRequest projectReq = new EmptyRequest();
		Utils.sign(projectReq, headers);
		List<ProjectDTO> projects = projectsService.getProjects(projectReq);

		List<CompositeNode> nodes = new ArrayList<>(projects.size());
		for (ProjectDTO project : projects) {
			GetProjectWithResourcesRequest request = new GetProjectWithResourcesRequest(project.getId(), false);
			Utils.sign(request, headers);
			ProjectDetailsDTO projectDetails = projectsService.getProjectWithResources(request);
			CompositeNode compositeNode = treeConverter.convert(projectDetails);
			nodes.add(compositeNode);
		}
		return nodes;
	}

	@GET
	@Path("{projectId}/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public List<CategoryDTO> getProjectCategories(@PathParam("projectId") String projectId) {

		GetProjectCategoriesRequest request = new GetProjectCategoriesRequest(projectId);
		Utils.sign(request, headers);

		return categoriesService.getCategories(request);
	}

	@GET
	@Path("{projectId}/rules")
	@Produces(MediaType.APPLICATION_JSON)
	public List<RuleDTO> getProjectRules(@PathParam("projectId") String projectId,
										 @QueryParam("filterEmpty") boolean filterEmpty)
	{
		GetProjectRulesRequest request = new GetProjectRulesRequest(projectId, filterEmpty);
		Utils.sign(request, headers);

		return rulesService.getRules(request);
	}

	@GET
	@Path("{projectId}/data-models")
	@Produces(MediaType.APPLICATION_JSON)
	public List<DataModelDTO> getProjectDataModels(@PathParam("projectId") String projectId,
												   @QueryParam("filterEmpty") boolean filterEmpty)
	{
		GetProjectDataModelsRequest request = new GetProjectDataModelsRequest(projectId, filterEmpty);
		Utils.sign(request, headers);

		return modelsService.getDataModels(request);
	}

	@GET
	@Path("{projectId}/libraries")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LibraryDTO> getProjectLibraries(@PathParam("projectId") String projectId,
												@QueryParam("filterEmpty") boolean filterEmpty)
	{
		GetProjectLibrariesRequest request = new GetProjectLibrariesRequest(projectId, filterEmpty);
		Utils.sign(request, headers);

		return libraryService.getLibraries(request);
	}

}
