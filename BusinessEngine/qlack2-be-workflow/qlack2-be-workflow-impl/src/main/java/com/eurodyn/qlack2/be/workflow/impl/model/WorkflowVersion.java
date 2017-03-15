package com.eurodyn.qlack2.be.workflow.impl.model;

import java.io.Serializable;
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
import javax.persistence.OrderBy;

/**
 * The persistent class for the wfl_workflow_version database table.
 *
 */
@Entity
@Table(name = "wfl_workflow_version")
public class WorkflowVersion implements Serializable {

	private static final long serialVersionUID = 771831700149128164L;

	@Id
	private String id;

	@Version
	private long dbversion;

	//bi-directional many-to-one association to Workflow
	@ManyToOne
	@JoinColumn(name="workflow")
	private Workflow workflow;

	private String name;

	private String description;

	private String content;

	@Enumerated(EnumType.ORDINAL)
	private State state;
	
	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "created_on")
	private long createdOn;
	
	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@Column(name = "last_modified_on")
	private long lastModifiedOn;

	@Column(name = "locked_by")
	private String lockedBy;
	
	@Column(name = "locked_on")
	private Long lockedOn;
	
	@Column(name = "enable_testing")
    private boolean enableTesting;
	
	@Column(name = "processId")
	private String processId;
	
	// bi-directional many-to-one association to Condition
	@OneToMany(mappedBy = "workflowVersion", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@OrderBy(value = "name ASC")
	private List<Condition> conditions;

	public WorkflowVersion() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public long getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(long lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}
	
	public String getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}

	public Long getLockedOn() {
		return lockedOn;
	}

	public void setLockedOn(Long lockedOn) {
		this.lockedOn = lockedOn;
	}
	
	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
	
	public boolean isEnableTesting() {
        return enableTesting;
	}
	
	public void setEnableTesting(boolean enableTesting) {
	        this.enableTesting = enableTesting;
	}
	
	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public static WorkflowVersion find(EntityManager em, String workflowVersionId) {
		return em.find(WorkflowVersion.class, workflowVersionId);
	}
	
	public static WorkflowVersion findByProcessId(EntityManager em, String processId) {
		Query query = em
				.createQuery("SELECT v FROM WorkflowVersion v WHERE v.processId = :processId");
		query.setParameter("processId", processId);

		return (WorkflowVersion) query.getSingleResult();
	}
	
	public static Long countWorkflowVersionsLockedByOtherUser(EntityManager em, String workflowId, String user) {
		Query query = em.createQuery("SELECT count(v) FROM WorkflowVersion v WHERE v.workflow.id = :workflowId and v.lockedOn IS NOT NULL and v.lockedBy <> :user");
		query.setParameter("workflowId", workflowId);
		query.setParameter("user", user);

		return (Long) query.getSingleResult();
	}
	
	public static boolean checkWorkflowVersionLockedByOtherUser(EntityManager em, String workflowVersionId, String user) {
		Query query = em.createQuery("SELECT 1 FROM WorkflowVersion v WHERE v.id = :workflowVersionId and v.lockedOn IS NOT NULL and v.lockedBy <> :user");
		query.setParameter("workflowVersionId", workflowVersionId);
		query.setParameter("user", user);

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
	
	public static String getWorkflowVersionIdByName(EntityManager em, String name, String workflowId) {
		Query query = em
				.createQuery("SELECT v.id FROM WorkflowVersion v WHERE v.name = :name and v.workflow.id = :workflowId");
		query.setParameter("name", name);
		query.setParameter("workflowId", workflowId);

		List<String> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}
	
	public static final boolean processIdExists(EntityManager em, String processId) {
		String jpql = "SELECT 1 FROM WorkflowVersion g WHERE g.processId=:processId";

		try {
			em.createQuery(jpql, Integer.class).setParameter("processId", processId).getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}
}
