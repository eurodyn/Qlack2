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
package com.eurodyn.qlack2.util.datasource.generic;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbcp.managed.BasicManagedDataSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Configurator {
	public static final Logger LOGGER = Logger.getLogger(Configurator.class.getName());
	private BundleContext context;

	private static List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();

	private TransactionManager transactionManager;

	private String driverClass;
	private String datasourceType;
	private String jdbcURL;
	private String username;
	private String password;
	private String jndiName;
	private String driverParametersMapping;

	// Connection pool settings
	private int initialSize;
	private int maxActive;
	private int maxIdle;
	private int minIdle;
	private long maxWait;
	private String validationQuery;
	private boolean testOnBorrow;
	private boolean removeAbandoned;
	private int removeAbandonedTimeout;

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public String getDriverParametersMapping() {
		return driverParametersMapping;
	}

	public void setDriverParametersMapping(String driverParametersMapping) {
		this.driverParametersMapping = driverParametersMapping;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getJdbcURL() {
		return jdbcURL;
	}

	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatasourceType() {
		return datasourceType;
	}

	public void setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
	}

	public BundleContext getContext() {
		return context;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	public void setInitialSize(int initialSize) {
		this.initialSize = initialSize;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public void setRemoveAbandoned(boolean removeAbandoned) {
		this.removeAbandoned = removeAbandoned;
	}

	public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
		this.removeAbandonedTimeout = removeAbandonedTimeout;
	}

	public void refresh() {
		try {
			Object registeredDs;

			// Configure the database driver.
			Object dbDriver = Class.forName(getDriverClass()).newInstance();
			// To configure the driver in a generic way, we use the
			// driverParametersMapping.
			String[] params = getDriverParametersMapping().split(",");
			for (String param : params) {
				param = param.trim();
				String paramKey = param.trim().split("-")[0];
				String paramValue = param.trim().split("-")[1];
				String property = BeanUtils.getProperty(this, paramValue);
				BeanUtils.setProperty(dbDriver, paramKey, property);
			}

			if (datasourceType.equals("javax.sql.XADataSource")) {
				BasicManagedDataSource managedDs = new BasicManagedDataSource();
				managedDs.setTransactionManager(transactionManager);
				managedDs.setXaDataSourceInstance((XADataSource) dbDriver);
				managedDs.setInitialSize(initialSize);
				managedDs.setMaxActive(maxActive);
				managedDs.setMaxIdle(maxIdle);
				managedDs.setMinIdle(minIdle);
				managedDs.setMaxWait(maxWait);
				managedDs.setValidationQuery(validationQuery);
				managedDs.setTestOnBorrow(testOnBorrow);
				managedDs.setRemoveAbandoned(removeAbandoned);
				managedDs.setRemoveAbandonedTimeout(removeAbandonedTimeout);
				registeredDs = managedDs;
			} else {
				registeredDs = dbDriver;
			}

			// If the service is already registered it should be unregistered first.
			for (ServiceRegistration<?> registration : serviceRegistrations) {
				registration.unregister();
			}

			// Expose datasources using the driver configured above.
			// This bundle accepts multiple jndi names as a comma-separated list so we
			// expose as many services as the provided jndi names.
			String[] jndiNames = jndiName.split(",");
			for (String name : jndiNames) {
				Dictionary<String, String> registrationProperties = new Hashtable<String, String>();
				registrationProperties.put("osgi.jndi.service.name", name);
				ServiceRegistration<?> registration = context.registerService(
						DataSource.class.getName(), registeredDs, registrationProperties);
				serviceRegistrations.add(registration);
				LOGGER.log(Level.CONFIG,
						"Registered Datasource for {0} under {1}.", new String[] {
								getDriverClass(), name });
			}
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format(
					"Could not find database driver {0}.", getDriverClass()), e);
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
			LOGGER.log(Level.SEVERE, MessageFormat.format(
					"Could not instantiate database driver {0}.", getDriverClass()), e);
		}
	}
}
