package com.eurodyn.qlack2.fuse.workflow.runtime.impl.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.apache.karaf.jndi.JndiService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * Bind transaction services under JNDI context java:comp so that JBPM can find them.
 *
 * Normally transaction services are made available to JBPM through
 * {@link org.kie.api.runtime.Environment}. However
 * {@link org.jbpm.shared.services.impl.TransactionalCommandService} does not
 * use the JBPM Environment and searches directly in the JNDI context, which is
 * probably a bug. Also, TransactionalCommandService functionality is equivalent
 * to transaction interception and entity manager injection, which are both
 * performed by the containing service anyway, so we can probably stop using it.
 *
 * Until the above are resolved, we need to bind the transaction services to JNDI.
 *
 * @author European Dynamics SA
 */
public class JndiUtil {
	private static final Logger logger = Logger.getLogger(JndiUtil.class.getName());

	private static final String COMP_JNDI_CONTEXT = "java:comp/";
	private static final String JBOSS_JNDI_CONTEXT = "java:jboss/";

	private static final String UTX_NAME = "UserTransaction";
	private static final String TM_NAME = "TransactionManager";
	private static final String TSR_NAME = "TransactionSynchronizationRegistry";

	private BundleContext context;
	private JndiService jndiService;

	public void setContext(BundleContext context) {
		this.context = context;
	}

	public void setJndiService(JndiService jndiService) {
		this.jndiService = jndiService;
	}

	public void bind() {
		try {
			// Test if transaction services are already bound (JBoss)
			if (isBound(JBOSS_JNDI_CONTEXT)) {
				return;
			}

			// Test if transaction services are already bound (bundle update)
			if (isBound(COMP_JNDI_CONTEXT)) {
				return;
			}

			// No transaction services are bound, assume a writable context and bind them (Karaf)
			bind(UserTransaction.class, UTX_NAME);
			bind(TransactionManager.class, TM_NAME);
			bind(TransactionSynchronizationRegistry.class, TSR_NAME);
		}
		catch (Exception e) {
			throw new RuntimeException("Cannot bind transaction services to java:comp", e);
		}
	}

	private void bind(Class<?> clazz, String name) throws Exception {
		ServiceReference<?> service = context.getServiceReference(clazz);
		Long serviceId = (Long) service.getProperty(Constants.SERVICE_ID);
		jndiService.bind(serviceId, COMP_JNDI_CONTEXT + name);
	}

	private boolean isBound(String prefix) {
		String name = prefix + UTX_NAME;
		try {
			InitialContext context = new InitialContext();
			context.lookup(name);
			logger.log(Level.FINE, "Found UserTransaction at {0}", name);
			return true;
		}
		catch (NamingException ex) {
			logger.log(Level.FINE, "Cannot find UserTransaction at {0}", name);
			return false;
		}
	}
}
