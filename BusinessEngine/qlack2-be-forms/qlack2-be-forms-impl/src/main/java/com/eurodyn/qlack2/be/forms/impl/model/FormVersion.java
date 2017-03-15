package com.eurodyn.qlack2.be.forms.impl.model;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the fmn_form_version database table.
 *
 */
@Entity
@Table(name = "fmn_form_version")
public class FormVersion implements Serializable {

	private static final long serialVersionUID = 771831700149128164L;

	@Id
	private String id;

	@Version
	private long dbversion;

	// bi-directional many-to-one association to Form
	@ManyToOne
	@JoinColumn(name = "form")
	private Form form;

	private String name;

	private String description;

	private String content;

	@Column(name = "document_id")
	private String documentId;

	@Enumerated(EnumType.ORDINAL)
	private State state;

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

	// bi-directional many-to-one association to Condition
	@OneToMany(mappedBy = "formVersion", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@OrderBy("name")
	private List<Condition> conditions;

	// bi-directional many-to-one association to Attachment
	@OneToMany(mappedBy = "formVersion", cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	private List<Attachment> attachments;

	public FormVersion() {
		id = UUID.randomUUID().toString();
	}

	public static FormVersion find(EntityManager em, String id) {
		return em.find(FormVersion.class, id);
	}

	public static String getFormVersionIdByName(EntityManager em, String name,
			String formId) {
		Query query = em
				.createQuery("SELECT v.id FROM FormVersion v WHERE v.name = :name and v.form.id = :formId");
		query.setParameter("name", name);
		query.setParameter("formId", formId);

		List<String> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}

	public static Long countFormVersionsLockedByOtherUser(EntityManager em,
			String formId, String user) {
		Query query = em
				.createQuery("SELECT count(v) FROM FormVersion v WHERE v.form.id = :formId and v.lockedOn IS NOT NULL and v.lockedBy <> :user");
		query.setParameter("formId", formId);
		query.setParameter("user", user);

		return (Long) query.getSingleResult();
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

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
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

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

}
