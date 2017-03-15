package com.eurodyn.qlack2.be.forms.api;

import com.eurodyn.qlack2.be.forms.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.forms.api.request.category.CountCategoryResourcesRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.GetCategoryIdByNameRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface CategoriesService {

	/**
	 * Retrieves the metadata of a category.
	 *
	 * @param request
	 * @return
	 */
	CategoryDTO getCategory(GetCategoryRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves a category id by its name.
	 *
	 * @param request
	 * @return
	 */
	String getCategoryIdByName(GetCategoryIdByNameRequest request)
			throws QInvalidTicketException;

	/**
	 * Retrieves the number of form resources that the category belongs to.
	 *
	 * @param request
	 * @return
	 */
	Long countCategoryResources(CountCategoryResourcesRequest request)
			throws QInvalidTicketException;

	/**
	 * Creates a new category.
	 *
	 * @param request
	 * @return
	 */
	String createCategory(CreateCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Updates an existing category.
	 *
	 * @param request
	 */
	void updateCategory(UpdateCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Deletes a category.
	 *
	 * @param request
	 */
	void deleteCategory(DeleteCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException;
}
