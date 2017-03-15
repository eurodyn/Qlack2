package com.eurodyn.qlack2.be.workflow.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the fmn_recent_project database table.
 *
 */
@Entity
@Table(name = "wfl_recent_project")
public class RecentProject implements Serializable {

	/**
	 *
	 */
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

	public RecentProject() {
		id = UUID.randomUUID().toString();
	}

	public static RecentProject find(EntityManager em, String id) {
		return em.find(RecentProject.class, id);
	}

	public static RecentProject getRecentProjectByProjectIdAndLastAccessedBy(EntityManager em, String projectId, String userId) {
		Query query = em
				.createQuery("SELECT r FROM RecentProject r WHERE r.projectId = :projectId and r.lastAccessedBy = :userId");
		query.setParameter("projectId", projectId);
		query.setParameter("userId", userId);

		List<RecentProject> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}
	
	public static RecentProject getRecentProjectByProjectId(EntityManager em, String projectId) {
		Query query = em
				.createQuery("SELECT r FROM RecentProject r WHERE r.projectId = :projectId");
		query.setParameter("projectId", projectId);

		List<RecentProject> resultList = query.getResultList();
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
