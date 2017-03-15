package com.eurodyn.qlack2.be.rules.api;

import java.util.List;

import com.eurodyn.qlack2.be.rules.api.dto.DataModelDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldTypeDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteDataModelVersionResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDisableTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanEnableTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanFinalizeDataModelResult;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanUpdateEnabledForTestingDataModelResult;
import com.eurodyn.qlack2.be.rules.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.rules.api.exception.QInvalidActionException;
import com.eurodyn.qlack2.be.rules.api.request.EmptyRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.CreateDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.DeleteDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.GetDataModelByProjectAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.GetDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.UpdateDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.CreateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.DeleteDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.EnableTestingDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.ExportDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.FinalizeDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelIdByVersionIdRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionByDataModelAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.ImportDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.LockDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UnlockDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UpdateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.project.GetProjectDataModelsRequest;
import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public interface DataModelsService {

	// -- Data models

	List<DataModelDTO> getDataModels(GetProjectDataModelsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	DataModelDTO getDataModel(GetDataModelRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	DataModelDTO getDataModelByProjectAndName(GetDataModelByProjectAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createDataModel(CreateDataModelRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void updateDataModel(UpdateDataModelRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteDataModelResult canDeleteDataModel(DeleteDataModelRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteDataModel(DeleteDataModelRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	// -- Data model versions

	List<DataModelFieldTypeDTO> getDataModelFieldTypes(EmptyRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	List<DataModelVersionDTO> getDataModelVersions(GetDataModelVersionsRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	DataModelVersionDTO getDataModelVersion(GetDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	DataModelVersionDTO getDataModelVersionByDataModelAndName(GetDataModelVersionByDataModelAndNameRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String getDataModelIdByVersionId(GetDataModelIdByVersionIdRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	String createDataModelVersion(CreateDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDeleteDataModelVersionResult canDeleteDataModelVersion(DeleteDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void deleteDataModelVersion(DeleteDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void lockDataModelVersion(LockDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	void unlockDataModelVersion(UnlockDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	CanUpdateEnabledForTestingDataModelResult canUpdateEnabledForTestingDataModelVersion(UpdateDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanEnableTestingDataModelResult canEnableTestingDataModelVersion(EnableTestingDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	CanDisableTestingDataModelResult canDisableTestingDataModelVersion(EnableTestingDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void enableTestingDataModelVersion(EnableTestingDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	CanFinalizeDataModelResult canFinalizeDataModelVersion(FinalizeDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException;

	void finalizeDataModelVersion(FinalizeDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException;

	byte[] exportDataModelVersion(ExportDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

	String importDataModelVersion(ImportDataModelVersionRequest request)
			throws QInvalidTicketException, QAuthorisationException, QInvalidActionException, QImportExportException;

	// -- Helpers

	List<String> checkDataModelVersionClosureContained(List<String> containedVersionIds);

	boolean canModifyDataModelVersionIdList(SignedTicket ticket, List<String> versionIds);

	void enableTestingDataModelVersionNoCascade(EnableTestingDataModelVersionRequest request);

	void finalizeDataModelVersionNoCascade(FinalizeDataModelVersionRequest request);

}
