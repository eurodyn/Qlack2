package com.eurodyn.qlack2.be.forms.impl;

import com.eurodyn.qlack2.be.forms.api.FormVersionsService;
import com.eurodyn.qlack2.be.forms.api.OrbeonService;
import com.eurodyn.qlack2.be.forms.api.dto.AttachmentDTO;
import com.eurodyn.qlack2.be.forms.api.dto.ContentDTO;
import com.eurodyn.qlack2.be.forms.api.dto.TranslationDTO;
import com.eurodyn.qlack2.be.forms.api.exception.QFormsRuntimeException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidFormVersionStateException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidOperationException;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidPreconditionsException;
import com.eurodyn.qlack2.be.forms.api.request.orbeon.*;
import com.eurodyn.qlack2.be.forms.api.request.version.GetFormVersionTranslationsRequest;
import com.eurodyn.qlack2.be.forms.client.api.rules.PreconditionFact;
import com.eurodyn.qlack2.be.forms.client.api.util.XmlFormProxy;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditOrbeonFormVersionDTO;
import com.eurodyn.qlack2.be.forms.impl.model.*;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.forms.impl.util.*;
import com.eurodyn.qlack2.be.rules.api.RulesRuntimeManagementService;
import com.eurodyn.qlack2.be.rules.api.request.runtime.StatelessMultiExecuteRequest;
import com.eurodyn.qlack2.be.rules.api.request.runtime.WorkingSetRuleVersionPair;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.fuse.lexicon.api.LanguageService;
import com.eurodyn.qlack2.fuse.lexicon.api.dto.LanguageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.DateTime;

