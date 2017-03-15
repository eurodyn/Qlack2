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


import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class CustomLogWorkItemHandler implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(CustomLogWorkItemHandler.class.getName());
	
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    	logger.log(Level.INFO, "Executing work item " + workItem);
    	Object inputLog = workItem.getParameter("input");
    	logger.log(Level.INFO, "input " + inputLog);
        manager.completeWorkItem(workItem.getId(), null);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
    	logger.log(Level.INFO, "Aborting work item " + workItem);
        manager.abortWorkItem(workItem.getId());
    }

}
