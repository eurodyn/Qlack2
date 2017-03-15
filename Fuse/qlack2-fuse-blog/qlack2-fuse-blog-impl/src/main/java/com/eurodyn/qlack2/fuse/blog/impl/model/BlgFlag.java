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

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="blg_flag")
public class BlgFlag  implements java.io.Serializable {
	private static final long serialVersionUID = 4750564945989267287L;
	
	@Id
    private String id;
    
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blog")
	private BlgBlog blogId;
    
	@Column(name="user_id")
	private String userId;
    
	@Column(name="flag_description")
	private String flagDescription;
    
	@Column(name="flag_name")
	private String flagName;
    
	@Column(name="date_flagged")
	private Long dateFlagged;

    public BlgFlag() {
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

	public String getUserId() {
		return userId;
	}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFlagDescription() {
		return flagDescription;
	}

    public void setFlagDescription(String flagDescription) {
        this.flagDescription = flagDescription;
    }

    public String getFlagName() {
		return flagName;
	}

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public Long getDateFlagged() {
		return dateFlagged;
	}

    public void setDateFlagged(Long dateFlagged) {
        this.dateFlagged = dateFlagged;
    }
}


