package com.eurodyn.qlack2.be.rules.web.rest;

import com.eurodyn.qlack2.be.rules.api.DataModelsService;
import com.eurodyn.qlack2.be.rules.api.LibraryVersionService;
import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.WorkingSetsService;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteWorkingSetResult;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.GetDataModelIdByVersionIdRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.GetLibraryIdByVersionIdRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.GetRuleIdByVersionIdRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.*;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.CreateWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.GetWorkingSetVersionByWorkingSetAndNameRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.ImportWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetVersionCreateRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetVersionImportRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetVersionRestDTO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/working-sets")
public class WorkingSetsRest {

    @Context
    private HttpHeaders headers;

    private WorkingSetsService workingSetsService;

    private RulesService rulesService;

    private DataModelsService modelsService;

    private LibraryVersionService libraryVersionService;

    private RestConverterUtil mapper = RestConverterUtil.INSTANCE;

    private FileUpload fileUpload;

    public void setWorkingSetsService(WorkingSetsService workingSetsService) {
        this.workingSetsService = workingSetsService;
    }

    public void setRulesService(RulesService rulesService) {
        this.rulesService = rulesService;
    }

    public void setDataModelsService(DataModelsService modelsService) {
        this.modelsService = modelsService;
    }

    public void setLibraryVersionService(LibraryVersionService libraryVersionService) {
        this.libraryVersionService = libraryVersionService;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    // -- Working Sets

    @GET
    @Path("{workingSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    public WorkingSetDTO getWorkingSet(@PathParam("workingSetId") String workingSetId) {

        GetWorkingSetRequest request = new GetWorkingSetRequest();
        request.setId(workingSetId);

        return workingSetsService.getWorkingSet(Utils.sign(request, headers));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument
    public String createWorkingSet(WorkingSetRestDTO workingSetRestDto) {

        checkUniqueWorkingSetNameOnCreate(workingSetRestDto.getProjectId(), workingSetRestDto.getName());

        CreateWorkingSetRequest request = mapper.mapCreateWorkingSet(workingSetRestDto);

        return workingSetsService.createWorkingSet(Utils.sign(request, headers));
    }

    @PUT
    @Path("{workingSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public void updateWorkingSet(@PathParam("workingSetId") String workingSetId, WorkingSetRestDTO workingSetRestDto) {

        checkUniqueWorkingSetNameOnUpdate(workingSetId, workingSetRestDto.getName());

        WorkingSetVersionRestDTO versionRestDto = workingSetRestDto.getVersion();
        if (versionRestDto != null) {
            checkRulesUnique(versionRestDto.getRuleVersionIds());
            checkDataModelsUnique(versionRestDto.getDataModelVersionIds());
            checkLibrariesUnique(versionRestDto.getLibraryVersionIds());
        }

        UpdateWorkingSetRequest request = mapper.mapUpdateWorkingSet(workingSetRestDto);
        request.setId(workingSetId);

        workingSetsService.updateWorkingSet(Utils.sign(request, headers));
    }

    @DELETE
    @Path("{workingSetId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteWorkingSet(@PathParam("workingSetId") String workingSetId) {

        DeleteWorkingSetRequest request = new DeleteWorkingSetRequest();
        request.setId(workingSetId);

        workingSetsService.deleteWorkingSet(Utils.sign(request, headers));
    }

    @GET
    @Path("{workingSetId}/canDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public CanDeleteWorkingSetResult canDeleteWorkingSet(@PathParam("workingSetId") String workingSetId) {

        DeleteWorkingSetRequest request = new DeleteWorkingSetRequest();
        request.setId(workingSetId);

        return workingSetsService.canDeleteWorkingSet(Utils.sign(request, headers));
    }

    // -- Working set versions

    @POST
    @Path("{workingSetId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String createWorkingSetVersion(@PathParam("workingSetId") String workingSetId, WorkingSetVersionCreateRestDTO versionRestDto) {

        checkUniqueWorkingSetVersionNameOnCreate(workingSetId, versionRestDto.getName());

        CreateWorkingSetVersionRequest request = mapper.mapCreateWorkingSetVersion(versionRestDto);
        request.setWorkingSetId(workingSetId);

        return workingSetsService.createWorkingSetVersion(Utils.sign(request, headers));
    }

    @PUT
    @Path("{workingSetId}/import-version")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String importWorkingSetVersion(@PathParam("workingSetId") String workingSetId, WorkingSetVersionImportRestDTO versionRestDto) {
        FileGetResponse fileResponse = fileUpload.getByID(versionRestDto.getFile());
        DBFileDTO fileDto = fileResponse.getFile();

        checkFileExtensionIsXml(fileDto);

        ImportWorkingSetVersionRequest request = new ImportWorkingSetVersionRequest();
        request.setWorkingSetId(workingSetId);
        request.setXml(fileDto.getFileData());
        Utils.sign(request, headers);

        return workingSetsService.importWorkingSetVersion(request);
    }

    // -- Helpers

    private void checkUniqueWorkingSetNameOnCreate(String projectId, String name) throws QValidationException {
        GetWorkingSetByProjectAndNameRequest request = new GetWorkingSetByProjectAndNameRequest();
        request.setProjectId(projectId);
        request.setName(name);

        WorkingSetDTO existingWorkingSet = workingSetsService.getWorkingSetByProjectAndName(Utils.sign(request, headers));
        if (existingWorkingSet != null) {
            throw new QValidationException(createUniqueWorkingSetNameError(name));
        }
    }

    private void checkUniqueWorkingSetNameOnUpdate(String workingSetId, String name) throws QValidationException {
        GetWorkingSetRequest currentRequest = new GetWorkingSetRequest();
        currentRequest.setId(workingSetId);

        // ... or just query for projectId
        WorkingSetDTO currentWorkingSet = workingSetsService.getWorkingSet(Utils.sign(currentRequest, headers));
        if (currentWorkingSet != null) {
            String projectId = currentWorkingSet.getProjectId();

            GetWorkingSetByProjectAndNameRequest request = new GetWorkingSetByProjectAndNameRequest();
            request.setProjectId(projectId);
            request.setName(name);

            WorkingSetDTO existingWorkingSet = workingSetsService.getWorkingSetByProjectAndName(Utils.sign(request, headers));
            if (existingWorkingSet != null) {
                if (!existingWorkingSet.getId().equals(workingSetId)) {
                    throw new QValidationException(createUniqueWorkingSetNameError(name));
                }
            }
        }
    }

    private void checkUniqueWorkingSetVersionNameOnCreate(String workingSetId, String versionName) throws QValidationException {
        GetWorkingSetVersionByWorkingSetAndNameRequest request = new GetWorkingSetVersionByWorkingSetAndNameRequest();
        request.setWorkingSetId(workingSetId);
        request.setName(versionName);

        WorkingSetVersionDTO existingVersion = workingSetsService.getWorkingSetVersionByWorkingSetAndName(Utils.sign(request, headers));
        if (existingVersion != null) {
            throw new QValidationException(createUniqueWorkingSetVersionNameError(versionName));
        }
    }

    private static ValidationErrors createUniqueWorkingSetNameError(String name) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("name");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.InvalidValue, name);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createUniqueWorkingSetVersionNameError(String versionName) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("name");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionName);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private void checkRulesUnique(List<String> versionIds) {
        Set<String> ruleIds = new HashSet<>();

        for (String versionId : versionIds) {
            GetRuleIdByVersionIdRequest request = new GetRuleIdByVersionIdRequest();
            request.setId(versionId);

            String ruleId = rulesService.getRuleIdByVersionId(Utils.sign(request, headers));
            if (ruleId == null) {
                throw new QValidationException(createRuleNotFoundError(versionId));
            }

            if (ruleIds.contains(ruleId)) {
                throw new QValidationException(createRulesUniqueError(versionId));
            }

            ruleIds.add(ruleId);
        }
    }

