package com.eurodyn.qlack2.be.forms.web.rest;

import com.eurodyn.qlack2.be.forms.api.CategoriesService;
import com.eurodyn.qlack2.be.forms.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.forms.api.request.category.*;
import com.eurodyn.qlack2.be.forms.web.dto.CategoryRDTO;
import com.eurodyn.qlack2.be.forms.web.util.ConverterUtil;
import com.eurodyn.qlack2.be.forms.web.util.Utils;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;

@Path("/categories")
public class CategoriesRest {
	private static final Logger LOGGER = Logger.getLogger(CategoriesRest.class.getName());

	@Context
	private HttpHeaders headers;

	private CategoriesService categoriesService;

	/**
	 * Retrieves the metadata of the category with the given id.
	 *
	 * @param categoryId
	 *            The category id
	 * @return
	 */
	@GET
	@Path("{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public CategoryDTO getCategory(@PathParam("categoryId") String categoryId) {
		GetCategoryRequest sreq = new GetCategoryRequest();
		sreq.setCategoryId(categoryId);

		Utils.sign(sreq, headers);
		return categoriesService.getCategory(sreq);
	}

	/**
	 * Retrieves the number of form resources that the category belongs to.
	 *
	 * @param categoryId
	 * @return
	 */
	@GET
	@Path("{categoryId}/resources/count")
	@Produces(MediaType.APPLICATION_JSON)
	public Long countCategoryResources(
			@PathParam("categoryId") String categoryId) {
		CountCategoryResourcesRequest sreq = new CountCategoryResourcesRequest();
		sreq.setCategoryId(categoryId);

		Utils.sign(sreq, headers);
		return categoriesService.countCategoryResources(sreq);
	}

	/**
	 * Creates a new category.
	 *
	 * @param categoryRDTO
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument
	public String createCategory(CategoryRDTO categoryRDTO) {
		GetCategoryIdByNameRequest categoryByNameRequest = new GetCategoryIdByNameRequest();
		categoryByNameRequest.setCategoryName(categoryRDTO.getName());
		categoryByNameRequest.setProjectId(categoryRDTO.getProjectId());

		Utils.sign(categoryByNameRequest, headers);
		String existingCategoryId = categoriesService
				.getCategoryIdByName(categoryByNameRequest);

		// A category with the given name already exists -> a validation
		// exception should be thrown
		if (existingCategoryId != null) {
			ValidationErrors ve = new ValidationErrors();
			ValidationFieldErrors vfe = new ValidationFieldErrors("name");
			ValidationErrorType vet = new ValidationErrorType(
					"validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.Message, 
					"validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.InvalidValue,
					categoryRDTO.getName());
			vfe.addError(vet);
			ve.addValidationError(vfe);
			throw new QValidationException(ve);
		}

		CreateCategoryRequest categoryRequest = ConverterUtil
				.categoryRDTOToCreateCategoryRequest(categoryRDTO);

		Utils.sign(categoryRequest, headers);
		return categoriesService.createCategory(categoryRequest);
	}

	/**
	 * Updates an existing category.
	 *
	 * @param categoryId
	 * @param categoryRDTO
	 */
	@PUT
	@Path("{categoryId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateSingleArgument(requestIndex = 1)
	public void updateCategory(@PathParam("categoryId") String categoryId,
			CategoryRDTO categoryRDTO) {
		GetCategoryIdByNameRequest categoryByNameRequest = new GetCategoryIdByNameRequest();
		categoryByNameRequest.setCategoryName(categoryRDTO.getName());
		categoryByNameRequest.setProjectId(categoryRDTO.getProjectId());

		Utils.sign(categoryByNameRequest, headers);
		String existingCategoryId = categoriesService
				.getCategoryIdByName(categoryByNameRequest);
		// A category with the given name already exists -> a validation
		// exception should be thrown
		if (existingCategoryId != null
				&& !categoryId.equals(existingCategoryId)) {
			ValidationErrors ve = new ValidationErrors();
			ValidationFieldErrors vfe = new ValidationFieldErrors("name");
			ValidationErrorType vet = new ValidationErrorType(
					"validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.Message, 
					"validation.error.category.unique.name");
			vet.putAttribute(ValidationAttribute.InvalidValue,
					categoryRDTO.getName());
			vfe.addError(vet);
			ve.addValidationError(vfe);
			throw new QValidationException(ve);
		}

		UpdateCategoryRequest categoryRequest = ConverterUtil
				.categoryRDTOToUpdateCategoryRequest(categoryRDTO);
		categoryRequest.setId(categoryId);

		Utils.sign(categoryRequest, headers);
		categoriesService.updateCategory(categoryRequest);
	}

	/**
	 * Deletes a category.
	 *
	 * @param categoryId
	 */
	@DELETE
	@Path("{categoryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deleteCategory(@PathParam("categoryId") String categoryId) {
		DeleteCategoryRequest sreq = new DeleteCategoryRequest();
		sreq.setCategoryId(categoryId);

		Utils.sign(sreq, headers);
		categoriesService.deleteCategory(sreq);
	}

	public void setCategoriesService(CategoriesService categoriesService) {
		this.categoriesService = categoriesService;
	}

}
