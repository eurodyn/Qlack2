package com.eurodyn.qlack2.be.rules.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.rules.api.CategoriesService;
import com.eurodyn.qlack2.be.rules.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteCategoryResult;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.DeleteCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.GetCategoryByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.GetCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectCategoriesRequest;
import com.eurodyn.qlack2.be.rules.api.util.Constants;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditCategoryDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.SecurityUtils;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class CategoriesServiceImpl implements CategoriesService {
	private static final Logger LOGGER = Logger.getLogger(CategoriesServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private IDMService idmService;

	private AuditClientService audit;

	private EventPublisherService eventPublisher;

	private EntityManager em;

	private ConverterUtil mapper;

	private AuditConverterUtil auditMapper;

	private SecurityUtils securityUtils;

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setAudit(AuditClientService audit) {
		this.audit = audit;
	}

	public void setEventPublisher(EventPublisherService eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setMapper(ConverterUtil mapper) {
		this.mapper = mapper;
	}

	public void setAuditMapper(AuditConverterUtil auditMapper) {
		this.auditMapper = auditMapper;
	}

	public void setSecurityUtils(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	@ValidateTicket
	@Override
	public List<CategoryDTO> getCategories(GetProjectCategoriesRequest request) {
		SignedTicket ticket = request.getSignedTicket();
		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Get categories for project {0}.", projectId);

		List<Category> categories = Category.findByProjectId(em, projectId);

		List<CategoryDTO> categoryDtos = mapper.mapCategoryList(categories, ticket);

		return categoryDtos;
	}

	@ValidateTicket
	@Override
	public CategoryDTO getCategory(GetCategoryRequest request) {
		SignedTicket ticket = request.getSignedTicket();
		String categoryId = request.getId();

		LOGGER.log(Level.FINE, "Get category {0}.", categoryId);

		Category category = Category.findById(em, categoryId);
		if (category == null) {
			return null;
		}

		CategoryDTO categoryDTO = mapper.mapCategory(category, ticket);

		AuditCategoryDTO auditDto = auditMapper.mapCategory(category);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.CATEGORY.toString(),
					null, ticket.getUserID(), auditDto);

		return categoryDTO;
	}

	@ValidateTicket
	@Override
	public CategoryDTO getCategoryByProjectAndName(GetCategoryByProjectAndNameRequest request) {
		SignedTicket ticket = request.getSignedTicket();
		String projectId = request.getProjectId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get category by project {0} and name {1}.", new Object[]{projectId, name});

		Category category = Category.findByProjectAndName(em, projectId, name);
		if (category == null) {
			return null;
		}

		CategoryDTO categoryDTO = mapper.mapCategory(category, ticket);

		return categoryDTO;
	}

	@ValidateTicket
	@Override
	public String createCategory(CreateCategoryRequest request) {
		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Create category in project {0}.", projectId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanManageCategory(ticket, projectId);

		Category category = new Category();
		String categoryId = category.getId();

		category.setProjectId(projectId);
		category.setName(request.getName());
		category.setDescription(request.getDescription());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		category.setCreatedBy(ticket.getUserID());
		category.setCreatedOn(millis);
		category.setLastModifiedBy(ticket.getUserID());
		category.setLastModifiedOn(millis);

		em.persist(category);

		publishEvent(ticket, Constants.EVENT_CREATE, categoryId);

		AuditCategoryDTO auditDto = auditMapper.mapCategory(category);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.CATEGORY.toString(),
					null, ticket.getUserID(), auditDto);

		return categoryId;
	}

	@ValidateTicket
	@Override
	public void updateCategory(UpdateCategoryRequest request) {
		String categoryId = request.getId();

		LOGGER.log(Level.FINE, "Update category {0}.", categoryId);

		Category category = Category.findById(em, categoryId);
		if (category == null) {
			return;
		}

		String projectId = category.getProjectId();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanManageCategory(ticket, projectId);

		category.setName(request.getName());
		category.setDescription(request.getDescription());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		category.setLastModifiedBy(ticket.getUserID());
		category.setLastModifiedOn(millis);

		publishEvent(ticket, Constants.EVENT_UPDATE, categoryId);

		AuditCategoryDTO auditDto = auditMapper.mapCategory(category);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.CATEGORY.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public CanDeleteCategoryResult canDeleteCategory(DeleteCategoryRequest request) {
		String categoryId = request.getId();
		SignedTicket ticket = request.getSignedTicket();

		LOGGER.log(Level.FINE, "Check if can delete category {0}.", categoryId);

		Category category = Category.findById(em, categoryId);
		if (category == null) {
			return null;
		}

		Long countWorkingSets = Category.countWorkingSetsByCategoryId(em, categoryId);
		Long countRules = Category.countRulesByCategoryId(em, categoryId);
		Long countDataModels = Category.countDataModelsByCategoryId(em, categoryId);
		Long countLibraries = Category.countLibrariesByCategoryId(em, categoryId);

		AuditCategoryDTO auditDto = auditMapper.mapCategory(category);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.CATEGORY.toString(),
					null, ticket.getUserID(), auditDto);

		Long count = countWorkingSets + countRules + countDataModels + countLibraries;
		if (count > 0) {
			CanDeleteCategoryResult result = new CanDeleteCategoryResult();
			result.setResult(false);

			result.setAssignedToResources(true);
			return result;
		}

		CanDeleteCategoryResult result = new CanDeleteCategoryResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteCategory(DeleteCategoryRequest request) {
		String categoryId = request.getId();

		LOGGER.log(Level.FINE, "Delete category {0}.", categoryId);

		Category category = Category.findById(em, categoryId);
		if (category == null) {
			return;
		}

		String projectId = category.getProjectId();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanManageCategory(ticket, projectId);

		Long countWorkingSets = Category.countWorkingSetsByCategoryId(em, categoryId);
		Long countRules = Category.countRulesByCategoryId(em, categoryId);
		Long countDataModels = Category.countDataModelsByCategoryId(em, categoryId);
		Long countLibraries = Category.countLibrariesByCategoryId(em, categoryId);

		Long count = countWorkingSets + countRules + countDataModels + countLibraries;
		if (count > 0) {
			throw new QInvalidActionException("You are not allowed to delete this category, as it is assigned to resources.");
		}

		AuditCategoryDTO auditDto = auditMapper.mapCategory(category);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.CATEGORY.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(category);

		publishEvent(ticket, Constants.EVENT_DELETE, categoryId);
	}

	private void publishEvent(SignedTicket ticket, String event, String categoryId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_CATEGORY_ID, categoryId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_CATEGORY + "/" + event);
	}

}
