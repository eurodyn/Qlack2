/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.fuse.cm.impl.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
@Entity
@Table(name = "cm_version")
public class Version {
	@Id
	private String id;
	@javax.persistence.Version
	private long dbversion;
	private String name;
	@ManyToOne
	@JoinColumn(name = "node")
	private Node node;
	@Column(name = "created_on")
	private long createdOn;
	private String filename;
	// The media type of the latest version.
	private String mimetype;
	
	// The size of the latest version.
	private Long size;

	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	@OneToMany(mappedBy = "version", cascade = CascadeType.ALL)
	private List<VersionAttribute> attributes;
	@OneToMany(mappedBy = "version", cascade = CascadeType.ALL)
	private List<VersionBin> versionBins;

	public Version() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}


	public List<VersionAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<VersionAttribute> attributes) {
		this.attributes = attributes;
	}
	

	/**
	 * @return the mimetype
	 */
	public String getMimetype() {
		return mimetype;
	}

	/**
	 * @param mimetype the mimetype to set
	 */
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	
	public static Version find(String versionID, EntityManager em) {
		return em.find(Version.class, versionID);
	}
	
	
	/**
	 * @return the versionBins
	 */
	public List<VersionBin> getVersionBins() {
		return versionBins;
	}

	/**
	 * @param versionBins the versionBins to set
	 */
	public void setVersionBins(List<VersionBin> versionBins) {
		this.versionBins = versionBins;
	}

	public static Version find(String fileID, String versionName,
			EntityManager em) {
		Version retVal = null;
		if (versionName != null) {
			Query query = em.createQuery("SELECT v FROM Version v WHERE v.node.id = :fileID and v.name = :versionName");
			query.setParameter("fileID", fileID);
			query.setParameter("versionName", versionName);
			retVal = (Version) query.getSingleResult();
		} else {
			retVal = findLatest(fileID, em);
		}
		return retVal;
	}
	
	public VersionAttribute getAttribute(String name) {
		for (VersionAttribute attribute : attributes) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}
	
	public static Version findLatest(String fileID, EntityManager em) {
		Query query = em.createQuery("SELECT v FROM Version v WHERE v.node.id = :fileID order by v.createdOn DESC");
		query.setParameter("fileID", fileID);
		query.setMaxResults(1);
		return (Version) query.getResultList().get(0);
	}
	
	public void setAttribute(String name, String value, EntityManager em) {
		VersionAttribute attribute = getAttribute(name);
		if (attribute == null) {
			attribute = new VersionAttribute();
			attribute.setVersion(this);
			attribute.setName(name);
		}
		attribute.setValue(value);
		em.merge(attribute);
	}
	
	public void removeAttribute(String name, EntityManager em) {
		em.remove(getAttribute(name));
	}
	
	
}