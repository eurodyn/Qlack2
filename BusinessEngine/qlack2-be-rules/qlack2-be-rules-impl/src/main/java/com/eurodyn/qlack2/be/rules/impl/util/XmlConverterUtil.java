package com.eurodyn.qlack2.be.rules.impl.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.joda.time.DateTime;

import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldType;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlDataModelFieldDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlDataModelFieldsDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlDataModelVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlDataModelVersionsDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlLibraryVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlLibraryVersionsDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlRuleVersionDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlRuleVersionsDTO;
import com.eurodyn.qlack2.be.rules.api.dto.xml.XmlWorkingSetVersionDTO;
import com.eurodyn.qlack2.be.rules.api.exception.QImportExportException;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelField;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Library;
import com.eurodyn.qlack2.be.rules.impl.model.LibraryVersion;
import com.eurodyn.qlack2.be.rules.impl.model.Rule;
import com.eurodyn.qlack2.be.rules.impl.model.RuleVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;

public class XmlConverterUtil {

	// -- Map

	public XmlWorkingSetVersionDTO mapWorkingSet(WorkingSetVersion version) {
		XmlWorkingSetVersionDTO xmlVersion = new XmlWorkingSetVersionDTO();
		xmlVersion.setWorkingSetName(version.getWorkingSet().getName());
		xmlVersion.setName(version.getName());
		xmlVersion.setDescription(version.getDescription());

		List<XmlRuleVersionDTO> xmlRuleVersions = mapRuleList(version.getRules());
		XmlRuleVersionsDTO xmlRuleVersionsWrapper = new XmlRuleVersionsDTO();
		xmlRuleVersionsWrapper.setRuleVersions(xmlRuleVersions);
		xmlVersion.setRuleVersions(xmlRuleVersionsWrapper);

		List<XmlDataModelVersionDTO> xmlDataModelVersions = mapDataModelList(version.getDataModels());
		XmlDataModelVersionsDTO xmlDataModelVersionsWrapper = new XmlDataModelVersionsDTO();
		xmlDataModelVersionsWrapper.setDataModelVersions(xmlDataModelVersions);
		xmlVersion.setDataModelVersions(xmlDataModelVersionsWrapper);

		List<XmlLibraryVersionDTO> xmlLibraryVersions = mapLibraryList(version.getLibraries());
		XmlLibraryVersionsDTO xmlLibraryVersionsWrapper = new XmlLibraryVersionsDTO();
		xmlLibraryVersionsWrapper.setLibraryVersions(xmlLibraryVersions);
		xmlVersion.setLibraryVersions(xmlLibraryVersionsWrapper);

		return xmlVersion;
	}

	public List<XmlRuleVersionDTO> mapRuleList(List<RuleVersion> versions) {
		List<XmlRuleVersionDTO> xmlVersions = new ArrayList<>();
		for (RuleVersion version : versions) {
			XmlRuleVersionDTO xmlVersion = mapRule(version);
			xmlVersions.add(xmlVersion);
		}
		return xmlVersions;
	}

	public List<XmlDataModelVersionDTO> mapDataModelList(List<DataModelVersion> versions) {
		List<XmlDataModelVersionDTO> xmlVersions = new ArrayList<>();
		for (DataModelVersion version : versions) {
			XmlDataModelVersionDTO xmlVersion = mapDataModel(version);
			xmlVersions.add(xmlVersion);
		}
		return xmlVersions;
	}

	public List<XmlLibraryVersionDTO> mapLibraryList(List<LibraryVersion> versions) {
		List<XmlLibraryVersionDTO> xmlVersions = new ArrayList<>();
		for (LibraryVersion version : versions) {
			XmlLibraryVersionDTO xmlVersion = mapLibrary(version);
			xmlVersions.add(xmlVersion);
		}
		return xmlVersions;
	}

	public XmlRuleVersionDTO mapRule(RuleVersion version) {
		XmlRuleVersionDTO xmlVersion = new XmlRuleVersionDTO();
		xmlVersion.setRuleName(version.getRule().getName());
		xmlVersion.setName(version.getName());
		xmlVersion.setDescription(version.getDescription());
		xmlVersion.setContent(version.getContent());
		return xmlVersion;
	}

