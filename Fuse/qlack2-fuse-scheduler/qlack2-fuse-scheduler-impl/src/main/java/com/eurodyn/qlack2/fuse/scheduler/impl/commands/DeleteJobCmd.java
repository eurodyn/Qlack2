package com.eurodyn.qlack2.fuse.scheduler.impl.commands;

import com.eurodyn.qlack2.fuse.scheduler.api.SchedulerService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "qlack", name = "scheduler-job-delete", description = "Deletes a job by name.")
@Service
public final class DeleteJobCmd implements Action {

  @Argument(index = 0, name = "jobName", description = "The name of the job to delete", required = true, multiValued = false)
  private String jobName;
  @Argument(index = 1, name = "jobGroup", description = "The name of the job group of the job to delete", required = true, multiValued = false)
  private String jobGroup;

  @Reference
  private SchedulerService schedulerService;

  @Override
  public Object execute() {
    schedulerService.deleteJob(jobName, jobGroup);
    return null;
  }

}
