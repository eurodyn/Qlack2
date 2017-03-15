package com.eurodyn.qlack2.be.rules.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.rules.api.LibraryService;
import com.eurodyn.qlack2.be.rules.api.LibraryVersionService;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteLibraryResult;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.library.CreateLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.DeleteLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.GetLibraryByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.GetLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.UpdateLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectLibrariesRequest;
import com.eurodyn.qlack2.be.rules.api.util.Constants;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditLibraryDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
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
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.CreateSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.DeleteSecureResourceRequest;
import com.eurodyn.qlack2.webdesktop.api.request.security.UpdateSecureResourceRequest;

public class LibraryServiceImpl implements LibraryService {
	private static final Logger LOGGER = Logger.getLogger(LibraryServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private IDMService idmService;

	private SecurityService securityService;

	private AuditClientService audit;

	private EventPublisherService eventPublisher;

	private EntityManager em;

	private LibraryVersionService libraryVersionService;

	private ConverterUtil mapper;

	private AuditConverterUtil auditMapper;

	private SecurityUtils securityUtils;

	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
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

	public void setLibraryVersionService(LibraryVersionService libraryVersionService) {
		this.libraryVersionService = libraryVersionService;
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
	public List<LibraryDTO> getLibraries(GetProjectLibrariesRequest request) {
		String projectId = request.getProjectId();
		boolean filterEmpty = request.isFilterEmpty();

		LOGGER.log(Level.FINE, "Get libraries for project {0}.", projectId);

		List<Library> libraries = Library.findByProjectId(em, projectId);

		List<LibraryDTO> libraryDtos = new ArrayList<>();
		for (Library library : libraries) {
			if (!filterEmpty || !library.getVersions().isEmpty()) {
				// do not check security, summary is always viewable
				libraryDtos.add(mapper.mapLibrarySummary(library));
			}
		}

		return libraryDtos;
	}

	@ValidateTicket
	@Override
	public LibraryDTO getLibrary(GetLibraryRequest request) {
		String libraryId = request.getLibraryId();

		LOGGER.log(Level.FINE, "Get library {0}.", libraryId);

		Library library = Library.findById(em, libraryId);
		if (library == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewLibrary(ticket, library);

		LibraryDTO libraryDto = mapper.mapLibrary(library, ticket);

		List<LibraryVersion> versions = LibraryVersion.findByLibraryId(em, libraryId);
		libraryDto.setVersions(mapper.mapLibraryVersionSummaryList(versions, ticket));

		AuditLibraryDTO auditDto = auditMapper.mapLibrary(library);
		auditDto.setVersions(auditMapper.mapLibraryVersionList(versions));
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.LIBRARY.toString(),
					null, ticket.getUserID(), auditDto);

		return libraryDto;
	}

	@ValidateTicket
	@Override
	public LibraryDTO getLibraryByProjectAndName(GetLibraryByProjectAndNameRequest request) {
		String projectId = request.getProjectId();
		String name = request.getName();

		LOGGER.log(Level.FINE, "Get library by project {0} and name {1}.", new Object[]{projectId, name});

		Library library = Library.findByProjectAndName(em, projectId, name);
		if (library == null) {
			return null;
		}

		SignedTicket ticket = request.getSignedTicket();
		LibraryDTO libraryDto = mapper.mapLibrary(library, ticket);

		return libraryDto;
	}

	@ValidateTicket
	@Override
	public String createLibrary(CreateLibraryRequest request) {
		String projectId = request.getProjectId();

		LOGGER.log(Level.FINE, "Create library in project {0}.", projectId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanCreateLibrary(ticket, projectId);

		Library library = new Library();
		String libraryId = library.getId();
		library.setProjectId(projectId);

		library.setName(request.getName());
		library.setDescription(request.getDescription());
		library.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		library.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		library.setCreatedBy(ticket.getUserID());
		library.setCreatedOn(millis);
		library.setLastModifiedBy(ticket.getUserID());
		library.setLastModifiedOn(millis);

		em.persist(library);

		// Create resource
		CreateSecureResourceRequest resourceRequest = new CreateSecureResourceRequest(libraryId, library.getName(), "Library");
		securityService.createSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_CREATE, libraryId);

		AuditLibraryDTO auditDto = auditMapper.mapLibrary(library);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.LIBRARY.toString(),
					null, ticket.getUserID(), auditDto);

		return libraryId;
	}

	@ValidateTicket
	@Override
	public void updateLibrary(UpdateLibraryRequest request) {
		String libraryId = request.getId();

		LOGGER.log(Level.FINE, "Update library {0}.", libraryId);

		Library library = Library.findById(em, libraryId);
		if (library == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		library.setName(request.getName());
		library.setDescription(request.getDescription());
		library.setActive(request.isActive());

		List<Category> categories = new ArrayList<>();
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				Category category = em.getReference(Category.class, categoryId);
				categories.add(category);
			}
		}
		library.setCategories(categories);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		library.setLastModifiedBy(ticket.getUserID());
		library.setLastModifiedOn(millis);

		UpdateSecureResourceRequest resourceRequest = new UpdateSecureResourceRequest(libraryId, library.getName(), "Library");
		securityService.updateSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_UPDATE, libraryId);

