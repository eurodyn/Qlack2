package com.eurodyn.qlack2.be.forms.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import com.eurodyn.qlack2.be.explorer.api.ProjectService;
import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.explorer.api.request.project.GetProjectRequest;
import com.eurodyn.qlack2.be.forms.api.FormVersionsService;
import com.eurodyn.qlack2.be.forms.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.FormVersionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.FormVersionDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.dto.TranslationDTO;
import com.eurodyn.qlack2.be.forms.api.dto.xml.XmlConditionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.xml.XmlFormVersionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.xml.XmlTranslationDTO;
import com.eurodyn.qlack2.be.forms.api.dto.xml.XmlTranslationValueDTO;
import com.eurodyn.qlack2.be.forms.api.dto.xml.XmlTranslationValuesDTO;
import com.eurodyn.qlack2.be.forms.api.exception.QFaultFormVersionTemplateException;
import com.eurodyn.qlack2.be.forms.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidConditionHierarchyException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidOperationException;
import com.eurodyn.qlack2.be.forms.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.UpdateFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.CanFinaliseFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.CountFormVersionsLockedByOtherUserRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.CreateFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.DeleteFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.DeleteFormVersionTranslationsRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.EnableTestingRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.ExportFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.FinaliseFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.GetFormVersionIdByNameRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.GetFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.GetFormVersionTranslationsRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.GetFormVersionsRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.ImportFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.LockFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.UnlockFormVersionRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.ValidateFormVersionConditionsHierarchyRequest;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditFormVersionDTO;
import com.eurodyn.qlack2.be.forms.impl.model.Condition;
import com.eurodyn.qlack2.be.forms.impl.model.ConditionType;
import com.eurodyn.qlack2.be.forms.impl.model.Form;
import com.eurodyn.qlack2.be.forms.impl.model.FormVersion;
import com.eurodyn.qlack2.be.forms.impl.model.State;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.forms.impl.util.Constants;
import com.eurodyn.qlack2.be.forms.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.forms.impl.util.SecureOperation;
import com.eurodyn.qlack2.be.forms.impl.util.SecurityUtils;
import com.eurodyn.qlack2.be.forms.impl.util.XmlUtil;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionRequest;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.lexicon.api.GroupService;
import com.eurodyn.qlack2.fuse.lexicon.api.KeyService;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.criteria.KeySearchCriteria;
import com.eurodyn.qlack2.fuse.lexicon.api.criteria.KeySearchCriteria.KeySearchCriteriaBuilder;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.GroupDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.KeyDTO;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;

public class FormVersionsServiceImpl implements FormVersionsService {
	private static final Logger LOGGER = Logger
			.getLogger(FormVersionsServiceImpl.class.getName());

	private GroupService groupService;

	private KeyService keyService;

	private LanguageService languageService;

	private IDMService idmService;

	private ProjectService projectExplorerService;

	private SecurityService securityService;

	private ConverterUtil converterUtil;

	private XmlUtil xmlUtil;

	private SecurityUtils securityUtils;

	private AuditClientService auditClientService;

	private EventPublisherService eventPublisher;

	private List<WorkingSetsService> workingSetsServiceList;

	private EntityManager em;

	@Override
	@ValidateTicket
	public List<FormVersionDTO> getFormVersions(GetFormVersionsRequest request) {
		LOGGER.log(Level.FINE,
				"Retrieving list of form versions for form with ID {0}",
				request.getFormId());

		String formId = request.getFormId();

		Form form = Form.find(em, formId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), form.getId(),
				form.getProjectId());

