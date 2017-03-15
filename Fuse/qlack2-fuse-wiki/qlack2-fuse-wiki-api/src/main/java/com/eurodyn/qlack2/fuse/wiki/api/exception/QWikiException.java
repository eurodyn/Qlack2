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
package com.eurodyn.qlack2.fuse.wiki.api.exception;

import com.eurodyn.qlack2.common.util.exception.QException;

/**
 * Core Wiki Exception
 * @author European Dynamics SA
 */
public class QWikiException extends QException {
    private static final long serialVersionUID = -3505963138471609436L;

//    public static enum CODES implements ExceptionCode {
//
//        ERR_WIKI_0001, //Wiki object is invalid
//        ERR_WIKI_0002, //Wiki entry object passed is invalid
//        ERR_WIKI_0003, //Wiki entry object Already Exists
//        ERR_WIKI_0004, //Wiki Entry Does not exist
//        ERR_WIKI_0005, //Wiki Does not exist
//        ERR_WIKI_0006, //Wiki Already exists
//        ERR_WIKI_0007, //Wiki Tag Does not exist
//        ERR_WIKI_0008, //Wiki Tag Already exists
//        ERR_WIKI_0009, //Wiki Tag object is invalid
//        ERR_WIKI_0010, //Wiki Page content I/O Error
//        ERR_WIKI_0011, //Wiki Page content parsing Error
//        ERR_WIKI_0012; //JMS Error
//    }

    /**
     * Constructor with code and message
     * @param message the exception message
     */
    public QWikiException(String message) {
        super(message);
    }


}