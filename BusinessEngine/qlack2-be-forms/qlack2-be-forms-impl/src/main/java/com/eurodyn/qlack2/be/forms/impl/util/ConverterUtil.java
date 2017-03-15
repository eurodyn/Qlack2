package com.eurodyn.qlack2.be.forms.impl.util;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.forms.api.dto.*;
import com.eurodyn.qlack2.be.forms.api.dto.xml.*;
import com.eurodyn.qlack2.be.forms.api.exception.QServiceNotAvailableException;
import com.eurodyn.qlack2.be.forms.impl.dto.*;
import com.eurodyn.qlack2.be.forms.impl.model.*;
import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleVersionIdentifierRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdentifierRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.webdesktop.api.DesktopUserService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserUncheckedRequest;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Utility class to convert 1.transfer object to entity 2.entity to transfer
 * object.
 *
 * @author European Dynamics SA
 */
public class ConverterUtil {
	private static final Logger LOGGER = Logger.getLogger(ConverterUtil.class
			.getName());

	private DesktopUserService desktopUserService;

	private LanguageService languageService;

	private List<WorkingSetsService> workingSetsServiceList;

	private List<RulesService> rulesServiceList;

	private UserDTO user(String userId) {
		if (userId != null) {
			GetUserUncheckedRequest request = new GetUserUncheckedRequest(userId, false);
			return desktopUserService.getUserUnchecked(request);
		}
		else {
			return null;
		}
	}

	public AuditProjectDTO projectToAuditProjectDTO(ProjectDTO project) {
		if (project == null) {
			return null;
		}

		AuditProjectDTO auditProjectDTO = new AuditProjectDTO();
		auditProjectDTO.setId(project.getId());
		auditProjectDTO.setName(project.getName());

		return auditProjectDTO;
	}

	/**
	 * Converts a category object to categoryDTO object. In case the parameter
	 * includeFormResources is true, then the form resources of each category
	 * are also converted to formDTOs
	 *
	 * @param category
	 * @param includeFormResources
	 * @return
	 */
	public CategoryDTO categoryToCategoryDTO(Category category,
			SignedTicket signedTicket, boolean includeFormResources) {
		if (category == null) {
			return null;
		}

		CategoryDTO categoryDTO = new CategoryDTO();
		categoryDTO.setId(category.getId());
		categoryDTO.setName(category.getName());

		if (includeFormResources && category.getForms() != null
				&& !category.getForms().isEmpty()) {
			categoryDTO.setForms(formsToFormDTOList(category.getForms(),
					signedTicket));
		}

		return categoryDTO;
	}

	/**
	 * Converts a category object to categoryDTO object.
	 *
	 * @param category
	 * @return
	 */
	public CategoryDTO categoryToCategoryDTO(Category category,
			SignedTicket signedTicket) {
		if (category == null) {
			return null;
		}

		CategoryDTO categoryDTO = new CategoryDTO();
		categoryDTO.setId(category.getId());
		categoryDTO.setName(category.getName());
		categoryDTO.setDescription(category.getDescription());
		categoryDTO.setCreatedOn(category.getCreatedOn());
		categoryDTO.setLastModifiedOn(category.getLastModifiedOn());

		categoryDTO.setCreatedBy(user(category.getCreatedBy()));
		categoryDTO.setLastModifiedBy(user(category.getLastModifiedBy()));

		return categoryDTO;
	}

	/**
	 * Converts a list of category objects to a list of categoryDTO objects
	 *
	 * @param categories
	 * @return
	 */
	public List<CategoryDTO> categoriesToCategoryDTOList(
			Collection<Category> categories, SignedTicket signedTicket) {
		if (categories == null) {
			return null;
		}

		List<CategoryDTO> categoryDTOs = new ArrayList<>();
		for (Category category : categories) {
			categoryDTOs.add(categoryToCategoryDTO(category, signedTicket));
		}
		return categoryDTOs;
	}

