package com.eurodyn.qlack2.be.workflow.web.rest;

import com.eurodyn.qlack2.be.workflow.api.WorkflowService;
import com.eurodyn.qlack2.be.workflow.api.WorkflowVersionService;
import com.eurodyn.qlack2.be.workflow.api.dto.ConditionDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowDTO;
import com.eurodyn.qlack2.be.workflow.api.dto.WorkflowVersionDTO;
import com.eurodyn.qlack2.be.workflow.api.exception.QInvalidDataException;
import com.eurodyn.qlack2.be.workflow.api.request.version.*;
import com.eurodyn.qlack2.be.workflow.api.request.workflow.*;
import com.eurodyn.qlack2.be.workflow.web.dto.VersionImportRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.WorkflowDetailsRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.WorkflowRDTO;
import com.eurodyn.qlack2.be.workflow.web.dto.WorkflowVersionRDTO;
import com.eurodyn.qlack2.be.workflow.web.util.ConverterUtil;
import com.eurodyn.qlack2.be.workflow.web.util.Utils;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
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
import java.util.*;
import java.util.logging.Logger;

@Path("/workflow")
public class WorkflowRest {

    private static final Logger LOGGER = Logger.getLogger(WorkflowRest.class.getName());

    @Context
    private HttpHeaders headers;

    private WorkflowService workflowService;
    private WorkflowVersionService workflowVersionService;
    private FileUpload fileUploadService;

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    public void setWorkflowVersionService(WorkflowVersionService workflowVersionService) {
        this.workflowVersionService = workflowVersionService;
    }

    public void setFileUploadService(FileUpload fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GET
    @Path("{projectId}/workflows")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkflowDTO> getWorkflows(@PathParam("projectId") String projectId) {
        GetWorkflowsRequest req = new GetWorkflowsRequest();
        req.setProjectId(projectId);
        Utils.sign(req, headers);
        return workflowService.getWorkflows(req);
    }

    @GET
    @Path("{workflowId}")
    @Produces(MediaType.APPLICATION_JSON)
    public WorkflowDTO getWorkflow(@PathParam("workflowId") String workflowId) {
        GetWorkflowRequest req = new GetWorkflowRequest();
        req.setWorkflowId(workflowId);
        Utils.sign(req, headers);
        return workflowService.getWorkflow(req);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument
    public String createWorkflow(WorkflowRDTO workflowRDTO) {
        checkUniqueWorkflowName(null, workflowRDTO.getName());

        CreateWorkflowRequest workflowRequest = ConverterUtil.workflowRDTOToCreateWorkflowRequest(workflowRDTO);

        Utils.sign(workflowRequest, headers);
        return workflowService.createWorkflow(workflowRequest);
    }

    /**
     * Retrieves the list of form version for the given form id.
     *
     * @param workflowId
     * @return
     */
    @GET
    @Path("{workflowId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WorkflowVersionDTO> getWorkflowVersions(@PathParam("workflowId") String workflowId) {
        GetWorkflowVersionsRequest req = new GetWorkflowVersionsRequest();
        req.setWorkflowId(workflowId);

        Utils.sign(req, headers);
        return workflowVersionService.getWorkflowVersions(req);
    }

    @PUT
    @Path("{workflowId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public void updateWorkflow(@PathParam("workflowId") String workflowId, WorkflowDetailsRDTO workflowDetailsRDTO) {
        checkUniqueWorkflowName(workflowId, workflowDetailsRDTO.getName());

        UpdateWorkflowRequest workflowRequest = ConverterUtil
                .workflowDetailsRDTOToUpdateWorkflowRequest(workflowDetailsRDTO);
        workflowRequest.setId(workflowId);

        try {
            Utils.sign(workflowRequest, headers);
            workflowService.updateWorkflow(workflowRequest);
        } catch (QInvalidDataException myException) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors(myException.getInvalidDataSource());
            ValidationErrorType vet = new ValidationErrorType(myException.getErrorCode());
            vet.putAttribute(ValidationAttribute.Message, myException.getErrorCode());
            vet.putAttribute(ValidationAttribute.InvalidValue, myException.getInvalidDataValue());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);
        } catch (Exception e) {
            throw e;
        }
    }

