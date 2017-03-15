package com.eurodyn.qlack2.be.forms.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the fmn_attachment database table.
 *
 */
@Entity
@Table(name = "fmn_attachment")
public class Attachment implements Serializable {

	private static final long serialVersionUID = 7323571597773477192L;

	@Id
	private String id;

	@Version
	private long dbversion;

	// bi-directional many-to-one association to FormVersion
	@ManyToOne
	@JoinColumn(name = "form_version")
	private FormVersion formVersion;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_content")
	private byte[] fileContent;

	@Column(name = "content_type")
	private String contentType;

	public Attachment() {
		id = UUID.randomUUID().toString();
	}

	public static Attachment find(EntityManager em, String id) {
		return em.find(Attachment.class, id);
	}

	public static Attachment findByFormVersionIdAndFileName(EntityManager em,
			String formVersionId, String fileName) {
		Query query = em
				.createQuery("SELECT a FROM Attachment a WHERE a.formVersion.id = :formVersionId and a.fileName = :fileName");
		query.setParameter("formVersionId", formVersionId);
		query.setParameter("fileName", fileName);

		List<Attachment> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
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

	public FormVersion getFormVersion() {
		return formVersion;
	}

	public void setFormVersion(FormVersion formVersion) {
		this.formVersion = formVersion;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