	/**
	 * Returns the list of category ids given a list of category objects
	 *
	 * @param categories
	 * @return
	 */
	public List<String> categoriesToCategoryIdsList(
			Collection<Category> categories) {
		if (categories == null) {
			return null;
		}

		List<String> categoryIds = new ArrayList<>();
		for (Category category : categories) {
			categoryIds.add(category.getId());
		}
		return categoryIds;
	}

	/**
	 * Converts a category object to auditCategoryDTO object.
	 *
	 * @param category
	 * @return
	 */
	public AuditCategoryDTO categoryToAuditCategoryDTO(Category category) {
		if (category == null) {
			return null;
		}

		AuditCategoryDTO auditCategoryDTO = new AuditCategoryDTO();
		auditCategoryDTO.setId(category.getId());
		auditCategoryDTO.setName(category.getName());
		auditCategoryDTO.setDescription(category.getDescription());
		return auditCategoryDTO;
	}

	/**
	 * Converts a list of category objects to a list of auditCategoryDTO objects
	 *
	 * @param categories
	 * @return
	 */
	public List<AuditCategoryDTO> categoriesToAuditCategoryDTOList(
			Collection<Category> categories) {
		if (categories == null) {
			return null;
		}

		List<AuditCategoryDTO> auditCategoryDTOs = new ArrayList<>();
		for (Category category : categories) {
			auditCategoryDTOs.add(categoryToAuditCategoryDTO(category));
		}
		return auditCategoryDTOs;
	}

	/**
	 * Converts a form object to formDTO object.
	 *
	 * @param form
	 * @return
	 */
	public FormDTO formToFormDTO(Form form, SignedTicket signedTicket) {
		if (form == null) {
			return null;
		}

		FormDTO formDTO = new FormDTO();
		formDTO.setId(form.getId());
		formDTO.setName(form.getName());
		formDTO.setDescription(form.getDescription());
		formDTO.setActive(form.isActive());
		formDTO.setCreatedOn(form.getCreatedOn());
		formDTO.setLastModifiedOn(form.getLastModifiedOn());

		formDTO.setCreatedBy(user(form.getCreatedBy()));
		formDTO.setLastModifiedBy(user(form.getLastModifiedBy()));

		formDTO.setLocales(form.getLocales());
		return formDTO;
	}

	/**
	 * Converts a list of form objects to a list of formDTO objects.
	 *
	 * @param forms
	 * @return
	 */
	public List<FormDTO> formsToFormDTOList(Collection<Form> forms,
			SignedTicket signedTicket) {
		if (forms == null) {
			return null;
		}

		List<FormDTO> formDTOs = new ArrayList<>();
		for (Form form : forms) {
			formDTOs.add(formToFormDTO(form, signedTicket));
		}
		return formDTOs;
	}

	public FormDetailsDTO formToFormDetailsDTO(Form form,
			SignedTicket signedTicket) {
		if (form == null) {
			return null;
		}

		FormDetailsDTO formDetailsDTO = new FormDetailsDTO();
		formDetailsDTO.setId(form.getId());
		formDetailsDTO.setName(form.getName());
		formDetailsDTO.setDescription(form.getDescription());
		formDetailsDTO.setActive(form.isActive());
		formDetailsDTO.setCreatedOn(form.getCreatedOn());
		formDetailsDTO.setLastModifiedOn(form.getLastModifiedOn());

		formDetailsDTO.setCreatedBy(user(form.getCreatedBy()));
		formDetailsDTO.setLastModifiedBy(user(form.getLastModifiedBy()));

		List<CategoryDTO> categoryDTOs = categoriesToCategoryDTOList(
				form.getCategories(), signedTicket);
		formDetailsDTO.setCategories(categoryDTOs);

		formDetailsDTO.setLocales(form.getLocales());

		List<FormVersionDetailsDTO> formVersionDetailsDTOs = formVersionsToFormVersionDetailsDTOList(
				form.getFormVersions(), signedTicket);
		formDetailsDTO.setFormVersions(formVersionDetailsDTOs);

		return formDetailsDTO;
	}

