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
package com.eurodyn.qlack2.fuse.scheduler.api;

import com.eurodyn.qlack2.fuse.scheduler.api.dto.TriggerDateInfo;
import com.eurodyn.qlack2.fuse.scheduler.api.exception.QSchedulerException;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerWrappedJob;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The Scheduler Service interface.
 */
public interface SchedulerService {

	/**
	 * Returns the name of the Scheduler
	 *
	 * @throws QSchedulerException if any exception comes
	 */
	public String getSchedulerName() throws QSchedulerException;

	/**
	 * Returns the instance Id of the Scheduler.
	 *
	 * @throws QSchedulerException if an exception occurs
	 */
	public String getSchedulerInstanceID() throws QSchedulerException;

	/**
	 * Starts the scheduler. Please note that this method does not check if the
	 * scheduler is already started before starting it. It is the caller's
	 * responsibility to make this check.
	 *
	 * @throws QSchedulerException if the scheduler cannot be started
	 */
	public void start() throws QSchedulerException;

	/**
	 * Stops the scheduler. Please note that this method does not check if the
	 * scheduler is already stopped before stopping it. It is the caller's
	 * responsibility to make this check. The scheduler cannot be restarted
	 * after it is stopped.
	 *
	 * @throws QSchedulerException if the scheduler cannot be stopped
	 */
	public void shutdown() throws QSchedulerException;

	/**
	 * Puts the scheduler in standby mode. Please note that this method does not
	 * check if the scheduler is already in standby mode. It is the caller's
	 * responsibility to make this check. The scheduler can be restarted at any
	 * time after being put in standby mode.
	 *
	 * @throws QSchedulerException if the scheduler cannot be stopped
	 */
	public void standby() throws QSchedulerException;

	/**
	 * Reports whether the Scheduler is started.
	 *
	 * @throws QSchedulerException if an exception occurs
	 */
	public boolean isStarted() throws QSchedulerException;

	/**
	 * Reports whether the Scheduler is shutdown.
	 *
	 * @throws QSchedulerException if an exception occurs
	 */
	public boolean isShutdown() throws QSchedulerException;

	/**
	 * Reports whether the Scheduler is in stand-by mode.
	 *
	 * @throws QSchedulerException if an exception occurs
	 */
	public boolean isInStandbyMode() throws QSchedulerException;

	/**
	 * Schedules a job.
	 *
	 * @param job a SchedulerWrappedJob job with the appropriate job information
	 * @param trigger a SchedulerWrappedTrigger trigger with the appropriate trigger information
	 * @throws QSchedulerException if an exception occurs
	 */
	public void scheduleJob(SchedulerWrappedJob job, SchedulerWrappedTrigger trigger) throws QSchedulerException;

	/**
	 * Deletes the Trigger with the given key, and stores the new given one -
	 * which must be associated with the same job
	 *
	 * @param oldTriggerName the key of the trigger to be rescheduled
	 * @param oldTriggerGroup the group that the old trigger belongs to
	 * @param trigger the new trigger
	 * @throws QSchedulerException if an exception occurs
	 */
	public void rescheduleJob(String oldTriggerName, String oldTriggerGroup, SchedulerWrappedTrigger trigger)
			throws QSchedulerException;

	/**
	 * Deletes a job.
	 *
	 * @param jobName the name of the job to be deleted
	 * @param jobGroup the group that the job belongs to
	 * @throws QSchedulerException if an exception occurs
	 */
	public boolean deleteJob(String jobName, String jobGroup) throws QSchedulerException;

	/**
	 * Deletes multiple jobs.
	 *
	 * @param jobs a Map that stores the jobName and the jobGroup of the job
	 * @throws QSchedulerException if an exception occurs
	 */
	public boolean deleteJobs(Map<String, String> jobs) throws QSchedulerException;

	/**
	 * Triggers the identified job.
	 *
	 * @param jobName the job key
	 * @param jobGroup the group that the job belongs to
	 * @throws QSchedulerException if an exception occurs
	 */
	public void triggerJob(String jobName, String jobGroup) throws QSchedulerException;

	/**
	 * Triggers the identified job.
	 *
	 * @param jobName the job key
	 * @param jobGroup the group that the job belongs to
	 * @param dataMap a Map with the job information
	 * @throws QSchedulerException if an exception occurs
	 */
	public void triggerJob(String jobName, String jobGroup, Map<String, Object> dataMap) throws QSchedulerException;

	/**
	 * Pauses a job.
	 *
	 * @param jobName the job key
	 * @param jobGroup the group that the job belongs to
	 * @throws QSchedulerException if an exception occurs
	 */
	public void pauseJob(String jobName, String jobGroup) throws QSchedulerException;

	/**
	 * Pauses a trigger.
	 *
	 * @param triggerName the trigger key
	 * @param triggerGroup the group that the trigger belongs to
	 * @throws QSchedulerException if an exception occurs
	 */
	public void pauseTrigger(String triggerName, String triggerGroup) throws QSchedulerException;

	/**
	 * Pauses all the jobs
	 *
	 * @throws QSchedulerException if an exception occurs
	 */
	public void pauseAll() throws QSchedulerException;

	/**
	 * Resume (un-pause) all the jobs.
	 *
	 * @throws QSchedulerException if an exception occurs
	 */
	public void resumeAll() throws QSchedulerException;

	/**
	 * Clears all the scheduler data.
	 *
	 * @throws QSchedulerException if an exception occurs
	 */
	public void clear() throws QSchedulerException;

	/**
	 * Get the details of the Job instance with the given key.
	 *
	 * @param jobName the name of the job
	 * @param jobGroup the group that the job belongs to
	 * @param includeDatamap whether to include a Datamap in the job details or not
	 * @throws QSchedulerException if an exception occurs
	 */
	public SchedulerWrappedJob getJobDetails(String jobName, String jobGroup,
			boolean includeDatamap) throws QSchedulerException;

	/**
	 * Gets the date information of the execution of a trigger.
	 *
	 * @param triggerName the trigger key
	 * @param triggerGroup the group that the trigger belongs to
	 * @throws QSchedulerException if an exception occurs
	 */
	public TriggerDateInfo getDateInfoForTrigger(String triggerName, String triggerGroup) throws QSchedulerException;

	/**
	 * Gets the next execution date of the trigger.
	 *
	 * @param triggerName the trigger key
	 * @param triggerGroup the group that the trigger belongs to
	 * @throws QSchedulerException if an exception occurs
	 */
	public Date getNextFireTimeForTrigger(String triggerName, String triggerGroup) throws QSchedulerException;
	
	/**
	 * Checks if a job already exists in scheduler
	 * 
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	public boolean checkExistJob(String jobName, String jobGroup);
	
	/**
	 * Checks if a trigger already exists in scheduler
	 * 
	 * @param triggerName
	 * @param triggerGroup
	 * @return
	 */
	public boolean checkExistTrigger(String triggerName, String triggerGroup);
	
	/**
	 * Gets current executing jobs' names 
	 * 
	 * @return
	 */
	public List<String> getCurrentlyExecutingJobsNames();
}