	public XmlLibraryVersionDTO mapLibrary(LibraryVersion version) {
		XmlLibraryVersionDTO xmlVersion = new XmlLibraryVersionDTO();
		xmlVersion.setLibraryName(version.getLibrary().getName());
		xmlVersion.setName(version.getName());
		xmlVersion.setDescription(version.getDescription());
		xmlVersion.setContentJar(version.getContentJar());
		return xmlVersion;
	}

	public XmlDataModelVersionDTO mapDataModel(DataModelVersion version) {
		XmlDataModelVersionDTO xmlVersion = new XmlDataModelVersionDTO();
		xmlVersion.setDataModelName(version.getDataModel().getName());
		xmlVersion.setName(version.getName());
		xmlVersion.setDescription(version.getDescription());
		xmlVersion.setModelPackage(version.getModelPackage());

		DataModelVersion parentVersion = version.getParentModel();
		if (parentVersion != null) {
			xmlVersion.setParentDataModel(parentVersion.getDataModel().getName());
			xmlVersion.setParentDataModelVersion(parentVersion.getName());
		}

		List<XmlDataModelFieldDTO> xmlFields = mapDataModelFields(version.getFields());
		XmlDataModelFieldsDTO xmlFieldsWrapper = new XmlDataModelFieldsDTO();
		xmlFieldsWrapper.setFields(xmlFields);
		xmlVersion.setFields(xmlFieldsWrapper);

		return xmlVersion;
	}

	public List<XmlDataModelFieldDTO> mapDataModelFields(List<DataModelField> fields) {
		List<XmlDataModelFieldDTO> xmlFields = new ArrayList<>();
		for (DataModelField field : fields) {
			XmlDataModelFieldDTO xmlField = mapDataModelField(field);
			xmlFields.add(xmlField);
		}
		return xmlFields;
	}

	public XmlDataModelFieldDTO mapDataModelField(DataModelField field) {
		XmlDataModelFieldDTO xmlField = new XmlDataModelFieldDTO();
		xmlField.setName(field.getName());

		DataModelFieldType primitiveType = field.getFieldPrimitiveType();
		if (primitiveType != null) {
			xmlField.setFieldPrimitiveType(primitiveType.ordinal());
		}

		DataModelVersion modelType = field.getFieldModelType();
		if (modelType != null) {
			xmlField.setFieldModelType(modelType.getDataModel().getName());
			xmlField.setFieldModelTypeVersion(modelType.getName());
		}

		return xmlField;
	}

	// -- Rules

	public RuleVersion mapRuleVersion(SignedTicket ticket, Rule rule, XmlRuleVersionDTO xmlVersion) {
		RuleVersion version = new RuleVersion();
		version.setRule(rule);

		version.setName(xmlVersion.getName());
		version.setDescription(xmlVersion.getDescription());
		version.setContent(xmlVersion.getContent());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setState(VersionState.DRAFT);
		version.setCreatedBy(ticket.getUserID());
		version.setCreatedOn(millis);
		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		return version;
	}

	public void computeDroolsRuleName(EntityManager em, String projectId, String ruleId, RuleVersion version, XmlRuleVersionDTO xmlVersion) {
		List<String> droolsRuleNames = RuleUtils.findRuleNames(xmlVersion.getContent());

		if (droolsRuleNames.isEmpty()) {
			throw new QImportExportException("Cannot find any rule definitions.");
		}

		if (droolsRuleNames.size() > 1) {
			throw new QImportExportException("Multiple rule definitions found.");
		}

		String droolsRuleName = droolsRuleNames.get(0);

		long matches = RuleVersion.countMatchingRuleName(em, droolsRuleName, ruleId, projectId);
		if (matches > 0) {
			throw new QImportExportException("The rule definition name is already used by another rule in the same project.");
		}

		version.setRuleName(droolsRuleName);
	}

	// -- Data models

	public DataModelVersion mapDataModelVersion(SignedTicket ticket, DataModel model, XmlDataModelVersionDTO xmlVersion) {
		DataModelVersion version = new DataModelVersion();
		version.setDataModel(model);

		version.setName(xmlVersion.getName());
		version.setDescription(xmlVersion.getDescription());
		version.setModelPackage(xmlVersion.getModelPackage());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setState(VersionState.DRAFT);
		version.setCreatedBy(ticket.getUserID());
		version.setCreatedOn(millis);
		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		return version;
	}

