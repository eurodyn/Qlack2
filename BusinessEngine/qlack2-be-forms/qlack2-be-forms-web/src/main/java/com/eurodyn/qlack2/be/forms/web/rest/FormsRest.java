package com.eurodyn.qlack2.be.forms.web.rest;

import com.eurodyn.qlack2.be.forms.api.FormVersionsService;
import com.eurodyn.qlack2.be.forms.api.FormsService;
import com.eurodyn.qlack2.be.forms.api.dto.*;
import com.eurodyn.qlack2.be.forms.api.exception.QInvalidConditionHierarchyException;
import com.eurodyn.qlack2.be.forms.api.request.form.*;
import com.eurodyn.qlack2.be.forms.api.request.version.*;
import com.eurodyn.qlack2.be.forms.web.dto.FormDetailsRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.FormRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.FormVersionContentRDTO;
import com.eurodyn.qlack2.be.forms.web.dto.FormVersionRDTO;
import com.eurodyn.qlack2.be.forms.web.util.ConverterUtil;
import com.eurodyn.qlack2.be.forms.web.util.Utils;
import com.eurodyn.qlack2.fuse.fileupload.api.FileUpload;
import com.eurodyn.qlack2.fuse.fileupload.api.response.FileGetResponse;
import com.eurodyn.qlack2.util.validator.util.annotation.ValidateSingleArgument;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationAttribute;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrorType;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationErrors;
import com.eurodyn.qlack2.util.validator.util.errors.ValidationFieldErrors;
import com.eurodyn.qlack2.util.validator.util.exception.QValidationException;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.logging.Logger;

@Path("/forms")
public class FormsRest {
    private static final Logger LOGGER = Logger.getLogger(FormsRest.class
            .getName());

    @Context
    private HttpHeaders headers;

    private FormsService formsService;

    private FormVersionsService formVersionsService;

    private FileUpload fileUpload;

    /**
     * Retrieves the metadata of the form with the given id.
     *
     * @param formId
     * @return
     */
    @GET
    @Path("{formId}")
    @Produces(MediaType.APPLICATION_JSON)
    public FormDTO getForm(@PathParam("formId") String formId) {
        GetFormRequest req = new GetFormRequest();
        req.setFormId(formId);

        Utils.sign(req, headers);
        return formsService.getForm(req);
    }

    /**
     * Creates a new form.
     *
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument
    public String createForm(FormRDTO formRDTO) {
        GetFormIdByNameRequest formByNameRequest = new GetFormIdByNameRequest();
        formByNameRequest.setFormName(formRDTO.getName());
        formByNameRequest.setProjectId(formRDTO.getProjectId());

        Utils.sign(formByNameRequest, headers);
        String existingFormId = formsService.getFormIdByName(formByNameRequest);
        // A form with the given name already exists -> a validation exception
        // should be thrown
        if (existingFormId != null) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors("name");
            ValidationErrorType vet = new ValidationErrorType(
                    "validation.error.form.unique.name");
            vet.putAttribute(ValidationAttribute.Message,
                    "validation.error.form.unique.name");
            vet.putAttribute(ValidationAttribute.InvalidValue,
                    formRDTO.getName());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);
        }

        CreateFormRequest formRequest = ConverterUtil
                .formRDTOToCreateFormRequest(formRDTO);

        Utils.sign(formRequest, headers);
        return formsService.createForm(formRequest);
    }

    /**
     * Updates an existing form.
     *
     * @param formId
     * @param formDetailsRDTO
     */
    @PUT
    @Path("{formId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public void updateForm(@PathParam("formId") String formId,
                           FormDetailsRDTO formDetailsRDTO) {
        ValidationErrors vErrors = new ValidationErrors();

        // Validate form name
        GetFormIdByNameRequest formByNameRequest = new GetFormIdByNameRequest();
        formByNameRequest.setFormName(formDetailsRDTO.getName());
        formByNameRequest.setProjectId(formDetailsRDTO.getProjectId());

        Utils.sign(formByNameRequest, headers);
        String existingFormId = formsService.getFormIdByName(formByNameRequest);
        // A form with the given name already exists -> a validation exception
        // should be thrown
        if (existingFormId != null && !formId.equals(existingFormId)) {
            ValidationFieldErrors vfe = new ValidationFieldErrors("name");
            ValidationErrorType vet = new ValidationErrorType(
                    "validation.error.form.unique.name");
            vet.putAttribute(ValidationAttribute.Message,
                    "validation.error.form.unique.name");
            vet.putAttribute(ValidationAttribute.InvalidValue,
                    formDetailsRDTO.getName());
            vfe.addError(vet);
            vErrors.addValidationError(vfe);
        }

        // There is no need to validate form version name, since it cannot be
        // modified.

        // If there exist conditions, then validate them
        List<ConditionDTO> conditions = ConverterUtil
                .conditionRDTOsToConditionDTOList(formDetailsRDTO
                        .getVersionConditions());

        if (conditions != null && !conditions.isEmpty()) {
            validateFormVersionConditions(conditions, vErrors);
        }

        // If there exist translations, then validate them
        List<TranslationDTO> translations = ConverterUtil
                .translationRDTOsToTranslationDTOList(formDetailsRDTO
                        .getVersionTranslations());

        if (translations != null && !translations.isEmpty()) {
            validateFormVersionTranslations(translations, vErrors);
        }

        if (vErrors.getValidationErrors() != null
                && !vErrors.getValidationErrors().isEmpty()) {
            throw new QValidationException(vErrors);
        }

        // Update the form, form version, conditions and translations
        UpdateFormRequest formRequest = ConverterUtil
                .formDetailsRDTOToUpdateFormRequest(formDetailsRDTO);
        formRequest.setId(formId);
        formRequest.setVersionConditions(conditions);
        formRequest.setVersionTranslations(translations);

        Utils.sign(formRequest, headers);
        formsService.updateForm(formRequest);
    }