	/**
	 * Converts a form object to auditFormDTO object.
	 *
	 * @param form
	 * @return
	 */
	public AuditFormDTO formToAuditFormDTO(Form form) {
		if (form == null) {
			return null;
		}

		AuditFormDTO formDTO = new AuditFormDTO();
		formDTO.setId(form.getId());
		formDTO.setName(form.getName());
		formDTO.setDescription(form.getDescription());
		formDTO.setActive(form.isActive());
		formDTO.setLocales(form.getLocales());

		List<AuditCategoryDTO> auditCategoryDTOs = categoriesToAuditCategoryDTOList(form
				.getCategories());
		formDTO.setCategories(auditCategoryDTOs);

		List<AuditFormVersionDTO> auditFormVersionDTOs = formVersionsToAuditFormVersionDTOList(form
				.getFormVersions());
		formDTO.setFormVersions(auditFormVersionDTOs);
		return formDTO;
	}

	public FormVersionDTO formVersionToFormVersionDTO(FormVersion formVersion) {
		if (formVersion == null) {
			return null;
		}

		FormVersionDTO formVersionDTO = new FormVersionDTO();
		formVersionDTO.setId(formVersion.getId());
		formVersionDTO.setName(formVersion.getName());
		formVersionDTO.setState(formVersion.getState().ordinal());

		return formVersionDTO;
	}

	public List<FormVersionDTO> formVersionsToFormVersionDTOList(
			Collection<FormVersion> formVersions) {
		if (formVersions == null) {
			return null;
		}

		List<FormVersionDTO> formVersionDTOs = new ArrayList<>();
		for (FormVersion formVersion : formVersions) {
			formVersionDTOs.add(formVersionToFormVersionDTO(formVersion));
		}

		return formVersionDTOs;
	}

	public FormVersionDetailsDTO formVersionToFormVersionDetailsDTO(
			FormVersion formVersion, SignedTicket signedTicket) {
		if (formVersion == null) {
			return null;
		}

		FormVersionDetailsDTO formVersionDTO = new FormVersionDetailsDTO();
		formVersionDTO.setId(formVersion.getId());
		formVersionDTO.setName(formVersion.getName());
		formVersionDTO.setDescription(formVersion.getDescription());
		formVersionDTO.setState(formVersion.getState().ordinal());
		formVersionDTO.setContent(formVersion.getContent());
		formVersionDTO.setCreatedOn(formVersion.getCreatedOn());
		formVersionDTO.setLastModifiedOn(formVersion.getLastModifiedOn());
		formVersionDTO.setLockedOn(formVersion.getLockedOn());

		formVersionDTO.setCreatedBy(user(formVersion.getCreatedBy()));
		formVersionDTO.setLastModifiedBy(user(formVersion.getLastModifiedBy()));
		formVersionDTO.setLockedBy(user(formVersion.getLockedBy()));

		formVersionDTO.setConditions(conditionToConditionDTOList(formVersion.getConditions()));

		return formVersionDTO;
	}

	public List<FormVersionDetailsDTO> formVersionsToFormVersionDetailsDTOList(
			Collection<FormVersion> formVersions, SignedTicket signedTicket) {
		if (formVersions == null) {
			return null;
		}

		List<FormVersionDetailsDTO> formVersionDetailsDTOs = new ArrayList<>();
		for (FormVersion formVersion : formVersions) {
			formVersionDetailsDTOs.add(formVersionToFormVersionDetailsDTO(
					formVersion, signedTicket));
		}

		return formVersionDetailsDTOs;
	}

	public XmlFormVersionDTO formVersionToXmlFormVersionDTO(
			FormVersion formVersion, List<KeyDTO> keys,
			SignedTicket signedTicket) {
		if (formVersion == null) {
			return null;
		}

		XmlFormVersionDTO xmlFormVersionDTO = new XmlFormVersionDTO();
		xmlFormVersionDTO.setName(formVersion.getName());
		xmlFormVersionDTO.setDescription(formVersion.getDescription());
		xmlFormVersionDTO.setContent(formVersion.getContent());

		if (formVersion.getConditions() != null
				&& !formVersion.getConditions().isEmpty()) {
			XmlConditionsDTO xmlConditionsDTO = new XmlConditionsDTO();
			xmlConditionsDTO.setConditions(conditionToXmlConditionDTOList(
					formVersion.getConditions(), signedTicket));
			xmlFormVersionDTO.setConditions(xmlConditionsDTO);
		}

		if (keys != null && !keys.isEmpty()) {
			XmlTranslationsDTO xmlTranslationsDTO = new XmlTranslationsDTO();
			xmlTranslationsDTO
					.setTranslations(keyDTOToXmlTranslationDTOList(keys));
			xmlFormVersionDTO.setTranslations(xmlTranslationsDTO);
		}

		return xmlFormVersionDTO;
	}