	public void mapDataModelVersionParent(EntityManager em, String projectId, DataModelVersion version, XmlDataModelVersionDTO xmlVersion) {
		DataModelVersion parentModel = getParentModel(em, projectId, xmlVersion);
		version.setParentModel(parentModel);
	}

	public void mapDataModelVersionFields(EntityManager em, String projectId, DataModelVersion version, XmlDataModelVersionDTO xmlVersion) {
		List<DataModelField> fields = new ArrayList<>();
		for (XmlDataModelFieldDTO xmlField : xmlVersion.getFields().getFields()) {
			DataModelField field = new DataModelField();
			field.setContainerModel(version);
			field.setName(xmlField.getName());

			DataModelFieldType primitiveType = getFieldPrimitiveType(xmlField);
			field.setFieldPrimitiveType(primitiveType);

			DataModelVersion modelType = getFieldModelType(em, projectId, xmlField);
			field.setFieldModelType(modelType);

			if ((primitiveType == null) == (modelType == null)) {
				throw new QImportExportException("Cannot specify both a primitive and a model type");
			}

			fields.add(field);
		}
		version.setFields(fields);
	}

	private DataModelVersion getParentModel(EntityManager em, String projectId, XmlDataModelVersionDTO xmlVersion) {
		String parentModelName = xmlVersion.getParentDataModel();
		String parentModelVersionName = xmlVersion.getParentDataModelVersion();
		if (parentModelName != null && parentModelVersionName != null) {
			String parentModelId = DataModelVersion.findIdByName(em, projectId, parentModelName, parentModelVersionName);
			DataModelVersion parentModel = em.find(DataModelVersion.class, parentModelId);
			if (parentModel == null) {
				throw new QImportExportException("Parent data model does not exit.");
			}
			return parentModel;
		}
		else {
			return null;
		}
	}

	private DataModelVersion getFieldModelType(EntityManager em, String projectId, XmlDataModelFieldDTO xmlField) {
		String fieldModelName = xmlField.getFieldModelType();
		String fieldModelVersionName = xmlField.getFieldModelTypeVersion();
		if (fieldModelName != null && fieldModelVersionName != null) {
			String fieldModelId = DataModelVersion.findIdByName(em, projectId, fieldModelName, fieldModelVersionName);
			DataModelVersion fieldModel = em.find(DataModelVersion.class, fieldModelId);
			if (fieldModel == null) {
				throw new QImportExportException("Field data model type does not exit.");
			}
			return fieldModel;
		}
		else {
			return null;
		}
	}

	private DataModelFieldType getFieldPrimitiveType(XmlDataModelFieldDTO xmlField) {
		Integer fieldTypeId = xmlField.getFieldPrimitiveType();
		if (fieldTypeId != null) {
			DataModelFieldType fieldType = findFieldTypeById(fieldTypeId);
			if (fieldType == null) {
				throw new QImportExportException("Field primitive type does not exit.");
			}
			return fieldType;
		}
		else {
			return null;
		}
	}

	private static DataModelFieldType findFieldTypeById(Integer id) {
		for (DataModelFieldType type : DataModelFieldType.values()) {
			if (id.equals(type.ordinal())) {
				return type;
			}
		}
		return null;
	}

	// -- Libraries

	public LibraryVersion mapLibraryVersion(SignedTicket ticket, Library library, XmlLibraryVersionDTO xmlVersion) {
		LibraryVersion version = new LibraryVersion();
		version.setLibrary(library);

		version.setName(xmlVersion.getName());
		version.setDescription(xmlVersion.getDescription());
		version.setContentJar(xmlVersion.getContentJar());

		DateTime now = DateTime.now();
		long millis = now.getMillis();

		version.setState(VersionState.DRAFT);
		version.setCreatedBy(ticket.getUserID());
		version.setCreatedOn(millis);
		version.setLastModifiedBy(ticket.getUserID());
		version.setLastModifiedOn(millis);

		return version;
	}

}
