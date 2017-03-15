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
package com.eurodyn.qlack2.fuse.lexicon.api;

import java.util.Map;

import com.eurodyn.qlack2.fuse.lexicon.api.dto.TemplateDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.exception.QTemplateProcessingException;

/**
 * A service providing functionalities to retrieve and process 
 * lexicon templates.
 * @author European Dynamics SA
 *
 */
public interface TemplateService {
	/**
	 * Creates a new template
	 * @param template The details of the template to create
	 * @return The ID of the new template
	 */
	String createTemplate(TemplateDTO template);
	
	/**
	 * Updates a template
	 * @param template The details of the template to update. The template ID is used to identify the
	 * template while the rest of the template data is used to update it.
	 */
	void updateTemplate(TemplateDTO template);
	
	/**
	 * Deletes a template
	 * @param templateID The ID of the template to delete
	 */
	void deleteTemplate(String templateID);
	
	/** 
	 * Retrieves the details of a template
	 * @param templateID The ID of the template to retrieve
	 * @return The retrieved template
	 */
	TemplateDTO getTemplate(String templateID);
	
	/**
	 * Retrieves the content of a template for all languages
	 * @param templateName The name of the template to retrieve
	 * @return The content of a specified template in all languages
	 * for which this template is available. The result of this method
	 * is a map in which the key is the language ID and the value is the 
	 * template content for this language or null if this template
	 * does not exist.
	 */
	Map<String, String> getTemplateContentByName(String templateName);

	/**
	 * Retrieves the content of a template.
	 * @param templateName The name of the template to retrieve.
	 * @param languageId The language for which to retrieve the
	 * template contents.
	 * @return The content of the specified template or null if the specified
	 * template does not exist.
	 */
	String getTemplateContentByName(String templateName, String languageId);
	
	/**
	 * Processes a template and returns the processed contents
	 * @param templateName The template name
	 * @param languageId The language for which to process the template
	 * @param templateData The data to use during template processing
	 * @return The processed template content.
	 * @throws QTemplateProcessingException If an error occurs during template processing.
	 */
	String processTemplateByName(String templateName, String languageId,
			Map<String, Object> templateData);
	
	/**
	 * Processes an already retrieved template.
	 * @param templateBody The string representation of the template.
	 * @param templateData The data to replace.
	 * @return
	 */
	String processTemplate(String templateBody, Map<String, Object> templateData);
	
	/**
	 * Processes a template and returns the processed contents
	 * @param templateName The template name
	 * @param locale The locale for which to process the template
	 * @param templateData The data to use during template processing
	 * @return The processed template content.
	 * @throws QTemplateProcessingException If an error occurs during template processing.
	 */
	String processTemplateByNameAndLocale(String templateName, String locale,
			Map<String, Object> templateData);
}
