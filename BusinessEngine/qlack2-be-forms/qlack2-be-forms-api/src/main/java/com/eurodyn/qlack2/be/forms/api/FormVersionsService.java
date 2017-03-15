package com.eurodyn.qlack2.be.forms.api;

import com.eurodyn.qlack2.be.forms.api.dto.FormVersionDTO;
import com.eurodyn.qlack2.be.forms.api.dto.FormVersionDetailsDTO;
import com.eurodyn.qlack2.be.forms.api.dto.TranslationDTO;
import com.eurodyn.qlack2.be.forms.api.exception.*;
import com.eurodyn.qlack2.be.forms.api.request.EmptySignedRequest;
import com.eurodyn.qlack2.be.forms.api.request.form.UpdateFormRequest;
import com.eurodyn.qlack2.be.forms.api.request.version.*;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

import java.util.List;

public interface FormVersionsService {

	List<FormVersionDTO> getFormVersions(GetFormVersionsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Retrieves the number of form versions locked by another user.
	 *
	 * @param request
	 * @return
	 */
	Long countFormVersionsLockedByOtherUser(
			CountFormVersionsLockedByOtherUserRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Creates a new form version.
	 *
	 * @param request
	 * @return
	 */
	String createFormVersion(CreateFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QFaultFormVersionTemplateException;

	/**
	 * Updates a form version.
	 *
	 * @param request
	 */
	void updateFormVersion(UpdateFormRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	/**
	 * Retrieves the metadata of a form version
	 *
	 * @param request
	 * @return
	 */
	FormVersionDetailsDTO getFormVersion(GetFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Retrieve a form version id by its name
	 *
	 * @param qSignedRequest
	 * @return
	 */
	String getFormVersionIdByName(GetFormVersionIdByNameRequest qSignedRequest)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Validates the conditions of the form version
	 *
	 * @param request
	 */
	void validateFormVersionConditionsHierarchy(
			ValidateFormVersionConditionsHierarchyRequest request)
			throws QInvalidTicketException, QInvalidConditionHierarchyException;

	/**
	 * Deletes a form version
	 *
	 * @param request
	 */
	void deleteFormVersion(DeleteFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	/**
	 * Gets the form version translations
	 *
	 * @param request
	 * @return
	 */
	List<TranslationDTO> getFormVersionTranslations(
			GetFormVersionTranslationsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Deletes the translations of a form version
	 *
	 * @param request
	 */
	void deleteFormVersionTranslations(
			DeleteFormVersionTranslationsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	/**
	 * Locks a form version.
	 *
	 * @param request
	 */
	void lockFormVersion(LockFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	/**
	 * Unlocks a form version.
	 *
	 * @param request
	 */
	void unlockFormVersion(UnlockFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	/**
	 * Checks whether the form version can be finalised. In order for a form
	 * version to be finalised, all working sets and rules that are used in its
	 * conditions should finalised.
	 *
	 * @param req
	 * @return
	 * @throws QInvalidTicketException
	 * @throws QAuthorisationException
	 * @throws QInvalidOperationException
	 */
	boolean canFinaliseFormVersion(CanFinaliseFormVersionRequest req)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	/**
	 * Finalises a form version.
	 *
	 * @param request
	 */
	void finaliseFormVersion(FinaliseFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	/**
	 * Enables or disables testing for a form version.
	 *
	 * @param request
	 */
	void enableTestingForFormVersion(EnableTestingRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException;

	/**
	 * Imports a form version.
	 *
	 * @param request
	 */
	String importFormVersion(ImportFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QImportExportException;

	/**
	 * Exports a form version
	 *
	 * @param request
	 * @return
	 * @throws QInvalidTicketException
	 * @throws QAuthorisationException
	 * @throws QInvalidOperationException
	 * @throws QImportExportException
	 * @throws QServiceNotAvailableException
	 */
	byte[] exportFormVersion(ExportFormVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException,
			QInvalidOperationException, QImportExportException, QServiceNotAvailableException;

	/**
	 * Retrieves the validation condition types
	 *
	 * @param request
	 * @return
	 */
	List<Integer> getConditionTypes(EmptySignedRequest request)
			throws QInvalidTicketException;

}
