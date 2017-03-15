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
package com.eurodyn.qlack2.fuse.aaa.impl.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the aaa_group_has_operation database table.
 *
 */
@Entity
@Table(name="aaa_group_has_operation")
public class GroupHasOperation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Version
	private long dbversion;

	//bi-directional many-to-one association to Group
	@ManyToOne
	@JoinColumn(name="group_id")
	private Group group;

	//bi-directional many-to-one association to Operation
	@ManyToOne
	@JoinColumn(name="operation")
	private Operation operation;
	
	//bi-directional many-to-one association to Resource
	@ManyToOne
	@JoinColumn(name="resource_id")
	private Resource resource;
	
	private boolean deny;

	public GroupHasOperation() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Operation getOperation() {
		return this.operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

    public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public boolean isDeny() {
		return deny;
	}

	public void setDeny(boolean deny) {
		this.deny = deny;
	}

	public static GroupHasOperation findByGroupIDAndOperationName(String groupID,
			String operationName, EntityManager em) {
		Query q = em.createQuery ("SELECT o FROM GroupHasOperation o WHERE "
                + "o.group.id = :groupID AND o.operation.name = :operationName AND o.resource IS NULL");
        q.setParameter("groupID", groupID);
        q.setParameter("operationName", operationName);
        List<GroupHasOperation> queryResults = q.getResultList();
        if (queryResults.isEmpty()) {
            return null;
        }
        return queryResults.get(0);
	}
	
	public static GroupHasOperation findByGroupAndResourceIDAndOperationName(
			String groupID, String operationName, String resourceID, EntityManager em) {
        Query q = em.createQuery ("SELECT o FROM GroupHasOperation o WHERE "
                + "o.group.id = :groupID AND o.operation.name = :operationName AND o.resource.id = :resourceID");
        q.setParameter("groupID", groupID);
        q.setParameter("operationName", operationName);
        q.setParameter("resourceID", resourceID);
        List<GroupHasOperation> queryResults = q.getResultList();
        if (queryResults.isEmpty()) {
            return null;
        }
        return queryResults.get(0);
    }
	
	public static List<GroupHasOperation> findByOperationName(String operationName,
			EntityManager em) {
		Query q = em.createQuery ("SELECT o FROM GroupHasOperation o WHERE "
                + "o.operation.name = :operationName AND o.resource IS NULL");
        q.setParameter("operationName", operationName);
        return q.getResultList();
	}
	
	public static List<GroupHasOperation> findByResourceIDAndOperationName(String operationName,
			String resourceID, EntityManager em) {
		Query q = em.createQuery ("SELECT o FROM GroupHasOperation o WHERE "
                + "o.operation.name = :operationName AND o.resource.id = :resourceID");
        q.setParameter("operationName", operationName);
        q.setParameter("resourceID", resourceID);
        return q.getResultList();
	}

}