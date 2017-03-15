package com.eurodyn.qlack2.be.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.eurodyn.qlack2.be.rules.api.dto.VersionState;

@Entity
@Table(name = "rul_data_model_version")
public class DataModelVersion implements Serializable {

	private static final long serialVersionUID = -2537491401104003973L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@ManyToOne
	@JoinColumn(name = "data_model_id")
	private DataModel dataModel;

	private String name;

	private String description;

	@Column(name = "package")
	private String modelPackage;

	@ManyToOne
	@JoinColumn(name = "parent_model_id")
	private DataModelVersion parentModel;

	@OneToMany(mappedBy = "containerModel", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<DataModelField> fields;

	@Enumerated(EnumType.ORDINAL)
	private VersionState state;

	@Column(name = "created_on")
	private long createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@Column(name = "locked_on")
	private Long lockedOn;

	@Column(name = "locked_by")
	private String lockedBy;

	@ManyToMany(mappedBy = "dataModels")
	private List<WorkingSetVersion> workingSets;

	// -- Constructors

	public DataModelVersion() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final String findDataModelIdById(EntityManager em, String id) {
		String jpql =
				"SELECT v.dataModel.id " +
				"FROM DataModelVersion v " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, String.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final DataModelVersion findById(EntityManager em, String id) {
		String jpql =
				"SELECT v " +
				"FROM DataModelVersion v " +
				"LEFT JOIN FETCH v.parentModel " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, DataModelVersion.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final DataModelVersion findByDataModelAndName(EntityManager em, String modelId, String name) {
		String jpql =
				"SELECT v " +
				"FROM DataModelVersion v " +
				"WHERE v.dataModel.id = :modelId AND v.name = :name";

		try {
			return em.createQuery(jpql, DataModelVersion.class)
					.setParameter("modelId", modelId)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final String findIdByName(EntityManager em, String projectId, String modelName, String name) {
		String jpql =
				"SELECT v.id " +
				"FROM DataModelVersion v " +
				"WHERE v.dataModel.projectId = :projectId AND v.dataModel.name = :modelName AND v.name = :name";

		try {
			return em.createQuery(jpql, String.class)
					.setParameter("projectId", projectId)
					.setParameter("modelName", modelName)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final List<DataModelVersion> findByDataModelId(EntityManager em, String modelId) {
		String jpql =
				"SELECT v " +
				"FROM DataModelVersion v " +
				"WHERE v.dataModel.id = :modelId " +
				"ORDER BY v.name ASC";

		return em.createQuery(jpql, DataModelVersion.class).setParameter("modelId", modelId).getResultList();
	}

	public static final List<DataModelVersion> findByWorkingSetVersionId(EntityManager em, String workingSetVersionId) {
		String jpql =
				"SELECT v " +
				"FROM DataModelVersion v " +
				"JOIN v.workingSets w " +
				"WHERE w.id = :workingSetVersionId";

		return em.createQuery(jpql, DataModelVersion.class).setParameter("workingSetVersionId", workingSetVersionId).getResultList();
	}

	public static final List<String> findIdsByWorkingSetVersionId(EntityManager em, String workingSetVersionId) {
		String jpql =
				"SELECT v.id " +
				"FROM DataModelVersion v " +
				"JOIN v.workingSets w " +
				"WHERE w.id = :workingSetVersionId";

		return em.createQuery(jpql, String.class).setParameter("workingSetVersionId", workingSetVersionId).getResultList();
	}

	public static final long countLockedByOtherUser(EntityManager em, String modelId, String userId) {
		String jpql =
				"SELECT count(v) " +
				"FROM DataModelVersion v " +
				"WHERE v.dataModel.id = :modelId AND v.lockedBy IS NOT NULL AND v.lockedBy <> :userId";

		return em.createQuery(jpql, Long.class)
				.setParameter("modelId", modelId)
				.setParameter("userId", userId)
				.getSingleResult();
	}

	public static List<DataModelVersion> findChildren(EntityManager em, String parentId) {
		String jpql =
				"SELECT DISTINCT v " +
				"FROM DataModelVersion v " +
				"JOIN v.dataModel m " +
				"WHERE v.parentModel.id = :parentId";

		return em.createQuery(jpql, DataModelVersion.class)
				.setParameter("parentId", parentId)
				.getResultList();
	}

	public static Long countChildrenOfModel(EntityManager em, String parentModelId) {
		String jpql =
				"SELECT count(v) " +
				"FROM DataModelVersion v " +
				"WHERE v.parentModel.dataModel.id = :parentModelId";

		return em.createQuery(jpql, Long.class)
				.setParameter("parentModelId", parentModelId)
				.getSingleResult();
	}

	public static List<DataModelVersion> findContainers(EntityManager em, String fieldTypeId) {
		// exclude self references
		String jpql =
				"SELECT DISTINCT v " +
				"FROM DataModelVersion v " +
				"JOIN v.dataModel m " +
				"JOIN v.fields f " +
				"WHERE f.fieldModelType.id = :fieldTypeId AND v.id <> :fieldTypeId";

		return em.createQuery(jpql, DataModelVersion.class)
				.setParameter("fieldTypeId", fieldTypeId)
				.getResultList();
	}

	public static Long countContainersOfModel(EntityManager em, String fieldTypeModelId) {
		// exclude self references trick does not work here
		String jpql =
				"SELECT count(v) " +
				"FROM DataModelVersion v " +
				"JOIN v.fields f " +
				"WHERE f.fieldModelType.dataModel.id = :fieldTypeModelId";

		return em.createQuery(jpql, Long.class)
				.setParameter("fieldTypeModelId", fieldTypeModelId)
				.getSingleResult();
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

	public DataModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public VersionState getState() {
		return state;
	}

	public void setState(VersionState state) {
		this.state = state;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Long getLockedOn() {
		return lockedOn;
	}

	public void setLockedOn(Long lockedOn) {
		this.lockedOn = lockedOn;
	}

	public String getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}

	public String getModelPackage() {
		return modelPackage;
	}

	public void setModelPackage(String modelPackage) {
		this.modelPackage = modelPackage;
	}

	public DataModelVersion getParentModel() {
		return parentModel;
	}

	public void setParentModel(DataModelVersion parentModel) {
		this.parentModel = parentModel;
	}

	public List<DataModelField> getFields() {
		return fields;
	}

	public void setFields(List<DataModelField> fields) {
		this.fields = fields;
	}

	public List<WorkingSetVersion> getWorkingSets() {
		return workingSets;
	}

	public void setWorkingSets(List<WorkingSetVersion> workingSets) {
		this.workingSets = workingSets;
	}
}