    private void checkDataModelsUnique(List<String> versionIds) {
        Set<String> modelIds = new HashSet<>();

        for (String versionId : versionIds) {
            GetDataModelIdByVersionIdRequest request = new GetDataModelIdByVersionIdRequest();
            request.setId(versionId);

            String modelId = modelsService.getDataModelIdByVersionId(Utils.sign(request, headers));
            if (modelId == null) {
                throw new QValidationException(createDataModelNotFoundError(versionId));
            }

            if (modelIds.contains(modelId)) {
                throw new QValidationException(createDataModelsUniqueError(versionId));
            }

            modelIds.add(modelId);
        }
    }

    private void checkLibrariesUnique(List<String> versionIds) {
        Set<String> libraryIds = new HashSet<>();

        for (String versionId : versionIds) {
            GetLibraryIdByVersionIdRequest request = new GetLibraryIdByVersionIdRequest();
            request.setId(versionId);

            String libraryId = libraryVersionService.getLibraryIdByVersionId(Utils.sign(request, headers));
            if (libraryId == null) {
                throw new QValidationException(createLibraryNotFoundError(versionId));
            }

            if (libraryIds.contains(libraryId)) {
                throw new QValidationException(createLibrariesUniqueError(versionId));
            }

            libraryIds.add(libraryId);
        }
    }

    private static ValidationErrors createRuleNotFoundError(String versionId) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("ruleVersionIds");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.workingSet.version.RuleNotFound");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.workingSet.version.RuleNotFound");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionId);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createDataModelNotFoundError(String versionId) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("dataModelVersionIds");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.workingSet.version.DataModelNotFound");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.workingSet.version.DataModelNotFound");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionId);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createLibraryNotFoundError(String versionId) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("libraryVersionIds");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.workingSet.version.LibraryNotFound");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.workingSet.version.LibraryNotFound");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionId);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createRulesUniqueError(String versionId) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("ruleVersionIds");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.workingSet.version.RulesUnique");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.workingSet.version.RulesUnique");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionId);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createDataModelsUniqueError(String versionId) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("dataModelVersionIds");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.workingSet.version.DataModelsUnique");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.workingSet.version.DataModelsUnique");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionId);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createLibrariesUniqueError(String versionId) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("libraryVersionIds");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.workingSet.version.LibrariesUnique");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.workingSet.version.LibrariesUnique");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionId);
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
