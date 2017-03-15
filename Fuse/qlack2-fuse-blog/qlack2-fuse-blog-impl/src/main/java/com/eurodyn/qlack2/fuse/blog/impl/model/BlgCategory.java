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

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Table;

@Entity
@Table(name="blg_category")
public class BlgCategory  implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;
	
	@Id
    private String id;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blog")
	private BlgBlog blogId;
    
	@Column(name="name")
	private String name;
    
	@Column(name="description")
	private String description;
	
	// bi-directional many-to-many association
	@ManyToMany(mappedBy = "blgPostHasCategories")
	private List<BlgPost> blgPostHasCategories;

    public BlgCategory() {
    	id = UUID.randomUUID().toString();
    }

	public String getId() {
		return id;
	}

    public void setId(String id) {
        this.id = id;
    }

	public BlgBlog getBlogId() {
		return blogId;
	}

    public void setBlogId(BlgBlog blogId) {
        this.blogId = blogId;
    }
   
	public String getName() {
		return this.name;
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

	public List<BlgPost> getBlgPostHasCategories() {
		return blgPostHasCategories;
	}

    public void setBlgPostHasCategories(List<BlgPost> blgPostHasCategories) {
        this.blgPostHasCategories = blgPostHasCategories;
    }
    
    public static BlgCategory find(EntityManager em, String id) {
		return em.find(BlgCategory.class, id);
	}
    
    public static BlgCategory findByNameAndBlog(EntityManager em, String name, String blogId) {
		try {
			Query q = em.createQuery("SELECT c FROM BlgCategory c WHERE c.name = :name AND c.blogId.id = :blogId");
			q.setParameter("name", name);
			q.setParameter("blogId", blogId);
			return (BlgCategory) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
    
    public static List<BlgCategory> findByBlog(EntityManager em, String blogId) {
		Query query = em.createQuery("select bcs from BlgCategory bcs where bcs.blogId.id=:blogId");
		query.setParameter("blogId", blogId);
        return query.getResultList();
	}
}