import javax.persistence.EntityManager;
import javax.transaction.*;
import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrbeonServiceImpl implements OrbeonService {
	private static final Logger LOGGER = Logger
			.getLogger(OrbeonServiceImpl.class.getName());

	private IDMService idmService;

	private FormVersionsService formVersionsService;

	private LanguageService languageService;

	private ConverterUtil converterUtil;

	private SecurityUtils securityUtils;

	private EntityManager em;

	private TransactionManager transactionManager;

	private XmlUtil xmlUtil;

	private AuditClientService auditClientService;

	private EventPublisherService eventPublisher;

	private List<RulesRuntimeManagementService> rulesRuntimeManagementServiceList;

	@ValidateTicket
	@Override
	public String retrieveDocument(RetrieveDocumentRequest req) {
		LOGGER.log(Level.FINE, "Getting content of form version with ID {0}",
				req.getDocumentId());

		String documentId = req.getDocumentId();

		FormVersion formVersion = FormVersion.find(em, documentId);

		securityUtils.checkFormOperation(req.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), formVersion.getForm()
						.getId(), formVersion.getForm().getProjectId());

		AuditOrbeonFormVersionDTO auditOrbeonFormVersionDTO = converterUtil
				.formVersionToAuditOrbeonFormDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.VIEW
				.toString(), GROUP.ORBEON_FORM_VERSION_DEFINITION.toString(),
				null, req.getSignedTicket().getUserID(),
				auditOrbeonFormVersionDTO);

		return formVersion.getContent();
	}

	@ValidateTicket
	@Override
	public void storeDocument(StoreDocumentRequest req) {
		LOGGER.log(Level.FINE, "Saving content of form version with ID {0}",
				req.getDocumentId());

		String documentId = req.getDocumentId();
		String content = req.getContent();

		FormVersion formVersion = FormVersion.find(em, documentId);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		securityUtils.checkFormOperation(req.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		// Form version is finalised and cannot be edited.
		if (formVersion.getState() == State.FINAL) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be locked, since it is finalised.",
					documentId);
			throw new QInvalidOperationException("Form version is finalised.");
		}

		// Form version is locked by another user and cannot be edited.
		if (formVersion.getLockedBy() != null
				&& !formVersion.getLockedBy().equals(
						req.getSignedTicket().getUserID())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be locked, since it is locked by another user.",
					documentId);
			throw new QInvalidOperationException(
					"Form version is locked by other user.");
		}

		formVersion.setContent(content);
		formVersion.setLastModifiedBy(req.getSignedTicket().getUserID());
		formVersion.setLastModifiedOn(millis);

		publishEvent(req.getSignedTicket(),
				Constants.EVENT_UPDATE_ORBEON_DEFINITION, documentId);

		AuditOrbeonFormVersionDTO auditOrbeonFormVersionDTO = converterUtil
				.formVersionToAuditOrbeonFormDTO(formVersion);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.UPDATE
				.toString(), GROUP.ORBEON_FORM_VERSION_DEFINITION.toString(),
				null, req.getSignedTicket().getUserID(),
				auditOrbeonFormVersionDTO);
	}

	@ValidateTicket
	@Override
	public AttachmentDTO retrieveAttachment(RetrieveAttachmentRequest req) {
		LOGGER.log(Level.FINE,
				"Getting attachment of form version with ID {0}",
				req.getDocumentId());

		String documentId = req.getDocumentId();
		String attachmentName = req.getAttachmentName();

		FormVersion formVersion = FormVersion.find(em, documentId);

		securityUtils.checkFormOperation(req.getSignedTicket(),
				SecureOperation.FRM_VIEW_FORM.toString(), formVersion.getForm()
						.getId(), formVersion.getForm().getProjectId());

		Attachment attachment = Attachment.findByFormVersionIdAndFileName(em,
				documentId, attachmentName);
		AttachmentDTO attachmentDTO = converterUtil
				.attachmentToAttachmentDTO(attachment);

		// Auditing is not performed in this case, since it is handled by the
		// retrieveDocument method, which is also called.
		return attachmentDTO;
	}

	@ValidateTicket
	@Override
	public void storeAttachment(StoreAttachmentRequest req) {
		LOGGER.log(Level.FINE, "Saving attachment of form version with ID {0}",
				req.getDocumentId());

		String documentId = req.getDocumentId();
		byte[] content = req.getContent();
		String contentType = req.getContentType();
		String attachmentName = req.getAttachmentName();

		FormVersion formVersion = FormVersion.find(em, documentId);

		securityUtils.checkFormOperation(req.getSignedTicket(),
				SecureOperation.FRM_MANAGE_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		// Form version is finalised and cannot be edited.
		if (formVersion.getState() == State.FINAL) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be locked, since it is finalised.",
					documentId);
			throw new QInvalidOperationException("Form version is finalised.");
		}

		// Form version is locked by another user and cannot be edited.
		if (formVersion.getLockedBy() != null
				&& !formVersion.getLockedBy().equals(
						req.getSignedTicket().getUserID())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} cannot be locked, since it is locked by another user.",
					documentId);
			throw new QInvalidOperationException(
					"Form version is locked by other user.");
		}

		Attachment attachment = new Attachment();
		attachment.setFileName(attachmentName);
		attachment.setFileContent(content);
		attachment.setContentType(contentType);
		attachment.setFormVersion(formVersion);

		em.persist(attachment);

		// Auditing and event publishing are not performed in this case, since
		// they are handled by the
		// storeDocument method, which is also called.
	}

	@ValidateTicket
	@Override
	public String retrieveForm(RetrieveFormRequest req) {
		LOGGER.log(
				Level.FINE,
				"Getting form version content with translations and initial data for request with ID {0}",
				req.getFormVersionRequestId());

		String retVal = null;

		// Use the orbeon "form" as an identifier to load the initial request
		// information.
		// This information includes the form version id, the locale to which
		// the form should be translated and the initial data that should be
		// loaded to the form
		String formVersionRequestId = req.getFormVersionRequestId();
		FormVersionRequestInfo requestInfo = FormVersionRequestInfo.find(em,
				formVersionRequestId);

		if (requestInfo != null) {
			FormVersion formVersion = requestInfo.getFormVersion();

			securityUtils.checkFormOperation(req.getSignedTicket(),
					SecureOperation.FRM_VIEW_RENDERED_FORM.toString(),
					formVersion.getForm().getId(), formVersion.getForm()
							.getProjectId());

			if (State.DRAFT.equals(formVersion.getState())) {
				LOGGER.log(
						Level.SEVERE,
						"Form version {0} is not finalised or enabled for testing.",
						formVersion.getId());
				throw new QInvalidFormVersionStateException(
						"Form version is not finalised or enabled for testing");
			}

			String xmlContent = formVersion.getContent();

			String content = xmlUtil.loadInitialData(xmlContent,
					requestInfo.getData());

			// Fetch translations to perform replacement of the translation
			// placeholders with the actual
			// translation value.
			GetFormVersionTranslationsRequest translationsRequest = new GetFormVersionTranslationsRequest();
			translationsRequest.setFormVersionId(formVersion.getId());
			translationsRequest.setSignedTicket(req.getSignedTicket());

			List<TranslationDTO> translations = formVersionsService
					.getFormVersionTranslations(translationsRequest);

			if (translations != null && !translations.isEmpty()) {

				LanguageDTO language = languageService
						.getLanguageByLocale(requestInfo.getLocale());

				Map<String, String> tokens = new HashMap<String, String>();

				for (TranslationDTO translation : translations) {
					if (language != null
							&& language.getId().equals(
									translation.getLanguage())) {
						tokens.put(translation.getKey(), translation.getValue());
					}
				}

				// Create pattern of the format "%(cat|beverage)%"
				String patternString = "%("
						+ StringUtils.join(tokens.keySet(), "|") + ")%";
				Pattern pattern = Pattern.compile(patternString);
				Matcher matcher = pattern.matcher(content);

				StringBuffer sb = new StringBuffer();
				while (matcher.find()) {
					matcher.appendReplacement(sb, tokens.get(matcher.group(1)));
				}
				matcher.appendTail(sb);

				retVal = sb.toString();
			} else {
				retVal = content;
			}

			AuditOrbeonFormVersionDTO auditOrbeonFormVersionDTO = converterUtil
					.formVersionToAuditOrbeonFormDTO(formVersion);
			auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.VIEW
					.toString(), GROUP.ORBEON_FORM_VERSION.toString(), null,
					req.getSignedTicket().getUserID(),
					auditOrbeonFormVersionDTO);
		}
		return retVal;
	}

	/**
	 * Calls an orbeon URL to retrieve the orbeon content. It uses apache http
	 * client to make the request. An important thing is that orbeon adds the
	 * JSESSIONID to the response and this must be forwarded back to the
	 * external app. Otherwise the next call that the external app made to
	 * orbeon fails with an exception that the session-id has been expired
	 */
	// TODO check if localhost should be changed to something else when
	// clustering is put into place
	@ValidateTicket
	@Override
	public ContentDTO getOrbeonFormContent(GetOrbeonFormContentRequest request) {
		LOGGER.log(
				Level.FINE,
				"Getting form content of form version with ID {0} by requesting it from orbeon",
				request.getForm());

		FormVersion formVersion = FormVersion.find(em, request.getForm());

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_RENDERED_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		if (State.DRAFT.equals(formVersion.getState())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} is not finalised or enabled for testing.",
					formVersion.getId());
			throw new QInvalidFormVersionStateException(
					"Form version is not finalised or enabled for testing");
		}

		// Execute precondition rules to decide if the form should be displayed
		// or not
		boolean validPreconditions = executePreconditionRules(formVersion,
				request.getFacts(), request.getSignedTicket());

		if (!validPreconditions) {
			LOGGER.log(
					Level.SEVERE,
					"Preconditions are not met. Form version {0} cannot be displayed.",
					formVersion.getId());
			throw new QInvalidPreconditionsException(
					"Preconditions are not met. Form cannot be displayed.");
		}

		FormVersionRequestInfo formVersionRequestInfo = new FormVersionRequestInfo();
		formVersionRequestInfo.setFormVersion(formVersion);
		formVersionRequestInfo.setLocale(request.getLocale());
		formVersionRequestInfo.setData(request.getData());

		// Save temporarily the request information to the database. This is
		// required, since orbeon does not provide any way to pass extra data
		// when retrieving a form definition.
		// The information that is needed is the locale to which the form should
		// be translated to and the initial data that should be loaded to the
		// form.
		// The id of the saved request info is used as the "form" identifier and
		// the resulted orbeon URL has the following format
		// http://host:port/qbe/qbe-proxy/fr/<application_name>/<request_info_id>/new
		saveFormVersionRequestInfo(formVersionRequestInfo);

		String content = null;
		String cookieHeaderValue = null;

		// try {
		// Make an HTTP call to orbeon to retrieve the form
		// HttpClientConnectionManager connectionManager = new
		// BasicHttpClientConnectionManager();
		// SystemDefaultRoutePlanner routePlanner = new
		// SystemDefaultRoutePlanner(
		// ProxySelector.getDefault());
		CloseableHttpClient httpClient = HttpClients.custom().build();
		// .setConnectionManager(connectionManager)
		// .setRoutePlanner(routePlanner)

		InputStream instream = null;
		// Prepare a request object
		// Use the id of the request info object

		String appName = request.getApplication().replaceAll("\\s", "");

		String server = System.getProperty("server.configuration.ip");
		StringBuilder url = new StringBuilder("http://");

		if (server != null) {
			url.append(server);
		} else {
			url.append("127.0.0.1");
		}
		url.append(":8181/qbe/qbe-proxy/fr/").append(appName).append('/')
				.append(formVersionRequestInfo.getId()).append("/new");

		HttpGet httpGet = new HttpGet(url.toString());
		try {
			final ObjectMapper mapper = new ObjectMapper();

			//TODO The name of the header can't come from a constant, since each
			//application using WD may define its own header name. We need a
			//different way to obtain it. This is a temporary hack to be
			//re-evaluated and corrected. Most probably we need a global constant
			//on the level of QBE defining the name of this header for all
			//QBE apps.
//			httpGet.addHeader(
//					com.eurodyn.qlack2.webdesktop.api.util.Constants.QLACK_AUTH_HEADER_NAME,
//					mapper.writeValueAsString(request.getSignedTicket()));
			httpGet.addHeader("X-Qlack-Fuse-IDM-Token-ORBEON",
					mapper.writeValueAsString(request.getSignedTicket()));

			// Execute the request
			HttpResponse response = httpClient.execute(httpGet);

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();

			// If the response does not enclose an entity, there is no need
			// to worry about connection release
			if (entity != null) {
				instream = entity.getContent();

				content = IOUtils.toString(instream, "UTF-8");

				content = StringUtils.replace(content, "/orbeon/",
						"/qbe-proxy/");
			}

			// Extract Set-Cookie header from response to forward it to
			// client
			// application
			if (response.getFirstHeader("Set-Cookie") != null) {
				cookieHeaderValue = response.getFirstHeader("Set-Cookie")
						.getValue();
			}
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, MessageFormat.format(
					"Error occured while requesting form {0} from orbeon",
					formVersionRequestInfo.getId()), ex);

			// In case of an IOException the connection will be
			// released
			// back to the connection manager automatically
			throw new QFormsRuntimeException(ex);
		} catch (RuntimeException ex) {
			LOGGER.log(Level.SEVERE, MessageFormat.format(
					"Error occured while requesting form {0} from orbeon",
					formVersionRequestInfo.getId()), ex);

			// In case of an unexpected exception you may want to
			// abort
			// the HTTP request in order to shut down the underlying
			// connection and release it back to the connection
			// manager.
			httpGet.abort();
			throw new QFormsRuntimeException(ex);
		} finally {
			try {
				// Closing the input stream will trigger connection
				// release
				if (instream != null) {
					instream.close();
				}

				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpClient.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}

			// Delete the temporarily saved request information
			deleteFormVersionRequestInfo(formVersionRequestInfo.getId());
		}

		ContentDTO contentDTO = new ContentDTO();
		contentDTO.setContent(content);
		contentDTO.setCookieHeaderValue(cookieHeaderValue);

		return contentDTO;
	}

	@ValidateTicket
	@Override
	public List<byte[]> submitOrbeonFormContent(
			SubmitOrbeonFormContentRequest request) {
		LOGGER.log(Level.FINE, "Submiting data of form version with ID {0}",
				request.getForm());

		FormVersion formVersion = FormVersion.find(em, request.getForm());

		securityUtils.checkFormOperation(request.getSignedTicket(),
				SecureOperation.FRM_VIEW_RENDERED_FORM.toString(), formVersion
						.getForm().getId(), formVersion.getForm()
						.getProjectId());

		if (State.DRAFT.equals(formVersion.getState())) {
			LOGGER.log(
					Level.SEVERE,
					"Form version {0} is not finalised or enabled for testing.",
					formVersion.getId());
			throw new QInvalidFormVersionStateException(
					"Form version is not finalised or enabled for testing");
		}

		// Execute validation and default validation rules
		List<byte[]> results = executeValidationRules(formVersion,
				request.getValidationConditions(), request.getFacts(),
				request.getSignedTicket());

		if (results != null && !results.isEmpty()) {
			byte[] xmlFormProxyBytes = results.get(0);
			Object object = deserializeObject(xmlFormProxyBytes);

			XmlFormProxy xmlFormProxy = (XmlFormProxy) object;

			if (xmlFormProxy.isValid()) {
				// All validation conditions are met, post conditions can be
				// executed
				results = executePostconditionRules(formVersion, results,
						request.getSignedTicket());
			}
		} else {
			results = executePostconditionRules(formVersion, results,
					request.getSignedTicket());
		}

		return results;
	}

	private boolean executePreconditionRules(FormVersion formVersion,
			List<byte[]> facts, SignedTicket signedTicket) {
		LOGGER.log(Level.FINE,
				"Executing preconditions of form version with ID {0}",
				formVersion.getId());

		boolean retVal = true;

		List<Condition> conditions = Condition.getConditionsWithoutParent(em,
				formVersion.getId(), ConditionType.PRECONDITION);

		List<Condition> preconditions = new ArrayList<>();
		sortConditions(conditions, preconditions);

		if (preconditions != null && !preconditions.isEmpty()) {
			List<byte[]> results = executeRules(preconditions, facts,
					signedTicket);

			if (results != null && !results.isEmpty()) {
				byte[] preconditionFactBytes = results.get(0);
				Object object = deserializeObject(preconditionFactBytes);

				PreconditionFact preconditionFact = (PreconditionFact) object;

				retVal = preconditionFact.isValid();
			} else {
				LOGGER.log(
						Level.SEVERE,
						"Output facts cannot be null or empty when executing preconditions of form version {0}.",
						formVersion.getId());
				throw new QFormsRuntimeException(
						"Output facts cannot be null or empty when executing preconditions");
			}

		}
		return retVal;
	}

	private List<byte[]> executeValidationRules(FormVersion formVersion,
			List<String> validationConditionNames, List<byte[]> facts,
			SignedTicket signedTicket) {
		LOGGER.log(Level.FINE,
				"Executing validation conditions of form version with ID {0}",
				formVersion.getId());

		List<byte[]> results = null;

		List<Condition> conditions = getDefaultValidationConditions(formVersion);
		conditions.addAll(getValidationConditions(formVersion,
				validationConditionNames));

		if (conditions != null && !conditions.isEmpty()) {
			results = executeRules(conditions, facts, signedTicket);

			if (results == null || results.isEmpty()) {
				LOGGER.log(
						Level.SEVERE,
						"Output facts cannot be null or empty when executing validation conditions of form version {0}.",
						formVersion.getId());
				throw new QFormsRuntimeException(
						"Output facts cannot be null or empty when executing validation conditions");
			}
		}
		return results;
	}

	private List<byte[]> executePostconditionRules(FormVersion formVersion,
			List<byte[]> facts, SignedTicket signedTicket) {
		LOGGER.log(Level.FINE,
				"Executing postconditions of form version with ID {0}",
				formVersion.getId());

		List<byte[]> results = null;

		List<Condition> conditions = Condition.getConditionsWithoutParent(em,
				formVersion.getId(), ConditionType.POSTCONDITION);

		List<Condition> postconditions = new ArrayList<>();
		sortConditions(conditions, postconditions);

		if (postconditions != null && !postconditions.isEmpty()) {
			results = executeRules(postconditions, facts, signedTicket);

			if (results == null || results.isEmpty()) {
				LOGGER.log(
						Level.SEVERE,
						"Output facts cannot be null or empty when executing postconditions of form version {0}.",
						formVersion.getId());
				throw new QFormsRuntimeException(
						"Output facts cannot be null or empty when executing postconditions");
			}

		}
		return results;
	}

	private List<byte[]> executeRules(List<Condition> conditions,
			List<byte[]> facts, SignedTicket signedTicket) {
		if (rulesRuntimeManagementServiceList.size() == 0) {
			LOGGER.log(Level.SEVERE,
					"RulesRuntimeManagementService not available");
			throw new QFormsRuntimeException(
					"RulesRuntimeManagementService not available");
		}

		List<WorkingSetRuleVersionPair> pairs = new ArrayList<>();
		for (Condition condition : conditions) {
			WorkingSetRuleVersionPair pair = new WorkingSetRuleVersionPair();
			pair.setWorkingSetVersionId(condition.getWorkingSetId());
			pair.setRuleVersionId(condition.getRuleId());
			pairs.add(pair);
		}

		StatelessMultiExecuteRequest statelessMultiExecuteRequest = new StatelessMultiExecuteRequest();
		statelessMultiExecuteRequest.setPairs(pairs);
		statelessMultiExecuteRequest.setFacts(facts);
		statelessMultiExecuteRequest.setSignedTicket(signedTicket);

		return rulesRuntimeManagementServiceList.get(0)
				.statelessMultiExecute(statelessMultiExecuteRequest).getFacts();
	}

	private List<Condition> getDefaultValidationConditions(
			FormVersion formVersion) {
		List<Condition> conditions = Condition.getConditionsWithoutParent(em,
				formVersion.getId(), ConditionType.DEFAULT_VALIDATION);

		List<Condition> defaultValidationConditions = new ArrayList<>();
		sortConditions(conditions, defaultValidationConditions);
		return defaultValidationConditions;
	}

	private List<Condition> getValidationConditions(FormVersion formVersion,
			List<String> validationConditionNames) {
		List<Condition> validationConditions = new ArrayList<>();

		if (validationConditionNames != null
				&& !validationConditionNames.isEmpty()) {
			List<Condition> conditions = Condition
					.getFilteredConditionsWithoutParent(em,
							formVersion.getId(), ConditionType.VALIDATION,
							validationConditionNames);

			sortConditions(conditions, validationConditions);
		}
		return validationConditions;
	}

	private void sortConditions(List<Condition> conditions,
			List<Condition> sortedConditions) {
		if (conditions != null) {
			for (Condition condition : conditions) {
				sortedConditions.add(condition);

				if (!condition.getChildren().isEmpty()) {
					sortConditions(condition.getChildren(), sortedConditions);
				}
			}
		}
	}

	private void saveFormVersionRequestInfo(
			FormVersionRequestInfo formVersionRequestInfo) {
		try {
			transactionManager.begin();
			em.joinTransaction();
			em.persist(formVersionRequestInfo);
			transactionManager.commit();
		} catch (SystemException | IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException | NotSupportedException ex) {
			LOGGER.log(Level.SEVERE, "Error saving request info", ex);
			try {
				transactionManager.rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e) {
				LOGGER.log(Level.SEVERE,
						"Error rolling back orbeon service transaction", e);
			}

			throw new QFormsRuntimeException(ex);
		}
	}

	private void deleteFormVersionRequestInfo(String formVersionRequestInfoId) {
		try {
			transactionManager.begin();
			em.joinTransaction();
			FormVersionRequestInfo persistedFormVersionRequestInfo = FormVersionRequestInfo
					.find(em, formVersionRequestInfoId);
			em.remove(persistedFormVersionRequestInfo);
			transactionManager.commit();
		} catch (SystemException | IllegalStateException | SecurityException
				| HeuristicMixedException | HeuristicRollbackException
				| RollbackException | NotSupportedException ex) {
			LOGGER.log(Level.SEVERE, "Error saving request info", ex);
			try {
				transactionManager.rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e) {
				LOGGER.log(Level.SEVERE,
						"Error rolling back orbeon service transaction", e);
			}
			throw new QFormsRuntimeException(ex);
		}
	}

	private byte[] serializeObject(Object object) {
		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);

			oos.writeObject(object);

			byte[] bytes = baos.toByteArray();

			return bytes;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error while serializing input fact", e);
			throw new QFormsRuntimeException(e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	private Object deserializeObject(byte[] bytes) {
		ObjectInputStream ois = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);

			Object object = ois.readObject();

			return object;
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Error while deserializing output fact", e);
			throw new QFormsRuntimeException(e);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
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

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setFormVersionsService(FormVersionsService formVersionsService) {
		this.formVersionsService = formVersionsService;
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

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setXmlUtil(XmlUtil xmlUtil) {
		this.xmlUtil = xmlUtil;
	}

	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}

	public void setEventPublisher(EventPublisherService eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setRulesRuntimeManagementServiceList(
			List<RulesRuntimeManagementService> rulesRuntimeManagementServiceList) {
		this.rulesRuntimeManagementServiceList = rulesRuntimeManagementServiceList;
	}

}
