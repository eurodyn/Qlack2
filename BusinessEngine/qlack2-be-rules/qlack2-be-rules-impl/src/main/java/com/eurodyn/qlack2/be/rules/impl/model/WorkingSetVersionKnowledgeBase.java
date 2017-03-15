package com.eurodyn.qlack2.be.rules.impl.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "rul_working_set_version_kbase")
public class WorkingSetVersionKnowledgeBase {

	@Id
	@Column(name = "working_set_version_id")
	private String workingSetVersionId;

	@OneToOne
	@PrimaryKeyJoinColumn(name = "working_set_version_id")
	private WorkingSetVersion workingSetVersion;

	@Column(name = "knowledge_base_id")
	private String knowledgeBaseId;

	// -- Queries

	public static String findKnowledgeBaseIdByWorkingSetVersionId(EntityManager em, String workingSetVersionId) {
		String jpql =
				"SELECT wskb.knowledgeBaseId " +
				"FROM WorkingSetVersionKnowledgeBase wskb " +
				"WHERE wskb.workingSetVersionId = :workingSetVersionId";

		try {
			return em.createQuery(jpql, String.class).setParameter("workingSetVersionId", workingSetVersionId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public static String findWorkingSetVersionIdByKnowledgeBaseId(EntityManager em, String knowledgeBaseId) {
		String jpql =
				"SELECT wskb.workingSetVersionId " +
				"FROM WorkingSetVersionKnowledgeBase wskb " +
				"WHERE wskb.knowledgeBaseId = :knowledgeBaseId";

		try {
			return em.createQuery(jpql, String.class).setParameter("knowledgeBaseId", knowledgeBaseId).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	// -- Accessors

	public String getWorkingSetVersionId() {
		return workingSetVersionId;
	}

	public void setWorkingSetVersionId(String workingSetVersionId) {
		this.workingSetVersionId = workingSetVersionId;
	}

	public WorkingSetVersion getWorkingSetVersion() {
		return workingSetVersion;
	}

	public void setWorkingSetVersion(WorkingSetVersion workingSetVersion) {
		this.workingSetVersion = workingSetVersion;
	}

	public String getKnowledgeBaseId() {
		return knowledgeBaseId;
	}

	public void setKnowledgeBaseId(String knowledgeBaseId) {
		this.knowledgeBaseId = knowledgeBaseId;
	}

}
