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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import com.eurodyn.qlack2.common.util.exception.QSecurityTicketException;
import com.eurodyn.qlack2.common.util.util.TokenHolder;
import com.eurodyn.qlack2.fuse.idm.api.IDMService;
import com.eurodyn.qlack2.fuse.idm.api.exception.QInvalidTicketException;
import com.eurodyn.qlack2.fuse.idm.api.request.ValidateTicketRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;


@Aspect
public class ValidateTicketHolderAspect {
	private static final Logger LOGGER = Logger.getLogger(ValidateTicketHolderAspect.class.getName());
	//TODO add caching
	
	// The IDM service to use to validate tokens (injected by blueprint).
	private IDMService idmService;
	
	public void setIdmService(IDMService idmService) {
		this.idmService = idmService;
	}

	@Pointcut("execution(@com.eurodyn.qlack2.fuse.idm.api.annotations.ValidateTicketHolder * * (..))")
	void validateTicketHolderPointCut() {
	}

	@Before("validateTicketHolderPointCut()")
	public void ticketRequiredAction(JoinPoint pjp)
			throws Throwable {
		SignedTicket signedTicket = SignedTicket.fromVal(TokenHolder.getToken()); 
		if (signedTicket != null) {
			LOGGER.log(Level.FINEST, "Validating ticket: {0}.", signedTicket);
			if (!idmService.validateTicket(new ValidateTicketRequest(signedTicket)).isValid()) {
				throw new QSecurityTicketException(signedTicket.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "No ticket found.");
			throw new QSecurityTicketException("No ticket found. TokenHolder=" + TokenHolder.getToken());
		}
	}

}
