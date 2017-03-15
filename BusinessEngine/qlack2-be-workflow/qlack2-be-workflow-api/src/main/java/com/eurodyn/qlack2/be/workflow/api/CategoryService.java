package com.eurodyn.qlack2.be.workflow.api;

import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.request.category.CountCategoryResourcesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoryIdByNameRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface CategoryService {
	
	CategoryDTO getCategory(GetCategoryRequest request) throws QInvalidTicketException;

	String getCategoryIdByName(GetCategoryIdByNameRequest request) throws QInvalidTicketException;

	Long countCategoryResources(CountCategoryResourcesRequest request) throws QInvalidTicketException;

	String createCategory(CreateCategoryRequest request) throws QInvalidTicketException, QAuthorisationException;

	void updateCategory(UpdateCategoryRequest request) throws QInvalidTicketException, QAuthorisationException;

	void deleteCategory(DeleteCategoryRequest request) throws QInvalidTicketException, QAuthorisationException;
}
