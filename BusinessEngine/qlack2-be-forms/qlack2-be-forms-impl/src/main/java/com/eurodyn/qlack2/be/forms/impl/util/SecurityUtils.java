package com.eurodyn.qlack2.be.forms.impl.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.eurodyn.qlack2.fuse.idm.api.exception.QAuthorisationException;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.webdesktop.api.SecurityService;
import com.eurodyn.qlack2.webdesktop.api.request.security.IsPermittedRequest;

public class SecurityUtils {
	private static final Logger LOGGER = Logger.getLogger(SecurityUtils.class
			.getName());

	private SecurityService securityService;

	public void checkFormOperation(SignedTicket signedTicket, String operation,
			String formId, String projectId) {
		Boolean isPermitted = securityService
				.isPermitted(new IsPermittedRequest(signedTicket, operation,
						formId));
		if (isPermitted == null) {
			require(signedTicket, operation, projectId);
		} else if (!isPermitted) {
			LOGGER.log(
					Level.SEVERE,
					"Operation {0} on form {1} is not allowed for userID {2}",
					new Object[] { operation, formId, signedTicket.getUserID() });
			throw new QAuthorisationException(signedTicket.getUserID(),
					signedTicket.toString(), operation, formId);
		}
	}

	public void checkCreateFormOperation(SignedTicket signedTicket,
			String projectId) {
		require(signedTicket, SecureOperation.FRM_VIEW_FORM.toString(),
				projectId);

		require(signedTicket, SecureOperation.FRM_MANAGE_FORM.toString(),
				projectId);
	}

	public void checkUnlockFormOperation(SignedTicket signedTicket,
			String formId, String projectId) {
		String userID = signedTicket.getUserID();

		Boolean isLockOnFormPermitted = securityService
				.isPermitted(new IsPermittedRequest(signedTicket,
						SecureOperation.FRM_LOCK_FORM.toString(), formId));

		if (isLockOnFormPermitted == null || !isLockOnFormPermitted) {
			// denied OP_FRM_LOCK_FORM for formId -> check
			// OP_FRM_UNLOCK_ANY_FORM for formId
			Boolean isUnLockAnyOnFormPermitted = securityService
					.isPermitted(new IsPermittedRequest(signedTicket,
							SecureOperation.FRM_UNLOCK_ANY_FORM.toString(),
							formId));

			if (isUnLockAnyOnFormPermitted == null
					|| !isUnLockAnyOnFormPermitted) {
				// denied OP_FRM_UNLOCK_ANY_FORM for formId -> check
				// OP_FRM_LOCK_FORM for projectId
				Boolean isLockOnProjectPermitted = securityService
						.isPermitted(new IsPermittedRequest(signedTicket,
								SecureOperation.FRM_LOCK_FORM.toString(),
								projectId));

				if (isLockOnProjectPermitted == null
						|| !isLockOnProjectPermitted) {
					// denied OP_FRM_LOCK_FORM for projectId -> check
					// OP_FRM_UNLOCK_ANY_FORM for project
					Boolean isUnLockAnyOnProjectPermitted = securityService
							.isPermitted(new IsPermittedRequest(signedTicket,
									SecureOperation.FRM_UNLOCK_ANY_FORM
											.toString(), projectId));

					if (isUnLockAnyOnProjectPermitted == null
							|| !isUnLockAnyOnProjectPermitted) {
						// denied OP_FRM_UNLOCK_ANY_FORM for projectId
						LOGGER.log(
								Level.SEVERE,
								"Unlocking form {0} is not allowed for userID {1}",
								new Object[] { formId, signedTicket.getUserID() });
						throw new QAuthorisationException(userID,
								signedTicket.toString(),
								SecureOperation.FRM_LOCK_FORM.toString(),
								formId);
					}
				}
			}
		}
	}

	public void checkCategoryOperation(SignedTicket signedTicket,
			String operation, String projectId) {
		require(signedTicket, operation, projectId);
	}

	public void require(SignedTicket signedTicket, String operationName,
			String resourceObjectID) {
		Boolean isPermitted = securityService
				.isPermitted(new IsPermittedRequest(signedTicket,
						operationName, resourceObjectID));
		if ((isPermitted == null) || (!isPermitted)) {
			LOGGER.log(
					Level.SEVERE,
					"Operation {0} on resource {1} is not allowed for userID {2}",
					new Object[] { operationName, resourceObjectID,
							signedTicket.getUserID() });
			throw new QAuthorisationException(signedTicket.getUserID(),
					signedTicket.getTicketID(), operationName, resourceObjectID);
		}
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

}
