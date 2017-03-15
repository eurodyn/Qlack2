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
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;

import com.eurodyn.qlack2.common.util.search.PagingParams;

@Entity
@Table(name="blg_comment")
public class BlgComment  implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;
	
	@Id
    private String id;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="post")
	private BlgPost postId;
    
	@Column(name="user_id")
	private String userId;
    
	@Column(name="body")
	private String body;
    
	@Column(name="date_commented")
	private Long dateCommented;
    
	@OneToMany(fetch=FetchType.LAZY, mappedBy="blgCommentId")
	private Set<BlgTrackbacks> blgTrackbackses = new HashSet<BlgTrackbacks>(0);

    public BlgComment() {
    	id = UUID.randomUUID().toString();
    }

	public String getId() {
		return id;
	}

    public void setId(String id) {
        this.id = id;
    }

	public BlgPost getPostId() {
		return postId;
	}

    public void setPostId(BlgPost postId) {
        this.postId = postId;
    }

	public String getUserId() {
		return userId;
	}

    public void setUserId(String userId) {
        this.userId = userId;
    }

	public String getBody() {
		return body;
	}

    public void setBody(String body) {
        this.body = body;
    }

    public Long getDateCommented() {
		return dateCommented;
	}

    public void setDateCommented(Long dateCommented) {
        this.dateCommented = dateCommented;
    }

	public Set<BlgTrackbacks> getBlgTrackbackses() {
		return blgTrackbackses;
	}

    public void setBlgTrackbackses(Set<BlgTrackbacks> blgTrackbackses) {
        this.blgTrackbackses = blgTrackbackses;
    }
    
    public static BlgComment find(EntityManager em, String id) {
		return em.find(BlgComment.class, id);
	}
    
    public static List<BlgComment> findByBlog(EntityManager em, String blogId, PagingParams pagingParams) {
		Query query = em.createQuery("select bc from BlgComment bc, BlgPost bp "
						+ " where bp.blogId.id =:blogId and bc.postId.id=bp.id "
						+ " and bp.commentsEnabled='1' "
						+ " order by bc.dateCommented desc");
		query.setParameter("blogId", blogId);
		if (pagingParams != null) {
			query.setFirstResult((pagingParams.getCurrentPage() - 1) * pagingParams.getPageSize());
			query.setMaxResults(pagingParams.getPageSize());
		}
        return query.getResultList();
	}
    
    public static List<BlgComment> findByPost(EntityManager em, String postId) {
		Query query = em.createQuery("select bc from BlgComment bc where bc.postId.id = :id");
		query.setParameter("id", postId);
        return query.getResultList();
	}
}


