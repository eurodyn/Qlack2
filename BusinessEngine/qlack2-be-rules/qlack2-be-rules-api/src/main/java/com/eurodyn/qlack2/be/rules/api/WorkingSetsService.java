package com.eurodyn.qlack2.be.rules.api;

import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteWorkingSetVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanGetWorkingSetVersionModelsJarResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanUpdateEnabledForTestingWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.workingset.CreateWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.DeleteWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.GetWorkingSetByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.GetWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.UpdateWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.CreateWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.DeleteWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.EnableTestingWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.ExportWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.FinalizeWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetProjectWorkingSetVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionByWorkingSetAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionDataModelsJarRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdByNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionIdentifierRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.ImportWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.LockWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.UnlockWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.UpdateWorkingSetVersionRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;

public interface WorkingSetsService {

	// -- Working Sets

	WorkingSetDTO getWorkingSet(GetWorkingSetRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	WorkingSetDTO getWorkingSetByProjectAndName(GetWorkingSetByProjectAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createWorkingSet(CreateWorkingSetRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void updateWorkingSet(UpdateWorkingSetRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteWorkingSetResult canDeleteWorkingSet(DeleteWorkingSetRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteWorkingSet(DeleteWorkingSetRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	// -- Working Set versions

	List<WorkingSetVersionIdentifierDTO> getProjectWorkingSetVersions(GetProjectWorkingSetVersionsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	WorkingSetVersionIdentifierDTO getWorkingSetVersionIdentifier(GetWorkingSetVersionIdentifierRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String getWorkingSetVersionIdByName(GetWorkingSetVersionIdByNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	WorkingSetVersionDTO getWorkingSetVersion(GetWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanGetWorkingSetVersionModelsJarResult canGetWorkingSetVersionDataModelsJar(GetWorkingSetVersionDataModelsJarRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	byte[] getWorkingSetVersionDataModelsJar(GetWorkingSetVersionDataModelsJarRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	WorkingSetVersionDTO getWorkingSetVersionByWorkingSetAndName(GetWorkingSetVersionByWorkingSetAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createWorkingSetVersion(CreateWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteWorkingSetVersionResult canDeleteWorkingSetVersion(DeleteWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void deleteWorkingSetVersion(DeleteWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void lockWorkingSetVersion(LockWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void unlockWorkingSetVersion(UnlockWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	CanUpdateEnabledForTestingWorkingSetResult canUpdateEnabledForTestingWorkingSetVersion(UpdateWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanEnableTestingWorkingSetResult canEnableTestingWorkingSetVersion(EnableTestingWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDisableTestingWorkingSetResult canDisableTestingWorkingSetVersion(EnableTestingWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void enableTestingWorkingSetVersion(EnableTestingWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	CanFinalizeWorkingSetResult canFinalizeWorkingSetVersion(FinalizeWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void finalizeWorkingSetVersion(FinalizeWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	byte[] exportWorkingSetVersion(ExportWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

	String importWorkingSetVersion(ImportWorkingSetVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

}
