/*
* Copyright 2015 EUROPEAN DYNAMICS SA <info@eurodyn.com>
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
package com.eurodyn.qlack2.fuse.idm.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.eurodyn.qlack2.common.util.util.TokenHolder;

/**
 * Method annotation which ensures that the SignedTicket passed to the method as
 * part of the request is validated before the method is executed. This is a
 * variation of the {@link ValidateTicket} which works with a thread-local
 * variable (instead of a request object) and has the IDMService injected by
 * blueprint instead of calling it via reflection.
 * 
 * @author European Dynamics
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateTicketHolder {
	String idmServiceField() default "idmService";
}
