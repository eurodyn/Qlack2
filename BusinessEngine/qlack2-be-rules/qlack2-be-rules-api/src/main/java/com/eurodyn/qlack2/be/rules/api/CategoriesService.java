package com.eurodyn.qlack2.be.rules.api;

import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteCategoryResult;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.GetCategoryByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectCategoriesRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface CategoriesService {

	List<CategoryDTO> getCategories(GetProjectCategoriesRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CategoryDTO getCategory(GetCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CategoryDTO getCategoryByProjectAndName(GetCategoryByProjectAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createCategory(CreateCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void updateCategory(UpdateCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteCategoryResult canDeleteCategory(DeleteCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteCategory(DeleteCategoryRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

}
