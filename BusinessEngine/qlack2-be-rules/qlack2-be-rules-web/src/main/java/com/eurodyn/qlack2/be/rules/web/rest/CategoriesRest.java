package com.eurodyn.qlack2.be.rules.web.rest;

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

import com.eurodyn.qlack2.be.rules.api.CategoriesService;
import com.eurodyn.qlack2.be.rules.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteCategoryResult;
import com.eurodyn.qlack2.be.rules.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.GetCategoryByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.rules.web.dto.CategoryRestDTO;
import com.eurodyn.qlack2.be.rules.web.util.RestConverterUtil;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;

@Path("/categories")
public class CategoriesRest {

	private CategoriesService categoriesService;

	private RestConverterUtil mapper = RestConverterUtil.INSTANCE;

	@Context
	private HttpHeaders headers;

	public void setCategoriesService(CategoriesService categoriesService) {
		this.categoriesService = categoriesService;
	}

	@GET
	@Path("{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public CategoryDTO getCategory(@PathParam("categoryId") String categoryId) {

		GetCategoryRequest request = new GetCategoryRequest();
		request.setId(categoryId);

		return categoriesService.getCategory(Utils.sign(request, headers));
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument
	public String createCategory(CategoryRestDTO categoryRestDto) {

		checkUniqueCategoryNameOnCreate(categoryRestDto.getProjectId(), categoryRestDto.getName());

		CreateCategoryRequest request = mapper.mapCreateCategory(categoryRestDto);

		return categoriesService.createCategory(Utils.sign(request, headers));
	}

	@PUT
	@Path("{categoryId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateCategory(@PathParam("categoryId") String categoryId, CategoryRestDTO categoryRestDto) {

		checkUniqueCategoryNameOnUpdate(categoryId, categoryRestDto.getName());

		UpdateCategoryRequest request = mapper.mapUpdateCategory(categoryRestDto);
		request.setId(categoryId);

		categoriesService.updateCategory(Utils.sign(request, headers));
	}

	@DELETE
	@Path("{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteCategory(@PathParam("categoryId") String categoryId) {

		DeleteCategoryRequest request = new DeleteCategoryRequest();
		request.setId(categoryId);

		categoriesService.deleteCategory(Utils.sign(request, headers));
	}

	@GET
	@Path("{categoryId}/canDelete")
	@Produces(MediaType.APPLICATION_JSON)
	public CanDeleteCategoryResult canDeleteCategory(@PathParam("categoryId") String categoryId) {

		DeleteCategoryRequest request = new DeleteCategoryRequest();
		request.setId(categoryId);

		return categoriesService.canDeleteCategory(Utils.sign(request, headers));
	}

	// -- Helpers

	private void checkUniqueCategoryNameOnCreate(String projectId, String name) throws QValidationException {
		GetCategoryByProjectAndNameRequest request = new GetCategoryByProjectAndNameRequest();
		request.setProjectId(projectId);
		request.setName(name);

		CategoryDTO existingCategory = categoriesService.getCategoryByProjectAndName(Utils.sign(request, headers));
		if (existingCategory != null) {
			throw new QValidationException(createUniqueCategoryNameError(name));
		}
	}

	private void checkUniqueCategoryNameOnUpdate(String categoryId, String name) throws QValidationException {
		GetCategoryRequest currentRequest = new GetCategoryRequest();
		currentRequest.setId(categoryId);

		// ... or just query for projectId
		CategoryDTO currentCategory = categoriesService.getCategory(Utils.sign(currentRequest, headers));
		if (currentCategory != null) {
			String projectId = currentCategory.getProjectId();

			GetCategoryByProjectAndNameRequest request = new GetCategoryByProjectAndNameRequest();
			request.setProjectId(projectId);
			request.setName(name);

			CategoryDTO existingCategory = categoriesService.getCategoryByProjectAndName(Utils.sign(request, headers));
			if (existingCategory != null) {
				if (!existingCategory.getId().equals(categoryId)) {
					throw new QValidationException(createUniqueCategoryNameError(name));
				}
			}
		}
	}

	private static ValidationErrors createUniqueCategoryNameError(String name) {
		ValidationErrors ve = new ValidationErrors();

		ValidationFieldErrors vfe = new ValidationFieldErrors("name");
		ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
		vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
		vet.putAttribute(ValidationAttribute.InvalidValue, name);
		vfe.addError(vet);

		ve.addValidationError(vfe);
		return ve;
	}

}
