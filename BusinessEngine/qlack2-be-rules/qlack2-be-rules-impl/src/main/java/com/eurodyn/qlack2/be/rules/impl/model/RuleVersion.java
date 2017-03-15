package com.eurodyn.qlack2.be.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Table;
import javax.persistence.Version;

import com.eurodyn.qlack2.be.rules.api.dto.VersionState;

@Entity
@Table(name = "rul_rule_version")
public class RuleVersion implements Serializable {

	private static final long serialVersionUID = 9001702972345448562L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@ManyToOne
	private Rule rule;

	private String name;

	private String description;

	private String content;

	@Column(name = "rule_name")
	private String ruleName; // parse content and cache rule name

	@Enumerated(EnumType.ORDINAL)
	private VersionState state;

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

	@ManyToMany(mappedBy = "rules")
	private List<WorkingSetVersion> workingSets;

	// -- Constructors

	public RuleVersion() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final String findRuleIdById(EntityManager em, String id) {
		String jpql =
				"SELECT v.rule.id " +
				"FROM RuleVersion v " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, String.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final RuleVersion findById(EntityManager em, String id) {
		String jpql =
				"SELECT v " +
				"FROM RuleVersion v " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, RuleVersion.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final RuleVersion findByRuleAndName(EntityManager em, String ruleId, String name) {
		String jpql =
				"SELECT v " +
				"FROM RuleVersion v " +
				"WHERE v.rule.id = :ruleId AND v.name = :name";

		try {
			return em.createQuery(jpql, RuleVersion.class)
					.setParameter("ruleId", ruleId)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final String findIdByName(EntityManager em, String projectId, String ruleName, String name) {
		String jpql =
				"SELECT v.id " +
				"FROM RuleVersion v " +
				"WHERE v.rule.projectId = :projectId AND v.rule.name = :ruleName AND v.name = :name";

		try {
			return em.createQuery(jpql, String.class)
					.setParameter("projectId", projectId)
					.setParameter("ruleName", ruleName)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final List<RuleVersion> findByRuleId(EntityManager em, String ruleId) {
		String jpql =
				"SELECT v " +
				"FROM RuleVersion v " +
				"WHERE v.rule.id = :ruleId " +
				"ORDER BY v.name ASC";

		return em.createQuery(jpql, RuleVersion.class).setParameter("ruleId", ruleId).getResultList();
	}

	public static final List<String> findIdsByRuleId(EntityManager em, String ruleId) {
		String jpql =
				"SELECT v.id " +
				"FROM RuleVersion v " +
				"WHERE v.rule.id = :ruleId";

		return em.createQuery(jpql, String.class).setParameter("ruleId", ruleId).getResultList();
	}

	public static final List<RuleVersion> findSystemByProjectId(EntityManager em, String projectId) {
		String jpql =
				"SELECT v " +
				"FROM RuleVersion v " +
				"JOIN v.rule r " +
				"JOIN FETCH v.workingSets " +
				"WHERE r.projectId = :projectId AND (v.state = :testing OR v.state = :final) " +
				"ORDER BY r.name, v.name ASC";

		return em.createQuery(jpql, RuleVersion.class)
				.setParameter("projectId", projectId)
				.setParameter("testing", VersionState.TESTING)
				.setParameter("final", VersionState.FINAL)
				.getResultList();
	}

	public static final List<RuleVersion> findByWorkingSetVersionId(EntityManager em, String workingSetVersionId) {
		String jpql =
				"SELECT v " +
				"FROM RuleVersion v " +
				"JOIN v.workingSets w " +
				"WHERE w.id = :workingSetVersionId";

		return em.createQuery(jpql, RuleVersion.class).setParameter("workingSetVersionId", workingSetVersionId).getResultList();
	}

	public static long countLockedByOtherUser(EntityManager em, String ruleId, String userId) {
		String jpql =
				"SELECT count(v) " +
				"FROM RuleVersion v " +
				"WHERE v.rule.id = :ruleId AND v.lockedBy IS NOT NULL AND v.lockedBy <> :userId";

		return em.createQuery(jpql, Long.class)
				.setParameter("ruleId", ruleId)
				.setParameter("userId", userId)
				.getSingleResult();
	}

	public static String findRuleNameById(EntityManager em, String id) {
		String jpql =
				"SELECT v.ruleName " +
				"FROM RuleVersion v " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, String.class)
					.setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Count rule versions in specified project that have the specified rule name,
	 * excluding rule versions that belong to the specified rule.
	 */
	public static long countMatchingRuleName(EntityManager em, String name, String ruleId, String projectId) {
		String jpql =
				"SELECT COUNT(v) " +
				"FROM RuleVersion v " +
				"JOIN v.rule r " +
				"WHERE v.ruleName = :name AND r.id <> :ruleId AND r.projectId = :projectId";

		return em.createQuery(jpql, Long.class)
				.setParameter("name", name)
				.setParameter("ruleId", ruleId)
				.setParameter("projectId", projectId)
				.getSingleResult();
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

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
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

	public VersionState getState() {
		return state;
	}

	public void setState(VersionState state) {
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public List<WorkingSetVersion> getWorkingSets() {
		return workingSets;
	}

	public void setWorkingSets(List<WorkingSetVersion> workingSets) {
		this.workingSets = workingSets;
	}
}