    @DELETE
    @Path("{workflowId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteWorkflow(@PathParam("workflowId") String workflowId) {
        DeleteWorkflowRequest req = new DeleteWorkflowRequest();
        req.setId(workflowId);
        Utils.sign(req, headers);
        workflowService.deleteWorkflow(req);
    }

    @GET
    @Path("{workflowId}/versions/countLockedByOtherUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Long countWorkflowVersionsLockedByOtherUser(@PathParam("workflowId") String workflowId) {
        CountWorkflowVersionsLockedByOtherUserRequest req = new CountWorkflowVersionsLockedByOtherUserRequest();
        req.setWorkflowId(workflowId);
        Utils.sign(req, headers);
        return workflowVersionService.countWorkflowVersionsLockedByOtherUser(req);
    }

    @POST
    @Path("{workflowId}/versions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument
    public String createWorkflowVersion(@PathParam("workflowId") String workflowId, WorkflowVersionRDTO workflowVersionRDTO) {
        // Validate form version name
        GetWorkflowVersionIdByNameRequest workflowVersionByNameRequest = new GetWorkflowVersionIdByNameRequest();
        workflowVersionByNameRequest.setWorkflowVersionName(workflowVersionRDTO.getName());
        workflowVersionByNameRequest.setWorkflowId(workflowId);

        Utils.sign(workflowVersionByNameRequest, headers);
        String existingWorkflowVersionId = workflowVersionService
                .getWorkflowVersionIdByName(workflowVersionByNameRequest);

        if (existingWorkflowVersionId != null) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors("name");
            ValidationErrorType vet = new ValidationErrorType("validation.error.workflow.version.unique.name");
            vet.putAttribute(ValidationAttribute.Message, "validation.error.workflow.version.unique.name");
            vet.putAttribute(ValidationAttribute.InvalidValue, workflowVersionRDTO.getName());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);
        }

        CreateWorkflowVersionRequest workflowVersionRequest = new CreateWorkflowVersionRequest();
        workflowVersionRequest.setWorkflowId(workflowId);
        workflowVersionRequest.setName(workflowVersionRDTO.getName());
        workflowVersionRequest.setDescription(workflowVersionRDTO.getDescription());

        if (workflowVersionRDTO.getBasedOn() != null) {
            GetWorkflowVersionRequest getVersionRequest = new GetWorkflowVersionRequest();
            getVersionRequest.setVersionId(workflowVersionRDTO.getBasedOn());

            Utils.sign(getVersionRequest, headers);
            WorkflowVersionDTO baseVersion = workflowVersionService.getWorkflowVersion(getVersionRequest);

            if (baseVersion != null) {
                workflowVersionRequest.setContent(baseVersion.getContent());

                List<ConditionDTO> baseConditions = baseVersion.getConditions();

                // Copy conditions
                List<ConditionDTO> copiedConditions = copyConditions(baseConditions);
                workflowVersionRequest.setConditions(copiedConditions);
            }
        }

        Utils.sign(workflowVersionRequest, headers);

