package com.eurodyn.qlack2.fuse.workflow.runtime.impl.util;

import java.util.List;

import com.eurodyn.qlack2.fuse.workflow.runtime.api.dto.TaskSummary;

public class ConverterUtil {
	   
    public static TaskSummary adapt(org.kie.api.task.model.TaskSummary taskSum, List<String> potentialOwners) {
        return new TaskSummary(
        		taskSum.getId(), 
        		taskSum.getName(),
                taskSum.getDescription(), 
                taskSum.getStatus().name(), 
                taskSum.getPriority(), 
                (taskSum.getActualOwner() != null) ? taskSum.getActualOwner().getId() : "", 
                (taskSum.getCreatedBy() != null) ? taskSum.getCreatedBy().getId(): "",
                taskSum.getCreatedOn(), 
                taskSum.getActivationTime(),
                taskSum.getExpirationTime(), 
                taskSum.getProcessId(), 
                taskSum.getProcessSessionId(),
                taskSum.getProcessInstanceId(), 
                null, 
                0,
                potentialOwners);
    }
}
