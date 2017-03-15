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
package com.eurodyn.qlack2.fuse.blog.api.dto;

/**
 *
 * @author European Dynamics SA.
 */
public class BlogTagDTO extends BlogBaseDTO {

    private static final long serialVersionUID = -2458215734883152532L;
    private String name;
    private String description;
    private long posts;

    /**
     * empty constructor
     */
    public BlogTagDTO() {};

    /**
     *
     * @param id
     * @param name
     * @param description
     */
    public BlogTagDTO(String id, String name, String description) {
        this.setId(id);
        this.name = name;
        this.description = description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the posts
     */
    public long getPosts() {
        return posts;
    }

    /**
     * @param posts the posts to set
     */
    public void setPosts(long posts) {
        this.posts = posts;
    }
}
