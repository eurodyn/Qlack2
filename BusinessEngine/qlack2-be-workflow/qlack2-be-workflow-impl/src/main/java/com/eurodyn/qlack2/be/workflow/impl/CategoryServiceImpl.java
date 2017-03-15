package com.eurodyn.qlack2.be.workflow.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.workflow.api.CategoryService;
import com.eurodyn.qlack2.be.workflow.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.workflow.api.request.category.CountCategoryResourcesRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoryIdByNameRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.workflow.api.util.Constants;
import com.eurodyn.qlack2.be.workflow.impl.dto.AuditCategoryDTO;
import com.eurodyn.qlack2.be.workflow.impl.model.Category;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.workflow.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.workflow.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.SecurityUtil;
import com.eurodyn.qlack2.be.workflow.impl.util.WorkflowConstants;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class CategoryServiceImpl implements CategoryService {
	
	private static final Logger LOGGER = Logger.getLogger(CategoryServiceImpl.class.getName());
	private EntityManager em;
	private IDMService idmService;
	private SecurityUtil securityUtil;
	private ConverterUtil converterUtil;
	private AuditClientService auditClientService;
	private EventPublisherService eventPublisher;
	
	@Override
	@ValidateTicket
	public CategoryDTO getCategory(GetCategoryRequest req) {
		LOGGER.log(Level.FINE, "Retrieving list of categories.");
		
		String categoryId = req.getCategoryId();
		Category category = Category.find(em, categoryId);
		
		AuditCategoryDTO auditCategoryDTO = converterUtil.categoryToAuditCategoryDTO(category);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.VIEW.toString(), GROUP.CATEGORY.toString(),
				null, req.getSignedTicket().getUserID(), auditCategoryDTO);

		return converterUtil.categoryToCategoryDTO(category, true);
	}

	@Override
	@ValidateTicket
	public String getCategoryIdByName(GetCategoryIdByNameRequest req) {
		LOGGER.log(Level.FINE, "Getting category ID by name {0} ", req.getCategoryName());
		
		String categoryName = req.getCategoryName();
		String projectId = req.getProjectId();

		return Category.getCategoryIdByName(em, categoryName, projectId);
	}

	@Override
	@ValidateTicket
	public Long countCategoryResources(CountCategoryResourcesRequest req) {
		LOGGER.log(Level.FINE, "Getting category resources by ID {0} ", req.getCategoryId());
		
		String categoryId = req.getCategoryId();
		
		return Category.countResourcesByCategoryId(em, categoryId);
	}

	@Override
	@ValidateTicket
	public String createCategory(CreateCategoryRequest req) {
		LOGGER.log(Level.FINE, "Creating new category with name {0} ", req.getName());
		
		securityUtil.checkCategoryOperation(req.getSignedTicket(), WorkflowConstants.OP_WFL_MANAGE_CATEGORY,
				req.getProjectId());
		DateTime now = DateTime.now();
		long millis = now.getMillis();
	
		Category category = new Category();
		category.setName(req.getName());
		category.setDescription(req.getDescription());
		category.setProjectId(req.getProjectId());
		category.setCreatedBy(req.getSignedTicket().getUserID());
		category.setCreatedOn(millis);
		category.setLastModifiedBy(req.getSignedTicket().getUserID());
		category.setLastModifiedOn(millis);
	
		em.persist(category);
		
		publishEvent(req.getSignedTicket(), Constants.EVENT_CREATE, category.getId());
		
		AuditCategoryDTO auditCategoryDTO = converterUtil.categoryToAuditCategoryDTO(category);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.CREATE.toString(), GROUP.CATEGORY.toString(),
				null, req.getSignedTicket().getUserID(), auditCategoryDTO);
			
		return category.getId();
	}

	@Override
	@ValidateTicket
	public void updateCategory(UpdateCategoryRequest req) {
		LOGGER.log(Level.FINE, "Updating category with name {0} ", req.getName());
		Category category = Category.find(em, req.getId());
		
		securityUtil.checkCategoryOperation(req.getSignedTicket(), WorkflowConstants.OP_WFL_MANAGE_CATEGORY,
				category.getProjectId());
		
		DateTime now = DateTime.now();
		long millis = now.getMillis();
		category.setName(req.getName());
		category.setDescription(req.getDescription());
		category.setLastModifiedBy(req.getSignedTicket().getUserID());
		category.setLastModifiedOn(millis);
		
		publishEvent(req.getSignedTicket(), Constants.EVENT_UPDATE, category.getId());
		
		AuditCategoryDTO auditCategoryDTO = converterUtil.categoryToAuditCategoryDTO(category);
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.UPDATE.toString(), GROUP.CATEGORY.toString(),
				null, req.getSignedTicket().getUserID(), auditCategoryDTO);
	}

	@Override
	@ValidateTicket
	public void deleteCategory(DeleteCategoryRequest req) {
		LOGGER.log(Level.FINE, "Deleting category with ID {0} ", req.getCategoryId());
		Category category = Category.find(em, req.getCategoryId());
		
		securityUtil.checkCategoryOperation(req.getSignedTicket(), WorkflowConstants.OP_WFL_MANAGE_CATEGORY,
			category.getProjectId());
		
		AuditCategoryDTO auditCategoryDTO = converterUtil.categoryToAuditCategoryDTO(category);
		em.remove(category);
		
		publishEvent(req.getSignedTicket(), Constants.EVENT_DELETE, req.getCategoryId());
		
		auditClientService.audit(LEVEL.QBE_WORKFLOW.toString(), EVENT.DELETE.toString(), GROUP.CATEGORY.toString(),
				null, req.getSignedTicket().getUserID(), auditCategoryDTO);
	}
	
	private void publishEvent(SignedTicket signedTicket, String event, String categoryId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", signedTicket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_CATEGORY_ID, categoryId);

		eventPublisher.publishSync(message, "com/eurodyn/qlack2/be/workflow/"
				+ Constants.RESOURCE_TYPE_CATEGORY + "/" + event);
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}
	
	public void setSecurityUtil(SecurityUtil securityUtil) {
		this.securityUtil = securityUtil;
	}
	
	public void setConverterUtil(ConverterUtil converterUtil) {
		this.converterUtil = converterUtil;
	}
	
	public void setAuditClientService(AuditClientService auditClientService) {
		this.auditClientService = auditClientService;
	}
	
	public void setEventPublisher(EventPublisherService eventPublisher) {
		this.eventPublisher = eventPublisher;
	}
}
