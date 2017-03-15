package com.eurodyn.qlack2.be.rules.api;

import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.LibraryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteLibraryResult;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.library.CreateLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.DeleteLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.GetLibraryByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.GetLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.UpdateLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectLibrariesRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface LibraryService {

	List<LibraryDTO> getLibraries(GetProjectLibrariesRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	LibraryDTO getLibrary(GetLibraryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	LibraryDTO getLibraryByProjectAndName(GetLibraryByProjectAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createLibrary(CreateLibraryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void updateLibrary(UpdateLibraryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteLibraryResult canDeleteLibrary(DeleteLibraryRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteLibrary(DeleteLibraryRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

}
