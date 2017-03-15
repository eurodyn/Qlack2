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
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="blg_layout")
public class BlgLayout  implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;
	
	@Id
    private String id;
    
	@Column(name="name")
	private String name;
    
	@Column(name="home")
	private String home;
    
	@OneToMany(fetch=FetchType.LAZY, mappedBy="blgLayoutId")
	private Set<BlgBlog> blgBlogs = new HashSet<BlgBlog>(0);

    public BlgLayout() {
    	id = UUID.randomUUID().toString();
    }

	public String getId() {
		 return this.id;
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

    public String getHome() {
		return home;
	}

    public void setHome(String home) {
        this.home = home;
    }

	public Set<BlgBlog> getBlgBlogs() {
		return blgBlogs;
	}

    public void setBlgBlogs(Set<BlgBlog> blgBlogs) {
        this.blgBlogs = blgBlogs;
    }
    
    public static BlgLayout find(EntityManager em, String layoutId) {
		return em.find(BlgLayout.class, layoutId);
	}
    
    public static BlgLayout findByName(EntityManager em, String name) {
		String myQuery = "SELECT g FROM BlgLayout g WHERE g.name=:name";

		try {
			return em.createQuery(myQuery, BlgLayout.class).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
    }
    
    public static List<BlgLayout> findAll(EntityManager em) {
		String myQuery = "SELECT g FROM BlgLayout g";

		try {
			return em.createQuery(myQuery, BlgLayout.class).getResultList();
		} catch (NoResultException e) {
			return null;
		}
    }
	
}


