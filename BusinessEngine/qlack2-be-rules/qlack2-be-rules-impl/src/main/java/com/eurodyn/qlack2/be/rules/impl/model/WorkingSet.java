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
@Table(name = "rul_working_set")
public class WorkingSet implements Serializable {

	private static final long serialVersionUID = 420557746484253386L;

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

	@OneToMany(mappedBy = "workingSet")
	private List<WorkingSetVersion> versions;

	@ManyToMany
	@JoinTable(name = "rul_working_sets_categories",
		joinColumns = {
			@JoinColumn(name = "working_set_id")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "category_id")
		}
	)
	private List<Category> categories;

	// -- Constructors

	public WorkingSet() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final WorkingSet findById(EntityManager em, String id) {
		String jpql =
				"SELECT s " +
				"FROM WorkingSet s " +
				"LEFT JOIN FETCH s.categories " +
				"WHERE s.id = :id";

		try {
			return em.createQuery(jpql, WorkingSet.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final WorkingSet findByProjectAndName(EntityManager em, String projectId, String name) {
		String jpql =
				"SELECT s " +
				"FROM WorkingSet s " +
				"WHERE s.projectId = :projectId AND s.name = :name";

		try {
			return em.createQuery(jpql, WorkingSet.class)
					.setParameter("projectId", projectId)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final List<WorkingSet> findByProjectId(EntityManager em, String projectId) {
		String jpql =
				"SELECT DISTINCT s " +
				"FROM WorkingSet s " +
				"LEFT JOIN FETCH s.categories " +
				"WHERE s.projectId = :projectId " +
				"ORDER BY s.name ASC";

		return em.createQuery(jpql, WorkingSet.class).setParameter("projectId", projectId).getResultList();
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

	public List<WorkingSetVersion> getVersions() {
		return versions;
	}

	public void setVersions(List<WorkingSetVersion> versions) {
		this.versions = versions;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}
