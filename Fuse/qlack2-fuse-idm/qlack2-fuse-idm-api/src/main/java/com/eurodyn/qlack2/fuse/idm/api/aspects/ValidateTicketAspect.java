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
package com.eurodyn.qlack2.fuse.idm.api.aspects;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;

/**
 *  @deprecated Use {@link ValidateTicketHolderAspect}.
 */
@Aspect
@Deprecated
public class ValidateTicketAspect {
	private static final Logger LOGGER = Logger.getLogger(ValidateTicketAspect.class.getName());

	@Pointcut("execution(@com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicket * * (..))"
			+ "&& args(com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest)")
	void validateTicketPointCut() {
	}

	@Before("validateTicketPointCut()")
	public void ticketRequiredAction(JoinPoint pjp)
			throws Throwable {
		ValidateTicket annotation =
				((MethodSignature) pjp.getStaticPart().getSignature())
				.getMethod().getAnnotation(ValidateTicket.class);
		int requestIndex = annotation.requestIndex();
		String idmServiceField = annotation.idmServiceField();
		QSignedRequest sreq = (QSignedRequest) pjp.getArgs()[requestIndex];

		if (sreq.getSignedTicket() != null) {
			LOGGER.log(Level.FINEST, "Validating ticket {0}", sreq.getSignedTicket().toString());
			Field idmField = pjp.getTarget().getClass().getDeclaredField(idmServiceField);
			idmField.setAccessible(true);
			IDMService idmService = (IDMService) idmField.get(pjp.getTarget());
			if (!idmService.validateTicket(new ValidateTicketRequest(sreq.getSignedTicket())).isValid()) {
				throw new QInvalidTicketException(sreq.getSignedTicket());
			}
		} else {
			LOGGER.log(Level.FINEST, "No ticket found.");
			throw new QInvalidTicketException(sreq.getSignedTicket());
		}
	}

}
