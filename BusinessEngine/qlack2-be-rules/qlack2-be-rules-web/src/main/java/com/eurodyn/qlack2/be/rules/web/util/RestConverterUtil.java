package com.eurodyn.qlack2.be.rules.web.util;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.rules.api.request.category.CreateCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.category.UpdateCategoryRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.CreateDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.UpdateDataModelRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.CreateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UpdateDataModelFieldRequest;
import com.eurodyn.qlack2.be.rules.api.request.datamodel.version.UpdateDataModelVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.CreateLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.UpdateLibraryRequest;
import com.eurodyn.qlack2.be.rules.api.request.library.version.UpdateLibraryVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.CreateRuleRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.UpdateRuleRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.CreateRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.rule.version.UpdateRuleVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.CreateWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.UpdateWorkingSetRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.CreateWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.api.request.workingset.version.UpdateWorkingSetVersionRequest;
import com.eurodyn.qlack2.be.rules.web.dto.CategoryRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelFieldRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelVersionCreateRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.DataModelVersionRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.LibraryRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.LibraryVersionRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.RuleRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.RuleVersionCreateRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.RuleVersionRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetVersionCreateRestDTO;
import com.eurodyn.qlack2.be.rules.web.dto.WorkingSetVersionRestDTO;

public class RestConverterUtil {

	public static final RestConverterUtil INSTANCE = new RestConverterUtil();

	private RestConverterUtil() {
	}

	// -- Categories

	public CreateCategoryRequest mapCreateCategory(CategoryRestDTO restDto) {
		CreateCategoryRequest request = new CreateCategoryRequest();

		request.setProjectId(restDto.getProjectId());
		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());

