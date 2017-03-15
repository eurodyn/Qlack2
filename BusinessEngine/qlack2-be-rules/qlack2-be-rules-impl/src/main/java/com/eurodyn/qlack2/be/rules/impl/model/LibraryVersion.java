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
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

import com.eurodyn.qlack2.be.rules.api.dto.VersionState;

@Entity
@Table(name = "rul_library_version")
public class LibraryVersion implements Serializable {

	private static final long serialVersionUID = -2537491401104003973L;

	@Id
	private String id;

	@Version
	private long dbversion;

	@ManyToOne
	private Library library;

	private String name;

	private String description;

	// XXX .jar should be lazy loaded
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(name = "content_jar")
	private byte[] contentJar;

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

	@ManyToMany(mappedBy = "libraries")
	private List<WorkingSetVersion> workingSets;

	// -- Constructors

	public LibraryVersion() {
		id = UUID.randomUUID().toString();
	}

	// -- Queries

	public static final String findLibraryIdById(EntityManager em, String id) {
		String jpql =
				"SELECT v.library.id " +
				"FROM LibraryVersion v " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, String.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final LibraryVersion findById(EntityManager em, String id) {
		String jpql =
				"SELECT v " +
				"FROM LibraryVersion v " +
				"WHERE v.id = :id";

		try {
			return em.createQuery(jpql, LibraryVersion.class).setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static final List<LibraryVersion> findByLibraryId(EntityManager em, String libraryId) {
		String jpql =
				"SELECT v " +
				"FROM LibraryVersion v " +
				"WHERE v.library.id = :libraryId " +
				"ORDER BY v.name ASC";

		return em.createQuery(jpql, LibraryVersion.class).setParameter("libraryId", libraryId).getResultList();
	}

	public static final List<LibraryVersion> findByWorkingSetVersionId(EntityManager em, String workingSetVersionId) {
		String jpql =
				"SELECT v " +
				"FROM LibraryVersion v " +
				"JOIN v.workingSets w " +
				"WHERE w.id = :workingSetVersionId";

		return em.createQuery(jpql, LibraryVersion.class).setParameter("workingSetVersionId", workingSetVersionId).getResultList();
	}

	public static Long countLibraryVersionsLockedByOtherUser(EntityManager em, String libraryId, String user) {
		Query query = em.createQuery("SELECT count(v) FROM LibraryVersion v WHERE v.library.id = :libraryId and v.lockedOn IS NOT NULL and v.lockedBy <> :user");
		query.setParameter("libraryId", libraryId);
		query.setParameter("user", user);

		return (Long) query.getSingleResult();
	}

	public static boolean checkLibraryVersionLockedByOtherUser(EntityManager em, String libraryVersionId, String user) {
		Query query = em.createQuery("SELECT 1 FROM LibraryVersion v WHERE v.id = :libraryVersionId and v.lockedOn IS NOT NULL and v.lockedBy <> :user");
		query.setParameter("libraryVersionId", libraryVersionId);
		query.setParameter("user", user);

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	public static String getLibraryVersionIdByName(EntityManager em, String name, String libraryId) {
		String jpql = "SELECT v.id FROM LibraryVersion v WHERE v.name = :name AND v.library.id = :libraryId";

		List<String> resultList = em.createQuery(jpql, String.class)
				.setParameter("name", name)
				.setParameter("libraryId", libraryId)
				.getResultList();

		return resultList.isEmpty() ? null : resultList.get(0);
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

	public Library getLibrary() {
		return library;
	}

	public void setLibrary(Library library) {
		this.library = library;
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

	public byte[] getContentJar() {
		return contentJar;
	}

	public void setContentJar(byte[] contentJar) {
		this.contentJar = contentJar;
	}

	public List<WorkingSetVersion> getWorkingSets() {
		return workingSets;
	}

	public void setWorkingSets(List<WorkingSetVersion> workingSets) {
		this.workingSets = workingSets;
	}
}
