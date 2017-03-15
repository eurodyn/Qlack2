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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import com.eurodyn.qlack2.common.util.search.PagingParams;

@Entity
@Table(name="blg_post")
public class BlgPost implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;

	@Id
    private String id;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blog")
    private BlgBlog blogId;
    
	@Column(name="name")
    private String name;
    
	@Column(name="body")
    private String body;
    
	@Column(name="comments_enabled")
    private boolean commentsEnabled;
    
	@Column(name="date_posted")
    private Long datePosted;
    
	@Column(name="archived")
    private boolean archived;
    
	@Column(name="published")
    private boolean published;
    
	@Column(name="trackback_ping_url")
    private String trackbackPingUrl;
	
	@ManyToMany
	@JoinTable(name = "blg_post_has_category",
		joinColumns = {
			@JoinColumn(name = "post")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "category")
	})
	private List<BlgCategory> blgPostHasCategories;
	
	@ManyToMany
	@JoinTable(name = "blg_post_has_tag",
		joinColumns = {
			@JoinColumn(name = "post")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "tag")
	})
	private List<BlgTag> blgPostHasTags;   
      
	@OneToMany(fetch=FetchType.LAZY, mappedBy="postId", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BlgTrackbacks> blgTrackbacksesForPostId = new ArrayList<BlgTrackbacks>();
       
    @OneToMany(fetch=FetchType.LAZY, mappedBy="postId", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BlgComment> blgComments = new ArrayList<BlgComment>();

    public BlgPost() {
    	id = java.util.UUID.randomUUID().toString();
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
		return name;
	}

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
		return body;
	}

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isCommentsEnabled() {
		return commentsEnabled;
	}

    public void setCommentsEnabled(boolean commentsEnabled) {
        this.commentsEnabled = commentsEnabled;
    }

    public Long getDatePosted() {
		return datePosted;
	}

    public void setDatePosted(Long datePosted) {
        this.datePosted = datePosted;
    }

    public boolean isArchived() {
		return archived;
	}

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public boolean isPublished() {
		return published;
	}

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getTrackbackPingUrl() {
		return trackbackPingUrl;
	}

    public void setTrackbackPingUrl(String trackbackPingUrl) {
        this.trackbackPingUrl = trackbackPingUrl;
    }

	public List<BlgTag> getBlgPostHasTags() {
		return blgPostHasTags;
	}

    public void setBlgPostHasTags(List<BlgTag> blgPostHasTags) {
        this.blgPostHasTags = blgPostHasTags;
    }

	public List<BlgTrackbacks> getBlgTrackbacksesForPostId() {
		return blgTrackbacksesForPostId;
	}

    public void setBlgTrackbacksesForPostId(List<BlgTrackbacks> blgTrackbacksesForPostId) {
        this.blgTrackbacksesForPostId = blgTrackbacksesForPostId;
    }
    
    public List<BlgCategory> getBlgPostHasCategories() {
		return blgPostHasCategories;
	}

    public void setBlgPostHasCategories(List<BlgCategory> blgPostHasCategories) {
        this.blgPostHasCategories = blgPostHasCategories;
    }

	public List<BlgComment> getBlgComments() {
		return blgComments;
	}

    public void setBlgComments(List<BlgComment> blgComments) {
        this.blgComments = blgComments;
    }
    
    public static BlgPost find(EntityManager em, String id) {
		return em.find(BlgPost.class, id);
	}
    
    public static BlgPost findByName(EntityManager em, String name) {
		String myQuery = "SELECT g FROM BlgPost g WHERE g.name=:name";

		try {
			return em.createQuery(myQuery, BlgPost.class).setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
    
    public static List<BlgPost> findByBlog(EntityManager em, String blogId, PagingParams pagingParams) {
		Query query = em.createQuery("select bp from BlgPost bp, BlgComment bc "
							+ " where bp.blogId.id =:blogId"
							+ " and bp.id = bc.postId.id"
							+ " group by bc.postId.id"
							+ " order by count(bc.postId.id) desc");
		query.setParameter("blogId", blogId);
		if (pagingParams != null) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1) * pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}
        return query.getResultList();
	}
    
    public static List<BlgPost> getPostsByBlog(EntityManager em, String blogId, boolean includeNotPublished, PagingParams pagingParams) {
    	String queryString = "SELECT p FROM BlgPost p WHERE p.blogId.id = :blogId";
		if (!includeNotPublished) {
			queryString = queryString.concat(" AND p.published = '1'");
		}
		queryString = queryString.concat(" ORDER BY p.datePosted DESC");

		Query query = em.createQuery(queryString);

		query.setParameter("blogId", blogId);
		if (pagingParams != null) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1) * pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}
        return query.getResultList();
	}
    
    public static List<BlgPost> getPostsByBlogAndCategory(EntityManager em, String blogId, String categoryId, boolean includeNotPublished) {
    	String queryString = "SELECT p FROM BlgPost p LEFT JOIN p.blgPostHasCategories h LEFT JOIN h.categoryId c "
				+ "WHERE p.blogId.id = :blogId AND c.id = :categoryId";
		if (!includeNotPublished) {
			queryString = queryString.concat(" AND p.published = '1'");
		}
		queryString = queryString.concat(" ORDER BY p.datePosted DESC");

		Query query = em.createQuery(queryString);

		query.setParameter("blogId", blogId);
		query.setParameter("categoryId", categoryId);

        return query.getResultList();
	}
    
    public static List<BlgPost> getPostsByBlogAndTag(EntityManager em, String blogId, String tagId, boolean includeNotPublished) {
    	String queryString = "SELECT p FROM BlgPostHasTag pht INNER JOIN pht.postId p"
				+ " WHERE p.blogId.id = :blogId AND pht.tagId.id = :tagId";
		if (!includeNotPublished) {
			queryString = queryString.concat(" AND p.published = '1'");
		}
		queryString = queryString.concat(" ORDER BY p.datePosted DESC");

		Query query = em.createQuery(queryString);

		query.setParameter("blogId", blogId);
		query.setParameter("tagId", tagId);

        return query.getResultList();
	}
    
    public static List<BlgPost> getPostsByBlogAndStartDate(EntityManager em, String blogId, long startDate, boolean includeNotPublished) {
    	String queryString = "SELECT p FROM BlgPost p WHERE p.blogId.id =:blogId and "
				+ "p.datePosted >=:fromDtPosted";
		if (!includeNotPublished) {
			queryString = queryString.concat(" AND p.published = '1'");
		}
		queryString = queryString.concat(" ORDER BY p.datePosted DESC");

		Query query = em.createQuery(queryString);

		query.setParameter("blogId", blogId);
		query.setParameter("fromDtPosted", startDate);

        return query.getResultList();
	}
}


