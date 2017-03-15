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
package com.eurodyn.qlack2.fuse.blog.impl.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="blg_tag")
public class BlgTag  implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;

	@Id
    private String id;
    
	@Column(name="name")
	private String name;
    
	@Column(name="description")
	private String description;
    
	// bi-directional many-to-many association to Workflow
	@ManyToMany(mappedBy = "blgPostHasTags")
	private List<BlgPost> blgPostHasTags;

    public BlgTag() {
    	id = java.util.UUID.randomUUID().toString();
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

    public String getDescription() {
		return description;
	}

    public void setDescription(String description) {
        this.description = description;
    }

	public List<BlgPost> getBlgPostHasTags() {
		return blgPostHasTags;
	}

    public void setBlgPostHasTags(List<BlgPost> blgPostHasTags) {
        this.blgPostHasTags = blgPostHasTags;
    }
    
    public static BlgTag find(EntityManager em, String id) {
		return em.find(BlgTag.class, id);
	}
    
    public static List<BlgTag> findByBlog(EntityManager em, String blogId) {
		Query query = em.createQuery("select distinct bts from BlgTag bts, BlgPostHasTag bpht, BlgPost bp "
						+ " where bts.id=bpht.tagId.id and bpht.postId.id = bp.id and bp.blogId.id =:blogId");
		query.setParameter("blogId", blogId);
        return query.getResultList();
	}
       
    public static BlgTag findByName(EntityManager em, String name) {
		String myQuery = "SELECT g FROM BlgTag g WHERE g.name=:name";

		try {
			return em.createQuery(myQuery, BlgTag.class).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
    
    public static List<BlgTag> findAll(EntityManager em) {
		String myQuery = "SELECT g FROM BlgTag g";

		try {
			return em.createQuery(myQuery, BlgTag.class).getResultList();
		} catch (NoResultException e) {
			return null;
		}
    }
}