    /**
     * Validates the form version conditions. The following validations are
     * performed:
     * <ul>
     * <li>The condition name is unique for the given form version</li>
     * <li>If a condition has a follows after condition (parent), then it is
     * validated that the parent condition exists</li>
     * <li>Validate that there does not exist a cyclic dependency among the
     * conditions and their parents</li>
     * </ul>
     *
     * @param conditions
     * @param vErrors
     */
    private void validateFormVersionConditions(List<ConditionDTO> conditions,
                                               ValidationErrors vErrors) {
        // Holds the ids and the conditions
        Map<String, ConditionDTO> conditionsMap = new HashMap<>();
        for (ConditionDTO condition : conditions) {
            conditionsMap.put(condition.getId(), condition);
        }

        Set<String> conditionNames = new HashSet<>();
        int index = 0;
        for (ConditionDTO condition : conditions) {

            // Check that the condition name is unique
            boolean isAdded = conditionNames.add(condition.getName());

            if (!isAdded) {
                ValidationFieldErrors vfe = new ValidationFieldErrors(
                        "versionConditions[" + index + "].name");
                ValidationErrorType vet = new ValidationErrorType(
                        "validation.error.condition.unique.name");
                vet.putAttribute(ValidationAttribute.Message,
                        "validation.error.condition.unique.name");
                vet.putAttribute(ValidationAttribute.InvalidValue,
                        condition.getName());
                vfe.addError(vet);
                vErrors.addValidationError(vfe);
            }

            // Check that the parent condition exists and has not been removed
            if (condition.getParentCondition() != null) {
                if (!conditionsMap.containsKey(condition.getParentCondition()
                        .getId())) {
                    ValidationFieldErrors vfe = new ValidationFieldErrors(
                            "versionConditions[" + index + "].parentCondition");
                    ValidationErrorType vet = new ValidationErrorType(
                            "validation.error.condition.parent.not.exists");
                    vet.putAttribute(ValidationAttribute.Message,
                            "validation.error.condition.parent.not.exists");
                    vet.putAttribute(ValidationAttribute.InvalidValue,
                            condition.getParentCondition().getName());
                    vfe.addError(vet);
                    vErrors.addValidationError(vfe);
                }
            }

            index++;
        }

        // Check that there is no cyclic dependency in the conditions
        Map<String, ConditionDTO> conditionDTOs = new HashMap<>();
        for (ConditionDTO conditionDTO : conditions) {
            conditionDTOs.put(conditionDTO.getId(), conditionDTO);
        }

        ValidateFormVersionConditionsHierarchyRequest validateRequest = new ValidateFormVersionConditionsHierarchyRequest();
        validateRequest.setConditions(conditionDTOs);

        Utils.sign(validateRequest, headers);
        try {
            formVersionsService
                    .validateFormVersionConditionsHierarchy(validateRequest);
        } catch (QInvalidConditionHierarchyException e) {
            ValidationFieldErrors vfe = new ValidationFieldErrors(
                    "conditionCyclicDependency");
            ValidationErrorType vet = new ValidationErrorType(
                    "validation.error.condition.cyclic.dependency");
            vet.putAttribute(ValidationAttribute.Message,
                    "validation.error.condition.cyclic.dependency");
            vfe.addError(vet);
            vErrors.addValidationError(vfe);
        }
    }

