package com.eurodyn.qlack2.fuse.rules.impl.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "rul_runtime_kbase_library")
public class RuntimeBaseLibrary implements Serializable {
	private static final long serialVersionUID = -1428178339286367026L;

	@Id
	private String id;

	@Lob
	private byte[] library;

	@ManyToOne
	@JoinColumn(name = "kbase_state_id")
	private RuntimeBaseState base;

	// -- Constructors

	public RuntimeBaseLibrary() {
		id = UUID.randomUUID().toString();
	}

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getLibrary() {
		return library;
	}

	public void setLibrary(byte[] library) {
		this.library = library;
	}

	public RuntimeBaseState getBase() {
		return base;
	}

	public void setBase(RuntimeBaseState base) {
		this.base = base;
	}

}
