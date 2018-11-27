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
package com.eurodyn.qlack2.fuse.aaa.api;

import com.eurodyn.qlack2.fuse.aaa.api.dto.ResourceDTO;

import java.util.Collection;

/**
 *
 * @author European Dynamics
 */
public interface ResourceService {

    /**
     * Registers a resource with the system in order for it to be access controlled
     * @param resourceDTO The information of the resource to register.
     * @return The ID of the registered resource
     */
    String createResource(ResourceDTO resourceDTO);
    
    /**
     * Updates a resource.
     * @param resourceDTO The details of the resource to update.
     */
    void updateResource(ResourceDTO resourceDTO);


    /**
     * Removes a resource from the system
     * @param resourceID The ID of the resource to remove
     */
    void deleteResource(String resourceID);


    /**
     * Removes a set of resources from the system
     * @param resourceIDs The IDs of the resources to remove
     */
    void deleteResources(Collection<String> resourceIDs);


    /**
     * Removes a resource from the system identified by its object ID
     * @param objectID The object ID of the resource to remove
     */
    void deleteResourceByObjectId(String objectID);


    /**
     * Removes a set of resources from the system
     * @param objectIDs The object IDs of the resources to remove 
     */
    void deleteResourcesByObjectIds(Collection<String> objectIDs);


    /**
     * Retrieves a resource by its ID
     * @param resourceID The ID of the resource to retrieve
     * @return The resource's details
     */
    ResourceDTO getResourceById(String resourceID);


    /**
     * Retrieves a resource by its object ID
     * @param objectID The id of the object of the resource to retrieve
     * @return The resource's details
     */
    ResourceDTO getResourceByObjectId(String objectID);


  /**
   * Retrieves a resource by its name
   *
   * @param resourceName The name of the resource to retrieve
   * @return The retrieved resource
   */
    ResourceDTO getResourceByName(String resourceName);
}