		AuditLibraryDTO auditDto = auditMapper.mapLibrary(library);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.LIBRARY.toString(),
					null, ticket.getUserID(), auditDto);

		if (request.getVersionRequest() != null) {
			libraryVersionService.updateLibraryVersion(request);
		}
	}

	@ValidateTicket
	@Override
	public CanDeleteLibraryResult canDeleteLibrary(DeleteLibraryRequest request) {
		String libraryId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete library {0}.", libraryId);

		Library library = Library.findById(em, libraryId);

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewLibrary(ticket, library);

		AuditLibraryDTO auditDto = auditMapper.mapLibrary(library);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.LIBRARY.toString(),
					null, ticket.getUserID(), auditDto);

		long countWorkingSets = WorkingSetVersion.countContainingLibrary(em, libraryId);
		if (countWorkingSets > 0) {
			CanDeleteLibraryResult result = new CanDeleteLibraryResult();
			result.setResult(false);

			result.setContainedInWorkingSet(true);
			return result;
		}

		long countLockedByOther = LibraryVersion.countLibraryVersionsLockedByOtherUser(em, libraryId, ticket.getUserID());
		if (countLockedByOther > 0) {
			CanDeleteLibraryResult result = new CanDeleteLibraryResult();
			result.setResult(false);

			result.setLockedByOtherUser(true);
			return result;
		}

		CanDeleteLibraryResult result = new CanDeleteLibraryResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteLibrary(DeleteLibraryRequest request) {
		String libraryId = request.getId();

		LOGGER.log(Level.FINE, "Delete library {0}.", libraryId);

		Library library = Library.findById(em, libraryId);
		if (library == null) {
			return;
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		long countWorkingSets = WorkingSetVersion.countContainingLibrary(em, libraryId);
		if (countWorkingSets > 0) {
			throw new QInvalidActionException("This library has versions contained in working sets.");
		}

		long countLockedByOther = LibraryVersion.countLibraryVersionsLockedByOtherUser(em, libraryId, ticket.getUserID());
		if (countLockedByOther > 0) {
			throw new QInvalidActionException("This library has versions locked by other users.");
		}

		AuditLibraryDTO auditDto = auditMapper.mapLibrary(library);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.LIBRARY.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(library);

		DeleteSecureResourceRequest resourceRequest = new DeleteSecureResourceRequest(libraryId);
		securityService.deleteSecureResource(resourceRequest);

		publishEvent(ticket, Constants.EVENT_DELETE, libraryId);
	}

	private void publishEvent(SignedTicket ticket, String event, String libraryId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_LIBRARY_ID, libraryId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_LIBRARY + "/" + event);
	}

}
