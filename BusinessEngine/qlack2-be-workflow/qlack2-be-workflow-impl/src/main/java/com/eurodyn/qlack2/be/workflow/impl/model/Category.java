package com.eurodyn.qlack2.be.workflow.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the wfl_category database table.
 *
 */
@Entity
@Table(name = "wfl_category")
public class Category implements Serializable {

	private static final long serialVersionUID = 4750564945989267287L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@Column(name = "project_id")
	private String projectId;

	private String name;

	private String description;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_on")
	private long createdOn;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	// bi-directional many-to-many association to Workflow
	@ManyToMany(mappedBy = "categories")
	@OrderBy("name")
	private List<Workflow> workflows;

	public Category() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public List<Workflow> getWorkflows() {
		return workflows;
	}

	public void setWorkflows(List<Workflow> workflows) {
		this.workflows = workflows;
	}

	public static Category find(EntityManager em, String id) {
		return em.find(Category.class, id);
	}

	public static List<Category> findByProjectId(EntityManager em, String projectId) {
        Query query = em.createQuery("SELECT g FROM Category g WHERE g.projectId=:projectId ORDER BY g.name ASC");
		query.setParameter("projectId", projectId);
        return query.getResultList();
	}

	public static Long countResourcesByCategoryId(EntityManager em, String categoryId) {
		String jpql =
				"SELECT count(w) " +
				"FROM Workflow w " +
				"JOIN w.categories c " +
				"WHERE c.id = :categoryId";

		return em.createQuery(jpql, Long.class)
				.setParameter("categoryId", categoryId)
				.getSingleResult();
	}

	public static String getCategoryIdByName(EntityManager em, String name, String projectId) {
		Query query = em
				.createQuery("SELECT c.id FROM Category c WHERE c.name = :name and c.projectId = :projectId");
		query.setParameter("name", name);
		query.setParameter("projectId", projectId);

		List<String> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}

	public static void deleteCategoriesForProjectId(EntityManager em, String projectId) {
		Query query = em.createQuery("DELETE FROM Category c WHERE c.projectId = :projectId");
		query.setParameter("projectId", projectId);
		query.executeUpdate();
	}
}
