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


/**
 * A DTO to facilitate information exchange with the Qlack API module.
 *
 * @author European Dynamics SA
 */
public class ComponentDTO extends BaseDTO {
    private static final long serialVersionUID = 3810363811878609212L;

    private String title;
    private String description;
    private String author;
    private String infoLink;
    private String boxLink;
    private String pageLink;
    private long registeredOn;
    private String iconLink;
    private String ownerUserID;
    private String configPage;
    private String privateKey;
    private byte state;
    private byte displayOrder;
    // This is the ID column of the ApiGadgetHasUser table. It is used in order for a gadget to be
    // able to determine for which user the gadget is being displayed without having to transmit
    // the actual user ID of the underlying user.
    private String userKey;

    /**
     * A default constructor.
     */
    public ComponentDTO() {
    }

    /**
     * A default constructor.
     * @param id The ID of the Gadget. Leave this empty when you are creating a new Gadget.
     * @param title The title of the Gadget.
     * @param description The description to be displayed for this Gadget.
     * @param author The name of the author/company of this Gadget.
     * @param infoLink A link pointing to a page providing further information about this Gadget.
     * @param boxLink The link to the page rendering the box of this Gadget to the end-user.
     * @param pageLink The link to the page rendering a full-screen version of this Gadget to the end-user.
     * @param registeredOn The date on which the Gadget was registered in the system. For new Gadgets, this
     * entry is automatically populated.
     * @param iconLink A link to a graphic image for this Gadget to be displayed when information about the
     * @param ownerUserID The id of the owner of this Gadget
     * @param configPage The configuration page of this Gadget
     * @param privateKey A private key of the gadget. This key is set automatically by the system when a
     * new Gadget is registered and cannot be updated.
     * Gadget is provided to the end-users.
     */
    public ComponentDTO(String id, String title, String description, String author, String infoLink, String boxLink,
            String pageLink, long registeredOn, String iconLink, String ownerUserID, String configPage, String privateKey) {
        setId(id);
        this.title = title;
        this.description = description;
        this.author = author;
        this.infoLink = infoLink;
        this.boxLink = boxLink;
        this.pageLink = pageLink;
        this.registeredOn = registeredOn;
        this.iconLink = iconLink;
        this.ownerUserID = ownerUserID;
        this.configPage = configPage;
        this.privateKey = privateKey;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the infoLink
     */
    public String getInfoLink() {
        return infoLink;
    }

    /**
     * @param infoLink the infoLink to set
     */
    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    /**
     * @return the boxLink
     */
    public String getBoxLink() {
        return boxLink;
    }

    /**
     * @param boxLink the boxLink to set
     */
    public void setBoxLink(String boxLink) {
        this.boxLink = boxLink;
    }

    /**
     * @return the pageLink
     */
    public String getPageLink() {
        return pageLink;
    }

    /**
     * @param pageLink the pageLink to set
     */
    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    /**
     * @return the registeredOn
     */
    public long getRegisteredOn() {
        return registeredOn;
    }

    /**
     * @param registeredOn the registeredOn to set
     */
    public void setRegisteredOn(long registeredOn) {
        this.registeredOn = registeredOn;
    }

    /**
     * @return the iconLink
     */
    public String getIconLink() {
        return iconLink;
    }

    /**
     * @param iconLink the iconLink to set
     */
    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    /**
     * @return the ownerUserID
     */
    public String getOwnerUserID() {
        return ownerUserID;
    }

    /**
     * @param ownerUserID the ownerUserID to set
     */
    public void setOwnerUserID(String ownerUserID) {
        this.ownerUserID = ownerUserID;
    }

    /**
     * @return the configPage
     */
    public String getConfigPage() {
        return configPage;
    }

    /**
     * @param configPage the configPage to set
     */
    public void setConfigPage(String configPage) {
        this.configPage = configPage;
    }

    /**
     * @return the privateKey
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * @param privateKey the privateKey to set
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @return the state
     */
    public byte getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(byte state) {
        this.state = state;
    }

    /**
     * @return the displayOrder
     */
    public byte getDisplayOrder() {
        return displayOrder;
    }

    /**
     * @param displayOrder the displayOrder to set
     */
    public void setDisplayOrder(byte displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * @return the userKey
     */
    public String getUserKey() {
        return userKey;
    }

    /**
     * @param userKey the userKey to set
     */
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
