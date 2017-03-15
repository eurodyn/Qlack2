package com.eurodyn.qlack2.be.rules.impl.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.eurodyn.qlack2.be.rules.api.dto.CategoryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldDTO;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldType;
import com.eurodyn.qlack2.be.rules.api.dto.DataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryDTO;
import com.eurodyn.qlack2.be.rules.api.dto.LibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.RuleVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.WorkingSetVersionIdentifierDTO;
import com.eurodyn.qlack2.be.rules.impl.model.Category;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelField;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSet;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.DesktopUserService;
import com.eurodyn.qlack2.webdesktop.api.dto.UserDTO;
import com.eurodyn.qlack2.webdesktop.api.request.user.GetUserUncheckedRequest;

public class ConverterUtil /* RuleMapper */ {

	private DesktopUserService desktopUserService;

	public void setDesktopUserService(DesktopUserService desktopUserService) {
		this.desktopUserService = desktopUserService;
	}

	private UserDTO user(String userId) {
		if (userId != null) {
			GetUserUncheckedRequest request = new GetUserUncheckedRequest(userId, false);
			return desktopUserService.getUserUnchecked(request);
		}
		else {
			return null;
		}
	}

	// -- Categories

	public CategoryDTO mapCategory(Category category, SignedTicket ticket) {
		CategoryDTO categoryDto = new CategoryDTO();
		categoryDto.setProjectId(category.getProjectId());
		categoryDto.setId(category.getId());
		categoryDto.setName(category.getName());
		categoryDto.setDescription(category.getDescription());

		categoryDto.setCreatedOn(category.getCreatedOn());
		categoryDto.setCreatedBy(user(category.getCreatedBy()));
		categoryDto.setLastModifiedOn(category.getLastModifiedOn());
		categoryDto.setLastModifiedBy(user(category.getLastModifiedBy()));

		return categoryDto;
	}

	public List<CategoryDTO> mapCategoryList(List<Category> categories, SignedTicket ticket) {
		List<CategoryDTO> categoryDtos = new ArrayList<>();
		for (Category category : categories) {
			categoryDtos.add(mapCategory(category, ticket));
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

	// -- Working Sets

	public WorkingSetDTO mapWorkingSetSummary(WorkingSet set) {
		WorkingSetDTO setDto = new WorkingSetDTO();
		setDto.setProjectId(set.getProjectId());
		setDto.setId(set.getId());
		setDto.setName(set.getName());

		return setDto;
	}

	public WorkingSetDTO mapWorkingSet(WorkingSet set, SignedTicket ticket) {
		WorkingSetDTO setDto = new WorkingSetDTO();
		setDto.setProjectId(set.getProjectId());
		setDto.setId(set.getId());
		setDto.setName(set.getName());
		setDto.setDescription(set.getDescription());
		setDto.setActive(set.isActive());

		setDto.setCreatedOn(set.getCreatedOn());
		setDto.setCreatedBy(user(set.getCreatedBy()));
		setDto.setLastModifiedOn(set.getLastModifiedOn());
		setDto.setLastModifiedBy(user(set.getLastModifiedBy()));

		setDto.setCategoryIds(mapCategoryIdList(set.getCategories()));
		return setDto;
	}

	public List<WorkingSetDTO> mapWorkingSetList(List<WorkingSet> sets, SignedTicket ticket) {
		List<WorkingSetDTO> setDtos = new ArrayList<>();
		for (WorkingSet set : sets) {
			setDtos.add(mapWorkingSet(set, ticket));
		}
		return setDtos;
	}

	public WorkingSetVersionIdentifierDTO mapWorkingSetVersionIdentifier(WorkingSetVersion version) {
		WorkingSetVersionIdentifierDTO versionDto = new WorkingSetVersionIdentifierDTO();

		WorkingSet workingSet = version.getWorkingSet();
		versionDto.setWorkingSetId(workingSet.getId());
		versionDto.setWorkingSetName(workingSet.getName());

		versionDto.setId(version.getId());
		versionDto.setName(version.getName());

		return versionDto;
	}

	public WorkingSetVersionDTO mapWorkingSetVersionSummary(WorkingSetVersion version, SignedTicket ticket) {
		WorkingSetVersionDTO versionDto = new WorkingSetVersionDTO();
		versionDto.setWorkingSetId(version.getWorkingSet().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());

		versionDto.setState(version.getState());
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));
		return versionDto;
	}

	public List<WorkingSetVersionDTO> mapWorkingSetVersionSummaryList(List<WorkingSetVersion> versions, SignedTicket ticket) {
		List<WorkingSetVersionDTO> versionDtos = new ArrayList<>();
		for (WorkingSetVersion version : versions) {
			versionDtos.add(mapWorkingSetVersionSummary(version, ticket));
		}
		return versionDtos;
	}

