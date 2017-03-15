package com.eurodyn.qlack2.be.rules.web.rest;

import com.eurodyn.qlack2.be.rules.api.DataModelsService;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteDataModelResult;
import com.eurodyn.qlack2.be.rules.api.exception.QInheritanceCycleException;
import com.eurodyn.qlack2.be.rules.api.exception.QNonUniqueFieldNamesException;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.*;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.CreateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionByDataModelAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelVersionsRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.ImportDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelVersionCreateRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelVersionImportRestDTO;
import com.eurodyn.qlack2.be.rules.web.util.RestConverterUtil;
import com.eurodyn.qlack2.be.rules.web.util.Utils;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.dto.DBFileDTO;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/data-models")
public class DataModelsRest {

    @Context
    private HttpHeaders headers;

    private DataModelsService modelsService;

    private RestConverterUtil mapper = RestConverterUtil.INSTANCE;

    private FileUpload fileUpload;

    public void setDataModelsService(DataModelsService modelsService) {
        this.modelsService = modelsService;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    // -- Data models

    @GET
    @Path("{modelId}")
    @Produces(MediaType.APPLICATION_JSON)
    public DataModelDTO getDataModel(@PathParam("modelId") String modelId) {

        GetDataModelRequest request = new GetDataModelRequest();
        request.setId(modelId);

        return modelsService.getDataModel(Utils.sign(request, headers));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument
    public String createDataModel(DataModelRestDTO modelRestDto) {

        checkUniqueDataModelNameOnCreate(modelRestDto.getProjectId(), modelRestDto.getName());

        CreateDataModelRequest request = mapper.mapCreateDataModel(modelRestDto);

        return modelsService.createDataModel(Utils.sign(request, headers));
    }

    @PUT
    @Path("{modelId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public void updateDataModel(@PathParam("modelId") String modelId, DataModelRestDTO modelRestDto) {

        checkUniqueDataModelNameOnUpdate(modelId, modelRestDto.getName());

        UpdateDataModelRequest request = mapper.mapUpdateDataModel(modelRestDto);
        request.setId(modelId);
        Utils.sign(request, headers);

        try {
            modelsService.updateDataModel(request);
        } catch (QInheritanceCycleException ice) {
            throw new QValidationException(createInheritanceCycleError());
        } catch (QNonUniqueFieldNamesException ufe) {
            throw new QValidationException(createDataModelFieldsUniqueNamesError());
        }
    }

    @DELETE
    @Path("{modelId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteDataModel(@PathParam("modelId") String modelId) {

        DeleteDataModelRequest request = new DeleteDataModelRequest();
        request.setId(modelId);

        modelsService.deleteDataModel(Utils.sign(request, headers));
    }

    @GET
    @Path("{modelId}/canDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public CanDeleteDataModelResult canDelete(@PathParam("modelId") String modelId) {

        DeleteDataModelRequest request = new DeleteDataModelRequest();
        request.setId(modelId);

        return modelsService.canDeleteDataModel(Utils.sign(request, headers));
    }

    // -- Data model versions

    @GET
    @Path("{modelId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DataModelVersionDTO> getDataModelVersions(@PathParam("modelId") String modelId,
                                                          @QueryParam("filterCycles") String filterCycles) {

        GetDataModelVersionsRequest request = new GetDataModelVersionsRequest();
        request.setId(modelId);
        request.setFilterCycles(filterCycles);
        Utils.sign(request, headers);

        return modelsService.getDataModelVersions(request);
    }

    @POST
    @Path("{modelId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String createDataModelVersion(@PathParam("modelId") String modelId, DataModelVersionCreateRestDTO versionRestDto) {

        checkUniqueDataModelVersionNameOnCreate(modelId, versionRestDto.getName());

        CreateDataModelVersionRequest request = mapper.mapCreateDataModelVersion(versionRestDto);
        request.setDataModelId(modelId);

        return modelsService.createDataModelVersion(Utils.sign(request, headers));
    }

    @PUT
    @Path("{modelId}/import-version")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String importDataModelVersion(@PathParam("modelId") String modelId, DataModelVersionImportRestDTO versionRestDto) {
        FileGetResponse fileResponse = fileUpload.getByID(versionRestDto.getFile());
        DBFileDTO fileDto = fileResponse.getFile();

        checkFileExtensionIsXml(fileDto);

        ImportDataModelVersionRequest request = new ImportDataModelVersionRequest();
        request.setDataModelId(modelId);
        request.setXml(fileDto.getFileData());
        Utils.sign(request, headers);

        return modelsService.importDataModelVersion(request);
    }

    // -- Helpers

    private static ValidationErrors createInheritanceCycleError() {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("parent");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.dataModel.version.parent.InheritanceCycle");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.dataModel.version.parent.InheritanceCycle");
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createDataModelFieldsUniqueNamesError() {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("fields");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.dataModel.version.fields.UniqueNames");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.dataModel.version.fields.UniqueNames");
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    // -- Helpers

    private void checkUniqueDataModelNameOnCreate(String projectId, String name) throws QValidationException {
        GetDataModelByProjectAndNameRequest request = new GetDataModelByProjectAndNameRequest();
        request.setProjectId(projectId);
        request.setName(name);

        DataModelDTO existingModel = modelsService.getDataModelByProjectAndName(Utils.sign(request, headers));
        if (existingModel != null) {
            throw new QValidationException(createUniqueDataModelNameError(name));
        }
    }

    private void checkUniqueDataModelNameOnUpdate(String modelId, String name) throws QValidationException {
        GetDataModelRequest currentRequest = new GetDataModelRequest();
        currentRequest.setId(modelId);

        // ... or just query for projectId
        DataModelDTO currentModel = modelsService.getDataModel(Utils.sign(currentRequest, headers));
        if (currentModel != null) {
            String projectId = currentModel.getProjectId();

            GetDataModelByProjectAndNameRequest request = new GetDataModelByProjectAndNameRequest();
            request.setProjectId(projectId);
            request.setName(name);

            DataModelDTO existingModel = modelsService.getDataModelByProjectAndName(Utils.sign(request, headers));
            if (existingModel != null) {
                if (!existingModel.getId().equals(modelId)) {
                    throw new QValidationException(createUniqueDataModelNameError(name));
                }
            }
        }
    }

    private void checkUniqueDataModelVersionNameOnCreate(String modelId, String versionName) throws QValidationException {
        GetDataModelVersionByDataModelAndNameRequest request = new GetDataModelVersionByDataModelAndNameRequest();
        request.setDataModelId(modelId);
        request.setName(versionName);

        DataModelVersionDTO existingVersion = modelsService.getDataModelVersionByDataModelAndName(Utils.sign(request, headers));
        if (existingVersion != null) {
            throw new QValidationException(createUniqueDataModelVersionNameError(versionName));
        }
    }

    private static ValidationErrors createUniqueDataModelNameError(String name) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("name");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.InvalidValue, name);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createUniqueDataModelVersionNameError(String versionName) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("name");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionName);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private void checkFileExtensionIsXml(DBFileDTO fileDto) throws QValidationException {
        String filename = fileDto.getFilename();

        String[] tokens = filename.split("\\.(?=[^\\.]+$)");
        if (!tokens[1].equalsIgnoreCase("xml")) {
            throw new QValidationException(createFileExtensionIsNotXmlError(filename));
        }
    }

    private static ValidationErrors createFileExtensionIsNotXmlError(String filename) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("file");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.XmlFile");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.XmlFile");
        vet.putAttribute(ValidationAttribute.InvalidValue, filename);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

}
