package com.eurodyn.qlack2.be.forms.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.forms.api.FormVersionsService;
import com.eurodyn.qlack2.be.forms.api.FormsService;
import com.eurodyn.qlack2.be.forms.api.dto.FormDTO;
import com.eurodyn.qlack2.be.forms.api.dto.FormVersionDTO;
import com.eurodyn.qlack2.be.forms.api.request.form.CreateFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.DeleteFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.GetFormIdByNameRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.GetFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.UpdateFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.DeleteFormVersionTranslationsRequest;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditFormDTO;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditFormVersionDTO;
import com.eurodyn.qlack2.be.forms.impl.model.Category;
import com.eurodyn.qlack2.be.forms.impl.model.Form;
import com.eurodyn.qlack2.be.forms.impl.model.FormVersion;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.forms.impl.util.Constants;
import com.eurodyn.qlack2.be.forms.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.forms.impl.util.SecureOperation;
import com.eurodyn.qlack2.be.forms.impl.util.SecurityUtils;
import com.eurodyn.qlack2.fuse.auditing.api.AuditClientService;
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
import com.eurodyn.qlack2.webdesktop.api.request.security.CreateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.UpdateSecureResourceRequest;

public class FormsServiceImpl implements FormsService {
	private static final Logger LOGGER = Logger
			.getLogger(FormsServiceImpl.class.getName());

	private IDMService idmService;

	private SecurityService securityService;

	private FormVersionsService formVersionsService;

	private GroupService groupService;

	private KeyService keyService;

	private LanguageService languageService;

	private ConverterUtil converterUtil;

	private SecurityUtils securityUtils;

	private AuditClientService auditClientService;

	private EventPublisherService eventPublisher;

	private EntityManager em;

	@Override
	@ValidateTicket
	public FormDTO getForm(GetFormRequest request) {
		LOGGER.log(Level.FINE, "Getting form with ID {0}", request.getFormId());

		String formId = request.getFormId();

		Form form = Form.find(em, formId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), form.getId(),
				form.getProjectId());

		FormDTO formDTO = converterUtil.formToFormDTO(form,
				request.getSignedTicket());

		List<String> categories = converterUtil
				.categoriesToCategoryIdsList(form.getCategories());
		formDTO.setCategories(categories);

		List<FormVersionDTO> formVersionDTOs = converterUtil
				.formVersionsToFormVersionDTOList(form.getFormVersions());
		formDTO.setFormVersions(formVersionDTOs);

