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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the aaa_resource database table.
 *
 */
@Entity
@Table(name="aaa_resource")
public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Version
	private long dbversion;

	private String name;

	private String description;

	@Column(name="object_id")
	private String objectId;

	//bi-directional many-to-one association to UserHasOperation
	@OneToMany(mappedBy="resource")
	private List<UserHasOperation> userHasOperations;

	//bi-directional many-to-one association to GroupHasOperation
	@OneToMany(mappedBy="resource")
	private List<GroupHasOperation> groupHasOperations;
	
	//bi-directional many-to-one association to OpTemplateHasOperation
	@OneToMany(mappedBy="resource")
	private List<OpTemplateHasOperation> opTemplateHasOperations;

	public Resource() {
		id = UUID.randomUUID().toString();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public List<UserHasOperation> getUserHasOperations() {
		return userHasOperations;
	}

	public void setUserHasOperations(List<UserHasOperation> userHasOperations) {
		this.userHasOperations = userHasOperations;
	}

	public UserHasOperation addUserHasOperation(UserHasOperation userHasOperation) {
		if (getUserHasOperations() == null) {
			setUserHasOperations(new ArrayList<UserHasOperation>());
		}
		getUserHasOperations().add(userHasOperation);
		userHasOperation.setResource(this);

		return userHasOperation;
	}

	public UserHasOperation removeUserHasOperation(UserHasOperation userHasOperation) {
		getUserHasOperations().remove(userHasOperation);
		userHasOperation.setResource(null);

		return userHasOperation;
	}

	public List<GroupHasOperation> getGroupHasOperations() {
		return groupHasOperations;
	}

	public void setGroupHasOperations(List<GroupHasOperation> groupHasOperations) {
		this.groupHasOperations = groupHasOperations;
	}

	public GroupHasOperation addGroupHasOperation(GroupHasOperation groupHasOperation) {
		if (getGroupHasOperations() == null) {
			setGroupHasOperations(new ArrayList<GroupHasOperation>());
		}
		getGroupHasOperations().add(groupHasOperation);
		groupHasOperation.setResource(this);

		return groupHasOperation;
	}

	public GroupHasOperation removeGroupHasOperation(GroupHasOperation groupHasOperation) {
		getGroupHasOperations().remove(groupHasOperation);
		groupHasOperation.setResource(null);

		return groupHasOperation;
	}
	
	public List<OpTemplateHasOperation> getOpTemplateHasOperations() {
		return opTemplateHasOperations;
	}

	public void setOpTemplateHasOperations(
			List<OpTemplateHasOperation> opTemplateHasOperations) {
		this.opTemplateHasOperations = opTemplateHasOperations;
	}

	public OpTemplateHasOperation addOpTemplateHasOperation(
			OpTemplateHasOperation opTemplateHasOperation) {
		if (getOpTemplateHasOperations() == null) {
			setOpTemplateHasOperations(new ArrayList<OpTemplateHasOperation>());
		}
		getOpTemplateHasOperations().add(opTemplateHasOperation);
		opTemplateHasOperation.setResource(this);

		return opTemplateHasOperation;
	}

	public OpTemplateHasOperation removeOpTemplateHasOperation(
			OpTemplateHasOperation opTemplateHasOperation) {
		getOpTemplateHasOperations().remove(opTemplateHasOperation);
		opTemplateHasOperation.setResource(null);

		return opTemplateHasOperation;
	}
	
	public static Resource find(String resourceID, EntityManager em) {
		return em.find(Resource.class, resourceID);
	}

	public static Resource findByObjectID(final String resourceObjectID, final EntityManager em) {
        Resource retVal = null;

        Query q = em.createQuery("select r from Resource r where r.objectId = :objectID")
                .setParameter("objectID", resourceObjectID);
        List<Resource> l = q.getResultList();
        if (!l.isEmpty()) {
            retVal = l.get(0);
        }

        return retVal;
    }

}