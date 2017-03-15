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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="blg_blog", uniqueConstraints = @UniqueConstraint(columnNames="name"))
public class BlgBlog  implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;
	
	@Id
	private String id;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blg_layout")
    private BlgLayout blgLayoutId;
    
	@Column(name="name")
	private String name;
	
	@Column(name="user_id")
    private String userId;
    
	@Column(name="picture")
	private byte[] picture;
	
	@Column(name="rss_feed_enabled")
    private Boolean rssFeedEnabled;
    
	@Column(name="language")
	private String language;
    
	@OneToMany(fetch=FetchType.LAZY, mappedBy="blogId")
	private Set<BlgCategory> blgCategories = new HashSet<BlgCategory>(0);
    
	@OneToMany(fetch=FetchType.LAZY, mappedBy="blogId")
	private Set<BlgPost> blgPosts = new HashSet<BlgPost>(0);
    
	@OneToMany(fetch=FetchType.LAZY, mappedBy="blogId")
	private Set<BlgFlag> blgFlags = new HashSet<BlgFlag>(0);

    public BlgBlog() {
    	id = UUID.randomUUID().toString();
    }

	public String getId() {
		 return id;
	}

    public void setId(String id) {
        this.id = id;
    }
    

	public BlgLayout getBlgLayoutId() {
		return blgLayoutId;
	}

    public void setBlgLayoutId(BlgLayout blgLayoutId) {
        this.blgLayoutId = blgLayoutId;
    }
    
	public String getName() {
		return name;
	}

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
		return userId;
	}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte[] getPicture() {
		return picture;
	}

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Boolean getRssFeedEnabled() {
		return rssFeedEnabled;
	}

    public void setRssFeedEnabled(Boolean rssFeedEnabled) {
        this.rssFeedEnabled = rssFeedEnabled;
    }

    public String getLanguage() {
		return language;
	}

    public void setLanguage(String language) {
        this.language = language;
    }
    
	public Set<BlgCategory> getBlgCategories() {
		return blgCategories;
	}

    public void setBlgCategories(Set<BlgCategory> blgCategories) {
        this.blgCategories = blgCategories;
    }

	public Set<BlgPost> getBlgPosts() {
		return blgPosts;
	}

    public void setBlgPosts(Set<BlgPost> blgPosts) {
        this.blgPosts = blgPosts;
    }

	public Set<BlgFlag> getBlgFlags() {
		return blgFlags;
	}

    public void setBlgFlags(Set<BlgFlag> blgFlags) {
        this.blgFlags = blgFlags;
    }
    
    public static BlgBlog find(EntityManager em, String id) {
		return em.find(BlgBlog.class, id);
	}
    
    public static BlgBlog findByName(EntityManager em, String name) {
		String myQuery = "SELECT g FROM BlgBlog g WHERE g.name=:name";

		try {
			return em.createQuery(myQuery, BlgBlog.class).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
    
    public static List<BlgBlog> findByUser(EntityManager em, String userId) {
		Query query = em.createQuery("SELECT b FROM BlgBlog b WHERE b.userId = :userId");
		query.setParameter("userId", userId);
        return query.getResultList();
	}
}


