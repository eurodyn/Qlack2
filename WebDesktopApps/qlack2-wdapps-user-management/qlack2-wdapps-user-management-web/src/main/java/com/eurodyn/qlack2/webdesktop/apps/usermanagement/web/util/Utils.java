package com.eurodyn.qlack2.webdesktop.apps.usermanagement.web.util;

import com.eurodyn.qlack2.fuse.idm.api.signing.QSignedRequest;
import com.eurodyn.qlack2.fuse.idm.api.signing.SignedTicket;
import com.eurodyn.qlack2.util.validator.util.ValidationHelper;
import com.eurodyn.qlack2.webdesktop.api.exception.IllegalGroupActionException;
import com.eurodyn.qlack2.webdesktop.api.exception.IllegalUserActionException;
import com.eurodyn.qlack2.webdesktop.api.exception.InvalidGroupActionException;
import com.eurodyn.qlack2.webdesktop.api.exception.InvalidUserActionException;
import com.eurodyn.qlack2.webdesktop.api.util.Constants;

import javax.ws.rs.core.HttpHeaders;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
	private static final Logger logger = Logger.getLogger(Utils.class.getName());

	private Utils() {
	}

	public static void sign(QSignedRequest request, HttpHeaders headers) {
		String ticket = Constants.getTicketHeader(headers.getRequestHeaders());
		request.setSignedTicket(SignedTicket.fromVal(ticket));
	}

	public static <T> T validateUser(Callable<T> callable) {
		try {
			return callable.call();
		} catch (InvalidUserActionException ex) {
			logger.log(Level.INFO, "Invalid user", ex);
			ValidationHelper.throwValidationError(
					new String[]{"username"},
					new String[]{ex.getMessage()},
					new String[]{null});
			return null;
		} catch (IllegalUserActionException ex) {
			logger.log(Level.INFO, "Illegal user", ex);
			ValidationHelper.throwValidationError(
					new String[]{"username"},
					new String[]{ex.getMessage()},
					new String[]{null});
			return null;
		} catch (Exception ex) {
			Utils.<RuntimeException>rethrow(ex);
			return null;
		}
	}

	public static <T> T validateGroup(Callable<T> callable) {
		try {
			return callable.call();
		} catch (InvalidGroupActionException ex) {
			logger.log(Level.INFO, "Invalid group", ex);
			ValidationHelper.throwValidationError(
					new String[]{"name"},
					new String[]{ex.getMessage()},
					new String[]{null});
			return null;
		} catch (IllegalGroupActionException ex) {
			logger.log(Level.INFO, "Illegal group", ex);
			ValidationHelper.throwValidationError(
					new String[]{"name"},
					new String[]{ex.getMessage()},
					new String[]{null});
			return null;
		} catch (Exception ex) {
			Utils.<RuntimeException>rethrow(ex);
			return null;
		}
	}

	// http://blog.jooq.org/2012/09/14/throw-checked-exceptions-like-runtime-exceptions-in-java/

	@SuppressWarnings("unchecked")
	private static <E extends Exception> void rethrow(Exception e) throws E {
		throw (E) e;
	}
}
