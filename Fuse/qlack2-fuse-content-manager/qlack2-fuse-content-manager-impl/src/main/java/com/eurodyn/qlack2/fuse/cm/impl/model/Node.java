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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import com.eurodyn.qlack2.fuse.cm.api.util.CMConstants;

@Entity
@Table(name = "cm_node")
public class Node {
	@Id
	private String id;
	@Version
	private long dbversion;
	@Enumerated(EnumType.ORDINAL)
	private NodeType type;
	@ManyToOne
	@JoinColumn(name = "parent")
	private Node parent;
	@Column(name = "created_on")
	private long createdOn;
	@OneToMany(mappedBy = "parent")
	private List<Node> children;
	@Column(name = "lock_token")
	private String lockToken;
	@OneToMany(mappedBy = "node", cascade = CascadeType.ALL)
	private List<NodeAttribute> attributes;
	// The media type of the latest version.
	private String mimetype;
	// The size of the latest version.
	private Long size;
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


	

	public Node() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public NodeType getType() {
		return type;
	}

	public void setType(NodeType type) {
		this.type = type;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getLockToken() {
		return lockToken;
	}

	public void setLockToken(String lockToken) {
		this.lockToken = lockToken;
	}
	
	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public List<NodeAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<NodeAttribute> attributes) {
		this.attributes = attributes;
	}
	
	public static Node findNode(String nodeID, EntityManager em)  {
		return em.find(Node.class, nodeID);
	}
	
	public static Node findFolder(String nodeID, EntityManager em)  {
		Node node = findNode(nodeID, em);
		if ((node != null) &&(node.getType() == NodeType.FOLDER)) {
			return node;
		}
		return null;
	}
	
	public static Node findFile(String nodeID, EntityManager em)  {
		Node node = findNode(nodeID, em);
		if ((node != null) && (node.getType() == NodeType.FILE)) {
			return node;
		}
		return null;
	}

	public NodeAttribute getAttribute(String name) {
		for (NodeAttribute attribute : attributes) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}
	
	
	public void setAttribute(String name, String value, EntityManager em) {
		NodeAttribute attribute = getAttribute(name);
		if (attribute == null) {
			attribute = new NodeAttribute();
			attribute.setNode(this);
			attribute.setName(name);
		}
		attribute.setValue(value);
		em.merge(attribute);
	}
	
	public void removeAttribute(String name, EntityManager em) {
		// remove the attribute itself
		em.remove(getAttribute(name));
		// remove the attribute from the list to remove the relation
		List <NodeAttribute> l = this.getAttributes();
		l.remove(this.getAttribute(name));
		this.setAttributes(l);
	}

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
}