	public List<XmlTranslationDTO> keyDTOToXmlTranslationDTOList(
			List<KeyDTO> keys) {
		if (keys == null) {
			return null;
		}

		List<XmlTranslationDTO> xmlTranslationDTOs = new ArrayList<>();
		for (KeyDTO key : keys) {
			xmlTranslationDTOs.add(keyDTOToXmlTranslationDTO(key));
		}

		return xmlTranslationDTOs;
	}

	private XmlTranslationDTO keyDTOToXmlTranslationDTO(KeyDTO key) {
		if (key == null) {
			return null;
		}

		XmlTranslationDTO xmlTranslationDTO = new XmlTranslationDTO();
		xmlTranslationDTO.setKey(key.getName());

		XmlTranslationValuesDTO xmlTranslationValuesDTO = new XmlTranslationValuesDTO();

		Map<String, String> translations = key.getTranslations();
		List<XmlTranslationValueDTO> xmlTranslationValueDTOs = new ArrayList<>();
		for (String languageId : translations.keySet()) {
			LanguageDTO languageDTO = languageService.getLanguage(languageId);

			XmlTranslationValueDTO xmlTranslationValueDTO = new XmlTranslationValueDTO();
			xmlTranslationValueDTO.setLang(languageDTO.getLocale());
			xmlTranslationValueDTO.setValue(translations.get(languageId));

			xmlTranslationValueDTOs.add(xmlTranslationValueDTO);
		}

		xmlTranslationValuesDTO.setValues(xmlTranslationValueDTOs);
		xmlTranslationDTO.setValues(xmlTranslationValuesDTO);

		return xmlTranslationDTO;
	}

	public AuditFormVersionDTO formVersionToAuditFormVersionDetailsDTO(
			FormVersion formVersion) {
		if (formVersion == null) {
			return null;
		}

		AuditFormVersionDTO formVersionDTO = new AuditFormVersionDTO();
		formVersionDTO.setId(formVersion.getId());
		formVersionDTO.setName(formVersion.getName());
		formVersionDTO.setDescription(formVersion.getDescription());
		formVersionDTO.setState(formVersion.getState().ordinal());
		formVersionDTO.setLocked((formVersion.getLockedOn() != null));
		formVersionDTO.setConditions(conditionToConditionDTOList(formVersion
				.getConditions()));

		return formVersionDTO;
	}

	/**
	 * Converts a list of form version objects to a list of auditFormVersionDTO
	 * objects
	 *
	 * @return
	 */
	public List<AuditFormVersionDTO> formVersionsToAuditFormVersionDTOList(
			Collection<FormVersion> formVersions) {
		if (formVersions == null) {
			return null;
		}

		List<AuditFormVersionDTO> formVersionDTOs = new ArrayList<>();
		for (FormVersion formVersion : formVersions) {
			formVersionDTOs
					.add(formVersionToAuditFormVersionDetailsDTO(formVersion));
		}
		return formVersionDTOs;
	}

	public AuditOrbeonFormVersionDTO formVersionToAuditOrbeonFormDTO(
			FormVersion formVersion) {
		if (formVersion == null) {
			return null;
		}

		AuditOrbeonFormVersionDTO orbeonFormVersionDTO = new AuditOrbeonFormVersionDTO();
		orbeonFormVersionDTO.setId(formVersion.getId());
		orbeonFormVersionDTO.setName(formVersion.getName());
		orbeonFormVersionDTO.setDescription(formVersion.getDescription());
		orbeonFormVersionDTO.setState(formVersion.getState().ordinal());

		return orbeonFormVersionDTO;
	}