		return request;
	}

	public UpdateCategoryRequest mapUpdateCategory(CategoryRestDTO restDto) {
		UpdateCategoryRequest request = new UpdateCategoryRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());

		return request;
	}

	// -- Working Sets

	public CreateWorkingSetRequest mapCreateWorkingSet(WorkingSetRestDTO restDto) {
		CreateWorkingSetRequest request = new CreateWorkingSetRequest();

		request.setProjectId(restDto.getProjectId());
		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		return request;
	}

	public UpdateWorkingSetRequest mapUpdateWorkingSet(WorkingSetRestDTO restDto) {
		UpdateWorkingSetRequest request = new UpdateWorkingSetRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		WorkingSetVersionRestDTO versionRestDto = restDto.getVersion();
		if (versionRestDto != null) {
			UpdateWorkingSetVersionRequest versionRequest = mapUpdateWorkingSetVersion(versionRestDto);
			request.setVersionRequest(versionRequest);
		}

		return request;
	}

	public CreateWorkingSetVersionRequest mapCreateWorkingSetVersion(WorkingSetVersionCreateRestDTO restDto) {
		CreateWorkingSetVersionRequest request = new CreateWorkingSetVersionRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setBasedOnId(restDto.getBasedOnId());

		return request;
	}

	public UpdateWorkingSetVersionRequest mapUpdateWorkingSetVersion(WorkingSetVersionRestDTO restDto) {
		UpdateWorkingSetVersionRequest request = new UpdateWorkingSetVersionRequest();

		request.setId(restDto.getId());
		request.setDescription(restDto.getDescription());

		request.setRuleVersionIds(restDto.getRuleVersionIds());
		request.setDataModelVersionIds(restDto.getDataModelVersionIds());
		request.setLibraryVersionIds(restDto.getLibraryVersionIds());

		return request;
	}

	// -- Rules

	public CreateRuleRequest mapCreateRule(RuleRestDTO restDto) {
		CreateRuleRequest request = new CreateRuleRequest();

		request.setProjectId(restDto.getProjectId());
		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		return request;
	}

	public UpdateRuleRequest mapUpdateRule(RuleRestDTO restDto) {
		UpdateRuleRequest request = new UpdateRuleRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		RuleVersionRestDTO versionRestDto = restDto.getVersion();
		if (versionRestDto != null) {
			UpdateRuleVersionRequest versionRequest = mapUpdateRuleVersion(versionRestDto);
			request.setVersionRequest(versionRequest);
		}

		return request;
	}

	public CreateRuleVersionRequest mapCreateRuleVersion(RuleVersionCreateRestDTO restDto) {
		CreateRuleVersionRequest request = new CreateRuleVersionRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setBasedOnId(restDto.getBasedOnId());

		return request;
	}

	public UpdateRuleVersionRequest mapUpdateRuleVersion(RuleVersionRestDTO restDto) {
		UpdateRuleVersionRequest request = new UpdateRuleVersionRequest();

		request.setId(restDto.getId());
		request.setDescription(restDto.getDescription());
		request.setContent(restDto.getContent());

		return request;
	}

	// -- Data Models

	public CreateDataModelRequest mapCreateDataModel(DataModelRestDTO restDto) {
		CreateDataModelRequest request = new CreateDataModelRequest();

		request.setProjectId(restDto.getProjectId());
		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		return request;
	}

	public UpdateDataModelRequest mapUpdateDataModel(DataModelRestDTO restDto) {
		UpdateDataModelRequest request = new UpdateDataModelRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		DataModelVersionRestDTO versionRestDto = restDto.getVersion();
		if (versionRestDto != null) {
			UpdateDataModelVersionRequest versionRequest = mapUpdateDataModelVersion(versionRestDto);
			request.setVersionRequest(versionRequest);
		}

		return request;
	}

	public CreateDataModelVersionRequest mapCreateDataModelVersion(DataModelVersionCreateRestDTO restDto) {
		CreateDataModelVersionRequest request = new CreateDataModelVersionRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setBasedOnId(restDto.getBasedOnId());

		return request;
	}

	public UpdateDataModelVersionRequest mapUpdateDataModelVersion(DataModelVersionRestDTO restDto) {
		UpdateDataModelVersionRequest request = new UpdateDataModelVersionRequest();

		request.setId(restDto.getId());
		request.setDescription(restDto.getDescription());
		request.setModelPackage(restDto.getModelPackage());
		request.setParentModelVersionId(restDto.getParentModelVersionId());

		request.setFieldRequests(mapUpdateDataModelFieldList(restDto.getFields()));

		return request;
	}

	public UpdateDataModelFieldRequest mapUpdateDataModelField(DataModelFieldRestDTO restDto) {
		UpdateDataModelFieldRequest request = new UpdateDataModelFieldRequest();

		request.setId(restDto.getId());
		request.setName(restDto.getName());
		request.setFieldTypeId(restDto.getFieldTypeId());
		request.setFieldTypeVersionId(restDto.getFieldTypeVersionId());

		return request;
	}

	public List<UpdateDataModelFieldRequest> mapUpdateDataModelFieldList(List<DataModelFieldRestDTO> restDtos) {
		List<UpdateDataModelFieldRequest> requests = new ArrayList<>();
		for (DataModelFieldRestDTO restDto : restDtos) {
			UpdateDataModelFieldRequest request = mapUpdateDataModelField(restDto);
			requests.add(request);
		}
		return requests;
	}

	// -- Libraries

	public CreateLibraryRequest mapCreateLibrary(LibraryRestDTO restDto) {
		CreateLibraryRequest request = new CreateLibraryRequest();

		request.setProjectId(restDto.getProjectId());
		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		return request;
	}

	public UpdateLibraryRequest mapUpdateLibrary(LibraryRestDTO restDto) {
		UpdateLibraryRequest request = new UpdateLibraryRequest();

		request.setName(restDto.getName());
		request.setDescription(restDto.getDescription());
		request.setActive(restDto.isActive());
		request.setCategoryIds(restDto.getCategoryIds());

		LibraryVersionRestDTO versionRestDto = restDto.getVersion();
		if (versionRestDto != null) {
			UpdateLibraryVersionRequest versionRequest = mapUpdateLibraryVersion(versionRestDto);
			request.setVersionRequest(versionRequest);
		}

		return request;
	}

	public UpdateLibraryVersionRequest mapUpdateLibraryVersion(LibraryVersionRestDTO restDto) {
		UpdateLibraryVersionRequest request = new UpdateLibraryVersionRequest();

		request.setId(restDto.getId());
		request.setDescription(restDto.getDescription());

		return request;
	}

}
