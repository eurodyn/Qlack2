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
package com.eurodyn.qlack2.fuse.scheduler.api.jobs;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The scheduler wrapped job.
 */
public class SchedulerWrappedJob implements Serializable {

	private static final long serialVersionUID = -2800045340773160426L;

	/** The job qualifier */
	private String jobQualifier;

	/** The job name */
	private String jobName;

	/** The job group */
	private String jobGroup;

	/** The job data */
	private Map<String, Object> dataMap;

	/** Whether the job is durable (default is false) */
	private boolean durable = false;

	/**
	 * Get the job qualifier.
	 *
	 * @return the jobQualifier
	 */
	public String getJobQualifier() {
		return jobQualifier;
	}

	/**
	 * Set the job qualifier.
	 *
	 * @param jobQualifier the jobQualifier to set
	 */
	public void setJobQualifier(String jobQualifier) {
		this.jobQualifier = jobQualifier;
	}

	/**
	 * Get the job name.
	 *
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * Set the job name.
	 *
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * Get the job group.
	 *
	 * @return the jobGroup
	 */
	public String getJobGroup() {
		return jobGroup;
	}

	/**
	 * Set the job group.
	 *
	 * @param jobGroup the jobGroup to set
	 */
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	/**
	 * Get the job data.
	 *
	 * @return the dataMap
	 */
	public Map<String, Object> getDataMap() {
		return dataMap != null ? new HashMap<String, Object>(dataMap) : null;
	}

	/**
	 * Set the job data.
	 *
	 * @param dataMap the dataMap to set
	 */
	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = new HashMap<String, Object>(dataMap);
	}

	/**
	 * Get whether the job is durable.
	 *
	 * @return the durable
	 */
	public boolean isDurable() {
		return durable;
	}

	/**
	 * Set whether the job is durable.
	 *
	 * @param durable the durable to set
	 */
	public void setDurable(boolean durable) {
		this.durable = durable;
	}

}
