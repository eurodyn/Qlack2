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
package com.eurodyn.qlack2.fuse.lexicon.impl;

import com.eurodyn.qlack2.fuse.lexicon.api.TemplateService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.TemplateDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.exception.QTemplateProcessingException;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Language;
import com.eurodyn.qlack2.fuse.lexicon.impl.model.Template;
import com.eurodyn.qlack2.fuse.lexicon.impl.util.ConverterUtil;
import freemarker.template.TemplateException;
import org.ops4j.pax.cdi.api.OsgiServiceProvider;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Transactional
@OsgiServiceProvider(classes = {TemplateService.class})
public class TemplateServiceImpl implements TemplateService {
	private static final Logger LOGGER = Logger.getLogger(TemplateServiceImpl.class.getName());
	@PersistenceContext(unitName = "fuse-lexicon")
	private EntityManager em;

	/**
	 * Helper method to process a template as string.
	 */
	private String _processTemplate(String content, String templateName,
			Map<String, Object> templateData) {
		StringWriter retVal = new StringWriter();
		try {
			freemarker.template.Template fTemplate = new freemarker.template.Template(templateName,
					new StringReader(content), null);
			fTemplate.process(templateData, retVal);
			retVal.flush();
		} catch (TemplateException | IOException ex) {
			// Catch exceptions and throw RuntimeException instead in order to
			// also roll back the transaction.
			LOGGER.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			throw new QTemplateProcessingException(MessageFormat.format("Error processing template {0}.",
					templateName));
		}
		return retVal.toString();
	}

	/**
	 * Helper method to process a template. It performs a 2-passes variable
	 * replacement to support variables resolution within variables. We have
	 * opted for a non-recursive calling approach to keep the code simple as
	 * only 2 levers of nesting is supported.
	 * 
	 * @param template
	 * @param templateData
	 * @return
	 */
	private String processTemplate(Template template, Map<String, Object> templateData) {
		String retVal = template.getContent();
		// 1st pass.
		if (retVal.contains("${")) {
			retVal = _processTemplate(template.getContent(), template.getName(), templateData);
			// 2nd pass (to support variables in variables).
			if (retVal.contains("${")) {
				retVal = _processTemplate(retVal, template.getName(), templateData);
			}
		}

		return retVal;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String createTemplate(TemplateDTO template) {
		Template entity = new Template();
		entity.setName(template.getName());
		entity.setContent(template.getContent());
		entity.setLanguage(Language.find(template.getLanguageId(), em));
		em.persist(entity);
		return entity.getId();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void updateTemplate(TemplateDTO template) {
		Template entity = Template.find(template.getId(), em);
		entity.setName(template.getName());
		entity.setContent(template.getContent());
		entity.setLanguage(Language.find(template.getLanguageId(), em));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public void deleteTemplate(String templateID) {
		em.remove(Template.find(templateID, em));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public TemplateDTO getTemplate(String templateID) {
		return ConverterUtil.templateToTemplateDTO(Template.find(templateID, em));
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public Map<String, String> getTemplateContentByName(String templateName) {
		List<Template> templates = Template.findByName(templateName, em);
		if (templates.isEmpty()) {
			return null;
		}
		Map<String, String> contents = new HashMap<>();
		for (Template template : templates) {
			contents.put(template.getLanguage().getId(), template.getContent());
		}
		return contents;
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String getTemplateContentByName(String templateName,
			String languageId) {
		Template template = Template.findByNameAndLanguageId(templateName, languageId, em);
		if (template == null) {
			return null;
		}
		return template.getContent();
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String processTemplateByName(String templateName, String languageId,
			Map<String, Object> templateData) {
		Template template = Template.findByNameAndLanguageId(templateName, languageId, em);

		return processTemplate(template, templateData);
	}

	@Override
	@Transactional(TxType.REQUIRED)
	public String processTemplateByNameAndLocale(String templateName,
			String locale, Map<String, Object> templateData) {
		Template template = Template.findByNameAndLocale(templateName, locale, em);
		
		return processTemplate(template, templateData);
	}

	@Override
	public String processTemplate(String templateBody, Map<String, Object> templateData) {
		Template template = new Template();
		template.setContent(templateBody);
		template.setName(UUID.randomUUID().toString());
		
		return processTemplate(template, templateData);
	}

}
