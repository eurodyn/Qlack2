package com.eurodyn.qlack2.be.rules.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.eurodyn.qlack2.be.rules.api.dto.VersionState;

@Entity
@Table(name = "rul_working_set_version")
public class WorkingSetVersion implements Serializable {

	private static final long serialVersionUID = 9001702972345448562L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@ManyToOne
	@JoinColumn(name = "working_set_id")
	private WorkingSet workingSet;

	private String name;

	private String description;

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

	@ManyToMany
	@JoinTable(name = "rul_working_sets_rules",
		joinColumns = {
			@JoinColumn(name = "working_set_version_id")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "rule_version_id")
		}
	)
	private List<RuleVersion> rules;

	@ManyToMany
	@JoinTable(name = "rul_working_sets_data_models",
		joinColumns = {
			@JoinColumn(name = "working_set_version_id")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "data_model_version_id")
		}
	)
	private List<DataModelVersion> dataModels;

	@ManyToMany
	@JoinTable(name = "rul_working_sets_libraries",
		joinColumns = {
			@JoinColumn(name = "working_set_version_id")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "library_version_id")
		}
	)
	private List<LibraryVersion> libraries;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "data_model_versions_jar")
	private byte[] dataModelsJar;

	@OneToOne(mappedBy = "workingSetVersion")
	private WorkingSetVersionKnowledgeBase knowledgeBase;

	// -- Constructors

	public WorkingSetVersion() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final WorkingSetVersion findById(EntityManager em, String id) {
		String jpql =
				"SELECT v " +
				"FROM WorkingSetVersion v " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, WorkingSetVersion.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final WorkingSetVersion findByWorkingSetAndName(EntityManager em, String setId, String name) {
		String jpql =
				"SELECT v " +
				"FROM WorkingSetVersion v " +
				"WHERE v.workingSet.id = :setId AND v.name = :name";

		try {
			return em.createQuery(jpql, WorkingSetVersion.class)
					.setParameter("setId", setId)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final String findIdByName(EntityManager em, String projectId, String workingSetName, String name) {
		String jpql =
				"SELECT v.id " +
				"FROM WorkingSetVersion v " +
				"WHERE v.workingSet.projectId = :projectId AND v.workingSet.name = :workingSetName AND v.name = :name";

		try {
			return em.createQuery(jpql, String.class)
					.setParameter("projectId", projectId)
					.setParameter("workingSetName", workingSetName)
					.setParameter("name", name)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final List<WorkingSetVersion> findByWorkingSetId(EntityManager em, String setId) {
		String jpql =
				"SELECT v " +
				"FROM WorkingSetVersion v " +
				"WHERE v.workingSet.id = :setId " +
				"ORDER BY v.name ASC";

		return em.createQuery(jpql, WorkingSetVersion.class).setParameter("setId", setId).getResultList();
	}

	public static final List<String> findIdsByWorkingSetId(EntityManager em, String setId) {
		String jpql =
				"SELECT v.id " +
				"FROM WorkingSetVersion v " +
				"WHERE v.workingSet.id = :setId";

		return em.createQuery(jpql, String.class).setParameter("setId", setId).getResultList();
	}

	public static final List<WorkingSetVersion> findByProjectId(EntityManager em, String projectId) {
		String jpql =
				"SELECT v " +
				"FROM WorkingSetVersion v " +
				"JOIN v.workingSet w " +
				"WHERE w.projectId = :projectId AND (v.state = :testing OR v.state = :final) " +
				"ORDER BY w.name, v.name ASC";

		return em.createQuery(jpql, WorkingSetVersion.class)
				.setParameter("projectId", projectId)
				.setParameter("testing", VersionState.TESTING)
				.setParameter("final", VersionState.FINAL)
				.getResultList();
	}

	public static long countLockedByOtherUser(EntityManager em, String setId, String userId) {
		String jpql =
				"SELECT count(v) " +
				"FROM WorkingSetVersion v " +
				"WHERE v.workingSet.id = :setId AND v.lockedBy IS NOT NULL AND v.lockedBy <> :userId";

		return em.createQuery(jpql, Long.class)
				.setParameter("setId", setId)
				.setParameter("userId", userId)
				.getSingleResult();
	}

	public static List<WorkingSetVersion> findContainingRuleVersion(EntityManager em, String versionId) {
		String jpql =
				"SELECT v " +
				"FROM WorkingSetVersion v " +
				"JOIN v.workingSet w " +
				"JOIN v.rules r " +
				"WHERE r.id = :versionId";

		return em.createQuery(jpql, WorkingSetVersion.class)
				.setParameter("versionId", versionId)
				.getResultList();
	}

	public static List<WorkingSetVersion> findContainingDataModelVersion(EntityManager em, String versionId) {
		String jpql =
				"SELECT v " +
				"FROM WorkingSetVersion v " +
				"JOIN v.workingSet w " +
				"JOIN v.dataModels m " +
				"WHERE m.id = :versionId";

		return em.createQuery(jpql, WorkingSetVersion.class)
				.setParameter("versionId", versionId)
				.getResultList();
	}

	public static List<WorkingSetVersion> findContainingLibraryVersion(EntityManager em, String versionId) {
		String jpql =
				"SELECT v " +
				"FROM WorkingSetVersion v " +
				"JOIN v.workingSet w " +
				"JOIN v.libraries l " +
				"WHERE l.id = :versionId";

		return em.createQuery(jpql, WorkingSetVersion.class)
				.setParameter("versionId", versionId)
				.getResultList();
	}

	public static long countContainingRule(EntityManager em, String ruleId) {
		String jpql =
				"SELECT count(v) " +
				"FROM WorkingSetVersion v " +
				"JOIN v.rules rv " +
				"WHERE rv.rule.id = :ruleId";

		return em.createQuery(jpql, Long.class)
				.setParameter("ruleId", ruleId)
				.getSingleResult();
	}

	public static long countContainingDataModel(EntityManager em, String modelId) {
		String jpql =
				"SELECT count(v) " +
				"FROM WorkingSetVersion v " +
				"JOIN v.dataModels mv " +
				"WHERE mv.dataModel.id = :modelId";

		return em.createQuery(jpql, Long.class)
				.setParameter("modelId", modelId)
				.getSingleResult();
	}

	public static long countContainingLibrary(EntityManager em, String libraryId) {
		String jpql =
				"SELECT count(v) " +
				"FROM WorkingSetVersion v " +
				"JOIN v.libraries lv " +
				"WHERE lv.library.id = :libraryId";

		return em.createQuery(jpql, Long.class)
				.setParameter("libraryId", libraryId)
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

	public WorkingSet getWorkingSet() {
		return workingSet;
	}

	public void setWorkingSet(WorkingSet workingSet) {
		this.workingSet = workingSet;
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

	public List<RuleVersion> getRules() {
		return rules;
	}

	public void setRules(List<RuleVersion> rules) {
		this.rules = rules;
	}

	public List<DataModelVersion> getDataModels() {
		return dataModels;
	}

	public void setDataModels(List<DataModelVersion> dataModels) {
		this.dataModels = dataModels;
	}

	public List<LibraryVersion> getLibraries() {
		return libraries;
	}

	public void setLibraries(List<LibraryVersion> libraries) {
		this.libraries = libraries;
	}

	public byte[] getDataModelsJar() {
		return dataModelsJar;
	}

	public void setDataModelsJar(byte[] dataModelsJar) {
		this.dataModelsJar = dataModelsJar;
	}

	public WorkingSetVersionKnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	public void setKnowledgeBase(WorkingSetVersionKnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}

}
