package com.eurodyn.qlack2.be.rules.impl.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import javax.persistence.EntityManager;

import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldType;
import com.eurodyn.qlack2.be.rules.impl.model.DataModel;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelField;
import com.eurodyn.qlack2.be.rules.impl.model.DataModelVersion;
import com.eurodyn.qlack2.be.rules.impl.model.WorkingSetVersion;

public class DataModelsJarUtil {

	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void createDataModelsJar(WorkingSetVersion version) {
		List<String> dataModelVersionIds = new ArrayList<>();
		for (DataModelVersion dataModelVersion : version.getDataModels()) {
			dataModelVersionIds.add(dataModelVersion.getId());
		}

		byte[] jar = generateJar(dataModelVersionIds);

		version.setDataModelsJar(jar);
	}

	private byte[] generateJar(List<String> versionIds) {

		ClassPool pool = new ClassPool();
		pool.appendSystemPath();

		PojoGenerator pojoGenerator = new PojoGenerator(pool);

		// generate javassist classes
		Map<String, StringDataModelVersion> stringVersions = new LinkedHashMap<>();
		for (String versionId : versionIds) {
			DataModelVersion version = DataModelVersion.findById(em, versionId);
			StringDataModelVersion stringVersion = convertDataModelVersionToString(version);
			stringVersions.put(versionId, stringVersion);

			String className = stringVersion.getClassName();
			pool.makeClass(className);
		}

		Map<String, CtClass> classes = new LinkedHashMap<>();
		for (Entry<String, StringDataModelVersion> entry : stringVersions.entrySet()) {
			String id = entry.getKey();
			StringDataModelVersion stringVersion = entry.getValue();

			try {
				CtClass clazz = pojoGenerator.generate(stringVersion);
				classes.put(id, clazz);
			}
			catch (NotFoundException | CannotCompileException e) {
				throw new RuntimeException(e);
			}
		}

		// compile javassist classes
		Map<String, byte[]> bytecodes = new LinkedHashMap<>();
		for (Entry<String, CtClass> entry : classes.entrySet()) {
			String id = entry.getKey();
			CtClass cc = entry.getValue();

			try {
				byte[] bytecode = cc.toBytecode();
				bytecodes.put(id, bytecode);
			}
			catch (IOException | CannotCompileException e) {
				throw new RuntimeException(e);
			}
		}

		// generate jar
		List<JarDataModelVersion> jarVersions = new ArrayList<>();
		for (String versionId : versionIds) {
			DataModelVersion version = DataModelVersion.findById(em, versionId);

			JarDataModelVersion jarVersion = new JarDataModelVersion();
			jarVersion.setClassName(getClassName(version));
			jarVersion.setLastModified(version.getLastModifiedOn());
			jarVersion.setBytes(bytecodes.get(versionId));

			jarVersions.add(jarVersion);
		}

		JarGenerator jarGenerator = new JarGenerator();
		byte[] bytes = jarGenerator.generate(jarVersions);

		return bytes;
	}

	private static StringDataModelVersion convertDataModelVersionToString(DataModelVersion version) {
		StringDataModelVersion stringVersion = new StringDataModelVersion();

		stringVersion.setId(version.getId());
		stringVersion.setName(version.getName());
		stringVersion.setClassName(getClassName(version));

		DataModelVersion parent = version.getParentModel();
		if (parent != null) {
			stringVersion.setSuperClassName(getClassName(parent));
		}

		List<StringDataModelField> stringFields = new ArrayList<>();
		for (DataModelField field : version.getFields()) {
			StringDataModelField stringField = new StringDataModelField();

			stringField.setName(field.getName());
			stringField.setClassName(getFieldClassName(field));

			stringFields.add(stringField);
		}
		stringVersion.setFields(stringFields);

		return stringVersion;
	}

	private static String getClassName(DataModelVersion version) {
		DataModel model = version.getDataModel();
		return version.getModelPackage() + "." + model.getName();
	}

	private static String getFieldClassName(DataModelField field) {
		if (field.getFieldPrimitiveType() != null) {
			DataModelFieldType type = field.getFieldPrimitiveType();
			return fieldTypeToClassName.get(type);
		}
		else {
			DataModelVersion version = field.getFieldModelType();
			return getClassName(version);
		}
	}

	private static final Map<DataModelFieldType, String> fieldTypeToClassName = new HashMap<>();

	{
		fieldTypeToClassName.put(DataModelFieldType.BOOLEAN, Boolean.class.getName());
		fieldTypeToClassName.put(DataModelFieldType.INTEGER, Integer.class.getName());
		fieldTypeToClassName.put(DataModelFieldType.FLOAT, Float.class.getName());
		fieldTypeToClassName.put(DataModelFieldType.DECIMAL, Double.class.getName());
		fieldTypeToClassName.put(DataModelFieldType.STRING, String.class.getName());
		fieldTypeToClassName.put(DataModelFieldType.DATE, Date.class.getName());
		fieldTypeToClassName.put(DataModelFieldType.BINARY, "byte[]");
	}

}