	public ConditionDTO conditionToConditionDTO(Condition condition) {
		if (condition == null) {
			return null;
		}

		ConditionDTO conditionDTO = new ConditionDTO();
		conditionDTO.setId(condition.getId());
		conditionDTO.setName(condition.getName());
		conditionDTO.setConditionType(Integer.valueOf(condition
				.getConditionType().ordinal()));
		conditionDTO.setWorkingSetId(condition.getWorkingSetId());
		conditionDTO.setRuleId(condition.getRuleId());
		conditionDTO.setParentCondition(conditionToConditionDTO(condition
				.getParent()));

		return conditionDTO;
	}

	public List<ConditionDTO> conditionToConditionDTOList(
			Collection<Condition> conditions) {
		if (conditions == null) {
			return null;
		}

		List<ConditionDTO> conditionDTOs = new ArrayList<ConditionDTO>();
		for (Condition condition : conditions) {
			conditionDTOs.add(conditionToConditionDTO(condition));
		}
		return conditionDTOs;
	}

	public Condition conditionDTOToCondition(ConditionDTO conditionDTO) {
		if (conditionDTO == null) {
			return null;
		}

		Condition condition = new Condition();
		if (conditionDTO.getId() != null) {
			condition.setId(conditionDTO.getId());
		}

		condition.setName(conditionDTO.getName());
		condition.setConditionType(ConditionType.values()[conditionDTO
				.getConditionType()]);
		condition.setWorkingSetId(conditionDTO.getWorkingSetId());
		condition.setRuleId(conditionDTO.getRuleId());

		return condition;
	}

	public XmlConditionDTO conditionToXmlConditionDTO(Condition condition,
			SignedTicket signedTicket) {
		if (condition == null) {
			return null;
		}

		if (workingSetsServiceList.size() == 0 || rulesServiceList.size() == 0) {
			LOGGER.log(Level.SEVERE,
					"WorkingSetsService and/or RulesService are not available");
			throw new QServiceNotAvailableException(
					"WorkingSetsService and/or RulesService are not available");
		}

		GetWorkingSetVersionIdentifierRequest workingSetVersionRequest = new GetWorkingSetVersionIdentifierRequest();
		workingSetVersionRequest.setSignedTicket(signedTicket);
		workingSetVersionRequest.setId(condition.getWorkingSetId());
		WorkingSetVersionIdentifierDTO workingSetVersion = workingSetsServiceList
				.get(0)
				.getWorkingSetVersionIdentifier(workingSetVersionRequest);

		GetRuleVersionIdentifierRequest ruleVersionRequest = new GetRuleVersionIdentifierRequest();
		ruleVersionRequest.setSignedTicket(signedTicket);
		ruleVersionRequest.setId(condition.getRuleId());
		RuleVersionIdentifierDTO ruleVersion = rulesServiceList.get(0)
				.getRuleVersionIdentifier(ruleVersionRequest);

		XmlConditionDTO xmlConditionDTO = new XmlConditionDTO();
		xmlConditionDTO.setName(condition.getName());
		xmlConditionDTO.setConditionType(Integer.valueOf(condition
				.getConditionType().ordinal()));
		xmlConditionDTO.setWorkingSet(workingSetVersion.getWorkingSetName());
		xmlConditionDTO.setWorkingSetVersion(workingSetVersion.getName());
		xmlConditionDTO.setRule(ruleVersion.getRuleName());
		xmlConditionDTO.setRuleVersion(ruleVersion.getName());
		xmlConditionDTO
				.setParentCondition((condition.getParent() != null) ? condition
						.getParent().getName() : null);

		return xmlConditionDTO;
	}

	public List<XmlConditionDTO> conditionToXmlConditionDTOList(
			Collection<Condition> conditions, SignedTicket signedTicket) {
		if (conditions == null) {
			return null;
		}

		List<XmlConditionDTO> conditionDTOs = new ArrayList<XmlConditionDTO>();
		for (Condition condition : conditions) {
			conditionDTOs.add(conditionToXmlConditionDTO(condition,
					signedTicket));
		}
		return conditionDTOs;
	}