		List<FormVersionDTO> formVersionDTOs = converterUtil
				.formVersionsToFormVersionDTOList(form.getFormVersions());
		return formVersionDTOs;
	}

	@Override
	@ValidateTicket
	public Long countFormVersionsLockedByOtherUser(
			CountFormVersionsLockedByOtherUserRequest request) {
		LOGGER.log(
				Level.FINE,
				"Getting the number of form versions of the form with ID {0} that are locked by a user other than the user with ID {1}",
				new String[] { request.getFormId(),
						request.getSignedTicket().getUserID() });

		String formId = request.getFormId();

		Form form = Form.find(em, formId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), form.getId(),
				form.getProjectId());

		Long count = FormVersion.countFormVersionsLockedByOtherUser(em, formId,
				request.getSignedTicket().getUserID());

		return count;
	}

	@Override
	@ValidateTicket
	public String createFormVersion(CreateFormVersionRequest request) {
		LOGGER.log(Level.FINE,
				"Creating new form version with name {0} for form with ID {1}",
				new String[] { request.getName(), request.getFormId() });

		String formId = request.getFormId();

		Form form = Form.find(em, formId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), form.getId(),
				form.getProjectId());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		// Get project details from projects explorer to fetch the
		// locales of the project and the project name
		GetProjectRequest projectRequest = new GetProjectRequest(
				form.getProjectId());
		projectRequest.setSignedTicket(request.getSignedTicket());

		ProjectDTO projectDTO = projectExplorerService
				.getProject(projectRequest);

		// If the parameter useTemplateContent is true, then overwrite any
		// content with the orbeon template
		String content = request.getContent();
		if (request.isUseTemplateContent()) {
			String orbeonFormName = form.getName() + " - " + request.getName();
			try {
				content = xmlUtil.getTemplateContent(projectDTO.getName(),
						orbeonFormName);
			} catch (XPathExpressionException | IOException
					| ParserConfigurationException | SAXException e) {
				LOGGER.log(Level.SEVERE,
						"Error reading orbeon template.xml file", e);
				throw new QFaultFormVersionTemplateException(
						"Could not parse orbeon orbeon template.xml file");
			}
		}

		FormVersion formVersion = new FormVersion();
		formVersion.setName(request.getName());
		formVersion.setDescription(request.getDescription());
		formVersion.setContent(content);
		formVersion.setForm(form);
		formVersion.setState(State.DRAFT);
		formVersion.setCreatedBy(request.getSignedTicket().getUserID());
		formVersion.setCreatedOn(millis);
		formVersion.setLastModifiedBy(request.getSignedTicket().getUserID());
		formVersion.setLastModifiedOn(millis);

		em.persist(formVersion);

		if (request.getConditions() != null) {
			// create conditions
			Map<String, ConditionDTO> conditionDTOs = new HashMap<>();
			for (ConditionDTO conditionDTO : request.getConditions()) {
				conditionDTOs.put(conditionDTO.getId(), conditionDTO);
			}

			Map<String, Condition> conditions = createConditions(conditionDTOs,
					formVersion);

			formVersion.setConditions(new ArrayList(conditions.values()));
		}

		if (request.getTranslations() != null) {
			// Create translations
			createTranslations(request.getTranslations(), formVersion.getId(),
					form.getLocales());
		}

		publishEvent(request.getSignedTicket(), Constants.EVENT_CREATE,
				formVersion.getId());

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.CREATE.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);

		return formVersion.getId();
	}

	@Override
	@ValidateTicket
	public void updateFormVersion(UpdateFormRequest request) {
		LOGGER.log(Level.FINE, "Updating form version with ID {0}",
				request.getVersionId());

		String formVersionId = request.getVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		// Form version is finalised and cannot be edited.
		if (formVersion.getState() == State.FINAL) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be updated, since it is finalised.",
					formVersionId);
			throw new QInvalidOperationException("Form version is finalised.");
		}

		// Form version is locked by another user and cannot be edited.
		if (formVersion.getLockedBy() != null
				&& !formVersion.getLockedBy().equals(
						request.getSignedTicket().getUserID())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be updated, since it is locked by another user.",
					formVersionId);
			throw new QInvalidOperationException(
					"Form version is locked by other user.");
		}

		formVersion.setDescription(request.getVersionDescription());
		formVersion.setContent(request.getVersionContent());
		formVersion.setLastModifiedBy(request.getSignedTicket().getUserID());
		formVersion.setLastModifiedOn(millis);

		if (request.getVersionConditions() != null) {
			List<Condition> oldConditions = formVersion.getConditions();

			Map<String, ConditionDTO> conditionDTOs = new HashMap<>();
			for (ConditionDTO conditionDTO : request.getVersionConditions()) {
				conditionDTOs.put(conditionDTO.getId(), conditionDTO);
			}

			// Validate conditions for cyclic dependencies. This validation
			// should be always performed, because invalid data will result in
			// infinite loop.
			ValidateFormVersionConditionsHierarchyRequest validateRequest = new ValidateFormVersionConditionsHierarchyRequest();
			validateRequest.setConditions(conditionDTOs);
			validateRequest.setSignedTicket(request.getSignedTicket());
			validateFormVersionConditionsHierarchy(validateRequest);

			// Create conditions
			Map<String, Condition> newConditions = createConditions(
					conditionDTOs, formVersion);

			// Loop over the old conditions to remove the ones that do
			// not exist any more
			for (Condition oldCondition : oldConditions) {
				if (!newConditions.containsKey(oldCondition.getId())) {
					em.remove(oldCondition);
				}
			}

			formVersion.setConditions(new ArrayList<>(newConditions.values()));
		} else {
			List<Condition> oldConditions = formVersion.getConditions();
			// Loop over the old conditions to remove the ones that do
			// not exist any more
			for (Condition oldCondition : oldConditions) {
				em.remove(oldCondition);
			}
		}

		if (request.getVersionTranslations() != null
				&& formVersion.getForm().getLocales() != null) {
			// Create translations
			createTranslations(request.getVersionTranslations(), formVersionId,
					formVersion.getForm().getLocales());
		}

		publishEvent(request.getSignedTicket(), Constants.EVENT_UPDATE,
				formVersionId);

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.UPDATE.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);
	}

	private Map<String, Condition> createConditions(
			Map<String, ConditionDTO> conditionDTOs, FormVersion formVersion) {
		// Map that holds the original id of the condition and the
		// created condition.
		Map<String, Condition> conditions = new HashMap<>();

		// Create conditions and fix parent relationships.
		for (ConditionDTO conditionDTO : conditionDTOs.values()) {
			Condition condition = conditions.get(conditionDTO.getId());

			if (condition == null) {
				condition = createCondition(conditionDTO, conditionDTOs,
						conditions, formVersion);
			}
		}
		return conditions;
	}

	/**
	 * This method creates a new condition and associates it with a newly
	 * created form version. All properties of the conditionDTO are copied to
	 * the new condition. When a condition has a parent one, then the parent
	 * condition is created and persisted recursively and then associated with
	 * new condition.
	 *
	 * @param conditionDTO
	 * @param conditionDTOs
	 * @param conditions
	 * @param formVersion
	 * @return
	 */
	private Condition createCondition(ConditionDTO conditionDTO,
			Map<String, ConditionDTO> conditionDTOs,
			Map<String, Condition> conditions, FormVersion formVersion) {
		Condition condition = em.find(Condition.class, conditionDTO.getId());

		if (condition == null) {
			condition = new Condition();
			condition.setId(conditionDTO.getId());
		}

		condition.setName(conditionDTO.getName());
		condition.setConditionType(ConditionType.values()[conditionDTO
				.getConditionType()]);
		condition.setFormVersion(formVersion);
		condition.setWorkingSetId(conditionDTO.getWorkingSetId());
		condition.setRuleId(conditionDTO.getRuleId());

		if (conditionDTO.getParentCondition() != null) {
			// check if parent has already been created
			Condition parentCondition = conditions.get(conditionDTO
					.getParentCondition().getId());
			if (parentCondition == null) {
				// Recursively create parent condition
				ConditionDTO parentConditionDTO = conditionDTOs
						.get(conditionDTO.getParentCondition().getId());
				parentCondition = createCondition(parentConditionDTO,
						conditionDTOs, conditions, formVersion);

				conditions.put(parentConditionDTO.getId(), parentCondition);
			}
			condition.setParent(parentCondition);
		} else {
			condition.setParent(null);
		}

		em.persist(condition);

		// Add the new condition to the already created ones, in order
		// to reuse them if a parent condition is encountered
		// while traversing the list of conditions.
		conditions.put(conditionDTO.getId(), condition);
		return condition;
	}

	private void createTranslations(List<TranslationDTO> translations,
			String formVersionId, List<String> locales) {
		List<KeyDTO> oldKeys = new ArrayList<>();

		List<LanguageDTO> languages = new ArrayList<>();

		for (String locale : locales) {
			languages.add(languageService.getLanguageByLocale(locale));
		}

		Map<String, Map<String, String>> translationsMap = new HashMap<>();
		for (TranslationDTO translation : translations) {
			Map<String, String> translationsForKey = translationsMap
					.get(translation.getKey());
			if (translationsForKey == null) {
				translationsForKey = new HashMap<>();
				translationsMap.put(translation.getKey(), translationsForKey);
			}
			translationsForKey.put(translation.getLanguage(),
					translation.getValue());
		}

		String groupName = formVersionId;
		GroupDTO groupDTO = groupService.getGroupByName(groupName);
		String groupId = null;

		// If a group with this name does not exist create it.
		if (groupDTO == null) {
			groupDTO = new GroupDTO();
			groupDTO.setTitle(groupName);
			groupDTO.setDescription("Translations for form version");
			groupId = groupService.createGroup(groupDTO);
		} else {
			groupId = groupDTO.getId();

			// In this case fetch old keys
			KeySearchCriteriaBuilder builder = KeySearchCriteriaBuilder
					.createCriteria();
			builder.inGroup(groupId);
			KeySearchCriteria criteria = builder.build();

			oldKeys = keyService.findKeys(criteria, false);
		}

		for (String key : translationsMap.keySet()) {
			KeyDTO keyDTO = keyService.getKeyByName(key, groupId, true);
			Map<String, String> translationValues = translationsMap.get(key);

			// If the key does not exist in the DB then create it.
			if (keyDTO == null) {
				keyDTO = new KeyDTO();
				keyDTO.setGroupId(groupId);
				keyDTO.setName(key);
				keyDTO.setTranslations(translationValues);
				String keyId = keyService.createKey(keyDTO, false);
				keyDTO.setId(keyId);
			}

			// Add default value for missing languages
			for (LanguageDTO language : languages) {
				if (!translationValues.containsKey(language.getId())) {
					translationValues.put(language.getId(), key);
				}
			}
			keyService.updateTranslationsForKey(keyDTO.getId(),
					translationValues);
		}

		// Remove non existing keys and their translations
		List<String> removedKeyIds = new ArrayList<>();
		for (KeyDTO keyDTO : oldKeys) {
			if (!translationsMap.containsKey(keyDTO.getName())) {
				removedKeyIds.add(keyDTO.getId());
			}
		}

		if (!removedKeyIds.isEmpty()) {
			keyService.deleteKeys(removedKeyIds);
		}
	}

	@Override
	@ValidateTicket
	public FormVersionDetailsDTO getFormVersion(GetFormVersionRequest request) {
		LOGGER.log(Level.FINE, "Getting form version with ID {0}",
				request.getFormVersionId());

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), formVersion.getForm()
						.getId(), formVersion.getForm().getProjectId());

		FormVersionDetailsDTO formVersionDetailsDTO = converterUtil
				.formVersionToFormVersionDetailsDTO(formVersion,
						request.getSignedTicket());

		List<TranslationDTO> translationsDTOs = getFormVersionTranslations(formVersionId);
		formVersionDetailsDTO.setTranslations(translationsDTOs);

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.VIEW.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);

		return formVersionDetailsDTO;
	}

	@Override
	@ValidateTicket
	public List<TranslationDTO> getFormVersionTranslations(
			GetFormVersionTranslationsRequest request) {
		LOGGER.log(Level.FINE,
				"Getting the translations of the form version with ID {0}",
				request.getFormVersionId());

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_RENDERED_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		List<TranslationDTO> translationsDTOs = getFormVersionTranslations(formVersionId);
		return translationsDTOs;
	}

	private List<TranslationDTO> getFormVersionTranslations(String formVersionId) {
		List<TranslationDTO> translationsDTOs = null;

		// Retrieve lexicon group based on the form version id which is by
		// convention the name of the group
		GroupDTO group = groupService.getGroupByName(formVersionId);

		if (group != null) {
			// Fetch keys
			KeySearchCriteriaBuilder builder = KeySearchCriteriaBuilder
					.createCriteria();
			builder.inGroup(group.getId());
			KeySearchCriteria criteria = builder.build();

			List<KeyDTO> keys = keyService.findKeys(criteria, true);

			translationsDTOs = converterUtil.keyDTOToTranslationDTOList(keys);
		}
		return translationsDTOs;
	}

	@Override
	@ValidateTicket
	public String getFormVersionIdByName(GetFormVersionIdByNameRequest request) {
		LOGGER.log(
				Level.FINE,
				"Getting form version with name {0} for form with ID {1}",
				new String[] { request.getFormVersionName(),
						request.getFormId() });

		String formVersionName = request.getFormVersionName();
		String formId = request.getFormId();

		Form form = Form.find(em, formId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), form.getId(),
				form.getProjectId());

		String formVersionId = FormVersion.getFormVersionIdByName(em,
				formVersionName, formId);

		return formVersionId;
	}

	@Override
	@ValidateTicket
	public void validateFormVersionConditionsHierarchy(
			ValidateFormVersionConditionsHierarchyRequest request) {
		LOGGER.log(Level.FINE,
				"Validating that there exist no cyclic dependency in the list of conditions");

		// Holds the ids and the conditions
		Map<String, ConditionDTO> conditions = request.getConditions();

		for (ConditionDTO condition : conditions.values()) {
			// Check that there is no cyclic dependency in the conditions
			if (condition.getParentCondition() != null) {
				ConditionDTO checkedCondition = condition.getParentCondition();

				while (checkedCondition != null) {
					if (checkedCondition.getId().equals(condition.getId())) {
						LOGGER.log(Level.SEVERE,
								"There exist a cyclic dependency in the defined conditions.");
						throw new QInvalidConditionHierarchyException(
								"There exist a cyclic dependency in the defined conditions.");
					}

					ConditionDTO childCondition = conditions
							.get(checkedCondition.getId());
					checkedCondition = (childCondition != null) ? childCondition
							.getParentCondition() : null;
				}
			}
		}
	}

	@Override
	@ValidateTicket
	public void deleteFormVersion(DeleteFormVersionRequest request) {
		LOGGER.log(Level.FINE, "Deleting form version with ID {0}",
				request.getFormVersionId());

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		// Form version is locked by another user and cannot be deleted.
		if (formVersion.getLockedBy() != null
				&& !formVersion.getLockedBy().equals(
						request.getSignedTicket().getUserID())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be deleted, since it is locked by another user.",
					formVersionId);
			throw new QInvalidOperationException(
					"Form version is locked by other user.");
		}

		// Delete form version translations
		DeleteFormVersionTranslationsRequest deleteRequest = new DeleteFormVersionTranslationsRequest();
		deleteRequest.setFormVersionId(formVersionId);
		deleteRequest.setSignedTicket(request.getSignedTicket());

		deleteFormVersionTranslations(deleteRequest);

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);

		em.remove(formVersion);

		publishEvent(request.getSignedTicket(), Constants.EVENT_DELETE,
				formVersionId);

		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.DELETE.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);
	}

	@Override
	@ValidateTicket
	public void deleteFormVersionTranslations(
			DeleteFormVersionTranslationsRequest request) {
		LOGGER.log(Level.FINE,
				"Deleting the translations of the form version with ID {0}",
				request.getFormVersionId());

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		// Retrieve lexicon group based on the form version id which is by
		// convention the name of the group
		GroupDTO group = groupService.getGroupByName(formVersionId);

		if (group != null) {
			// Delete group to delete all translations
			groupService.deleteGroup(group.getId());
		}
	}

	@Override
	@ValidateTicket
	public void lockFormVersion(LockFormVersionRequest request) {
		LOGGER.log(Level.FINE, "Locking form version with ID {0}",
				request.getFormVersionId());

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_LOCK_FORM.toString(), formVersion.getForm()
						.getId(), formVersion.getForm().getProjectId());

		// Form version is finalised and cannot be locked.
		if (formVersion.getState() == State.FINAL) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be locked, since it is finalised.",
					formVersionId);
			throw new QInvalidOperationException("Form version is finalised.");
		}

		// Form version is already locked and cannot be locked.
		if (formVersion.getLockedBy() != null) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be locked, since it is locked by another user.",
					formVersionId);
			throw new QInvalidOperationException(
					"Form version is already locked.");
		}

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		formVersion.setLockedBy(request.getSignedTicket().getUserID());
		formVersion.setLockedOn(millis);

		publishEvent(request.getSignedTicket(), Constants.EVENT_LOCK,
				formVersionId);

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.LOCK.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);

	}

	@Override
	@ValidateTicket
	public void unlockFormVersion(UnlockFormVersionRequest request) {
		LOGGER.log(Level.FINE, "Unlocking form version with ID {0}",
				request.getFormVersionId());

		String formVersionId = request.getFormVersionId();

		SignedTicket signedTicket = request.getSignedTicket();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		// Form version is finalised and cannot be unlocked.
		if (formVersion.getState() == State.FINAL) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be unlocked, since it is finalised.",
					formVersionId);
			throw new QInvalidOperationException("Form version is finalised.");
		}

		if (formVersion.getLockedBy() == null) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be unlocked, since it is already unlocked.",
					formVersionId);
			throw new QInvalidOperationException(
					"Form version is already unlocked.");
		} else {
			if (formVersion.getLockedBy().equals(
					request.getSignedTicket().getUserID())) {
				securityUtils.checkUnlockFormOperation(request
						.getSignedTicket(), formVersion.getForm().getId(),
						formVersion.getForm().getProjectId());
			} else {
				// Form version is locked by another user and cannot be unlocked
				// unless the current user has the operation FRM_UNLOCK_ANY_FORM
				Boolean isOnFormPermitted = securityService
						.isPermitted(new IsPermittedRequest(signedTicket,
								SecureOperation.FRM_UNLOCK_ANY_FORM.toString(),
								formVersion.getForm().getId()));

				Boolean isOnProjectPermitted = securityService
						.isPermitted(new IsPermittedRequest(signedTicket,
								SecureOperation.FRM_UNLOCK_ANY_FORM.toString(),
								formVersion.getForm().getProjectId()));

				if (isOnFormPermitted != null && !isOnFormPermitted
						&& isOnProjectPermitted != null
						&& !isOnProjectPermitted) {
					LOGGER.log(
							Level.SEVERE,
							"Form version {0} cannot be unlocked, since it is locked by another user.",
							formVersionId);
					throw new QInvalidOperationException(
							"Form version is locked by other user.");
				}
			}
		}

		formVersion.setLockedBy(null);
		formVersion.setLockedOn(null);

		publishEvent(request.getSignedTicket(), Constants.EVENT_UNLOCK,
				formVersionId);

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.UNLOCK.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);

	}

	@Override
	@ValidateTicket
	public boolean canFinaliseFormVersion(CanFinaliseFormVersionRequest req) {
		LOGGER.log(
				Level.FINE,
				"Checking if finalising form version with ID {0} can be performed",
				req.getFormVersionId());

		String formVersionId = req.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		boolean canFinaliseFormVersion = true;
		if (formVersion.getConditions() != null
				&& !formVersion.getConditions().isEmpty()) {
			if (workingSetsServiceList.size() == 0) {
				LOGGER.log(Level.SEVERE, "No working set service found.");
				throw new QInvalidOperationException(
						"No working set service found.");
			}

			for (Condition condition : formVersion.getConditions()) {
				GetWorkingSetVersionRequest workingSetVersionRequest = new GetWorkingSetVersionRequest();
				workingSetVersionRequest.setSignedTicket(req.getSignedTicket());
				workingSetVersionRequest.setId(condition.getWorkingSetId());
				WorkingSetVersionDTO workingSetVersion = workingSetsServiceList
						.get(0).getWorkingSetVersion(workingSetVersionRequest);
				if (workingSetVersion.getState() != VersionState.FINAL) {
					canFinaliseFormVersion = false;
					break;
				}
			}
		}

		return canFinaliseFormVersion;
	}

	@Override
	@ValidateTicket
	public void finaliseFormVersion(FinaliseFormVersionRequest request) {
		LOGGER.log(Level.FINE, "Finalising form version with ID {0}",
				request.getFormVersionId());

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		// Form version is finalised and cannot be finalised.
		if (formVersion.getState() == State.FINAL) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be finalised, since it is already finalised.",
					formVersionId);
			throw new QInvalidOperationException("Form version is finalised.");
		}

		// Form version is locked by another user and cannot be finalised.
		if (formVersion.getLockedBy() != null
				&& !formVersion.getLockedBy().equals(
						request.getSignedTicket().getUserID())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be finalised, since it is locked by another user.",
					formVersionId);
			throw new QInvalidOperationException(
					"Form version is locked by other user.");
		}

		formVersion.setState(State.FINAL);

		formVersion.setLockedBy(null);
		formVersion.setLockedOn(null);

		publishEvent(request.getSignedTicket(), Constants.EVENT_FINALISE,
				formVersionId);

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.FINALISE.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);

	}

	@Override
	@ValidateTicket
	public void enableTestingForFormVersion(EnableTestingRequest request) {
		if (request.isEnableTesting()) {
			LOGGER.log(Level.FINE,
					"Enabling testing for form version with ID {0}",
					request.getFormVersionId());
		} else {
			LOGGER.log(Level.FINE,
					"Disabling testing for form version with ID {0}",
					request.getFormVersionId());
		}

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		// Form version is finalised and cannot be finalised.
		if (formVersion.getState() == State.FINAL) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be enabled for testing, since it is finalised.",
					formVersionId);
			throw new QInvalidOperationException("Form version is finalised.");
		}

		// Form version is locked by another user and cannot be finalised.
		if (formVersion.getLockedBy() != null
				&& !formVersion.getLockedBy().equals(
						request.getSignedTicket().getUserID())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be enabled for testing, since it is locked by another user.",
					formVersionId);
			throw new QInvalidOperationException(
					"Form version is locked by other user.");
		}

		EVENT auditEvent = null;
		if (request.isEnableTesting()) {
			formVersion.setState(State.TESTING);
			auditEvent = EVENT.ENABLE_TESTING;

			publishEvent(request.getSignedTicket(),
					Constants.EVENT_ENABLE_TESTING, formVersionId);
		} else {
			formVersion.setState(State.DRAFT);
			auditEvent = EVENT.DISABLE_TESTING;

			publishEvent(request.getSignedTicket(),
					Constants.EVENT_DISABLE_TESTING, formVersionId);
		}

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				auditEvent.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);

	}

	@Override
	@ValidateTicket
	public String importFormVersion(ImportFormVersionRequest request) {
		LOGGER.log(Level.FINE, "Importing new form version");

		String formVersionId = null;

		Form form = Form.find(em, request.getFormId());

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), form.getId(),
				form.getProjectId());

		try {
			ByteArrayInputStream reader = new ByteArrayInputStream(
					request.getContent());
			JAXBContext jaxbContext = JAXBContext
					.newInstance(XmlFormVersionDTO.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			XmlFormVersionDTO xmlFormVersionDTO = (XmlFormVersionDTO) jaxbUnmarshaller
					.unmarshal(reader);

			DateTime now = DateTime.now();
			long millis = now.getMillis();

			FormVersion formVersion = new FormVersion();
			formVersion.setName(xmlFormVersionDTO.getName());
			formVersion.setDescription(xmlFormVersionDTO.getDescription());
			formVersion.setContent(xmlFormVersionDTO.getContent());
			formVersion.setForm(form);
			formVersion.setState(State.DRAFT);
			formVersion.setCreatedBy(request.getSignedTicket().getUserID());
			formVersion.setCreatedOn(millis);
			formVersion
					.setLastModifiedBy(request.getSignedTicket().getUserID());
			formVersion.setLastModifiedOn(millis);

			em.persist(formVersion);

			formVersionId = formVersion.getId();

			if (xmlFormVersionDTO.getConditions() != null) {
				List<XmlConditionDTO> xmlConditionDTOs = xmlFormVersionDTO
						.getConditions().getConditions();

				List<ConditionDTO> conditionDTOs = converterUtil
						.xmlConditionDTOToConditionDTOList(xmlConditionDTOs,
								form.getProjectId(), request.getSignedTicket());

				// create conditions
				Map<String, ConditionDTO> mappedConditionDTOs = new HashMap<>();
				for (ConditionDTO conditionDTO : conditionDTOs) {
					mappedConditionDTOs.put(conditionDTO.getId(), conditionDTO);
				}

				// Validate conditions for cyclic dependencies. This validation
				// should be always performed, because invalid data will result
				// in infinite loop.
				ValidateFormVersionConditionsHierarchyRequest validateRequest = new ValidateFormVersionConditionsHierarchyRequest();
				validateRequest.setConditions(mappedConditionDTOs);
				validateRequest.setSignedTicket(request.getSignedTicket());
				validateFormVersionConditionsHierarchy(validateRequest);

				Map<String, Condition> conditions = createConditions(
						mappedConditionDTOs, formVersion);

				formVersion.setConditions(new ArrayList(conditions.values()));
			}

			// import translations
			if (xmlFormVersionDTO.getTranslations() != null
					&& xmlFormVersionDTO.getTranslations().getTranslations() != null
					&& form.getLocales() != null) {
				List<XmlTranslationDTO> xmlTranslationDTOs = xmlFormVersionDTO
						.getTranslations().getTranslations();

				createImportedTranslations(xmlTranslationDTOs, formVersionId,
						form.getLocales());
			}

			publishEvent(request.getSignedTicket(), Constants.EVENT_IMPORT,
					formVersion.getId());

			AuditFormVersionDTO auditFormVersionDTO = converterUtil
					.formVersionToAuditFormVersionDetailsDTO(formVersion);
			auditClientService.audit(LEVEL.QBE_FORMS.toString(),
					EVENT.IMPORT.toString(), GROUP.FORM_VERSION.toString(),
					null, request.getSignedTicket().getUserID(),
					auditFormVersionDTO);

		} catch (JAXBException e) {
			LOGGER.log(Level.SEVERE,
					"Could not unmarshal form version during import", e);
			throw new QImportExportException(
					"Could not unmarshal form version during import", e);
		}

		return formVersionId;
	}

	private void createImportedTranslations(
			List<XmlTranslationDTO> xmlTranslationDTOs, String formVersionId,
			List<String> locales) {

		Map<String, LanguageDTO> languages = new HashMap<>();

		for (String locale : locales) {
			LanguageDTO language = languageService.getLanguageByLocale(locale);
			languages.put(language.getLocale(), language);
		}

		String groupName = formVersionId;
		GroupDTO groupDTO = groupService.getGroupByName(groupName);
		String groupId = null;

		// If a group with this name does not exist create it.
		if (groupDTO == null) {
			groupDTO = new GroupDTO();
			groupDTO.setTitle(groupName);
			groupDTO.setDescription("Translations for form version");
			groupId = groupService.createGroup(groupDTO);

			for (XmlTranslationDTO xmlTranslationDTO : xmlTranslationDTOs) {
				String key = xmlTranslationDTO.getKey();

				XmlTranslationValuesDTO xmlTranslationValuesDTO = xmlTranslationDTO
						.getValues();
				List<XmlTranslationValueDTO> values = xmlTranslationValuesDTO
						.getValues();

				Map<String, String> translationValues = new HashMap<>();
				for (XmlTranslationValueDTO value : values) {
					LanguageDTO language = languages.get(value.getLang());
					if (language != null) {
						translationValues.put(language.getId(),
								value.getValue());
					} else {
						throw new QImportExportException(
								"Inconsistency in form and translation languages during import");
					}
				}

				for (LanguageDTO language : languages.values()) {
					if (!translationValues.containsKey(language.getId())) {
						throw new QImportExportException(
								"Inconsistency in form and translation languages during import");
					}
				}

				KeyDTO keyDTO = new KeyDTO();
				keyDTO.setGroupId(groupId);
				keyDTO.setName(key);
				keyDTO.setTranslations(translationValues);
				String keyId = keyService.createKey(keyDTO, false);
				keyDTO.setId(keyId);
			}

		}
	}

	@Override
	@ValidateTicket
	public byte[] exportFormVersion(ExportFormVersionRequest request) {
		LOGGER.log(Level.FINE, "Exporting form version with ID {0}",
				new String[] { request.getFormVersionId() });

		byte[] retVal = null;

		String formVersionId = request.getFormVersionId();

		FormVersion formVersion = FormVersion.find(em, formVersionId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), formVersion.getForm()
						.getId(), formVersion.getForm().getProjectId());

		// Form version is not finalised and cannot be exported.
		if (!State.FINAL.equals(formVersion.getState())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be exported, since it is not finalised.",
					formVersionId);
			throw new QInvalidOperationException(
					"Form version is not finalised.");
		}

		List<KeyDTO> keys = null;
		// Retrieve lexicon group based on the form version id which is by
		// convention the name of the group
		GroupDTO group = groupService.getGroupByName(formVersionId);

		if (group != null) {
			// Fetch keys
			KeySearchCriteriaBuilder builder = KeySearchCriteriaBuilder
					.createCriteria();
			builder.inGroup(group.getId());
			KeySearchCriteria criteria = builder.build();

			keys = keyService.findKeys(criteria, true);
		}

		XmlFormVersionDTO xmlFormVersionDTO = converterUtil
				.formVersionToXmlFormVersionDTO(formVersion, keys,
						request.getSignedTicket());

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			JAXBContext jaxbContext = JAXBContext
					.newInstance(XmlFormVersionDTO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(xmlFormVersionDTO, outputStream);

			retVal = outputStream.toByteArray();
		} catch (JAXBException e) {
			LOGGER.log(Level.SEVERE,
					"Could not marshal form version during export", e);
			throw new QImportExportException(
					"Could not marshal form version during export", e);
		}

		publishEvent(request.getSignedTicket(), Constants.EVENT_EXPORT,
				formVersion.getId());

		AuditFormVersionDTO auditFormVersionDTO = converterUtil
				.formVersionToAuditFormVersionDetailsDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.EXPORT.toString(), GROUP.FORM_VERSION.toString(), null,
				request.getSignedTicket().getUserID(), auditFormVersionDTO);

		return retVal;
	}

	@Override
	@ValidateTicket
	public List<Integer> getConditionTypes(EmptySignedRequest request) {
		LOGGER.log(Level.FINE,
				"Retrieving the list of available condition types");

		List<Integer> conditionTypes = new ArrayList<>();

		for (ConditionType type : ConditionType.values()) {
			conditionTypes.add(type.ordinal());
		}

		return conditionTypes;
	}

	private void publishEvent(SignedTicket signedTicket, String event,
			String formVersionId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_FORM_VERSION_ID, formVersionId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/be/forms/"
				+ Constants.RESOURCE_TYPE_FORM_VERSION + "/" + event);
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setKeyService(KeyService keyService) {
		this.keyService = keyService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setProjectExplorerService(ProjectService projectExplorerService) {
		this.projectExplorerService = projectExplorerService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}

	public void setXmlUtil(XmlUtil xmlUtil) {
		this.xmlUtil = xmlUtil;
	}

	public void setSecurityUtils(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}

	public void setEventPublisher(EventPublisherService eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setWorkingSetsServiceList(
			List<WorkingSetsService> workingSetsServiceList) {
		this.workingSetsServiceList = workingSetsServiceList;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
}
