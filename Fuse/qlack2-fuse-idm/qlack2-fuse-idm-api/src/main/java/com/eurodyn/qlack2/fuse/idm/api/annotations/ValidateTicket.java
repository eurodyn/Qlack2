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
package com.eurodyn.qlack2.fuse.idm.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation which ensures that the SignedTicket passed to the
 * method as part of the requests is validated before the method is executed.
 * In order for the validation to work the following is needed:
 * <ul>
 * <li>A request object (extending com.eurodyn.qlack2.fuse.common.rest.QRequest) passed
 * as argument to the annotated method. The index of this argument in the method signature
 * is specified by the requestIndex argument of the annotation. The default is 0
 * since in the general case this will be the only argument of the method.</li>
 * <li>An IDMService object which will be used to perform the validation. This is
 * retrieved via reflection from the class containing the annotated method. The name
 * of the class field holding the service is specified via tha idmServiceField argument
 * of the annotation (the default is "idmService")</li>
 * </ul>
 * @author Christina
 *
 * @deprecated Use {@link ValidateTicketHolder} instead.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface ValidateTicket {
	int requestIndex() default 0;
	String idmServiceField() default "idmService";
	// When the SignedTicket is not available as part of the arguments of the
	// method to be validated, it can also be fetched directly by the HTTP
	// headers.
}
