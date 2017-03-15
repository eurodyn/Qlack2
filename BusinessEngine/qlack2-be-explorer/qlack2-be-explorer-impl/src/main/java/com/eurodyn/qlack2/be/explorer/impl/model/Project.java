package com.eurodyn.qlack2.be.explorer.impl.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.Version;

@Entity
@Table(name = "exp_project")
public class Project {
	@Id
	private String id;

	@Version
	private long dbversion;

	private String name;
	private String description;
	private boolean active;
	private boolean rules;
	private boolean workflows;
	private boolean forms;

	@Column(name = "created_on")
	private long createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	public Project() {
		id = UUID.randomUUID().toString();
	}

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

	public boolean isRules() {
		return rules;
	}

	public void setRules(boolean rules) {
		this.rules = rules;
	}

	public boolean isWorkflows() {
		return workflows;
	}

	public void setWorkflows(boolean workflows) {
		this.workflows = workflows;
	}

	public boolean isForms() {
		return forms;
	}

	public void setForms(boolean forms) {
		this.forms = forms;
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

	public static Project findByName(EntityManager em, String name) {
		TypedQuery<Project> query = em.createQuery("SELECT p FROM Project p WHERE p.name = :name", Project.class);
		query.setParameter("name", name);
		List<Project> queryResult = query.getResultList();
		return queryResult.size() > 0 ? queryResult.get(0) : null;
	}
}
