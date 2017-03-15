package com.eurodyn.qlack2.be.rules.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.rules.api.LibraryVersionService;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteLibraryVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingLibraryResult;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlLibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.library.CountLibraryVersionsLockedByOtherUserRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.UpdateLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.CreateLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.DeleteLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.EnableTestingLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.ExportLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.FinaliseLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.GetLibraryIdByVersionIdRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.GetLibraryVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.GetLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.GetLibraryVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.ImportLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.LockLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.UnlockLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.UpdateLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.util.Constants;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditLibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.EVENT;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.GROUP;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConstants.LEVEL;
import com.eurodyn.qlack2.be.rules.impl.util.AuditConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.ConverterUtil;
import com.eurodyn.qlack2.be.rules.impl.util.KnowledgeBaseUtil;
import com.eurodyn.qlack2.be.rules.impl.util.SecurityUtils;
import com.eurodyn.qlack2.be.rules.impl.util.VersionStateUtils;
import com.eurodyn.qlack2.be.rules.impl.util.XmlConverterUtil;
import com.eurodyn.qlack2.fuse.auditclient.api.AuditClientService;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class LibraryVersionServiceImpl implements LibraryVersionService {
	private static final Logger LOGGER = Logger.getLogger(LibraryVersionServiceImpl.class.getName());

	@SuppressWarnings("unused")
	private IDMService idmService;

	private AuditClientService audit;

	private EventPublisherService eventPublisher;

	private EntityManager em;

	private ConverterUtil mapper;

	private XmlConverterUtil xmlMapper;

	private AuditConverterUtil auditMapper;

	private SecurityUtils securityUtils;

	private VersionStateUtils versionStateUtils;

	private KnowledgeBaseUtil knowledgeBaseUtil;

	private int maxFileSize;

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

	public void setXmlMapper(XmlConverterUtil xmlMapper) {
		this.xmlMapper = xmlMapper;
	}

	public void setAuditMapper(AuditConverterUtil auditMapper) {
		this.auditMapper = auditMapper;
	}

	public void setSecurityUtils(SecurityUtils securityUtils) {
		this.securityUtils = securityUtils;
	}

	public void setVersionStateUtils(VersionStateUtils versionStateUtils) {
		this.versionStateUtils = versionStateUtils;
	}

	public void setKnowledgeBaseUtil(KnowledgeBaseUtil knowledgeBaseUtil) {
		this.knowledgeBaseUtil = knowledgeBaseUtil;
	}

	/**
	 * Set the maximum file size while also converting it to bytes
	 * @param maxFileSize
	 */
	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize * 1024 * 1024;
	}

	@Override
	public int getMaxFileSize() {
		return maxFileSize; // for use by -web
	}

	@ValidateTicket
	@Override
	public List<LibraryVersionDTO> getLibraryVersions(GetLibraryVersionsRequest request) {
		String libraryId = request.getId();

		LOGGER.log(Level.FINE, "Get library versions for library {0}.", libraryId);

		SignedTicket ticket = request.getSignedTicket();

		// do not check security, summary is always viewable
		List<LibraryVersion> versions = LibraryVersion.findByLibraryId(em, libraryId);

		List<LibraryVersionDTO> versionDtos = mapper.mapLibraryVersionSummaryList(versions, ticket);

		return versionDtos;
	}

	@Override
	@ValidateTicket
	public LibraryVersionDTO getLibraryVersion(GetLibraryVersionRequest request) {
		String versionId = request.getVersionId();

		LOGGER.log(Level.FINE, "Get rule version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewLibrary(ticket, library);

		LibraryVersionDTO versionDto = mapper.mapLibraryVersion(version, ticket);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.VIEW.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionDto;
	}

	@Override
	@ValidateTicket
	public Long countLibraryVersionsLockedByOtherUser(CountLibraryVersionsLockedByOtherUserRequest req) {
		String libraryId = req.getLibraryId();

		LOGGER.log(Level.FINE, "Check library versions locked by other for library {0}.", libraryId);

		Library library = Library.findById(em, libraryId);

		SignedTicket ticket = req.getSignedTicket();
		securityUtils.checkCanViewLibrary(ticket, library);

		Long count = LibraryVersion.countLibraryVersionsLockedByOtherUser(em, libraryId, ticket.getUserID());
		return count;
	}

	@Override
	@ValidateTicket
	public String getLibraryVersionIdByName(GetLibraryVersionIdByNameRequest req) {
		String libraryId = req.getLibraryId();
		String name = req.getLibraryVersionName();

		LOGGER.log(Level.FINE, "Get library version by library {0} and name {1}.", new Object[]{libraryId, name});

		String versionId = LibraryVersion.getLibraryVersionIdByName(em, name, libraryId);

		return versionId;
	}

	@ValidateTicket
	@Override
	public String getLibraryIdByVersionId(GetLibraryIdByVersionIdRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Get library id for library version {0}.", versionId);

		String libraryId = LibraryVersion.findLibraryIdById(em, versionId);

		return libraryId;
	}

	@Override
	@ValidateTicket
	public String createLibraryVersion(CreateLibraryVersionRequest req) {
		String libraryId = req.getLibraryId();

		LOGGER.log(Level.FINE, "Create library version for library {0}.", libraryId);

		Library library = Library.findById(em, libraryId);

		SignedTicket ticket = req.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		byte[] jar = req.getContentJar();

		if (jar.length > maxFileSize) {
			throw new QInvalidActionException("The size of uploaded file exceeds the maximum allowed file size.");
		}

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		LibraryVersion version = new LibraryVersion();
		String versionId = version.getId();

		version.setName(req.getName());
		version.setDescription(req.getDescription());
		version.setContentJar(jar);
		version.setLibrary(library);
		version.setState(VersionState.DRAFT);
		version.setCreatedBy(ticket.getUserID());
		version.setCreatedOn(millis);
		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		em.persist(version);

		publishVersionEvent(ticket, Constants.EVENT_CREATE, versionId);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CREATE.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	@ValidateTicket
	@Override
	public void updateLibraryVersion(UpdateLibraryRequest lRequest) {
		UpdateLibraryVersionRequest request = lRequest.getVersionRequest();
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Update library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = lRequest.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		if (version.getState() == VersionState.FINAL)
			throw new QInvalidActionException("You are not allowed to update a version which is finalized.");

		if (LibraryVersion.checkLibraryVersionLockedByOtherUser(em, versionId, ticket.getUserID()))
			throw new QInvalidActionException("You are not allowed to update a version which is locked by another user.");

		version.setDescription(request.getDescription());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		if (version.getState() == VersionState.TESTING) {
			invalidateWorkingSets(ticket, version);
		}

		publishVersionEvent(ticket, Constants.EVENT_UPDATE, versionId);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void updateLibraryVersion(UpdateLibraryVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Update library version content {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		if (version.getState() == VersionState.FINAL)
			throw new QInvalidActionException("You are not allowed to update a version which is finalized.");

		if (LibraryVersion.checkLibraryVersionLockedByOtherUser(em, versionId, ticket.getUserID()))
			throw new QInvalidActionException("You are not allowed to update a version which is locked by another user.");

		byte[] jar = request.getContentJar();

		if (jar.length > maxFileSize) {
			throw new QInvalidActionException("The size of uploaded file exceeds the maximum allowed file size.");
		}

		version.setContentJar(jar);

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		publishVersionEvent(ticket, Constants.EVENT_UPDATE_CONTENT, versionId);

		if (version.getState() == VersionState.TESTING) {
			invalidateWorkingSets(ticket, version);
		}

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UPDATE_CONTENT.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	private void invalidateWorkingSets(SignedTicket ticket, LibraryVersion version) {
		String versionId = version.getId();
		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingLibraryVersion(em, versionId);
		for (WorkingSetVersion workingSetVersion : workingSetVersions) {
			if (workingSetVersion.getState() == VersionState.TESTING) {
				knowledgeBaseUtil.destroyKnowledgeBase(ticket, workingSetVersion);
			}
		}
	}

	@ValidateTicket
	@Override
	public CanDeleteLibraryVersionResult canDeleteLibraryVersion(DeleteLibraryVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can delete library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DELETE.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingLibraryVersion(em, versionId);
		if (!workingSetVersions.isEmpty()) {
			CanDeleteLibraryVersionResult result = new CanDeleteLibraryVersionResult();
			result.setResult(false);

			result.setContainedInWorkingSetVersions(true);
			List<String> workingSetNames = new ArrayList<>();
			for (WorkingSetVersion workingSetVersion : workingSetVersions) {
				workingSetNames.add(workingSetVersion.getWorkingSet().getName() + " / " + workingSetVersion.getName());
			}
			result.setWorkingSetVersions(workingSetNames);

			return result;
		}

		CanDeleteLibraryVersionResult result = new CanDeleteLibraryVersionResult();
		result.setResult(true);

		return result;
	}

	@ValidateTicket
	@Override
	public void deleteLibraryVersion(DeleteLibraryVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Delete library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		versionStateUtils.checkCanModifyLibraryVersion(ticket.getUserID(), version);

		List<WorkingSetVersion> workingSetVersions = WorkingSetVersion.findContainingLibraryVersion(em, versionId);
		if (!workingSetVersions.isEmpty()) {
			throw new QInvalidActionException("The library version is contained in a working set version.");
		}

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.DELETE.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		em.remove(version);

		publishVersionEvent(ticket, Constants.EVENT_DELETE, versionId);
	}

	@Override
	@ValidateTicket
	public void lockLibraryVersion(LockLibraryVersionRequest req) {
		String versionId = req.getId();

		LOGGER.log(Level.FINE, "Lock library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);

		Library library = version.getLibrary();

		SignedTicket ticket = req.getSignedTicket();
		securityUtils.checkCanLockLibraryVersion(ticket, library);

		if (version.getState() == VersionState.FINAL)
			throw new QInvalidActionException("You cannot lock a library version which is finalised.");

		if (version.getLockedBy() != null) {
			// TODO check if different exceptions should be thrown according to
			// who locked the version
			throw new QInvalidActionException("The library version is already locked.");
		}

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setLockedBy(ticket.getUserID());
		version.setLockedOn(millis);

		publishVersionEvent(ticket, Constants.EVENT_LOCK, versionId);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.LOCK.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@Override
	@ValidateTicket
	public void unlockLibraryVersion(UnlockLibraryVersionRequest req) {
		String versionId = req.getId();

		LOGGER.log(Level.FINE, "Unlock library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);

		Library library = version.getLibrary();

		SignedTicket ticket = req.getSignedTicket();
		securityUtils.checkCanUnlockLibraryVersion(ticket, library);

		if (version.getState() == VersionState.FINAL)
			throw new QInvalidActionException("You cannot unlock a library version which is finalised.");

		if (version.getLockedBy() == null) {
			throw new QInvalidActionException("The library version is already unlocked.");
		}

		if (!version.getLockedBy().equals(ticket.getUserID())) {
			boolean canUnlockAny = securityUtils.canUnlockAnyLibraryVersion(ticket, library);
			if (!canUnlockAny) {
				throw new QInvalidActionException("Library version is locked by other user.");
			}
		}

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_UNLOCK, versionId);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.UNLOCK.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public CanDisableTestingLibraryResult canDisableTestingLibraryVersion(EnableTestingLibraryVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Check can disable testing library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.CAN_DISABLE_TESTING.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		List<WorkingSetVersion> enabledTestingWorkingSetVersions = filterEnabledTestingWorkingSetVersions(version.getWorkingSets());
		if (!enabledTestingWorkingSetVersions.isEmpty()) {
			List<String> workingSetVersionNames = new ArrayList<>();
			for (WorkingSetVersion workingSetVersion : enabledTestingWorkingSetVersions) {
				workingSetVersionNames.add(workingSetVersion.getWorkingSet().getName() + " / " + workingSetVersion.getName());
			}

			CanDisableTestingLibraryResult result = new CanDisableTestingLibraryResult();
			result.setResult(false);
			result.setContainedInWorkingSetVersions(true);
			result.setWorkingSetVersions(workingSetVersionNames);
			return result;
		}

		CanDisableTestingLibraryResult result = new CanDisableTestingLibraryResult();
		result.setResult(true);
		return result;
	}

	private List<WorkingSetVersion> filterEnabledTestingWorkingSetVersions(List<WorkingSetVersion> versions) {
		List<WorkingSetVersion> enabledTestingVersions = new ArrayList<>();
		for (WorkingSetVersion version : versions) {
			if (version.getState() == VersionState.TESTING) {
				enabledTestingVersions.add(version);
			}
		}
		return enabledTestingVersions;
	}

	@ValidateTicket
	@Override
	public void enableTestingLibraryVersion(EnableTestingLibraryVersionRequest request) {
		String versionId = request.getId();
		boolean enableTesting = request.isEnableTesting();

		LOGGER.log(Level.FINE, "Enable testing library version {0} ({1}).", new Object[]{versionId, enableTesting});

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		versionStateUtils.checkLibraryVersionNotFinalized(version);
		versionStateUtils.checkCanModifyLibraryVersion(ticket.getUserID(), version);

		if (enableTesting) {
			version.setState(VersionState.TESTING);
		}
		else {
			List<WorkingSetVersion> enabledTestingWorkingSetVersions = filterEnabledTestingWorkingSetVersions(version.getWorkingSets());
			if (!enabledTestingWorkingSetVersions.isEmpty()) {
				throw new QInvalidActionException("This library version is contained in working set versions with testing enabled.");
			}

			version.setState(VersionState.DRAFT);
		}

		String stringEvent = enableTesting ? Constants.EVENT_ENABLE_TESTING : Constants.EVENT_DISABLE_TESTING;
		publishVersionEvent(ticket, stringEvent, versionId);

		EVENT event = enableTesting ? EVENT.ENABLE_TESTING : EVENT.DISABLE_TESTING;
		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), event.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public void finaliseLibraryVersion(FinaliseLibraryVersionRequest req) {
		String versionId = req.getId();

		LOGGER.log(Level.FINE, "Finalize library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = req.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		if (version.getState() == VersionState.FINAL)
			throw new QInvalidActionException("Library version is already finalised.");

		if (version.getLockedBy() != null
				&& !version.getLockedBy().equals(ticket.getUserID()))
			throw new QInvalidActionException("The library version is locked by other user.");

		version.setState(VersionState.FINAL);

		version.setLockedBy(null);
		version.setLockedOn(null);

		publishVersionEvent(ticket, Constants.EVENT_FINALISE, versionId);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.FINALISE.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);
	}

	@ValidateTicket
	@Override
	public byte[] exportLibraryVersion(ExportLibraryVersionRequest request) {
		String versionId = request.getId();

		LOGGER.log(Level.FINE, "Export library version {0}.", versionId);

		LibraryVersion version = LibraryVersion.findById(em, versionId);
		if (version == null) {
			return null;
		}

		Library library = version.getLibrary();

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanViewLibrary(ticket, library);

		if (version.getState() != VersionState.FINAL) {
			throw new QInvalidActionException("Version is not finalized.");
		}

		XmlLibraryVersionDTO xmlVersionDto = xmlMapper.mapLibrary(version);

		byte[] xml = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// XXX generated XML is immutable, generate once and cache ?
			JAXBContext jaxbContext = JAXBContext.newInstance(XmlLibraryVersionDTO.class);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(xmlVersionDto, baos);

			xml = baos.toByteArray();
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot export library version.", e);
		}

		publishVersionEvent(ticket, Constants.EVENT_EXPORT, versionId);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.EXPORT.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return xml;
	}

	@ValidateTicket
	@Override
	public String importLibraryVersion(ImportLibraryVersionRequest request) {
		String libraryId = request.getLibraryId();
		byte[] xml = request.getXml();

		LOGGER.log(Level.FINE, "Import library version in library {0}.", libraryId);

		Library library = Library.findById(em, libraryId);
		if (library == null) {
			throw new IllegalArgumentException("Library does not exist");
		}

		SignedTicket ticket = request.getSignedTicket();
		securityUtils.checkCanUpdateLibrary(ticket, library);

		XmlLibraryVersionDTO xmlVersion = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(xml);

			JAXBContext jaxbContext = JAXBContext.newInstance(XmlLibraryVersionDTO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			xmlVersion = (XmlLibraryVersionDTO) jaxbUnmarshaller.unmarshal(bais);
		}
		catch (JAXBException e) {
			throw new QImportExportException("Cannot import library version.", e);
		}

		String existingLibraryVersionId = LibraryVersion.getLibraryVersionIdByName(em, xmlVersion.getName(), libraryId);
		if (existingLibraryVersionId != null) {
			throw new QImportExportException("Another library version with the same name already exists.");
		}

		LibraryVersion version = xmlMapper.mapLibraryVersion(ticket, library, xmlVersion);
		String versionId = version.getId();

		em.persist(version);

		publishVersionEvent(ticket, Constants.EVENT_IMPORT, versionId);

		AuditLibraryVersionDTO auditDto = auditMapper.mapLibraryVersion(version);
		audit.audit(LEVEL.QBE_RULES.toString(), EVENT.IMPORT.toString(), GROUP.LIBRARY_VERSION.toString(),
					null, ticket.getUserID(), auditDto);

		return versionId;
	}

	// -- Helpers

	@Override
	public boolean canModifyLibraryVersionIdList(SignedTicket ticket, List<String> versionIds) {
		List<LibraryVersion> versions = new ArrayList<>();
		for (String versionId : versionIds) {
			LibraryVersion version = LibraryVersion.findById(em, versionId);
			versions.add(version);
		}
		return canModifyLibraryVersionList(ticket, versions);
	}

	private boolean canModifyLibraryVersionList(SignedTicket ticket, List<LibraryVersion> versions) {
		for (LibraryVersion version : versions) {
			if (!canModifySingleLibraryVersion(ticket, version)) {
				return false;
			}
		}
		return true;
	}

	private boolean canModifySingleLibraryVersion(SignedTicket ticket, LibraryVersion version) {
		Library library = version.getLibrary();
		if (!securityUtils.canUpdateLibrary(ticket, library)) {
			return false;
		}

		if (!versionStateUtils.canModifyLibraryVersion(ticket.getUserID(), version)) {
			return false;
		}

		return true;
	}

	private void publishVersionEvent(SignedTicket ticket, String event, String versionId) {
		Map<String, Object> message = new HashMap<>();
		message.put("srcUserId", ticket.getUserID());
		message.put("event", event);
		message.put(Constants.EVENT_DATA_LIBRARY_VERSION_ID, versionId);

		eventPublisher.publishSync(message, Constants.TOPIC_PREFIX + Constants.RESOURCE_TYPE_LIBRARY_VERSION + "/" + event);
	}

}
