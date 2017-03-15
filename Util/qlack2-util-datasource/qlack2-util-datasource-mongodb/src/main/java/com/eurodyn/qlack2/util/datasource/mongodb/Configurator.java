package com.eurodyn.qlack2.util.datasource.mongodb;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Configurator {
	public static final Logger LOGGER = Logger.getLogger(Configurator.class.getName());
	private static List<ServiceRegistration<?>> serviceRegistrations = new ArrayList<>();
	private String jndiName = "mongodb";
	private BundleContext context;

	public void setContext(BundleContext context) {
		this.context = context;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public void refresh() {
		// Configure the database driver.
		MongoClient mongoClient = new MongoClient("docker-vm-ed", 30001);
		MongoDatabase database = mongoClient.getDatabase("esthesis");

		// If the service is already registered it should be unregistered first.
		for (ServiceRegistration<?> registration : serviceRegistrations) {
			registration.unregister();
		}

		// Expose datasources using the driver configured above.
		// This bundle accepts multiple jndi names as a comma-separated list so
		// we
		// expose as many services as the provided jndi names.
		String[] jndiNames = jndiName.split(",");
		for (String name : jndiNames) {
			Dictionary<String, String> registrationProperties = new Hashtable<String, String>();
			registrationProperties.put("osgi.jndi.service.name", name);
//			ServiceRegistration<?> registration = context.registerService(DataSource.class.getName(), database,
//					registrationProperties);
			ServiceRegistration<?> registration = context.registerService(database.getClass().getName(), database,
					registrationProperties);
			
			serviceRegistrations.add(registration);
			LOGGER.log(Level.CONFIG, "Registered Datasource for {0} under {1}.",
					new String[] { database.getClass().getName(), name });
		}
	}
}
