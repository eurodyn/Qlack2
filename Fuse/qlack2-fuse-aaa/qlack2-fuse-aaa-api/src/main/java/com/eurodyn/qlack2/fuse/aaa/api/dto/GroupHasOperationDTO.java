package com.eurodyn.qlack2.fuse.aaa.api.dto;

import java.io.Serializable;

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
public class GroupHasOperationDTO implements Serializable {
  private static final long serialVersionUID = -2269306349841980589L;

  private String id;
  private GroupDTO groupDTO;
  private OperationDTO operationDTO;
  private ResourceDTO resourceDTO;

  private boolean deny;

  public GroupDTO getGroupDTO() {
    return groupDTO;
  }

  public void setGroupDTO(GroupDTO groupDTO) {
    this.groupDTO = groupDTO;
  }

  public OperationDTO getOperationDTO() {
    return operationDTO;
  }

  public void setOperationDTO(OperationDTO operationDTO) {
    this.operationDTO = operationDTO;
  }

  public ResourceDTO getResourceDTO() {
    return resourceDTO;
  }

  public void setResourceDTO(ResourceDTO resourceDTO) {
    this.resourceDTO = resourceDTO;
  }

  public boolean isDeny() {
    return deny;
  }

  public void setDeny(boolean deny) {
    this.deny = deny;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
