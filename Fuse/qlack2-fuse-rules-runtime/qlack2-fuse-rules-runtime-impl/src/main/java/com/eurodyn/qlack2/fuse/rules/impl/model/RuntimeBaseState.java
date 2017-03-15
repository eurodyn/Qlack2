package com.eurodyn.qlack2.fuse.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "rul_runtime_kbase_state")
public class RuntimeBaseState implements Serializable {
	private static final long serialVersionUID = -3899085624479625816L;

	@Id
	private String id;

	/** compiled rules */
	@Lob
	private byte[] state;

	@OneToMany(mappedBy = "base", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<RuntimeBaseLibrary> libraries;

	@OneToMany(mappedBy = "base", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<RuntimeSession> sessions;

	// -- Constructors

	public RuntimeBaseState() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static RuntimeBaseState findById(EntityManager em, String id) {
		String jpql =
				"SELECT b " +
				"FROM RuntimeBaseState b " +
				"LEFT JOIN FETCH b.libraries " +
				"WHERE b.id = :id";

		try {
			return em.createQuery(jpql, RuntimeBaseState.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	// -- Accessors

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getState() {
		return state;
	}

	public void setState(byte[] state) {
		this.state = state;
	}

	public List<RuntimeBaseLibrary> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<RuntimeBaseLibrary> libraries) {
		this.libraries = libraries;
	}

	public List<RuntimeSession> getSessions() {
		return sessions;
	}

	public void setSessions(List<RuntimeSession> sessions) {
		this.sessions = sessions;
	}

}
