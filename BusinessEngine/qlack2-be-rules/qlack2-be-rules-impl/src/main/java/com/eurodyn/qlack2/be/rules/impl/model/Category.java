package com.eurodyn.qlack2.be.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "rul_category")
public class Category implements Serializable {

	private static final long serialVersionUID = -6895990915861080796L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@Column(name = "project_id")
	private String projectId;

	private String name;

	private String description;

	@Column(name = "created_on")
	private long createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@ManyToMany(mappedBy = "categories")
	private List<WorkingSet> workingSets;

	// XXX traverse or query ? for query, drop this direction ?
	@ManyToMany(mappedBy = "categories")
	private List<Rule> rules;

	@ManyToMany(mappedBy = "categories")
	private List<DataModel> dataModels;

	@ManyToMany(mappedBy = "categories")
	private List<Library> libraries;

	// -- Constructors

	public Category() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final Category findById(EntityManager em, String id) {
		return em.find(Category.class, id);
	}

	public static final Category findByProjectAndName(EntityManager em, String projectId, String name) {
		String jpql =
				"SELECT c " +
				"FROM Category c " +
				"WHERE c.projectId = :projectId AND c.name = :name";

		try {
			return em.createQuery(jpql, Category.class)
					.setParameter("projectId", projectId)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final List<Category> findByProjectId(EntityManager em, String projectId) {
		String jpql = "SELECT c FROM Category c WHERE c.projectId = :projectId ORDER BY c.name ASC";

		return em.createQuery(jpql, Category.class)
				.setParameter("projectId", projectId)
				.getResultList();
	}

	public static Long countWorkingSetsByCategoryId(EntityManager em, String categoryId) {
		String jpql =
				"SELECT count(s) " +
				"FROM WorkingSet s " +
				"JOIN s.categories c " +
				"WHERE c.id = :categoryId";

		return em.createQuery(jpql, Long.class)
				.setParameter("categoryId", categoryId)
				.getSingleResult();
	}

	// Can I have a separate RuleCategory JPA mapping so that I can count properly in SQL ?

	public static Long countRulesByCategoryId(EntityManager em, String categoryId) {
		String jpql =
				"SELECT count(r) " +
				"FROM Rule r " +
				"JOIN r.categories c " +
				"WHERE c.id = :categoryId";

		return em.createQuery(jpql, Long.class)
				.setParameter("categoryId", categoryId)
				.getSingleResult();
	}

	public static Long countDataModelsByCategoryId(EntityManager em, String categoryId) {
		String jpql =
				"SELECT count(m) " +
				"FROM DataModel m " +
				"JOIN m.categories c " +
				"WHERE c.id = :categoryId";

		return em.createQuery(jpql, Long.class)
				.setParameter("categoryId", categoryId)
				.getSingleResult();
	}

	public static Long countLibrariesByCategoryId(EntityManager em, String categoryId) {
		String jpql =
				"SELECT count(l) " +
				"FROM Library l " +
				"JOIN l.categories c " +
				"WHERE c.id = :categoryId";

		return em.createQuery(jpql, Long.class)
				.setParameter("categoryId", categoryId)
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

	public List<WorkingSet> getWorkingSets() {
		return workingSets;
	}

	public void setWorkingSets(List<WorkingSet> workingSets) {
		this.workingSets = workingSets;
	}

	public List<Rule> getRules() {
		return rules;
	}

	public void setRules(List<Rule> rules) {
		this.rules = rules;
	}

	public List<DataModel> getDataModels() {
		return dataModels;
	}

	public void setDataModels(List<DataModel> dataModels) {
		this.dataModels = dataModels;
	}

	public List<Library> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<Library> libraries) {
		this.libraries = libraries;
	}

}
