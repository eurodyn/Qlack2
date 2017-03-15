package com.eurodyn.qlack2.be.forms.impl.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the fmn_form_version database table.
 *
 */
@Entity
@Table(name = "fmn_form_version_request_info")
public class FormVersionRequestInfo implements Serializable {

	private static final long serialVersionUID = -3628983461955353560L;

	@Id
	private String id;

	@Version
	private long dbversion;

	// bi-directional many-to-one association to FormVersion
	@ManyToOne
	@JoinColumn(name = "form_version")
	private FormVersion formVersion;

	private String locale;

	private String data;

	public FormVersionRequestInfo() {
		id = UUID.randomUUID().toString();
	}

	public static FormVersionRequestInfo find(EntityManager em, String id) {
		return em.find(FormVersionRequestInfo.class, id);
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

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