        try {
            return workflowVersionService.createWorkflowVersion(workflowVersionRequest);
        } catch (QInvalidDataException myException) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors(myException.getInvalidDataSource());
            ValidationErrorType vet = new ValidationErrorType(myException.getErrorCode());
            vet.putAttribute(ValidationAttribute.Message, myException.getErrorCode());
            vet.putAttribute(ValidationAttribute.InvalidValue, myException.getInvalidDataValue());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);

        } catch (Exception e) {
            throw e;
        }

    }

    @POST
    @Path("{workflowId}/import-version")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument
    public String importWorkflowVersion(@PathParam("workflowId") String workflowId, VersionImportRDTO versionContent) {

        ImportWorkflowVersionRequest workflowVersionRequest = new ImportWorkflowVersionRequest();
        workflowVersionRequest.setWorkflowId(workflowId);

        FileGetResponse fResponse = fileUploadService.getByID(versionContent.getContentVersion());

        //check if we have a .xml file
        String[] tokens = fResponse.getFile().getFilename().split("\\.(?=[^\\.]+$)");
        if (!tokens[1].equalsIgnoreCase("xml")) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors("contentVersion");
            ValidationErrorType vet = new ValidationErrorType("qlack.validation.XmlFile");
            vet.putAttribute(ValidationAttribute.Message, "qlack.validation.XmlFile");
            vet.putAttribute(ValidationAttribute.InvalidValue, fResponse.getFile().getFilename());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);
        }

        workflowVersionRequest.setVersionContent(fResponse.getFile().getFileData());
        Utils.sign(workflowVersionRequest, headers);

        try {
            return workflowVersionService.importWorkflowVersion(workflowVersionRequest);
        } catch (QInvalidDataException myException) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors(myException.getInvalidDataSource());
            ValidationErrorType vet = new ValidationErrorType(myException.getErrorCode());
            vet.putAttribute(ValidationAttribute.Message, myException.getErrorCode());
            vet.putAttribute(ValidationAttribute.InvalidValue, myException.getInvalidDataValue());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);

        } catch (Exception e) {
            throw e;
        }

    }


    private void checkUniqueWorkflowName(String id, String name) throws QValidationException {
        GetWorkflowByNameRequest req = new GetWorkflowByNameRequest();
        req.setName(name);

        Utils.sign(req, headers);
        WorkflowDTO existingWorkflow = workflowService.getWorkflowByName(req);
        if (existingWorkflow == null) {
            return;
        }

        if (id != null && id.equals(existingWorkflow.getId())) {
            return;
        }

        ValidationErrors ve = new ValidationErrors();
        ValidationFieldErrors vfe = new ValidationFieldErrors("name");
        ValidationErrorType vet = new ValidationErrorType("validation.error.workflow.unique.name");
        vet.putAttribute(ValidationAttribute.Message, "validation.error.workflow.unique.name");
        vet.putAttribute(ValidationAttribute.InvalidValue, name);
        vfe.addError(vet);
        ve.addValidationError(vfe);
        throw new QValidationException(ve);
    }

    private List<ConditionDTO> copyConditions(List<ConditionDTO> conditions) {
        // Map that holds the original id of the condition and the
        // copied condition.
        Map<String, ConditionDTO> copiedConditionsMap = new HashMap<>();

        // Clone conditions and fix parent relationships.
        List<ConditionDTO> copiedConditions = new ArrayList<>();
        for (ConditionDTO condition : conditions) {
            ConditionDTO copiedCondition = copiedConditionsMap.get(condition
                    .getId());

            if (copiedCondition == null) {
                copiedCondition = copyCondition(condition, copiedConditionsMap);
            }

            copiedConditions.add(copiedCondition);
        }
        return copiedConditions;
    }

    private ConditionDTO copyCondition(ConditionDTO condition,
                                       Map<String, ConditionDTO> copiedConditionsMap) {
        // Map all properties to new condition apart from id and dbVersion.
        // Note that the method BeanUtils.cloneBean could be used but lead to an
        // exception (If this is a new instance, make sure any version and/or
        // auto-generated primary key fields are null/default when persisting.)
        // This problem could not be fixed by setting dbVersion to null.
        ConditionDTO copiedCondition = new ConditionDTO();

        // Setting the id is necessary to enable referencing of parents
        copiedCondition.setId(UUID.randomUUID().toString());
        copiedCondition.setName(condition.getName());
        copiedCondition.setConditionType(condition.getConditionType());
        copiedCondition.setWorkingSetId(condition.getWorkingSetId());
        copiedCondition.setRuleId(condition.getRuleId());

        if (condition.getParentCondition() != null) {
            // check if parent has already been copied
            ConditionDTO copiedParentCondition = copiedConditionsMap
                    .get(condition.getParentCondition().getId());
            if (copiedParentCondition == null) {
                // Recursively copy parent condition
                copiedParentCondition = copyCondition(
                        condition.getParentCondition(), copiedConditionsMap);

                copiedConditionsMap.put(condition.getParentCondition().getId(),
                        copiedParentCondition);
            }
            copiedCondition.setParentCondition(copiedParentCondition);
        }

        // Add copied condition to the already copied ones, in order
        // to reuse them if a parent condition is encountered
        // while traversing the list of conditions.
        copiedConditionsMap.put(condition.getId(), copiedCondition);

        return copiedCondition;
    }
}
