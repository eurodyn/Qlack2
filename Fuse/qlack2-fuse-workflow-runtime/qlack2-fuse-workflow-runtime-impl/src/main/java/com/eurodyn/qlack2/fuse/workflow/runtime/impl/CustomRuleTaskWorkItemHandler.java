/**
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eurodyn.qlack2.fuse.workflow.runtime.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.eurodyn.qlack2.fuse.rules.api.RulesRuntimeService;
import com.eurodyn.qlack2.fuse.rules.api.StatelessExecutionResults;

public class CustomRuleTaskWorkItemHandler implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(CustomRuleTaskWorkItemHandler.class.getName());
	
	private RulesRuntimeService rulesRuntimeService;
	private BundleContext context;
	
	public CustomRuleTaskWorkItemHandler() {
		this.context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		ServiceReference txRef = context.getServiceReference(RulesRuntimeService.class.getName());
		this.rulesRuntimeService = (RulesRuntimeService) context.getService(txRef);
	}
	
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    	logger.log(Level.INFO, "Executing work item " + workItem.getName());
    	
    	String kbaseId = (String) workItem.getParameter("kbaseId");
    	
    	Map<String, byte[]> globals = (Map<String, byte[]>) workItem.getParameter("globals");
    	
    	List<byte[]> facts = (List<byte[]>) workItem.getParameter("facts");
    	
    	String rule = (String) workItem.getParameter("rule");
    	logger.log(Level.INFO, "rule " + rule);
    	
    	StatelessExecutionResults ruleResults = null;
    	
    	if (rule!= null && !rule.equals(""))
    		ruleResults = rulesRuntimeService.statelessExecute(kbaseId, rule, globals, facts);
    	else
    		ruleResults = rulesRuntimeService.statelessExecute(kbaseId, globals, facts);
    	
    	Map<String, Object> results = new HashMap<String, Object>();
    	results.put("globals", ruleResults.getGlobals());
    	results.put("facts", ruleResults.getFacts());

    	manager.completeWorkItem(workItem.getId(), results);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    	// Do nothing, cannot be aborted
    }

}
