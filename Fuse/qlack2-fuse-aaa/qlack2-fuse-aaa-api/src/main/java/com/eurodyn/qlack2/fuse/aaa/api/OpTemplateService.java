/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.aaa.api;

import com.eurodyn.qlack2.fuse.aaa.api.dto.OpTemplateDTO;

/**
 *
 * @author European Dynamics SA
 */
public interface OpTemplateService {
	/**
	 * Creates a new operation template. This method creates the new template
	 * without adding any operations to it. In order to add operations to the
	 * template after its creation the addOperationsById and addOperationsByName
	 * methods can be used.
	 * 
	 * @param templateDTO
	 *            The information of the template to be created.
	 * @return The template ID
	 */
	String createTemplate(OpTemplateDTO templateDTO);

	/**
	 * Deletes an existing operation template
	 * 
	 * @param templateID
	 *            The id of the template to delete
	 */
	void deleteTemplateByID(String templateID);

	/**
	 * Deletes an existing operation template
	 * 
	 * @param templateName
	 *            The name of the template to delete
	 */
	void deleteTemplateByName(String templateName);

	/**
	 * Retrieves an operation template by its ID
	 * 
	 * @param templateID
	 *            The ID of the template to retrieve
	 * @return The retrieved template, or null if the template does not exist.
	 */
	OpTemplateDTO getTemplateByID(String templateID);

	/**
	 * Retrieves an operation template by its name
	 * 
	 * @param templateName
	 *            The name of the template to retrieve
	 * @return The retrieved template
	 */
	OpTemplateDTO getTemplateByName(String templateName);

	/**
	 * Adds an operation to an operation template. If the operation is already
	 * present in the template the value of the isDeny flag is updated.
	 * 
	 * @param templateID
	 *            The ID of the template to add the operation to
	 * @param operationName
	 *            The name of the operation to add
	 * @param isDeny
	 *            True if the operation should be denied (negative permission),
	 *            false otherwise.
	 */
	void addOperation(String templateID, String operationName, boolean isDeny);

	/**
	 * Adds an operation on a resource to an operation template. If the
	 * operation is already present in the template the value of the isDeny flag
	 * is updated.
	 * 
	 * @param templateID
	 *            The ID of the template to add the operation to
	 * @param operationName
	 *            The name of the operation to add
	 * @param resourceID
	 *            The resource on which the operation is applied.
	 * @param isDeny
	 *            True if the operation should be denied (negative permission),
	 *            false otherwise.
	 */
	void addOperation(String templateID, String operationName, String resourceID, boolean isDeny);

	/**
	 * Removes an operation from an operation template. This method should be
	 * used to remove operations which are not assigned to specific resources.
	 * Otherwise the overloaded removeOperation(String, String, String) method
	 * should be used.
	 * 
	 * @param templateID
	 *            The ID of the template to remove the operation from
	 * @param operationName
	 *            The name of the operation.
	 */
	void removeOperation(String templateID, String operationName);

	/**
	 * Removes an operation on a resource from an operation template.
	 * 
	 * @param templateID
	 *            The ID of the template to remove the operation from
	 * @param operationName
	 *            The name of the operation
	 * @param resourceID
	 *            The resource ID.
	 */
	void removeOperation(String templateID, String operationName, String resourceID);

	/**
	 * Retrieves the permission an operation is given in an operation template.
	 * This method should be used for operations which are not assigned to
	 * specific resources. Otherwise the overloaded getOperationAccess(String,
	 * String, String) method should be used.
	 * 
	 * @param templateID
	 *            The ID of the template for which to retrieve the operation
	 *            permission
	 * @param operationName
	 *            The name of the operation
	 * @return true if the operation is available in the operation template with
	 *         positive permission (deny = false), false if the operation is
	 *         available in the operation template with negative permission
	 *         (deny = true) and null if the operation is not available in the
	 *         operation template.
	 */
	Boolean getOperationAccess(String templateID, String operationName);

	/**
	 * Retrieves the permission an operation is given in an operation template
	 * for a specific resource.
	 * 
	 * @param templateID
	 *            The ID of the template for which to retrieve the operation
	 *            permission
	 * @param operationName
	 *            The name of the operation
	 * @param resourceID
	 *            The ID of the resource to check the operation for.
	 * @return true if the operation is available in the operation template with
	 *         positive permission (deny = false), false if the operation is
	 *         available in the operation template with negative permission
	 *         (deny = true) and null if the operation is not available in the
	 *         operation template.
	 */
	Boolean getOperationAccess(String templateID, String operationName, String resourceID);

	/**
	 * Updates the name and description of an existing template. The template is 
	 * looked up by ID.
	 * 
	 * @param templateDTO The details of the template to update.
	 * @return True if the template found and updated, false if the template
	 *         could not be found.
	 */
	boolean updateTemplate(OpTemplateDTO templateDTO);
}
