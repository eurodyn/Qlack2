package com.eurodyn.qlack2.be.workflow.impl.model;

import java.io.Serializable;
import java.util.Comparator;
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
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the wfl_category database table.
 *
 */
@Entity
@Table(name = "wfl_condition")
public class Condition implements Serializable {

	private static final long serialVersionUID = 8901007364617702190L;

	@Id
	private String id;

	@Version
	private long dbversion;

	// bi-directional many-to-one association to WorkflowVersion
	@ManyToOne
	@JoinColumn(name = "workflow_version")
	private WorkflowVersion workflowVersion;

	private String name;

	@Column(name = "condition_type")
	@Enumerated(EnumType.ORDINAL)
	private ConditionType conditionType;

	@Column(name = "working_set_id")
	private String workingSetId;

	@Column(name = "rule_id")
	private String ruleId;

	// bi-directional many-to-one association to Condition
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "parent")
	private Condition parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Condition> children;

	public Condition() {
		id = UUID.randomUUID().toString();
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

	public WorkflowVersion getWorkflowVersion() {
		return workflowVersion;
	}

	public void setWorkflowVersion(WorkflowVersion workflowVersion) {
		this.workflowVersion = workflowVersion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ConditionType getConditionType() {
		return conditionType;
	}

	public void setConditionType(ConditionType conditionType) {
		this.conditionType = conditionType;
	}

	public String getWorkingSetId() {
		return workingSetId;
	}

	public void setWorkingSetId(String workingSetId) {
		this.workingSetId = workingSetId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public Condition getParent() {
		return parent;
	}

	public void setParent(Condition parent) {
		this.parent = parent;
	}
	
	public List<Condition> getChildren() {
		return children;
	}

	public void setChildren(List<Condition> children) {
		this.children = children;
	}
	
	public static class ConditionComparator implements Comparator<Condition> {

		@Override
		public int compare(Condition o1, Condition o2) {
			return o1.getId().compareTo(o2.getId());
		}

	}
	
	public static Condition find(EntityManager em, String conditionId) {
		return em.find(Condition.class, conditionId);
	}
	
	public static Condition findByName(EntityManager em, String name) {
		String myQuery = "SELECT g FROM Condition g WHERE g.name=:name";

		try {
			return em.createQuery(myQuery, Condition.class).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public static long getConditionsCountForWorkingSetVersion(EntityManager em, String id) {
		Query query = em.createQuery("SELECT COUNT(c) FROM Condition c WHERE c.workingSetId = :id");
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public static long getConditionsCountForRuleVersion(EntityManager em, String id) {
		Query query = em.createQuery("SELECT COUNT(c) FROM Condition c WHERE c.ruleId = :id");
		query.setParameter("id", id);
		return (long) query.getSingleResult();
	}
	
	public static List<Condition> getConditionsWithoutParent(EntityManager em, String versionId, ConditionType conditionType) {
		Query query = em
				.createQuery("SELECT c FROM Condition c WHERE c.workflowVersion.id = :versionId and c.conditionType = :conditionType and c.parent is null");
		query.setParameter("versionId", versionId);
		query.setParameter("conditionType", conditionType);

		return query.getResultList();
	}
}
