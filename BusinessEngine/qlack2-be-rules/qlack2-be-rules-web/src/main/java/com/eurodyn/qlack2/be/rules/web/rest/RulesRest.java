package com.eurodyn.qlack2.be.rules.web.rest;

import com.eurodyn.qlack2.be.rules.api.RulesService;
import com.eurodyn.qlack2.be.rules.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.can.CanDeleteRuleResult;
import com.eurodyn.qlack2.be.rules.api.request.rule.*;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.*;
import com.eurodyn.qlack2.be.rules.web.dto.RuleRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.RuleVersionCreateRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.RuleVersionImportRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.RuleVersionRestDTO;
import com.eurodyn.qlack2.be.rules.web.util.RestConverterUtil;
import com.eurodyn.qlack2.be.rules.web.util.RuleUtils;
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

@Path("/rules")
public class RulesRest {

    @Context
    private HttpHeaders headers;

    private RulesService rulesService;

    private RestConverterUtil mapper = RestConverterUtil.INSTANCE;

    private FileUpload fileUpload;

    public void setRulesService(RulesService rulesService) {
        this.rulesService = rulesService;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    // -- Rules

    @GET
    @Path("{ruleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public RuleDTO getRule(@PathParam("ruleId") String ruleId) {

        GetRuleRequest request = new GetRuleRequest();
        request.setId(ruleId);

        return rulesService.getRule(Utils.sign(request, headers));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument
    public String createRule(RuleRestDTO ruleRestDto) {

        checkUniqueRuleNameOnCreate(ruleRestDto.getProjectId(), ruleRestDto.getName());

        CreateRuleRequest request = mapper.mapCreateRule(ruleRestDto);

        return rulesService.createRule(Utils.sign(request, headers));
    }

    @PUT
    @Path("{ruleId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public void updateRule(@PathParam("ruleId") String ruleId, RuleRestDTO ruleRestDto) {

        checkUniqueRuleNameOnUpdate(ruleId, ruleRestDto.getName());

        checkDroolsRuleName(ruleId, ruleRestDto);

        UpdateRuleRequest request = mapper.mapUpdateRule(ruleRestDto);
        request.setId(ruleId);
        Utils.sign(request, headers);

        rulesService.updateRule(request);
    }

    @DELETE
    @Path("{ruleId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteRule(@PathParam("ruleId") String ruleId) {

        DeleteRuleRequest request = new DeleteRuleRequest();
        request.setId(ruleId);

        rulesService.deleteRule(Utils.sign(request, headers));
    }

    @GET
    @Path("{ruleId}/canDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public CanDeleteRuleResult canDelete(@PathParam("ruleId") String ruleId) {

        DeleteRuleRequest request = new DeleteRuleRequest();
        request.setId(ruleId);

        return rulesService.canDeleteRule(Utils.sign(request, headers));
    }

    // -- Rule versions

    @GET
    @Path("{ruleId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RuleVersionDTO> getRuleVersions(@PathParam("ruleId") String ruleId) {

        GetRuleVersionsRequest request = new GetRuleVersionsRequest();
        request.setId(ruleId);
        Utils.sign(request, headers);

        return rulesService.getRuleVersions(request);
    }

    @POST
    @Path("{ruleId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String createRuleVersion(@PathParam("ruleId") String ruleId, RuleVersionCreateRestDTO versionRestDto) {

        checkUniqueRuleVersionNameOnCreate(ruleId, versionRestDto.getName());

        CreateRuleVersionRequest request = mapper.mapCreateRuleVersion(versionRestDto);
        request.setRuleId(ruleId);

        return rulesService.createRuleVersion(Utils.sign(request, headers));
    }

    @PUT
    @Path("{ruleId}/import-version")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ValidateSingleArgument(requestIndex = 1)
    public String importRuleVersion(@PathParam("ruleId") String ruleId, RuleVersionImportRestDTO versionRestDto) {
        FileGetResponse fileResponse = fileUpload.getByID(versionRestDto.getFile());
        DBFileDTO fileDto = fileResponse.getFile();

        checkFileExtensionIsXml(fileDto);

        ImportRuleVersionRequest request = new ImportRuleVersionRequest();
        request.setRuleId(ruleId);
        request.setXml(fileDto.getFileData());
        Utils.sign(request, headers);

        return rulesService.importRuleVersion(request);
    }

    // -- Helpers

    private void checkUniqueRuleNameOnCreate(String projectId, String name) throws QValidationException {
        GetRuleByProjectAndNameRequest request = new GetRuleByProjectAndNameRequest();
        request.setProjectId(projectId);
        request.setName(name);
        Utils.sign(request, headers);

        RuleDTO existingRule = rulesService.getRuleByProjectAndName(request);
        if (existingRule != null) {
            throw new QValidationException(createUniqueRuleNameError(name));
        }
    }

    private void checkUniqueRuleNameOnUpdate(String ruleId, String name) throws QValidationException {
        GetRuleRequest currentRequest = new GetRuleRequest();
        currentRequest.setId(ruleId);
        Utils.sign(currentRequest, headers);

        // ... or just query for projectId
        RuleDTO currentRule = rulesService.getRule(currentRequest);
        if (currentRule != null) {
            String projectId = currentRule.getProjectId();

            GetRuleByProjectAndNameRequest request = new GetRuleByProjectAndNameRequest();
            request.setProjectId(projectId);
            request.setName(name);
            Utils.sign(request, headers);

            RuleDTO existingRule = rulesService.getRuleByProjectAndName(request);
            if (existingRule != null) {
                if (!existingRule.getId().equals(ruleId)) {
                    throw new QValidationException(createUniqueRuleNameError(name));
                }
            }
        }
    }

    private void checkUniqueRuleVersionNameOnCreate(String ruleId, String versionName) throws QValidationException {
        GetRuleVersionByRuleAndNameRequest request = new GetRuleVersionByRuleAndNameRequest();
        request.setRuleId(ruleId);
        request.setName(versionName);

        RuleVersionDTO existingVersion = rulesService.getRuleVersionByRuleAndName(Utils.sign(request, headers));
        if (existingVersion != null) {
            throw new QValidationException(createUniqueRuleVersionNameError(versionName));
        }
    }

    private static ValidationErrors createUniqueRuleNameError(String name) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("name");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.InvalidValue, name);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createUniqueRuleVersionNameError(String versionName) {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("name");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.UniqueName");
        vet.putAttribute(ValidationAttribute.InvalidValue, versionName);
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private void checkDroolsRuleName(String ruleId, RuleRestDTO ruleRestDto) {
        RuleVersionRestDTO ruleVersionRestDto = ruleRestDto.getVersion();
        if (ruleVersionRestDto != null) {
            String drl = ruleVersionRestDto.getContent();
            List<String> droolsRuleNames = RuleUtils.findRuleNames(drl);

            if (droolsRuleNames.isEmpty()) {
                throw new QValidationException(createNoDroolsRuleError());
            }

            if (droolsRuleNames.size() > 1) {
                throw new QValidationException(createMultipleDroolsRulesError());
            }

            String ruleName = droolsRuleNames.get(0);

            CountMatchingRuleNameRequest matchingRequest = new CountMatchingRuleNameRequest();
            matchingRequest.setRuleId(ruleId);
            matchingRequest.setDroolsRuleName(ruleName);
            Utils.sign(matchingRequest, headers);

            long matches = rulesService.countMatchingRuleName(matchingRequest);
            if (matches > 0) {
                throw new QValidationException(createNonUniqueDroolsRuleNameError());
            }
        }
    }

    private static ValidationErrors createNoDroolsRuleError() {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("version.content");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.rules.NoDroolsRule");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.rules.NoDroolsRule");
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createMultipleDroolsRulesError() {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("version.content");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.rules.MultipleDroolsRules");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.rules.MultipleDroolsRules");
        vfe.addError(vet);

        ve.addValidationError(vfe);
        return ve;
    }

    private static ValidationErrors createNonUniqueDroolsRuleNameError() {
        ValidationErrors ve = new ValidationErrors();

        ValidationFieldErrors vfe = new ValidationFieldErrors("version.content");
        ValidationErrorType vet = new ValidationErrorType("qlack.validation.rules.NonUniqueDroolsRuleName");
        vet.putAttribute(ValidationAttribute.Message, "qlack.validation.rules.NonUniqueDroolsRuleName");
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
