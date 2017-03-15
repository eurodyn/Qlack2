package com.eurodyn.qlack2.be.rules.impl.util;

import java.util.ArrayList;
import java.util.List;

import com.eurodyn.qlack2.be.explorer.api.dto.ProjectDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditCategoryDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditDataModelDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditDataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditLibraryDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditLibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditProjectDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuleDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditRuleVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditWorkingSetDTO;
import com.eurodyn.qlack2.be.rules.impl.dto.AuditWorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;

public class AuditConverterUtil {

	// -- Projects

	public AuditProjectDTO mapProject(ProjectDTO project) {
		AuditProjectDTO projectDto = new AuditProjectDTO();
		projectDto.setId(project.getId());
		projectDto.setName(project.getName());

		return projectDto;
	}

	// -- Categories

	public AuditCategoryDTO mapCategory(Category category) {
		AuditCategoryDTO categoryDto = new AuditCategoryDTO();
		categoryDto.setId(category.getId());
		categoryDto.setName(category.getName());
		categoryDto.setDescription(category.getDescription());

		return categoryDto;
	}

	public List<AuditCategoryDTO> mapCategoryList(List<Category> categories) {
		List<AuditCategoryDTO> categoryDtos = new ArrayList<>();
		for (Category category : categories) {
			categoryDtos.add(mapCategory(category));
		}
		return categoryDtos;
	}

	public List<String> mapCategoryIdList(List<Category> categories) {
		List<String> categoryIds = new ArrayList<>();
		for (Category category : categories) {
			categoryIds.add(category.getId());
		}
		return categoryIds;
	}

	// -- Working sets

	public AuditWorkingSetDTO mapWorkingSet(WorkingSet set) {
		AuditWorkingSetDTO setDto = new AuditWorkingSetDTO();
		setDto.setId(set.getId());
		setDto.setName(set.getName());
		setDto.setDescription(set.getDescription());
		setDto.setActive(set.isActive());

		setDto.setCategoryIds(mapCategoryIdList(set.getCategories()));
		return setDto;
	}

	public AuditWorkingSetVersionDTO mapWorkingSetVersion(WorkingSetVersion version) {
		AuditWorkingSetVersionDTO versionDto = new AuditWorkingSetVersionDTO();
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		versionDto.setState(version.getState().ordinal());
		versionDto.setLocked(version.getLockedOn() != null);

		return versionDto;
	}

	public List<AuditWorkingSetVersionDTO> mapWorkingSetVersionList(List<WorkingSetVersion> versions) {
		List<AuditWorkingSetVersionDTO> versionDtos = new ArrayList<>();
		for (WorkingSetVersion version : versions) {
			versionDtos.add(mapWorkingSetVersion(version));
		}
		return versionDtos;
	}

	// -- Rules

	public AuditRuleDTO mapRule(Rule rule) {
		AuditRuleDTO ruleDto = new AuditRuleDTO();
		ruleDto.setId(rule.getId());
		ruleDto.setName(rule.getName());
		ruleDto.setDescription(rule.getDescription());
		ruleDto.setActive(rule.isActive());

		ruleDto.setCategoryIds(mapCategoryIdList(rule.getCategories()));
		return ruleDto;
	}

	public AuditRuleVersionDTO mapRuleVersion(RuleVersion version) {
		AuditRuleVersionDTO versionDto = new AuditRuleVersionDTO();
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		versionDto.setState(version.getState().ordinal());
		versionDto.setLocked(version.getLockedOn() != null);

		return versionDto;
	}

	public List<AuditRuleVersionDTO> mapRuleVersionList(List<RuleVersion> versions) {
		List<AuditRuleVersionDTO> versionDtos = new ArrayList<>();
		for (RuleVersion version : versions) {
			versionDtos.add(mapRuleVersion(version));
		}
		return versionDtos;
	}

	public List<String> mapRuleVersionIdList(List<RuleVersion> versions) {
		List<String> ids = new ArrayList<>();
		for (RuleVersion version : versions) {
			ids.add(version.getId());
		}
		return ids;
	}

	// -- Data models

	public AuditDataModelDTO mapDataModel(DataModel model) {
		AuditDataModelDTO modelDto = new AuditDataModelDTO();
		modelDto.setId(model.getId());
		modelDto.setName(model.getName());
		modelDto.setDescription(model.getDescription());
		modelDto.setActive(model.isActive());

		modelDto.setCategoryIds(mapCategoryIdList(model.getCategories()));
		return modelDto;
	}

	public AuditDataModelVersionDTO mapDataModelVersion(DataModelVersion version) {
		AuditDataModelVersionDTO versionDto = new AuditDataModelVersionDTO();
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		versionDto.setModelPackage(version.getModelPackage());

		DataModelVersion parentModelVersion = version.getParentModel();
		if (parentModelVersion != null) {
			versionDto.setParentModelId(parentModelVersion.getDataModel().getId());
			versionDto.setParentModelName(parentModelVersion.getDataModel().getName());

			versionDto.setParentModelVersionId(parentModelVersion.getId());
			versionDto.setParentModelVersionName(parentModelVersion.getName());
		}

		versionDto.setState(version.getState().ordinal());
		versionDto.setLocked(version.getLockedOn() != null);

		return versionDto;
	}

	public List<AuditDataModelVersionDTO> mapDataModelVersionList(List<DataModelVersion> versions) {
		List<AuditDataModelVersionDTO> versionDtos = new ArrayList<>();
		for (DataModelVersion version : versions) {
			versionDtos.add(mapDataModelVersion(version));
		}
		return versionDtos;
	}

	public List<String> mapDataModelVersionIdList(List<DataModelVersion> versions) {
		List<String> ids = new ArrayList<>();
		for (DataModelVersion version : versions) {
			ids.add(version.getId());
		}
		return ids;
	}

	// -- Libraries

	public AuditLibraryDTO mapLibrary(Library library) {
		AuditLibraryDTO libraryDto = new AuditLibraryDTO();
		libraryDto.setId(library.getId());
		libraryDto.setName(library.getName());
		libraryDto.setDescription(library.getDescription());
		libraryDto.setActive(library.isActive());

		libraryDto.setCategoryIds(mapCategoryIdList(library.getCategories()));
		return libraryDto;
	}

	public AuditLibraryVersionDTO mapLibraryVersion(LibraryVersion version) {
		AuditLibraryVersionDTO versionDto = new AuditLibraryVersionDTO();
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		versionDto.setState(version.getState().ordinal());
		versionDto.setLocked(version.getLockedOn() != null);

		return versionDto;
	}

	public List<AuditLibraryVersionDTO> mapLibraryVersionList(List<LibraryVersion> versions) {
		List<AuditLibraryVersionDTO> versionDtos = new ArrayList<>();
		for (LibraryVersion version : versions) {
			versionDtos.add(mapLibraryVersion(version));
		}
		return versionDtos;
	}

	public List<String> mapLibraryVersionIdList(List<LibraryVersion> versions) {
		List<String> ids = new ArrayList<>();
		for (LibraryVersion version : versions) {
			ids.add(version.getId());
		}
		return ids;
	}

}