    /**
     * Validates the form version translations. The following validations are
     * performed:
     * <ul>
     * <li>
     * The translation keys are unique for the given version.</li>
     * <li>
     * For each language only one translation value has been defined for each
     * translation key.</li>
     * </ul>
     *
     * @param translations
     * @param vErrors
     */
    private void validateFormVersionTranslations(
            List<TranslationDTO> translations, ValidationErrors vErrors) {

        Map<String, String> uniqueKey = new HashMap<>();
        Map<String, Set<String>> uniqueLang = new HashMap<>();

        int index = 0;
        for (TranslationDTO translation : translations) {
            // Validation for unique key name
            String keyId = uniqueKey.get(translation.getKey());
            if (keyId == null) {
                uniqueKey.put(translation.getKey(), translation.getKeyId());
            } else if (!keyId.equals(translation.getKeyId())) {
                ValidationFieldErrors vfe = new ValidationFieldErrors(
                        "versionTranslations[" + index + "].key");
                ValidationErrorType vet = new ValidationErrorType(
                        "validation.error.translation.unique.name");
                vet.putAttribute(ValidationAttribute.Message,
                        "validation.error.translation.unique.name");
                vet.putAttribute(ValidationAttribute.InvalidValue,
                        translation.getKey());
                vfe.addError(vet);
                vErrors.addValidationError(vfe);
            }

            // Validation that for each language only one translation value
            // should be defined for each translation key.
            Set<String> languages = uniqueLang.get(translation.getKeyId());
            if (languages == null) {
                languages = new HashSet<>();
                uniqueLang.put(translation.getKeyId(), languages);
            }

            boolean isAdded = languages.add(translation.getLanguage());
            if (!isAdded) {
                ValidationFieldErrors vfe = new ValidationFieldErrors(
                        "versionTranslations[" + index + "].value");
                ValidationErrorType vet = new ValidationErrorType(
                        "validation.error.translation.unique.language");
                vet.putAttribute(ValidationAttribute.Message,
                        "validation.error.translation.unique.language");
                vet.putAttribute(ValidationAttribute.InvalidValue,
                        translation.getLanguage());
                vfe.addError(vet);
                vErrors.addValidationError(vfe);
            }

            index++;
        }
    }

    /**
     * Deletes a form.
     *
     * @param formId
     */
    @DELETE
    @Path("{formId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteForm(@PathParam("formId") String formId) {
        DeleteFormRequest req = new DeleteFormRequest();
        req.setFormId(formId);

        Utils.sign(req, headers);
        formsService.deleteForm(req);
    }

    /**
     * Retrieves the list of form version for the given form id.
     *
     * @param formId
     * @return
     */
    @GET
    @Path("{formId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<FormVersionDTO> getFormVersions(
            @PathParam("formId") String formId) {
        GetFormVersionsRequest req = new GetFormVersionsRequest();
        req.setFormId(formId);

        Utils.sign(req, headers);
        return formVersionsService.getFormVersions(req);
    }

