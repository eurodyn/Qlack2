package com.eurodyn.qlack2.fuse.workflow.runtime.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.apache.commons.codec.digest.DigestUtils;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.spi.ProcessContext;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.jbpm.process.workitem.email.EmailWorkItemHandler;
import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import com.eurodyn.qlack2.fuse.auditing.api.Constants;
import com.eurodyn.qlack2.fuse.auditing.api.dto.AuditLogDTO;
import com.eurodyn.qlack2.fuse.eventpublisher.api.EventPublisherService;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.QWorkflowRuntimeException;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.WorkflowRuntimeService;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.ProcessInstanceDesc;
import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.TaskSummary;
import com.eurodyn.qlack2.fuse.workflow.runtime.impl.model.ProcessContent;
import com.eurodyn.qlack2.fuse.workflow.runtime.impl.util.ConverterUtil;
import com.eurodyn.qlack2.fuse.workflow.runtime.impl.util.JndiUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkflowRuntimeServiceImpl implements WorkflowRuntimeService {

	private static String LOG_LEVEL = "LOG_LEVEL_RUNTIME_WORKFLOW";
	private static String LOG_GROUP = "LOG_GROUP_RUNTIME_WORKFLOW";
	private static final Logger logger = Logger.getLogger(WorkflowRuntimeServiceImpl.class.getName());

	private EventPublisherService eventPublisherService;

	private RuntimeManager runtimeManager;
    private RuntimeEnvironment environment;

	private UserTransaction utx;
	private TransactionManager tm;
	private TransactionSynchronizationRegistry tsr;

	private JndiUtil jndiUtil;

	private EntityManagerFactory emf;
    private EntityManager em;
    private JPAMapper mapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private UserGroupCallback userGroupCallback;
    private KieBase kbase;
    private RuntimeEnvironmentBuilder builder;

    //private TaskService taskService;

    private String emailHost;
    private String emailPort;
    private String emailUsername;
    private String emailPassword;

	public void setEventPublisherService(EventPublisherService eventPublisherService) {
		this.eventPublisherService = eventPublisherService;
	}

	public void setJndiUtil(JndiUtil jndiUtil) {
		this.jndiUtil = jndiUtil;
	}

	public void setUtx(UserTransaction utx) {
		this.utx = utx;
	}

	public void setTm(TransactionManager tm) {
		this.tm = tm;
	}

	public void setTsr(TransactionSynchronizationRegistry tsr) {
		this.tsr = tsr;
	}

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	public void setUserGroupCallback(UserGroupCallback userGroupCallback) {
		this.userGroupCallback = userGroupCallback;
	}

	public void setEmailHost(String host) {
		this.emailHost = host;
	}

	public void setEmailPort(String port) {
		this.emailPort = port;
	}

	public void setEmailUsername(String username) {
		this.emailUsername = username;
	}

	public void setEmailPassword(String password) {
		this.emailPassword = password;
	}

	public void initWorkflowRuntimeService()
	{
		try {
			mapper = new JPAMapper(emf);

	        builder = RuntimeEnvironmentBuilder.getEmpty();
			builder.persistence(true);
			builder.entityManagerFactory(emf);
			builder.userGroupCallback(userGroupCallback);
			builder.knowledgeBase(kbase);
			builder.mapper(mapper);
			builder.registerableItemsFactory(new DefaultRegisterableItemsFactory());

			builder.addEnvironmentEntry(EnvironmentName.TRANSACTION, utx);
			builder.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, tm);
			builder.addEnvironmentEntry(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY, tsr);
			builder.addEnvironmentEntry(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);

			jndiUtil.bind();

	        environment = builder.get();
	        kbase = environment.getKieBase();

	        ((SimpleRegisterableItemsFactory)environment.getRegisterableItemsFactory()).addWorkItemHandler("Log", CustomLogWorkItemHandler.class);
	        ((SimpleRegisterableItemsFactory)environment.getRegisterableItemsFactory()).addWorkItemHandler("Service Task", ServiceTaskHandler.class);
	        ((SimpleRegisterableItemsFactory)environment.getRegisterableItemsFactory()).addWorkItemHandler("Rest", RESTWorkItemHandler.class);
	        ((SimpleRegisterableItemsFactory)environment.getRegisterableItemsFactory()).addWorkItemHandler("QBERuleTask", CustomRuleTaskWorkItemHandler.class);

	        runtimeManager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);

	        checkRestartWorkflowInstancesInDB();
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "Cannot init workflow runtime", e);
			audit("LOG_EVENT_INIT_RUNTIME_MANAGER", null, null, e);
			throw new QWorkflowRuntimeException(e.toString(), e);
		}
	}

	public void disposeWorkflowRuntimeService()
	{
		if (runtimeManager != null) {
            runtimeManager.close();
            runtimeManager = null;
        }
	}

	@Override
	public Long startWorkflowInstance(String processId, String content, Map<String, Object> parameters) {
		long processInstanceId = -1;
		try {
			//change classloader due to mvel issue with classloaders in OSGi
			Thread thread = Thread.currentThread();
			ClassLoader loader = thread.getContextClassLoader();
			thread.setContextClassLoader(this.getClass().getClassLoader());

			logger.log(Level.INFO, "Inside startWorkflowInstance!!!!");

			//check ProcessContent table
			checkProcessContentExistence(processId, content);

			addContentKnowledgeBase(processId, content);

			RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());
	        KieSession ksession = runtimeEngine.getKieSession();

	        registerCustomWorkItemHandlers(ksession);
	        ProcessInstance processInstance = ksession.startProcess(processId, parameters);
			processInstanceId = processInstance.getId();
			logger.log(Level.INFO, "ProcessInstanceId: " + processInstanceId);

			// revert the classloader
			thread.setContextClassLoader(loader);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "startWorkflowInstance: Exception throws: " + e.toString());
			audit("LOG_EVENT_START_WORKFLOW_INSTANCE", processId, null, e);
			throw new QWorkflowRuntimeException(e.toString(), e);
		}

		if (processInstanceId!= -1)
			return processInstanceId;
		else
			return null;
	}

	@Override
	public List<ProcessInstanceDesc> getProcessInstancesByProcessId(String processId)
	{
		logger.log(Level.INFO, "inside getProcessInstancesByProcessId!!!!");
		//runtimeEngine = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get());
		//auditLogService = runtimeEngine.getAuditLogService();
		//return auditLogService.findProcessInstances(processId);

		List<ProcessInstanceDesc> processInstances = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processId", processId +"%");
        processInstances = (List<ProcessInstanceDesc>) queryStringWithParameters(params, "getProcessInstancesByProcessId");

        for (ProcessInstanceDesc instance : processInstances)
        {
        	String stateDesc;
        	switch (instance.getState()) {
            	case ProcessInstance.STATE_ABORTED: stateDesc = "Aborted";
                      							   break;
            	case ProcessInstance.STATE_ACTIVE: stateDesc = "Active";
				   								   break;
            	case ProcessInstance.STATE_COMPLETED: stateDesc = "Completed";
				   								      break;
            	case ProcessInstance.STATE_PENDING: stateDesc = "Pending";
				   									break;
            	case ProcessInstance.STATE_SUSPENDED: stateDesc = "Suspended";
            										  break;
            	default: stateDesc = "Invalid state";
            						 break;
        	}
            instance.setStateDesc(stateDesc);
        }

        return processInstances;
	}

	@Override
	public ProcessInstanceDesc getProcessInstanceDetails(Long processInstanceId)
	{
		ProcessInstanceDesc processInstance = null;
		if (processInstanceId != -1)
		{
			Map<String, Object> params = new HashMap<String, Object>();
		    params.put("instanceId", processInstanceId);
		    processInstance = (ProcessInstanceDesc) queryStringWithParametersSingleResult(params, "getProcessInstanceDetailsByProcessInstanceId");

	        String stateDesc;
	        switch (processInstance.getState()) {
	           	case ProcessInstance.STATE_ABORTED: stateDesc = "Aborted"; break;
		        case ProcessInstance.STATE_ACTIVE: stateDesc = "Active"; break;
		        case ProcessInstance.STATE_COMPLETED: stateDesc = "Completed"; break;
		        case ProcessInstance.STATE_PENDING: stateDesc = "Pending"; break;
		        case ProcessInstance.STATE_SUSPENDED: stateDesc = "Suspended"; break;
		        default: stateDesc = "Invalid state"; break;
		    }
	        processInstance.setStateDesc(stateDesc);
		}
	    return processInstance;
	}

	@Override
	public Object getVariableInstance(Long processInstanceId, String variableName)
	{
		Object variable = null;
		if (processInstanceId != -1)
			try {
				logger.log(Level.INFO, "Inside getVariableInstance!!!!, processInstanceId:" + processInstanceId);

				ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
				RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(myContext);
		        KieSession ksession = runtimeEngine.getKieSession();
		        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		        if (processInstance == null || !(processInstance instanceof WorkflowProcessInstance)) {
		        	return null;
		        }
		        variable = ((WorkflowProcessInstance) processInstance).getVariable(variableName);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "getVariableInstance: Exception throws: " + e.toString());
				throw new QWorkflowRuntimeException(e.toString(), e);
			}
		return variable;
	}
	
	@Override
	public void setVariableInstance(Long processInstanceId, String variableName, Object data)
	{
		if (processInstanceId != -1)
			try {
				logger.log(Level.INFO, "Inside setVariableInstance!!!!, processInstanceId: " + processInstanceId + 
						", variableName: " + variableName);
				ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
				RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(myContext);
		        KieSession ksession = runtimeEngine.getKieSession();
		        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		        ProcessContext kcontext = new ProcessContext(ksession);
		        kcontext.setProcessInstance(processInstance);
		        kcontext.setVariable(variableName, data); 
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "setVariableInstance: Exception throws: " + e.toString());
				throw new QWorkflowRuntimeException(e.toString(), e);
			}
	}

	@Override
	public void stopWorkflowInstance(Long processInstanceId) {
		ProcessInstance processInstance= null;
		try {
			logger.log(Level.INFO, "Inside stopWorkflowInstance!!!!, processInstanceId:" + processInstanceId);

			//change classloader due to mvel issue with classloaders in OSGi
			Thread thread = Thread.currentThread();
			ClassLoader loader = thread.getContextClassLoader();
			thread.setContextClassLoader(this.getClass().getClassLoader());

			ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
			RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(myContext);
	        KieSession ksession = runtimeEngine.getKieSession();
	        processInstance = ksession.getProcessInstance(processInstanceId);
			ksession.abortProcessInstance(processInstanceId);

			// revert the classloader
			thread.setContextClassLoader(loader);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "stopWorkflowInstance: Exception throws: " + e.toString());
			audit("LOG_EVENT_STOP_WORKFLOW_INSTANCE", processInstance.getProcessId(), processInstanceId.toString(), e);
			throw new QWorkflowRuntimeException(e.toString(), e);
		}
	}

	@Override
	public void deleteWorkflowInstance(Long processInstanceId) {
		ProcessInstanceLog processInstance = null;
		try {
			logger.log(Level.INFO, "Inside deleteWorkflowInstance!!!!, processInstanceId:" + processInstanceId);

	        processInstance = (ProcessInstanceLog)em.createQuery("FROM ProcessInstanceLog WHERE processInstanceId = :processInstanceId").setParameter("processInstanceId", processInstanceId).getSingleResult();
	        if (processInstance.getStatus() != ProcessInstance.STATE_COMPLETED
	        		&& processInstance.getStatus() != ProcessInstance.STATE_ABORTED)
	        	throw new QWorkflowRuntimeException("Forbidden!! The process instance you wish to delete is not Completed or Aborted.");

	        em.remove(processInstance);

	        List<NodeInstanceLog> nodeInstances = em.createQuery("FROM NodeInstanceLog WHERE processInstanceId = :processInstanceId").setParameter("processInstanceId", processInstanceId).getResultList();
	        for (NodeInstanceLog nodeInstance: nodeInstances) {
	            em.remove(nodeInstance);
	        }

	        List<VariableInstanceLog> variableInstances = em.createQuery("FROM VariableInstanceLog WHERE processInstanceId = :processInstanceId").setParameter("processInstanceId", processInstanceId).getResultList();
	        for (VariableInstanceLog variableInstance: variableInstances) {
	            em.remove(variableInstance);
	        }
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "deleteWorkflowInstance: Exception throws: " + e.toString());
			audit("LOG_EVENT_DELETE_WORKFLOW_INSTANCE", processInstance.getProcessId(), processInstanceId.toString(), e);
			throw new QWorkflowRuntimeException(e.toString(), e);
		}
	}

	@Override
	public void suspendWorkflowInstance(Long processInstanceId) {
		logger.log(Level.SEVERE, "suspendWorkflowInstance: Not supported by jBPM6...");
	}

	@Override
	public void resumeWorkflowInstance(Long processInstanceId) {
		logger.log(Level.SEVERE, "resumeWorkflowInstance: Not supported by jBPM6...");
	}

	@Override
	public void signalProcessInstance(Long processInstanceId, String signalName, Object event)
	{
		ProcessInstance processInstance = null;
		if (processInstanceId != -1)
			try {
				logger.log(Level.INFO, "Inside signalProcessInstance!!!!, processInstanceId:" + processInstanceId);

				ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
				logger.log(Level.INFO, "MyContext: " + myContext.toString()  + " id, " + myContext.getContextId());

				RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(myContext);
		        logger.log(Level.INFO, runtimeEngine.toString());

		        KieSession ksession = runtimeEngine.getKieSession();
		        processInstance = ksession.getProcessInstance(processInstanceId);
				logger.log(Level.INFO, ksession.toString() + " id, " + ksession.getId());

				ksession.signalEvent(signalName, event, processInstanceId);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "signalProcessInstance: Exception throws: " + e.toString());
				audit("LOG_EVENT_SIGNAL_WORKFLOW_INSTANCE", processInstance.getProcessId(), processInstanceId.toString(), e);
				throw new QWorkflowRuntimeException(e.toString(), e);
			}
	 }

	@Override
    public TaskSummary getTaskDetails(Long processInstanceId, Long taskId)
    {
    	ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
        Task task = runtimeManager.getRuntimeEngine(myContext).getTaskService().getTaskById(taskId);

        if (task != null) {
            List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
            List<String> potOwnersList = null;
            if (potentialOwners != null) {
            	potOwnersList = new ArrayList<String>(potentialOwners.size());
                for (OrganizationalEntity e : potentialOwners) {
                	potOwnersList.add(e.getId());
                }
            }
            return new TaskSummary(task.getId(), task.getNames().get(0).getText(),
                    task.getDescriptions().get(0).getText(), task.getTaskData().getStatus().name(), task.getPriority(), (task.getTaskData().getActualOwner() != null) ? task.getTaskData().getActualOwner()
                    .getId() : "", (task.getTaskData().getCreatedBy() != null) ? task.getTaskData().getCreatedBy().getId()
                    : "", task.getTaskData().getCreatedOn(), task.getTaskData().getActivationTime(), task.getTaskData()
                    .getExpirationTime(), task.getTaskData().getProcessId(), task.getTaskData().getProcessSessionId(),
                    task.getTaskData().getProcessInstanceId(), task.getTaskData().getDeploymentId()
                    , task.getTaskData().getParentId(), potOwnersList);
        }
        return null;
    }

	@Override
    public List<TaskSummary> getTasksAssignedAsPotentialOwner(Long processInstanceId, String userId, List<String> statusList) {
    	ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
        TaskService taskService = runtimeManager.getRuntimeEngine(myContext).getTaskService();
        List<Status> queryStatusList = new ArrayList<>();
		if ( statusList!= null )
		{
			for (String status : statusList)
				queryStatusList.add(Status.valueOf(status));
		}
        List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>();
		List<org.kie.api.task.model.TaskSummary> myTasks = taskService.getTasksAssignedAsPotentialOwnerByStatus(userId, queryStatusList, "en-UK");
        for (org.kie.api.task.model.TaskSummary myTask : myTasks)
        {
        	if (myTask.getProcessInstanceId().equals(processInstanceId))
        	{
	        	Task task = taskService.getTaskById(myTask.getId());
	        	 List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
	             List<String> potOwnersList = null;
	             if (potentialOwners != null) {
	             	potOwnersList = new ArrayList<String>(potentialOwners.size());
	                 for (OrganizationalEntity e : potentialOwners) {
	                 	potOwnersList.add(e.getId());
	                 }
	             }
	             taskSummaries.add(ConverterUtil.adapt(myTask, potOwnersList));
        	}
        }
        return taskSummaries;
    }

	@Override
    public List<TaskSummary> getAllTasksAssignedAsPotentialOwner(String userId, List<String> statusList) {
		List<Status> queryStatusList = new ArrayList<>();
		if ( statusList!= null )
		{
			for (String status : statusList)
				queryStatusList.add(Status.valueOf(status));
		}
		TaskService taskService = runtimeManager.getRuntimeEngine(ProcessInstanceIdContext.get()).getTaskService();
		List<TaskSummary> taskSummaries = new ArrayList<TaskSummary>();
		List<org.kie.api.task.model.TaskSummary> myTasks = taskService.getTasksAssignedAsPotentialOwnerByStatus(userId, queryStatusList, "en-UK");
        for (org.kie.api.task.model.TaskSummary myTask : myTasks)
        {
        	Task task = taskService.getTaskById(myTask.getId());
        	 List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
             List<String> potOwnersList = null;
             if (potentialOwners != null) {
             	potOwnersList = new ArrayList<String>(potentialOwners.size());
                 for (OrganizationalEntity e : potentialOwners) {
                 	potOwnersList.add(e.getId());
                 }
             }
             taskSummaries.add(ConverterUtil.adapt(myTask, potOwnersList));
        }
        return taskSummaries;
    }
	
	@Override
	public List<Long> getTasksByProcessInstanceId(Long processInstanceId) {
	    ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
	    return runtimeManager.getRuntimeEngine(myContext).getTaskService().getTasksByProcessInstanceId(processInstanceId);
	}

	@Override
    public void acceptTask(Long processInstanceId, Long taskId, String userId)
    {
		if (processInstanceId != -1)
			try {
				logger.log(Level.INFO, "Inside acceptTask!!!!, processInstanceId:" + processInstanceId);
				//change classloader due to mvel issue with classloaders in OSGi
				//maybe it is needed in startTask and completeTask but 
				//for the time being we didn't see the exception regarding MVEL operations there
				Thread thread = Thread.currentThread();
				ClassLoader loader = thread.getContextClassLoader();
				thread.setContextClassLoader(this.getClass().getClassLoader());
				
		     	ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
		     	TaskService taskService = runtimeManager
		     			.getRuntimeEngine(myContext)
		     			.getTaskService();
		     	taskService.claim(taskId, userId);
		     	
		     	// revert the classloader
				thread.setContextClassLoader(loader);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "acceptTask: Exception throws: " + e.toString());
				audit("LOG_EVENT_ACCEPT_TASK", taskId.toString(), processInstanceId.toString(), e);
				throw new QWorkflowRuntimeException(e.toString(), e);
			}
    }

	@Override
    public void startTask(Long processInstanceId, Long taskId, String userId)
    {
		if (processInstanceId != -1)
			try {
				logger.log(Level.INFO, "Inside startTask!!!!, processInstanceId:" + processInstanceId);
		     	ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
		     	TaskService taskService = runtimeManager
		     			.getRuntimeEngine(myContext)
		     			.getTaskService();
		     	taskService.start(taskId, userId);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "startask: Exception throws: " + e.toString());
				audit("LOG_EVENT_START_TASK", taskId.toString(), processInstanceId.toString(), e);
				throw new QWorkflowRuntimeException(e.toString(), e);
			}
    }

	@Override
    public void completeTask(Long processInstanceId, Long taskId, String userId, Map<String, Object> data)
    {
		if (processInstanceId != -1)
			try {
				logger.log(Level.INFO, "Inside completeTask!!!!, processInstanceId:" + processInstanceId);
		     	ProcessInstanceIdContext myContext = ProcessInstanceIdContext.get(processInstanceId);
		     	TaskService taskService = runtimeManager
		     			.getRuntimeEngine(myContext)
		     			.getTaskService();
		     	taskService.complete(taskId, userId, data);
			}
			catch (Exception e)
			{
				logger.log(Level.SEVERE, "completeTask: Exception throws: " + e.toString());
				audit("LOG_EVENT_COMPLETE_TASK", taskId.toString(), processInstanceId.toString(), e);
				throw new QWorkflowRuntimeException(e.toString(), e);
			}
    }

	private void showLoc(Class cls)
	{
		try {
			logger.log(Level.INFO, "CLASS = " + cls.getName());
			ProtectionDomain domain = cls.getProtectionDomain();
			ClassLoader loader= cls.getClassLoader();
	        logger.log(Level.INFO, "\tloader = " + loader);
	        URL source = domain.getCodeSource().getLocation();
	        logger.log(Level.INFO, "\tsource = " + source);
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
		 }
    }

	protected KieBase createKnowledgeBase(Map<String, ResourceType> resources) {
	      RuntimeEnvironmentBuilder builder = null;
	      for (Map.Entry<String, ResourceType> entry : resources.entrySet())
	          builder.addAsset(ResourceFactory.newClassPathResource(entry.getKey()), entry.getValue());
	      environment = builder.get();
	      return environment.getKieBase();
	}

	private void checkRestartWorkflowInstancesInDB() throws Exception
	{
		try {
			List<ProcessInstanceDesc> processInstances = null;
	        Map<String, Object> params = new HashMap<String, Object>();
	        List<Integer> states = new ArrayList();
	        states.add(ProcessInstance.STATE_ACTIVE);
	        params.put("states", states);

	        processInstances = (List<ProcessInstanceDesc>) queryStringWithParameters(params, "getProcessInstancesByStatus");

	        for (ProcessInstanceDesc instance : processInstances)
	        	restoreActiveWorkflowInstance(instance);
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "checkRestartWorkflowInstancesInDB: Exception throws: " + e.toString());
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T queryStringWithParameters(Map<String, Object> params, String queryName) {
		Query query = em.createNamedQuery(queryName);
		if (params != null && !params.isEmpty()) {
			for (String name : params.keySet())
				query.setParameter(name, params.get(name));
		}

		return (T) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	private <T> T queryStringWithParametersSingleResult(Map<String, Object> params, String queryName) {
		Query query = em.createNamedQuery(queryName);
		if (params != null && !params.isEmpty()) {
			for (String name : params.keySet())
				query.setParameter(name, params.get(name));
		}

		return (T) query.getSingleResult();
	}

	private void restoreActiveWorkflowInstance(ProcessInstanceDesc processInstance) throws Exception {
		try {
			logger.log(Level.INFO, "restoreActiveWorkflowInstance: , processInstanceId:" + processInstance.getProcessInstanceId());		
			addContentKnowledgeBase(processInstance.getProcessId(), ProcessContent.find(em, processInstance.getProcessId()).getContent());
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "restoreActiveWorkflowInstance: Exception throws: " + e.toString());
			audit("LOG_EVENT_RESTORE_WORKFLOW_INSTANCE", processInstance.getProcessId(), processInstance.getProcessInstanceId().toString(), e);
			throw e;
		}
	}

	private void registerCustomWorkItemHandlers(KieSession session)
	{
		//register WorkItemHandlers for KieSession
		EmailWorkItemHandler emailHandler = new EmailWorkItemHandler(emailHost, emailPort, emailUsername, emailPassword);
		session.getWorkItemManager().registerWorkItemHandler("Email", emailHandler);
		logger.log(Level.INFO, "registerCustomWorkItemHandlers: Register EmailWorkItemHandler, host: " + emailHost +
				", port: " + emailPort + ", username" + emailUsername);
	}

	private synchronized void checkProcessContentExistence(String processId, String content) throws Exception
	{
		try {

			ProcessContent myContent = ProcessContent.find(em, processId);
			String checksum = DigestUtils.md5Hex(content);

			if (myContent == null)
			{
				logger.log(Level.INFO, "Saving content of processId: " + processId);
				myContent = new ProcessContent();
				myContent.setId(processId);
				myContent.setContent(content);
				myContent.setChecksum(checksum);
				em.persist(myContent);
			}
			else
			{
				//check MD5
				if (myContent.getChecksum().equals(checksum))
					logger.log(Level.INFO, "Ignoring content to be saved, because the content is unchanged since the last time it was processed");
				else
				{
					logger.log(Level.INFO, "The content of processId: " + processId + " is different");
					//RuntimeEngine runtime = runtimeManager.getRuntimeEngine(EmptyContext.get());
					//AuditService logService = runtime.getAuditLogService();
					//if (logService.findActiveProcessInstances(processId).size() > 0)
					List<ProcessInstanceDesc> processInstances = null;
			        Map<String, Object> params = new HashMap<String, Object>();
			        List<Integer> states = new ArrayList();
			        states.add(ProcessInstance.STATE_ACTIVE);
			        params.put("states", states);
			        params.put("processId", processId);

			        if (((List<ProcessInstanceDesc>)queryStringWithParameters(params, "getProcessInstancesByProcessIdAndStatus")).size() > 0)
						throw new Exception("Cannot update content of processId: " + processId + " because there are active process instances.");
					myContent.setContent(content);
					myContent.setChecksum(checksum);
					em.merge(myContent);
				}
			}

		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "checkProcessContentExistence: Exception throws: " + e.toString());
			audit("LOG_EVENT_CHECK_PROCESS_CONTENT", processId, null, e);
			throw e;
		}
	}

	private void addContentKnowledgeBase(String processId, String content) throws Exception {
		try{
			InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));
			KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kbuilder.add(ResourceFactory.newInputStreamResource(stream), ResourceType.BPMN2);

			//It is best practice to always check the hasErrors() method after an addition,
			//you should not add more resources or get the KnowledgePackages if there are errors.
			//getKnowledgePackages() will return an empty list if there are errors.
			if (kbuilder.hasErrors())
			{
				logger.log(Level.INFO, "Printing kBuilder-errors:" + kbuilder.getErrors().toString());
				throw new Exception(kbuilder.getErrors().toString());
			}
			else
				((InternalKnowledgeBase)kbase).addPackages(((KnowledgeBuilderImpl)kbuilder).getPackages());
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "addContentKnowledgeBase: Exception throws: " + e.toString());
			throw e;
		}
	}

	private void audit(String event, String description, String referenceId, Object traceData) {

		final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    ((Throwable)traceData).printStackTrace(printWriter);

		String traceDataStr = "";
		if (traceData != null) {
			try {
				traceDataStr = objectMapper.writeValueAsString(result.toString());
			} catch (Exception e) {
				traceDataStr = e.getLocalizedMessage();
			}
		}

		final AuditLogDTO dto = new AuditLogDTO();
		dto.setLevel(LOG_LEVEL);
		dto.setEvent(event);
		dto.setGroupName(LOG_GROUP);
		dto.setPrinSessionId("SYSTEM");
		dto.setReferenceId(referenceId);
		dto.setShortDescription(description);
		dto.setTraceData(traceDataStr);
		eventPublisherService.publishAsync(new HashMap<String, Object>() {
			{
				put(Constants.EVENT_ADMIN_DTO_PROPERTY, dto);
			}
		}, Constants.EVENT_ADMIN_TOPIC);
	}
}
