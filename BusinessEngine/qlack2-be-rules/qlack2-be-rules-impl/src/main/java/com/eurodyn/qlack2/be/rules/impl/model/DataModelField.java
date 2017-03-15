package com.eurodyn.qlack2.be.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.eurodyn.qlack2.be.rules.api.dto.DataModelFieldType;
import com.eurodyn.qlack2.be.rules.api.dto.VersionState;

@Entity
@Table(name = "rul_data_model_field")
public class DataModelField implements Serializable {

	private static final long serialVersionUID = 3079484937956175741L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@ManyToOne
	@JoinColumn(name = "container_model_id")
	private DataModelVersion containerModel;

	private String name;

	// exclusive or with model type
	@Column(name = "field_primitive_type")
	@Enumerated(EnumType.ORDINAL)
	private DataModelFieldType fieldPrimitiveType;

	// exclusive or with primitive type
	@ManyToOne
	@JoinColumn(name = "field_model_type_id")
	private DataModelVersion fieldModelType;

	// -- Constructors

	public DataModelField() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final List<DataModelField> findByContainerModelId(EntityManager em, String containerModelId) {
		String jpql =
				"SELECT f " +
				"FROM DataModelField f " +
				"LEFT JOIN FETCH f.fieldModelType " +
				"WHERE f.containerModel.id = :containerModelId";

		return em.createQuery(jpql, DataModelField.class).setParameter("containerModelId", containerModelId).getResultList();
	}

	public static final List<VersionState> getFieldStates(EntityManager em, String containerModelId) {
		String jpql =
				"SELECT mt.state " +
				"FROM DataModelField f JOIN f.fieldModelType mt " +
				"WHERE f.containerModel.id = :containerModelId";

		return em.createQuery(jpql, VersionState.class).setParameter("containerModelId", containerModelId).getResultList();
	}

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getDbversion() {
		return dbversion;
	}

	public void setDbversion(long dbversion) {
		this.dbversion = dbversion;
	}

	public DataModelVersion getContainerModel() {
		return containerModel;
	}

	public void setContainerModel(DataModelVersion containerModel) {
		this.containerModel = containerModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataModelFieldType getFieldPrimitiveType() {
		return fieldPrimitiveType;
	}

	public void setFieldPrimitiveType(DataModelFieldType fieldPrimitiveType) {
		this.fieldPrimitiveType = fieldPrimitiveType;
	}

	public DataModelVersion getFieldModelType() {
		return fieldModelType;
	}

	public void setFieldModelType(DataModelVersion fieldModelType) {
		this.fieldModelType = fieldModelType;
	}

}
