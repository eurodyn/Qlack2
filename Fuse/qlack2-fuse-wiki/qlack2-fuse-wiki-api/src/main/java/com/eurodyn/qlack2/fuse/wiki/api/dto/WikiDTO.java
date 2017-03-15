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
package com.eurodyn.qlack2.fuse.wiki.api.dto;

import java.util.Set;

/**
 * DTO for Wiki object
 * @author European Dynamics SA.
 */
public class WikiDTO extends WikiBaseDTO {

    private static final long serialVersionUID = 2378135712546084632L;
    private String name;
    private String description;
    private byte[] logo;
    private Set<WikiEntryDTO> wikEntries;

    /**
     * Default constructor
     */
    public WikiDTO() {
    }

    /**
     * Constructor with parameters
     * @param name
     * @param description
     * @param wikEntries
     */
    public WikiDTO(String name, String description, Set<WikiEntryDTO> wikEntries) {
        this.name = name;
        this.description = description;
        this.wikEntries = wikEntries;
    }

    /**
     * get Description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * set description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * get name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * set name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get wiki entries
     * @return wiki entries
     */
    public Set<WikiEntryDTO> getWikEntries() {
        return wikEntries;
    }

    /**
     * set wiki entries
     * @param wikEntries
     */
    public void setWikEntries(Set<WikiEntryDTO> wikEntries) {
        this.wikEntries = wikEntries;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WikiDTO other = (WikiDTO) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 61 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }

    /**
     * get the logo
     * @return the logo
     */
    public byte[] getLogo() {
        return logo;
    }

    /**
     * set the logo
     * @param logo the logo to set
     */
    public void setLogo(byte[] logo) {
        this.logo = logo;
    }
}
