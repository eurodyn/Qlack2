package com.eurodyn.qlack2.be.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "rul_data_model")
public class DataModel implements Serializable {

	private static final long serialVersionUID = -6081692495357866722L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@Column(name = "project_id")
	private String projectId;

	private String name;

	private String description;

	private boolean active;

	@Column(name = "created_on")
	private long createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@OneToMany(mappedBy = "dataModel")
	private List<DataModelVersion> versions;

	// XXX seems to work without cascade
	@ManyToMany
//	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "rul_data_models_categories",
		joinColumns = {
			@JoinColumn(name = "data_model_id")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "category_id")
		}
	)
	private List<Category> categories;

	// -- Constructors

	public DataModel() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final DataModel findById(EntityManager em, String id) {
		String jpql =
				"SELECT m " +
				"FROM DataModel m " +
				"LEFT JOIN FETCH m.categories " +
				"WHERE m.id = :id";

		try {
			return em.createQuery(jpql, DataModel.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final DataModel findByProjectAndName(EntityManager em, String projectId, String name) {
		String jpql =
				"SELECT m " +
				"FROM DataModel m " +
				"WHERE m.projectId = :projectId AND m.name = :name";

		try {
			return em.createQuery(jpql, DataModel.class)
					.setParameter("projectId", projectId)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final List<DataModel> findByProjectId(EntityManager em, String projectId) {
		String jpql =
				"SELECT DISTINCT m " +
				"FROM DataModel m " +
				"LEFT JOIN FETCH m.categories " +
				"WHERE m.projectId = :projectId " +
				"ORDER BY m.name ASC";

		return em.createQuery(jpql, DataModel.class)
				.setParameter("projectId", projectId)
				.getResultList();
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

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public List<DataModelVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<DataModelVersion> versions) {
		this.versions = versions;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}