		AuditFormDTO auditFormDTO = converterUtil.formToAuditFormDTO(form);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.VIEW
				.toString(), GROUP.FORM.toString(), null, request
				.getSignedTicket().getUserID(), auditFormDTO);

		return formDTO;
	}

	@Override
	@ValidateTicket
	public String getFormIdByName(GetFormIdByNameRequest request) {
		LOGGER.log(Level.FINE,
				"Getting form with name {0} for project with ID {1}",
				new String[] { request.getFormName(), request.getProjectId() });

		String formName = request.getFormName();
		String projectId = request.getProjectId();

		String formId = Form.getFormIdByName(em, formName, projectId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), formId, projectId);

		return formId;
	}

	@Override
	@ValidateTicket
	public String createForm(CreateFormRequest request) {
		LOGGER.log(Level.FINE, "Creating new form with name {0} for project with ID {1}",
				new String[] {request.getName(), request.getProjectId()});

		securityUtils.checkCreateFormOperation(request.getSignedTicket(),
				request.getProjectId());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		Form form = new Form();
		form.setName(request.getName());
		form.setDescription(request.getDescription());
		form.setProjectId(request.getProjectId());
		form.setActive(request.isActive());
		form.setCreatedBy(request.getSignedTicket().getUserID());
		form.setCreatedOn(millis);
		form.setLastModifiedBy(request.getSignedTicket().getUserID());
		form.setLastModifiedOn(millis);
		form.setLocales(request.getLocales());

		List<Category> categories = new ArrayList<>();
		if (request.getCategories() != null) {
			for (String categoryId : request.getCategories()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		form.setCategories(categories);

		em.persist(form);

		publishEvent(request.getSignedTicket(), Constants.EVENT_CREATE, form.getId());

		// Create resource
		CreateSecureResourceRequest resourceRequest = new CreateSecureResourceRequest(
				form.getId(), form.getName(), "Form");
		securityService.createSecureResource(resourceRequest);

		AuditFormDTO auditFormDTO = converterUtil.formToAuditFormDTO(form);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.CREATE
				.toString(), GROUP.FORM.toString(), null, request
				.getSignedTicket().getUserID(), auditFormDTO);

		return form.getId();
	}

	@Override
	@ValidateTicket
	public void updateForm(UpdateFormRequest request) {
		LOGGER.log(Level.FINE, "Updating form with ID {0}", request.getId());

		String formId = request.getId();

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		Form form = Form.find(em, formId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), form.getId(),
				form.getProjectId());

		// Prepare lists of old and new locales
		List<String> oldLocales = new ArrayList<String>();
		if (form.getLocales() != null) {
			oldLocales.addAll(form.getLocales());
		}

		List<String> newLocales = new ArrayList<String>();
		if (request.getLocales() != null) {
			newLocales.addAll(request.getLocales());
			oldLocales.removeAll(request.getLocales());
		}

		if (form.getLocales() != null) {
			newLocales.removeAll(form.getLocales());
		}

		// Update form metadata
		form.setName(request.getName());
		form.setDescription(request.getDescription());
		form.setActive(request.isActive());
		form.setLastModifiedBy(request.getSignedTicket().getUserID());
		form.setLastModifiedOn(millis);
		form.setLocales(request.getLocales());

		List<Category> categories = new ArrayList<>();
		if (request.getCategories() != null) {
			for (String categoryId : request.getCategories()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		form.setCategories(categories);

		// Update resource
		UpdateSecureResourceRequest resourceRequest = new UpdateSecureResourceRequest(
				form.getId(), form.getName(), "Form");
		securityService.updateSecureResource(resourceRequest);

		// Update form version
		if (request.getVersionId() != null) {
			formVersionsService.updateFormVersion(request);
		}

		// If the form has versions then update the translations for old and new locales
		List<FormVersion> formVersions = form.getFormVersions();
		if (formVersions != null) {
			updateTranslations(formVersions, oldLocales, newLocales);
		}

		publishEvent(request.getSignedTicket(), Constants.EVENT_UPDATE, formId);

		AuditFormDTO auditFormDTO = converterUtil.formToAuditFormDTO(form);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.UPDATE
				.toString(), GROUP.FORM.toString(), null, request
				.getSignedTicket().getUserID(), auditFormDTO);

	}

	private void updateTranslations(List<FormVersion> formVersions,
			List<String> oldLocales, List<String> newLocales) {
		if (!oldLocales.isEmpty() || !newLocales.isEmpty()) {
			for (FormVersion formVersion : formVersions) {
				// Fetch lexicon group for form version
				GroupDTO groupDTO = groupService.getGroupByName(formVersion
						.getId());

				if (groupDTO != null) {
					String groupId = groupDTO.getId();

					// Delete translations for removed locales
					for (String oldLocale : oldLocales) {
						groupService.deleteLanguageTranslationsByLocale(
								groupId, oldLocale);
					}

					// Add default translations for added locales
					if (!newLocales.isEmpty()) {
						// Fetch existing keys
						KeySearchCriteriaBuilder builder = KeySearchCriteriaBuilder
								.createCriteria();
						builder.inGroup(groupId);
						KeySearchCriteria criteria = builder.build();

						List<KeyDTO> keys = keyService.findKeys(criteria, true);
						if (keys != null) {
							for (KeyDTO key : keys) {
								Map<String, String> translations = key
										.getTranslations();

								// Add default value for missing languages
								for (String newLocale : newLocales) {
									LanguageDTO language = languageService
											.getLanguageByLocale(newLocale);

									translations.put(language.getId(),
											key.getName());
								}

								keyService.updateTranslationsForKey(key.getId(),
										translations);

							}
						}
					}

					AuditFormVersionDTO auditFormVersionDTO = converterUtil
							.formVersionToAuditFormVersionDetailsDTO(formVersion);
					auditClientService.audit(LEVEL.QBE_FORMS.toString(),
							EVENT.UPDATE.toString(), GROUP.FORM_VERSION.toString(),
							null, "SYSTEM", auditFormVersionDTO);
				}
			}
		}
	}

	@Override
	@ValidateTicket
	public void deleteForm(DeleteFormRequest request) {
		LOGGER.log(Level.FINE, "Deleting form with ID {0}",
				request.getFormId());

		String formId = request.getFormId();

		Form form = Form.find(em, formId);

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), form.getId(),
				form.getProjectId());

		// Delete translations of all form versions
		for (FormVersion formVersion : form.getFormVersions()) {
			DeleteFormVersionTranslationsRequest deleteRequest = new DeleteFormVersionTranslationsRequest();
			deleteRequest.setFormVersionId(formVersion.getId());
			deleteRequest.setSignedTicket(request.getSignedTicket());

			formVersionsService.deleteFormVersionTranslations(deleteRequest);
		}

		AuditFormDTO auditFormDTO = converterUtil.formToAuditFormDTO(form);

		em.remove(form);

		// Delete resource from aaa
		DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(
				formId);
		securityService.deleteSecureResource(resourceRequest);

		publishEvent(request.getSignedTicket(), Constants.EVENT_DELETE, formId);

		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.DELETE
				.toString(), GROUP.FORM.toString(), null, request
				.getSignedTicket().getUserID(), auditFormDTO);

	}

	private void publishEvent(SignedTicket signedTicket, String event,
			String formId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_FORM_ID, formId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/be/forms/"
				+ Constants.RESOURCE_TYPE_FORM + "/" + event);
	}

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	public void setFormVersionsService(FormVersionsService formVersionsService) {
		this.formVersionsService = formVersionsService;
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

	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
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

	public void setEm(EntityManager em) {
		this.em = em;
	}
}