	public List<ConditionDTO> xmlConditionDTOToConditionDTOList(
			List<XmlConditionDTO> xmlConditionDTOs, String projectId,
			SignedTicket signedTicket) {
		if (xmlConditionDTOs == null) {
			return null;
		}

		Map<String, ConditionDTO> conditionDTOsMap = new HashMap<String, ConditionDTO>();
		for (XmlConditionDTO xmlConditionDTO : xmlConditionDTOs) {
			ConditionDTO conditionDTO = xmlConditionDTOToConditionDTO(xmlConditionDTO, projectId,
					signedTicket);

			if (conditionDTO != null) {
				conditionDTOsMap.put(conditionDTO.getName(), conditionDTO);
			}
		}

		// Loop to create parent relationships
		for (XmlConditionDTO xmlConditionDTO : xmlConditionDTOs) {
			if (xmlConditionDTO.getParentCondition() != null && !StringUtils.isEmpty(xmlConditionDTO.getParentCondition())) {
				ConditionDTO conditionDTO = conditionDTOsMap.get(xmlConditionDTO.getName());
				ConditionDTO parentCondtionDTO = conditionDTOsMap.get(xmlConditionDTO.getParentCondition());

				conditionDTO.setParentCondition(parentCondtionDTO);
			}
		}

		return new ArrayList<>(conditionDTOsMap.values());
	}

	public ConditionDTO xmlConditionDTOToConditionDTO(
			XmlConditionDTO xmlConditionDTO, String projectId,
			SignedTicket signedTicket) {
		if (xmlConditionDTO == null) {
			return null;
		}

		if (workingSetsServiceList.size() == 0 || rulesServiceList.size() == 0) {
			LOGGER.log(Level.SEVERE,
					"WorkingSetsService and/or RulesService are not available");
			throw new QServiceNotAvailableException(
					"WorkingSetsService and/or RulesService are not available");
		}

		GetWorkingSetVersionIdByNameRequest workingSetVersionRequest = new GetWorkingSetVersionIdByNameRequest();
		workingSetVersionRequest.setSignedTicket(signedTicket);
		workingSetVersionRequest.setProjectId(projectId);
		workingSetVersionRequest.setWorkingSetName(xmlConditionDTO
				.getWorkingSet());
		workingSetVersionRequest
				.setName(xmlConditionDTO.getWorkingSetVersion());
		String workingSetVersionId = workingSetsServiceList.get(0)
				.getWorkingSetVersionIdByName(workingSetVersionRequest);

		GetRuleVersionIdByNameRequest ruleVersionRequest = new GetRuleVersionIdByNameRequest();
		ruleVersionRequest.setSignedTicket(signedTicket);
		ruleVersionRequest.setProjectId(projectId);
		ruleVersionRequest.setRuleName(xmlConditionDTO.getRule());
		ruleVersionRequest.setName(xmlConditionDTO.getRuleVersion());
		String ruleVersionId = rulesServiceList.get(0).getRuleVersionIdByName(
				ruleVersionRequest);

		ConditionDTO conditionDTO = new ConditionDTO();
		conditionDTO.setId(UUID.randomUUID().toString());
		conditionDTO.setName(xmlConditionDTO.getName());
		conditionDTO.setConditionType(xmlConditionDTO.getConditionType());
		conditionDTO.setWorkingSetId(workingSetVersionId);
		conditionDTO.setRuleId(ruleVersionId);

		// TODO
		// conditionDTO.setParentCondition(parentCondition);

		return conditionDTO;
	}

	public List<TranslationDTO> keyDTOToTranslationDTOList(
			Collection<KeyDTO> keys) {
		if (keys == null) {
			return null;
		}

		List<TranslationDTO> translationsDTOs = new ArrayList<>();
		for (KeyDTO key : keys) {
			Map<String, String> translations = key.getTranslations();
			for (String language : translations.keySet()) {
				TranslationDTO translationDTO = new TranslationDTO();
				translationDTO.setKeyId(key.getId());
				translationDTO.setKey(key.getName());
				translationDTO.setValue(translations.get(language));
				translationDTO.setLanguage(language);
				translationsDTOs.add(translationDTO);
			}
		}
		return translationsDTOs;
	}

