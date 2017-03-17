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
package com.eurodyn.qlack2.fuse.auditing.api;

import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLevelDTO;

import java.util.List;

/**
 * Local interface to manage AuditLevelDTO related operations.
 */
public interface AuditLevelService {

    /**
     * Creates a new audit level
     * @param level The details of the level to be created. The current date is set as createdOn date
     * @return The id of the new level
     */
    String addLevel(AuditLevelDTO level);

    /**
     * To delete audit level by its id.
     * @param levelId - The id of the level to be deleted
     */
    void deleteLevelById(String levelId);

    /**
     * To delete audit level by name.
     * @param levelName - The name of the level to be deleted
     */
    void deleteLevelByName(String levelName);

    /**
     * Updates an audit level
     * @param level The level to be updated. The level's id is used to identify the level, while
     * the rest of the level fields contain the updated information
     */
    void updateLevel(AuditLevelDTO level);

    /**
     * To get audit level by name.
     * @param levelName - String - Name of the level.
     * @return The audit level information
     */
    AuditLevelDTO getAuditLevelByName(String levelName);

    /**
     * To clear cache of audit levels by name.
     */
    void clearAuditLevelCache();

    /**
     * To get all audit levels present in system.
     * @return List of AuditLevels.
     */
    List<AuditLevelDTO> listAuditLevels();
}
