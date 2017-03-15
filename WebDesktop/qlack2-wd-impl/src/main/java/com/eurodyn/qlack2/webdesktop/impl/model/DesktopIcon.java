package com.eurodyn.qlack2.webdesktop.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import org.apache.commons.collections.CollectionUtils;


/**
 * The persistent class for the desktop_icon database table.
 * 
 */
@Entity
@Table(name="wd_desktop_icon")
public class DesktopIcon implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@Column(name = "user_id")
	String userId;
	@ManyToOne
	@JoinColumn(name = "application")
	private Application application;

	public DesktopIcon() {
		id = UUID.randomUUID().toString();
	}
	
	@SuppressWarnings("unchecked")
	public static List<DesktopIcon> getDesktopIconsForUser(String userId, EntityManager em) {
		Query query = em.createQuery("SELECT i FROM DesktopIcon i WHERE i.userId = :userId AND i.application.bundleSymbolicName IS NOT NULL");
		query.setParameter("userId", userId);
		return query.getResultList();
	}
	
	public static DesktopIcon getDesktopIconForUserAndApplication(String userId, String appUuid, EntityManager em) {
		Query query = em.createQuery("SELECT i FROM DesktopIcon i WHERE i.userId = :userId AND i.application.appUuid = :appUuid");
		query.setParameter("userId", userId);
		query.setParameter("appUuid", appUuid);
		@SuppressWarnings("unchecked")
		List<DesktopIcon> queryResult =  query.getResultList();
		if (CollectionUtils.isEmpty(queryResult)) {
			return null;
		} else {
			return queryResult.get(0);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

}