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
package com.eurodyn.qlack2.fuse.simm.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * This class and its subclasses are a form of Exception
 * that indicates conditions that Group related module might want to catch.
 * @author European Dynamics SA
 */
public class QSIMMException extends QException {
    private static final long serialVersionUID = -6683569270499552613L;


//    public static enum CODES implements ExceptionCode {
//
//        ERR_SIMM_0001, // Category is not set or Category id is null in provided Activity DTO.
//        ERR_SIMM_0002, // User has been already joined in Group.
//        ERR_SIMM_0003, // Provided userID is null.
//        ERR_SIMM_0004, // Provided groupID is null.
//        ERR_SIMM_0005, // Provided homePageActivityID is null.
//        ERR_SIMM_0006, // Provided activityId is null.
//        ERR_SIMM_0007, // Provided groupDTO is null.
//        ERR_SIMM_0008, // Provided group name already exist.
//        ERR_SIMM_0009, // User has not been joined the group.
//        ERR_SIMM_0010, // Provided categoryName is null.
//        ERR_SIMM_0011, // Provided category name already exist.
//        ERR_SIMM_0012, // Provided categoryId is null.
//        ERR_SIMM_0013, // Provided categoryDTO is null.
//        ERR_SIMM_0014, // Category already exists with the name as in provided categoryDTO.
//        ERR_SIMM_0015, // Category does not exists with the provided categoryId.
//        ERR_SIMM_0016, // Provided searchTerm is null.
//        ERR_SIMM_0017, // Activity does not exist with the provided activityId.
//        ERR_SIMM_0018, // Group does not exist with the provided groupId.
//        ERR_SIMM_0019, // HomePageActivity does not exist with the provided activityId.
//        ERR_SIMM_0020, // More than one GroupUser found with the provided userId and groupId.
//        ERR_SIMM_0021, // More than one Activity found with the provided activitiId.
//        ERR_SIMM_0022, // Provided activityDTO is null.
//        ERR_SIMM_0023, // HomePageActivity already exists with the provided groupingID and activityDTO.
//        ERR_SIMM_0024, // Activity already exists as provided activityDTO.
//        ERR_SIMM_0025, // Group already exists as provided groupDTO.
//        ERR_SIMM_0026, // Provided groupDTO is null or does not contain Id.
//        ERR_SIMM_0027, // Provided status for group user is incorrect.
//        ERR_SIMM_0028, // More than one friend found with the provided userId and friend.
//        ERR_SIMM_0029, // User is already present in friend list.
//        ERR_SIMM_0030, // Provided userId or FreindID is not valid ID.
//        ERR_SIMM_0031, // Friend object is null.
//        ERR_SIMM_0032, // User can not join this group unless an invitation has been received first.
//        ERR_SIMM_0033  // Generic exception
//    }
    /**
     *
     * @param message
     */
    public QSIMMException(String message) {
        super(message);
    }

}
