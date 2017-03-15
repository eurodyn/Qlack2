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
package com.eurodyn.qlack2.fuse.scheduler.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.eurodyn.qlack2.fuse.scheduler.api.SchedulerService;
import com.eurodyn.qlack2.fuse.scheduler.api.dto.TriggerDateInfo;
import com.eurodyn.qlack2.fuse.scheduler.api.exception.QSchedulerException;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerWrappedJob;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.triggers.SchedulerWrappedTrigger;
import com.eurodyn.qlack2.fuse.scheduler.api.utils.Constants;
import com.eurodyn.qlack2.fuse.scheduler.impl.utils.QuartzConverters;

public class SchedulerServiceImpl implements SchedulerService {

	private static final Logger logger = Logger.getLogger(SchedulerServiceImpl.class.getName());

	private SchedulerProvider schedulerProvider;

	public void setSchedulerProvider(SchedulerProvider schedulerProvider) {
		this.schedulerProvider = schedulerProvider;
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public String getSchedulerName() throws QSchedulerException {
		try {
			return schedulerProvider.getScheduler().getSchedulerName();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public String getSchedulerInstanceID() throws QSchedulerException {
		try {
			return schedulerProvider.getScheduler().getSchedulerInstanceId();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void start() throws QSchedulerException {
		schedulerProvider.startScheduler();
	}

	/**
	 * {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void shutdown() throws QSchedulerException {
		schedulerProvider.shutdownScheduler();
	}

	/**
	 * {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void standby() throws QSchedulerException {
		try {
			schedulerProvider.getScheduler().standby();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public boolean isStarted() throws QSchedulerException {
		try {
			return schedulerProvider.getScheduler().isStarted();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public boolean isShutdown() throws QSchedulerException {
		try {
			return schedulerProvider.getScheduler().isShutdown();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public boolean isInStandbyMode() throws QSchedulerException {
		try {
			return schedulerProvider.getScheduler().isInStandbyMode();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param job {@inheritDoc}
	 * @param trigger {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void scheduleJob(SchedulerWrappedJob job, SchedulerWrappedTrigger trigger) throws QSchedulerException {
		try {
			logger.log(Level.FINE, "Scheduling job ''{1}.{0}'' with trigger ''{3}.{2}''.",
					new String[]{job.getJobName(), job.getJobGroup(), trigger.getTriggerName(), trigger.getTriggerGroup()});

			schedulerProvider.getScheduler().scheduleJob(
					QuartzConverters.getQuartzJob(job),
					QuartzConverters.getQuartzTrigger(trigger, job.getJobName(), job.getJobGroup()));
		} catch (ParseException | SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param oldTriggerName {@inheritDoc}
	 * @param oldTriggerGroup {@inheritDoc}
	 * @param trigger {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void rescheduleJob(String oldTriggerName, String oldTriggerGroup, SchedulerWrappedTrigger trigger) throws QSchedulerException {
		try {
			schedulerProvider.getScheduler().rescheduleJob(
					TriggerKey.triggerKey(oldTriggerName, oldTriggerGroup),
					QuartzConverters.getQuartzTrigger(trigger, oldTriggerName, oldTriggerGroup));
		} catch (SchedulerException | ParseException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param jobName {@inheritDoc}
	 * @param jobGroup {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public boolean deleteJob(String jobName, String jobGroup) throws QSchedulerException {
		try {
			return schedulerProvider.getScheduler().deleteJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param jobs {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public boolean deleteJobs(Map<String, String> jobs) throws QSchedulerException {
		boolean deletedAll = true;

		try {
			for (Entry<String, String> job : jobs.entrySet()) {
				String name = job.getKey();
				String group = job.getValue();
				boolean deleted = schedulerProvider.getScheduler().deleteJob(JobKey.jobKey(name, group));
				deletedAll = deletedAll && deleted;
			}
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}

		return deletedAll;
	}

	/**
	 * {@inheritDoc}
	 * @param jobName {@inheritDoc}
	 * @param jobGroup {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void triggerJob(String jobName, String jobGroup) throws QSchedulerException {
		triggerJob(jobName, jobGroup, null);
	}

	/**
	 * {@inheritDoc}
	 * @param jobName {@inheritDoc}
	 * @param jobGroup {@inheritDoc}
	 * @param dataMap {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void triggerJob(String jobName, String jobGroup, Map<String, Object> dataMap) throws QSchedulerException {
		try {
			if (dataMap != null) {
				JobDataMap jobDataMap = new JobDataMap();
				jobDataMap.putAll(dataMap);

				SchedulerWrappedJob wrappedJob = getJobDetails(jobName, jobGroup, true);
				String jobQualifier = (String) wrappedJob.getDataMap().get(Constants.QSCH_JOB_QUALIFIER);
				jobDataMap.put(Constants.QSCH_JOB_QUALIFIER, jobQualifier);

				schedulerProvider.getScheduler().triggerJob(JobKey.jobKey(jobName, jobGroup), jobDataMap);
			} else {
				schedulerProvider.getScheduler().triggerJob(JobKey.jobKey(jobName, jobGroup));
			}
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param jobName {@inheritDoc}
	 * @param jobGroup {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void pauseJob(String jobName, String jobGroup) throws QSchedulerException {
		try {
			schedulerProvider.getScheduler().pauseJob(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param triggerName {@inheritDoc}
	 * @param triggerGroup {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void pauseTrigger(String triggerName, String triggerGroup) throws QSchedulerException {
		try {
			schedulerProvider.getScheduler().pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void pauseAll() throws QSchedulerException {
		try {
			schedulerProvider.getScheduler().pauseAll();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void resumeAll() throws QSchedulerException {
		try {
			schedulerProvider.getScheduler().resumeAll();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public void clear() throws QSchedulerException {
		try {
			schedulerProvider.getScheduler().clear();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param jobName {@inheritDoc}
	 * @param jobGroup {@inheritDoc}
	 * @param includeDatamap {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public SchedulerWrappedJob getJobDetails(String jobName, String jobGroup, boolean includeDatamap)
			throws QSchedulerException {
		SchedulerWrappedJob job = null;

		try {
			JobDetail jobDetail = schedulerProvider.getScheduler().getJobDetail(JobKey.jobKey(jobName, jobGroup));
			if (jobDetail != null) {
				job = new SchedulerWrappedJob();
				job.setJobName(jobName);
				job.setJobGroup(jobGroup);

				String jobClassName = jobDetail.getJobDataMap().getString(Constants.QSCH_JOB_QUALIFIER);
				job.setJobQualifier(jobClassName);

				if (includeDatamap) {
					JobDataMap jobDataMap = jobDetail.getJobDataMap();
					job.setDataMap(jobDataMap);
				}
			}
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}

		return job;
	}

	/**
	 * Gets the trigger instance with the given key and the given trigger group.
	 *
	 * @param triggerName the trigger key
	 * @param triggerGroup the group that the trigger belongs to
	 * @throws SchedulerException if an exception occurs while getTrigger
	 */
	private Trigger getTrigger(String triggerName, String triggerGroup) throws SchedulerException {
		return schedulerProvider.getScheduler().getTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
	}

	/**
	 * {@inheritDoc}
	 * @param triggerName {@inheritDoc}
	 * @param triggerGroup {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public TriggerDateInfo getDateInfoForTrigger(String triggerName, String triggerGroup) throws QSchedulerException {
		TriggerDateInfo triggerInfo = new TriggerDateInfo();
		try {
			Trigger trigger = getTrigger(triggerName, triggerGroup);
			triggerInfo.setEndTime(trigger.getEndTime());
			triggerInfo.setFinalFireTime(trigger.getFinalFireTime());
			triggerInfo.setNextFireTime(trigger.getNextFireTime());
			triggerInfo.setPreviousFireTime(trigger.getPreviousFireTime());
			triggerInfo.setStarTime(trigger.getStartTime());
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}

		return triggerInfo;
	}

	/**
	 * {@inheritDoc}
	 * @param triggerName {@inheritDoc}
	 * @param triggerGroup {@inheritDoc}
	 * @return {@inheritDoc}
	 * @throws QSchedulerException {@inheritDoc}
	 */
	@Override
	public Date getNextFireTimeForTrigger(String triggerName, String triggerGroup) throws QSchedulerException {
		try {
			return getTrigger(triggerName, triggerGroup).getNextFireTime();
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}
	
	@Override
	public boolean checkExistJob(String jobName, String jobGroup){
		try {
			return schedulerProvider.getScheduler().checkExists(JobKey.jobKey(jobName, jobGroup));
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}
	
	@Override
	public boolean checkExistTrigger(String triggerName, String triggerGroup){
		try {
			return schedulerProvider.getScheduler().checkExists(TriggerKey.triggerKey(triggerName, triggerGroup));
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
	}
	
	@Override
	public List<String> getCurrentlyExecutingJobsNames(){
		List<String> names = null;
		try {
			List<JobExecutionContext> runningJobs = schedulerProvider.getScheduler().getCurrentlyExecutingJobs();
			
			names = new ArrayList<String>();
			
			for (JobExecutionContext runningJob : runningJobs){
				names.add(runningJob.getJobDetail().getKey().getName().toString());
			}
		} catch (SchedulerException ex) {
			throw new QSchedulerException(ex);
		}
		
		return names;
	}
}
