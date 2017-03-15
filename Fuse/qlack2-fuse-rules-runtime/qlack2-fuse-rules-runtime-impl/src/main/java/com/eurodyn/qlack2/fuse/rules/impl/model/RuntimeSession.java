package com.eurodyn.qlack2.fuse.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "rul_runtime_ksession")
public class RuntimeSession implements Serializable {
	private static final long serialVersionUID = -3899085624479625816L;

	@Id
	private String id;

	@Column(name = "session_id")
	private int sessionId;

	@ManyToOne
	@JoinColumn(name = "kbase_state_id")
	private RuntimeBaseState base;

	@OneToMany(mappedBy = "session", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<RuntimeGlobal> globals;

	// -- Constructors

	public RuntimeSession() {
		id = UUID.randomUUID().toString();
	}

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public RuntimeBaseState getBase() {
		return base;
	}

	public void setBase(RuntimeBaseState base) {
		this.base = base;
	}

	public List<RuntimeGlobal> getGlobals() {
		return globals;
	}

	public void setGlobals(List<RuntimeGlobal> globals) {
		this.globals = globals;
	}

}