    /**
     * Creates a form version.
     *
     * @return
     */
    @POST
    @Path("{formId}/versions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String createFormVersion(@PathParam("formId") String formId,
                                    FormVersionRDTO formVersionRDTO) {

        // Validate form version name
        GetFormVersionIdByNameRequest formVersionByNameRequest = new GetFormVersionIdByNameRequest();
        formVersionByNameRequest.setFormVersionName(formVersionRDTO.getName());
        formVersionByNameRequest.setFormId(formId);

        Utils.sign(formVersionByNameRequest, headers);
        String existingFormVersionId = formVersionsService
                .getFormVersionIdByName(formVersionByNameRequest);
        // A form version with the given name already exists -> a validation
        // exception
        // should be thrown
        if (existingFormVersionId != null) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors("name");
            ValidationErrorType vet = new ValidationErrorType(
                    "validation.error.form.version.unique.name");
            vet.putAttribute(ValidationAttribute.Message,
                    "validation.error.form.version.unique.name");
            vet.putAttribute(ValidationAttribute.InvalidValue,
                    formVersionRDTO.getName());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);
        }

        CreateFormVersionRequest req = new CreateFormVersionRequest();
        req.setName(formVersionRDTO.getName());
        req.setDescription(formVersionRDTO.getDescription());
        req.setFormId(formId);
        req.setUseTemplateContent(true);

        if (formVersionRDTO.getBasedOn() != null) {
            GetFormVersionRequest formVersionRequest = new GetFormVersionRequest();
            formVersionRequest.setFormVersionId(formVersionRDTO.getBasedOn());

            Utils.sign(formVersionRequest, headers);
            FormVersionDetailsDTO baseVersion = formVersionsService
                    .getFormVersion(formVersionRequest);

            if (baseVersion != null) {
                if (baseVersion.getContent() != null
                        && StringUtils.isNotEmpty(baseVersion.getContent())) {
                    req.setContent(baseVersion.getContent());

                    // In case the base version already has content, use this
                    // instead of the orbeon template
                    req.setUseTemplateContent(false);
                }

                List<ConditionDTO> baseConditions = baseVersion.getConditions();

                // Copy conditions
                List<ConditionDTO> copiedConditions = copyConditions(baseConditions);
                req.setConditions(copiedConditions);

                // Copy translations
                req.setTranslations(baseVersion.getTranslations());
            }
        }

        Utils.sign(req, headers);
        return formVersionsService.createFormVersion(req);
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

    /**
     * This method creates a copy of a condition and associates it with a newly
     * created form version. All properties of the condition are copied to the
     * new one, except for the id and the dbVersion. When a condition has a
     * parent one, then the parent condition is copied and persisted recursively
     * and then associated with new copy of the condition.
     *
     * @param condition
     * @param copiedConditionsMap
     * @return
     */
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

    /**
     * Retrieves the number of form versions locked by another user.
     *
     * @param formId
     * @return
     */
    @GET
    @Path("{formId}/versions/countLockedByOtherUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Long countFormVersionsLockedByOtherUser(
            @PathParam("formId") String formId) {
        CountFormVersionsLockedByOtherUserRequest req = new CountFormVersionsLockedByOtherUserRequest();
        req.setFormId(formId);

        Utils.sign(req, headers);
        return formVersionsService.countFormVersionsLockedByOtherUser(req);
    }

    /**
     * Imports a form version.
     */
    @PUT
    @Path("{formId}/import-version")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String importFormVersion(@PathParam("formId") String formId,
                                    FormVersionContentRDTO formVersionContentRDTO) {
        FileGetResponse fileResponse = fileUpload.getByID(formVersionContentRDTO.getFile());

        // check if we have a .xml file
        String[] tokens = fileResponse.getFile().getFilename()
                .split("\\.(?=[^\\.]+$)");
        if (!tokens[1].equalsIgnoreCase("xml")) {
            ValidationErrors ve = new ValidationErrors();
            ValidationFieldErrors vfe = new ValidationFieldErrors(
                    "file");
            ValidationErrorType vet = new ValidationErrorType(
                    "validation.error.upload.xml");
            vet.putAttribute(ValidationAttribute.Message,
                    "validation.error.upload.xml");
            vet.putAttribute(ValidationAttribute.InvalidValue, fileResponse
                    .getFile().getFilename());
            vfe.addError(vet);
            ve.addValidationError(vfe);
            throw new QValidationException(ve);
        }

        ImportFormVersionRequest importRequest = new ImportFormVersionRequest();
        importRequest.setFormId(formId);
        importRequest.setContent(fileResponse.getFile().getFileData());

        Utils.sign(importRequest, headers);
        return formVersionsService.importFormVersion(importRequest);
    }

    public void setFormsService(FormsService formsService) {
        this.formsService = formsService;
    }

    public void setFormVersionsService(FormVersionsService formVersionsService) {
        this.formVersionsService = formVersionsService;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

}
