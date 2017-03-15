package com.eurodyn.qlack2.be.client;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.frontend.ClientProxyFactoryBean;

import com.eurodyn.qlack2.be.client.connectivity.ClientCredentials;
import com.eurodyn.qlack2.be.client.connectivity.ServerAddress;
import com.eurodyn.qlack2.be.client.exception.ServiceClientInstantiationException;
import com.eurodyn.qlack2.be.client.proxy.ServiceInvocationHandler;
import com.eurodyn.qlack2.be.forms.api.CategoriesService;
import com.eurodyn.qlack2.be.forms.api.FormVersionsService;
import com.eurodyn.qlack2.be.forms.api.FormsService;
import com.eurodyn.qlack2.be.forms.api.OrbeonService;
import com.eurodyn.qlack2.be.forms.api.ProjectsService;
import com.eurodyn.qlack2.be.workflow.api.CategoryService;
import com.eurodyn.qlack2.be.workflow.api.ProjectService;
import com.eurodyn.qlack2.be.workflow.api.RuntimeService;
import com.eurodyn.qlack2.be.workflow.api.WorkflowService;
import com.eurodyn.qlack2.be.workflow.api.WorkflowVersionService;

public class QBEClient {
	// Reference to a pre-built ServiceDecorator object to be used
	// when creating clients for the underlying services.
	// private final ServiceDecorator serviceDecorator;
	private ServerAddress qbeAddress;
	private ClientCredentials credentials;

	public static final String cxfPrefix = "/api";
	public static final String qbePrefix = "/ws/qbe";
	// Map available services to their URLs
	private static Map<String, String> serviceURLs;

	static {
		serviceURLs = new HashMap<>();
		serviceURLs.put(com.eurodyn.qlack2.be.explorer.api.ProjectService.class.getName(), "/explorer/ProjectService");
		serviceURLs.put(ProjectsService.class.getName(), "/forms/ProjectsService");
		serviceURLs.put(CategoriesService.class.getName(), "/forms/CategoriesService");
		serviceURLs.put(FormsService.class.getName(), "/forms/FormsService");
		serviceURLs.put(FormVersionsService.class.getName(), "/forms/FormVersionsService");
		serviceURLs.put(OrbeonService.class.getName(), "/forms/OrbeonService");

		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.ProjectsService.class.getName(), "/rules/ProjectsService");
		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.CategoriesService.class.getName(), "/rules/CategoriesService");
		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.WorkingSetsService.class.getName(), "/rules/WorkingSetsService");
		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.RulesService.class.getName(), "/rules/RulesService");
		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.DataModelsService.class.getName(), "/rules/DataModelsService");
		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.LibraryService.class.getName(), "/rules/LibraryService");
		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.LibraryVersionService.class.getName(), "/rules/LibraryVersionService");
		serviceURLs.put(com.eurodyn.qlack2.be.rules.api.RulesRuntimeManagementService.class.getName(), "/rules/RulesRuntimeManagementService");

		serviceURLs.put(CategoryService.class.getName(), "/workflow/CategoryService");
		serviceURLs.put(ProjectService.class.getName(), "/workflow/ProjectService");
		serviceURLs.put(WorkflowService.class.getName(), "/workflow/WorkflowService");
		serviceURLs.put(WorkflowVersionService.class.getName(), "/workflow/WorkflowVersionService");
		serviceURLs.put(RuntimeService.class.getName(), "/workflow/RuntimeService");
	}

	public QBEClient(final ServerAddress qbeAddress,
			final ClientCredentials credentials) throws Exception {
		this.qbeAddress = qbeAddress;
		this.credentials = credentials;
	}

	public <T> T forService(Class<T> serviceClass)
			throws ServiceClientInstantiationException {
		if (serviceURLs.get(serviceClass.getName()) == null) {
			throw new ServiceClientInstantiationException("No service for " + serviceClass.getName() + " available to QBEClient");
		}
		ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
		factory.setServiceClass(serviceClass);
		factory.setDataBinding(new AegisDatabinding());
		factory.setAddress(qbeAddress.getAddress() + cxfPrefix + qbePrefix
				+ serviceURLs.get(serviceClass.getName()));

		T serviceClient = factory.create(serviceClass);

		@SuppressWarnings("unchecked")
		T proxy = (T) Proxy.newProxyInstance(ServiceInvocationHandler.class
				.getClassLoader(), new Class[] { serviceClass },
				new ServiceInvocationHandler(serviceClient, qbeAddress,
						credentials, cxfPrefix));

		return proxy;
	}
}
