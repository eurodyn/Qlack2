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
package com.eurodyn.qlack2.fuse.aaa.impl.util;

import com.eurodyn.qlack2.fuse.aaa.api.dto.UserDTO;
import com.eurodyn.qlack2.fuse.aaa.impl.model.User;

/**
 *
 * @author European Dynamics S.A.
 */
public class AaaUserSearchHelper {
    private UserDTO userDTO;


    public AaaUserSearchHelper(User userEntity, Object sortCriterion) {
        //sortCriterion is ignored since it is only included in the query
        //for compatibility with certain DBs.
        userDTO = ConverterUtil.userToUserDTO(userEntity);
    }


    public UserDTO getUserDTO() {
        return userDTO;
    }
}