	public WorkingSetVersionDTO mapWorkingSetVersion(WorkingSetVersion version, SignedTicket ticket) {
		WorkingSetVersionDTO versionDto = new WorkingSetVersionDTO();
		versionDto.setWorkingSetId(version.getWorkingSet().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		versionDto.setState(version.getState());
		versionDto.setCreatedOn(version.getCreatedOn());
		versionDto.setCreatedBy(user(version.getCreatedBy()));
		versionDto.setLastModifiedOn(version.getLastModifiedOn());
		versionDto.setLastModifiedBy(user(version.getLastModifiedBy()));
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));
		return versionDto;
	}

	public List<WorkingSetVersionDTO> mapWorkingSetVersionList(List<WorkingSetVersion> versions, SignedTicket ticket) {
		List<WorkingSetVersionDTO> versionDtos = new ArrayList<>();
		for (WorkingSetVersion version : versions) {
			versionDtos.add(mapWorkingSetVersion(version, ticket));
		}
		return versionDtos;
	}

	// -- Rules

	public RuleDTO mapRuleSummary(Rule rule) {
		RuleDTO ruleDto = new RuleDTO();
		ruleDto.setProjectId(rule.getProjectId());
		ruleDto.setId(rule.getId());
		ruleDto.setName(rule.getName());

		return ruleDto;
	}

	public RuleDTO mapRule(Rule rule, SignedTicket ticket) {
		RuleDTO ruleDto = new RuleDTO();
		ruleDto.setProjectId(rule.getProjectId());
		ruleDto.setId(rule.getId());
		ruleDto.setName(rule.getName());
		ruleDto.setDescription(rule.getDescription());
		ruleDto.setActive(rule.isActive());

		ruleDto.setCreatedOn(rule.getCreatedOn());
		ruleDto.setCreatedBy(user(rule.getCreatedBy()));
		ruleDto.setLastModifiedOn(rule.getLastModifiedOn());
		ruleDto.setLastModifiedBy(user(rule.getLastModifiedBy()));

		ruleDto.setCategoryIds(mapCategoryIdList(rule.getCategories()));
		return ruleDto;
	}

	public List<RuleDTO> mapRuleList(List<Rule> rules, SignedTicket ticket) {
		List<RuleDTO> ruleDtos = new ArrayList<>();
		for (Rule rule : rules) {
			ruleDtos.add(mapRule(rule, ticket));
		}
		return ruleDtos;
	}

	public RuleVersionIdentifierDTO mapRuleVersionIdentifier(RuleVersion version) {
		RuleVersionIdentifierDTO versionDto = new RuleVersionIdentifierDTO();

		Rule rule = version.getRule();
		versionDto.setRuleId(rule.getId());
		versionDto.setRuleName(rule.getName());

		versionDto.setId(version.getId());
		versionDto.setName(version.getName());

		return versionDto;
	}

	public RuleVersionDTO mapRuleVersionSummary(RuleVersion version, SignedTicket ticket) {
		RuleVersionDTO versionDto = new RuleVersionDTO();
		versionDto.setRuleId(version.getRule().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());

		versionDto.setState(version.getState());
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));
		return versionDto;
	}

	public List<RuleVersionDTO> mapRuleVersionSummaryList(List<RuleVersion> versions, SignedTicket ticket) {
		List<RuleVersionDTO> versionDtos = new ArrayList<>();
		for (RuleVersion version : versions) {
			versionDtos.add(mapRuleVersionSummary(version, ticket));
		}
		return versionDtos;
	}

	public RuleVersionDTO mapRuleVersion(RuleVersion version, SignedTicket ticket) {
		RuleVersionDTO versionDto = new RuleVersionDTO();
		versionDto.setRuleId(version.getRule().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		versionDto.setContent(version.getContent()); // potentially big

		versionDto.setState(version.getState());
		versionDto.setCreatedOn(version.getCreatedOn());
		versionDto.setCreatedBy(user(version.getCreatedBy()));
		versionDto.setLastModifiedOn(version.getLastModifiedOn());
		versionDto.setLastModifiedBy(user(version.getLastModifiedBy()));
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));
		return versionDto;
	}

	public List<RuleVersionDTO> mapRuleVersionList(List<RuleVersion> versions, SignedTicket ticket) {
		List<RuleVersionDTO> versionDtos = new ArrayList<>();
		for (RuleVersion version : versions) {
			versionDtos.add(mapRuleVersion(version, ticket));
		}
		return versionDtos;
	}

	// -- Data models

	public DataModelDTO mapDataModelSummary(DataModel model) {
		DataModelDTO modelDto = new DataModelDTO();
		modelDto.setProjectId(model.getProjectId());
		modelDto.setId(model.getId());
		modelDto.setName(model.getName());

		return modelDto;
	}

	public DataModelDTO mapDataModel(DataModel model, SignedTicket ticket) {
		DataModelDTO modelDto = new DataModelDTO();
		modelDto.setProjectId(model.getProjectId());
		modelDto.setId(model.getId());
		modelDto.setName(model.getName());
		modelDto.setDescription(model.getDescription());
		modelDto.setActive(model.isActive());

		modelDto.setCreatedOn(model.getCreatedOn());
		modelDto.setCreatedBy(user(model.getCreatedBy()));
		modelDto.setLastModifiedOn(model.getLastModifiedOn());
		modelDto.setLastModifiedBy(user(model.getLastModifiedBy()));

		modelDto.setCategoryIds(mapCategoryIdList(model.getCategories()));
		return modelDto;
	}

	public List<DataModelDTO> mapDataModelList(List<DataModel> models, SignedTicket ticket) {
		List<DataModelDTO> modelDtos = new ArrayList<>();
		for (DataModel model : models) {
			modelDtos.add(mapDataModel(model, ticket));
		}
		return modelDtos;
	}

	public DataModelVersionDTO mapDataModelVersionSummary(DataModelVersion version, SignedTicket ticket) {
		DataModelVersionDTO versionDto = new DataModelVersionDTO();
		versionDto.setDataModelId(version.getDataModel().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());

		versionDto.setState(version.getState());
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));
		return versionDto;
	}

	public List<DataModelVersionDTO> mapDataModelVersionSummaryList(List<DataModelVersion> versions, SignedTicket ticket) {
		List<DataModelVersionDTO> versionDtos = new ArrayList<>();
		for (DataModelVersion version : versions) {
			versionDtos.add(mapDataModelVersionSummary(version, ticket));
		}
		return versionDtos;
	}

	public DataModelVersionDTO mapDataModelVersion(DataModelVersion version, SignedTicket ticket) {
		DataModelVersionDTO versionDto = new DataModelVersionDTO();
		versionDto.setDataModelId(version.getDataModel().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		versionDto.setModelPackage(version.getModelPackage());

		DataModelVersion parentModelVersion = version.getParentModel();
		if (parentModelVersion != null) {
			versionDto.setParentModelId(parentModelVersion.getDataModel().getId());
			versionDto.setParentModelVersionId(parentModelVersion.getId());
		}

		versionDto.setState(version.getState());
		versionDto.setCreatedOn(version.getCreatedOn());
		versionDto.setCreatedBy(user(version.getCreatedBy()));
		versionDto.setLastModifiedOn(version.getLastModifiedOn());
		versionDto.setLastModifiedBy(user(version.getLastModifiedBy()));
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));
		return versionDto;
	}

	public List<DataModelVersionDTO> mapDataModelVersionList(List<DataModelVersion> versions, SignedTicket ticket) {
		List<DataModelVersionDTO> versionDtos = new ArrayList<>();
		for (DataModelVersion version : versions) {
			versionDtos.add(mapDataModelVersion(version, ticket));
		}
		return versionDtos;
	}

	public DataModelFieldDTO mapDataModelField(DataModelField field) {
		DataModelFieldDTO fieldDto = new DataModelFieldDTO();
		fieldDto.setId(field.getId());
		fieldDto.setName(field.getName());

		DataModelFieldType primitiveType = field.getFieldPrimitiveType();
		if (primitiveType != null) {
			fieldDto.setFieldTypeId(String.valueOf(primitiveType.ordinal()));
			fieldDto.setFieldTypeName(primitiveType.name());
		}

		DataModelVersion modelTypeVersion = field.getFieldModelType();
		if (modelTypeVersion != null) {
			DataModel modelType = modelTypeVersion.getDataModel();
			fieldDto.setFieldTypeId(modelType.getId());
			fieldDto.setFieldTypeName(modelType.getName());

			fieldDto.setFieldTypeVersionId(modelTypeVersion.getId());
			fieldDto.setFieldTypeVersionName(modelTypeVersion.getName());
		}

		return fieldDto;
	}

	public List<DataModelFieldDTO> mapDataModelFieldList(List<DataModelField> fields) {
		List<DataModelFieldDTO> fieldDtos = new ArrayList<>();
		for (DataModelField field : fields) {
			fieldDtos.add(mapDataModelField(field));
		}
		return fieldDtos;
	}

	// -- Libraries

	public LibraryDTO mapLibrarySummary(Library library) {
		LibraryDTO libraryDto = new LibraryDTO();
		libraryDto.setProjectId(library.getProjectId());
		libraryDto.setId(library.getId());
		libraryDto.setName(library.getName());

		return libraryDto;
	}

	public LibraryDTO mapLibrary(Library library, SignedTicket ticket) {
		LibraryDTO libraryDto = new LibraryDTO();
		libraryDto.setProjectId(library.getProjectId());
		libraryDto.setId(library.getId());
		libraryDto.setName(library.getName());
		libraryDto.setDescription(library.getDescription());
		libraryDto.setActive(library.isActive());

		libraryDto.setCreatedOn(library.getCreatedOn());
		libraryDto.setCreatedBy(user(library.getCreatedBy()));
		libraryDto.setLastModifiedOn(library.getLastModifiedOn());
		libraryDto.setLastModifiedBy(user(library.getLastModifiedBy()));

		libraryDto.setCategoryIds(mapCategoryIdList(library.getCategories()));
		return libraryDto;
	}

	public List<LibraryDTO> mapLibraryList(List<Library> libraries, SignedTicket ticket) {
		List<LibraryDTO> libraryDtos = new ArrayList<>();
		for (Library library : libraries) {
			libraryDtos.add(mapLibrary(library, ticket));
		}
		return libraryDtos;
	}

	public LibraryVersionDTO mapLibraryVersionSummary(LibraryVersion version, SignedTicket ticket) {
		LibraryVersionDTO versionDto = new LibraryVersionDTO();
		versionDto.setLibraryId(version.getLibrary().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());

		versionDto.setState(version.getState().ordinal());
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));
		return versionDto;
	}

	public List<LibraryVersionDTO> mapLibraryVersionSummaryList(List<LibraryVersion> versions, SignedTicket ticket) {
		List<LibraryVersionDTO> versionDtos = new ArrayList<>();
		for (LibraryVersion version : versions) {
			versionDtos.add(mapLibraryVersionSummary(version, ticket));
		}
		return versionDtos;
	}

	public LibraryVersionDTO mapLibraryVersion(LibraryVersion version, SignedTicket ticket) {
		LibraryVersionDTO versionDto = new LibraryVersionDTO();
		versionDto.setLibraryId(version.getLibrary().getId());
		versionDto.setId(version.getId());
		versionDto.setName(version.getName());
		versionDto.setDescription(version.getDescription());

		StringBuilder contentText = new StringBuilder();
		//parse the jar
		if (version.getContentJar() != null)
		{
			List<String> classNames = getClassNamesInPackage(version.getContentJar());
			for (String className : classNames) {
				if (contentText.length() > 0)
					contentText.append(",");
				contentText.append(className);
			}
		}

		versionDto.setContentText(contentText.toString());

		versionDto.setState(version.getState().ordinal());
		versionDto.setCreatedOn(version.getCreatedOn());
		versionDto.setCreatedBy(user(version.getCreatedBy()));
		versionDto.setLastModifiedOn(version.getLastModifiedOn());
		versionDto.setLastModifiedBy(user(version.getLastModifiedBy()));
		versionDto.setLockedOn(version.getLockedOn());
		versionDto.setLockedBy(user(version.getLockedBy()));

		return versionDto;
	}

	public List<LibraryVersionDTO> mapLibraryVersionList(List<LibraryVersion> versions, SignedTicket ticket) {
		List<LibraryVersionDTO> versionDtos = new ArrayList<>();
		for (LibraryVersion version : versions) {
			versionDtos.add(mapLibraryVersion(version, ticket));
		}
		return versionDtos;
	}

	private List<String> getClassNamesInPackage(byte[] data) {
		List<String> classNames = new ArrayList<String>();

		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(data));
		try {
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry())
			    if(entry.getName().endsWith(".class") && !entry.isDirectory()) {
			        // This ZipEntry represents a class. Now, what class does it represent?
			        StringBuilder className = new StringBuilder();
			        for(String part : entry.getName().split("/")) {
			            if(className.length() != 0)
			                className.append(".");
			            className.append(part);
			            if(part.endsWith(".class"))
			                className.setLength(className.length()-".class".length());
			        }
			        /*MyClassLoader cl = new MyClassLoader(zip);
			    	Class entryClass = cl.findClass(className.toString());
			    	long uid = 0;
			    	ObjectStreamClass myClass = ObjectStreamClass.lookup(entryClass);
			    	if (myClass!=null)
			    		uid = myClass.getSerialVersionUID();*/

			        classNames.add(className.toString());
			    }
		}
		catch (IOException e)
		{
			return null;
		}
		return classNames;
	}

}
