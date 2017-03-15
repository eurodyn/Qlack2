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
package com.eurodyn.qlack2.fuse.scheduler.api.builders;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.eurodyn.qlack2.fuse.scheduler.api.exception.QSchedulerException;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerJob;
import com.eurodyn.qlack2.fuse.scheduler.api.jobs.SchedulerWrappedJob;

/**
 * Builder for {@link SchedulerJob}.
 */
public class SchedulerJobBuilder {

	/** The job qualifier */
	private String jobQualifier;

	/** The job name */
	private String jobName;

	/** The job group */
	private String jobGroup;

	/** The job data */
	private Map<String, Object> dataMap;

	/** Whether the job is durable */
	private boolean durable = false;

	/**
	 * Hide default constructor
	 */
	private SchedulerJobBuilder() {
	}

	/**
	 * Create new job.
	 *
	 * @return
	 */
	public static SchedulerJobBuilder newJob() {
		return new SchedulerJobBuilder();
	}

	/**
	 * Build the new job.
	 *
	 * @return
	 * @throws QSchedulerException
	 */
	public SchedulerWrappedJob build() throws QSchedulerException {
		checkJobData();

		SchedulerWrappedJob swj = new SchedulerWrappedJob();
		swj.setJobQualifier(jobQualifier);

		if (StringUtils.isEmpty(jobName)) {
			swj.setJobName("Job_" + UUID.randomUUID().toString());
		}
		else {
			swj.setJobName(jobName);
		}
		if (StringUtils.isEmpty(jobGroup)) {
			swj.setJobGroup("JobGroup_" + UUID.randomUUID().toString());
		}
		else {
			swj.setJobGroup(jobGroup);
		}
		if (dataMap != null) {
			swj.setDataMap(dataMap);
		}
		swj.setDurable(durable);

		return swj;
	}

	/**
	 * Set the job qualifier.
	 *
	 * @param jobQualifier - the job qualifier parameter passed
	 * @return
	 */
	public SchedulerJobBuilder withJobQualifier(String jobQualifier) {
		this.jobQualifier = jobQualifier;
		return this;
	}

	/**
	 * Call to set the durable parameter of the new job to true.
	 *
	 * @return
	 */
	public SchedulerJobBuilder durable() {
		this.durable = true;
		return this;
	}

	/**
	 * Set the job data.
	 *
	 * @param dataMap - a Map with the data for the job
	 * @return
	 */
	public SchedulerJobBuilder withData(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
		return this;
	}

	/**
	 * Set the job identity (job name and job group).
	 *
	 * @param jobName - the job name parameter passed
	 * @param jobGroup - the job group parameter passed
	 * @return
	 */
	public SchedulerJobBuilder withIdentity(String jobName, String jobGroup) {
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		return this;
	}

	/**
	 * Checks whether the job qualifier is null.
	 *
	 * @throws QSchedulerException
	 */
	private void checkJobData() throws QSchedulerException {
		if (jobQualifier == null) {
			throw new IllegalArgumentException("Job qualifier can not be empty.");
		}
	}
}
