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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="blg_trackbacks")
public class BlgTrackbacks implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;

	@Id
	private String id;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="trackback_post")
	private BlgPost trackbackPostId;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blg_comment")
	private BlgComment blgCommentId;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="post")
	private BlgPost postId;

    public BlgTrackbacks() {
    	id = java.util.UUID.randomUUID().toString();
    }

	public String getId() {
		return id;
	}

    public void setId(String id) {
        this.id = id;
    }
    
    public BlgPost getTrackbackPostId() {
		return trackbackPostId;
	}

    public void setTrackbackPostId(BlgPost trackbackPostId) {
        this.trackbackPostId = trackbackPostId;
    }

	public BlgComment getBlgCommentId() {
		return blgCommentId;
	}

    public void setBlgCommentId(BlgComment blgCommentId) {
        this.blgCommentId = blgCommentId;
    }

	public BlgPost getPostId() {
		return postId;
	}

    public void setPostId(BlgPost postId) {
        this.postId = postId;
    }
}