	/**
	 * Converts an attachment object to attachmentDTO object.
	 *
	 * @param attachment
	 * @return
	 */
	public AttachmentDTO attachmentToAttachmentDTO(Attachment attachment) {
		if (attachment == null) {
			return null;
		}

		AttachmentDTO attachmentDTO = new AttachmentDTO();
		attachmentDTO.setFileName(attachment.getFileName());
		attachmentDTO.setFileContent(attachment.getFileContent());
		attachmentDTO.setContentType(attachment.getContentType());
		return attachmentDTO;
	}

	public String xmlDocumentToString(Document document) {
		String content = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");

			transformer.transform(new DOMSource(document), new StreamResult(
					new OutputStreamWriter(out, "UTF-8")));
			content = out.toString();
		} catch (TransformerException | UnsupportedEncodingException e) {
			LOGGER.log(Level.SEVERE,
					"Error transforming xmlDocument to String", e);
		}
		return content;
	}

	public List<WorkingSetDTO> workingSetVersionIdentifierDTOsToWorkingSetDTOList(
			Collection<WorkingSetVersionIdentifierDTO> workingSetVersionIdentifierDTOs) {
		if (workingSetVersionIdentifierDTOs == null) {
			return null;
		}

		List<WorkingSetDTO> workingSetDTOs = new ArrayList<>();
		for (WorkingSetVersionIdentifierDTO workingSetVersionIdentifierDTO : workingSetVersionIdentifierDTOs) {
			workingSetDTOs
					.add(workingSetVersionIdentifierDTOToWorkingSetDTO(workingSetVersionIdentifierDTO));
		}
		return workingSetDTOs;
	}

	public WorkingSetDTO workingSetVersionIdentifierDTOToWorkingSetDTO(
			WorkingSetVersionIdentifierDTO workingSetVersionIdentifierDTO) {
		if (workingSetVersionIdentifierDTO == null) {
			return null;
		}

		WorkingSetDTO workingSetDTO = new WorkingSetDTO();
		workingSetDTO.setId(workingSetVersionIdentifierDTO.getId());
		workingSetDTO.setName(workingSetVersionIdentifierDTO.getName());
		workingSetDTO.setWorkingSetName(workingSetVersionIdentifierDTO
				.getWorkingSetName());
		return workingSetDTO;
	}

	public List<RuleDTO> ruleVersionIdentifierDTOsToRuleDTOList(
			Collection<RuleVersionIdentifierDTO> ruleVersionIdentifierDTOs) {
		if (ruleVersionIdentifierDTOs == null) {
			return null;
		}

		List<RuleDTO> ruleDTOs = new ArrayList<>();
		for (RuleVersionIdentifierDTO ruleVersionIdentifierDTO : ruleVersionIdentifierDTOs) {
			ruleDTOs.add(ruleVersionIdentifierDTOToRuleDTO(ruleVersionIdentifierDTO));
		}
		return ruleDTOs;
	}

	public RuleDTO ruleVersionIdentifierDTOToRuleDTO(
			RuleVersionIdentifierDTO ruleVersionIdentifierDTO) {
		if (ruleVersionIdentifierDTO == null) {
			return null;
		}

		RuleDTO ruleDTO = new RuleDTO();
		ruleDTO.setId(ruleVersionIdentifierDTO.getId());
		ruleDTO.setName(ruleVersionIdentifierDTO.getName());
		ruleDTO.setRuleName(ruleVersionIdentifierDTO.getRuleName());
		ruleDTO.setWorkingSetId(ruleVersionIdentifierDTO
				.getWorkingSetVersionId());

		return ruleDTO;

	}

	public void setDesktopUserService(DesktopUserService desktopUserService) {
		this.desktopUserService = desktopUserService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public void setWorkingSetsServiceList(
			List<WorkingSetsService> workingSetsServiceList) {
		this.workingSetsServiceList = workingSetsServiceList;
	}

	public void setRulesServiceList(List<RulesService> rulesServiceList) {
		this.rulesServiceList = rulesServiceList;
	}

}
