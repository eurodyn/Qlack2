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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * The persistent class for the aaa_op_template database table.
 * 
 */
@Entity
@Table(name = "aaa_op_template")
public class OpTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Version
	private long dbversion;

	private String description;

	private String name;

	// bi-directional many-to-one association to OpTemplateHasOperation
	@OneToMany(mappedBy = "template")
	private List<OpTemplateHasOperation> opTemplateHasOperations;

	public OpTemplate() {
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

	public List<OpTemplateHasOperation> getOpTemplateHasOperations() {
		return this.opTemplateHasOperations;
	}

	public void setOpTemplateHasOperations(
			List<OpTemplateHasOperation> opTemplateHasOperations) {
		this.opTemplateHasOperations = opTemplateHasOperations;
	}

	public OpTemplateHasOperation addOpTemplateHasOperation(
			OpTemplateHasOperation opTemplateHasOperation) {
		if (getOpTemplateHasOperations() == null) {
			setOpTemplateHasOperations(new ArrayList<>());
		}
		getOpTemplateHasOperations().add(opTemplateHasOperation);
		opTemplateHasOperation.setTemplate(this);

		return opTemplateHasOperation;
	}

	public OpTemplateHasOperation removeOpTemplateHasOperation(
			OpTemplateHasOperation opTemplateHasOperation) {
		getOpTemplateHasOperations().remove(opTemplateHasOperation);
		opTemplateHasOperation.setTemplate(null);

		return opTemplateHasOperation;
	}

	public static OpTemplate find(String opTemplateID, EntityManager em) {
		return em.find(OpTemplate.class, opTemplateID);
	}

	public static OpTemplate findByName(final String opTemplateName,
			final EntityManager em) {
		OpTemplate retVal = null;

		Query q = em
				.createQuery("select ot from OpTemplate ot where ot.name = :opTemplateName");
		q.setParameter("opTemplateName", opTemplateName);
		List<OpTemplate> l = q.getResultList();
		if (!l.isEmpty()) {
			retVal = l.get(0);
		}

		return retVal;
	}

}