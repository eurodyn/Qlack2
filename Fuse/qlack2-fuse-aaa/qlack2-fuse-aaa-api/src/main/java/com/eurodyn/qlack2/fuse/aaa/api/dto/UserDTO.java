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
package com.eurodyn.qlack2.fuse.aaa.api.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author European Dynamics SA
 */
public class UserDTO implements Serializable {

  private static final long serialVersionUID = -7705469804446714609L;

  private String id;
  private String username;
  private String password;
  private byte status;
  private boolean superadmin;
  private boolean external;
  private Set<UserAttributeDTO> userAttributes;
  // The session Id created for this user. Expect this to be populated, only, when attempting to
  // login the user.
  private String sessionId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return the status
   */
  public byte getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(byte status) {
    this.status = status;
  }

  public boolean isSuperadmin() {
    return superadmin;
  }

  public void setSuperadmin(boolean superadmin) {
    this.superadmin = superadmin;
  }

  public boolean isExternal() {
    return external;
  }

  public void setExternal(boolean external) {
    this.external = external;
  }

  /**
   * @return Set of UserAttributeDTO
   */
  public Set<UserAttributeDTO> getUserAttributes() {
    return userAttributes;
  }

  /**
   * @param userAttributes The list of attributes to set.
   */
  public void setUserAttributes(Set<UserAttributeDTO> userAttributes) {
    this.userAttributes = userAttributes;
  }

  public UserAttributeDTO getAttribute(String name) {
    UserAttributeDTO retVal = null;
    if (userAttributes != null) {
      for (UserAttributeDTO userAttributesDTO : userAttributes) {
        if (userAttributesDTO.getName().equalsIgnoreCase(name)) {
          retVal = userAttributesDTO;
          break;
        }
      }
    }
    return retVal;
  }

  /**
   * Returns the string representation of the value of an attribute.
   *
   * @param name The name of the attribute.
   * @return The value of the attribute.
   */
  public String getAttributeData(String name) {
    String retVal = null;
    if (userAttributes != null) {
      for (UserAttributeDTO userAttributesDTO : userAttributes) {
        if (userAttributesDTO.getName().equalsIgnoreCase(name)) {
          retVal = userAttributesDTO.getData();
          break;
        }
      }
    }
    return retVal;
  }

  /**
   * Returns the binary data of the given attribute.
   *
   * @param name The name of the attribute to search for.
   * @return The value of the binary attribute.
   */
  public byte[] getAttributeBinData(String name) {
    byte[] retVal = null;
    if (userAttributes != null) {
      for (UserAttributeDTO userAttributesDTO : userAttributes) {
        if (userAttributesDTO.getName().equalsIgnoreCase(name)) {
          retVal = userAttributesDTO.getBinData();
          break;
        }
      }
    }
    return retVal;
  }

  public void setAttribute(UserAttributeDTO attribute) {
    boolean found = false;
    if (userAttributes != null) {
      for (UserAttributeDTO userAttributesDTO : userAttributes) {
        if (userAttributesDTO.getName().equalsIgnoreCase(attribute.getName())) {
          userAttributesDTO.setData(attribute.getData());
          userAttributesDTO.setBinData(attribute.getBinData());
          userAttributesDTO.setContentType(attribute.getContentType());
          found = true;
          break;
        }
      }
    }
    if (!found) {
      if (userAttributes == null) {
        userAttributes = new HashSet<>();
      }
      attribute.setUserId(id);
      userAttributes.add(attribute);
    }
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }
}
