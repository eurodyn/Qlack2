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
package com.eurodyn.qlack2.fuse.componentlibrary.api.dto;

import java.io.Serializable;

/**
 *
 * @author European Dynamics
 */
public class ComponentPermissionDTO implements Serializable {
    private static final long serialVersionUID = -4936745041448772701L;

    private String id;
    private String gadgetID;
    private String permission;
    private String userID;
    private Boolean enabled;
    private String gadgetTitle;

    /**
     * @return the gadgetID
     */
    public String getGadgetID() {
        return gadgetID;
    }

    /**
     * @param gadgetID the gadgetID to set
     */
    public void setGadgetID(String gadgetID) {
        this.gadgetID = gadgetID;
    }

    /**
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * @param permission the permission to set
     */
    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the gadgetTitle
     */
    public String getGadgetTitle() {
        return gadgetTitle;
    }

    /**
     * @param gadgetTitle the gadgetTitle to set
     */
    public void setGadgetTitle(String gadgetTitle) {
        this.gadgetTitle = gadgetTitle;
    }

}
