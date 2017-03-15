package com.eurodyn.qlack2.be.workflow.web.rest;

import java.util.logging.Logger;

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

import com.eurodyn.qlack2.be.workflow.api.CategoryService;
import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.request.category.CountCategoryResourcesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoryIdByNameRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.workflow.web.dto.CategoryRDTO;
import com.eurodyn.qlack2.be.workflow.web.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.web.util.Utils;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;

@Path("/categories")
public class CategoryRest {

	private static final Logger LOGGER = Logger.getLogger(CategoryRest.class.getName());

	@Context
	private HttpHeaders headers;

	private CategoryService categoryService;

	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GET
	@Path("{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public CategoryDTO getCategory(@PathParam("categoryId") String categoryId) {
		GetCategoryRequest sreq = new GetCategoryRequest();
		sreq.setCategoryId(categoryId);

		Utils.sign(sreq, headers);
		return categoryService.getCategory(sreq);
	}

	@GET
	@Path("{categoryId}/resources/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Long countCategoryResources(@PathParam("categoryId") String categoryId) {
		CountCategoryResourcesRequest sreq = new CountCategoryResourcesRequest();
		sreq.setCategoryId(categoryId);

		Utils.sign(sreq, headers);
		return categoryService.countCategoryResources(sreq);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument
	public String createCategory(CategoryRDTO categoryRDTO) {
		GetCategoryIdByNameRequest categoryByNameRequest = new GetCategoryIdByNameRequest();
		categoryByNameRequest.setCategoryName(categoryRDTO.getName());
		categoryByNameRequest.setProjectId(categoryRDTO.getProjectId());

		Utils.sign(categoryByNameRequest, headers);
		String existingCategoryId = categoryService.getCategoryIdByName(categoryByNameRequest);
		// A category with the given name already exists -> a validation exception should be thrown
		if (existingCategoryId != null) {
			ValidationErrors ve = new ValidationErrors();
			ValidationFieldErrors vfe = new ValidationFieldErrors("name");
			ValidationErrorType vet = new ValidationErrorType("validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.Message, "validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.InvalidValue, categoryRDTO.getName());
			vfe.addError(vet);
			ve.addValidationError(vfe);
			throw new QValidationException(ve);
		}

		CreateCategoryRequest categoryRequest = ConverterUtil.categoryRDTOToCreateCategoryRequest(categoryRDTO);

		Utils.sign(categoryRequest, headers);
		return categoryService.createCategory(categoryRequest);
	}

	@PUT
	@Path("{categoryId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateCategory(@PathParam("categoryId") String categoryId, CategoryRDTO categoryRDTO) {
		GetCategoryIdByNameRequest categoryByNameRequest = new GetCategoryIdByNameRequest();
		categoryByNameRequest.setCategoryName(categoryRDTO.getName());
		categoryByNameRequest.setProjectId(categoryRDTO.getProjectId());

		Utils.sign(categoryByNameRequest, headers);
		String existingCategoryId = categoryService.getCategoryIdByName(categoryByNameRequest);

		// A category with the given name already exists -> a validation exception should be thrown
		if (existingCategoryId != null && !categoryId.equals(existingCategoryId)) {
			ValidationErrors ve = new ValidationErrors();
			ValidationFieldErrors vfe = new ValidationFieldErrors("name");
			ValidationErrorType vet = new ValidationErrorType("validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.Message, "validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.InvalidValue, categoryRDTO.getName());
			vfe.addError(vet);
			ve.addValidationError(vfe);
			throw new QValidationException(ve);
		}

		UpdateCategoryRequest categoryRequest = ConverterUtil.categoryRDTOToUpdateCategoryRequest(categoryRDTO);
		categoryRequest.setId(categoryId);
		Utils.sign(categoryRequest, headers);
		categoryService.updateCategory(categoryRequest);
	}

	@DELETE
	@Path("{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteCategory(@PathParam("categoryId") String categoryId) {
		DeleteCategoryRequest req = new DeleteCategoryRequest();
		req.setCategoryId(categoryId);

		Utils.sign(req, headers);
		categoryService.deleteCategory(req);
	}
}
