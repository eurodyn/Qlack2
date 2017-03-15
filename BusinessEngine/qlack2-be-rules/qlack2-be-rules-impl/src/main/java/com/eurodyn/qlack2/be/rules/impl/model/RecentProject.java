package com.eurodyn.qlack2.be.rules.impl.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "rul_recent_project")
public class RecentProject implements Serializable {

	private static final long serialVersionUID = 3579586130522949344L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@Column(name = "project_id")
	private String projectId;

	@Column(name = "last_accessed_on")
	private long lastAccessedOn;

	@Column(name = "last_accessed_by")
	private String lastAccessedBy;

	// -- Constructors

	public RecentProject() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static RecentProject find(EntityManager em, String id) {
		return em.find(RecentProject.class, id);
	}

	public static RecentProject getRecentProjectByProjectId(EntityManager em, String projectId) {
		String jpql = "SELECT r FROM RecentProject r WHERE r.projectId = :projectId";

		try {
			return em.createQuery(jpql, RecentProject.class)
					.setParameter("projectId", projectId)
					.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
	}

	public static RecentProject getRecentProjectByProjectIdAndUserId(EntityManager em, String projectId, String userId) {
		String jpql = "SELECT r FROM RecentProject r WHERE r.projectId = :projectId AND r.lastAccessedBy = :userId";

		try {
			return em.createQuery(jpql, RecentProject.class)
					.setParameter("projectId", projectId)
					.setParameter("userId", userId)
					.getSingleResult();
		}
		catch (NoResultException e) {
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

	public long getLastAccessedOn() {
		return lastAccessedOn;
	}

	public void setLastAccessedOn(long lastAccessedOn) {
		this.lastAccessedOn = lastAccessedOn;
	}

	public String getLastAccessedBy() {
		return lastAccessedBy;
	}

	public void setLastAccessedBy(String lastAccessedBy) {
		this.lastAccessedBy = lastAccessedBy;
	}

}
