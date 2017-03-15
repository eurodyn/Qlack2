package com.eurodyn.qlack2.be.forms.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the fmn_form database table.
 *
 */
@Entity
@Table(name = "fmn_form")
public class Form implements Serializable {

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

	@Column(name = "created_on")
	private long createdOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	// bi-directional many-to-one association to FormVersion
	@OneToMany(mappedBy = "form")
	@OrderBy("name")
	private List<FormVersion> formVersions;

	// bi-directional many-to-many association to Category
	@ManyToMany
	@JoinTable(name = "fmn_form_has_category", joinColumns = { @JoinColumn(name = "form") }, inverseJoinColumns = { @JoinColumn(name = "category") })
	private List<Category> categories;

	// uni-directional many-to-many association to Languages
	@ElementCollection
	@CollectionTable(
			name="fmn_form_has_language",
			joinColumns = @JoinColumn(name="form")
	)
	@Column(name = "locale")
	private List<String> locales;

	public Form() {
		id = UUID.randomUUID().toString();
	}

	public static Form find(EntityManager em, String id) {
		return em.find(Form.class, id);
	}

	public static String getFormIdByName(EntityManager em, String name,
			String projectId) {
		Query query = em
				.createQuery("SELECT f.id FROM Form f WHERE f.name = :name and f.projectId = :projectId");
		query.setParameter("name", name);
		query.setParameter("projectId", projectId);

		List<String> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}

	public static List<Form> getUncategorisedFormsForProjectId(
			EntityManager em, String projectId) {
		Query query = em
				.createQuery(
						"SELECT f FROM Form f WHERE f.projectId=:projectId and not exists (SELECT c FROM f.categories c) ORDER BY f.name ASC",
						Form.class);
		query.setParameter("projectId", projectId);

		return query.getResultList();
	}

	public static List<Form> getFormsForProjectId(EntityManager em,
			String projectId) {
		Query query = em
				.createQuery(
						"SELECT f FROM Form f WHERE f.projectId=:projectId ORDER BY f.name ASC",
						Form.class);
		query.setParameter("projectId", projectId);

		return query.getResultList();
	}

	public static void deleteFormsForProjectId(EntityManager em, String projectId) {
		Query query = em.createQuery("DELETE FROM Form f WHERE f.projectId = :projectId");
		query.setParameter("projectId", projectId);
		query.executeUpdate();
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

	public List<FormVersion> getFormVersions() {
		return formVersions;
	}

	public void setFormVersions(List<FormVersion> formVersions) {
		this.formVersions = formVersions;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public List<String> getLocales() {
		return locales;
	}

	public void setLocales(List<String> locales) {
		this.locales = locales;
	}
}
