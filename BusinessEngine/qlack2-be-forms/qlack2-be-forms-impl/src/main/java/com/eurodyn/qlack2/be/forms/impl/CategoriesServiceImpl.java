package com.eurodyn.qlack2.be.forms.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.forms.api.CategoriesService;
import com.eurodyn.qlack2.be.forms.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.forms.api.request.category.CountCategoryResourcesRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.GetCategoryIdByNameRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.forms.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.forms.impl.dto.AuditCategoryDTO;
import com.eurodyn.qlack2.be.forms.impl.model.Category;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.forms.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.forms.impl.util.Constants;
import com.eurodyn.qlack2.be.forms.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.forms.impl.util.SecureOperation;
import com.eurodyn.qlack2.be.forms.impl.util.SecurityUtils;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class CategoriesServiceImpl implements CategoriesService {
	private static final Logger LOGGER = Logger
			.getLogger(CategoriesServiceImpl.class.getName());

	private IDMService idmService;

	private ConverterUtil converterUtil;

	private SecurityUtils securityUtils;

	private AuditClientService auditClientService;

	private EventPublisherService eventPublisher;

	private EntityManager em;

	@Override
	@ValidateTicket
	public CategoryDTO getCategory(GetCategoryRequest request) {
		LOGGER.log(Level.FINE, "Getting category with ID {0}",
				request.getCategoryId());

		String categoryId = request.getCategoryId();

		Category category = Category.find(em, categoryId);
		CategoryDTO categoryDTO = converterUtil.categoryToCategoryDTO(category,
				request.getSignedTicket());

		AuditCategoryDTO auditCategoryDTO = converterUtil
				.categoryToAuditCategoryDTO(category);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(), EVENT.VIEW
				.toString(), GROUP.CATEGORY.toString(), null, request
				.getSignedTicket().getUserID(), auditCategoryDTO);

		return categoryDTO;
	}

	@Override
	@ValidateTicket
	public String getCategoryIdByName(GetCategoryIdByNameRequest request) {
		LOGGER.log(
				Level.FINE,
				"Getting category with name {0} for project with ID {1}",
				new String[] { request.getCategoryName(),
						request.getProjectId() });

		String categoryName = request.getCategoryName();
		String projectId = request.getProjectId();

		String categoryId = Category.getCategoryIdByName(em, categoryName,
				projectId);

		return categoryId;
	}

	@Override
	@ValidateTicket
	public Long countCategoryResources(CountCategoryResourcesRequest request) {
		LOGGER.log(
				Level.FINE,
				"Getting the number of forms that belong to category with ID {0}",
				request.getCategoryId());

		String categoryId = request.getCategoryId();

		Long count = Category.countResourcesByCategoryId(em, categoryId);

		return count;
	}

	@Override
	@ValidateTicket
	public String createCategory(CreateCategoryRequest request) {
		LOGGER.log(Level.FINE,
				"Creating new category with name {0} for project with ID {1}",
				new String[] { request.getName(), request.getProjectId() });

		securityUtils.checkCategoryOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_CATEGORY.toString(),
				request.getProjectId());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		Category category = new Category();
		category.setName(request.getName());
		category.setDescription(request.getDescription());
		category.setProjectId(request.getProjectId());
		category.setCreatedBy(request.getSignedTicket().getUserID());
		category.setCreatedOn(millis);
		category.setLastModifiedBy(request.getSignedTicket().getUserID());
		category.setLastModifiedOn(millis);

		em.persist(category);

		publishEvent(request.getSignedTicket(), Constants.EVENT_CREATE,
				category.getId());

		AuditCategoryDTO auditCategoryDTO = converterUtil
				.categoryToAuditCategoryDTO(category);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.CREATE.toString(), GROUP.CATEGORY.toString(), null,
				request.getSignedTicket().getUserID(), auditCategoryDTO);

		return category.getId();
	}

	@Override
	@ValidateTicket
	public void updateCategory(UpdateCategoryRequest request) {
		LOGGER.log(Level.FINE, "Updating category with ID {0}", request.getId());

		String categoryId = request.getId();

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		Category category = Category.find(em, categoryId);

		securityUtils.checkCategoryOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_CATEGORY.toString(),
				category.getProjectId());

		category.setName(request.getName());
		category.setDescription(request.getDescription());
		category.setLastModifiedBy(request.getSignedTicket().getUserID());
		category.setLastModifiedOn(millis);

		publishEvent(request.getSignedTicket(), Constants.EVENT_UPDATE,
				categoryId);

		AuditCategoryDTO auditCategoryDTO = converterUtil
				.categoryToAuditCategoryDTO(category);
		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.UPDATE.toString(), GROUP.CATEGORY.toString(), null,
				request.getSignedTicket().getUserID(), auditCategoryDTO);
	}

	@Override
	@ValidateTicket
	public void deleteCategory(DeleteCategoryRequest request) {
		LOGGER.log(Level.FINE, "Deleting category with ID {0}",
				request.getCategoryId());

		String categoryId = request.getCategoryId();

		Category category = Category.find(em, categoryId);

		securityUtils.checkCategoryOperation(request.getSignedTicket(),
				SecureOperation.FRM_MANAGE_CATEGORY.toString(),
				category.getProjectId());

		AuditCategoryDTO auditCategoryDTO = converterUtil
				.categoryToAuditCategoryDTO(category);

		em.remove(category);

		publishEvent(request.getSignedTicket(), Constants.EVENT_DELETE,
				categoryId);

		auditClientService.audit(LEVEL.QBE_FORMS.toString(),
				EVENT.DELETE.toString(), GROUP.CATEGORY.toString(), null,
				request.getSignedTicket().getUserID(), auditCategoryDTO);
	}

	private void publishEvent(SignedTicket signedTicket, String event,
			String categoryId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_CATEGORY_ID, categoryId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/be/forms/"
				+ Constants.RESOURCE_TYPE_CATEGORY + "/" + event);
	}

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
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
