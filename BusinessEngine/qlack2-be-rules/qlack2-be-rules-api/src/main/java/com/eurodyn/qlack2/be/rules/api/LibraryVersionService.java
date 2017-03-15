package com.eurodyn.qlack2.be.rules.api;

import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.LibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteLibraryVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingLibraryResult;
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
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public interface LibraryVersionService {

	int getMaxFileSize();

	List<LibraryVersionDTO> getLibraryVersions(GetLibraryVersionsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	LibraryVersionDTO getLibraryVersion(GetLibraryVersionRequest sreq)
			throws QInvalidTicketException, QAuthorisationException;

	Long countLibraryVersionsLockedByOtherUser(CountLibraryVersionsLockedByOtherUserRequest sreq)
			throws QInvalidTicketException, QAuthorisationException;

	String getLibraryVersionIdByName(GetLibraryVersionIdByNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String getLibraryIdByVersionId(GetLibraryIdByVersionIdRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createLibraryVersion(CreateLibraryVersionRequest sreq)
			throws QInvalidTicketException, QAuthorisationException;

	void updateLibraryVersion(UpdateLibraryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void updateLibraryVersion(UpdateLibraryVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteLibraryVersionResult canDeleteLibraryVersion(DeleteLibraryVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteLibraryVersion(DeleteLibraryVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void lockLibraryVersion(LockLibraryVersionRequest sreq)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void unlockLibraryVersion(UnlockLibraryVersionRequest sreq)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	CanDisableTestingLibraryResult canDisableTestingLibraryVersion(EnableTestingLibraryVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void enableTestingLibraryVersion(EnableTestingLibraryVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void finaliseLibraryVersion(FinaliseLibraryVersionRequest sreq)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	byte[] exportLibraryVersion(ExportLibraryVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

	String importLibraryVersion(ImportLibraryVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

	// -- Helpers

	boolean canModifyLibraryVersionIdList(SignedTicket ticket, List<String> versionIds);

}
