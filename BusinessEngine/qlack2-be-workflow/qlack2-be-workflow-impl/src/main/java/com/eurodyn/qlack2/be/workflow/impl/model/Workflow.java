package com.eurodyn.qlack2.be.workflow.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the wfl_workflow database table.
 *
 */
@Entity
@Table(name = "wfl_workflow")
public class Workflow implements Serializable {

	private static final long serialVersionUID = 8323721376445517982L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@Column(name = "project_id")
	private String projectId;

	private String name;

	private String description;

	private boolean active;
	
	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_on")
	private long createdOn;
	
	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	// bi-directional many-to-one association to WorkflowVersion
	@OneToMany(mappedBy = "workflow")
	@OrderBy(value = "name ASC")
	private List<WorkflowVersion> workflowVersions;

	//bi-directional many-to-many association to Category
	@ManyToMany
	@JoinTable(name = "wfl_workflow_has_category",
		joinColumns = {
			@JoinColumn(name = "workflow")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "category")
	})
	private List<Category> categories;

	public Workflow() {
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getCreatedOn() {
		return createdOn;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public List<WorkflowVersion> getWorkflowVersions() {
		return workflowVersions;
	}

	public void setWorkflowVersions(List<WorkflowVersion> workflowVersions) {
		this.workflowVersions = workflowVersions;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	public static Workflow find(EntityManager em, String workflowId) {
		return em.find(Workflow.class, workflowId);
	}
	
	public static Workflow findByName(EntityManager em, String name) {
		String myQuery = "SELECT g FROM Workflow g WHERE g.name=:name";

		try {
			return em.createQuery(myQuery, Workflow.class).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public static List<Workflow> findByProjectId(EntityManager em, String projectId) {     
        Query query = em.createQuery("SELECT g FROM Workflow g WHERE g.projectId=:projectId ORDER BY g.name ASC");
		query.setParameter("projectId", projectId);
        return query.getResultList();
	}
	
	public static final boolean nameExists(EntityManager em, String name) {
		String jpql = "SELECT 1 FROM Workflow g WHERE g.name=:name";

		try {
			em.createQuery(jpql, Integer.class).setParameter("name", name).getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
	
	public static List<Workflow> findUncategorizedWorkflowsByProjectId(EntityManager em, String projectId) {     
        Query query = em.createQuery("SELECT g FROM Workflow g WHERE g.projectId=:projectId and not exists (SELECT c FROM g.categories c) ORDER BY g.name ASC");
		query.setParameter("projectId", projectId);
        return query.getResultList();
	}
	
	public static void deleteWorkflowsForProjectId(EntityManager em, String projectId) {
		Query query = em.createQuery("DELETE FROM Workflow w WHERE w.projectId = :projectId");
		query.setParameter("projectId", projectId);
		query.executeUpdate();
	}
}
